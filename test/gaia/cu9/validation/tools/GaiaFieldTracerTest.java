package gaia.cu9.validation.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.archive.architecture.core.dm.GaiaSource;

public class GaiaFieldTracerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void CU4Test() {
		GaiaFieldTracer test = new GaiaFieldTracer(NSSSolution.class);
		assertEquals("long", test.getFieldType("sourceid"));
		assertNull(test.getFieldType("ra"));
	}
	
	@Test
	public void CU9Test() {
		GaiaFieldTracer test = new GaiaFieldTracer(GaiaSource.class);
		assertEquals("Long", test.getFieldType("sourceid"));
		assertEquals("Double", test.getFieldType("ra"));
	}

}
