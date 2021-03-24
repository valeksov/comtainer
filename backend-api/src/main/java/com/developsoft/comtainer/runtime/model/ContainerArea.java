package com.developsoft.comtainer.runtime.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContainerArea implements Comparable<ContainerArea>{

	private boolean free;
	private Integer startX; 
	private Integer startY;
	private Integer startZ;
	private Integer length;	
	private Integer width; 
	private Integer height;
	
	@Override
	public int compareTo(final ContainerArea other) {
		int compareZ = Integer.compare(getStartZ(), other.getStartZ());
		if (compareZ != 0) {
			return compareZ;
		}
		int compareX = Integer.compare(getStartX(), other.getStartX());
		if (compareX != 0) {
			return compareX;
		}
		return Integer.compare(getStartY(), other.getStartY());
	}

}
