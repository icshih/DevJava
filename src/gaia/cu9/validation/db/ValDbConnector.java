/*
 * CU9 WP940 Validation
 * Copyright (C) 2006-2014 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package gaia.cu9.validation.db;

import gaia.cu1.tools.dal.DatabaseStore;
import gaia.cu1.tools.dal.jdbc.JdbcStore;
import gaia.cu1.tools.dal.pool.GaiaObjectPool;
import gaia.cu1.tools.dal.pool.StorePools;
import gaia.cu1.tools.exception.GaiaConfigurationException;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.util.props.PropertyLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the database configuration relying on GaiaTools'
 * DAL Api.
 * 
 * @author icshih
 * @version $Id: ValDbConnector.java 545748 2017-02-07 15:47:32Z ishih $
 */
public class ValDbConnector {

	private static final String DB_URL = "gaiaval.n1data.lan";
	private static final String DB_PORT = "1528";
	private static final String DB_HOME = "cu9testdata/scival/VALDB";

	static GaiaObjectPool<DatabaseStore> pool = null;
	static JdbcStore store = null;

	private static final Logger LOG = LoggerFactory.getLogger(ValDbConnector.class);

	public static JdbcStore getJdbcStore() {
		return store;
	}
	
	/**
	 * A convenience static method to get a jdbc store object according to the
	 * dal property
	 *
	 * @param dalPropertyFile
	 * @return
	 * @throws GaiaDataAccessException
	 * @throws GaiaConfigurationException
	 */
	public static JdbcStore getNewDbConnection(String dalPropertyFile)
			throws GaiaDataAccessException, GaiaConfigurationException {
		setDbFrom(dalPropertyFile);
		GaiaObjectPool<DatabaseStore> pool = StorePools.getDefaultPool();
		DatabaseStore store = pool.borrowObject();
		return (JdbcStore) store;
	}

	/**
	 * Configures Derby database with Embedded mode
	 * <p>
	 * The embedded database is used for development and integration test only.
	 * It creates a database in WP's reports/ directory. It will be removed with
	 * "ant cleanall".
	 *
	 * @param wpRootDir
	 */
	public static void setDerbyEmbededAt(String wpRootDir) {
		PropertyLoader.setProperty("gaia.cu1.tools.dal.Store", "gaia.cu1.tools.dal.jdbc.JdbcStore");
		PropertyLoader.setProperty("gaia.cu1.tools.db.username", "valtest");
		PropertyLoader.setProperty("gaia.cu1.tools.db.password", "valtest");
		PropertyLoader.setProperty("gaia.cu1.tools.db.driver", "org.apache.derby.jdbc.EmbeddedDriver");
		PropertyLoader.setProperty("gaia.cu1.tools.db.url", "jdbc:derby:" + wpRootDir + "/reports/VALDB;create=true");
		setDerbyLog(wpRootDir+ "/reports");
	}

	/**
	 * Configures Derby database with Client mode
	 * <p>
	 * The Client mode allows multiple access to the database. Before using this
	 * method, user needs to start a Derby database server.
	 *
	 * @param dbUrl the url address where the database is running
	 * @param dbPort the port to communicate with the database
	 * @param dbHome the full path to the database
	 * @param dbName the database name
	 */
	static void setDerbyClientAt(String dbUrl, String dbPort, String dbHome, String dbName, String schema) {
		PropertyLoader.setProperty("gaia.cu1.tools.dal.Store", "gaia.cu1.tools.dal.jdbc.JdbcStore");
		PropertyLoader.setProperty("gaia.cu1.tools.db.username", schema);
		PropertyLoader.setProperty("gaia.cu1.tools.db.password", "scival");
		PropertyLoader.setProperty("gaia.cu1.tools.db.driver", "org.apache.derby.jdbc.ClientDriver");
		PropertyLoader.setProperty("gaia.cu1.tools.db.url",
				String.format("jdbc:derby://%s:%s//%s/%s;create=true", dbUrl, dbPort, dbHome, dbName, ";create=true"));
		setDerbyLog();
	}

	/**
	 * Sets the validation database on ESAC
	 * <p>
	 * This method is used when the validation is running on ESAC VM.
	 *
	 * @param dbName the database name
	 * @param schema the schema name in the database
	 */
	public static void setDerbyClientAtESAC(String dbName, String schema) {
		setDerbyClientAt(DB_URL, DB_PORT, DB_HOME, dbName, schema);
	}

	/**
	 * Sets the validation database using the DAL property
	 *
	 * @param dalPropertyFile
	 */
	public static void setDbFrom(String dalPropertyFile) {
		PropertyLoader.load(dalPropertyFile, true);
		setDerbyLog();
	}

	/**
	 * Resets derby log file property
	 *
	 * @param rootDir
	 */
	public static void setDerbyLog(String rootDir) {
		if (System.getProperty("derby.stream.error.file") == null) {
			System.setProperty("derby.stream.error.file", rootDir + "/reports/derby.log");
		}
	}

	static void setDerbyLog() {
		setDerbyLog(System.getProperty("user.dir"));
	}

	/**
	 * Sets the default database pool based on the DAL properties
	 * 
	 * @throws GaiaConfigurationException
	 * @throws GaiaDataAccessException
	 */
	public static void setDefaultPool() throws GaiaDataAccessException, GaiaConfigurationException {
		pool = StorePools.getDefaultPool();
//		openDefaultJdbcStore();
	}

	/**
	 * Casts the DatabaseStore object to a JdbcStore
	 * <p>
	 * If the database property is not set, a ClassCastException will be thrown.
	 * @throws GaiaDataAccessException
	 */
	public static void openDefaultJdbcStore() throws GaiaDataAccessException {
		store = (JdbcStore) pool.borrowObject();
	}

	/**
	 * Returns the DatabaseStore instance to the pool 
	 * <p>
	 * The store itself is still alive
	 */
	static void closeDefaultJdbcStore() {
		if (store != null) {
			try {
				pool.returnObject(store);
				LOG.info("Pool has {} active JdbcStore instances", ValDbConnector.pool.getNumActive());
			} catch (GaiaDataAccessException e) {
				LOG.error("There is a problem of returning the store to the pool");
			}
		} else {
			LOG.warn("There is not active JdbcStore instance");
		}
	}
}
