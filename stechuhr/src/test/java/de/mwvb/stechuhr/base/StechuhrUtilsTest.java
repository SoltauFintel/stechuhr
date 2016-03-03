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
}
