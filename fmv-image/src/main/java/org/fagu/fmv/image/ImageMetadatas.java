package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.image.exif.Flash;
import org.fagu.fmv.media.MetadatasContainer;
import org.fagu.fmv.utils.geo.Coordinates;
import org.fagu.fmv.utils.media.Size;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:02:49
 */
public interface ImageMetadatas extends MetadatasContainer {

	String getFormat();

	long getCreateTime();

	OffsetDateTime getDate();

	Size getResolution();

	Size getDimension();

	String getColorSpace();

	Optional<String> getICCProfile();

	Integer getColorDepth();

	Integer getCompressionQuality();

	String getCompression();

	String getResolutionUnit();

	String getDevice();

	String getDeviceModel();

	String getSoftware();

	Integer getISOSpeed();

	Optional<String> getLensModel();

	Optional<Orientation> getOrientation();

	Float getExposureTime();

	default String getExposureTimeFormat() {
		Float exposure = getExposureTime();
		if(exposure == null) {
			return StringUtils.EMPTY;
		}
		float floatValue = exposure.floatValue();
		if(floatValue < 1) {
			return "1/" + Math.round(1F / floatValue);
		}
		return Float.toString(floatValue);
	}

	Float getAperture();

	default String getApertureFormat() {
		Float aperture = getAperture();
		if(aperture == null) {
			return StringUtils.EMPTY;
		}
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = (DecimalFormat)NumberFormat.getInstance();
		format.setDecimalFormatSymbols(symbols);
		format.setMaximumFractionDigits(1);
		format.setMinimumFractionDigits(1);
		return "F/" + format.format(aperture.floatValue());
	}

	Float getFocalLength();

	Flash getFlash();

	Coordinates getCoordinates();

	default Coordinates getCoordinates(LTude latitudeLTude, LTude longitudeLTude) {
		try {
			return LTude.toCoordinates(latitudeLTude, longitudeLTude);
		} catch(Exception ignored) { // ignore
		}
		return null;
	}

	// -------------------------------------

	public static class LTude {

		private final int degrees;

		private final int minutes;

		private final float secondes;

		private final double value;

		private final String ref;

		public LTude(double value, String ref) {
			this.degrees = 0;
			this.minutes = 0;
			this.secondes = 0;
			this.value = value;
			this.ref = ref;
		}

		public LTude(int degrees, int minutes, float secondes, String ref) {
			this.degrees = degrees;
			this.minutes = minutes;
			this.secondes = secondes;
			this.value = toDouble(degrees, minutes, secondes);
			this.ref = ref;
		}

		public static LTude of(Double latitude, String ref) {
			if(latitude == null || ref == null) {
				return null;
			}
			return new LTude(latitude, ref);
		}

		public int getDegrees() {
			return degrees;
		}

		public int getMinutes() {
			return minutes;
		}

		public float getSecondes() {
			return secondes;
		}

		public String getRef() {
			return ref;
		}

		public double getValue() {
			return value;
		}

		public boolean isOver() {
			return value > 90D || value < - 90D;
		}

		public double toDouble() {
			if("S".equalsIgnoreCase(ref) || "W".equalsIgnoreCase(ref)) {
				return - value;
			}
			return value;
		}

		public static Coordinates toCoordinates(LTude latitude, LTude longitude) {
			if(latitude != null && longitude != null && ! (latitude.isOver() || longitude.isOver())) {
				return new Coordinates(latitude.toDouble(), longitude.toDouble());
			}
			return null;
		}

		private static double toDouble(int degrees, int minutes, float secondes) {
			return degrees + minutes / 60D + secondes / 3600D;
		}

	}

}
