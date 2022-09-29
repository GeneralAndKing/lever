package wiki.lever.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wiki.lever.config.security.SecurityConstant;
import wiki.lever.integration.DatasourceMockData;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

/**
 * 2022/9/12 22:52:12
 *
 * @author yue
 */
@DatasourceMockData
class AuthenticationControllerTest extends AbstractControllerTest {

    @Test
    void userLoginSuccess() {
        given(super.spec)
                .body(new LoginParam("admin", "123456"))
                .filter(document("users", requestFields(
                        fieldWithPath("username").description("Login user name."),
                        fieldWithPath("password").description("Login user password.")
                )))
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value());
    }
}

record LoginParam(String username, String password) {
}