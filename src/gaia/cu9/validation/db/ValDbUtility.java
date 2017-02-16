package gaia.cu9.validation.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.tools.dal.jdbc.JdbcDdlGenerator;
import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.jdbc.JdbcStore.DatabaseType;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.util.props.PropertyLoader;

/**
 * This class adapts GaiaTools' DbTestUtility to modify the table creation
 * property, so that the modified table permits field to auto-incremental the
 * value.
 * 
 * @author icshih
 *
 */
public class ValDbUtility {

	private static final Logger LOG = LoggerFactory.getLogger(ValDbUtility.class);

	/**
	 * Replace the field type in the createTable property.
	 * 
	 * @param dbType
	 *            data type whose property should be changed
	 * @param clazz
	 *            data model class
	 * @param columnName
	 *            the column name to change
	 * @param newDefinition
	 *            the new definition. Can use {TYPE} as placehold for previous
	 *            value.
	 * @return the **OLD** value (e.g. in case you want to set it back
	 *         afterwards
	 */
	public static String replaceCreateTableStatement(DatabaseType dbType, Class<?> clazz, String columnName,
			String newDefinition) {

		final String key = new JdbcDdlGenerator(dbType).getCreateTablePropertyKey(clazz);
		final String createTable = PropertyLoader.getProperty(key);

		final String newValue = getCreateTableWithAutoIncrement(createTable, columnName, newDefinition);

		String oldValue = PropertyLoader.setProperty(key, newValue);

		return oldValue;
	}

