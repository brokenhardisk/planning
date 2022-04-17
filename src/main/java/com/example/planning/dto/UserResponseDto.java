package com.example.planning.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "User Response Dto")
public class UserResponseDto {
    @ApiModelProperty(value = "User ID.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer id;
    @ApiModelProperty(value = "Shift ID.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Integer shiftId;
    @NotEmpty
    @ApiModelProperty(value = "User name", example = "John Doe")
    private String name;
    @ApiModelProperty(value = "Last Updated Timestamp for the shift.", example = "2022-12-22")
    private LocalDateTime lastUpdated;

}
