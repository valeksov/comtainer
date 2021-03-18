package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RuntimeListElement {

	private final CompositeKey key;
	private final List<CargoItemRuntime> list;
	private final List<List<CargoItemPlacementRuntime>> allCombinations;
	
	public RuntimeListElement(final CompositeKey key) {
		super();
		this.key = key;
		this.list = new ArrayList<CargoItemRuntime>();
		this.allCombinations = new ArrayList<List<CargoItemPlacementRuntime>>();
	}
	
	public RuntimeListElement(final CompositeKey key, final List<CargoItemRuntime> list) {
		this(key);
		this.list.addAll(list);
	}
	
	public int countItems() {
		int count = 0;
		for (final CargoItemRuntime next : list) {
			count += next.getRemainingQuantity();
		}
		return count;
	}
}
