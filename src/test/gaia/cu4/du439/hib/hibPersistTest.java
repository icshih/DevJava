package test.gaia.cu4.du439.hib;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gaia.cu1.mdb.cu4.du439.dm.Composite;
import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.mdb.cu4.du439.dm.TwoBodySolution;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;
import main.gaia.cu4.du439.hib.CompositeId;
import main.gaia.cu4.du439.hib.NSSSolutionId;

public class hibPersistTest {

	static SessionFactory sessionFactory;
	List<NSSSolution> objects;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
		
		File conf = new File("conf/hibernate.cfg.xml");
		ServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure(conf)
				.build();
		MetadataSources sources = new MetadataSources(registry);
		sources.addDirectory(new File("conf"));
		sessionFactory = sources.buildMetadata().buildSessionFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Path test = Paths.get("data/test/NSSSolution_000-000-071.gbin");
		objects = new ArrayList<>();
		try (GbinReader<NSSSolution> reader = GbinFactory.getGbinReader(test.toFile())) {
			reader.readAllToList(objects);
		} catch (GaiaException e) {
			System.out.println("Unable to read " + test.toString());
		}
		System.out.println("Numbers of objects:" + objects.size());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void NSSSilutionTest() {
		NSSSolutionId nsss = null;
		Session session = null;
		for (NSSSolution n : objects) {
			nsss = NSSSolutionId.fromNSSSolutionImpl(n);
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.persist(nsss);
			session.getTransaction().commit();
			session.close();
		}
		
		Session read = sessionFactory.openSession();
		read.beginTransaction();
		List<NSSSolutionId> results = read.createQuery("from NSSSolutionId").getResultList();
		assertTrue( objects.get(0).getSourceId() == results.get(0).getSourceId() );
	}
	
	@Test@Ignore
	public void CompositeTest() {
		NSSSolution origin = objects.get(0);
		TwoBodySolution[] tbs = origin.getModels();
		CompositeId compId = null;
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Composite c = (Composite) tbs[0];
		
		compId = CompositeId.fromCompositeImpl(c);
		session.persist(compId);
		
		session.getTransaction().commit();
		session.close();
	}
}
