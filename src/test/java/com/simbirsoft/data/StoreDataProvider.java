package com.simbirsoft.data;

import org.testng.annotations.DataProvider;

public class StoreDataProvider {

    @DataProvider
    public Object[][] orderInvalidIds() {
        return new Object[][]{{"-1"}, {"0"},  {" "}, {"<>"}, {"id"}};
    }
}
