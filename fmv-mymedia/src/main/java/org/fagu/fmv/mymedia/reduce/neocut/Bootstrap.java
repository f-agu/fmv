package org.fagu.fmv.mymedia.reduce.neocut;

import static org.fagu.fmv.utils.ByteSize.toStringDiffSize;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.media.FileTypeUtils.FileIs;
import org.fagu.fmv.mymedia.compare.ImageDiffPercent;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.utils.AppVersion;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 3 avr. 2018 23:00:35
 */
public class Bootstrap implements Closeable {

	private static final String PROPERTY_LOG_FILE = "fmv.reduceneo.log.file";

	private static final String PROPERTY_LOG_FILE_DEFAULT_NAME = "fmv-reduceneo.log";

	private static final double MAX_PERCENT_NOT_SIMILAR = 5D;

	private final Set<Time> times = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			Time.valueOf(0.5), Time.valueOf(1.5), Time.valueOf(3))));

	private final File folderOrFileToReduce;

	private final File destinationFolder;

	private final File templateFolder;

	private final Images images;

	private final NavigableSet<File> movieFiles = new TreeSet<>();

	private final List<Template> templates = new ArrayList<>();

	private final ExecutorService executorService = Executors.newFixedThreadPool(times.size());

	private final Logger logger;

	public Bootstrap(File folderOrFileToReduce) throws IOException {
		File folder = folderOrFileToReduce.isDirectory() ? folderOrFileToReduce : folderOrFileToReduce.getParentFile();
		logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(folder, PROPERTY_LOG_FILE, PROPERTY_LOG_FILE_DEFAULT_NAME));
		// logger = Loggers.systemOut();

		AppVersion.logMyVersion(logger::log);

		this.folderOrFileToReduce = Objects.requireNonNull(folderOrFileToReduce);
		destinationFolder = new File(folder.getAbsolutePath() + "-neocut");
		FileUtils.forceMkdir(destinationFolder);
		templateFolder = new File(folder, "conf-neocut");
		logger.log("Destination folder: " + destinationFolder);
		images = new Images(new File(folder, "images-neocut"), times);
	}

	public void findSimilarBetweenThem() throws IOException {
		init();
		Map<File, Set<File>> similarMap = analyzeBetweenThem();
		similarMap.forEach((k, v) -> System.out.println(k + ": " + v));
	}

	public void findSimilarWithTemplate() throws IOException {
		init();
		compareWithTemplate((template, movieFile) -> {
			System.out.println(movieFile.getName() + " : " + template.getName());
		});
		//
		// Map<Template, Set<File>>
		// Map<Template, Set<File>> similarMap = compareWithTemplate();
		// similarMap.forEach((k, v) -> System.out.println(k + ": " + v));
	}

	public void reduce() throws IOException {
		init();
		compareWithTemplate((template, movieFile) -> {
			System.out.println(movieFile);
			logger.log(template.getName() + " => " + movieFile);
			try (Reducer reducer = new Reducer(logger)) {
				String fileName = movieFile.getName();
				// File destFile = File.createTempFile(FilenameUtils.getBaseName(fileName), '.' +
				// FilenameUtils.getExtension(fileName),
				// destinationFolder);
				File destFile = new File(destinationFolder, fileName);
				if(destFile.exists() && destFile.length() > 10) {
					logger.log("File exists: " + destFile);
					return;
				}
				reducer.reduce(movieFile, destFile, template);
				logger.log(toStringDiffSize(movieFile.length(), destFile.length()));
				System.out.println();
			} catch(Exception e) {
				logger.log(e);
			}
		});
	}

	@Override
	public void close() throws IOException {
		executorService.shutdown();
	}

	// ***************************************

	private void init() throws IOException {
		System.out.println("Loading images...");
		loadAllImages(folderOrFileToReduce);
		System.out.println("Loading templates...");
		loadTemplates();
	}

	private void loadAllImages(File folderOrFile) throws IOException {
		if(folderOrFile.isFile()) {
			images.getImages(folderOrFile);
			// if(file.getName().startsWith("...")) {
			movieFiles.add(folderOrFile);
			// }
			return;
		}
		FileIs verifyWith = FileTypeUtils.with(FileType.VIDEO);
		File[] files = folderOrFile.listFiles(f -> f.isDirectory() || verifyWith.verify(f));
		if(files == null) {
			return;
		}
		for(File file : files) {
			loadAllImages(file);
		}
	}

	private void loadTemplates() throws IOException {
		FileUtils.forceMkdir(templateFolder);
		File[] files = templateFolder.listFiles(f -> "properties".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(files == null) {
			return;
		}
		for(File file : files) {
			Template template = Template.load(file);
			templates.add(template);
			logger.log("Add template " + template.getName());
		}
	}

	private Map<File, Set<File>> analyzeBetweenThem() throws IOException {
		Set<File> done = new HashSet<>();
		Map<File, Set<File>> map = new HashMap<>();
		for(File movieFile : movieFiles) {
			System.out.print(movieFile.getName().substring(0, 20) + "...  ");
			if(done.contains(movieFile)) {
				System.out.println("#");
				continue;
			}
			for(File withFile : movieFiles.tailSet(movieFile, false)) {
				if(isSimilar(movieFile, withFile)) {
					map.computeIfAbsent(movieFile, k -> new HashSet<>())
							.add(withFile);
					done.add(withFile);
					System.out.print('X');
				} else {
					System.out.print('.');
				}
			}
			System.out.println();
		}
		return map;
	}

	private void compareWithTemplate(BiConsumer<Template, File> similarConsumer) throws IOException {
		Set<File> done = new HashSet<>();
		for(Template template : templates) {
			for(File movieFile : movieFiles) {
				if(done.contains(movieFile)) {
					continue;
				}
				if(isSimilar(template.getModelMap(), images.getImages(movieFile))) {
					similarConsumer.accept(template, movieFile);
					done.add(movieFile);
				}
			}
		}
	}

	private boolean isSimilar(File f1, File f2) throws IOException {
		Map<Time, File> map1 = images.getImages(f1);
		Map<Time, File> map2 = images.getImages(f2);
		return isSimilar(map1, map2);
	}

	private boolean isSimilar(Map<Time, File> map1, Map<Time, File> map2) throws IOException {
		AtomicBoolean similar = new AtomicBoolean(false);
		List<CompletableFuture<Boolean>> completableFutures = new ArrayList<>();
		for(Entry<Time, File> entry : map1.entrySet()) {
			completableFutures.add(CompletableFuture.supplyAsync(() -> {
				try {
					double diffPercent = getDiffPercent(entry.getValue(), map2.get(entry.getKey()));
					similar.set(diffPercent < MAX_PERCENT_NOT_SIMILAR);
					return similar.get();
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}, executorService));
		}
		try {
			CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
					.get();
		} catch(InterruptedException | ExecutionException e) {
			logger.log(e);
		}
		return similar.get();
	}

	private double getDiffPercent(File f1, File f2) throws IOException {
		try (InputStream i1 = new FileInputStream(f1);
				InputStream i2 = new FileInputStream(f2)) {
			return ImageDiffPercent.getDifferencePercent(i1, i2);
		}
	}

	public static void main(String[] args) throws IOException {
		File folderOrFileToCut = new File(args[0]);
		try (Bootstrap bootstrap = new Bootstrap(folderOrFileToCut)) {
			// bootstrap.findSimilarBetweenThem();
			// bootstrap.findSimilarWithTemplate();
			bootstrap.reduce();
		}
	}

}
