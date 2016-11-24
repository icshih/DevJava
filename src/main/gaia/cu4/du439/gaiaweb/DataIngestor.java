package main.gaia.cu4.du439.gaiaweb;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import main.gaia.cu4.du439.hib.NSSSolutionId;

public class DataIngestor {
	
	String mapConfDir = null;
	File dbConf = null;
	ServiceRegistry registry = null;
	MetadataSources sources = null;
	static SessionFactory sessionFactory = null;
	
	static Logger logger = LoggerFactory.getLogger(DataIngestor.class);
	
	public DataIngestor(String configFile, String mappingDir) {
		setHibConfig(configFile);
		setRegister();
		setMetadataSources(mappingDir);
	}
	
	/**
	 * Sets up Hibernate database configuration file
	 * @param configFile
	 */
	
	void setHibConfig(String configFile) {
		dbConf = new File(configFile);
	}
	
	void setRegister() {
		registry = new StandardServiceRegistryBuilder()
				.configure(dbConf)
				.build();
	}
	
	void setMetadataSources(String mapConfDir) {
		sources = new MetadataSources(registry);
		sources.addDirectory(new File(mapConfDir));
	}
	
	public MetadataSources getMetadataSources() {
		return sources;
	}
	
	public void initSession() {
		if (sources != null)
			sessionFactory = sources.buildMetadata().buildSessionFactory();
		else
			logger.error("Not Hibernate session is built.");
	}

	public void persist(File nssFile) {
		NSSSolutionId nsss = null;
		Session session = null;
		for (NSSSolution n : NSSReader.getObjectsOf(nssFile)) {
			nsss = NSSSolutionId.fromNSSSolutionImpl(n);
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.persist(nsss);
			session.getTransaction().commit();
			session.close();
		}
	}
	
	public void persist(String zipFile) {
		URI zipUri = URI.create("jar:file:" + zipFile);
		Map<String, String> env = new HashMap<>();
		env.put("create", "false");
		try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
			Path temp = Paths.get("data/temp/temp.gbin");
			Files.createFile(temp);
			Files.walkFileTree(zipfs.getPath("/"), new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
			    {
					if (file.getFileName().toString().endsWith(".gbin")) {
						Files.copy(file.toAbsolutePath(), temp, StandardCopyOption.REPLACE_EXISTING);
						persist(temp.toFile());
					}
			        return FileVisitResult.CONTINUE;
			    }
			});
			Files.delete(temp);
		} catch (IOException e) {
			logger.error("see", e);
		}
	}
	
	public static void main(String[] args) {
		
		String CONF_FILE = args[0];
		String MAPPING_DIR = args[1];
		String ZIP_FILE_PATH = args[2];
		
		DataIngestor ingest = new DataIngestor(CONF_FILE, MAPPING_DIR);
		ingest.initSession();
		ingest.persist(ZIP_FILE_PATH);
	}
}
