package com.writtenbyaliens.zodiaclovemachine;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.fPoint;

public final class Utils {

	private static float sign(fPoint p1, fPoint p2, fPoint p3) {
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
	}

	private static boolean pointInTriangle(fPoint pt, fPoint v1, fPoint v2,
			fPoint v3) {
		boolean b1, b2, b3;

		b1 = sign(pt, v1, v2) < 0.0f;
		b2 = sign(pt, v2, v3) < 0.0f;
		b3 = sign(pt, v3, v1) < 0.0f;

		return ((b1 == b2) && (b2 == b3));
	}

	public static String returnZodiacSign(fPoint selectedSign) {

		String sign = "";
		fPoint a;
		fPoint b;
		fPoint c;

		// TODO Create a check for each star sign. Return when one is found
		// Test check for gemini
		a = new fPoint(200, 613);
		b = new fPoint(270, 610);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return sign;
		}

		return sign;
	}

}
