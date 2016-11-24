package test.gaia.cu4.du439.hib;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.mdb.cu4.du439.dm.Composite;
import gaia.cu1.mdb.cu4.du439.dm.NSSParam;
import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.mdb.cu4.du439.dm.TwoBodySolution;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;
import main.gaia.cu4.du439.hib.CompositeId;
import main.gaia.cu4.du439.hib.NSSParamId;
import main.gaia.cu4.du439.hib.NSSSolutionId;

public class hibDmTransferTest {

	static List<NSSSolution> objects;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		Path test = Paths.get("data/test/NSSSolution_000-000-071.gbin");
		objects = new ArrayList<>();
		try (GbinReader<NSSSolution> reader = GbinFactory.getGbinReader(test.toFile())) {
			reader.readAllToList(objects);
		} catch (GaiaException e) {
			System.out.println("Unable to read " + test.toString());
		}
		System.out.println("Numbers of objects:" + objects.size());
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
	public void NSSSolutionTest() {
		NSSSolution origin = objects.get(0);
		NSSSolutionId transfered = NSSSolutionId.fromNSSSolutionImpl(origin);
		NSSSolution recovered = NSSSolutionId.toNSSSolutionImpl(transfered);
		assertTrue( transfered.getId() == 0 );
		assertEquals( recovered.getSourceId(), origin.getSourceId() );
		TwoBodySolution[] ot= origin.getModels();
		TwoBodySolution[] rt = recovered.getModels();
		assertTrue( ot[0].getSourceId() == rt[0].getSourceId() );
		assertTrue( recovered.getFirstComp().equals(origin.getFirstComp()) );
	}

	@Test
	public void CompositeTest() {
		TwoBodySolution[] tbs = objects.get(1).getModels();
		Composite origin = (Composite) tbs[0];
		CompositeId transfered = CompositeId.fromCompositeImpl(origin);
		Composite recovered = CompositeId.toCompositeImpl(transfered);
		assertTrue( transfered.getId() == 0 );
		assertEquals( recovered.getSourceId(), origin.getSourceId() );
		assertTrue( recovered.getModelId().equals(origin.getModelId()) );
		NSSParam[] o = origin.getParams();
		NSSParam[] r = recovered.getParams();
		assertTrue( o.length == r.length );
		assertEquals( o[0].getName(), r[0].getName() );
	}
	
	@Test
	public void NSSParamTest() {
		Composite comp = (Composite) objects.get(1).getModels()[0];
		NSSParam origin = comp.getParams()[0];
		NSSParamId transfered = NSSParamId.fromNSSParamImpl(origin);
		NSSParam recovered = NSSParamId.toNSSParamImpl(transfered);
		assertTrue( transfered.getId() == 0 );
		assertEquals( recovered.getName(), origin.getName() );
		assertTrue( recovered.getIsUsed() == origin.getIsUsed() );
		assertTrue( recovered.getValue() == origin.getValue() );
	}
}