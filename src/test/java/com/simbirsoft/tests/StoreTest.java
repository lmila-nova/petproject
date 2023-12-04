package com.simbirsoft.tests;

import com.simbirsoft.api.PetApi;
import com.simbirsoft.api.StoreApi;
import com.simbirsoft.data.StoreDataProvider;
import com.simbirsoft.dto.OrderDto;
import com.simbirsoft.dto.PetDto;
import com.simbirsoft.dto.factory.DtoFactory;
import io.qameta.allure.Epic;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static com.simbirsoft.data.LinksDataClass.ORDER_ID_PATH;
import static com.simbirsoft.data.LinksDataClass.PET_CAT_URL;
import static com.simbirsoft.data.LinksDataClass.PET_STORE_STORE_URI;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@Epic(PET_STORE_STORE_URI)
public class StoreTest {

    private final Set<Long> testOrdersIds = new HashSet<>();
    private final Set<Long> testPetsIds = new HashSet<>();

    @AfterClass
    public void cleanTestData() {
        testOrdersIds.forEach(new StoreApi()::deleteOrder);
        testPetsIds.forEach(new PetApi()::deletePet);
    }

    @Test(description = "Создание заказа по API")
    public void createOrderTest() {
        PetDto pet = new PetApi().createPet(DtoFactory.getPet(PET_CAT_URL));
        testPetsIds.add(pet.getId());
        OrderDto order = DtoFactory.getOrder(pet.getId());
        Long orderId = Long.valueOf(StoreApi.doPostStore(order)
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("status", equalTo(order.getStatus().name()))
            .and().body(matchesJsonSchemaInClasspath("order.json"))
            .and().extract().path("id").toString());
        testOrdersIds.add(orderId);
    }

    @Test(description = "Удаление заказа по API")
    public void deleteOrderTest() {
        PetDto pet = new PetApi().createPet(DtoFactory.getPet(PET_CAT_URL));
        testPetsIds.add(pet.getId());
        OrderDto order = new StoreApi().createOrder(DtoFactory.getOrder(pet.getId()));
        StoreApi.doDeleteStore(String.valueOf(order.getId()))
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test(description = "Получение заказа по невалидному id", dataProvider = "orderInvalidIds",
        dataProviderClass = StoreDataProvider.class)
    public void getOrderByInvalidIdTest(String id) {
        StoreApi.getRequestSpecification()
            .pathParam("orderId", id)
            .when()
            .get(ORDER_ID_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "Удаление заказа по невалидному id", dataProvider = "orderInvalidIds",
        dataProviderClass = StoreDataProvider.class)
    public void deleteOrderByInvalidIdTest(String id) {
        StoreApi.doDeleteStore(id)
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
