package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.PackageGroup;

public class GroupCountComparator implements Comparator<PackageGroup> {
	
	@Override
	public int compare(final PackageGroup o1, final PackageGroup o2) {
		return (-1) * Integer.compare(o1.countPackages(), o2.countPackages());
	}

}
