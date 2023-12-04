package com.simbirsoft.dto.factory;

import com.simbirsoft.dto.OrderDto;
import com.simbirsoft.dto.PetDto;
import com.simbirsoft.dto.UserDto;
import com.simbirsoft.dto.enums.OrderStatusEnum;
import com.simbirsoft.dto.enums.PetStatusEnum;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.Collections;

public class DtoFactory {

    public static UserDto getUser() {
        return UserDto.builder()
            .id(Long.valueOf(RandomStringUtils.randomNumeric(10)))
            .username(RandomStringUtils.randomAlphabetic(8))
            .email(RandomStringUtils.randomAlphanumeric(10))
            .password(RandomStringUtils.randomAlphabetic(8))
            .build();
    }

    public static PetDto getPet(final String url) {
        return PetDto.builder()
            .id(Long.valueOf(RandomStringUtils.randomNumeric(10)))
            .name(RandomStringUtils.randomAlphabetic(8))
            .photoUrls(Collections.singletonList(url))
            .status(PetStatusEnum.available)
            .build();
    }

    public static OrderDto getOrder(final Long petId) {
        return OrderDto.builder()
            .petId(petId)
            .status(OrderStatusEnum.approved)
            .shipDate(LocalDateTime.now())
            .build();
    }
}
