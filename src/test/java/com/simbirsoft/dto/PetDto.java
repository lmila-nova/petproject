package com.simbirsoft.dto;

import com.simbirsoft.dto.enums.PetStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PetDto {

    private Long id;
    private CategoryDto category;
    private String name;
    private List<String> photoUrls;
    private List<TagDto> tags;
    private PetStatusEnum status;

    @Override
    public String toString() {
        return name;
    }
}
