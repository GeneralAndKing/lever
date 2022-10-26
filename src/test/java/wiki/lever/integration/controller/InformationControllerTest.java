package wiki.lever.integration.controller;

import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RequestMethod;
import wiki.lever.modal.param.information.MappingInformation;
import wiki.lever.modal.param.information.ParameterInformation;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static wiki.lever.integration.controller.test.InformationTestController.*;
import static wiki.lever.modal.param.information.MappingInformation.RequestType.PATH_VARIABLE;
import static wiki.lever.modal.param.information.MappingInformation.RequestType.QUERY_PARAMETER;

/**
 * 2022/10/14 21:48:41
 *
 * @author yue
 */
@Slf4j
@ActiveProfiles({"no-security", "information"})
class InformationControllerTest extends AbstractControllerTest {

    private static final FieldDescriptor[] INFORMATION_MAPPING_FIELD_DESCRIPTORS = {fieldWithPath("path").type(STRING).description("Mapping http request path."),
            fieldWithPath("method").type(STRING).description("Http request method."),
            fieldWithPath("requestParameterInformation[]").type(ARRAY).description("Http request parameter information list."),
            fieldWithPath("requestParameterInformation[].name").type(STRING).description("Request parameter field name."),
            fieldWithPath("requestParameterInformation[].type").type(STRING).description("Request parameter field type."),
            fieldWithPath("requestParameterInformation[].requestType").description("Request parameter type."),
            fieldWithPath("requestParameterInformation[].information").description("Request parameter information list."),
            fieldWithPath("responseType").description("Http response entity type. Contains `OBJECT`, `COLLECTION`, `VOID`."),
            fieldWithPath("responseResult").description("Http response entity type name.")};
    private static final String INFORMATION_MAPPING = "/information/mapping";

    @Test
    void getSimpleInformationSuccess() {
        // Check api
        given().spec(spec)
                .queryParam("param1", "First")
                .queryParam("param2", 2)
                .get(SIMPLE_REQUEST_FIELD)
                .then().statusCode(HttpStatus.OK.value())
                .assertThat()
                .body(equalTo("First2"));

        // Test
        MappingInformation mappingInformation = getMappingInformation(given().spec(spec)
                .filter(document("informationMapping",
                        responseFields(
                                fieldWithPath("[]").description("Mapping information array."))
                                .andWithPrefix("[].", INFORMATION_MAPPING_FIELD_DESCRIPTORS)
                )), SIMPLE_REQUEST_FIELD);
        assertNotNull(mappingInformation);
        List<ParameterInformation> parameterInformationList = mappingInformation.requestParameterInformation();
        assertEquals(2, parameterInformationList.size());
        ParameterInformation param1 = buildQueryParameter("param1", String.class);
        ParameterInformation param2 = buildQueryParameter("param2", Integer.class);
        assertThat(parameterInformationList).isEqualTo(List.of(param1, param2));
    }

    @Test
    void getRecordInformationSuccess() {
        MappingInformation mappingInformation = getMappingInformation(given().spec(spec), RECORD_REQUEST_FIELD);
        assertNotNull(mappingInformation);
        List<ParameterInformation> parameterInformationList = mappingInformation.requestParameterInformation();
        assertEquals(15, parameterInformationList.size());
        ParameterInformation byteValue = buildQueryParameter("byteValue", Byte.class);
        ParameterInformation shortValue = buildQueryParameter("shortValue", Short.class);
        ParameterInformation intValue = buildQueryParameter("intValue", Integer.class);
        ParameterInformation longValue = buildQueryParameter("longValue", Long.class);
        ParameterInformation stringValue = buildQueryParameter("stringValue", String.class);
        ParameterInformation floatValue = buildQueryParameter("floatValue", Float.class);
        ParameterInformation doubleValue = buildQueryParameter("doubleValue", Double.class);
        ParameterInformation booleanValue = buildQueryParameter("booleanValue", Boolean.class);
        ParameterInformation primitiveByteValue = buildQueryParameter("primitiveByteValue", byte.class);
        ParameterInformation primitiveShortValue = buildQueryParameter("primitiveShortValue", short.class);
        ParameterInformation primitiveIntValue = buildQueryParameter("primitiveIntValue", int.class);
        ParameterInformation primitiveLongValue = buildQueryParameter("primitiveLongValue", long.class);
        ParameterInformation primitiveFloatValue = buildQueryParameter("primitiveFloatValue", float.class);
        ParameterInformation primitiveDoubleValue = buildQueryParameter("primitiveDoubleValue", double.class);
        ParameterInformation primitiveBooleanValue = buildQueryParameter("primitiveBooleanValue", boolean.class);

        List<ParameterInformation> parameterInformation = List.of(
                byteValue, shortValue, intValue, longValue, stringValue, floatValue, doubleValue, booleanValue,
                primitiveByteValue, primitiveShortValue, primitiveIntValue, primitiveLongValue, primitiveFloatValue,
                primitiveDoubleValue, primitiveBooleanValue);
        assertThat(parameterInformationList).isEqualTo(parameterInformation);

    }

    @Test
    void getPathVariableInformationSuccess() {
        MappingInformation mappingInformation = getMappingInformation(given().spec(spec), PATH_VARIABLE_REQUEST);
        assertNotNull(mappingInformation);
        assertEquals(RequestMethod.GET, mappingInformation.method());
        List<ParameterInformation> parameterInformationList = mappingInformation.requestParameterInformation();
        assertEquals(2, parameterInformationList.size());
        ParameterInformation param = buildPathVariable("param", String.class);
        ParameterInformation number = buildPathVariable("number", Integer.class);
        assertThat(parameterInformationList).isEqualTo(List.of(param, number));
    }

    private MappingInformation getMappingInformation(RequestSpecification spec, String testMapping) {
        return IteratorUtils.find(given().spec(spec)
                        .get(INFORMATION_MAPPING)
                        .then().statusCode(HttpStatus.OK.value())
                        .extract().jsonPath()
                        .getList("", MappingInformation.class)
                        .listIterator(),
                item -> StringUtils.equals(item.path(), testMapping));
    }

    @NotNull
    private static ParameterInformation buildQueryParameter(String name, Class<?> type) {
        return new ParameterInformation(name, type.getSimpleName(), QUERY_PARAMETER, Collections.emptyList());
    }

    @NotNull
    private static ParameterInformation buildPathVariable(String name, Class<?> type) {
        return new ParameterInformation(name, type.getSimpleName(), PATH_VARIABLE, Collections.emptyList());
    }
}

