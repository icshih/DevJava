package gaia.cu9.validation.tools;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.gaiatools.test.dm.AgisSource;
import gaia.cu1.mdb.cu3.agis.dm.Source;
import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu4.nss.epochparams.dm.CU7Periodicity;
import gaia.cu4.nss.epochparams.dm.EBElementary;
import gaia.cu4.nss.epochparams.dm.StarObject;
import gaia.cu4.nss.epochparams.dm.Transit;
import gaia.cu9.archive.architecture.core.dm.GaiaSource;
import gaia.cu9.validation.dm.TestResult;

public class GaiaFieldTracerTest {

	static GaiaFieldTracer test;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		test = new GaiaFieldTracer();
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
	public void PrintPropertyTest() {
		GaiaFieldTracer cu3 = new GaiaFieldTracer(TestResult.class);
		cu3.fieldList.stream()
		.forEach(f -> System.out.println(String.format("<property name=\"%s\" type=\"%s\"/>", f.getName(), f.getType().getSimpleName().toLowerCase())));
	}
	
	@Test
	public void CU4Test() {
		GaiaFieldTracer cu4 = new GaiaFieldTracer(NSSSolution.class);
		assertEquals("long", cu4.getFieldType("sourceid"));
		assertNull(cu4.getFieldType("ra"));
	}
	
	@Test
	public void CU9Test() {
		GaiaFieldTracer cu9 = new GaiaFieldTracer(GaiaSource.class);
		assertEquals("Long", cu9.getFieldType("sourceid"));
		assertEquals("Double", cu9.getFieldType("ra"));
	}

	@Test
	public void recurseSuperClassesTest() {
		test.recurseSuperClasses("gaia.cu4.nss.epochparams.dm.StarObject").stream()
			.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
					f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
		System.out.println("============");
		test.recurseSuperClasses("gaia.cu1.mdb.cu3.agis.dm.Source").stream()
		.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
				f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
		System.out.println("============");
		test.recurseSuperClasses("gaia.cu9.archive.architecture.core.dm.GaiaSource").stream()
		.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
				f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
	}
	
	@Test
	public void recurseSuperClasses2Test() throws GaiaException {
		test.recurseSuperClasses(StarObject.class).stream()
			.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
					f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
		System.out.println("============");
		test.recurseSuperClasses(Source.class).stream()
		.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
				f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
		System.out.println("============");
		test.recurseSuperClasses(GaiaSource.class).stream()
		.forEach(f -> System.out.println(String.format("%-26s : %-70s : %5b : %5b", 
				f.getName(), f.getGenericType().getTypeName(), f.getType().isArray(), f.getType().isEnum())));
	}
	
	@Test
	public void getNodeTest() throws GaiaException {
		List<String> so = test.getNode(StarObject.class);
		assertEquals(4, so.size());
		so.stream().forEach(n -> System.out.println(n));
		System.out.println("============");
		List<String> gs = test.getNode(GaiaSource.class);
		assertEquals(0, gs.size());
	}
	
	@Test
	public void getNodesTest() throws GaiaException {
		Map<Integer,List<String>> mso = test.getNodes(StarObject.class);
		assertEquals(3, mso.size());
		mso.get(0).stream().forEach(n -> System.out.println(n));
		mso.get(1).stream().forEach(n -> System.out.println(n));
		mso.get(2).stream().forEach(n -> System.out.println(n));
		System.out.println("============");
		Map<Integer,List<String>> mns = test.getNodes(NSSSolution.class);
		assertEquals(3, mso.size());
		mns.get(0).stream().forEach(n -> System.out.println(n));
		mns.get(1).stream().forEach(n -> System.out.println(n));
//		mns.get(2).stream().forEach(n -> System.out.println(n));
		System.out.println("============");
		Map<Integer,List<String>> mgs = test.getNodes(GaiaSource.class);
		assertEquals(1, mgs.size());
		mgs.get(0).stream().forEach(n -> System.out.println(n));
		
	}
	
	@Test
	public void getNodeMapTest() throws GaiaException {
		Map<String, Integer> mso = test.getNodeMap(StarObject.class);
		mso.keySet().forEach(k -> System.out.println(k + " : " + mso.get(k)));
		assertEquals(6, mso.size());
		System.out.println("============");
		Map<String, Integer> mns = test.getNodeMap(NSSSolution.class);
		mns.keySet().forEach(k -> System.out.println(k + " : " + mns.get(k)));
		assertEquals(2, mns.size());
		System.out.println("============");
		Map<String, Integer> mgs = test.getNodeMap(GaiaSource.class);
		mgs.keySet().forEach(k -> System.out.println(k + " : " + mgs.get(k)));
		assertEquals(1, mgs.size());
	}
	
	@Test
	public void getPropertiesTest() {
		test.getProperties(Transit.class);
	}
	@Test
	public void matchGaiaDmTest() {
		String string = "java.util.List<gaia.cu4.nss.epochparams.dm.EBElementary>";
		assertEquals("gaia.cu4.nss.epochparams.dm.EBElementary", test.matchGaiaDm(string));
		assertNull(test.matchGaiaDm("Bateau sur l'eau"));
	}
}
