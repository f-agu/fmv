package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
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

import static org.fagu.fmv.core.exec.ExecutableOption.STOP_PROPAGATION_MAKE_BACKGROUND;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.core.Hash;
import org.fagu.fmv.core.project.OutputInfos;
import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.core.project.ProjectListener;
import org.fagu.fmv.core.project.Properties;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Drawtext;
import org.fagu.fmv.ffmpeg.filter.impl.Scale;
import org.fagu.fmv.ffmpeg.filter.impl.ScaleMode;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.utils.Color;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class FileCache {

	public static final Size PREVIEW_SIZE = Size.QVGA;

	private Project project;

	private Map<Executable, ExecutableCache> previousKeyMap;

	private ExecutorService previewOrMakeExecutorService;

	private ScheduledExecutorService lookupService;

	// ------------------------------------

	/**
	 * @author f.agu
	 */
	public enum Cache {
		PREVIEW, MAKE
	}

	// ------------------------------------

	/**
	 * @author f.agu
	 */
	public enum State {
		UNDECLARED, TODO, DONE
	}

	// ------------------------------------

	/**
	 * @param project
	 */
	public FileCache(Project project) {
		this.project = project;
		previousKeyMap = new HashMap<>();
		previewOrMakeExecutorService = Executors.newSingleThreadExecutor();
	}

	/**
	 *
	 */
	public void start() {
		if(lookupService == null) {
			lookupService = Executors.newSingleThreadScheduledExecutor();
			lookupService.scheduleAtFixedRate(backgroundService(), 0, 2, TimeUnit.SECONDS);
		}
	}

	/**
	 * @param executable
	 * @return
	 */
	public Future<File> add(Executable executable) {
		if( ! project.getProperty(Properties.PREPARE_BACKGROUND)) {
			return null;
		}
		Hash hash = executable.getHash();
		Hash previousHash = null;
		ExecutableCache executableCache = previousKeyMap.get(executable);
		if(executableCache == null) { // n'existe pas dans le cache
			executableCache = new ExecutableCache(hash);
		} else if( ! executableCache.isSame(hash)) { // existe, mais pas le meme hash
			previousHash = executableCache.previousHash;
			executableCache.previousHash = hash;
		} else { // existe, hash identique
			executableCache.makeState = executableCache.previewState = State.DONE;
			executableCache = null;
		}

		if(executableCache != null) {
			previousKeyMap.put(executable, executableCache);
			return previewOrMakeExecutorService.submit(createCallable(previousHash, executable, executableCache, Cache.MAKE));
		}
		return null;
	}

	/**
	 * @return
	 */
	public boolean isReady() {
		try {
			return previewOrMakeExecutorService.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param executable
	 * @param cache
	 * @return
	 */
	public File getFile(Executable executable, Cache cache) {
		ExecutableCache executableCache = previousKeyMap.get(executable);
		if(executableCache == null) {
			executableCache = new ExecutableCache(executable.getHash());
		}
		if(executableCache.getFile(cache) == null) {
			try {
				return execute(executable.getId(), executable.getHash(), executable, executableCache, cache);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			// Future<File> future = executorService.submit(createCallable(null, executable, executableCache, cache));
			// try {
			// return future.get();
			// } catch(InterruptedException | ExecutionException e) {
			// throw new RuntimeException(e);
			// }
		}
		return executableCache.getFile(cache);
	}

	/**
	 * @return
	 */
	public Set<Executable> getExecutables() {
		return Collections.unmodifiableSet(previousKeyMap.keySet());
	}

	/**
	 * @param executable
	 * @param cache
	 * @return
	 */
	public State getState(Executable executable, Cache cache) {
		ExecutableCache executableCache = previousKeyMap.get(executable);
		if(executableCache != null && cache != null) {
			switch(cache) {
				case MAKE:
					return executableCache.makeState;
				case PREVIEW:
					return executableCache.previewState;
				default:
			}
		}
		return State.UNDECLARED;
	}

	/**
	 *
	 */
	public void clean() {
		Pattern filePattern = Pattern.compile("([\\w-]+)\\.([\\w-]+)\\.mp4");
		for(final Cache cache : Cache.values()) {
			previewOrMakeExecutorService.submit(() -> {
				File[] listFiles = getFolder(cache).listFiles();
				if(listFiles.length == 0) {
					return;
				}
				for(File file : listFiles) {
					boolean toDelete = true;
					Matcher matcher = filePattern.matcher(file.getName());
					if(matcher.matches()) {
						Optional<Identifiable> optIdentifiable = BaseIdentifiable.findById(project, matcher.group(1));
						if(optIdentifiable.isPresent() && matcher.group(2).equals(optIdentifiable.get().getHash().toString())) {
							toDelete = false;
						}
					}
					if(toDelete) {
						file.delete();
					}
				}
			});
		}
	}

	// ***********************************************

	// ----------------------------------------------

	/**
	 * @author f.agu
	 */
	private class ExecutableCache {

		private Hash previousHash;

		private File previewFile;

		private File makeFile;

		private State makeState = State.TODO;

		private State previewState = State.TODO;

		/**
		 * @param hash
		 */
		public ExecutableCache(Hash hash) {
			this.previousHash = hash;
		}

		/**
		 * @param hash
		 * @return
		 */
		boolean isSame(Hash hash) {
			return previousHash != null && previousHash.equals(hash);
		}

		/**
		 * @param cache
		 * @return
		 */
		File getFile(Cache cache) {
			if(cache == Cache.PREVIEW) {
				return previewFile;
			}
			if(cache == Cache.MAKE) {
				return makeFile;
			}
			throw new IllegalArgumentException("Cache undefined: " + cache);
		}

		/**
		 * @param file
		 * @param cache
		 */
		void setFile(File file, Cache cache) {
			if(cache == Cache.PREVIEW) {
				previewFile = file;
			} else if(cache == Cache.MAKE) {
				makeFile = file;
			} else {
				throw new IllegalArgumentException("Cache undefined: " + cache);
			}
		}
	}

	// ----------------------------------------------

	/**
	 * @param previousHash
	 * @param executable
	 * @param executableCache
	 * @param cacheToReturn
	 * @param cause
	 * @return
	 */
	private Callable<File> createCallable(final Hash previousHash, final Executable executable, final ExecutableCache executableCache,
			final Cache cacheToReturn) {
		// TODO ############################################
		// TODO ############################################
		// TODO ############################################
		// TODO ############################################
		Hash currentHash = executable.getHash();
		return () -> {

			// TODO ############################################
			final String id = executable.getId();
			if(previousHash != null) {
				delete(id, previousHash);
			}
			try {
				// make
				File makeFile = null; // cacheToReturn == Cache.MAKE &&
				// if(ExecCause.RUN_BACKGROUND == cause && !
				// executable.getOptions().contains(ExecutableOptions.STOP_PROPAGATION_MAKE_BACKGROUND)) {
				makeFile = getFile(id, currentHash, Cache.MAKE);
				if( ! makeFile.exists()) {
					executable.execute(makeFile, Cache.MAKE);
				}
				if( ! makeFile.exists()) {
					throw new FileNotFoundException(makeFile.getPath());
				}
				executableCache.setFile(makeFile, Cache.MAKE);
				executableCache.makeState = State.DONE;
				// }

				// preview
				File previewFile = getFile(id, currentHash, Cache.PREVIEW);
				if( ! previewFile.exists()) {
					File tmpFile = null;
					if(makeFile == null || ! makeFile.exists()) {
						tmpFile = getFile(id + ".tmp", currentHash, Cache.PREVIEW);
						executable.execute(tmpFile, Cache.PREVIEW);
						makeFile = tmpFile;
					}
					scaleForPreview(makeFile, previewFile, executable);
					if(tmpFile != null) {
						tmpFile.delete();
					}
				}
				executableCache.setFile(previewFile, Cache.PREVIEW);
				executableCache.previewState = State.DONE;
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			return executableCache.getFile(cacheToReturn);
		};
		// TODO ############################################
		// TODO ############################################
		// TODO ############################################
		// TODO ############################################
	}

	/**
	 * @param id
	 * @param currentHash
	 * @param executable
	 * @param executableCache
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	private File execute(String id, Hash currentHash, Executable executable, ExecutableCache executableCache, Cache cache) throws IOException {
		if(cache == Cache.MAKE) {
			return executeMake(id, currentHash, executable, executableCache);
		}
		if(cache == Cache.PREVIEW) {
			return executePreview(id, currentHash, executable, executableCache);
		}
		throw new RuntimeException("cache undefined: " + cache);
	}

	/**
	 * @param id
	 * @param currentHash
	 * @param executable
	 * @param executableCache
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	private File executeMake(String id, Hash currentHash, Executable executable, ExecutableCache executableCache) throws IOException {
		File file = getFile(id, currentHash, Cache.MAKE);
		if( ! file.exists()) {
			executable.execute(file, Cache.MAKE);
			if( ! file.exists()) {
				throw new FileNotFoundException(file.getPath());
			}
		}
		executableCache.setFile(file, Cache.MAKE);
		executableCache.makeState = State.DONE;
		return file;
	}

	/**
	 * @param id
	 * @param currentHash
	 * @param executable
	 * @param executableCache
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	private File executePreview(String id, Hash currentHash, Executable executable, ExecutableCache executableCache) throws IOException {
		ProjectListener projectListener = project.getListener();
		File previewFile = getFile(id, currentHash, Cache.PREVIEW);
		if( ! previewFile.exists()) {
			File tmpFile = null;
			File makeFile = getFile(id, currentHash, Cache.MAKE);
			if(makeFile == null || ! makeFile.exists()) {
				tmpFile = getFile(id + ".tmp", currentHash, Cache.PREVIEW);
				projectListener.eventExecPrePreviewViaMake(executable);
				executable.execute(tmpFile, Cache.PREVIEW);
				if( ! tmpFile.exists()) {
					throw new FileNotFoundException(tmpFile.getPath());
				}
				makeFile = tmpFile;
			}
			projectListener.eventExecPrePreviewScale(executable, previewFile);
			scaleForPreview(makeFile, previewFile, executable);
			if(tmpFile != null) {
				tmpFile.delete();
			}
			projectListener.eventExecPostPreviewScale(executable, previewFile);
		}
		executableCache.setFile(previewFile, Cache.PREVIEW);
		executableCache.previewState = State.DONE;
		return previewFile;
	}

	/**
	 * @param inFile
	 * @param toFile
	 * @param id
	 * @throws IOException
	 */
	private void scaleForPreview(File inFile, File toFile, Identifiable identifiable) throws IOException {
		OutputInfos outputInfos = project.getOutputInfos();
		Size previewSize = outputInfos.getSize().fitAndKeepRatioTo(PREVIEW_SIZE);

		FFMPEGExecutorBuilder builder = FFUtils.builder(project);

		builder.addMediaInputFile(inFile);

		builder.filter(Scale.to(previewSize, ScaleMode.fitToBox()));
		Drawtext drawtext = Drawtext.build().x(0);
		drawtext.y(16 * identifiable.getDepth(i -> i instanceof Executable));
		drawtext.text(identifiable.getId() + " - %{pts} / %{n}").fontColor(Color.WHITE).fontSize(15);
		builder.filter(drawtext);

		OutputProcessor outputProcessor = builder.addMediaOutputFile(toFile);
		outputProcessor.format(outputInfos.getFormat());
		outputProcessor.overwrite();

		FFExecutor<Object> build = builder.build();
		build.execute();
	}

	/**
	 * @param id
	 * @param hash
	 * @throws IOException
	 */
	private void delete(String id, Hash hash) throws IOException {
		for(final Cache cache : Cache.values()) {
			delete(id, hash, cache);
		}
	}

	/**
	 * @param id
	 * @param hash
	 * @param cache
	 * @throws IOException
	 */
	private void delete(String id, Hash hash, Cache cache) throws IOException {
		FileUtils.deleteQuietly(getFile(id, hash, cache));
	}

	/**
	 * @param cache
	 * @return
	 */
	private File getFolder(Cache cache) {
		return project.getFolderInTemp(cache.name().toLowerCase());
	}

	/**
	 * @param id
	 * @param hash
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	private File getFile(String id, Hash hash, Cache cache) throws IOException {
		File folder = getFolder(cache);
		FileUtils.forceMkdir(folder);
		return new File(folder, id + '.' + hash.toString() + '.' + project.getOutputInfos().getFormat());
	}

	/**
	 * @return
	 */
	private Runnable backgroundService() {
		return () -> {
			try {
				List<Executable> execs = BaseIdentifiable.stream(project).//
				filter(ident -> ident instanceof Executable). //
				map(ident -> (Executable)ident). //
				collect(Collectors.toList());

				if(execs.isEmpty()) {
					addSynchronous(BaseIdentifiable.getRoots(project));
					return;
				}

				for(Executable exec : execs) {
					if(containsExecutableStopPropagation(exec)) {
						continue;
					}
					addSynchronous(findFirstExecutables(exec));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
	}

	/**
	 * @param executables
	 * @return
	 */
	private void addSynchronous(List<Executable> executables) {
		for(Executable executable : executables) {
			try {
				Future<File> add = add(executable);
				if(add != null) {
					add.get();
				}
			} catch(InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param parent
	 * @return
	 */
	private boolean containsExecutableStopPropagation(Identifiable parent) {
		if((parent instanceof Executable && ((Executable)parent).has(STOP_PROPAGATION_MAKE_BACKGROUND))) {
			return true;
		}
		for(Identifiable identifiable : parent.getIdentifiableChildren()) {
			if(containsExecutableStopPropagation(identifiable)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param parent
	 * @return
	 */
	private List<Executable> findFirstExecutables(Identifiable parent) {
		List<Executable> execs = new ArrayList<>();
		if(parent.isExecutable()) {
			execs.add((Executable)parent);
		}
		for(Identifiable identifiable : parent.getIdentifiableChildren()) {
			execs.addAll(findFirstExecutables(identifiable));
		}
		return execs;
	}

}
