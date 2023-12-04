package com.simbirsoft.tests;

import com.simbirsoft.api.PetApi;
import com.simbirsoft.api.StoreApi;
import com.simbirsoft.api.UserApi;
import com.simbirsoft.dto.OrderDto;
import com.simbirsoft.dto.PetDto;
import com.simbirsoft.dto.UserDto;
import com.simbirsoft.dto.factory.DtoFactory;
import io.qameta.allure.Epic;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.simbirsoft.data.LinksDataClass.ORDER_ID_PATH;
import static com.simbirsoft.data.LinksDataClass.PET_CAT_URL;
import static com.simbirsoft.data.LinksDataClass.USER_USERNAME_PATH;
import static com.simbirsoft.utils.JSONUtils.toJson;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестирование по пользовательским сценариям")
public class UseCasesTest {

    private final HashMap<String, String> testUsersNames = new HashMap<>();
    private final Set<Long> testPetsIds = new HashSet<>();
    private final Set<Long> testOrdersIds = new HashSet<>();

    @BeforeMethod
    public void auth(Method m) {
        UserApi api = new UserApi();
        UserDto user = api.createUser(DtoFactory.getUser());
        testUsersNames.put(m.getName(), user.getUsername());
        api.authUser(user);
    }

    @AfterClass
    public void cleanTestData() {
        testUsersNames.values().forEach(new UserApi()::deleteUser);
        testOrdersIds.forEach(new StoreApi()::deleteOrder);
        testPetsIds.forEach(new PetApi()::deletePet);
    }

    @Test(description = "Сценарий №1: Размещение заказа на питомца авторизованным пользователем")
    public void placeOrderByAuthorizedUserTest() {
        PetDto pet = new PetApi().createPet(DtoFactory.getPet(PET_CAT_URL));
        testPetsIds.add(pet.getId());
        OrderDto order = DtoFactory.getOrder(pet.getId());
        Long orderId = Long.valueOf(
            StoreApi.doPostStore(order)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .and().body(matchesJsonSchemaInClasspath("order.json"))
                .and().extract().path("id").toString());
        testOrdersIds.add(orderId);
        StoreApi.getRequestSpecification()
            .pathParam("orderId", orderId)
            .when()
            .get(ORDER_ID_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("status", equalTo(order.getStatus().name()));
    }

    @Test(description = "Сценарий №2: Добавление питомца авторизованным пользователем")
    public void addPetByAuthorizedUserTest() {
        PetDto pet = DtoFactory.getPet("");
        Long petId = Long.valueOf(
            PetApi.doPostPet(pet)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and().body(matchesJsonSchemaInClasspath("pet.json"))
                .and().extract().path("id").toString());
        testPetsIds.add(petId);
        pet.setPhotoUrls(List.of(PET_CAT_URL));
        PetApi.getRequestSpecification()
            .body(toJson(pet))
            .when()
            .put()
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .assertThat()
            .and().body(matchesJsonSchemaInClasspath("pet.json"));
        PetApi.doGetPet(petId.toString())
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .assertThat()
            .and().body(matchesJsonSchemaInClasspath("pet.json"))
            .body("name", equalTo(pet.getName()))
            .body("photoUrls", equalTo(pet.getPhotoUrls()));
    }

    @Test(description = "Сценарий №3: Смена пароля пользователем")
    public void changePasswordForUserTest(Method m) {
        String username = testUsersNames.get(m.getName());
        UserDto user = UserDto.builder()
            .username(username)
            .password(RandomStringUtils.randomAlphabetic(8))
            .build();
        UserApi.getRequestSpecification()
            .pathParam("username", username)
            .body(toJson(user))
            .when()
            .put(USER_USERNAME_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
        UserApi.doGetUser(username)
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("username", equalTo(user.getUsername()))
            .body("password", equalTo(user.getPassword()))
            .and().body(matchesJsonSchemaInClasspath("user.json"));
        UserApi.doGetLogin(user)
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
    }
}
