package org.rsta.swimreston.shared;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class Utilities {

	private static final BigDecimal SIXTY = new BigDecimal(60);
	private static final String DECIMAL = ".";
	private static final String COLON = ":";
	private static Map<Integer, String> strokeMap = new HashMap<Integer, String>(
			5);

	/**
	 * Static initialization block to map the magic numbers to the actual stroke
	 * names
	 */
	static {
		strokeMap.put(1, "Free");
		strokeMap.put(2, "Back");
		strokeMap.put(3, "Breast");
		strokeMap.put(4, "Fly");
		strokeMap.put(5, "Medley");
	}

	/**
	 * Utility method for taking the time (stored as an integer in TeamManager
	 * database) and converting it to a string of format minute:decimal seconds
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDuration(Integer time) {
		// Variable to hold the value to return from this method
		String formattedDuration = "0.00";
		String strTime = null;
		int length = 0;

		if (time != null) {
			strTime = time.toString();
			length = strTime.length();
		}

		switch (length) {
		case 0:
			// leave formattedDuration set "0.00"
			break;
		case 1:
			formattedDuration = "0.0" + time.toString();
			break;
		case 2:
			formattedDuration = "0." + time.toString();
			break;
		default:
			int tenthsPlace = length - 2;
			String strDecimalSeconds = strTime.substring(tenthsPlace, length);
			String strWholeSeconds = strTime.substring(0, tenthsPlace);
			BigDecimal wholeSeconds = new BigDecimal(strWholeSeconds);
			BigDecimal[] minutesAndSeconds = wholeSeconds
					.divideAndRemainder(SIXTY);
			BigDecimal minutes = minutesAndSeconds[0];
			wholeSeconds = minutesAndSeconds[1];
			strWholeSeconds = wholeSeconds.toString();
			// Pad seconds with a leading zero if necessary
			if (strWholeSeconds.length() == 1) {
				strWholeSeconds = "0" + strWholeSeconds;
			}
			String seconds = strWholeSeconds + DECIMAL + strDecimalSeconds;
			if (minutes.compareTo(BigDecimal.ZERO) == 1) {
				seconds = minutes.toString() + COLON + seconds;
			}
			formattedDuration = seconds;
			break;
		}

		return formattedDuration;
	}

	public static String formatStroke(Integer strokeNumber) {
		return strokeMap.get(strokeNumber);
	}

	public static String formatEventName(Integer distance, Integer stroke,
			Boolean isRelay) {

		String strokeName = formatStroke(stroke);

		// If this is Medley and not relay, prepend Individual
		if (stroke == 5 && !isRelay) {
			strokeName = "Individual " + strokeName;
		}

		// If a relay, append that to event
		if (isRelay) {
			strokeName += " Relay";
		}
		return distance.toString() + " " + strokeName;
	}
}
