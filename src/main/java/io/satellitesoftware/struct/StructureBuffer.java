package io.satellitesoftware.struct;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.nio.ByteBuffer;
import io.satellitesoftware.struct.DataType;
import io.satellitesoftware.struct.FD;
import io.satellitesoftware.struct.StructureDefinition;
import io.satellitesoftware.struct.IFilter;
import io.satellitesoftware.struct.NullFilter;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteOrder;

public class StructureBuffer {
	private final StructureDefinition mStructureDefinition;
	private final IFilter mFilter;

	public StructureBuffer(final StructureDefinition structureDefinition, final IFilter filter) {
		mStructureDefinition = structureDefinition;
		mFilter = filter;
	}

	/**
	 * Construct a StructureBuffer corresponding to the provided definition
	 */
	public StructureBuffer(final StructureDefinition structureDefinition) {
		this(structureDefinition, new NullFilter());
	}

	/**
	 * Using the specified structure definition, convert a mapping of string names 
	 * to values into a bytebuffer.
	 *
	 * @param fieldValueMap non-null mapping of strings to values
	 * @return ByteBuffer byte layout of the data structure
	 */
	public void compose(ByteBuffer b, final int initPos, final Map<String, Number> fieldValueMap) {
		// Iterate over the structure definition and extract entries from the provided map
		// if they are defined. If such a named field is not defined, ignore,
		// thereby leaving the bytes at that position null.
		int offset = 0;
		for(FD fd : mStructureDefinition) {
			Number val = fieldValueMap.get(fd.name);
			if(val != null) {
				fd.encode(b, initPos + offset, val);
			}
			offset += fd.type.getSize();
		}
	}
	/**
	 * Using the specified structure definition, convert a mapping of string names 
	 * to values into a bytebuffer.
	 *
	 * @param fieldValueMap non-null mapping of strings to values
	 * @return ByteBuffer byte layout of the data structure
	 */
	public ByteBuffer compose(final Map<String, Number> fieldValueMap) {
		ByteBuffer b = ByteBuffer.allocate(mStructureDefinition.getSize());
		b.order(ByteOrder.BIG_ENDIAN);

		compose(b, 0, fieldValueMap);
		return b;
	}

	/**
	 * Using the specified structure definition, convert a sequence of mappings of
	 * string names to values into a bytebuffer.
	 */
	public ByteBuffer composeAll(final List<Map<String, Number>> fieldValueMapSequence) {
		ByteBuffer b = ByteBuffer.allocate(mStructureDefinition.getSize() * fieldValueMapSequence.size());
		b.order(ByteOrder.BIG_ENDIAN);
		for(Map<String, Number> entry : fieldValueMapSequence) {
			compose(b, b.position(), entry);
			b.position(b.position() + mStructureDefinition.getSize());
		}
		b.position(0);

		return b;
	}

	/**
	 * Using the specified structure definition, convert a byte buffer into
	 * a mapping of string names to Number values
	 *
	 * @param ByteBuffer b non-null bytebuffer
	 * @return return Map<String,Number> name:value pairs 
	 */
	public Map<String, Number> decompose(ByteBuffer b) {
		Map<String,Number> out = new HashMap<String,Number>();
		if(b.remaining() >= mStructureDefinition.getSize()) {
			for(FD fd : mStructureDefinition) {
				out.put(fd.name, fd.decode(b));
			}
			out = mFilter.apply(out);
		}
		return out;
	}

	/**
	 * Iterate through a bytebuffer decomming structures until
	 * the buffer is exhausted.
	 */
	public List<Map<String, Number>> decomposeAll(ByteBuffer b) {
		List<Map<String, Number>> out = new ArrayList<Map<String,Number>>();
		while(b.remaining() >= mStructureDefinition.getSize()) {
			out.add(decompose(b));
		}
		return out;
	}

	/**
	 * Iterate through a bytebuffer decomming structures until
	 * the buffer is exhausted and return the output using a key
	 * field from the decom'd struct.
	 */
	public Map<Number, Map<String, Number>> decomposeByKey(ByteBuffer b, final String k) {
		Map<Number, Map<String, Number>> out = new TreeMap<Number, Map<String,Number>>();
		while(b.remaining() >= mStructureDefinition.getSize()) {
			Map<String, Number> m = decompose(b);
			out.put(m.get(k), m);
		}
		return out;
	}
}
