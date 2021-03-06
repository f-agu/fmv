package org.fagu.fmv.soft.mediainfo;

/*-
 * #%L
 * fmv-soft-auto
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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.Orientation;
import org.fagu.fmv.image.exif.Flash;
import org.fagu.fmv.utils.geo.Coordinates;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 * @created 24 août 2020 16:55:52
 */
public class MediaInfoImageMetadatas implements ImageMetadatas {

	private final Info info;

	public MediaInfoImageMetadatas(Info info) {
		this.info = Objects.requireNonNull(info);
	}

	@Override
	public Map<String, Object> getData() {
		return info.getData();
	}

	@Override
	public String getFormat() {
		return info.getFirstGeneral()
				.flatMap(g -> g.getFirstString("Format"))
				.orElse(null);
	}

	@Override
	public long getCreateTime() {
		return info.getFirstGeneral()
				.flatMap(g -> g.getFirstString("File_Created_Date_Local"))
				.map(s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
				.map(ldt -> ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.orElse(0L);
	}

	@Override
	public OffsetDateTime getDate() {
		LocalDateTime localDateTime = LocalDateTime.now();
		ZoneOffset zoneOffSet = ZoneId.systemDefault().getRules().getOffset(localDateTime);
		return info.getFirstGeneral()
				.flatMap(g -> g.getFirstString("File_Created_Date_Local"))
				.map(s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
				.map(ldt -> ldt.atOffset(zoneOffSet))
				.orElse(null);
	}

	@Override
	public Size getResolution() {
		return null;
	}

	@Override
	public Size getDimension() {
		ImageInfo imageInfo = info.getFirstImage().orElse(null);
		if(imageInfo == null) {
			return null;
		}
		Optional<Integer> width = imageInfo.getWidth();
		Optional<Integer> height = imageInfo.getHeight();
		if(width.isPresent() && height.isPresent()) {
			return Size.valueOf(width.get(), height.get());
		}
		return null;
	}

	@Override
	public String getColorSpace() {
		return info.getFirstImage()
				.flatMap(i -> i.getFirstString("ColorSpace"))
				.orElse(null);
	}

	@Override
	public Optional<String> getICCProfile() {
		return info.getFirstImage()
				.flatMap(i -> i.getFirstString("colour_primaries"));
	}

	@Override
	public Integer getColorDepth() {
		return info.getFirstImage()
				.flatMap(i -> i.getFirstInteger("BitDepth"))
				.orElse(null);
	}

	@Override
	public Integer getCompressionQuality() {
		return null;
	}

	@Override
	public String getCompression() {
		String value = info.getFirstImage()
				.flatMap(i -> i.getFirstString("Compression_Mode"))
				.orElse(null);
		if("undefined".equalsIgnoreCase(value)) {
			return null;
		}
		return value;
	}

	@Override
	public String getResolutionUnit() {
		return null;
	}

	@Override
	public String getDevice() {
		return null;
	}

	@Override
	public String getDeviceModel() {
		return null;
	}

	@Override
	public String getSoftware() {
		return null;
	}

	@Override
	public Integer getISOSpeed() {
		return null;
	}

	@Override
	public Optional<Orientation> getOrientation() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getLensModel() {
		return Optional.empty();
	}

	@Override
	public Float getExposureTime() {
		return null;
	}

	@Override
	public Float getAperture() {
		return null;
	}

	@Override
	public Float getFocalLength() {
		return null;
	}

	@Override
	public Flash getFlash() {
		return null;
	}

	@Override
	public Coordinates getCoordinates() {
		return null;
	}

}
