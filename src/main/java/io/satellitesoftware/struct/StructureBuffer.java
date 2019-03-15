import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;
import io.satellitesoftware.struct.DataType;
import io.satellitesoftware.struct.FD;
import io.satellitesoftware.struct.StructureDefinition;

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
	public ByteBuffer compose(final Map<String, String> fieldValueMap) {
		ByteBuffer b = ByteBuffer.allocate(mStructureDefinition.getSize());

		// Iterate over the structure definition and extract entries from the provided map
		// if they are defined. If such a named field is not defined, ignore,
		// thereby leaving the bytes at that position null.
		int offset = 0;
		for(FD fd : mStructureDefinition) {
			String val = fieldValueMap.get(fd.name);
			if(val != null) {
				fd.encode(b, offset, val);
			}
			offset += fd.type.getSize();
		}
		return b;
	}

	/**
	 * Using the specified structure definition, convert a byte buffer into
	 * a mapping of string names to string values
	 *
	 * @param ByteBuffer b non-null bytebuffer
	 * @return return Map<String,String> name:value pairs 
	 */
	public Map<String, String> decompose(ByteBuffer b) {
		Map<String,String> out = new HashMap<String,String>();
		int offset = 0;
		for(FD fd : mStructureDefinition) {
			out.put(fd.name, fd.decode(b));
		}
		return out;
	}
}
