package com.simbirsoft.api;

import com.simbirsoft.dto.PetDto;
import io.qameta.allure.Step;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.apache.http.HttpStatus;

import java.util.logging.Level;

import static com.simbirsoft.data.LinksDataClass.PET_ID_PATH;
import static com.simbirsoft.data.LinksDataClass.PET_SHORT_URI;
import static com.simbirsoft.data.LinksDataClass.PET_STORE_PET_URI;
import static com.simbirsoft.utils.JSONUtils.toJson;

@Log
public class PetApi extends BaseApi {

    public static RequestSpecification getRequestSpecification() {
        return BaseApi.getRequestSpecificationJson().baseUri(PET_STORE_PET_URI);
    }

    @Step("Выполнить POST запрос к " + PET_SHORT_URI)
    public static ValidatableResponse doPostPet(PetDto pet) {
        return getRequestSpecification()
            .body(toJson(pet))
            .when()
            .post()
            .then();
    }

    @Step("Выполнить DELETE запрос к " + PET_SHORT_URI + PET_ID_PATH)
    public static ValidatableResponse doDeletePet(Long petId) {
        return getRequestSpecification()
            .pathParam("petId", petId)
            .when()
            .delete(PET_ID_PATH)
            .then();
    }

    @Step("Выполнить GET запрос к " + PET_SHORT_URI + PET_ID_PATH)
    public static ValidatableResponse doGetPet(String petId) {
        return getRequestSpecification()
            .pathParam("petId", petId)
            .when()
            .get(PET_ID_PATH)
            .then();
    }

    @Step("Создать питомца {pet}")
    public PetDto createPet(PetDto pet) {
        return PetApi.doPostPet(pet)
            .assertThat()
            .statusCode(HttpStatus.SC_OK).and().extract().body().as(PetDto.class, ObjectMapperType.GSON);
    }

    @Step("Удалить питомца с id = {petId}")
    public void deletePet(final Long petId) {
        int statusCode = doDeletePet(petId)
            .extract().statusCode();
        if (statusCode != HttpStatus.SC_OK) {
            log.log(Level.WARNING, "Pet with id = " + petId + " wasn't deleted");
        }
    }
}
