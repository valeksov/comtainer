package com.developsoft.comtainer.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigDto {
	private Integer cargoSupport; //in percents
	private Float lightUnstackableWeightLimit;	//derived from F)	 2nd  sub property for unstackable goods- to determine light goods that unstackable. 
	private boolean openNewContainer; //if Cargo does not fit in current containers

}
