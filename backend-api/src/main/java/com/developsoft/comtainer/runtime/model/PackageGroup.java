package com.developsoft.comtainer.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackageGroup implements Comparable<PackageGroup>{

	private final Integer value;
	private final List<BasePackage> packages;

	public PackageGroup(final int value) {
		super();
		this.value = new Integer(value);
		this.packages = new ArrayList<BasePackage>();
	}
	public void addPackage(final BasePackage p) {
		if (!p.isCombined()) {
			this.packages.add(p);
		}
	}
	
	public int countPackages() {
		return (int) packages.stream().filter(p -> !p.isCombined()).count();
	}

	private List<BasePackage> getAvailable() {
		return packages.stream().filter(p -> !p.isCombined()).collect(Collectors.toList());
	}
	
	private static ComboPackage createCombo(final BasePackage first, final BasePackage second, final int val) {
		final int type = (first.getLength() == val) ? ComboPackage.BY_WIDTH : ComboPackage.BY_LENGTH;  
		return new ComboPackage(first, second, type);
	}
	
	public boolean match(final List<BasePackage> allPackages, final int maxLength, final int maxWidth) {
		final List<BasePackage> availablePackages = getAvailable();
		if (availablePackages.size() > 1) {
			for (int f = 0; f < availablePackages.size(); f++) {
				for (int s = 0; s < availablePackages.size(); s++) {
					final BasePackage first = availablePackages.get(f);
					final BasePackage second = availablePackages.get(s);
					if (!first.getId().equals(second.getId())) {
							final ComboPackage newPackage = createCombo (first, second, value); 
							if (Math.min(newPackage.getLength(), newPackage.getWidth()) <= Math.min(maxLength, maxWidth) &&
									Math.max(newPackage.getLength(), newPackage.getWidth()) <= Math.max(maxLength, maxWidth)) {
								newPackage.confirm();
								allPackages.add(newPackage);
								for (int i = 0; i < availablePackages.size(); i++) {
									if (i != f && i != s) {
										allPackages.add(availablePackages.get(i));
									}
								}
								return true;
							}
					}
				}
			}
		} 
		allPackages.addAll(availablePackages);
		return false;
	}

	@Override
	public int compareTo(final PackageGroup arg0) {
		return value.compareTo(arg0.value);
	}

	public static Map<Integer, PackageGroup> mapPackageGroups (final List<BasePackage> packages) {
		final Map<Integer, PackageGroup> result = new HashMap<Integer, PackageGroup>();
		for (final BasePackage p : packages) {
			final int valueKey =  p.getLength();
			PackageGroup group = result.get(valueKey);
			if (group == null) {
				group = new PackageGroup(valueKey);
				result.put(valueKey, group);
			}
			group.addPackage(p);
			
			if (p.getLength() != p.getWidth()) {
				group = result.get(p.getWidth());
				if (group == null) {
					group = new PackageGroup(p.getWidth());
					result.put(p.getWidth(), group);
				}
				group.addPackage(p);
			}
			
		}
		return result;
	}
	
	public static List<BasePackage> matchPackages(final List<BasePackage> packages, final int maxLength, final int maxWidth) {
		final List<BasePackage> result = new ArrayList<BasePackage>();
		List<BasePackage> localPackages = new ArrayList<BasePackage>();
		for (final BasePackage p : packages) {
			localPackages.add(p);
		}
		
		boolean hasMatch = true;
		while (hasMatch) {
			final Map<Integer, PackageGroup> mapGroups = mapPackageGroups(localPackages);
			final List<BasePackage> newPackages = new ArrayList<BasePackage>();
			final List<PackageGroup> listGroups = new ArrayList<PackageGroup>(); 
			listGroups.addAll(mapGroups.values());
			Collections.sort(listGroups);
			Collections.reverse(listGroups);
			hasMatch = false;
			for (final PackageGroup nextGroup : listGroups) {
				if (nextGroup.match(newPackages, maxLength, maxWidth)) {
					hasMatch = true;
				}
			}
			localPackages = newPackages;
		}
		final Map<String, BasePackage> packagesMap = new HashMap<String, BasePackage>();
		for (final BasePackage p : localPackages) {
			if (!p.isCombined()) {
				packagesMap.put(p.getId(), p);
			}
		}
		result.addAll(packagesMap.values().stream().collect(Collectors.toList()));
		return result;
	}
	
}
