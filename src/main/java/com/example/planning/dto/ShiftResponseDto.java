package com.example.planning.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "Shift Response Dto")
public class ShiftResponseDto {
    @ApiModelProperty(value = "Shift ID.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer id;
    @ApiModelProperty(value = "Workday ID.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer workdayId;
    @NotEmpty
    @ApiModelProperty(value = "Shift Start Time.", required = true, example = "00:08")
    private String start;
    @NotEmpty
    @ApiModelProperty(value = "Shift End Time.", required = true, example = "16:00")
    private String end;
    @ApiModelProperty(value = "Last Updated Timestamp for the shift.", required = false, example = "2022-12-22")
    private LocalDateTime lastUpdated;

}
