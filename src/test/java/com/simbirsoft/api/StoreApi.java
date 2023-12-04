package com.simbirsoft.api;

import com.simbirsoft.dto.OrderDto;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;

import java.util.logging.Level;

import static com.simbirsoft.data.LinksDataClass.ORDER_ID_PATH;
import static com.simbirsoft.data.LinksDataClass.PET_STORE_STORE_URI;
import static com.simbirsoft.data.LinksDataClass.STORE_SHORT_URI;
import static com.simbirsoft.utils.JSONUtils.fromJson;
import static com.simbirsoft.utils.JSONUtils.toJson;

@Log
public class StoreApi extends BaseApi {

    public static RequestSpecification getRequestSpecification() {
        return BaseApi.getRequestSpecificationJson().baseUri(PET_STORE_STORE_URI);
    }

    @Step("Выполнить POST-запрос к " + STORE_SHORT_URI)
    public static ValidatableResponse doPostStore(final OrderDto order) {
        return getRequestSpecification()
            .body(toJson(order))
            .when()
            .post()
            .then();
    }

    @Step("Выполнить DELETE-запрос к " + STORE_SHORT_URI)
    public static ValidatableResponse doDeleteStore(final String orderId) {
        return getRequestSpecification()
            .pathParam("orderId", orderId)
            .when()
            .delete(ORDER_ID_PATH)
            .then();
    }

    @Step("Создать заказ {order}")
    public OrderDto createOrder(final OrderDto order) {
        return fromJson(doPostStore(order)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and().extract().body().asString(),
            OrderDto.class);
    }

    @Step("Удалить заказ c id = {orderId}")
    public void deleteOrder(final Long orderId) {
        int statusCode = doDeleteStore(orderId.toString())
            .extract().statusCode();
        if (statusCode != HttpStatus.SC_OK) {
            log.log(Level.WARNING, "Order with id = " + orderId + " wasn't deleted");
        }
    }
}
