package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gaia.cu1.tools.dal.jdbc.JdbcObjectUpdater;
import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.jdbc.MetaUtils;
import gaia.cu1.tools.dal.jdbc.JdbcStore.DatabaseType;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.infra.wb.Whiteboard;
import gaia.cu1.tools.util.GaiaFactory;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.validation.db.ValDbUtility.AutoIncrementInfo;
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
//		postgres.createTableIfMissing(dmClass);
//		derby.createTableIfMissing(dmClass);
//		createTable(postgres, dmClass);
//		createTable(derby, dmClass);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		postgres.dropTable(dmClass);
//		derby.dropTable(dmClass);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoNothing() {
		
	}
	
	@Test
	public void test() throws GaiaDataAccessException {
		assertEquals("PostgreSQL", postgres.getDbType().getName());
		assertEquals("Derby", derby.getDbType().getName());
		assertEquals("dpccu9validationtestresult", postgres.getTableName(dmClass));
		assertEquals("dpccu9validationtestresult", derby.getTableName(dmClass));
	}
	
	@Test
	public void testDefaultProperty() {
		String key = JdbcObjectUpdater.class.getName() + ".useGeneratedKeys";
		System.out.println(PropertyLoader.getProperty(key));
	}
	
	@Test
	public void testINSERT() throws GaiaException {
		PropertyLoader.setProperty(dmClass.getName()+".generatedKeys", "id");
		createTable(postgres, dmClass);
		
		String realTableName = MetaUtils.getDatabaseTableName(postgres.getTableName(dmClass), postgres.getDatabaseMetaData());
		System.out.println(realTableName);
		postgres.addObject(TestResult());
		postgres.commit();
//		derby.addObject(TestResult());
	}
	
	static TestResult TestResult() {
		TestResult object = new TestResultImpl();
//		object.setId(1L);
		object.setRunId("ABCDEF");
		LocalDateTime now = ValDbDataTimer.getDateTimeNow();
		object.setStartDate(ValDbDataTimer.getDate(now));
		object.setStartTime(ValDbDataTimer.getTime(now));
		object.setTestCase("testCase001");
		object.setDescription("Unit test case 1");
		object.setFlag(TestResultFlag.PENDING.name());
		return object;
	}
	
	static void createTable(JdbcStore jdbcStore, Class<?> dmClass) throws GaiaDataAccessException {
		modifyCreateTableProp(jdbcStore.getDbType(), dmClass, "id");
		jdbcStore.createTableIfMissing(dmClass);
	}
	
	static AutoIncrementInfo modifyCreateTableProp(DatabaseType dType, Class<?> dmClass, String fieldName) {
		AutoIncrementInfo aii = ValDbUtility.mutatePropertyToAddAutoIncrement(dType, dmClass, fieldName);
//		PropertyLoader.setProperty(dmClass.getName()+".useGeneratedKeys", "true");
		
	    return aii;
	}
	
	@Test@Ignore
	public void testModifyCreateTable() throws GaiaDataAccessException {
		postgres.dropTable(dmClass);
		derby.dropTable(dmClass);
		AutoIncrementInfo pii = modifyCreateTableProp(postgres.getDbType(), dmClass, "id");
		assertEquals("CREATE TABLE DPCCU9VALIDATIONTESTRESULT(ID SERIAL,RUNID VARCHAR(4000),STARTDATE VARCHAR(4000),STARTTIME VARCHAR(4000),SOLUTIONID BIGINT,TESTCASE VARCHAR(4000),DESCRIPTION VARCHAR(4000),FLAG VARCHAR(4000),GMDBCOMMENT VARCHAR(4000),ENDDATE VARCHAR(4000),ENDTIME VARCHAR(4000));", System.getProperty(pii.key));
		postgres.createTableIfMissing(dmClass);
		AutoIncrementInfo dii =modifyCreateTableProp(derby.getDbType(), dmClass, "id");
		assertEquals("CREATE TABLE DPCCU9VALIDATIONTESTRESULT(ID INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),RUNID VARCHAR(4000),STARTDATE VARCHAR(4000),STARTTIME VARCHAR(4000),SOLUTIONID BIGINT,TESTCASE VARCHAR(4000),DESCRIPTION VARCHAR(4000),FLAG VARCHAR(4000),GMDBCOMMENT VARCHAR(4000),ENDDATE VARCHAR(4000),ENDTIME VARCHAR(4000));", System.getProperty(dii.key));
		derby.createTableIfMissing(dmClass);
	}
	
	@Test
	public void testMatchCreateTable() {
		// The string, (ID BIGINT NOT NULL , doesn't match the pattern
		// have to modify the data model with string type
		String createTable = "CREATE TABLE DPCCU9VALIDATIONTESTRESULT(ID VARCHAR(4000), RUNID VARCHAR(4000),STARTDATE VARCHAR(4000),STARTTIME VARCHAR(4000),SOLUTIONID BIGINT,TESTCASE VARCHAR(4000),DESCRIPTION VARCHAR(4000),FLAG VARCHAR(4000),GMDBCOMMENT VARCHAR(4000),ENDDATE VARCHAR(4000),ENDTIME VARCHAR(4000))";
		String columnName = "id";
		Pattern pattern = Pattern.compile("([,\\(]" + columnName + ") ([\\w\\(\\)]+)([,\\)])", Pattern.CASE_INSENSITIVE);
		System.out.println(pattern.pattern());
        Matcher matcher = pattern.matcher(createTable);
        assertTrue(matcher.find());
	}

}
