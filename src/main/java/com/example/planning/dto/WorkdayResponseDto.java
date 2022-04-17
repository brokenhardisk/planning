package com.example.planning.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "Workday Response Dto")
public class WorkdayResponseDto {
    @ApiModelProperty(value = "Workday ID.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer id;
    @NotEmpty
    @ApiModelProperty(value = "Date of the Workday.", example = "2022-12-22")
    private String date;
    @ApiModelProperty(value = "List of shifts")
    private List<ShiftResponseDto> shifts;

}
