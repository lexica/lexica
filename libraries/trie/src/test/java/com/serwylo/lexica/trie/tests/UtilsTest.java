package com.serwylo.lexica.trie.tests;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testLetterMask() {
		int actualMaskAbc = Utils.calcLetterMask("abc");
		int actualMaskCde = Utils.calcLetterMask("CdE");
		int actualMaskAllLower = Utils.calcLetterMask("abcdefghijklmnopqrstuvwxyz");
		int actualMaskAllUpper = Utils.calcLetterMask("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		int actualMaskAllMixed = Utils.calcLetterMask("aBcDeFgHiJkLmNoPqRsTuVwXyZ");

		// TODO: Does "Qu" need any special treatment here? I suspect not.

		int expectedMaskAbc = (1 << 0) | (1 << 1) | (1 << 2);
		int expectedMaskCde = (1 << 2) | (1 << 3) | (1 << 4);
		int expectedMaskAll =
				(1 << 0) | (1 << 1) | (1 << 2) | (1 << 3) | (1 << 4) |
				(1 << 5) | (1 << 6) | (1 << 7) | (1 << 8) | (1 << 9) |
				(1 << 10) | (1 << 11) | (1 << 12) | (1 << 13) | (1 << 14) |
				(1 << 15) | (1 << 16) | (1 << 17) | (1 << 18) | (1 << 19) |
				(1 << 20) | (1 << 21) | (1 << 22) | (1 << 23) | (1 << 24) |
				(1 << 25);

		Assert.assertEquals(expectedMaskAbc, actualMaskAbc);
		Assert.assertEquals(expectedMaskCde, actualMaskCde);
		Assert.assertEquals(expectedMaskAll, actualMaskAllLower);
		Assert.assertEquals(expectedMaskAll, actualMaskAllUpper);
		Assert.assertEquals(expectedMaskAll, actualMaskAllMixed);
	}

}
