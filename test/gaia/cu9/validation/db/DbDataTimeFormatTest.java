package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gaia.cu9.validation.common.ValidationException;

public class DbDataTimeFormatTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void test() {
		LocalDateTime now = getDateTimeNow();
		assertEquals(now.toLocalDate().toString(), getDate(now));
		assertEquals(now.toLocalTime().toString(), getTime(now));
	}

	@Test
	public void durationTest() throws ValidationException {
		assertEquals(86400, getDuration("2017-02-02", "13:16:01", "2017-02-03", "13:16:01"));
	}

	/**
	 * Get duration in second between start and end data-time.
	 * 
	 * The data-time format must be one of the ISO 8601, see
	 * {@link https://en.wikipedia.org/wiki/ISO_8601}.
	 * 
	 * @param startDate
	 *            in ISO date format
	 * @param startTime
	 *            in ISO time format
	 * @param endDate
	 *            in ISO date format
	 * @param endTime
	 *            in ISO time format
	 * @return duration in second
	 * @throws ValidationException
	 *             if the return value is negative.
	 */
	static long getDuration(String startDate, String startTime, String endDate, String endTime)
			throws ValidationException {
		long duration = Duration.between(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.parse(startTime)),
				LocalDateTime.of(LocalDate.parse(endDate), LocalTime.parse(endTime))).getSeconds();
		if (duration >= 0)
			return duration;
		else
			throw new ValidationException("The duration is negative, please check input parameters.");
	}

	static LocalDateTime getDateTimeNow() {
		return LocalDateTime.now();
	}

	static String getDate(LocalDateTime now) {
		return now.toLocalDate().toString();
	}

	static String getTime(LocalDateTime now) {
		return now.toLocalTime().toString();
	}
}