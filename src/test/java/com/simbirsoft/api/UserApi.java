package com.simbirsoft.api;

import com.simbirsoft.dto.UserDto;
import io.qameta.allure.Step;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;

import java.util.logging.Level;

import static com.simbirsoft.data.LinksDataClass.LOGIN_SHORT_URI;
import static com.simbirsoft.data.LinksDataClass.PET_STORE_USER_URI;
import static com.simbirsoft.data.LinksDataClass.USER_SHORT_URI;
import static com.simbirsoft.data.LinksDataClass.USER_USERNAME_PATH;
import static com.simbirsoft.utils.JSONUtils.toJson;

@Log
public class UserApi extends BaseApi {

    public static RequestSpecification getRequestSpecification() {
        return BaseApi.getRequestSpecificationJson().baseUri(PET_STORE_USER_URI);
    }

    @Step("Выполнить POST-запрос к " + USER_SHORT_URI)
    public static ValidatableResponse doPostUser(UserDto user) {
        return getRequestSpecification()
            .body(toJson(user))
            .when()
            .post()
            .then();
    }

    @Step("Выполнить DELETE-запрос к " + USER_SHORT_URI + USER_USERNAME_PATH)
    public static ValidatableResponse doDeleteUser(String username) {
        return getRequestSpecification()
            .pathParam("username", username)
            .when()
            .delete(USER_USERNAME_PATH)
            .then();
    }

    @Step("Выполнить GET-запрос к " + USER_SHORT_URI + USER_USERNAME_PATH)
    public static ValidatableResponse doGetUser(String username) {
        return getRequestSpecification()
            .pathParam("username", username)
            .when()
            .get(USER_USERNAME_PATH)
            .then();
    }

    @Step("Выполнить GET-запрос к " + USER_SHORT_URI + LOGIN_SHORT_URI)
    public static ValidatableResponse doGetLogin(UserDto user) {
        return getRequestSpecification()
            .queryParam("username", user.getUsername())
            .queryParam("password", user.getPassword())
            .when()
            .get(LOGIN_SHORT_URI)
            .then();
    }

    @Step("Создать пользователя {user}")
    public UserDto createUser(final UserDto user) {
        doPostUser(user)
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
        return getUser(user.getUsername());
    }

    @Step("Найти пользователя {username}")
    public UserDto getUser(final String username) {
        return doGetUser(username)
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and().extract().body().as(UserDto.class, ObjectMapperType.GSON);
    }

    @Step("Удалить пользователя с username = {username}")
    public void deleteUser(final String username) {
        int statusCode = doDeleteUser(username)
            .extract().statusCode();
        if (statusCode != HttpStatus.SC_OK) {
            log.log(Level.WARNING, "User = " + username + " wasn't deleted");
        }
    }

    @Step("Авторизовать пользователя {user}")
    public void authUser(final UserDto user) {
        doGetLogin(user)
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
    }
}
