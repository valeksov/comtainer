package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import com.developsoft.comtainer.rest.dto.CargoItemDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemRuntime {

	private final CargoItemDto source;
	private final CargoGroupRuntime group;
	private int remainingQuantity;
	
	
	CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group) {
		super();
		this.source = source;
		this.group = group;
		this.remainingQuantity = source.getQuantity();
	}
	
	public List<CargoItemPlacementRuntime> createPlacements() {
		final List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		final int endO = getSource().isRotatable() ? 6 : 2; 
		for (int i = 1; i <= getRemainingQuantity(); i++) {
			for (int o = 1; o <= endO; o++) {
				final CargoItemPlacementRuntime orientation = new CargoItemPlacementRuntime(this, o);
				result.add(orientation);
			}
		}
		return result;
	}
	
	public boolean isPlaced() {
		return getRemainingQuantity() == 0;
	}
	
	public boolean markUsed (final int quantity) {
		if (getRemainingQuantity() >= quantity) {
			setRemainingQuantity(getRemainingQuantity() - quantity);
			return true;
		}
		return false;
	}

	public String getId() {
		return getSource().getId();
	}
	
	public int getLength() {
		return getSource().getLength();
	}

	public int getWidth() {
		return getSource().getWidth();
	}

	public int getHeight() {
		return getSource().getHeight();
	}
	
	public float getWeigth() {
		return getSource().getWeigth();
	}
	
	public void print() {
		final StringBuffer strBuf = new StringBuffer();
		if (getRemainingQuantity() > 1) {
			strBuf.append(getRemainingQuantity());
			strBuf.append(" * ");
		}
		strBuf.append(getLength());
		strBuf.append(" x ");
		strBuf.append(getWidth());
		strBuf.append(" x ");
		strBuf.append(getHeight());
		strBuf.append(", W ");
		strBuf.append(getWeigth());
		System.out.println(strBuf.toString());
	}

}