	public static String getCreateTableWithAutoIncrement(String createTable, String columnName, String newDefinition) {

		Pattern pattern = Pattern.compile("([,\\(]" + columnName + ") ([\\w\\(\\)]+)([,\\)])",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(createTable);
		if (!matcher.find()) {
			throw new IllegalArgumentException(
					"Failed to find column " + columnName + " in the createTable statement: " + createTable);
		}

		newDefinition = newDefinition.replace("{TYPE}", "$2");
		String newValue = matcher.replaceFirst("$1 " + newDefinition + "$3");

		return newValue;
	}

	/**
	 * Sets up a given field as an auto-increment field for testing. The table
	 * involved will probably be dropped and recreated. Serves as a reference on
	 * how to do this for different db's.
	 * 
	 * @param jdbcStore
	 *            the JdbcStore instance to use
	 * @param dmClass
	 *            the data model class for the table we want to change
	 * @param fieldName
	 *            the data model field name (not the column name)
	 * @return a 2 element array: { start, increment} which give expected the
	 *         auto-increment start value and increment value.
	 * @throws GaiaDataAccessException
	 *             if something goes horribly wrong
	 */
	public static int[] setupAutoIncrementField(JdbcStore jdbcStore, Class<?> dmClass, String fieldName)
			throws GaiaDataAccessException {

		final DatabaseType dbType = jdbcStore.getDbType();

		AutoIncrementInfo aii = getAutoIncrementInfo(dbType, dmClass, fieldName);
		PropertyLoader.setProperty(aii.key, aii.newCreateTable);

		if (!DatabaseType.ORACLE.equals(dbType)) {
			try {
				jdbcStore.dropTable(dmClass);
				jdbcStore.createTableIfMissing(dmClass);
				jdbcStore.commit();
			} finally {
				PropertyLoader.setOrClearProperty(aii.key, aii.oldCreateTable);
			}
		} else {
			final String columnName = PropertyLoader.getProperty(dmClass.getName() + "." + fieldName);
			createOracleTrigger(jdbcStore, dmClass, columnName, 100, 2);
			final String genKeysKey = dmClass.getName() + ".generatedKeys";
			PropertyLoader.setProperty(genKeysKey, columnName);
		}

		return new int[] { aii.start, aii.increment };

	}

	/**
	 * Where we can choose the auto-increment start/inc values, choose something
	 * other than 1,1 : it makes it easier to distinguish code based increments
	 * (e.g. in whiteboard)/
	 * <p>
	 * <b>newType in Derby database is modified from the original DbTestUltility.</b>
	 * @param dbType
	 * @param dmClass
	 * @param fieldName
	 * @return
	 */
	public static AutoIncrementInfo getAutoIncrementInfo(final DatabaseType dbType, final Class<?> dmClass,
			final String fieldName) {

		final int[] startAndIncrement;
		final String newType;
		if (DatabaseType.CACHE.equals(dbType) || DatabaseType.H2.equals(dbType)) {
			newType = "IDENTITY";
			startAndIncrement = new int[] { 1, 1 };
		} else if (DatabaseType.DERBY.equals(dbType)) {
			newType = "INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";
			startAndIncrement = new int[] { 1, 1 };
		} else if (DatabaseType.POSTGRESQL.equals(dbType)) {
			newType = "SERIAL";
			startAndIncrement = new int[] { 1, 1 };
		} else if (DatabaseType.MYSQL.equals(dbType)) {
			newType = "{TYPE} AUTO_INCREMENT PRIMARY KEY";
			startAndIncrement = new int[] { 1, 1 };
		} else if (DatabaseType.ORACLE.equals(dbType)) {
			// Don't drop the Oracle one, or we loose the trigger
			newType = null;
			startAndIncrement = new int[] { 100, 2 };
		} else {
			throw new IllegalStateException();
		}

		final String columnName = PropertyLoader.getProperty(dmClass.getName() + "." + fieldName);

		final String key = new JdbcDdlGenerator(dbType).getCreateTablePropertyKey(dmClass);
		final String createTable = PropertyLoader.getProperty(key);
		if (createTable == null) {
			throw new IllegalArgumentException("The property '" + key + "' has not been defined!");
		}

		final String newCreateTable = getCreateTableWithAutoIncrement(createTable, columnName, newType);

		return new AutoIncrementInfo(newType, startAndIncrement[0], startAndIncrement[1], key, createTable,
				newCreateTable);
	}

	public static AutoIncrementInfo mutatePropertyToAddAutoIncrement(final DatabaseType dbType, final Class<?> dmClass,
			final String fieldName) {
		AutoIncrementInfo aii = getAutoIncrementInfo(dbType, dmClass, fieldName);
		PropertyLoader.setProperty(aii.key, aii.newCreateTable);
		return aii;
	}

	public static class AutoIncrementInfo {

		public final String typeDefinition;
		public final int start;
		public final int increment;
		public final String key;
		public final String oldCreateTable;
		public final String newCreateTable;

		public AutoIncrementInfo(String typeDefinition, int start, int increment, String key, String oldDefinition,
				String newDefinition) {
			super();
			this.typeDefinition = typeDefinition;
			this.start = start;
			this.increment = increment;
			this.key = key;
			this.oldCreateTable = oldDefinition;
			this.newCreateTable = newDefinition;
		}

	}

	/**
	 * Set up an oracle sequence + trigger to create an auto-increment field
	 * 
	 * @param jdbcStore
	 *            store to use
	 * @param clazz
	 *            dm class name
	 * @param colName
	 *            SQL column name
	 * @param from
	 *            sequence start value
	 * @param increment
	 *            sequence increment value
	 * @throws GaiaDataAccessException
	 *             if it all goes wrong
	 */
	public static void createOracleTrigger(JdbcStore jdbcStore, Class<?> clazz, String colName, int from, int increment)
			throws GaiaDataAccessException {

		String tblName = jdbcStore.getTableName(clazz);

		String sql = "CREATE SEQUENCE src_id_seq START WITH " + from + " INCREMENT BY " + increment;
		String sql2 = "CREATE OR REPLACE TRIGGER acc_pk_trg " + "  BEFORE INSERT ON " + tblName + " FOR EACH ROW "
				+ " BEGIN " + "    SELECT src_id_seq.NEXTVAL INTO :new." + colName + " FROM DUAL; END;";

		try {
			jdbcStore.executeSQL("drop TRIGGER acc_pk_trg");
		} catch (Exception e) {
			LOG.warn("Failed to drop acc_pkg_rg: {}", e.getCause().getMessage());
		}
		try {
			jdbcStore.executeSQL("drop sequence src_id_seq");
		} catch (Exception e) {
			LOG.warn("Failed to drop src_id_seq: {}", e.getCause().getMessage());
		}

		try {
			jdbcStore.executeSQL(sql);
			jdbcStore.getConnection().createStatement().execute(sql2);
		} catch (Exception e) {
			throw new GaiaDataAccessException("Failed to set up triggers", e);
		}

		jdbcStore.commit();

	}

	public static String getNotNullStatement(DatabaseType dbType) {
		switch (dbType) {
		case H2:
			return "ALTER TABLE %s ALTER COLUMN %s SET NOT NULL";
		default:
			return "ALTER TABLE %s ALTER COLUMN %s NOT NULL";
		}
	}

}
