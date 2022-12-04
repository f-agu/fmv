package org.fagu.fmv.mymedia.sources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Oodrive
 * @author f.agu
 * @created 27 juil. 2022 11:24:09
 */
public class ProjectStats implements Comparable<ProjectStats> {

	private final String group;

	private final String name;

	private final String version;

	private String gitUrl;

	private int countFiles;

	private final Map<String, AtomicInteger> countFilesByExtension;

	private long sizeFiles;

	private int countModules;

	public ProjectStats(String group, String name, String version) {
		this.group = Objects.requireNonNull(group);
		this.name = Objects.requireNonNull(name);
		this.version = Objects.requireNonNull(version);
		this.countFilesByExtension = new HashMap<>();
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void addFile(long size, String extension) {
		++countFiles;
		sizeFiles += size;
		countFilesByExtension.computeIfAbsent(extension, k -> new AtomicInteger())
				.incrementAndGet();
	}

	public void incrementModule() {
		addModules(1);
	}

	public void addModules(int module) {
		countModules += module;
	}

	public int getCountFiles() {
		return countFiles;
	}

	public Optional<Integer> getCountFiles(String extension) {
		return Optional.ofNullable(countFilesByExtension.get(extension))
				.map(AtomicInteger::get);
	}

	public Map<String, AtomicInteger> getCountFilesByExtension() {
		return Collections.unmodifiableMap(countFilesByExtension);
	}

	public long getSizeFiles() {
		return sizeFiles;
	}

	public int getCountModules() {
		return countModules;
	}

	public Optional<String> getGitUrl() {
		return Optional.ofNullable(gitUrl);
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	@Override
	public int compareTo(ProjectStats other) {
		int c = group.compareTo(other.group);
		return c == 0 ? name.compareTo(other.name) : c;
	}

}
