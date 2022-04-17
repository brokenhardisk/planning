package com.example.planning.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(value = "Generic Error Message", description = "A generic error message in case of failures.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessageDto {

    @ApiModelProperty(value = "The error message.")
    private String message;

    @ApiModelProperty(value = "The field.")
    private String field;

}
