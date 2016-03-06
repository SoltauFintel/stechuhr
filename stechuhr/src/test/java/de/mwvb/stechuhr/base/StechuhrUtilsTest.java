package de.mwvb.stechuhr.base;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class StechuhrUtilsTest {

	@Test
	public void testZweistellig_0() {
		Assert.assertEquals("00", StechuhrUtils.zweistellig(0));
	}

	@Test
	public void testZweistellig_1() {
		Assert.assertEquals("01", StechuhrUtils.zweistellig(1));
	}

	@Test
	public void testZweistellig_10() {
		Assert.assertEquals("10", StechuhrUtils.zweistellig(10));
	}

	@Test
	public void testZweistellig_100() {
		Assert.assertEquals("100", StechuhrUtils.zweistellig(100));
	}
	
	@Test
	public void testFormatDate_1() {
		Assert.assertEquals("31.12.2016", StechuhrUtils.formatDate(LocalDate.of(2016, 12, 31)));
	}
	
	@Test
	public void testFormatDate_2() {
		Assert.assertEquals("01.01.2017", StechuhrUtils.formatDate(LocalDate.of(2017, 1, 1)));
	}
	
	@Test
	public void testFormatWTDate() {
		Assert.assertEquals("Donnerstag 03.03.2016", StechuhrUtils.formatWTDate(LocalDate.of(2016, 3, 3)));
	}
	
	@Test
	public void testGetDezimalstunden() {
		Assert.assertEquals("1,25", StechuhrUtils.getDezimalstunden(1, 15));
	}
	
	@Test
	public void testGetExceptionText() {
		String text = StechuhrUtils.getExceptionText(new NullPointerException());
		Assert.assertNotNull(text);
		if (!text.startsWith("java.lang.NullPointerException\r\n\tat de.mwvb.stechuhr.") // Windows
				&& !text.startsWith("java.lang.NullPointerException\n\tat de.mwvb.stechuhr.")) { // Non-Windows (z.B. Travis CI)
			System.out.println("Ausgabe von getExceptionText(): " + text.replace("\r", "[CR]").replace("\n", "[LF]\n"));
		}
	}
	
	@Test
	public void testNurZiffern() {
		Assert.assertTrue(StechuhrUtils.nurZiffern("123"));
		Assert.assertTrue(StechuhrUtils.nurZiffern("01"));
		Assert.assertTrue(StechuhrUtils.nurZiffern("1"));
		Assert.assertTrue(StechuhrUtils.nurZiffern("1234567890"));
		
		Assert.assertFalse(StechuhrUtils.nurZiffern(null));
		Assert.assertFalse(StechuhrUtils.nurZiffern(""));
		Assert.assertFalse(StechuhrUtils.nurZiffern(" "));
		Assert.assertFalse(StechuhrUtils.nurZiffern("A"));
		Assert.assertFalse(StechuhrUtils.nurZiffern("1.4"));
		Assert.assertFalse(StechuhrUtils.nurZiffern("-6"));
	}
}
