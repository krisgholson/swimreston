package org.rsta.swimreston;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rsta.swimreston.shared.Utilities;

public class UtilitiesTest {

	@Test
	public void testFormatDurationOverTwoMinutes() {
		assertEquals("2:19.63", Utilities.formatDuration(13963));
	}

	@Test
	public void testFormatDurationOverOneMinute() {
		assertEquals("1:40.80", Utilities.formatDuration(10080));
	}

	@Test
	public void testFormatDurationUnderOneMinute() {
		assertEquals("32.40", Utilities.formatDuration(3240));
	}

	@Test
	public void testFormatDurationAtHundredthsPlace() {
		assertEquals("0.05", Utilities.formatDuration(5));
	}

	@Test
	public void testFormatDurationAtTenthsPlace() {
		assertEquals("0.50", Utilities.formatDuration(50));
	}

	@Test
	public void testFormatDurationWithNull() {
		assertEquals("0.00", Utilities.formatDuration(null));
	}

}
