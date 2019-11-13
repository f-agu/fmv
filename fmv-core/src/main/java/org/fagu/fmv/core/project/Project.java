package org.fagu.fmv.core.project;

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

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.fagu.fmv.core.FMV;
import org.fagu.fmv.core.exec.BaseIdentifiable;
import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.exec.ExecutableFactory;
import org.fagu.fmv.core.exec.FileCache;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.im.IMIdentifyImageMetadatas;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.utils.PropertyValue;
import org.fagu.fmv.utils.PropertyValues;
import org.fagu.fmv.utils.Proxifier;
import org.fagu.fmv.utils.collection.MapSortedSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class Project {

	private boolean modified;

	private String name;

	private String fmvVersion;

	private File saveFile;

	private OutputInfos outputInfos;

	private final NavigableMap<Integer, FileSource> fileSources;

	private final MapSortedSet<FileType, String> extensions;

	private final FileFilter fileFilter;

	private final List<ProjectListener> projectListeners;

	private final Set<Pattern> undeleteFileSet;

	private final NavigableMap<String, String> propertyMap;

	private final PropertyValues propertyValues;

	private final FileCache fileCache;

	/**
	 * @param saveFile
	 */
	public Project(File saveFile) {
		this.saveFile = saveFile;

		propertyMap = new TreeMap<>();
		propertyValues = new PropertyValues(propertyMap);
		fileSources = new TreeMap<>();
		extensions = MultiValueMaps.hashMapTreeSet();
		fileFilter = pathname -> {
			if(pathname.isDirectory()) {
				return true;
			}
			String name = pathname.getName();
			return extensions.containsValue(FilenameUtils.getExtension(name).toLowerCase());
		};

		projectListeners = new ArrayList<>(4);

		outputInfos = new OutputInfos();
		fileCache = new FileCache(this);

		undeleteFileSet = new HashSet<>(4);
		undeleteFileSet.add(Pattern.compile("preview-\\d+"));
		undeleteFileSet.add(Pattern.compile("make-\\d+"));
	}

	/**
	 * @param saveFile
	 * @param outputInfos
	 */
	public Project(File saveFile, OutputInfos outputInfos) {
		this(saveFile);
		this.outputInfos = outputInfos;
		fmvVersion = FMV.getVersion();

		extensions.addAll(FileType.AUDIO, Arrays.asList("mp3", "ogg", "wav")); // TODO
		extensions.addAll(FileType.VIDEO, Arrays.asList("mov", "mp4", "avi", "mkv")); // TODO
		extensions.addAll(FileType.IMAGE, Arrays.asList("jpg", "jpeg", "gif", "png")); // TODO
	}

	/**
	 * @param property
	 * @return
	 */
	public <V> V getProperty(PropertyValue<V> propertyValue) {
		return propertyValues.getProperty(propertyValue);
	}

	/**
	 * @param name
	 * @return
	 */
	public String getPropertyValue(String name) {
		return propertyMap.get(name);
	}

	/**
	 * @return
	 */
	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(propertyMap.keySet());
	}

	/**
	 * @param property
	 * @param value
	 * @return
	 */
	public <V> V setProperty(PropertyValue<V> property, V value) {
		String before = propertyMap.put(property.name(), property.fromValue(value));
		return before == null ? null : property.toValue(before);
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public String setPropertyValue(String name, String value) {
		return propertyMap.put(name, value);
	}

	/**
	 *
	 */
	public void modified() {
		modified = true;
	}

	/**
	 * @return
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		modified();
	}

	/**
	 * @return
	 */
	public FileCache getFileCache() {
		return fileCache;
	}

	/**
	 * @return the executables
	 */
	public List<Executable> getExecutables() {
		return BaseIdentifiable.getRoots(this);
	}

	/**
	 * @return
	 */
	public OutputInfos getOutputInfos() {
		return outputInfos;
	}

	/**
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return saveFile;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
		modified();
	}

	/**
	 * @param file
	 */
	public void addSource(FileSource fileSource) {
		if( ! fileSources.containsValue(fileSource)) {
			int index = fileSources.size();
			fileSources.put(index, fileSource);
			fileSource.getMetadatas();
			modified();
		}
	}

	/**
	 * @param file
	 */
	public void addSource(File file) {
		addSource(file, file);
		modified();
	}

	/**
	 * @param fileType
	 * @param extension
	 */
	public void addExtension(FileType fileType, String extension) {
		extensions.add(fileType, extension.toLowerCase());
		modified();
	}

	/**
	 * @return
	 */
	public MapSortedSet<FileType, String> getExtensions() {
		return extensions;
	}

	/**
	 * @return
	 */
	public FileSource getSource(int num) {
		return fileSources.get(num);
	}

	/**
	 * @return
	 */
	public Collection<FileSource> getSources() {
		return fileSources.values();
	}

	/**
	 * @param projectListener
	 */
	public void addListener(ProjectListener projectListener) {
		projectListeners.add(projectListener);
	}

	/**
	 * @return
	 */
	public ProjectListener getListener() {
		Proxifier<ProjectListener> proxifier = new Proxifier<>(ProjectListener.class);
		proxifier.addAll(projectListeners);
		return proxifier.proxify();
	}

	/**
	 * @param extension
	 * @return
	 */
	public FileType getTypeByExtension(String extension) {
		String extlc = extension.toLowerCase();
		return extensions.entrySet().stream()
				.filter(e -> e.getValue().contains(extlc))
				.map(Entry::getKey)
				.findFirst()
				.orElse(null);
	}

	/**
	 * @return
	 */
	public File getTempFolder() {
		File folder = new File(saveFile.getAbsolutePath() + ".tmp");
		try {
			FileUtils.forceMkdir(folder);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return folder;
	}

	/**
	 * @param folderName
	 * @return
	 */
	public File getFolderInTemp(String folderName) {
		File tmpFolder = getTempFolder();
		File folder = new File(tmpFolder, folderName);
		try {
			FileUtils.forceMkdir(folder);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return folder;
	}

	/**
	 *
	 */
	public void cleanTempFolder() {
		File tmpFolder = getTempFolder();
		for(File file : tmpFolder.listFiles()) {
			if(undeleteFileSet.stream().noneMatch(p -> p.matcher(file.getName()).matches())) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	/**
	 * @param folderPrefix
	 * @return
	 */
	// public File createFolderFor(String folderPrefix) {
	// File makeParent = saveFile.getParentFile();
	// try {
	// return org.fagu.fmv.utils.file.FileUtils.getTempFolder(folderPrefix, null, makeParent);
	// } catch(IOException e) {
	// throw new RuntimeException(e);
	// }
	// }

	/**
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
	public File createTempFile(String prefix) throws IOException {
		File tmpFolder = getTempFolder();
		String format = getOutputInfos().getFormat();
		return File.createTempFile(prefix, '.' + format, tmpFolder);
	}

	/**
	 * @throws LoadException
	 */
	public void load() throws LoadException {
		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		try {
			Document document = reader.read(getSaveFile());
			Element root = document.getRootElement();

			loadMain(root);
			loadSources(root);
			loadExtensions(root);
			loadProperties(root);
			loadUndeleteFile(root);
			loadOutputInfos(root);

			loadExecutables(root);

		} catch(DocumentException e) {
			throw new LoadException(e);
		}
		modified = false;

		fileCache.clean();
		fileCache.start();
	}

	/**
	 *
	 */
	public void save() throws IOException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("fmv");

		saveMain(root);
		saveSources(root);
		saveExtensions(root);
		saveProperties(root);
		saveUndeleteFileName(root);
		saveOutputInfos(root);
		saveExecutables(root);

		// write
		OutputFormat format = new OutputFormat("\t");
		format.setTrimText(true);
		format.setNewlines(true);
		XMLWriter writer = new XMLWriter(new FileWriter(getSaveFile()), format);
		writer.write(document);
		writer.close();

		modified = false;
	}

	// ************************************

	/**
	 * @param fromFile
	 * @param file
	 */
	private void addSource(File fromFile, File file) {
		if(file.isFile()) {
			addFile(fromFile, file);
		} else if(file.isDirectory()) {
			File[] listFiles = file.listFiles(fileFilter);
			Arrays.sort(listFiles);
			for(File f : listFiles) {
				addSource(fromFile, f);
			}
		}
	}

	/**
	 * @param fromFile
	 * @param file
	 */
	private void addFile(File fromFile, File file) {
		int index = fileSources.size();
		FileSource fileSource = new FileSource(getTypeByExtension(FilenameUtils.getExtension(file.getName())), file, index);
		if( ! fileSources.containsValue(fileSource)) {
			ProjectListener projectListener = getListener();
			projectListener.eventAddSourcePre(fromFile, fileSource, index);
			fileSources.put(index, fileSource);
			fileSource.getMetadatas();
			projectListener.eventAddSourcePost(fromFile, fileSource, index);
			modified();
		}
	}

	/**
	 * @param rootElement
	 */
	private void loadMain(Element root) {
		Element element = root.element("main");
		setName(element.attributeValue("name"));
		fmvVersion = element.attributeValue("fmv-version");
	}

	/**
	 * @param root
	 */
	private void loadSources(Element root) {
		Element sourcesElement = root.element("sources");

		TreeMap<Integer, FileSource> srcMap = new TreeMap<>();
		for(Element element : LoadUtils.elements(sourcesElement, "source")) {
			// number
			int number = NumberUtils.toInt(element.attributeValue("n"), Integer.MIN_VALUE);
			if(number == Integer.MIN_VALUE) {
				System.out.println("Number 'n' undefined");
				continue;
			}
			// type
			FileType fileType = null;
			try {
				fileType = FileType.valueOf(element.attributeValue("type").trim().toUpperCase());
			} catch(Exception e) {
				System.out.println("FileType undefined for number: " + number);
				continue;
			}

			// path
			File file = new File(element.element("path").getTextTrim());
			if( ! file.exists()) {
				System.out.println("File not found: " + file);
				continue;
			}
			FileSource fileSource = new FileSource(fileType, file, number);
			srcMap.put(number, fileSource);

			// metadatas
			String metadatas = element.element("metadatas").getTextTrim();
			if(fileSource.isImage()) {
				fileSource.setImageMetadatas(IMIdentifyImageMetadatas.parseJSON(metadatas));
			} else if(fileSource.isAudioOrVideo()) {
				fileSource.setVideoMetadatas(MovieMetadatas.parseJSON(metadatas));
			}
		}
		for(FileSource fileSource : srcMap.values()) {
			addSource(fileSource);
		}
	}

	/**
	 * @param root
	 */
	private void loadExtensions(Element root) {
		extensions.clear();
		Element extensionsElement = root.element("extensions");

		for(Element element : LoadUtils.elements(extensionsElement, "extension")) {
			String type = element.attributeValue("type");
			FileType fileType = null;
			try {
				fileType = FileType.valueOf(type.toUpperCase());
			} catch(IllegalArgumentException e) {
				System.out.println("Undefined extension type: " + type);
				continue;
			}
			String[] text = element.getText().split(",");
			for(int i = 0; i < text.length; ++i) {
				text[i] = text[i].trim();
			}
			extensions.addAll(fileType, Arrays.asList(text));
		}
	}

	/**
	 * @param root
	 */
	private void loadProperties(Element root) {
		propertyMap.clear();
		Element propertiesElement = root.element("properties");
		for(Element element : LoadUtils.elements(propertiesElement, "property")) {
			String name = element.attributeValue("name");
			String value = element.getText();
			propertyMap.put(name, value);
		}
	}

	/**
	 * @param root
	 */
	private void loadUndeleteFile(Element root) {
		Element undeleteElement = root.element("undelete");
		if(undeleteElement == null) {
			return;
		}
		undeleteFileSet.clear();

		for(Element element : LoadUtils.elements(undeleteElement, "name")) {
			String text = element.getText();
			undeleteFileSet.add(Pattern.compile(text));
		}
	}

	/**
	 * @param root
	 * @throws LoadException
	 */
	private void loadOutputInfos(Element root) throws LoadException {
		Element outputInfosElement = LoadUtils.elementRequire(root, "output");
		outputInfos.load(outputInfosElement);
	}

	/**
	 * @param root
	 * @throws LoadException
	 */
	private void loadExecutables(Element root) throws LoadException {
		for(Element element : LoadUtils.elements(root, "exec")) {
			ExecutableFactory.getSingleton().get(this, element, null);
		}
	}

	/**
	 * @param root
	 */
	private void saveMain(Element root) {
		Element mainElement = root.addElement("main");
		mainElement.addAttribute("name", getName());
		mainElement.addAttribute("fmv-version", fmvVersion);
	}

	/**
	 * @param root
	 */
	private void saveSources(Element root) {
		Element sourcesElement = root.addElement("sources");
		for(FileSource fileSource : getSources()) {
			Element sourceElement = sourcesElement.addElement("source");
			sourceElement.addAttribute("n", Integer.toString(fileSource.getNumber()));
			sourceElement.addAttribute("type", fileSource.getFileType().name());

			Element pathElement = sourceElement.addElement("path");
			pathElement.setText(fileSource.getFile().getAbsolutePath());

			Element mdElement = sourceElement.addElement("metadatas");
			Metadatas metadatas = fileSource.getMetadatas();
			mdElement.setText(metadatas != null ? metadatas.toJSON() : "");
		}
	}

	/**
	 * @param root
	 */
	private void saveExtensions(Element root) {
		Element extensionsElement = root.addElement("extensions");
		for(Entry<FileType, SortedSet<String>> entry : getExtensions().entrySet()) {
			Element extensionElement = extensionsElement.addElement("extension");
			extensionElement.addAttribute("type", entry.getKey().name());
			extensionElement.setText(StringUtils.join(entry.getValue(), ','));
		}
	}

	/**
	 * @param root
	 */
	private void saveProperties(Element root) {
		Element propertiesElement = root.addElement("properties");
		for(Entry<String, String> entry : propertyMap.entrySet()) {
			Element propertyElement = propertiesElement.addElement("property");
			propertyElement.addAttribute("name", entry.getKey());
			propertyElement.setText(entry.getValue());
		}
	}

	/**
	 * @param root
	 */
	private void saveUndeleteFileName(Element root) {
		Element undeleteElement = root.addElement("undelete");
		for(Pattern pattern : undeleteFileSet) {
			Element nameElement = undeleteElement.addElement("name");
			nameElement.setText(pattern.pattern());
		}
	}

	/**
	 * @param root
	 */
	private void saveOutputInfos(Element root) {
		Element outputElement = root.addElement("output");
		outputInfos.save(outputElement);
	}

	/**
	 * @param root
	 */
	private void saveExecutables(Element root) {
		for(Executable executable : getExecutables()) {
			Element execElement = root.addElement("exec");
			executable.save(execElement);
		}
	}

}
