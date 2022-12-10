package wiki.lever.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationModule;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.web.mappings.HandlerMethodDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDetails;
import org.springframework.boot.actuate.web.mappings.servlet.RequestMappingConditionsDescription;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import wiki.lever.modal.param.information.MappingInformation;
import wiki.lever.modal.param.information.MappingItem;
import wiki.lever.modal.param.information.RequestInformation;
import wiki.lever.service.InformationService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static wiki.lever.modal.param.information.MappingItem.RequestParameterType.*;
import static wiki.lever.modal.param.information.MappingItem.ResponseType.*;

/**
 * 2022/10/29 19:30:49
 *
 * @author yue
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformationServiceImpl implements InformationService {
    private final WebApplicationContext webApplicationContext;

    private static final List<Class<?>> BASE_TYPE_LIST = List.of(
            Byte.class, Short.class, Integer.class, Long.class, String.class, Float.class, Double.class, Boolean.class,
            byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class
    );

    private static final SchemaGeneratorConfig schemaGeneratorConfig;

    static {
        OptionPreset optionPreset = new OptionPreset(
                Option.SCHEMA_VERSION_INDICATOR,
                Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT,
                Option.DEFINITIONS_FOR_MEMBER_SUPERTYPES,
                Option.NULLABLE_FIELDS_BY_DEFAULT,
                Option.DEFINITION_FOR_MAIN_SCHEMA,
                Option.NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS,
                Option.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS,
                Option.SIMPLIFIED_ENUMS,
                Option.SIMPLIFIED_OPTIONALS,
                Option.DEFINITIONS_FOR_ALL_OBJECTS,
                Option.ALLOF_CLEANUP_AT_THE_END
        );
        schemaGeneratorConfig = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, optionPreset)
                .with(new JacksonModule(JacksonOption.FLATTENED_ENUMS_FROM_JSONVALUE))
                .with(new JakartaValidationModule(JakartaValidationOption.NOT_NULLABLE_FIELD_IS_REQUIRED))
                .build();
    }

    @Override
    public MappingInformation mappingInformation(List<DispatcherServletMappingDescription> descriptionList) {
        MappingInformation result = new MappingInformation();
        SchemaBuilder schemaBuilder = new SchemaGenerator(schemaGeneratorConfig).buildMultipleSchemaDefinitions();
        for (DispatcherServletMappingDescription description : descriptionList) {
            MappingItem mapping = getMappingItem(schemaBuilder, description);
            if (Objects.isNull(mapping)) {
                continue;
            }
            result.add(mapping);
        }
        ObjectNode definitions = schemaBuilder.collectDefinitions("modalSchema");
        return result.setModalSchema(definitions);
    }

    private MappingItem getMappingItem(SchemaBuilder schemaBuilder, DispatcherServletMappingDescription description) {
        DispatcherServletMappingDetails details = description.getDetails();
        if (Objects.isNull(details)) {
            return null;
        }
        HandlerMethodDescription handlerMethodDescription = details.getHandlerMethod();
        RequestMappingConditionsDescription mappingConditions = details.getRequestMappingConditions();
        RequestMethod requestMethod = CollectionUtils.firstElement(mappingConditions.getMethods());
        String path = CollectionUtils.firstElement(mappingConditions.getPatterns());
        if (Objects.isNull(requestMethod) || StringUtils.isEmpty(path)) {
            log.warn("Can not found mapping for [{}]", handlerMethodDescription.getDescriptor());
            return null;
        }
        Class<?> handlerClass = getClass(handlerMethodDescription.getClassName());
        if (Objects.isNull(handlerClass)) {
            log.debug("Can not found mapping for [{} {}]", requestMethod.name(), path);
            return null;
        }
        MappingItem mapping = new MappingItem()
                .setPath(path)
                .setMethod(requestMethod);

        Optional<Method> methodOptional = Arrays.stream(handlerClass.getDeclaredMethods())
                .filter(method -> StringUtils.equals(method.getName(), handlerMethodDescription.getName()))
                .findFirst();
        if (methodOptional.isEmpty()) {
            log.debug("Can not found mapping handler for [{}#{}]",
                    handlerMethodDescription.getClassName(), handlerMethodDescription.getName());
            return null;
        }
        Method method = methodOptional.get();
        Type genericReturnType = method.getGenericReturnType();
        Arrays.stream(method.getParameters()).forEach(parameter -> mapping.parameterAdd(requestParameter(schemaBuilder, parameter)));
        return mapping.setResponseReference(responseReference(schemaBuilder, genericReturnType))
                .setResponseType(responseType(genericReturnType));
    }

    private RequestInformation requestParameter(SchemaBuilder schemaBuilder, Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        schemaBuilder.createSchemaReference(parameterType);
        RequestInformation information = new RequestInformation();
        String name = parameter.getName();
        Annotation[] annotations = parameter.getAnnotations();
        boolean baseType = BASE_TYPE_LIST.stream().anyMatch(ignoreType -> Objects.equals(ignoreType, parameterType));
        information.setReference(baseType ? "" : "#/modalSchema/" + parameterType.getSimpleName());
        information.setRequestParameterType(OTHER);
        if (annotations.length == 0) {
            return information
                    .setName(name)
                    .setRequestParameterType(QUERY_PARAMETER);
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof PathVariable pathVariable) {
                if (StringUtils.isNotEmpty(pathVariable.name())) {
                    name = pathVariable.name();
                }
                information.setRequestParameterType(PATH_VARIABLE);
            }
            if (annotation instanceof RequestParam requestParam) {
                if (StringUtils.isNotEmpty(requestParam.name())) {
                    name = requestParam.name();
                }
                information.setRequestParameterType(QUERY_PARAMETER);
            }
            if (annotation instanceof RequestBody) {
                information.setRequestParameterType(REQUEST_BODY);
            }
        }
        return information.setName(name);
    }

    private MappingItem.ResponseType responseType(Type response) {
        if (StringUtils.containsIgnoreCase(response.getTypeName(), List.class.getName())) {
            return ARRAY;
        }
        if (StringUtils.containsIgnoreCase(response.getTypeName(), Void.class.getName())) {
            return VOID;
        }
        return OBJECT;
    }

    private String responseReference(SchemaBuilder schemaBuilder, Type response) {
        if (!(response instanceof ParameterizedType responseType)) {
            return null;
        }
        Type[] actualTypeArguments = responseType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            schemaBuilder.createSchemaReference(response);
            return convertToReference(response);
        }
        schemaBuilder.createSchemaReference(responseType);
        Type returnType = actualTypeArguments[0];
        if (returnType instanceof ParameterizedType parameterizedType) {
            return responseReference(schemaBuilder, parameterizedType);
        }
        return convertToReference(returnType);
    }

    private static String convertToReference(Type type) {
        if (Objects.isNull(type)) {
            return null;
        }
        if (type instanceof Class<?> responseClass) {
            return BASE_TYPE_LIST.stream().filter(ignore -> Objects.equals(ignore, responseClass)).findFirst()
                    .map(Class::getSimpleName)
                    .orElseGet(() -> "#/modalSchema/" + responseClass.getSimpleName());
        }
        return null;
    }

    /**
     * Get handler for mapping.
     *
     * @param className class name
     * @return current class type
     */
    private Class<?> getClass(String className) {
        try {
            return ClassUtils.forName(className, webApplicationContext.getClassLoader());
        } catch (ClassNotFoundException e) {
            log.warn("Can not get class for name {}.", className, e);
            return null;
        }
    }
}
