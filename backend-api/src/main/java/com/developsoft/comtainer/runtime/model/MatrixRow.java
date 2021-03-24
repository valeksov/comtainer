package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatrixRow {

	private final int rowNum;
	private final int numColumns;
	private final List<MatrixElement> elements;
	private int usedElements;
	
	public MatrixRow(final int rowNum, final int colNum) {
		super();
		this.rowNum = rowNum;
		this.numColumns = colNum;
		this.elements = initElements(colNum);
	}
	private List<MatrixElement> initElements(final int colNum) {
		final List<MatrixElement> result = new ArrayList<MatrixElement>();
		for (int i = 0; i< colNum; i++) {
			result.add(new MatrixElement(this, i));
		}
		return result;
	}
	
	public void placeElement() {
		this.usedElements++;
	}
	public int getNumberFreeElements() {
		return numColumns - usedElements; 
	}
}
