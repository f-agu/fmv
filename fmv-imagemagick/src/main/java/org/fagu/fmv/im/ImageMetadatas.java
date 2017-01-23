package org.fagu.fmv.im;

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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.Soft.SoftExecutor;
import org.fagu.fmv.utils.media.Size;

import net.sf.json.JSONObject;


/**
 * <pre>
 * identify -ping -format "wh:%w %h\nModel:%[EXIF:Model]\nDateTimeOriginal:%[EXIF:DateTimeOriginal]\ncolorspace:%[colorspace]\nFocalLength:%[EXIF:FocalLength]\nISO:%[EXIF:ISOSpeedRatings]\nEXIFImageWH:%[EXIF:EXIFImageWidth] %[EXIF:EXIFImageLength]\nCreateDate:%[xap:CreateDate]\nGPSLon:%[EXIF:GPSLongitude]\nxy:%x %y\nGPSLonRef:%[EXIF:GPSLongitudeRef]\nGPSLat:%[EXIF:GPSLatitude]\nFlash:%[EXIF:Flash]\nExposureTime:%[EXIF:ExposureTime]\nFNumber:%[EXIF:FNumber]\nGPSLatRef:%[EXIF:GPSLatitudeRef]\nMake:%[EXIF:Make]" C:\Users\f.agu\Downloads\012.jpg
 * </pre>
 * 
 * <pre>
 * identify -ping -format "%[exif:*]%[date:*]%[xap:*]xy=%x %y\ncolorspace=%[colorspace]\nwh=%w %h" 0.jpg
 * </pre>
 * 
 * 
 * @author f.agu
 */
public class ImageMetadatas implements Metadatas, MetadataProperties, Serializable {

	private static final long serialVersionUID = - 3899723797675922936L;

	private static final Hashtable<File, Future<ImageMetadatas>> FUTURE_HASHTABLE = new Hashtable<>();

	// --------------------------------------------------------

	public abstract static class ImageMetadatasBuilder<B extends ImageMetadatasBuilder<?>> implements MetadatasBuilder<ImageMetadatas, B> {

		final Collection<File> sourceFiles;

		Soft identifySoft;

		Consumer<String> logger;

		Consumer<SoftExecutor> customizeExecutor;

		private ImageMetadatasBuilder(Collection<File> sourceFiles) {
			this.sourceFiles = sourceFiles;
			identifySoft = Identify.search();
		}

		@Override
		public B soft(Soft identifySoft) {
			this.identifySoft = Objects.requireNonNull(identifySoft);
			return getThis();
		}

		public B logger(Consumer<String> logger) {
			this.logger = logger;
			return getThis();
		}

		@Override
		public B customizeExecutor(Consumer<SoftExecutor> customizeExecutor) {
			this.customizeExecutor = customizeExecutor;
			return getThis();
		}

		@Override
		public ImageMetadatas extract() throws IOException {
			Map<File, ImageMetadatas> extract = ImageMetadatas.extract(identifySoft, sourceFiles, logger, customizeExecutor);
			return extract.get(sourceFiles.iterator().next());
		}

		// ********************************

