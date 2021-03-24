package com.developsoft.comtainer.runtime.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatrixElement {

	private String value;
	private boolean placed;
	private final MatrixRow row;
	private final int colNum;
	
	public MatrixElement(final MatrixRow row, final int colNum) {
		super();
		this.row = row;
		this.colNum = colNum;
		value = "*";
		placed = false;
	}
	
	public void place() {
		this.placed = true;
		this.row.placeElement();
	}
}
