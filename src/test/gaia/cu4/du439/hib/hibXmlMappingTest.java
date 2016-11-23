package test.gaia.cu4.du439.hib;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.util.props.PropertyLoader;

public class hibXmlMappingTest {

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
		File conf = new File("conf/hibernate.cfg.xml");
		ServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure(conf)
				.build();
		MetadataSources sources = new MetadataSources(registry);
//		sources.addDirectory(new File("conf"));
		sources.addFile("conf/NSSSolutionId.hbm.xml");
		sources.addFile("conf/CompositeId.hbm.xml");
		sources.addFile("conf/NSSParamId.hbm.xml");
		sources.buildMetadata().buildSessionFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.getTransaction();
		tx.begin();
		System.out.println(tx.getStatus().name());
		tx.commit();
		System.out.println("Done");
	}

}
