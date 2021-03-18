package com.developsoft.comtainer.util.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.runtime.model.RuntimeListElement;

public class ItemsNumberComparator implements Comparator<RuntimeListElement>{

	@Override
	public int compare(final RuntimeListElement arg0, final RuntimeListElement arg1) {
		return (-1) * Integer.compare(arg0.countItems(), arg1.countItems());
	}

}
