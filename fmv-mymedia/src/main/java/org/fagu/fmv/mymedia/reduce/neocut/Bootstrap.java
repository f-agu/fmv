package org.fagu.fmv.mymedia.reduce.neocut;

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
import org.fagu.fmv.mymedia.compare.ImageDiffPercent;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 3 avr. 2018 23:00:35
 */
public class Bootstrap {

	private static final String PROPERTY_LOG_FILE = "fmv.reduceneo.log.file";

	private static final String PROPERTY_LOG_FILE_DEFAULT_NAME = "fmv-reduceneo.log";

	private static final double MAX_PERCENT_NOT_SIMILAR = 5D;

	private final Set<String> extensions = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("mp4", "avi", "mkv")));

	private final Set<Time> times = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			Time.valueOf(0.5), Time.valueOf(1.5), Time.valueOf(3))));

	private final File folderToReduce;

	private final Images images;

	private final NavigableSet<File> movieFiles = new TreeSet<>();

	private final List<Template> templates = new ArrayList<>();

	private final ExecutorService executorService = Executors.newFixedThreadPool(times.size());

	private final Logger logger;

	public Bootstrap(File folderToReduce) throws IOException {
		// logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(folderToReduce, PROPERTY_LOG_FILE,
		// PROPERTY_LOG_FILE_DEFAULT_NAME));
		logger = Loggers.systemOut();
		this.folderToReduce = Objects.requireNonNull(folderToReduce);
		images = new Images(new File(folderToReduce, "images-neocut"), times);
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
			logger.log(template.getName() + " => " + movieFile);
			try (Reducer reducer = new Reducer(logger)) {
				String fileName = movieFile.getName();
				File destFile = File.createTempFile(FilenameUtils.getBaseName(fileName), '.' + FilenameUtils.getExtension(fileName), movieFile
						.getParentFile());
				reducer.reduce(movieFile, destFile, template);

			} catch(Exception e) {
				logger.log(e);
			}
		});
	}

	// ***************************************

	private void init() throws IOException {
		loadAllImages(folderToReduce);
		loadTemplates();
	}

	private void loadAllImages(File folder) throws IOException {
		File[] files = folder.listFiles(f -> f.isDirectory() || extensions.contains(FilenameUtils.getExtension(f.getName())));
		if(files == null) {
			return;
		}
		for(File file : files) {
			if(file.isDirectory()) {
				loadAllImages(file);
			} else {
				images.getImages(file);

				if(file.getName().startsWith("tushy")) {
					movieFiles.add(file);
				}
			}
		}
	}

	private void loadTemplates() throws IOException {
		File templateFolder = new File(folderToReduce, "conf-neocut");
		FileUtils.forceMkdir(templateFolder);
		File[] files = templateFolder.listFiles(f -> "properties".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(files == null) {
			return;
		}
		for(File file : files) {
			templates.add(Template.load(file));
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
					Images.extractImage(movieFile, Time.valueOf(60));
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
		File folderToCut = new File(args[0]);
		Bootstrap bootstrap = new Bootstrap(folderToCut);
		// bootstrap.findSimilarBetweenThem();
		// bootstrap.findSimilarWithTemplate();
		bootstrap.reduce();
	}

}
