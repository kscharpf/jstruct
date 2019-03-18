package io.satellitesoftware.struct;

import java.util.Map;

public interface IFilter {
	public Map<String, Number> apply(Map<String, Number> m);
}
