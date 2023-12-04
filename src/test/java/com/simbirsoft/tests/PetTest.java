package com.simbirsoft.tests;

import com.simbirsoft.api.BaseApi;
import com.simbirsoft.api.PetApi;
import com.simbirsoft.data.PetDataProvider;
import com.simbirsoft.dto.PetDto;
import com.simbirsoft.dto.factory.DtoFactory;
import io.qameta.allure.Epic;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static com.simbirsoft.data.LinksDataClass.FIND_BY_STATUS_SHORT_URI;
import static com.simbirsoft.data.LinksDataClass.PET_CAT_URL;
import static com.simbirsoft.data.LinksDataClass.PET_ID_PATH;
import static com.simbirsoft.data.LinksDataClass.PET_STORE_PET_URI;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@Epic(PET_STORE_PET_URI)
public class PetTest {

    private final Set<Long> testPetsIds = new HashSet<>();

    @AfterTest
    public void cleanTestData() {
        testPetsIds.forEach(new PetApi()::deletePet);
    }

    @Test(description = "Создание питомца по API")
    public void createPetTest() {
        PetDto pet = DtoFactory.getPet(PET_CAT_URL);
        Long petId = Long.valueOf(PetApi.doPostPet(pet)
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .and()
            .body("name", equalTo(pet.getName()))
            .body("photoUrls", equalTo(pet.getPhotoUrls()))
            .and().body(matchesJsonSchemaInClasspath("pet.json"))
            .and().extract().path("id").toString());
        testPetsIds.add(petId);
    }

    @Test(description = "Удаление питомца по API")
    public void deletePetTest() {
        PetDto pet = new PetApi().createPet(DtoFactory.getPet(PET_CAT_URL));
        PetApi.doDeletePet(pet.getId())
            .assertThat()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test(description = "Получение питомца по некорректному значению статуса", dataProvider = "petInvalidStatuses",
        dataProviderClass = PetDataProvider.class)
    public void getPetByInvalidStatusTest(final String status) {
        PetApi.getRequestSpecification()
            .queryParam("status", status)
            .when()
            .get(FIND_BY_STATUS_SHORT_URI)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "Получение питомца по некорректному id", dataProvider = "petInvalidIds",
        dataProviderClass = PetDataProvider.class)
    public void getPetByInvalidIdTest(final String id) {
        PetApi.doGetPet(id)
            .assertThat()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "Обновление данных о питомце с некорректным статусом", dataProvider = "petInvalidStatuses",
        dataProviderClass = PetDataProvider.class)
    public void updatePetWithInvalidStatusTest(final String status) {
        PetDto pet = new PetApi().createPet(DtoFactory.getPet(PET_CAT_URL));
        testPetsIds.add(pet.getId());
        BaseApi.getRequestSpecification()
            .baseUri(PET_STORE_PET_URI)
            .pathParam("petId", pet.getId())
            .queryParam("status", status)
            .when()
            .post(PET_ID_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
    }
}
