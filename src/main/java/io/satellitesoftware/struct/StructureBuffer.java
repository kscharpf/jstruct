package io.satellitesoftware.struct;

import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;
import io.satellitesoftware.struct.DataType;
import io.satellitesoftware.struct.FD;
import io.satellitesoftware.struct.StructureDefinition;
import java.util.List;
import java.util.ArrayList;

public class StructureBuffer {
	private final StructureDefinition mStructureDefinition;

	/**
	 * Construct a StructureBuffer corresponding to the provided definition
	 */
	public StructureBuffer(final StructureDefinition structureDefinition) {
		mStructureDefinition = structureDefinition;
	}

	/**
	 * Using the specified structure definition, convert a mapping of string names 
	 * to values into a bytebuffer.
	 *
	 * @param fieldValueMap non-null mapping of strings to values
	 * @return ByteBuffer byte layout of the data structure
	 */
	public void compose(ByteBuffer b, final Map<String, Number> fieldValueMap) {
		// Iterate over the structure definition and extract entries from the provided map
		// if they are defined. If such a named field is not defined, ignore,
		// thereby leaving the bytes at that position null.
		int offset = 0;
		for(FD fd : mStructureDefinition) {
			Number val = fieldValueMap.get(fd.name);
			if(val != null) {
				fd.encode(b, offset, val);
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

		compose(b, fieldValueMap);
		return b;
	}

	/**
	 * Using the specified structure definition, convert a sequence of mappings of
	 * string names to values into a bytebuffer.
	 */
	public ByteBuffer compose_all(final List<Map<String, Number>> fieldValueMapSequence) {
		ByteBuffer b = ByteBuffer.allocate(mStructureDefinition.getSize() * fieldValueMapSequence.size());
		for(Map<String, Number> entry : fieldValueMapSequence) {
			compose(b, entry);
		}
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
			int offset = 0;
			for(FD fd : mStructureDefinition) {
				out.put(fd.name, fd.decode(b));
			}
		}
		return out;
	}

	/**
	 * Iterate through a bytebuffer decomming structures until
	 * the buffer is exhausted.
	 */
	public List<Map<String, Number>> decompose_all(ByteBuffer b) {
		List<Map<String, Number>> out = new ArrayList<Map<String,Number>>();
		while(b.remaining() >= mStructureDefinition.getSize()) {
			out.add(decompose(b));
		}
		return out;
	}
}
