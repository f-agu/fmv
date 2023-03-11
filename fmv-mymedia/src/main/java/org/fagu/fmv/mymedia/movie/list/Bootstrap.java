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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.list.column.AudioCodecLongNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.AudioCodecNameColumn;
import org.fagu.fmv.mymedia.movie.list.column.AudioColumn;
import org.fagu.fmv.mymedia.movie.list.column.CategoryColumn;
import org.fagu.fmv.mymedia.movie.list.column.FMVTreatedColumn;
import org.fagu.fmv.mymedia.movie.list.column.LastModifiedDateColumn;
import org.fagu.fmv.mymedia.movie.list.column.NameColumn;
import org.fagu.fmv.mymedia.movie.list.column.PathColumn;
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
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;
import org.fagu.fmv.utils.IniFile;


/**
 * @author f.agu
 */
public class Bootstrap implements Closeable {

	private static final String LOG_FILE_PROPERTY = "fmv.movie.list.logfile";

	private final PrintStream printStream;

	private final List<Column> columns;

	private boolean headerWritten;

	private Logger logger;

	public Bootstrap(PrintStream printStream) {
		this.printStream = Objects.requireNonNull(printStream);
		columns = new ArrayList<>();
	}

	public void addColumn(Column column) {
		if(column != null) {
			columns.add(column);
		}
	}

	public void list(File file) {
		list(file, OptionalInt.empty());
	}

	public void list(File file, OptionalInt maxDepth) {
		list(Collections.singletonList(file), maxDepth);
	}

	public void list(Collection<File> files, OptionalInt maxDepth) {
		if(columns.isEmpty()) {
			populateDefaultColumns();
		}
		writeHeaders();
		for(File file : files) {
			recurse(file.toPath(), file, 0, maxDepth.orElseGet(() -> Integer.MAX_VALUE));
		}
		for(Column column : columns) {
			try {
				column.close();
			} catch(Exception e) {
				logger.log(e);
			}
		}
	}

	@Override
	public void close() throws IOException {
		printStream.close();
	}

	// ****************************************************

	private void populateDefaultColumns() {
		columns.add(new NameColumn());
		// columns.add(new SagaNameColumn());
		// columns.add(new SagaOrderColumn());
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
		// columns.add(new AgeLegalColumn());
		// columns.add(new AgeSuggestedColumn(logger));
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

	private void writeHeaders() {
		if(headerWritten) {
			return;
		}
		printStream.println(columns.stream().map(Column::title).collect(Collectors.joining("\t")));
		headerWritten = true;
	}

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

	private FileFilter createFileFilter(IniFile iniFile) {
		if(iniFile == null) {
			return f -> true;
		}
		return f -> ! iniFile.contains("exclude", f.getName());
	}

	private void doFile(Path rootPath, File file) {
		if("Thumbs.db".equalsIgnoreCase(file.getName())) {
			file.delete();
			return;
		}

		if( ! FileTypeUtils.with(FileType.VIDEO).verify(file)) {
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

	private static Logger openLogger() throws IOException {
		String property = System.getProperty(LOG_FILE_PROPERTY);
		if(property == null) {
			property = "sync.log";
		}
		return LoggerFactory.openLogger(new File(property));
	}

	private static List<ListConfig> loadConfig(String[] args, Logger logger) throws IOException {
		List<ListConfig> configs = new LinkedList<>();
		for(String arg : args) {
			File confFile = new File(arg);
			if( ! confFile.exists()) {
				System.out.println("MovieListConfigFile not found: " + arg);
				continue;
			}
			configs.add(ListConfig.load(confFile, logger));
		}
		return configs;
	}

	private static void displayConfig(List<ListConfig> configs, Logger logger) {
		logger.log("List declared:");
		configs.stream()
				.flatMap(lc -> lc.getFolders().stream())
				.forEach(f -> logger.log("Folder: " + f));
	}

	public static void main(String[] args) throws IOException {
		if(args.length < 2) {
			System.out.println("Usage: " + Bootstrap.class.getName()
					+ " <output-file> <movielist-config-file1> [<movielist-config-file2> <movielist-config-file3> ...]");
			return;
		}
		try (Logger logger = openLogger();
				PrintStream printStream = new PrintStream(new File(args[0]));
				Bootstrap bootstrap = new Bootstrap(printStream)) {
			Logger forkLogger = Loggers.fork(logger, Loggers.systemOut());
			bootstrap.logger = forkLogger;

			forkLogger.log("file.encoding: " + System.getProperty("file.encoding"));
			forkLogger.log("Default Charset=" + Charset.defaultCharset());

			AppVersion.logMyVersion(forkLogger::log);
			String[] confArgs = Arrays.copyOfRange(args, 1, args.length);
			List<ListConfig> configs = loadConfig(confArgs, forkLogger);
			forkLogger.log("");
			displayConfig(configs, forkLogger);
			forkLogger.log("");
			if(YesNo.YES.equals(ScannerHelper.yesNo("Continue with this configuration"))) {
				configs.stream()
						.flatMap(lc -> lc.getFolders().stream())
						.forEach(bootstrap::list);
			}
		}
	}

}
