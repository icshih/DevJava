package test.gaia.cu9.du439.gaiaweb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.util.props.PropertyLoader;
import main.gaia.cu4.du439.gaiaweb.DataIngestor;

public class DataIngestorIntegrationTest {

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
	public void test() {
		DataIngestor ingest = new DataIngestor("data/test/hibtest.cfg.xml", "conf");
		ingest.initSession();
		ingest.persist("/Users/icshih/Gaia/data/CU4/NssSolution_20150909-20150909083809_Part_1_1.zip");
	}

}
