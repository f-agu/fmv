package org.fagu.fmv.mymedia.movie.list;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.mymedia.file.FileUtils;
import org.fagu.fmv.mymedia.movie.list.column.AgeLegalColumn;
import org.fagu.fmv.mymedia.movie.list.column.AgeSuggestedColumn;
import org.fagu.fmv.mymedia.movie.list.column.AudioCodecLongNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.AudioCodecNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.AudioColumn;
import org.fagu.fmv.mymedia.movie.list.column.CategoryColumn;
import org.fagu.fmv.mymedia.movie.list.column.FMVTreatedColumn;
import org.fagu.fmv.mymedia.movie.list.column.LastModifiedDateColumn;
import org.fagu.fmv.mymedia.movie.list.column.NameColumn;
import org.fagu.fmv.mymedia.movie.list.column.PathColumn;
import org.fagu.fmv.mymedia.movie.list.column.SagaNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.SagaOrderColumn;
import org.fagu.fmv.mymedia.movie.list.column.SizeBytesColumn;
import org.fagu.fmv.mymedia.movie.list.column.StreamTypeCountColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoCodecLongNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoCodecNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoDurationColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoHDColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoPixelsColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoRatioColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoRatioFloatColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoSizeColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoSizeHeightColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoSizeNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoSizeWidthColumn;
import org.fagu.fmv.mymedia.movie.list.column.VideoSubtitleColumn;
import org.fagu.fmv.mymedia.movie.list.datatype.DataStoreImpl;
import org.fagu.fmv.utils.IniFile;


/**
 * @author f.agu
 */
public class Bootstrap implements Closeable {

	private static final Set<String> EXTENSIONS = getExtensions();

	private final PrintStream printStream;

	private final List<Column> columns;

	private boolean headerWritten;

	/**
	 * @param printStream
	 */
	public Bootstrap(PrintStream printStream) {
		this.printStream = Objects.requireNonNull(printStream);
		columns = new ArrayList<>();
	}

	/**
	 * @param column
	 */
	public void addColumn(Column column) {
		if(column != null) {
			columns.add(column);
		}
	}

	/**
	 * @param file
	 */
	public void list(File file) {
		list(file, OptionalInt.empty());
	}

	/**
	 * @param file
	 * @param maxDepth
	 */
	public void list(File file, OptionalInt maxDepth) {
		list(Collections.singletonList(file), maxDepth);
	}

	/**
	 * @param files
	 * @param maxDepth
	 */
	public void list(Collection<File> files, OptionalInt maxDepth) {
		if(columns.isEmpty()) {
			populateDefaultColumns();
		}
		writeHeaders();
		for(File file : files) {
			recurse(file.toPath(), file, 0, maxDepth.orElseGet(() -> Integer.MAX_VALUE));
		}
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		printStream.close();
	}

	// ****************************************************

	/**
	 *
	 */
	private void populateDefaultColumns() {
		columns.add(new NameColumn());
		columns.add(new SagaNameColumn());
		columns.add(new SagaOrderColumn());
		columns.add(new VideoHDColumn());
		columns.add(new VideoSizeColumn());
		columns.add(new VideoSizeNameColumn());
		columns.add(new VideoSizeWidthColumn());
		columns.add(new VideoSizeHeightColumn());
		columns.add(new VideoRatioColumn());
		columns.add(new VideoRatioFloatColumn());
		columns.add(new VideoPixelsColumn());
		columns.add(new VideoDurationColumn());
		columns.add(new VideoCodecNameColumn());
		columns.add(new VideoCodecLongNameColumn());
		columns.add(new CategoryColumn(0));
		columns.add(new CategoryColumn(1));
		columns.add(new CategoryColumn(2));
		columns.add(new AgeLegalColumn());
		columns.add(new AgeSuggestedColumn());
		columns.add(new SizeBytesColumn());
		columns.add(new LastModifiedDateColumn());
		columns.add(new AudioColumn());
		columns.add(new AudioCodecNameColumn());
		columns.add(new AudioCodecLongNameColumn());
		columns.add(new VideoSubtitleColumn());
		for(Type type : Type.values()) {
			columns.add(new StreamTypeCountColumn(type));
		}
		columns.add(new FMVTreatedColumn());
		columns.add(new PathColumn());
	}

