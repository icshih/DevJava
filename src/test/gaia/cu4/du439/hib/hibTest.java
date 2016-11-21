package test.gaia.cu4.du439.hib;

import static org.junit.Assert.*;

import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.util.props.PropertyLoader;

public class hibTest {

	SessionFactory sessionFactory;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure()
				.build();
		sessionFactory = new MetadataSources(registry)
				.buildMetadata()
				.buildSessionFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
	}

}
