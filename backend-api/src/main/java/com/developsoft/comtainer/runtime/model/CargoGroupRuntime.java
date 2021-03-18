package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoGroupRuntime {

	private final CargoGroupDto source;
	private final List<CargoItemRuntime> items;
	
	public CargoGroupRuntime (final CargoGroupDto source) {
		super();
		this.source = source;
		this.items = new ArrayList<CargoItemRuntime>();
		initItems();
	}
	
	private void initItems() {
		if (source.getItems() != null) {
			source.getItems().forEach(item -> this.items.addAll(CargoItemRuntime.createRotations(item, this)));
		}
	}
	
	public boolean isPlaced() {
		return this.getItems().stream().filter(item -> item.isPlaced()).count() == 0;
	}
}
