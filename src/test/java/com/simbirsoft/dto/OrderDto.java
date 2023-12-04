package com.simbirsoft.dto;

import com.simbirsoft.dto.enums.OrderStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderDto {

    private Long id;
    private Long petId;
    private Long quantity;
    private LocalDateTime shipDate;
    private OrderStatusEnum status;
    private Boolean complete;
}
