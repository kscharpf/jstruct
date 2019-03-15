package io.satellitesoftware.struct;

import io.satellitesoftware.struct.DataType;
import io.satellitesoftware.struct.FD;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class StructureDefinition implements Iterable<FD> {
	private List<FD>         mOrderedFields;
	private Map<String, FD>  mNameToFieldDefinition = new HashMap<String, FD>();
	private int              mStructureSize = 0;

	/**
	 * Construct an empty structure definition
	 */
	public StructureDefinition() {
		mOrderedFields = new ArrayList<FD>();
	}

	/**
	 * Construct a structure definition from an ordered list of types. When
	 * using this constructor, any padding variables should be included in
	 * the list.
	 * @param orderedFields an ordered list of field definitions (ordered by position)
	 */
	public StructureDefinition(final List<FD> orderedFields) {
		mOrderedFields = orderedFields;
		for(FD fd : mOrderedFields) {
			mStructureSize += fd.type.getSize();
			mNameToFieldDefinition.put(fd.name, fd);
		}
	}

	/**
	 * Append an entry at the end of the field list.
	 * Note: this method is not concurrency safe due to the getSize operation
	 * @param field field definition to append to the end of the structure
	 */
	public void addField(final FD field) {
		mOrderedFields.add(field);
		mStructureSize += field.type.getSize();
		mNameToFieldDefinition.put(field.name, field);
	}

	/**
	 * Note: this method is not concurrency safe due to the addField operation
	 * @return int size of structure
	 */
	public int getSize() {
		return mStructureSize;
	}

	/**
	 * Get an iterator for the structure definition
	 */
	public Iterator<FD> iterator() {
		return mOrderedFields.iterator();
	}
}
