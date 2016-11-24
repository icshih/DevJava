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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.mdb.MdbDmVersion;
import gaia.cu1.mdb.cu4.du439.dm.NSSSolution;
import gaia.cu1.tools.dal.gbin.GbinFactory;
import gaia.cu1.tools.dal.gbin.GbinMetaData;
import gaia.cu1.tools.dal.gbin.GbinMetaData.ChunkMetaData;
import gaia.cu1.tools.dal.gbin.GbinReader;
import gaia.cu1.tools.exception.GaiaException;

public class NSSReader {

	static Logger logger = LoggerFactory.getLogger(NSSReader.class);

	public static String getCurrentDmVersion() throws GaiaException {
		return MdbDmVersion.VERSION;
	}

	/**
	 * Checks if the DM version
	 * <p>
	 * This methods checks the dm version consistency between the input gbin
	 * file and the current mdb. Developers shall be careful about the potential
	 * conflict.
	 * 
	 * @param gbinFile
	 * @return
	 */
	public static boolean isSameVersion(File gbinFile) {
		boolean isSame = false;
		try (GbinReader<?> reader = GbinFactory.getGbinReader(gbinFile)) {
			GbinMetaData meta = reader.getGbinMetaData();
			ChunkMetaData first = meta.getChunkList().get(0);
			if (first.mdbVersion.equals(NSSReader.getCurrentDmVersion())) {
				isSame = true;
				logger.info("The data model version {} in the gbin file is up-to-date.", first.mdbVersion);
			} else
				logger.info("The data model versions ARE NOT consistent, gbin file:{} -> current mdb:{}.",
						first.mdbVersion, NSSReader.getCurrentDmVersion());
		} catch (GaiaException e) {
			logger.error("Unable to read {}", gbinFile.getName());
		}
		return isSame;
	}

	/**
	 * Get a list of NSS objects from a gbin file
	 *
	 * @param gbinFile
	 * @return
	 */
	public static List<NSSSolution> getObjectsOf(File gbinFile) {
		List<NSSSolution> contents = new ArrayList<>();
		try (GbinReader<NSSSolution> reader = GbinFactory.getGbinReader(gbinFile)) {
			reader.readAllToList(contents);
		} catch (GaiaException e) {
			logger.error("Unable to read {}", gbinFile.getName());
		}
		return contents;
	}
	
	/**
	 * Get a list of NSS objects from a zip file
	 *
	 * @param zipFile the zip file downloaded from GaiaWeb
	 * @return
	 */
	public static List<NSSSolution> read(String zipFile) {
		List<NSSSolution> contents = new ArrayList<>();
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
						contents.addAll(getObjectsOf(temp.toFile()));
					}
			        return FileVisitResult.CONTINUE;
			    }
			});
			Files.delete(temp);
		} catch (IOException e) {
			logger.error("see", e);
		}
		return contents;
	}
		
	
}
