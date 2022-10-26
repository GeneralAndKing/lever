package wiki.lever.integration.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wiki.lever.config.security.SecurityConstant;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.integration.DataSourceMockData;
import wiki.lever.repository.cache.UserTokenRepository;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static wiki.lever.integration.util.FieldConstraint.*;

/**
 * 2022/9/12 22:52:12
 *
 * @author yue
 */
@DataSourceMockData
class AuthenticationControllerTest extends AbstractControllerTest {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Test
    void userLoginSuccessTest() {
        given(super.spec)
                .body(new LoginParam("admin", "123456"))
                .filter(document("authentication", requestFields(
                        fieldWithPath(LoginParam.USERNAME).type(STRING).description(LoginParam.USERNAME_DESCRIPTION).attributes(REQUIRE),
                        fieldWithPath(LoginParam.PASSWORD).type(STRING).description(LoginParam.PASSWORD_DESCRIPTION).attributes(REQUIRE)
                ), responseFields(
                        fieldWithPath(LoginParam.ID).type(STRING).description(LoginParam.ID_DESCRIPTION),
                        fieldWithPath(LoginParam.SUBJECT).type(STRING).description(LoginParam.SUBJECT_DESCRIPTION),
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
    @DataSourceMockData("userLoginSuccessTest")
    void userLoginSuccessCacheTest() {
        UserToken admin = given(super.spec)
                .body(new LoginParam("admin", "123456"))
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserToken.class);
        Optional<UserToken> adminTokenOptional = userTokenRepository.findById(admin.getSubject());
        assertTrue(adminTokenOptional.isPresent());
        assertEquals(admin.getSubject(), adminTokenOptional.get().getSubject());
    }

    @Test
    void userLoginSuccessFromCacheTest() {
        LoginParam loginParam = new LoginParam("admin", "123456");
        UserToken admin = given(super.spec)
                .body(loginParam)
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserToken.class);
        Optional<UserToken> adminTokenOptional = userTokenRepository.findById(admin.getSubject());
        Assertions.assertTrue(adminTokenOptional.isPresent());
        assertEquals(admin.getSubject(), adminTokenOptional.get().getSubject());

        UserToken userToken = given(super.spec)
                .body(loginParam)
                .when().post(SecurityConstant.AUTHENTICATION_URL)
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserToken.class);
        assertEquals(admin.getId(), userToken.getId());
        assertEquals(admin.getSubject(), userToken.getSubject());
        assertEquals(admin.getAccessToken(), userToken.getAccessToken());
        assertEquals(admin.getRefreshToken(), userToken.getRefreshToken());
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
                        fieldWithPath("permissions").type(ARRAY).description("User roles' permissions. Has `GET`, `POST`, `PUT`, `PATCH`, `DELETE`."),
                        subsectionWithPath("permissions[].method").type(STRING).description("Current method path array. Include `[GET|POST|PUT|PATCH|DELETE]`"),
                        subsectionWithPath("permissions[].path").type(STRING).description("Current mapping path array.")
                )))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin.getAccessToken())
                .when().get("/authorization/tokenInfo")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(LoginParam.SUBJECT, equalTo(LoginParam.SUBJECT))
                .body(LoginParam.USERNAME, equalTo(user.username()))
                .body("roles", hasSize(1))
                .body("roles[0]", equalTo("PUBLIC"))
                .body("permissions", hasSize(1))
                .body("permissions[0].path", equalTo("/authorization/tokenInfo"))
                .body("permissions[0].method", equalTo("GET"));
    }
}

record LoginParam(String username, String password) {
    static final String USERNAME = "username";
    static final String USERNAME_DESCRIPTION = "Login user name.";
    static final String PASSWORD = "password";
    static final String PASSWORD_DESCRIPTION = "Login user password.";
    static final String ID = "id";
    static final String ID_DESCRIPTION = "User unique identification.";
    static final String SUBJECT = "subject";
    static final String SUBJECT_DESCRIPTION = "User unique identification.";
    static final String ACCESS_TOKEN = "accessToken";
    static final String ACCESS_TOKEN_DESCRIPTION = "User access token.";
    static final String REFRESH_TOKEN = "refreshToken";
    static final String REFRESH_TOKEN_DESCRIPTION = "Obtain a new valid access token when the user access token expires, and the token should be long-lived. It won't include user information, To be increasing the security of user tokens and only one-time use.";
}
