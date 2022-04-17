package com.example.planning.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "User Request Dto")
public class UserRequestDto {
    @NotEmpty
    @ApiModelProperty(value = "User Name", required = true, example = "John Doe")
    private String name;
    @ApiModelProperty(value = "Shift ID.", example = "1")
    private Integer shiftId;

}
