package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.jdbc.MetaUtils;
import gaia.cu1.tools.dal.jdbc.JdbcStore.DatabaseType;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.validation.db.ValDbUtility.AutoIncrementInfo;
import gaia.cu9.validation.dm.TestResult;
import gaia.cu9.validation.dm.TestResultFlag;
import gaia.cu9.validation.dmimpl.TestResultImpl;

public class dbIntegrationTest {

	static JdbcStore postgres, derby;
	static Class<?> dmClass = TestResult.class;
	static RunIdGenerator generator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		generator = new RunIdGenerator();
		postgres = ValDbConnector.getNewDbConnection("data/test/PostgresDal.properties");
		derby = ValDbConnector.getNewDbConnection("data/test/DerbyDal.properties");
		createTable(postgres, dmClass);
		createTable(derby, dmClass);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		postgres.dropTable(dmClass);
		postgres.commit();
		derby.dropTable(dmClass);
		derby.commit();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConfiguration() throws GaiaDataAccessException {
		assertEquals("PostgreSQL", postgres.getDbType().getName());
		assertEquals("Derby", derby.getDbType().getName());
		assertEquals("dpccu9validationtestresult", postgres.getTableName(dmClass));
		assertEquals("dpccu9validationtestresult", derby.getTableName(dmClass));
		assertEquals("dpccu9validationtestresult",
				MetaUtils.getDatabaseTableName(postgres.getTableName(dmClass), postgres.getDatabaseMetaData()));
	}

	@Test
	public void testInsertAndUpdate() throws GaiaException, SQLException {
		String RUNID;
		String TESTCASE;
		TestResult object;
		for (int i = 0; i < 10; i++) {
			object = TestResult(i);
			postgres.addObject(object);
			postgres.commit();
			derby.addObject(object);
			derby.commit();
		}
		// The Where Property will be set in MDBDictionary
		postgres.setWhereProperty(dmClass, String.format("%s = ? AND %s = ?", "testCase", "runId"));
		derby.setWhereProperty(dmClass, String.format("%s = ? AND %s = ?", "testCase", "runId"));
		ResultSet rs = postgres.executeQuerySQL("SELECT runid, testcase FROM " + postgres.getTableName(dmClass));
		while (rs.next() != false) {
			RUNID = rs.getString("runid");
			TESTCASE = rs.getString("testcase");
			String[] colNames = new String[4];
			colNames[0] = "endDate";
			colNames[1] = "endTime";
			colNames[2] = "gmdbcomment";
			colNames[3] = "flag";
			Object[] colValues = new Object[4];
			LocalDateTime now = ValDbDataTimer.getDateTimeNow();
			colValues[0] = ValDbDataTimer.getDate(now);
			colValues[1] = ValDbDataTimer.getTime(now);
			colValues[2] = "This is an unit test";
			colValues[3] = TestResultFlag.ERROR.name();
			postgres.updateObject(dmClass, colNames, colValues, TESTCASE, RUNID);
			postgres.commit();
			derby.updateObject(dmClass, colNames, colValues, TESTCASE, RUNID);
			derby.commit();
		}
	}

	@Test
	public void testMatchCreateTable() {
		// The string, (ID BIGINT NOT NULL , doesn't match the pattern
		// have to modify the data model with string type
		String createTable = "CREATE TABLE DPCCU9VALIDATIONTESTRESULT(ID VARCHAR(4000), RUNID VARCHAR(4000),STARTDATE VARCHAR(4000),STARTTIME VARCHAR(4000),SOLUTIONID BIGINT,TESTCASE VARCHAR(4000),DESCRIPTION VARCHAR(4000),FLAG VARCHAR(4000),GMDBCOMMENT VARCHAR(4000),ENDDATE VARCHAR(4000),ENDTIME VARCHAR(4000))";
		String columnName = "id";
		Pattern pattern = Pattern.compile("([,\\(]" + columnName + ") ([\\w\\(\\)]+)([,\\)])",
				Pattern.CASE_INSENSITIVE);
		System.out.println(pattern.pattern());
		Matcher matcher = pattern.matcher(createTable);
		assertTrue(matcher.find());
	}

	TestResult TestResult(int i) {
		TestResult object = new TestResultImpl();
		object.setRunId(generator.next());
		LocalDateTime now = ValDbDataTimer.getDateTimeNow();
		object.setStartDate(ValDbDataTimer.getDate(now));
		object.setStartTime(ValDbDataTimer.getTime(now));
		object.setTestCase("testCase00" + i);
		object.setDescription("Unit test case " + i);
		object.setFlag(TestResultFlag.PENDING.name());
		return object;
	}

	static void createTable(JdbcStore jdbcStore, Class<?> dmClass) throws GaiaDataAccessException {
		modifyCreateTableProp(jdbcStore.getDbType(), dmClass, "id");
		String key = PropertyLoader.getProperty(dmClass.getName() + ".generatedKeys");
		if (key == null || key.trim().isEmpty())
			PropertyLoader.setProperty(dmClass.getName() + ".generatedKeys", "id");
		jdbcStore.createTableIfMissing(dmClass);
		jdbcStore.commit();
	}

	static AutoIncrementInfo modifyCreateTableProp(DatabaseType dType, Class<?> dmClass, String fieldName) {
		AutoIncrementInfo aii = ValDbUtility.mutatePropertyToAddAutoIncrement(dType, dmClass, fieldName);
		return aii;
	}

}
