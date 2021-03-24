package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.BasePackage;

public class BasePackageAreaComparator implements Comparator<BasePackage>{

	@Override
	public int compare(BasePackage o1, BasePackage o2) {
		final int o1Area = o1.getLength() * o1.getWidth();
		final int o2Area = o2.getLength() * o2.getWidth();
		return (-1) * Integer.compare(o1Area, o2Area);
	}

}
