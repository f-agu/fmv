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
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.MapImageMetadatas;
import org.fagu.fmv.image.exif.Flash;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.utils.geo.Coordinates;
import org.fagu.fmv.utils.media.Size;

import com.google.gson.Gson;


/**
 * <pre>
 * identify -ping -format "wh:%w %h\nModel:%[EXIF:Model]\nDateTimeOriginal:%[EXIF:DateTimeOriginal]\ncolorspace:%[colorspace]\nFocalLength:%[EXIF:FocalLength]\nISO:%[EXIF:ISOSpeedRatings]\nEXIFImageWH:%[EXIF:EXIFImageWidth] %[EXIF:EXIFImageLength]\nCreateDate:%[xap:CreateDate]\nGPSLon:%[EXIF:GPSLongitude]\nxy:%x %y\nGPSLonRef:%[EXIF:GPSLongitudeRef]\nGPSLat:%[EXIF:GPSLatitude]\nFlash:%[EXIF:Flash]\nExposureTime:%[EXIF:ExposureTime]\nFNumber:%[EXIF:FNumber]\nGPSLatRef:%[EXIF:GPSLatitudeRef]\nMake:%[EXIF:Make]" C:\Users\f.agu\Downloads\012.jpg
 * </pre>
 * 
 * <pre>
 * identify -ping -format "%[exif:*]%[date:*]%[xap:*]xy=%x %y\ncolorspace=%[colorspace]\nwh=%w %h" 0.jpg
 * </pre>
 * 
 * <pre>
 * magick identify -ping -format "==%f==\nformat=%m\n%[exif:*]%[date:*]%[xap:*]%[icc:*]%[*]xy=%x %y\ncolorspace=%[colorspace]\nwh=%w %h\ncdepth=%z\ncompression=%C\ncompressionq=%Q\nresunit=%U\n" "C:\Personnel\Pictures\Phone3\08.jpg"
 * </pre>
 * 
 * @author f.agu
 */
public class IMIdentifyImageMetadatas extends MapImageMetadatas implements Serializable {

	private static final long serialVersionUID = - 3899723797675922936L;

	private static final Hashtable<File, Future<IMIdentifyImageMetadatas>> FUTURE_HASHTABLE = new Hashtable<>();

	// --------------------------------------------------------

