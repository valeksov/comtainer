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
	
	public CargoGroupRuntime (final CargoGroupDto source) {
		super();
		this.source = source;
		this.items = new ArrayList<CargoItemRuntime>();
		initItems();
	}
	
	public List<CargoItemRuntime> initItems() {
		if (source.getItems() != null) {
			this.items.clear();
			source.getItems().forEach(source -> this.items.add(new CargoItemRuntime(source, this)));
		}
		return this.items;
	}
	
	public boolean isPlaced() {
		return this.getItems().stream().filter(item -> !item.isPlaced()).count() == 0;
	}
	
	public boolean wasPlacedPreviousRun() {
		return this.source.isAlreadyLoaded();
	}
}
