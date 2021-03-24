package com.developsoft.comtainer.runtime.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BasePackage {

	private final String id;
	private CargoItemStepRuntime step;
	protected int length;
	protected int width;

	protected int horizontalRotation;

	protected int offsetLength;
	protected int offsetWidth;
	
	protected ComboPackage parent;
	protected boolean combined;
	protected boolean placedInContainer;

	protected int startX;
	protected int startY;
	
	public BasePackage(final String id) {
		super();
		this.id = id;
		this.horizontalRotation = 0;
		this.parent = null;
		this.offsetLength = 0;
		this.offsetWidth = 0;
	}
	
	public BasePackage(final CargoItemStepRuntime step) {
		this(UUID.randomUUID().toString());
		this.step = step;
		this.length = step.getLength();
		this.width = step.getWidth();
	}
	
	public BasePackage clone() {
		final BasePackage clone = new BasePackage(getId());
		clone.setLength(getLength());
		clone.setWidth(getWidth());
		return clone;
	}
	
	public void updateContainerCoordinates(final int startX, final int startY) {
		this.startX = startX;
		this.startY = startY;
	}
	
	public void placeInContainer() {
		this.offsetLength += startX;
		this.offsetWidth += startY;
		this.step.updateCoordinates(getOffsetLength(), getOffsetWidth(), 0);
		this.step.setPlacedInContainer(placedInContainer);
	}
	
	public void setPlacedInContainer(boolean placedInContainer) {
		this.placedInContainer = placedInContainer;
	}
	public void swapOffsets() {
		swapOffsetsInternal();
	}	
	public void swapOffsetsInternal() {
		final int temp = this.offsetLength;
		this.offsetLength = this.offsetWidth;
		this.offsetWidth = temp;
	}
	
	public void rotate (final int horizontalRotation) {
		this.horizontalRotation = horizontalRotation;
		if (horizontalRotation == 1) {
			swapOffsets();
		}

	}
	
	public int getLength() {
		return getHorizontalRotation() == 0 ? this.length : this.width;
	}

	public int getWidth() {
		return getHorizontalRotation() == 0 ? this.width : this.length;
	}

	public int getOffsetLength() {
		final int parentOffsetLength = (this.parent != null) ? parent.getOffsetLength() : 0; 
		return offsetLength + parentOffsetLength;
	}

	public int getOffsetWidth() {
		final int parentOffsetWidth = (this.parent != null) ? parent.getOffsetWidth() : 0;
		return offsetWidth + parentOffsetWidth;
	}

	public int getCount() {
		return 1;
	}
	
	public int getHorizontalRotation() {
		if (this.parent != null) {
			return (this.horizontalRotation == parent.getHorizontalRotation()) ? 0 : 1;
		}
		return this.horizontalRotation;
	}
	
}