	public abstract static class ImageMetadatasSourcesBuilder<B extends ImageMetadatasSourcesBuilder<?, T>, T>
			implements MetadatasBuilder<IMIdentifyImageMetadatas, B> {

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
		public IMIdentifyImageMetadatas extract() throws IOException {
			Map<T, IMIdentifyImageMetadatas> extract = IMIdentifyImageMetadatas.extract(identifySoft, sources, logger, customizeExecutor);
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

		public Map<File, IMIdentifyImageMetadatas> extractAll() throws IOException {
			return IMIdentifyImageMetadatas.extract(identifySoft, sources, logger, customizeExecutor);
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasInputStreamBuilder extends ImageMetadatasSourcesBuilder<ImageMetadatasInputStreamBuilder, Object> {

		private ImageMetadatasInputStreamBuilder(InputStream inputStream) {
			super(new InputStreamSources(inputStream));
		}

	}

	// --------------------------------------------------------

	protected IMIdentifyImageMetadatas(Map<String, Object> metadatas) {
		super(metadatas);
	}

	@Override
	public String getFormat() {
		return getString("format");
	}

	@Override
	public OffsetDateTime getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		// exif
		OffsetDateTime date = Stream.of("exif:datetime", "exif:datetimeoriginal")
				.map(prop -> {
					try {
						Date parse = dateFormat.parse(getString(prop));
						Instant instant = parse.toInstant();
						ZoneOffset offset = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()).getOffset();
						return instant.atOffset(offset);
					} catch(Exception ignored) { // ignore
						return null;
					}
				})
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
		if(date != null) {
			return date;
		}
		// psd
		try {
			String line = getString("xap:createdate");
			int lastIndex = line.lastIndexOf(':');
			String xapDate = line.substring(0, lastIndex) + line.substring(lastIndex + 1, line.length());
			Date parse = dateFormat.parse(xapDate);
			return parse.toInstant().atOffset(OffsetDateTime.now().getOffset());
		} catch(Exception ignored) { // ignore
		}

		// other
		OffsetDateTime dateCreate = null;
		OffsetDateTime dateModify = null;
		try {
			dateCreate = OffsetDateTime.parse(getString("date:create"));
		} catch(Exception ignored) {// ignore
		}
		try {
			dateModify = OffsetDateTime.parse(getString("date:modify"));
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
		return date;
	}

	@Override
	public Size getResolution() {
		try {
			String resolution = getString("xy");
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

	@Override
	public Size getDimension() {
		try {
			String dimensions = getString("wh");
			if(StringUtils.isBlank(dimensions)) {
				dimensions = getString("exif:exifimagewidth") + ' ' + getString("exif:exifimagelength");
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

	@Override
	public String getColorSpace() {
		return getString("colorspace");
	}

	@Override
	public Optional<String> getICCProfile() {
		return getFirstString("icc:description");
	}

	@Override
	public Integer getColorDepth() {
		return getFirstInteger("cdepth").orElse(null);
	}

	@Override
	public Integer getCompressionQuality() {
		return getFirstInteger("compressionq").orElse(null);
	}

	@Override
	public String getCompression() {
		return getString("compression");
	}

	@Override
	public String getResolutionUnit() {
		return getString("resunit");
	}

	@Override
	public String getDevice() {
		return getString("exif:make");
	}

	@Override
	public String getDeviceModel() {
		return getString("exif:model");
	}

	@Override
	public String getSoftware() {
		return getString("exif:software");
	}

	@Override
	public Integer getISOSpeed() {
		return getFirstInteger("exif:isospeedratings")
				.orElseGet(() -> getFirstInteger("exif:photographicsensitivity")
						.orElse(null));
	}

	@Override
	public Float getExposureTime() {
		return getFirstFloat("exif:exposuretime").orElse(null);
	}

	@Override
	public Optional<String> getLensModel() {
		return Optional.empty();
	}

	@Override
	public Float getAperture() {
		return getFirstFloat("exif:fnumber").orElse(null);
	}

	@Override
	public Float getFocalLength() {
		return getFirstFloat("exif:focallength").orElse(null);
	}

	@Override
	public Flash getFlash() {
		return getFirstInteger("exif:flash")
				.map(Flash::valueOf)
				.orElse(null);
	}

	@Override
	public Coordinates getCoordinates() {
		try {
			LTude latitude = LTude.of(parseCoordinate(getString("exif:gpslatitude")), getString("exif:gpslatituderef"));
			LTude longitude = LTude.of(parseCoordinate(getString("exif:gpslongitude")), getString("exif:gpslongituderef"));
			return LTude.toCoordinates(latitude, longitude);
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	public static IMIdentifyImageMetadatas parseJSON(String json) {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> params = gson.fromJson(json, Map.class);
		return new IMIdentifyImageMetadatas(params);
	}

	public static ImageMetadatas extract(InputStream inputStream) throws IOException {
		return with(inputStream).extract();
	}

	public static synchronized IMIdentifyImageMetadatas extractSingleton(final File sourceFile) {
		Future<IMIdentifyImageMetadatas> future = FUTURE_HASHTABLE.get(sourceFile);
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

	private String getString(String prop) {
		return getFirstString(prop).orElse(null);
	}

	private static <T> Map<T, IMIdentifyImageMetadatas> extract(
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
		// https://imagemagick.org/script/escape.php
		IMOperation op = new IMOperation();
		StringJoiner joiner = new StringJoiner("\n", "", "\n");
		joiner.add("==%f==");
		joiner.add("format=%m");
		joiner.add("%[exif:*]%[date:*]%[xap:*]%[icc:*]%[*]xy=%x %y");
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
		Map<T, IMIdentifyImageMetadatas> outMap = new LinkedHashMap<>();
		Iterator<Source<T>> sourceIterator = sources.iterator();
		TreeMap<String, Object> params = null;
		Source<T> currentSource = null;
		for(String line : outputs) {
			if(StringUtils.isBlank(line) || boundary.equals(line)) {
				continue;
			}
			if((line.startsWith("==") || line.startsWith(boundary + "==")) && line.endsWith("==")) {
				if(currentSource != null) {
					outMap.put(currentSource.value, new IMIdentifyImageMetadatas(params));
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
			outMap.put(currentSource.value, new IMIdentifyImageMetadatas(params));
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
