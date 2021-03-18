package com.developsoft.comtainer.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ComtainerResponseDto extends ComtainerRequestDto {

	private Integer status; //0 - Success; 1 - Failure

}