	/**
	 *
	 */
	private void writeHeaders() {
		if(headerWritten) {
			return;
		}
		printStream.println(columns.stream().map(Column::title).collect(Collectors.joining("\t")));
		headerWritten = true;
	}

	/**
	 * @param rootPath
	 * @param currentFile
	 * @param currentDepth
	 * @param maxDepth
	 */
	private void recurse(Path rootPath, File currentFile, int currentDepth, int maxDepth) {
		if(currentFile.isFile()) {
			doFile(rootPath, currentFile);
			return;
		}
		System.out.println(currentFile);

		IniFile iniFile = loadIniFile(currentFile);

		File[] files = currentFile.listFiles(createFileFilter(iniFile));
		if(files != null) {
			Arrays.sort(files);
			for(File f : files) {
				recurse(rootPath, f, currentDepth + 1, maxDepth);
			}
		}
	}

	/**
	 * @param folder
	 * @return
	 */
	private IniFile loadIniFile(File folder) {
		File file = new File(folder, ".fmv-listmovie");
		if( ! file.exists()) {
			return null;
		}
		try {
			return IniFile.load(file);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param iniFile
	 * @return
	 */
	private FileFilter createFileFilter(IniFile iniFile) {
		if(iniFile == null) {
			return f -> true;
		}
		return f -> ! iniFile.contains("exclude", f.getName());
	}

	/**
	 * @param rootPath
	 * @param file
	 */
	private void doFile(Path rootPath, File file) {
		if("Thumbs.db".equalsIgnoreCase(file.getName())) {
			file.delete();
			return;
		}

		String extension = FilenameUtils.getExtension(file.getName());
		if(extension == null) {
			return;
		}
		if( ! EXTENSIONS.contains(extension)) {
			return;
		}

		Map<String, String> values = new LinkedHashMap<>();
		for(Column column : columns) {
			String value = column.value(rootPath, file, new DataStoreImpl(file, values)).orElse(null);
			values.put(column.title(), value);
		}

		printStream.println(values.values().stream()
				.map(StringUtils::defaultString)
				.collect(Collectors.joining("\t")));
	}

	/**
	 * @return
	 */
	private static Set<String> getExtensions() {
		Set<String> set = new HashSet<>(4);
		set.add("mp4");
		set.add("avi");
		set.add("mkv");
		return Collections.unmodifiableSet(set);
	}

	/**
	 * @throws IOException
	 */
	public static void listFull() throws IOException {
		File root = FileUtils.findFirstHarddriveFaguVv()
				.orElseThrow(() -> new RuntimeException("Harddrive not found"));
		try (PrintStream printStream = new PrintStream(new File("D:\\tmp\\list-full.out")); //
				Bootstrap listMovies = new Bootstrap(printStream)) {

			listMovies.list(new File(root, "Dessins animés"));
			listMovies.list(new File(root, "Dessins animés série"));
			listMovies.list(new File(root, "Documentaires"));
			listMovies.list(new File(root, "Films"));
			listMovies.list(new File(root, "Films HD"));
			listMovies.list(new File(root, "Séries"));
		}
	}

	/**
	 * @throws IOException
	 */
	public static void listName() throws IOException {
		File root = FileUtils.findFirstHarddriveFaguVv()
				.orElseThrow(() -> new RuntimeException("Harddrive not found"));
		try (PrintStream printStream = new PrintStream(new File("D:\\tmp\\list-name.out")); //
				Bootstrap listMovies = new Bootstrap(printStream)) {
			listMovies.addColumn(new NameColumn());

			// printStream.println("=== Dessins animés ===");
			// listMovies.list(new File(root, "Dessins animés"));
			// printStream.println("=== Dessins animés séries ===");
			// Arrays.asList(new File(root, "Dessins animés série").list()).forEach(printStream::println);
			// printStream.println("=== Films ===");
			// listMovies.list(new File(root, "Films"));
			printStream.println("=== Films HD ===");
			listMovies.list(new File(root, "Films HD"));
			// printStream.println("=== Séries ===");
			// Arrays.asList(new File(root, "Séries").list()).forEach(printStream::println);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// listName();
		listFull();
	}

}
