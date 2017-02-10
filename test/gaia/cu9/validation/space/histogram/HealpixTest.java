package gaia.cu9.validation.space.histogram;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.GaiaFactory;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu9.validation.tap.VOTableReader;
import healpix.core.HealpixIndex;
import healpix.essentials.HealpixBase;
import healpix.essentials.Pointing;
import healpix.essentials.Scheme;

public class HealpixTest {

	HealpixBase base = new HealpixBase();

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
		HealpixTools tools = new HealpixTools(4096, Scheme.NESTED);
		List<Long> hpix = new ArrayList<>();
		VOTableReader reader = new VOTableReader(Paths.get("data/test", "result.vot").toFile());
		long start = System.currentTimeMillis();
		while (reader.hasNext()) {
			Object[] object = reader.next();
			hpix.add(tools.getHealPix((double) object[1], (double) object[2]));
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
			hpix.add((long) GaiaFactory.getSourceIdUtil().extractHealpix((long) object[0]));
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
		System.out.println(String.format("Pesolution of %s arcsec/p requires %d pixels (%d, %s): ",
				resolution, base.getNpix(), NSIDE, base.getScheme().name()));
	}
	
	@Test
	public void testSourceIdUtil() throws Exception {
		long sourceId = 44437209113872256L;
		double ra = 54.4539656791039;
		double dec = 17.83844542369319;
		
		HealpixTools hPix = new HealpixTools(4096, Scheme.NESTED);
		long Hpixel = hPix.getHealPix(ra, dec);
		
		HealpixTools lPix = new HealpixTools(128, Scheme.NESTED);
		long Lpixel = lPix.getHealPix(ra, dec);
		System.out.println(lPix.nest2uniq(128, Lpixel));
		System.out.println(lPix.nest2uniq(4096, Lpixel));
		System.out.println(lPix.nest2uniq(4096, Hpixel));
		System.out.println(String.format("Level-%d p: %d, Level-%d p: %d", hPix.base.getOrder(), Hpixel, lPix.base.getOrder(), Lpixel));
		
//		int pixel = GaiaFactory.getSourceIdUtil().extractHealpix(sourceId);
//		System.out.println(pixel);
//		System.out.println(Integer.toBinaryString(pixel));
//		base.setNsideAndScheme(4096, Scheme.NESTED);
//		System.out.println(getHealPix(ra, dec) + " " + base.getScheme().name());
//		long[] nei = base.neighbours(pixel);
//		for (long n : nei) {
//			System.out.println(n);
//		}
	}

	@Test
	public void testByteShift() {
		int root = 1262;
		int numOfStep = 12 - 7;
		for (int s=0; s < (numOfStep); s++) {
			System.out.println("Step: " + s);
			int next = root << 2;
			System.out.println(next);
			root = next;
		}
	}
	
	@Test
	public void testSubDivision() {
		long root = 1262;
		long subPixel = 1293293;
		long ROOT = root << 2;
		List<Long> next = nextBranch(root);
		List<Long> finalBranch = new ArrayList<>();
		int numOfStep = 12 - 7;
		for (int s=1; s < (numOfStep); s++) {
			long NEXT_ROOT = ROOT << 2;
			List<Long> thisBranch = new ArrayList<>();
			for (long n : next) {
				thisBranch.addAll(nextBranch(n));
			}
			if (s == (numOfStep-1)) {
				finalBranch = thisBranch;
			}
			next = thisBranch;
			ROOT = NEXT_ROOT;
		}
		finalBranch.stream().forEach(s -> System.out.println(s));
		assertTrue(finalBranch.contains(subPixel));
		assertTrue(finalBranch.contains((long) 1292288));
		assertTrue(finalBranch.contains(ROOT));
	}
	
	List<Long> nextBranch(long pixel) {
		List<Long> temp = new ArrayList<>();
		temp.add(4*pixel);
		temp.add(4*pixel+1);
		temp.add(4*pixel+2);
		temp.add(4*pixel+3);
		return temp;
	}
	@Test
	public void testHealPixMap() throws Exception {
		HealpixBase base = new HealpixBase();
		base.setNsideAndScheme(4096, Scheme.NESTED);
	}
	
	class HealpixTools {
	
		HealpixBase base;
		HealpixTools(long nside, Scheme scheme) {
			try {
				base = new HealpixBase(nside, scheme);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Gets Healpix index
		 * <p>
		 * The input (IAU) coordinates is transferred to HealPix one
		 * @param ra
		 * @param dec
		 * @return
		 * @throws Exception
		 */
		long getHealPix(double ra, double dec) {
			double theta = ((90.0-dec)/180.0)*Math.PI;
			double phi = (ra/360.0)*2*Math.PI;
			Pointing pointing = new Pointing(theta, phi);
			try {
				return base.ang2pix(pointing);
			} catch (Exception e) {
				return -1;
			}
		}
		
		long getHealPix(long sourceId) {
			try {
				return (long) GaiaFactory.getSourceIdUtil().extractHealpix(sourceId);
			} catch (GaiaException e) {
				return -1;
			}
		}
		
		long nest2uniq(int nside, long pnest) {
			int maxPixel = 12*nside*nside - 1;
			if (pnest <= maxPixel)
				return pnest + 4*nside*nside;
			else
				return -1;
		}
	}
	
	/**
	 * Gets Healpix index
	 * <p>
	 * The input (IAU) coordinates is transferred to HealPix one
	 * @param ra
	 * @param dec
	 * @return
	 * @throws Exception
	 */
	long getHealPix(double ra, double dec) throws Exception {
		double theta = ((90.0-dec)/180.0)*Math.PI;
		double phi = (ra/360.0)*2*Math.PI;
		Pointing pointing = new Pointing(theta, phi);
		return base.ang2pix(pointing);
	}
}
