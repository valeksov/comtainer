package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.developsoft.comtainer.rest.dto.CargoItemDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemRuntime {

	private final String uid;
	private final CargoItemDto source;
	private int remainingQuantity;
	private final int orientation;
	private final CargoGroupRuntime group;
	
	private final List<CargoItemRuntime> linkedRotations;
	private final List<CargoItemStepRuntime> runtimeSteps;
	
	public CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group, final String uid) {
		this(source, group, 1, uid);
	}
	
	private CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group, int orientation, final String uid) {
		super();
		this.source = source;
		this.uid = uid;
		this.remainingQuantity = source.getQuantity();
		this.orientation = orientation;
		this.linkedRotations = new ArrayList<CargoItemRuntime>();
		this.runtimeSteps = new ArrayList<CargoItemStepRuntime>();
		this.group = group;
	}
	
	@SuppressWarnings("unused")
	private static void print(final CargoItemDto source) {
		final StringBuffer strBuf = new StringBuffer();
		if (source.getQuantity() > 1) {
			strBuf.append(source.getQuantity());
			strBuf.append(" * ");
		}
		strBuf.append(source.getLength());
		strBuf.append(" x ");
		strBuf.append(source.getWidth());
		strBuf.append(" x ");
		strBuf.append(source.getHeight());
		strBuf.append(", W ");
		strBuf.append(source.getWeigth());
		System.out.println(strBuf.toString());
	}
	public static List<CargoItemRuntime> createRotations(final CargoItemDto source, final CargoGroupRuntime group) {
//		print(source);
		final List<CargoItemRuntime> result = new ArrayList<CargoItemRuntime>();
		final String uid = UUID.randomUUID().toString();
		final CargoItemRuntime orientation1 = new CargoItemRuntime(source, group, 1, uid);
		result.add(orientation1);
		if (source.isRotatable()) {
			final CargoItemRuntime orientation3 = (source.getHeight() != source.getWidth()) ? new CargoItemRuntime(source, group, 3, uid) : null;
			final CargoItemRuntime orientation5 = (source.getHeight() != source.getLength()) ? new CargoItemRuntime(source, group, 5, uid) : null;
			if (orientation3 != null) {
				result.add(orientation3);
				orientation1.linkedRotations.add(orientation3);
				orientation3.linkedRotations.add(orientation1);
				if (orientation5 != null) {
					orientation3.linkedRotations.add(orientation5);
				}
			}
			if (orientation5 != null) {
				result.add(orientation5);
				orientation1.linkedRotations.add(orientation5);
				orientation5.linkedRotations.add(orientation1);
				if (orientation3 != null) {
					orientation5.linkedRotations.add(orientation3);
				}
			}
		}
		return result;
	}
	
	public boolean isPlaced() {
		return remainingQuantity == 0;
	}
	
	private void reduceQuantity(final int quantity) {
		this.remainingQuantity -= quantity;
	}
	
	public boolean markUsed (final int quantity) {
		if (remainingQuantity >= quantity) {
			reduceQuantity(quantity);
			this.linkedRotations.forEach(next -> next.reduceQuantity(quantity));
			return true;
		}
		return false;
	}
	
	public int getHeight() {
		final int result;
		switch (this.orientation) {
			case 3:		result = this.source.getWidth(); break;
			case 5:		result = this.source.getLength(); break;
			default:	result = this.source.getHeight();	
		}
		return result;
	}
	
	public int getLength() {
		final int result;
		switch (this.orientation) {
			case 5:		result = this.source.getWidth(); break;
			default:	result = this.source.getLength();	
		}
		return result;
	}
	
	public int getWidth() {
		final int result;
		switch (this.orientation) {
			case 1:		result = this.source.getWidth(); break;
			default:	result = this.source.getHeight();
		}
		return result;
	}
	
	public void resetRuntimeSteps () {
		clearRuntimeSteps();
		this.linkedRotations.forEach(next -> next.clearRuntimeSteps());
	}
	
	private void clearRuntimeSteps() {
		this.runtimeSteps.clear();
	}
	
	public void addRuntimeStep (final CargoItemStepRuntime step) {
		addRuntimeStepInternal(step);
		this.linkedRotations.forEach(next -> next.addRuntimeStepInternal(step));
	}
	
	private void addRuntimeStepInternal (final CargoItemStepRuntime step) {
		this.runtimeSteps.add(step);
	}

	public int getAvailableQuantity() {
		int usedQuantity = 0;
		for (final CargoItemStepRuntime step : runtimeSteps) {
			if (step.isPlaced()) {
				usedQuantity += step.getQuantity();
			}
		}
		return this.remainingQuantity - usedQuantity;
	}
}
