package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContainerAreaRuntime {

	private float maxWeight;
	private int maxLength;
	private int maxWidth;
	private int maxHeight;
	private int startX;
	private int startY;
	private int startZ;
	private boolean fixedLength;
	private boolean fixedWidth;
	private boolean fixedHeight;
	private boolean checkAllArea;
	private int targetDimension;
	private int cargoSupport;
	private final List<LoadPlanStepRuntime> steps;
	
	public ContainerAreaRuntime() {
		super();
		this.steps = new ArrayList<LoadPlanStepRuntime>();
	}
	public ContainerAreaRuntime(final float weight, final int length, final int width, final int height, final int startX, final int startY, final int startZ, final int cargoSupport,
													final boolean fixedLength, final boolean fixedWidth, final boolean fixedHeight, final boolean checkAllArea, final int targetDimension) {
		this();
		this.maxWeight = weight;
		this.maxLength = length;
		this.maxWidth = width;
		this.maxHeight = height;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.fixedLength = fixedLength;
		this.fixedWidth = fixedWidth;
		this.fixedHeight = fixedHeight;
		this.checkAllArea = checkAllArea;
		this.targetDimension = targetDimension;
		this.cargoSupport = cargoSupport;
	}
	
	public void addStep(final LoadPlanStepRuntime step) {
		getSteps().add(step);
	}
	
	public ContainerAreaRuntime clone() {
		return new ContainerAreaRuntime(getMaxWeight(), getMaxLength(), getMaxWidth(), getMaxHeight(), getStartX(), getStartY(), getStartZ(), getCargoSupport(),
												isFixedLength(), isFixedWidth(), isFixedHeight(), isCheckAllArea(), getTargetDimension());
	}
	
	public int getDimensionValue(final int dimension) {
		return (dimension < 2) ? getMaxLength() : (dimension < 3) ? getMaxWidth() : getMaxHeight();
	}

}
