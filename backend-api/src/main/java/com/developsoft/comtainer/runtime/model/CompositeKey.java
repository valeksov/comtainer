package com.developsoft.comtainer.runtime.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompositeKey {
	private final Integer height;
	private final Integer width;
	
	public CompositeKey(final Integer height, final Integer width) {
		super();
		this.height = height;
		this.width = width;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			final CompositeKey other = (CompositeKey) o;
			return this.height.equals(other.height) && this.width.equals(other.width);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final Long l = ((long)height)*1000000000L + width;
		return l.hashCode();
	}

}
