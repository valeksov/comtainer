package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CargoItemPlacementListElement {

	private final CargoItemPlacementKey key;
	private final List<CargoItemPlacementRuntime> elements;
	private final List<CargoItemPlacementRuntime> maxChain;
	
	public CargoItemPlacementListElement(final CargoItemPlacementKey key) {
		super();
		this.key = key;
		this.elements = new ArrayList<CargoItemPlacementRuntime>();
		this.maxChain = new ArrayList<CargoItemPlacementRuntime>();
	}
	
	public CargoItemPlacementListElement(final CargoItemPlacementKey key, final List<CargoItemPlacementRuntime> allElements) {
		this(key);
		elements.addAll(allElements);
	}
	
	public void setMaxChain(final List<CargoItemPlacementRuntime> chainElements) {
		if (getMaxChain().size() > 0) {
			clearChain();
		}
		getMaxChain().addAll(chainElements);	
	}
	
	public void clearChain() {
		this.maxChain.clear();
	}
	
	public List<CargoItemPlacementRuntime> getAvailableElements() {
		return getElements().stream().filter(el -> !el.isPlaced()).collect(Collectors.toList());
	}
	
	public int countAvailableElements() {
		return getAvailableElements().size();
	}
}
