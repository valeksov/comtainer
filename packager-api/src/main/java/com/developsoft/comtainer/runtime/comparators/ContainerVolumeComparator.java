package com.developsoft.comtainer.runtime.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.rest.dto.ContainerDto;

public class ContainerVolumeComparator implements Comparator<ContainerDto>{

	@Override
	public int compare(final ContainerDto c1, final ContainerDto c2) {
		final Long vol1 = c1.getMaxAllowedVolume() != null ? c1.getMaxAllowedVolume() : c1.getLength().longValue() * c1.getWidth().longValue() * c1.getHeight().longValue();
		final Long vol2 = c2.getMaxAllowedVolume() != null ? c2.getMaxAllowedVolume() : c2.getLength().longValue() * c2.getWidth().longValue() * c2.getHeight().longValue();
		return (-1) * Long.compare(vol1, vol2);
	}

}
