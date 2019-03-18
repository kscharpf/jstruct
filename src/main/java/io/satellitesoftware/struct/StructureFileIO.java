package io.satellitesoftware.struct;

import io.satellitesoftware.struct.StructureDefinition;
import io.satellitesoftware.struct.StructureBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import java.io.IOException;

public class StructureFileIO {
	private final StructureBuffer mBuffer;

	public StructureFileIO(final StructureDefinition definition) {
		mBuffer = new StructureBuffer(definition);
	}

	public List<Map<String,Number>> decompose(final String fname) {
		List<Map<String,Number>> rv = new ArrayList<Map<String,Number>>();
		try {
			RandomAccessFile aFile = new RandomAccessFile(fname, "r");
			FileChannel fc = aFile.getChannel();
			ByteBuffer b = ByteBuffer.allocate((int)fc.size());
			b.order(ByteOrder.BIG_ENDIAN);
			fc.read(b);
			b.flip();

			List<Map<String,Number>> out = mBuffer.decompose_all(b);
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rv;
	}

	public void compose(final String fname, List<Map<String,Number>> fieldValueMapSequence) {
		for(Map<String,Number> entry : fieldValueMapSequence) {
			try {
				File file = new File(fname);
				ByteBuffer b = mBuffer.compose_all(fieldValueMapSequence);
				FileChannel fc = new FileOutputStream(file).getChannel();
				fc.write(b);
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
