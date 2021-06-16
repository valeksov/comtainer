package com.developsoft.comtainer.runtime.comparators;

import java.util.Comparator;

import com.developsoft.comtainer.rest.dto.CargoGroupDto;
import com.developsoft.comtainer.rest.dto.CargoItemDto;

public class GroupTotalVolumeComparator  implements Comparator<CargoGroupDto> {

	@Override
	public int compare(final CargoGroupDto g1, final CargoGroupDto g2) {
		return (-1) * Long.compare(getGroupTotalVolume(g1), getGroupTotalVolume(g2));
	}

	private Long getGroupTotalVolume (final CargoGroupDto group) {
		long result = 0;
		if (group != null) {
			if (group.getItems() != null && group.getItems().size() > 0) {
				for (final CargoItemDto nextItem : group.getItems()) {
					if (nextItem.getLength() != null && nextItem.getWidth() != null && nextItem.getHeight() != null && nextItem.getQuantity() != null) {
						final long itemVolume = nextItem.getLength().longValue() * nextItem.getWidth().longValue() * nextItem.getHeight().longValue() * nextItem.getQuantity().longValue();
						result += itemVolume;
					}
				}
			}
			
			if (group.getGroups() != null && group.getGroups().size() > 0) {
				for (final CargoGroupDto nextSubGroup : group.getGroups()) {
					result += getGroupTotalVolume(nextSubGroup);
				}
			}
		}
		return result;
	}
}
