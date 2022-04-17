package com.example.planning.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "Shift Request Dto")
public class ShiftRequestDto {
    @NotEmpty
    @ApiModelProperty(value = "Shift Date.", required = true, example = "2022-12-22")
    private String date;
    @NotEmpty
    @ApiModelProperty(value = "Shift Start Time.", required = true, example = "08:00")
    private String start;
    @NotEmpty
    @ApiModelProperty(value = "Shift End Time.", required = true, example = "16:00")
    private String end;

}
