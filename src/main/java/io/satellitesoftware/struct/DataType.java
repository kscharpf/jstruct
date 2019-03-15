package io.satellitesoftware.struct;

public enum DataType {
	FLOAT(4), 
	USHORT(2), 
	UINT(4), 
	INT(4), 
	UINT_SWAP(4);

	private final int size;

	DataType(final int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}
}
