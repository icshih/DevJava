package gaia.cu9.validation.space.histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.GaiaFactory;
import healpix.core.HealpixIndex;
import healpix.essentials.HealpixBase;
import healpix.essentials.Pointing;
import healpix.essentials.Scheme;

public class GaiaHealpixTools {

	public static HealpixBase GAIA_BASE;
	public static final int GAIA_ORDER = 12;
	public static final long GAIA_NSIDE = 4096;
	public static final Scheme GAIA_SCHEME = Scheme.NESTED;
	int order;
	long nside;
	Scheme scheme;
	HealpixBase base;

	protected static final Logger LOG = LoggerFactory.getLogger(GaiaHealpixTools.class);

	/**
	 * Default constructor
	 * <p>
	 * This constructor follows Gaia's Healpix configuration with Nside = 4096
	 * and NESTED numbering schema
	 * 
	 * @throws Exception
	 */
	public GaiaHealpixTools() throws Exception {
		GAIA_BASE = new HealpixBase(GAIA_NSIDE, GAIA_SCHEME);
	}

	GaiaHealpixTools(long nside, Scheme scheme) throws Exception {
		base = new HealpixBase(nside, scheme);
		order = HealpixBase.nside2order(nside);
		this.nside = base.getNside();
		this.scheme = base.getScheme();
	}

	GaiaHealpixTools(int order, Scheme scheme) throws Exception {
		this(HealpixBase.npix2Nside(HealpixBase.order2Npix(order)), scheme);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public long getNSide() {
		return nside;
	}

	public void setNSide(long nside) {
		this.nside = nside;
	}

	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	/**
	 * Gets the Nside at the resolution
	 * 
	 * @param resInArcSec
	 *            the resolution in arcsec
	 * @return nside
	 */
	public static long getNside(int resInArcSec) {
		return HealpixIndex.calculateNSide(resInArcSec);
	}

	/**
	 * Gets Healpix index
	 * <p>
	 * The input (IAU) coordinates is transferred to HealPix's. The Nside and
	 * Scheme are fixed.
	 * 
	 * @param ra
	 * @param dec
	 * @return the healpix index or -1 if exception is caught.
	 */
	public static long getGaiaHealPix(double ra, double dec) {
		double theta = ((90.0 - dec) / 180.0) * Math.PI;
		double phi = (ra / 360.0) * 2 * Math.PI;
		Pointing pointing = new Pointing(theta, phi);
		try {
			return GaiaHealpixTools.GAIA_BASE.ang2pix(pointing);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets Healpix index
	 * 
	 * @param sourceId
	 * @return the healpix index or -1 if exception is caught.
	 */
	public static long getGaiaHealPix(long sourceId) {
		try {
			return (long) GaiaFactory.getSourceIdUtil().extractHealpix(sourceId);
		} catch (GaiaException e) {
			return -1;
		}
	}

	/**
	 * Gets Healpix index
	 * <p>
	 * The input (IAU) coordinates is transferred to HealPix's
	 * 
	 * @param ra
	 * @param dec
	 * @return the healpix index or -1 if exception is caught.
	 */
	long getHealPix(double ra, double dec) {
		double theta = ((90.0 - dec) / 180.0) * Math.PI;
		double phi = (ra / 360.0) * 2 * Math.PI;
		Pointing pointing = new Pointing(theta, phi);
		try {
			return base.ang2pix(pointing);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Gets the healpix index at higer order.
	 * <p>
	 * This method returns the healpix at the higher order which covers the same
	 * sphere area.
	 * 
	 * @param gaiaHealpix
	 *            the healpix used by Gaia (at order 12)
	 * @param order
	 *            the higher order (must be less than 12)
	 * @return new healpix at the order, or the gaiaHealpix if order <= 12.
	 */
	public static long getParentIndex(long gaiaHealpix, int order) {
		long index;
		int numOfStep = GAIA_ORDER - order;
		if (numOfStep <= 0) {
			LOG.warn("The request resolution is equal to or smaller than that of Gaia, no further action");
			index = gaiaHealpix;
		} else {
			int step = 0;
			long thisPix = gaiaHealpix;
			LOG.debug(String.format("LEVEL:%d, INDEX:%d", GAIA_ORDER - step, thisPix));
			while (step < numOfStep) {
				long upperHealpix = thisPix >> 2;
				thisPix = upperHealpix;
				step++;
				LOG.debug(String.format("LEVEL:%d, INDEX:%d", GAIA_ORDER - step, thisPix));
			}
			index = thisPix;
		}
		return index;
	}

	/**
	 * Gets the collection of Gaia pixels covered by the higher order
	 * 
	 * @param ra
	 * @param dec
	 * @param order
	 *            the higher order (must be less than 12)
	 * @return a collect of Gaia healpixs, or empty list of order <= 12
	 * @throws Exception
	 */
	public static List<Long> getChildIndexs(double ra, double dec, int order) throws Exception {
		List<Long> pixels = new ArrayList<>();
		int numOfStep = GAIA_ORDER - order;
		if (numOfStep <= 0) {
			LOG.warn("The request resolution is equal to or smaller than that of Gaia, no further action");
		} else {
			pixels.add(new GaiaHealpixTools(order, Scheme.NESTED).getHealPix(ra, dec));
			LOG.debug(String.format("LEVEL:%d, INDEX:%d", order, pixels.get(0)));
			for (int step = 0; step < numOfStep; step++) {
				pixels = pixels.stream().flatMap(s -> nextBranch(s).stream()).collect(Collectors.toList());
				LOG.debug(String.format("LEVEL:%d, INDEX:%d", order + step + 1, pixels.get(0)));
			}
		}
		return pixels;
	}

	static List<Long> nextBranch(long pixel) {
		List<Long> temp = new ArrayList<>();
		temp.add(4 * pixel);
		temp.add(4 * pixel + 1);
		temp.add(4 * pixel + 2);
		temp.add(4 * pixel + 3);
		return temp;
	}

	long nest2uniq(int nside, long pnest) {
		int maxPixel = 12 * nside * nside - 1;
		if (pnest <= maxPixel)
			return pnest + 4 * nside * nside;
		else
			return -1;
	}

}
