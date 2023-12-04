package com.simbirsoft.data;

import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider
    public Object[][] invalidUsernames() {
        return new Object[][]{{"-10"}, {"0"}, {" "}, {"<>"}};
    }
}
