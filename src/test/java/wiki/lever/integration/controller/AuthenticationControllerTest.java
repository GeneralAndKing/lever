package wiki.lever.integration.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wiki.lever.config.security.SecurityConstant;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.integration.DatasourceMockData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static wiki.lever.integration.util.FieldConstraint.*;

/**
 * 2022/9/12 22:52:12
 *
 * @author yue
 */
@DatasourceMockData
class AuthenticationControllerTest extends AbstractControllerTest {

    @Test
    void userLoginSuccessTest() {
        given(super.spec)
                .body(new LoginParam("admin", "123456"))
                .filter(document("authentication", requestFields(
                        fieldWithPath(LoginParam.USERNAME).type(STRING).description(LoginParam.USERNAME_DESCRIPTION).attributes(REQUIRE),
                        fieldWithPath(LoginParam.PASSWORD).type(STRING).description(LoginParam.PASSWORD_DESCRIPTION).attributes(REQUIRE)
                ), responseFields(
                        fieldWithPath(LoginParam.SUBJECT).type(STRING).description(LoginParam.SUBJECT_DESCRIPTION),
                        fieldWithPath(LoginParam.USERNAME).type(STRING).description(LoginParam.USERNAME_DESCRIPTION),
                        fieldWithPath(LoginParam.ACCESS_TOKEN).type(STRING).description(LoginParam.ACCESS_TOKEN_DESCRIPTION),
                        fieldWithPath(LoginParam.REFRESH_TOKEN).type(STRING).description(LoginParam.REFRESH_TOKEN_DESCRIPTION)
                )))
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value())
                .body(LoginParam.SUBJECT, notNullValue())
                .body(LoginParam.USERNAME, notNullValue())
                .body(LoginParam.ACCESS_TOKEN, notNullValue())
                .body(LoginParam.REFRESH_TOKEN, notNullValue());
    }

    @Test
    void userLoginErrorMethodTest() {
        given(super.spec)
                .body(new LoginParam("admin", "123456"))
                .when().get(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(ERROR_DESCRIPTION, containsString("Authentication method not supported"));
    }

    @Test
    void userLoginErrorRequestBodyTest() {
        given(super.spec)
                .formParam("username", "admin")
                .formParam("password", "123456")
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(ERROR_DESCRIPTION, containsString("Authentication request parse error, please use Json body"));
    }

    @Test
    void userLoginFailTest() {
        given(super.spec)
                .body(new LoginParam("admin", "error"))
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(ERROR_DESCRIPTION, startsWith("Authentication exception: "))
                .body(ERROR, hasSize(1));
    }

    @Test
    void authorizationTokenInfoSuccessTest() {
        LoginParam user = new LoginParam("admin", "123456");
        UserToken admin = given(super.spec)
                .body(user)
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserToken.class);
        given(super.spec)
                .filter(document("authorizationTokenInfo", requestHeaders(
                        headerWithName("Authorization").description("Basic auth credentials.")
                ), responseFields(
                        fieldWithPath(LoginParam.SUBJECT).description(LoginParam.SUBJECT_DESCRIPTION),
                        fieldWithPath(LoginParam.USERNAME).description(LoginParam.USERNAME_DESCRIPTION),
                        fieldWithPath("roles").type(ARRAY).description("User current roles array."),
                        fieldWithPath("permissions").type(OBJECT).description("User roles' permissions. Has `GET`, `POST`, `PUT`, `PATCH`, `DELETE`."),
                        subsectionWithPath("permissions.*").type(ARRAY).description("Current method path array. Include `[GET|POST|PUT|PATCH|DELETE]`")
                )))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getAccessToken())
                .when().get("/authorization/tokenInfo")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(LoginParam.SUBJECT, equalTo(LoginParam.SUBJECT))
                .body(LoginParam.USERNAME, equalTo(user.username()))
                .body("roles", hasSize(1))
                .body("roles[0]", equalTo("PUBLIC"))
                .body("permissions", notNullValue())
                .body("permissions.GET", hasSize(1))
                .body("permissions.GET[0]", equalTo("/authorization/tokenInfo"));
    }
}

record LoginParam(String username, String password) {
    static final String USERNAME = "username";
    static final String USERNAME_DESCRIPTION = "Login user name.";
    static final String PASSWORD = "password";
    static final String PASSWORD_DESCRIPTION = "Login user password.";
    static final String SUBJECT = "subject";
    static final String SUBJECT_DESCRIPTION = "User unique identification.";
    static final String ACCESS_TOKEN = "accessToken";
    static final String ACCESS_TOKEN_DESCRIPTION = "User access token.";
    static final String REFRESH_TOKEN = "refreshToken";
    static final String REFRESH_TOKEN_DESCRIPTION = "Obtain a new valid access token when the user access token expires, and the token should be long-lived. It won't include user information, To be increasing the security of user tokens and only one-time use.";
}
