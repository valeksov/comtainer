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
		if (startX.equals(other.startX) && startY.equals(other.startY)) {
			return startZ.compareTo(other.startZ);
		} else if (startX.equals(other.startX)) {
			return startY.compareTo(other.startY);
		}
		return startX.compareTo(other.startX);
	}

}
