package gaia.cu9.validation.db;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RunIdGenerator {

	SecureRandom random = new SecureRandom();

	public String next() {
		return new BigInteger(32, random).toString(16).toUpperCase();
	}
}