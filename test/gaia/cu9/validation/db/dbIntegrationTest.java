package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.util.props.PropertyLoader;

public class dbIntegrationTest {

	static JdbcStore postgres, derby;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		postgres = ValDbConnector.getNewDbConnection("data/test/PostgresDal.properties");
		derby = ValDbConnector.getNewDbConnection("data/test/DerbyDal.properties");
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
		
	}

}
