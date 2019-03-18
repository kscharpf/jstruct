package io.satellitesoftware.struct;

import java.util.Map;

public class NullFilter implements IFilter {
	public Map<String, Number> apply(Map<String, Number> m) {
		return m;
	}
}
