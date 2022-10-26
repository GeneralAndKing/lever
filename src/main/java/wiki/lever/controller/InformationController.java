package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.web.mappings.HandlerMethodDescription;
import org.springframework.boot.actuate.web.mappings.MappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDescription;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletMappingDetails;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletsMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.RequestMappingConditionsDescription;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import wiki.lever.modal.exception.SystemException;
import wiki.lever.modal.param.information.MappingInformation;
import wiki.lever.modal.param.information.ParameterInformation;
import wiki.lever.modal.param.information.ParameterType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 2022/10/7 16:19:37
 *
 * @author yue
 */
@Slf4j
@RestController
@RequestMapping("/information")
@RequiredArgsConstructor
public class InformationController {

    private final WebApplicationContext webApplicationContext;
    private final Collection<MappingDescriptionProvider> descriptionProviders;

    private static final List<Class<?>> BASE_TYPE_LIST = List.of(
            Byte.class, Short.class, Integer.class, Long.class, String.class, Float.class, Double.class, Boolean.class,
            byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class
    );

    @GetMapping("/mapping")
    public HttpEntity<?> mapping() {
        return ResponseEntity.ok(descriptionProviders.stream()
                .filter(DispatcherServletsMappingDescriptionProvider.class::isInstance)
                .map(DispatcherServletsMappingDescriptionProvider.class::cast)
                .findFirst().orElseThrow(() -> new SystemException("Can not find dispatcher servlet mapping provider."))
                .describeMappings(webApplicationContext).values().stream()
                .flatMap(Collection::stream)
                .map(DispatcherServletMappingDescription::getDetails)
                .map(this::toInformation)
                .filter(Objects::nonNull)
                .toList());
    }

    private MappingInformation toInformation(DispatcherServletMappingDetails details) {
        if (Objects.isNull(details)) {
            return null;
        }
        HandlerMethodDescription handlerMethodDescription = details.getHandlerMethod();
        RequestMappingConditionsDescription mappingConditions = details.getRequestMappingConditions();
        RequestMethod requestMethod = CollectionUtils.firstElement(mappingConditions.getMethods());
        String path = CollectionUtils.firstElement(mappingConditions.getPatterns());
        if (ObjectUtils.anyNull(requestMethod, path)) {
            log.warn("Can not found mapping for [{}]", handlerMethodDescription.getDescriptor());
            return null;
        }
        Class<?> handlerClass = getHandlerClass(handlerMethodDescription);
        if (Objects.isNull(handlerClass)) {
            // Fix idea warning. It can't identify ObjectUtils.anyNull method.
            assert requestMethod != null;
            log.debug("Can not found mapping for [{} {}]", requestMethod.name(), path);
            return null;
        }
        Optional<Method> methodOptional = Arrays.stream(handlerClass.getDeclaredMethods())
                .filter(method -> StringUtils.equals(method.getName(), handlerMethodDescription.getName()))
                .findFirst();
        if (methodOptional.isEmpty()) {
            log.debug("Can not found mapping handler for [{}#{}]",
                    handlerMethodDescription.getClassName(), handlerMethodDescription.getName());
            return null;
        }
        if (handlerClass.getPackageName().startsWith("org.springframework.boot")) {
            return null;
        }
        Method method = methodOptional.get();
        Parameter[] parameters = method.getParameters();
        return getMappingInformation(path, requestMethod, parameters);
    }

    /**
     * Get current type.
     *
     * @param path          request path
     * @param requestMethod request type
     * @param parameters    parameter list
     * @return First is name, second is request type
     */
    private MappingInformation getMappingInformation(String path, RequestMethod requestMethod, Parameter[] parameters) {
        List<ParameterInformation> parameterInformation = Arrays.stream(parameters)
                .map(parameter -> {
                    String name = parameter.getName();
                    Annotation[] annotations = parameter.getAnnotations();
                    if (annotations.length == 0) {
                        return new ParameterType(name, parameter.getType(), MappingInformation.RequestType.QUERY_PARAMETER);
                    }
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof PathVariable pathVariable) {
                            if (StringUtils.isNotEmpty(pathVariable.name())) {
                                name = pathVariable.name();
                            }
                            return new ParameterType(name, parameter.getType(), MappingInformation.RequestType.PATH_VARIABLE);
                        }
                        if (annotation instanceof RequestParam requestParam) {
                            if (StringUtils.isNotEmpty(requestParam.name())) {
                                name = requestMethod.name();
                            }
                            return new ParameterType(name, parameter.getType(), MappingInformation.RequestType.QUERY_PARAMETER);
                        }
                        if (annotation instanceof RequestBody) {
                            return new ParameterType(name, parameter.getType(), MappingInformation.RequestType.REQUEST_BODY);
                        }
                    }
                    return new ParameterType(name, parameter.getType(), MappingInformation.RequestType.OTHER);
                })
                .map(this::getParameterInformation)
                .flatMap(Collection::stream)
                .toList();
        return new MappingInformation(path, requestMethod, null, parameterInformation, Collections.emptyList());
    }

    /**
     * Get handler for mapping.
     *
     * @param handlerMethodDescription description
     * @return current class type
     */
    private Class<?> getHandlerClass(HandlerMethodDescription handlerMethodDescription) {
        try {
            return ClassUtils.forName(handlerMethodDescription.getClassName(), webApplicationContext.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private List<ParameterInformation> getParameterInformation(ParameterType parameterType) {
        Class<?> clazz = parameterType.clazz();

        MappingInformation.RequestType type = parameterType.type();
        boolean baseType = BASE_TYPE_LIST.stream().anyMatch(ignoreType -> Objects.equals(ignoreType, clazz));
        if (baseType) {
            return List.of(new ParameterInformation(
                    parameterType.name(),
                    clazz.getSimpleName(),
                    type,
                    Collections.emptyList()));
        }
        Field[] fields = clazz.getDeclaredFields();

        return Arrays.stream(fields)
                .map(field -> new ParameterInformation(
                        field.getName(),
                        field.getType().getSimpleName(),
                        type,
                        BASE_TYPE_LIST.stream().anyMatch(ignoreType -> Objects.equals(ignoreType, field.getType()))
                                ? Collections.emptyList()
                                : getParameterInformation(new ParameterType(field.getName(), field.getType(), type))))
                .toList();
    }

}