		@SuppressWarnings("unchecked")
		private B getThis() {
			return (B)this;
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasFileBuilder extends ImageMetadatasBuilder<ImageMetadatasFilesBuilder> {

		private ImageMetadatasFileBuilder(File sourceFile) {
			super(Collections.singletonList(sourceFile));
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasFilesBuilder extends ImageMetadatasBuilder<ImageMetadatasFilesBuilder> {

		private ImageMetadatasFilesBuilder(Collection<File> sourceFiles) {
			super(new ArrayList<>(sourceFiles)); // defensive copy
		}

		public Map<File, ImageMetadatas> extractAll() throws IOException {
			return ImageMetadatas.extract(identifySoft, sourceFiles, logger, customizeExecutor);
		}

	}

	// --------------------------------------------------------

	private final long createTime;

	private final TreeMap<String, String> metadatas;

	/**
	 * @param metadatas
	 */
	protected ImageMetadatas(TreeMap<String, String> metadatas) {
		this.metadatas = metadatas;
		createTime = System.currentTimeMillis();
	}

	/**
	 * @see org.fagu.fmv.media.MetadataProperties#getNames()
	 */
	@Override
	public NavigableSet<String> getNames() {
		return metadatas.navigableKeySet();
	}

	/**
	 * @see org.fagu.fmv.media.MetadataProperties#get(java.lang.String)
	 */
	@Override
	public String get(String propertyName) {
		return metadatas.get(propertyName);
	}

	/**
	 * @return
	 */
	public Date getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		Date date = null;
		// exif
		try {
			date = dateFormat.parse(metadatas.get("exif:datetimeoriginal"));
		} catch(Exception ignored) {}

		// psd
		if(date == null) {
			try {
				String line = metadatas.get("xap:createdate");
				int lastIndex = line.lastIndexOf(':');
				String xapDate = line.substring(0, lastIndex) + line.substring(lastIndex + 1, line.length());
				return dateFormat.parse(xapDate);
			} catch(Exception ignored) {}
		}

		// other
		if(date == null) {
			SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);

			Date dateCreate = null;
			Date dateModify = null;
			try {
				dateCreate = iso8601DateFormat.parse(metadatas.get("date:create"));
			} catch(Exception ignored) {
				// ignore
			}
			try {
				dateModify = iso8601DateFormat.parse(metadatas.get("date:modify"));
			} catch(Exception ignored) {
				// ignore
			}

			// work around bug ImageMagick
			if(dateCreate != null && dateModify != null) {
				int compare = dateCreate.compareTo(dateModify);
				date = compare < 0 ? dateCreate : dateModify;
			} else if(dateCreate != null) {
				date = dateCreate;
			} else if(dateModify != null) {
				date = dateModify;
			}
		}
		if(Math.abs(createTime - date.getTime()) < 4000) { // 4s
			return null;
		}
		return date;
	}

	/**
	 * @return
	 */
	public Size getResolution() {
		try {
			String resolution = metadatas.get("xy");
			if(resolution.length() > 0) {
				String[] resolutionTab = resolution.split(" ");
				if(resolutionTab.length == 4) {
					return Size.valueOf(Integer.parseInt(resolutionTab[0]), Integer.parseInt(resolutionTab[2]));
				} else if(resolutionTab.length >= 2) {
					return Size.valueOf(Integer.parseInt(resolutionTab[0]), Integer.parseInt(resolutionTab[1]));
				}
			}
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public Size getDimension() {
		try {
			String dimensions = metadatas.get("wh");
			if(StringUtils.isBlank(dimensions)) {
				dimensions = metadatas.get("exif:exifimagewidth") + ' ' + metadatas.get("exif:exifimagelength");
			}
			if(dimensions.length() > 0) {
				String[] dimensionsTab = dimensions.split(" ");
				if(dimensionsTab.length >= 2) {
					return Size.valueOf(Integer.parseInt(dimensionsTab[0]), Integer.parseInt(dimensionsTab[1]));
				}
			}
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getColorSpace() {
		return metadatas.get("colorspace");
	}

	/**
	 * @return
	 */
	public int getColorDepth() {
		return NumberUtils.toInt(metadatas.get("cdepth"));
	}

	/**
	 * @return
	 */
	public int getCompressionQuality() {
		return NumberUtils.toInt(metadatas.get("compressionq"));
	}

	/**
	 * @return
	 */
	public String getCompression() {
		return metadatas.get("compression");
	}

	/**
	 * @return
	 */
	public String getResolutionUnit() {
		return metadatas.get("resunit");
	}

	/**
	 * @return
	 */
	public String getDevice() {
		return metadatas.get("exif:make");
	}

	/**
	 * @return
	 */
	public String getDeviceModel() {
		return metadatas.get("exif:model");
	}

	/**
	 * @return
	 */
	public String getSoftware() {
		return metadatas.get("exif:software");
	}

	/**
	 * @return
	 */
	public Integer getISOSpeed() {
		try {
			return Integer.parseInt(metadatas.get("exif:isospeedratings").split(",")[0]);
		} catch(Exception ignored) {}
		return null;
	}

	/**
	 * @return
	 */
	public Float getExposureTime() {
		try {
			String etime = metadatas.get("exif:exposuretime");
			if(etime.contains("/")) {
				String[] exposure = etime.split("/");
				return Float.valueOf(Float.parseFloat(exposure[0]) / Float.parseFloat(exposure[1]));
			}
			return Float.parseFloat(etime);
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getExposureTimeFormat() {
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

	/**
	 * @return
	 */
	public Float getAperture() {
		try {
			String[] fNumber = metadatas.get("exif:fnumber").split("/");
			return Float.valueOf(Float.parseFloat(fNumber[0]) / Float.parseFloat(fNumber[1]));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getApertureFormat() {
		Float aperture = getAperture();
		if(aperture == null) {
			return StringUtils.EMPTY;
		}
		return "F/" + aperture.floatValue();
	}

	/**
	 * @return
	 */
	public Float getFocalLength() {
		try {
			String[] focalLength = metadatas.get("exif:focallength").split("/");
			return Float.valueOf(Float.parseFloat(focalLength[0]) / Float.parseFloat(focalLength[1]));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public Flash getFlash() {
		try {
			return Flash.valueOf(Integer.parseInt(metadatas.get("exif:flash")));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @return
	 */
	public Coordinates getCoordinates() {
		try {
			Double latitude = parseCoordinate(metadatas.get("exif:gpslatitude"));
			String latitudeRef = metadatas.get("exif:gpslatituderef");
			if(latitude != null && ! "N".equalsIgnoreCase(latitudeRef)) {
				latitude = - latitude;
			}
			Double longitude = parseCoordinate(metadatas.get("exif:gpslongitude"));
			String longitudeRef = metadatas.get("exif:gpslongituderef");
			if(longitude != null && ! "E".equalsIgnoreCase(longitudeRef)) {
				longitude = - longitude;
			}
			if(latitude != null && longitude != null && ! (latitude > 90 || latitude < - 90 || longitude > 90 || longitude < - 90)) {
				return new Coordinates(latitude, longitude);
			}
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	/**
	 * @see org.fagu.fmv.media.Metadatas#toJSON()
	 */
	@Override
	public String toJSON() {
		return JSONObject.fromObject(metadatas).toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return metadatas.toString();
	}

	/**
	 * @return
	 */
	public static ImageMetadatas parseJSON(String json) {
		TreeMap<String, String> params = new TreeMap<>();
		JSONObject jsonObject = JSONObject.fromObject(json);
		Iterator<?> keys = jsonObject.keys();
		while(keys.hasNext()) {
			String key = (String)keys.next();
			params.put(key, jsonObject.getString(key));
		}
		return new ImageMetadatas(params);
	}

	/**
	 * @param sourceFile
	 * @return
	 * @throws IOException
	 */
	public static synchronized ImageMetadatas extractSingleton(final File sourceFile) throws IOException {
		Future<ImageMetadatas> future = FUTURE_HASHTABLE.get(sourceFile);
		if(future != null) {
			try {
				return future.get();
			} catch(InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		ExecutorService service = Executors.newSingleThreadExecutor();
		future = service.submit(with(sourceFile)::extract);
		FUTURE_HASHTABLE.put(sourceFile, future);
		service.shutdown();
		try {
			boolean terminated = service.awaitTermination(2, TimeUnit.MINUTES);
			if( ! terminated) {
				service.shutdownNow();
			}
			// throws the exception if one occurred during the invocation
			return future.get(2, TimeUnit.MINUTES);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param sourceFile
	 * @return
	 */
	public static ImageMetadatasFileBuilder with(File sourceFile) {
		return new ImageMetadatasFileBuilder(sourceFile);
	}

	/**
	 * @param sourceFiles
	 * @return
	 */
	public static ImageMetadatasFilesBuilder with(Collection<File> sourceFiles) {
		return new ImageMetadatasFilesBuilder(sourceFiles);
	}

	// *****************************************

	/**
	 * @param identifySoft
	 * @param sourceFiles
	 * @param logger
	 * @param customizeExecutor
	 * @return
	 * @throws IOException
	 */
	private static Map<File, ImageMetadatas> extract(Soft identifySoft, Collection<File> sourceFiles, Consumer<String> logger,
			Consumer<SoftExecutor> customizeExecutor) throws IOException {
		Objects.requireNonNull(identifySoft);
		if(sourceFiles.isEmpty()) {
			return Collections.emptyMap();
		}

		final String boundary = "BOUNDARY";
		// prepare
		IMOperation op = new IMOperation();
		StringJoiner joiner = new StringJoiner("\n");
		joiner.add("==%f==");
		joiner.add("%[exif:*]%[date:*]%[xap:*]xy=%x %y");
		joiner.add("colorspace=%[colorspace]");
		joiner.add("wh=%w %h");
		joiner.add("cdepth=%z");
		joiner.add("compression=%C");
		joiner.add("compressionq=%Q");
		joiner.add("resunit=%U");
		joiner.add(boundary);

		op.ping().format(joiner.toString() + "\n");

		int size = sourceFiles.size();
		sourceFiles.forEach(file -> op.image(file, "[0]"));

		List<String> outputs = new ArrayList<>();
		SoftExecutor softExecutor = identifySoft.withParameters(op.toList()) //
				.addOutReadLine(outputs::add) //
				.logCommandLine(logger);
		if(customizeExecutor != null) {
			customizeExecutor.accept(softExecutor);
		}
		softExecutor.execute();

		// parse output
		Map<File, ImageMetadatas> outMap = new LinkedHashMap<>(size);
		Iterator<File> srcFileIterator = sourceFiles.iterator();
		TreeMap<String, String> params = null;
		File currentFile = null;
		for(String line : outputs) {
			if(StringUtils.isBlank(line) || boundary.equals(line)) {
				continue;
			}
			if((line.startsWith("==") || line.startsWith(boundary + "==")) && line.endsWith("==")) {
				if(currentFile != null) {
					outMap.put(currentFile, new ImageMetadatas(params));
				}
				currentFile = srcFileIterator.next();
				if(StringUtils.substringBetween(line, "==").equals(currentFile.getName())) {
					params = new TreeMap<>();
					params.put("filename", currentFile.getName());
					continue;
				} else {
					params = null;
					currentFile = null;
				}
			}
			if(params == null) {
				continue;
			}
			int index = line.indexOf('=');
			String key = line.substring(0, index);
			String value = line.substring(index + 1);
			params.put(key.toLowerCase(), value);
		}
		if(currentFile != null) {
			outMap.put(currentFile, new ImageMetadatas(params));
		}
		return outMap;
	}

	/**
	 * @param value
	 * @return
	 */
	private Double parseCoordinate(String value) {
		if(value != null && value.length() > 0) {
			String[] valueTab = value.split(",");
			if(valueTab.length == 3) {
				double result = 0.0d;
				double q = 1;
				for(int i = 0; i < 3; i++) {
					String[] tab = valueTab[i].trim().split("/");
					if(i > 0) {
						q *= 60;
					}
					result += (Double.parseDouble(tab[0]) / Double.parseDouble(tab[1])) / q;
				}
				return result;
			}
		}
		return null;
	}

}
