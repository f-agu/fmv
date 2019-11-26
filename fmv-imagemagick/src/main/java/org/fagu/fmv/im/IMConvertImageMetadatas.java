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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.MapImageMetadatas;
import org.fagu.fmv.image.exif.Flash;
import org.fagu.fmv.image.exif.Resolution;
import org.fagu.fmv.media.MetadatasBuilder;
import org.fagu.fmv.media.MetadatasContainer;
import org.fagu.fmv.media.NavigableMapMetadatasContainer;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.utils.geo.Coordinates;
import org.fagu.fmv.utils.media.Size;

import com.google.gson.Gson;


/**
 * <pre>
 * magick convert <source1> <source2> json:-
 * </pre>
 * 
 * @author f.agu
 */
public class IMConvertImageMetadatas extends MapImageMetadatas implements Serializable {

	private static final long serialVersionUID = - 3899723797675922936L;

	private static final Hashtable<File, Future<IMConvertImageMetadatas>> FUTURE_HASHTABLE = new Hashtable<>();

	// --------------------------------------------------------

	public abstract static class ImageMetadatasSourcesBuilder<B extends ImageMetadatasSourcesBuilder<?, T>, T>
			implements MetadatasBuilder<IMConvertImageMetadatas, B> {

		final Sources<T> sources;

		Soft convertSoft;

		Consumer<CommandLine> logger;

		Consumer<SoftExecutor> customizeExecutor;

		private ImageMetadatasSourcesBuilder(Sources<T> sources) {
			this.sources = sources;
			convertSoft = Convert.search();
		}

		@Override
		public B soft(Soft convertSoft) {
			this.convertSoft = Objects.requireNonNull(convertSoft);
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
		public IMConvertImageMetadatas extract() throws IOException {
			Map<T, IMConvertImageMetadatas> extract = IMConvertImageMetadatas.extract(convertSoft, sources, logger, customizeExecutor);
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

		public Map<File, IMConvertImageMetadatas> extractAll() throws IOException {
			return IMConvertImageMetadatas.extract(convertSoft, sources, logger, customizeExecutor);
		}

	}

	// --------------------------------------------------------

	public static class ImageMetadatasInputStreamBuilder extends ImageMetadatasSourcesBuilder<ImageMetadatasInputStreamBuilder, Object> {

		private ImageMetadatasInputStreamBuilder(InputStream inputStream) {
			super(new InputStreamSources(inputStream));
		}

	}

	// --------------------------------------------------------

	private final String json;

	protected IMConvertImageMetadatas(Map<String, Object> metadatas, String json) {
		super(metadatas);
		this.json = Objects.requireNonNull(json);
	}

	@Override
	public Object get(String name) {
		if(name.contains(":")) {
			return getProperties().get(name);
		}
		return getData().get(name);
	}

	@Override
	public String getFormat() {
		return getFirstString("format").orElse(null);
	}

	@Override
	public OffsetDateTime getDate() {
		MetadatasContainer properties = getProperties();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		// exif
		OffsetDateTime date = Stream.of("exif:DateTime", "exif:DateTimeOriginal", "exif:DateTimeDigitized")
				.map(properties::getFirstString)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(value -> {
					try {
						Date parse = dateFormat.parse(value);
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
		date = properties.getFirstString("xap:createdate")
				.map(s -> {
					try {
						int lastIndex = s.lastIndexOf(':');
						String xapDate = s.substring(0, lastIndex) + s.substring(lastIndex + 1, s.length());
						Date parse = dateFormat.parse(xapDate);
						return parse.toInstant().atOffset(OffsetDateTime.now().getOffset());
					} catch(Exception e) {
						return null;
					}
				})
				.orElse(null);
		if(date != null) {
			return date;
		}

		// other
		OffsetDateTime dateCreate = null;
		OffsetDateTime dateModify = null;
		try {
			dateCreate = properties.getFirstString("date:create").map(OffsetDateTime::parse).orElse(null);
		} catch(Exception ignored) {// ignore
		}
		try {
			dateModify = properties.getFirstString("date:modify").map(OffsetDateTime::parse).orElse(null);
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
		return getFirstMetadatas("resolution")
				.flatMap(mc -> toSize(mc, "x", "y"))
				.filter(Objects::nonNull)
				.orElseGet(() -> toSize(getProperties(), "exif:XResolution", "exif:YResolution")
						.orElse(null));
	}

	@Override
	public Size getDimension() {
		return getFirstMetadatas("geometry")
				.flatMap(mc -> toSize(mc, "width", "height"))
				.filter(Objects::nonNull)
				.orElseGet(() -> toSize(getProperties(), "exif:PixelXDimension", "exif:PixelYDimension")
						.orElse(null));
	}

	@Override
	public String getColorSpace() {
		return getFirstString("colorspace").orElse(null);
	}

	@Override
	public Optional<String> getICCProfile() {
		return getFirstString("icc:description");
	}

	@Override
	public Integer getColorDepth() {
		return getFirstInteger("depth").orElse(null);
	}

	@Override
	public Integer getCompressionQuality() {
		return getFirstInteger("quality").orElse(null);
	}

	@Override
	public String getCompression() {
		return getFirstString("compression").orElse(null);
	}

	@Override
	public String getResolutionUnit() {
		return getProperties()
				.getFirstInteger("exif:ResolutionUnit")
				.flatMap(Resolution::valueOf)
				.map(Resolution::getText)
				.orElse(null);
	}

	@Override
	public String getDevice() {
		return getProperties()
				.getFirstString("exif:Make")
				.orElse(null);
	}

	@Override
	public String getDeviceModel() {
		return getProperties()
				.getFirstString("exif:Model")
				.orElse(null);
	}

	@Override
	public String getSoftware() {
		return getProperties()
				.getFirstString("exif:Software")
				.orElse(null);
	}

	@Override
	public Integer getISOSpeed() {
		MetadatasContainer properties = getProperties();
		return getProperties()
				.getFirstInteger("exif:isospeedratings")
				.orElseGet(() -> properties.getFirstInteger("exif:photographicsensitivity")
						.orElse(null));
	}

	@Override
	public Float getExposureTime() {
		return getProperties()
				.getFirstFloat("exif:exposuretime")
				.orElse(null);
	}

	@Override
	public Optional<String> getLensModel() {
		return Optional.empty();
	}

	@Override
	public Float getAperture() {
		return getProperties()
				.getFirstFloat("exif:fnumber")
				.orElse(null);
	}

	@Override
	public Float getFocalLength() {
		return getProperties()
				.getFirstFloat("exif:FocalLength")
				.orElse(null);
	}

	@Override
	public Flash getFlash() {
		return getProperties()
				.getFirstInteger("exif:Flash")
				.map(Flash::valueOf)
				.orElse(null);
	}

	@Override
	public Coordinates getCoordinates() {
		MetadatasContainer properties = getProperties();
		try {
			LTude latitude = LTude.of(parseCoordinate(properties.getFirstString("exif:GPSLatitude").orElse(null)),
					properties.getFirstString("exif:GPSLatitudeRef").orElse(null));
			LTude longitude = LTude.of(parseCoordinate(properties.getFirstString("exif:GPSLongitude").orElse(null)),
					properties.getFirstString("exif:GPSLongitudeRef").orElse(null));
			return LTude.toCoordinates(latitude, longitude);
		} catch(Exception ignored) {// ignore
		}
		return null;
	}

	@Override
	public String toJSON() {
		return json;
	}

	public MetadatasContainer getProperties() {
		return getFirstMetadatas("properties").orElse(new NavigableMapMetadatasContainer(Collections.emptyNavigableMap()));
	}

	public static ImageMetadatas extract(InputStream inputStream) throws IOException {
		return with(inputStream).extract();
	}

	public static synchronized IMConvertImageMetadatas extractSingleton(final File sourceFile) {
		Future<IMConvertImageMetadatas> future = FUTURE_HASHTABLE.get(sourceFile);
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

	@SuppressWarnings("unchecked")
	private static <T> Map<T, IMConvertImageMetadatas> extract(
			Soft identifySoft,
			Sources<T> sources,
			Consumer<CommandLine> logger,
			Consumer<SoftExecutor> customizeExecutor)
			throws IOException {

		Objects.requireNonNull(identifySoft);
		if( ! sources.has()) {
			return Collections.emptyMap();
		}

		IMOperation op = new IMOperation();
		sources.addImages(op);
		op.add("json:-");

		StringBuilder output = new StringBuilder();
		SoftExecutor softExecutor = identifySoft.withParameters(op.toList())
				.addOutReadLine(output::append)
				.logCommandLine(logger);
		if(customizeExecutor != null) {
			customizeExecutor.accept(softExecutor);
		}
		sources.getSoftExecutor().accept(softExecutor);
		softExecutor.execute();

		Gson gson = new Gson();
		List<Object> map = gson.fromJson(output.toString(), List.class);
		Iterator<Source<T>> iterator = sources.iterator();
		return map.stream()
				.map(m -> new IMConvertImageMetadatas((Map<String, Object>)((Map<String, Object>)m).get("image"), output.toString()))
				.collect(Collectors.toMap(m -> iterator.next().value, Function.identity()));
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

	private static Optional<Size> toSize(MetadatasContainer metadatasContainer, String widthName, String heightName) {
		Integer x = metadatasContainer.getFirstInteger(widthName).orElse(null);
		Integer y = metadatasContainer.getFirstInteger(heightName).orElse(null);
		if(x != null && y != null) {
			return Optional.of(Size.valueOf(x, y));
		}
		return Optional.empty();
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
