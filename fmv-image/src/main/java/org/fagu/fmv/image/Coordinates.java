package org.fagu.fmv.image;

/*
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


/**
 * {@link http ://www.koders.com/java/fid1A8CA372AFDE340AB75D59BD3D500CEA1588795E.aspx}
 *
 * @author f.agu
 */
public class Coordinates {

	/**
	 * the latitude of the location. Valid range: [-90.0, 90.0].<br>
	 * Positive values indicate northern latitude and negative values southern latitude.
	 */
	private final double latitude;

	/**
	 * the longitude of the location. Valid range: [-180.0, 180.0].<br>
	 * Positive values indicate eastern longitude and negative values western longitude.
	 */
	private final double longitude;

	/**
	 * @param latitude
	 * @param longitude
	 */
	public Coordinates(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param maximumFractionDigitsSeconds
	 * @return
	 */
	public String getLatitudeDMS(int maximumFractionDigitsSeconds) {
		return toDMS(latitude, maximumFractionDigitsSeconds, 'S', 'N');
	}

	/**
	 * @param maximumFractionDigitsSeconds
	 * @return
	 */
	public String getLongitudeDMS(int maximumFractionDigitsSeconds) {
		return toDMS(longitude, maximumFractionDigitsSeconds, 'W', 'E');
	}

	/**
	 * @param to
	 * @return
	 */
	public float distance(Coordinates to) {
		// Haversine Formula (from R.W. Sinnott, "Virtues of the Haversine", Sky
		// and Telescope, vol. 68, no. 2, 1984, p.
		// 159):
		// See the following URL for more info on calculating distances:
		// http://www.census.gov/cgi-bin/geo/gisfaq?Q5.1
		double earthRadius = 6371; // km,
		// http://en.wikipedia.org/wiki/Earth_radius

		double lat1 = Math.toRadians(to.getLatitude());
		double lon1 = Math.toRadians(to.getLongitude());
		double lat2 = Math.toRadians(getLatitude());
		double lon2 = Math.toRadians(getLongitude());

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = (Math.sin(dlat / 2)) * (Math.sin(dlat / 2)) + (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2)))
				* (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2)));

		double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));
		double km = earthRadius * c;

		return (float)(km * 1000F);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getLatitudeDMS(3) + ',' + getLongitudeDMS(3);
	}

	// **********************************

	/**
	 * @param coord
	 * @param maxFraction
	 * @param negative
	 * @param positive
	 * @return
	 */
	private String toDMS(double inCoord, int maxFraction, char negative, char positive) {
		double coord = inCoord;
		String degrees, minutes, seconds;

		// gets the modulus the coordinate divided by one (MOD1).
		// in other words gets all the numbers after the decimal point.
		// e.g. mod = 87.728056 % 1 == 0.728056
		//
		// next get the integer part of the coord. On other words the whole
		// number part.
		// e.g. intPart = 87
		double mod = coord % 1;

		// set degrees to the value of intPart
		// e.g. degrees = "87"

		degrees = Integer.toString(Math.abs((int)coord));

		// next times the MOD1 of degrees by 60 so we can find the integer part
		// for minutes.
		// get the MOD1 of the new coord to find the numbers after the decimal
		// point.
		// e.g. coord = 0.728056 * 60 == 43.68336
		// mod = 43.68336 % 1 == 0.68336
		//
		// next get the value of the integer part of the coord.
		// e.g. intPart = 43
		coord = mod * 60;
		mod = coord % 1;

		// set minutes to the value of intPart.
		// e.g. minutes = "43"
		minutes = Integer.toString(Math.abs((int)coord));

		// do the same again for minutes
		// e.g. coord = 0.68336 * 60 == 40.0016
		// e.g. intPart = 40
		coord = mod * 60;
		// intPart = (int)Math.round(coord);

		// set seconds to the value of intPart.
		// e.g. seconds = "40"
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(maxFraction);
		DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		seconds = decimalFormat.format(Math.abs(coord));

		// I used this format for android but you can change it
		// to return in whatever format you like
		// e.g. output = "87/1,43/1,40/1"
		// output = degrees + "/1," + minutes + "/1," + seconds + "/1";

		// Standard output of D°M′S″
		return degrees + '�' + minutes + '\'' + seconds + "\" " + (inCoord < 0 ? negative : inCoord > 0 ? positive : "");
	}
}
