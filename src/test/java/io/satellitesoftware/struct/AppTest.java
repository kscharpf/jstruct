package io.satellitesoftware.struct;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import io.satellitesoftware.struct.StructureDefinition;
import io.satellitesoftware.struct.FD;
import io.satellitesoftware.struct.DataType;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.IOException;


/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public List<FD> makeFields() {
	    List<FD> fields = new ArrayList<FD>();
	    fields.add(new FD(DataType.UINT, "Field1"));
	    fields.add(new FD(DataType.INT, "Field2"));
	    fields.add(new FD(DataType.USHORT, "Field3"));
	    fields.add(new FD(DataType.USHORT, "Field4"));
	    fields.add(new FD(DataType.UINT, "Field5"));
	    fields.add(new FD(DataType.UINT_SWAP, "Field6"));
	    return fields;
    }

    public StructureDefinition makeSD(List<FD> fields) {
	    return new StructureDefinition(fields);
    }

    public StructureDefinition makeSD() {
	    List<FD> fields = makeFields();
	    return makeSD(fields);
    }

    /**
     * Rigourous Test :-)
     */
    public void testStructureDefinition()
    {
	    List<FD> fields = makeFields();
	    StructureDefinition sd = makeSD(fields);

	    assertEquals(20, sd.getSize());

	    int i=0;
	    for(FD fd : sd) {
		    assertEquals(fields.get(i),fd);
		    i++;
	    }
    }

    public Map<String, Number> makeValues() {
	    Map<String, Number> m = new HashMap<String, Number>();
	    m.put("Field1", new Long(0xFFFFFFFFL));
	    m.put("Field2", new Integer(0x87654321));
	    m.put("Field3", new Integer(0));
	    m.put("Field4", new Integer(0xFEED));
	    m.put("Field5", new Long(0));
	    m.put("Field6", new Long(0));
	    return m;
    }
    public Map<String, Number> makeValues2() {
	    Map<String, Number> m = new HashMap<String, Number>();
	    m.put("Field1", new Long(0xEEEEEEEEL));
	    m.put("Field2", new Integer(0x12345678));
	    m.put("Field3", new Integer(3333));
	    m.put("Field4", new Integer(0xFEED));
	    m.put("Field5", new Long(23457899));
	    m.put("Field6", new Long(0xFEEDFACEL));
	    return m;
    }

    public void testStructureBuffer() {
	    StructureDefinition sd = makeSD();
	    StructureBuffer sb = new StructureBuffer(sd);
	    Map<String,Number> m = makeValues();
	    ByteBuffer b = sb.compose(m);
	    assertEquals(sb.decompose(b), m);
    }

    public List<Map<String, Number>> makeValueList() {
	    List<Map<String, Number>> myList = new ArrayList<Map<String,Number>>();
	    Map<String, Number> mv2 = makeValues2();
	    Map<String, Number> mv = makeValues();
	    myList.add(mv2);
	    myList.add(mv);

	    return myList;
    }

    public void testStructureBufferList() {
	    StructureDefinition sd = makeSD();
	    StructureBuffer sb = new StructureBuffer(sd);
	    List<Map<String, Number>> myList = makeValueList();

	    ByteBuffer b = sb.compose_all(myList);
	    
	    List<Map<String, Number>> myListCopy = sb.decompose_all(b);
	    assertEquals(myList.size(), myListCopy.size());
	    assertEquals(myList, myListCopy);
    }

    public void testStructuredFileIO() {
	    try {
	    	StructureDefinition sd = makeSD();
	    	StructureFileIO sf = new StructureFileIO(sd);
	    	List<Map<String, Number>> myList = makeValueList();

	    	File f = File.createTempFile("myfile_",".bin");
	    	String fname = f.getName();

		System.out.println("Created file: " + fname);
	    	f.delete();
	    	sf.compose(fname, myList);
	
	    	List<Map<String, Number>> myListCopy = sf.decompose(fname);
	
	    	assertEquals(myList, myListCopy);
	    } catch(IOException e) {
		    e.printStackTrace();
		    fail();
	    }
    }
}
