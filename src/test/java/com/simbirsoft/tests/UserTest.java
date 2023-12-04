package com.simbirsoft.tests;

import com.simbirsoft.api.UserApi;
import com.simbirsoft.data.UserDataProvider;
import com.simbirsoft.dto.UserDto;
import com.simbirsoft.dto.factory.DtoFactory;
import io.qameta.allure.Epic;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static com.simbirsoft.data.LinksDataClass.PET_STORE_USER_URI;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@Epic(PET_STORE_USER_URI)
public class UserTest {

    private final Set<String> testUsersNames = new HashSet<>();

    @AfterClass
    public void cleanTestData() {
        testUsersNames.forEach(new UserApi()::deleteUser);
    }

    @Test(description = "Создание пользователя по API")
    public void createUserTest() {
        UserDto user = DtoFactory.getUser();
        testUsersNames.add(user.getUsername());
        UserApi.doPostUser(user)
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
        UserApi.doGetUser(user.getUsername())
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .assertThat()
            .body("username", equalTo(user.getUsername()))
            .body("email", equalTo(user.getEmail()))
            .body("password", equalTo(user.getPassword()))
            .and().body(matchesJsonSchemaInClasspath("user.json"));
    }

    @Test(description = "Удаление пользователя по API")
    public void deleteUserTest() {
        UserDto user = new UserApi().createUser(DtoFactory.getUser());
        UserApi.doDeleteUser(user.getUsername())
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test(description = "Получение пользователя по невалидному имени", dataProvider = "invalidUsernames",
        dataProviderClass = UserDataProvider.class)
    public void getUserByInvalidUsernameTest(String username) {
        UserApi.doDeleteUser(username)
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
