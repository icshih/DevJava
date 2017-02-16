package gaia.cu9.validation.db;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RunIdGeneratorTest {

	static RunIdGenerator gen;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		gen = new RunIdGenerator();
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
		long start = System.currentTimeMillis();
		String runId = gen.next();
		long end = System.currentTimeMillis();
		long dur = end - start;
		System.out.println(String.format("%s (%d msec)", runId, dur));
	}
	
	@Test
	public void multiTest() {
		assertNotEquals(gen.next(), gen.next());
	}
}
