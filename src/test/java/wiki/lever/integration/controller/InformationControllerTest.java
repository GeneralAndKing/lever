package wiki.lever.integration.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

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
            fieldWithPath("responseInformation").description("Http response entity type name.")};
    private static final String INFORMATION_MAPPING = "/information/mapping";

//    @Test
//    void getSimpleInformationSuccess() {
//        // Check api
//        given().spec(spec)
//                .queryParam("param1", "First")
//                .queryParam("param2", 2)
//                .get(SIMPLE_REQUEST)
//                .then().statusCode(HttpStatus.OK.value())
//                .assertThat()
//                .body(equalTo("First2"));
//
//        // Test
//        MappingInformation mappingInformation = getMappingInformation(given().spec(spec)
//                .filter(document("informationMapping",
//                        responseFields(
//                                fieldWithPath("[]").description("Mapping information array."))
//                                .andWithPrefix("[].", INFORMATION_MAPPING_FIELD_DESCRIPTORS)
//                )), SIMPLE_REQUEST);
//        assertNotNull(mappingInformation);
//        List<RequestInformation> requestInformationList = mappingInformation.requestInformation();
//        assertEquals(2, requestInformationList.size());
//        RequestInformation param1 = buildQueryParameter("param1", String.class);
//        RequestInformation param2 = buildQueryParameter("param2", Integer.class);
//        assertThat(requestInformationList).isEqualTo(List.of(param1, param2));
//    }
//
//    @Test
//    void getRecordInformationSuccess() {
//        MappingInformation mappingInformation = getMappingInformation(given().spec(spec), RECORD_REQUEST);
//        assertNotNull(mappingInformation);
//        List<RequestInformation> requestInformationList = mappingInformation.requestInformation();
//        assertEquals(15, requestInformationList.size());
//        RequestInformation byteValue = buildQueryParameter("byteValue", Byte.class);
//        RequestInformation shortValue = buildQueryParameter("shortValue", Short.class);
//        RequestInformation intValue = buildQueryParameter("intValue", Integer.class);
//        RequestInformation longValue = buildQueryParameter("longValue", Long.class);
//        RequestInformation stringValue = buildQueryParameter("stringValue", String.class);
//        RequestInformation floatValue = buildQueryParameter("floatValue", Float.class);
//        RequestInformation doubleValue = buildQueryParameter("doubleValue", Double.class);
//        RequestInformation booleanValue = buildQueryParameter("booleanValue", Boolean.class);
//        RequestInformation primitiveByteValue = buildQueryParameter("primitiveByteValue", byte.class);
//        RequestInformation primitiveShortValue = buildQueryParameter("primitiveShortValue", short.class);
//        RequestInformation primitiveIntValue = buildQueryParameter("primitiveIntValue", int.class);
//        RequestInformation primitiveLongValue = buildQueryParameter("primitiveLongValue", long.class);
//        RequestInformation primitiveFloatValue = buildQueryParameter("primitiveFloatValue", float.class);
//        RequestInformation primitiveDoubleValue = buildQueryParameter("primitiveDoubleValue", double.class);
//        RequestInformation primitiveBooleanValue = buildQueryParameter("primitiveBooleanValue", boolean.class);
//
//        List<RequestInformation> requestInformation = List.of(
//                byteValue, shortValue, intValue, longValue, stringValue, floatValue, doubleValue, booleanValue,
//                primitiveByteValue, primitiveShortValue, primitiveIntValue, primitiveLongValue, primitiveFloatValue,
//                primitiveDoubleValue, primitiveBooleanValue);
//        assertThat(requestInformationList).isEqualTo(requestInformation);
//
//    }
//
//    @Test
//    void getPathVariableInformationSuccess() {
//        MappingInformation mappingInformation = getMappingInformation(given().spec(spec), PATH_VARIABLE_REQUEST);
//        assertNotNull(mappingInformation);
//        assertEquals(RequestMethod.GET, mappingInformation.method());
//        List<RequestInformation> requestInformationList = mappingInformation.requestInformation();
//        assertEquals(2, requestInformationList.size());
//        RequestInformation param = buildPathVariableParameter("param", String.class);
//        RequestInformation number = buildPathVariableParameter("number", Integer.class);
//        assertThat(requestInformationList).isEqualTo(List.of(param, number));
//    }
//
//    @Test
//    void getJsonRequestInformationSuccess() {
//        MappingInformation mappingInformation = getMappingInformation(given().spec(spec), JSON_BODY_REQUEST);
//        assertNotNull(mappingInformation);
//        assertEquals(RequestMethod.POST, mappingInformation.method());
//        List<RequestInformation> requestInformationList = mappingInformation.requestInformation();
//        assertEquals(3, requestInformationList.size());
//        RequestInformation stringValue = buildRequestBodyParameter("stringValue", String.class);
//        RequestInformation intValue = buildRequestBodyParameter("intValue", Integer.class);
//        RequestInformation boolValue = buildRequestBodyParameter("boolValue", Boolean.class);
//        assertThat(requestInformationList).isEqualTo(List.of(stringValue, intValue, boolValue));
//    }
//
//    private MappingInformation getMappingInformation(RequestSpecification spec, String testMapping) {
//        return IteratorUtils.find(given().spec(spec)
//                        .get(INFORMATION_MAPPING)
//                        .then().statusCode(HttpStatus.OK.value())
//                        .extract().jsonPath()
//                        .getList("", MappingInformation.class)
//                        .listIterator(),
//                item -> StringUtils.equals(item.path(), testMapping));
//    }
//
//    @NotNull
//    private static RequestInformation buildQueryParameter(String name, Class<?> type) {
//        return new RequestInformation(name, type.getSimpleName(), QUERY_PARAMETER, Collections.emptyList());
//    }
//
//    @NotNull
//    private static RequestInformation buildPathVariableParameter(String name, Class<?> type) {
//        return new RequestInformation(name, type.getSimpleName(), PATH_VARIABLE, Collections.emptyList());
//    }
//
//    @NotNull
//    private static RequestInformation buildRequestBodyParameter(String name, Class<?> type) {
//        return new RequestInformation(name, type.getSimpleName(), REQUEST_BODY, Collections.emptyList());
//    }
}

