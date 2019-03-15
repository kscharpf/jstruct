package io.satellitesoftware.struct;

import java.util.Objects;
import java.nio.ByteBuffer;
import io.satellitesoftware.struct.DataType;

/**
 * This class specifies the definition of a field
 */
public class FD {
	public final DataType type;
	public final String   name;

	public FD(final DataType type, final String name) {
		this.type = type;
		this.name = name;
	}

	public void encode(ByteBuffer b, final int offset, final String val) {
		switch(type) {
			case FLOAT:
				b.putFloat(offset, Float.parseFloat(val));
				break;

			case UINT:
			case INT:
				b.putInt(offset, Integer.parseInt(val));
				break;

			case USHORT:
				b.putShort(offset, Short.parseShort(val));
				break;

			case UINT_SWAP:
				{
					int tmp = Integer.parseInt(val);
					tmp = ((tmp & 0xFFFF0000)>>>16) | ((tmp & 0x0000FFFF) << 16);
					b.putInt(offset, tmp);
				}
				break;
		}
	}

	public String decode(ByteBuffer b) {
		String result = null;
		switch(type) {
			case FLOAT:
				result = String.format("%f", b.getFloat());
				break;

			case UINT:
				result = String.format("%d", b.getInt() & 0xFFFFFFFFL);
				break;

			case INT:
				result = String.format("%d", b.getInt());
				break;

			case USHORT:
				result = String.format("%d", b.getShort() & 0xFFFF);
				break;

			case UINT_SWAP:
				{
					long val = b.getInt() & 0xFFFFFFFFL;
					val = ((val & 0xFFFF0000) >>> 16) | ((val & 0x0000FFFF) << 16);
					result = String.format("%d", val & 0xFFFFFFFFL);
				}
		}
		return result;
	}

	public boolean equals(FD rhs) {
		return  rhs.type == this.type &&
			rhs.name.equals(this.name);
	}

	public int hashCode() {
		return Objects.hash(this.type, this.name);
	}
}
