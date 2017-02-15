package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.jdbc.test.DbTestUtility;
import gaia.cu1.tools.dal.jdbc.test.DbTestUtility.AutoIncrementInfo;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.validation.dm.TestResult;
import gaia.cu9.validation.dm.TestResultFlag;
import gaia.cu9.validation.dmimpl.TestResultImpl;

public class dbIntegrationTest {

	static JdbcStore postgres, derby;
	static Class<?> dmClass = TestResult.class;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		postgres = ValDbConnector.getNewDbConnection("data/test/PostgresDal.properties");
		derby = ValDbConnector.getNewDbConnection("data/test/DerbyDal.properties");
		postgres.setAutoCommit(true);
		derby.setAutoCommit(true);
		postgres.createTableIfMissing(dmClass);
		derby.createTableIfMissing(dmClass);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		postgres.dropTable(dmClass);
		derby.dropTable(dmClass);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws GaiaDataAccessException {
		assertEquals("PostgreSQL", postgres.getDbType().getName());
		assertEquals("Derby", derby.getDbType().getName());
		assertEquals("dpccu9validationtestresult", postgres.getTableName(dmClass));
		assertEquals("dpccu9validationtestresult", derby.getTableName(dmClass));
	}
	
	@Test
	public void testINSERT() throws GaiaDataAccessException {
		TestResult object = new TestResultImpl();
		object.setId(1L);
		object.setRunId("ABCDEF");
		LocalDateTime now = ValDbDataTimer.getDateTimeNow();
		object.setStartDate(ValDbDataTimer.getDate(now));
		object.setStartTime(ValDbDataTimer.getTime(now));
		object.setTestCase("testCase001");
		object.setDescription("Unit test case 1");
		object.setFlag(TestResultFlag.PENDING.name());
		
		postgres.addObject(object);
		derby.addObject(object);
	}
	
	static void modifyCreateTable(JdbcStore jdbcStore, Class<?> dmClass, String fieldName) {
		AutoIncrementInfo aii = DbTestUtility.mutatePropertyToAddAutoIncrement(jdbcStore.getDbType(), dmClass, fieldName);
        DbTestUtility.mutatePropertyToAddAutoIncrement(jdbcStore.getDbType(), dmClass, fieldName);
	}
	
	@Test
	public void testModifyCreateTable() {
		AutoIncrementInfo pii = DbTestUtility.mutatePropertyToAddAutoIncrement(postgres.getDbType(), dmClass, "runId");
		AutoIncrementInfo dii = DbTestUtility.mutatePropertyToAddAutoIncrement(derby.getDbType(), dmClass, "runId");
		System.out.println("PostgreSQL: OLD CREATE TABLE: " + pii.oldCreateTable);
		System.out.println("PostgreSQL: NEW CREATE TABLE: " + pii.newCreateTable);
		System.out.println("DERBY: OLD CREATE TABLE: " + dii.oldCreateTable);
		System.out.println("DERBY: NEW CREATE TABLE: " + dii.newCreateTable);
	}

}
