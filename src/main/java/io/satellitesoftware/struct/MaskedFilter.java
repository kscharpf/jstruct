package io.satellitesoftware.struct;

import java.util.Map;
import java.util.HashMap;

public class MaskedFilter implements IFilter {
	private String            mMaskName;
	private Map<String, Long> mFieldToBit;

	public MaskedFilter(final String maskName, Map<String, Long> fieldToBit) {
		mMaskName   = maskName;
		mFieldToBit = fieldToBit;
	}

	public Map<String, Number> apply(Map<String, Number> m) {
		Map<String, Number> out = new HashMap<String, Number>();

		long maskValue = m.get(mMaskName).longValue();
		for(Map.Entry<String, Number> e : m.entrySet()) {
			if(mFieldToBit.containsKey(e.getKey())) {
				long v = mFieldToBit.get(e.getKey());
				if((v & maskValue) != 0) {
					out.put(e.getKey(), e.getValue());
				}
			} else {
				out.put(e.getKey(), e.getValue());
			}
		}
		return out;
	}
}
