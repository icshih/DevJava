package gaia.cu9.validation.space.histogram;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.validation.tap.VOTableReader;
import healpix.core.HealpixIndex;
import healpix.essentials.HealpixBase;
import healpix.essentials.Scheme;

public class GaiaHealpixToolsTest {

	HealpixBase base = new HealpixBase();
	
	long sourceId = 44437209113872256L;
	double ra = 54.4539656791039;
	double dec = 17.83844542369319;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyLoader.load();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertRaDec() throws Exception {
		List<Long> hpix = new ArrayList<>();
		VOTableReader reader = new VOTableReader(Paths.get("data/test", "result.vot").toFile());
		long start = System.currentTimeMillis();
		while (reader.hasNext()) {
			Object[] object = reader.next();
			hpix.add(GaiaHealpixTools.getGaiaHealPix((double) object[1], (double) object[2]));
		}
		long stop = System.currentTimeMillis();
		System.out.println("ConvertRaDec, processing time: " + (stop-start)/1000.0 + "s");
		assertEquals(reader.getNumberRows(), hpix.size());
	}
	
	@Test 
	public void testConvertSourceId() throws GaiaException {
		List<Long> hpix = new ArrayList<>();
		VOTableReader reader = new VOTableReader(Paths.get("data/test", "result.vot").toFile());
		long start = System.currentTimeMillis();
		while (reader.hasNext()) {
			Object[] object = reader.next();
			hpix.add(GaiaHealpixTools.getGaiaHealPix((long) object[0]));
		}
		long stop = System.currentTimeMillis();
		System.out.println("ConvertSourceId, Processing time: " + (stop-start)/1000.0 + "s");
		assertEquals(reader.getNumberRows(), hpix.size());
	}
	
	@Test
	public void testNumberPixels() throws Exception {
		long nside = 1;
		base = new HealpixBase(nside, Scheme.NESTED);
		System.out.println(String.format("Nside=%d, Npix=%d, Order=%d", nside, base.getNpix(), base.getOrder()));
		
	}
	
	@Test
	public void test() throws Exception {
		// pixel resolution in arcsec
		double resolution = 1800;
		int NSIDE = HealpixIndex.calculateNSide(resolution);
		base = new HealpixBase(NSIDE, Scheme.NESTED);
		System.out.println(String.format("Resolution of %s arcsec/p requires %d pixels (%d, %s): ",
				resolution, base.getNpix(), NSIDE, base.getScheme().name()));
	}
	
	@Test
	public void testSourceIdUtil() throws Exception {		
		GaiaHealpixTools hPix = new GaiaHealpixTools(4096L, Scheme.NESTED);
		long Hpixel = hPix.getHealPix(ra, dec);
		
		GaiaHealpixTools lPix = new GaiaHealpixTools(128L, Scheme.NESTED);
		long Lpixel = lPix.getHealPix(ra, dec);
		System.out.println(lPix.nest2uniq(128, Lpixel));
		System.out.println(lPix.nest2uniq(4096, Lpixel));
		System.out.println(lPix.nest2uniq(4096, Hpixel));
		System.out.println(String.format("Level-%d p: %d, Level-%d p: %d", hPix.base.getOrder(), Hpixel, lPix.base.getOrder(), Lpixel));
	}

	@Test
	public void testGetParentIndex() throws Exception {
		long gaiaPixel = GaiaHealpixTools.getGaiaHealPix(sourceId);
		assertEquals(1262, GaiaHealpixTools.getParentIndex(gaiaPixel, 7));
		assertEquals(gaiaPixel, GaiaHealpixTools.getParentIndex(gaiaPixel, 14));
	}
	
	@Test
	public void testGetChildIndexs() throws Exception {
		List<Long> pixels = GaiaHealpixTools.getChildIndexs(ra, dec, 7);
		assertTrue(pixels.contains(1293293L));
		assertTrue(pixels.contains(1292288L));
		double base = 4;
		double order = GaiaHealpixTools.GAIA_ORDER - 7;
		assertTrue(Math.pow(base, order) == pixels.size());
		assertTrue(GaiaHealpixTools.getChildIndexs(ra, dec, 14).isEmpty());

	}
}
