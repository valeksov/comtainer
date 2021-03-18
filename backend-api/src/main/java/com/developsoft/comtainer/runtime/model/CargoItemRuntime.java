package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import com.developsoft.comtainer.rest.dto.CargoItemDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemRuntime {

	private final CargoItemDto source;
	private int remainingQuantity;
	private final int orientation;
	private final CargoGroupRuntime group;
	
	private final List<CargoItemRuntime> linkedRotations;
	
	public CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group) {
		this(source, group, 1);
	}
	
	private CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group, int orientation) {
		super();
		this.source = source;
		this.remainingQuantity = source.getQuantity();
		this.orientation = orientation;
		this.linkedRotations = new ArrayList<CargoItemRuntime>();
		this.group = group;
	}
	
	public static List<CargoItemRuntime> createRotations(final CargoItemDto source, final CargoGroupRuntime group) {
		final List<CargoItemRuntime> result = new ArrayList<CargoItemRuntime>();
		final CargoItemRuntime orientation1 = new CargoItemRuntime(source, group, 1);
		result.add(orientation1);
		if (source.isRotatable()) {
			final CargoItemRuntime orientation3 = (source.getHeight() != source.getWidth()) ? new CargoItemRuntime(source, group, 3) : null;
			final CargoItemRuntime orientation5 = (source.getHeight() != source.getLength()) ? new CargoItemRuntime(source, group, 5) : null;
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
	
}
