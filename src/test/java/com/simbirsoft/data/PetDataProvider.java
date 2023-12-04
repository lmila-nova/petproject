package com.simbirsoft.data;

import org.testng.annotations.DataProvider;

public class PetDataProvider {

    @DataProvider
    public Object[][] petInvalidStatuses() {
        return new Object[][]{{"fetched"}, {"1"}, {" "}, {"<>"}};
    }

    @DataProvider
    public Object[][] petInvalidIds() {
        return new Object[][]{{"-10"}, {"0"}, {" "}, {"<>"}, {"id"}};
    }
}
