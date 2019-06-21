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
import java.io.InputStream;
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
import java.util.NavigableMap;
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
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
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

	public abstract static class ImageMetadatasSourcesBuilder<B extends ImageMetadatasSourcesBuilder<?, T>, T>
			implements MetadatasBuilder<ImageMetadatas, B> {

		final Sources<T> sources;

		Soft identifySoft;

		Consumer<CommandLine> logger;

		Consumer<SoftExecutor> customizeExecutor;

		private ImageMetadatasSourcesBuilder(Sources<T> sources) {
			this.sources = sources;
			identifySoft = Identify.search();
		}

		@Override
		public B soft(Soft identifySoft) {
			this.identifySoft = Objects.requireNonNull(identifySoft);
			return getThis();
		}

		public B logger(Consumer<CommandLine> logger) {
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
			Map<T, ImageMetadatas> extract = ImageMetadatas.extract(identifySoft, sources, logger, customizeExecutor);
			return extract.values().iterator().next();
		}

		// ********************************

		@SuppressWarnings("unchecked")
		private B getThis() {
			return (B)this;
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasFileBuilder extends ImageMetadatasSourcesBuilder<ImageMetadatasFileBuilder, File> {

		private ImageMetadatasFileBuilder(File sourceFile) {
			super(new FilesSources(Collections.singletonList(sourceFile)));
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasFilesBuilder extends ImageMetadatasSourcesBuilder<ImageMetadatasFilesBuilder, File> {

		private ImageMetadatasFilesBuilder(Collection<File> sourceFiles) {
			super(new FilesSources(new ArrayList<>(sourceFiles))); // defensive copy
		}

		public Map<File, ImageMetadatas> extractAll() throws IOException {
			return ImageMetadatas.extract(identifySoft, sources, logger, customizeExecutor);
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasInputStreamBuilder extends ImageMetadatasSourcesBuilder<ImageMetadatasInputStreamBuilder, Object> {

		private ImageMetadatasInputStreamBuilder(InputStream inputStream) {
			super(new InputStreamSources(inputStream));
		}

	}

	// --------------------------------------------------------

	private final long createTime;

	private final NavigableMap<String, String> metadatas;

	protected ImageMetadatas(TreeMap<String, String> metadatas) {
		this.metadatas = Collections.unmodifiableNavigableMap(new TreeMap<>(metadatas));
		createTime = System.currentTimeMillis();
	}

	@Override
	public NavigableSet<String> getNames() {
		return metadatas.navigableKeySet();
	}

	@Override
	public Object get(String propertyName) {
		return metadatas.get(propertyName);
	}

	public String getFirst(String... propertyNames) {
		String value;
		for(String propName : propertyNames) {
			value = metadatas.get(propName);
			if(value != null) {
				return value;
			}
		}
		return null;
	}

	public NavigableMap<String, String> getMetadatas() {
		return metadatas;
	}

	public Date getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		Date date = null;
		// exif
		try {
			date = dateFormat.parse(metadatas.get("exif:datetimeoriginal"));
		} catch(Exception ignored) { // ignore
		}

		// psd
		if(date == null) {
			try {
				String line = metadatas.get("xap:createdate");
				int lastIndex = line.lastIndexOf(':');
				String xapDate = line.substring(0, lastIndex) + line.substring(lastIndex + 1, line.length());
				return dateFormat.parse(xapDate);
			} catch(Exception ignored) { // ignore
			}
		}

		// other
		if(date == null) {
			SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);

			Date dateCreate = null;
			Date dateModify = null;
			try {
				dateCreate = iso8601DateFormat.parse(metadatas.get("date:create"));
			} catch(Exception ignored) {// ignore
			}
			try {
				dateModify = iso8601DateFormat.parse(metadatas.get("date:modify"));
			} catch(Exception ignored) {// ignore
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
		// if(Math.abs(createTime - date.getTime()) < 4000) { // 4s
		// return null;
		// }
		return date;
	}

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

	public String getColorSpace() {
		return metadatas.get("colorspace");
	}

	public int getColorDepth() {
		return NumberUtils.toInt(metadatas.get("cdepth"));
	}

	public int getCompressionQuality() {
		return NumberUtils.toInt(metadatas.get("compressionq"));
	}

	public String getCompression() {
		return metadatas.get("compression");
	}

	public String getResolutionUnit() {
		return metadatas.get("resunit");
	}

	public String getDevice() {
		return metadatas.get("exif:make");
	}

	public String getDeviceModel() {
		return metadatas.get("exif:model");
	}

	public String getSoftware() {
		return metadatas.get("exif:software");
	}

	public Integer getISOSpeed() {
		try {
			return Integer.parseInt(metadatas.get("exif:isospeedratings").split(",")[0]);
		} catch(Exception ignored) { // ignore
		}
		return null;
	}

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

	public Float getAperture() {
		try {
			String[] fNumber = metadatas.get("exif:fnumber").split("/");
			return Float.valueOf(Float.parseFloat(fNumber[0]) / Float.parseFloat(fNumber[1]));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	public String getApertureFormat() {
		Float aperture = getAperture();
		if(aperture == null) {
			return StringUtils.EMPTY;
		}
		return "F/" + aperture.floatValue();
	}

	public Float getFocalLength() {
		try {
			String[] focalLength = metadatas.get("exif:focallength").split("/");
			return Float.valueOf(Float.parseFloat(focalLength[0]) / Float.parseFloat(focalLength[1]));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	public Flash getFlash() {
		try {
			return Flash.valueOf(Integer.parseInt(metadatas.get("exif:flash")));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

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

	@Override
	public String toJSON() {
		return JSONObject.fromObject(metadatas).toString();
	}

	@Override
	public Map<String, Object> getData() {
		return Collections.unmodifiableMap(metadatas);
	}

	@Override
	public String toString() {
		return metadatas.toString();
	}

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

	public static ImageMetadatas extract(InputStream inputStream) throws IOException {
		return with(inputStream).extract();
	}

	public static synchronized ImageMetadatas extractSingleton(final File sourceFile) {
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

	public static ImageMetadatasFileBuilder with(File sourceFile) {
		return new ImageMetadatasFileBuilder(sourceFile);
	}

	public static ImageMetadatasFilesBuilder with(Collection<File> sourceFiles) {
		return new ImageMetadatasFilesBuilder(sourceFiles);
	}

	public static ImageMetadatasInputStreamBuilder with(InputStream inputStream) {
		return new ImageMetadatasInputStreamBuilder(inputStream);
	}

	// *****************************************

	private static <T> Map<T, ImageMetadatas> extract(
			Soft identifySoft,
			Sources<T> sources,
			Consumer<CommandLine> logger,
			Consumer<SoftExecutor> customizeExecutor)
			throws IOException {

		Objects.requireNonNull(identifySoft);
		if( ! sources.has()) {
			return Collections.emptyMap();
		}

		final String boundary = "BOUNDARY";
		// prepare
		IMOperation op = new IMOperation();
		StringJoiner joiner = new StringJoiner("\n", "", "\n");
		joiner.add("==%f==");
		joiner.add("%[exif:*]%[date:*]%[xap:*]%[*]xy=%x %y");
		joiner.add("colorspace=%[colorspace]");
		joiner.add("wh=%w %h");
		joiner.add("cdepth=%z");
		joiner.add("compression=%C");
		joiner.add("compressionq=%Q");
		joiner.add("resunit=%U");
		joiner.add(boundary);

		op.ping().format(joiner.toString());

		sources.addImages(op);

		List<String> outputs = new ArrayList<>();
		SoftExecutor softExecutor = identifySoft.withParameters(op.toList())
				.addOutReadLine(outputs::add)
				.logCommandLine(logger);
		if(customizeExecutor != null) {
			customizeExecutor.accept(softExecutor);
		}
		sources.getSoftExecutor().accept(softExecutor);
		softExecutor.execute();

		// parse output
		Map<T, ImageMetadatas> outMap = new LinkedHashMap<>();
		Iterator<Source<T>> sourceIterator = sources.iterator();
		TreeMap<String, String> params = null;
		Source<T> currentSource = null;
		for(String line : outputs) {
			if(StringUtils.isBlank(line) || boundary.equals(line)) {
				continue;
			}
			if((line.startsWith("==") || line.startsWith(boundary + "==")) && line.endsWith("==")) {
				if(currentSource != null) {
					outMap.put(currentSource.value, new ImageMetadatas(params));
				}
				currentSource = sourceIterator.next();
				if(StringUtils.substringBetween(line, "==").equals(currentSource.name)) {
					params = new TreeMap<>();
					params.put("filename", currentSource.name);
					continue;
				} else {
					params = null;
					currentSource = null;
				}
			}
			if(params == null) {
				continue;
			}
			int index = line.indexOf('=');
			if(index >= 0) {
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				params.put(key.toLowerCase(), value);
			}
		}
		if(currentSource != null) {
			outMap.put(currentSource.value, new ImageMetadatas(params));
		}
		return outMap;
	}

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

	// --------------------------------------------

	private static interface Sources<T> extends Iterable<Source<T>> {

		boolean has();

		void addImages(IMOperation imOperation);

		default Consumer<SoftExecutor> getSoftExecutor() {
			return c -> {};
		}

	}

	// --------------------------------------------

	private static class Source<T> {

		private final T value;

		private final String name;

		private Source(T t, String name) {
			this.value = t;
			this.name = name;
		}

	}

	// --------------------------------------------

	private static class FilesSources implements Sources<File> {

		private final Collection<File> sourceFiles;

		private FilesSources(Collection<File> sourceFiles) {
			this.sourceFiles = Objects.requireNonNull(sourceFiles);
		}

		@Override
		public boolean has() {
			return ! sourceFiles.isEmpty();
		}

		@Override
		public void addImages(IMOperation imOperation) {
			sourceFiles.forEach(file -> imOperation.image(file, "[0]"));
		}

		@Override
		public Iterator<Source<File>> iterator() {
			return sourceFiles.stream()
					.map(f -> new Source<>(f, f.getName()))
					.iterator();
		}

	}

	// --------------------------------------------

	private static class InputStreamSources implements Sources<Object> {

		private final InputStream inputStream;

		private InputStreamSources(InputStream inputStream) {
			this.inputStream = Objects.requireNonNull(inputStream);
		}

		@Override
		public boolean has() {
			return true;
		}

		@Override
		public void addImages(IMOperation imOperation) {
			// - : standard input
			// [0] : first page of the image
			imOperation.image("-[0]");
		}

		@Override
		public Iterator<Source<Object>> iterator() {
			return Stream.of(new Source<>(new Object(), "-")).iterator();
		}

		@Override
		public Consumer<SoftExecutor> getSoftExecutor() {
			return se -> se.input(inputStream);
		}

	}

}
