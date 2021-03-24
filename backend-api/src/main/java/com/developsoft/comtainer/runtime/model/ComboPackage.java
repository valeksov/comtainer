package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

public class ComboPackage extends BasePackage{
	public static final int BY_LENGTH = 1;
	public static final int BY_WIDTH = 2;
	private final BasePackage first;
	private final BasePackage second;
	private final int comboType;
	
	public ComboPackage (final BasePackage first, final BasePackage second, final int comboType) {
		super(first.getId()+second.getId());
		this.first = first;
		this.second = second;
		this.comboType = comboType;
		init();
	}

	public List<BasePackage> split() {
		final List<BasePackage> result = new ArrayList<BasePackage>();
		this.first.setCombined(false);
		this.second.setCombined(false);
		this.first.setParent(null);
		this.second.setParent(null);
		this.first.setOffsetLength(0);
		this.second.setOffsetLength(0);
		this.first.setOffsetWidth(0);
		this.second.setOffsetWidth(0);
		result.add(this.first);
		result.add(this.second);
		return result;
	}
	
	@Override
	public void placeInContainer() {
		this.offsetLength += startX;
		this.offsetWidth += startY;
		this.first.placeInContainer();
		this.second.placeInContainer();
	}
	
	@Override
	public void updateContainerCoordinates(final int startX, final int startY) {
		this.startX= startX;
		this.startY= startY;
		this.first.updateContainerCoordinates(startX, startY);
		this.second.updateContainerCoordinates(startX, startY);
	}
	
	@Override
	public void setPlacedInContainer(boolean placedInContainer) {
		this.placedInContainer = placedInContainer;
		this.first.setPlacedInContainer(placedInContainer);
		this.second.setPlacedInContainer(placedInContainer);
	}

	@Override
	public int getCount() {
		return this.first.getCount() + this.second.getCount();
	}

	public void confirm() {
		rotate(this.second);
		first.setCombined(true);
		first.setParent(this);
		second.setCombined(true);
		second.setParent(this);
		switch (this.comboType) {
			case BY_LENGTH : {
				second.setOffsetLength(first.getLength()); break;
			}
			default : {
				second.setOffsetWidth(first.getWidth()); break;
			}
		}
	}
	
	private void init () {
		final BasePackage secondLocal = this.second.clone();
		rotate(secondLocal);
		switch (this.comboType) {
			case BY_LENGTH : {
				this.length = first.getLength() + secondLocal.getLength();
				this.width = first.getWidth();
				break;
			}
			default : {
				this.length = first.getLength();
				this.width = first.getWidth() + secondLocal.getWidth();
				break;
			}
		}
	}
	
	private void rotate(final BasePackage s) {
		switch (this.comboType) {
			case BY_LENGTH : {
				if (first.getWidth() != s.getWidth()) {
					s.rotate(1);
				};
				break;
			}
			default : {
				if (first.getLength() != s.getLength()) {
					s.rotate(1);
				}
			}
		}
	}
	
	@Override
	public void swapOffsets() {
		swapOffsetsInternal();
		this.first.swapOffsets();
		this.second.swapOffsets();
	}

}
