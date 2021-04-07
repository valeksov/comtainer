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
	
	
	public CargoItemRuntime (final CargoItemDto source, final CargoGroupRuntime group) {
		super();
		this.source = source;
		this.group = group;
		this.remainingQuantity = source.getQuantity();
	}
	
	private void addItem (final List<CargoItemPlacementRuntime> result, final CargoItemPlacementRuntime item, final int maxLength, final int maxWidth, final int maxHeight, 
													final boolean fixedLength, final boolean fixedWidth, final boolean fixedHeight) {
		if (fixedLength && item.getLength() != maxLength) {
			return;
		}
		if (maxLength > 0 && item.getLength() > maxLength) {
			return;
		}
		if (fixedWidth && item.getWidth() != maxWidth) {
			return;
		}
		if (maxWidth > 0 && item.getWidth() > maxWidth) {
			return;
		}
		if (fixedHeight && item.getHeight() != maxHeight) {
			return;
		}
		if (maxHeight > 0 && item.getHeight() > maxHeight) {
			return;
		}
		result.add(item);
	}
	
	public List<CargoItemPlacementRuntime> createPlacements(final int maxLength, final int maxWidth, final int maxHeight, 
																	final boolean fixedLength, final boolean fixedWidth, final boolean fixedHeight) {
		final List<CargoItemPlacementRuntime> result = new ArrayList<CargoItemPlacementRuntime>();
		for (int i = 0; i < getRemainingQuantity(); i++) {
			addItem(result, new CargoItemPlacementRuntime(this, 1), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
			if (!getSource().getLength().equals(getSource().getWidth())) {
				addItem(result, new CargoItemPlacementRuntime(this, 2), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
			}
			if(getSource().isRotatable()) {
				if (!getSource().getHeight().equals(getSource().getWidth())) {
					addItem(result, new CargoItemPlacementRuntime(this, 3), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
					if (!getSource().getLength().equals(getSource().getHeight())) {
						addItem(result, new CargoItemPlacementRuntime(this, 4), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
					}
				}
				if (!getSource().getLength().equals(getSource().getHeight())) {
					addItem(result, new CargoItemPlacementRuntime(this, 5), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
					if (!getSource().getHeight().equals(getSource().getWidth())) {
						addItem(result, new CargoItemPlacementRuntime(this, 6), maxLength, maxWidth, maxHeight, fixedLength, fixedWidth, fixedHeight);
					}
				}
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
	
	public float getWeight() {
		return getSource().getWeight();
	}
	
	public void print(final String preffix) {
		final StringBuffer strBuf = new StringBuffer();
		if (preffix != null) {
			strBuf.append(preffix);
			strBuf.append(" ");
		}
		strBuf.append("Runtime Item (");
		strBuf.append(getSource().getId());
		strBuf.append(") ");
		if (getRemainingQuantity() > 1) {
			strBuf.append(getRemainingQuantity());
			strBuf.append(" * ");
		}
		strBuf.append("(");
		strBuf.append(getLength());
		strBuf.append(" x ");
		strBuf.append(getWidth());
		strBuf.append(" x ");
		strBuf.append(getHeight());
		strBuf.append("), W:");
		strBuf.append(getWeight());
		System.out.println(strBuf.toString());
	}

}
