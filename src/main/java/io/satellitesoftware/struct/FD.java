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

	public void encode(ByteBuffer b, final int offset, final Number val) {
		switch(type) {
			case FLOAT:
				b.putFloat(offset, ((Float)val).floatValue());
				break;

			case UINT:
				b.putInt(offset, ((Long)val).intValue());
				break;

			case INT:
				b.putInt(offset, ((Long)val).intValue());
				break;

			case USHORT:
				b.putShort(offset, ((Integer)val).shortValue());
				break;

			case UINT_SWAP:
				{
					long tmp = ((Long)val).longValue();
					tmp = ((tmp & 0xFFFF0000)>>>16) | ((tmp & 0x0000FFFF) << 16);
					b.putInt(offset, (int)tmp);
				}
				break;
		}
	}

	public Number decode(ByteBuffer b) {
		Number result = null;
		switch(type) {
			case FLOAT:
				result = new Float(b.getFloat());
				break;

			case UINT:
				result = new Long(((long)(b.getInt())) & 0xFFFFFFFFL);
				break;

			case INT:
				result = new Integer(b.getInt());
				break;

			case USHORT:
				result = new Integer(((int)(b.getShort())) & 0xFFFF);
				break;

			case UINT_SWAP:
				{
					long val = b.getInt() & 0xFFFFFFFFL;
					val = ((val & 0xFFFF0000) >>> 16) | ((val & 0x0000FFFF) << 16);
					result = new Long(val & 0xFFFFFFFFL);
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
