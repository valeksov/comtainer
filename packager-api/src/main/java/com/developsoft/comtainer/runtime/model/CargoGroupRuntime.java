package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoGroupRuntime {

	private CargoGroupDto source;
	private final List<CargoItemRuntime> items;
	private final List<CargoGroupRuntime> groups;
	
	public CargoGroupRuntime (final CargoGroupDto source) {
		super();
		this.source = source;
		this.items = new ArrayList<CargoItemRuntime>();
		this.groups = new ArrayList<CargoGroupRuntime>();
		initItems();
	}
	
	public List<CargoItemRuntime> initItems() {
		if (getSource().getItems() != null) {
			this.items.clear();
			getSource().getItems().forEach(item -> this.items.add(new CargoItemRuntime(item, this)));
		}
		if (getSource().getGroups() != null && getSource().getGroups().size() > 0) {
			this.groups.clear();
			for (final CargoGroupDto nextGroup : getSource().getGroups()) {
				final CargoGroupRuntime nextGroupRuntime = new CargoGroupRuntime(nextGroup);
				this.groups.add(nextGroupRuntime);
				this.items.addAll(nextGroupRuntime.getItems());
			}
		}
		return getItems();
	}
	
	public boolean isPlaced() {
		for (final CargoItemRuntime nextItem : getItems()) {
			if (!nextItem.isPlaced()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean wasPlacedPreviousRun() {
		return this.source.isAlreadyLoaded();
	}
}
