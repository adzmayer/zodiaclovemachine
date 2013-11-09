package com.writtenbyaliens.zodiaclovemachine;

import com.writtenbyaliens.zodiaclovemachine.UtilityClasses.Constants;
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

	public static int returnZodiacSign(fPoint selectedSign) {

		int sign = 0;
		fPoint a;
		fPoint b;
		fPoint c;

		// TODO Create a check for each star sign. Return when one is found
		// Test check for gemini
		a = new fPoint(200, 613);
		b = new fPoint(270, 610);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.GEMINI;
		}

		// Cancer
		a = new fPoint(320, 590);
		b = new fPoint(370, 566);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.CANCER;
		}

		// Leo
		a = new fPoint(410, 548);
		b = new fPoint(434, 476);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.LEO;
		}

		// Virgo
		a = new fPoint(442, 418);
		b = new fPoint(452, 362);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.VIRGO;
		}

		// Libra
		a = new fPoint(432, 314);
		b = new fPoint(402, 266);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.LIBRA;
		}

		// Scorpio
		a = new fPoint(388, 235);
		b = new fPoint(322, 200);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.SCORPIO;
		}

		// Sagittarius
		a = new fPoint(280, 175);
		b = new fPoint(225, 180);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.SAGITTARIUS;
		}

		// Capricorn
		a = new fPoint(118, 216);
		b = new fPoint(166, 194);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.CAPRICORN;
		}

		// Aquarius
		a = new fPoint(48, 318);
		b = new fPoint(74, 262);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.AQUARIUS;
		}

		// Pisces
		a = new fPoint(30, 416);
		b = new fPoint(36, 364);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.PISCES;
		}

		// Aries
		a = new fPoint(86, 528);
		b = new fPoint(56, 478);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.ARIES;
		}

		// Taurus
		a = new fPoint(108, 554);
		b = new fPoint(160, 600);
		c = new fPoint(220, 400);
		if (pointInTriangle(selectedSign, a, b, c)) {
			return Constants.ZodiacSigns.TAURUS;
		}

		return sign;
	}

}
