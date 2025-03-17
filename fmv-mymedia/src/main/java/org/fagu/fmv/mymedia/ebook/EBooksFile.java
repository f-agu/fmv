package org.fagu.fmv.mymedia.ebook;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;
import org.fagu.fmv.utils.ByteSize;
import org.fagu.fmv.utils.io.UnclosedInputStream;


/**
 * @author fagu
 */
public class EBooksFile {

	private static final long FAKE_MIN_SIZE = 4_600_000L;

	private final File file;

	private final Map<String, String> metadataMap;

	private final NavigableMap<Long, String> entriesBySize;

	private EBooksFile(File file, Map<String, String> metadataMap, NavigableMap<Long, String> entriesBySize) {
		this.file = file;
		this.metadataMap = metadataMap;
		this.entriesBySize = entriesBySize;
	}

	public static EBooksFile open(File file) throws IOException {
		Map<String, String> metadataMap = null;
		NavigableMap<Long, String> entriesBySize = new TreeMap<>(Collections.reverseOrder());
		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(file))) {
			ZipArchiveEntry zipEntry = null;
			while((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				if(zipEntry.getName().endsWith(".opf")) {
					SAXReader reader = new SAXReader();
					Document document = reader.read(new UnclosedInputStream(zipArchiveInputStream));
					Element rootElement = document.getRootElement();
					Element metadataElement = rootElement.element("metadata");

					List<Element> elements = metadataElement.elements();
					metadataMap = new HashMap<>();
					for(Element element : elements) {
						String name = element.getName();
						if( ! "meta".equals(name)) {
							metadataMap.put(name, element.getText());
						}
					}
				}
				long size = IOUtils.copyLarge(zipArchiveInputStream, NullOutputStream.NULL_OUTPUT_STREAM);
				entriesBySize.put(size, zipEntry.getName());
			}
		} catch(DocumentException e) {
			throw new IOException(e);
		}
		if(metadataMap != null) {
			return new EBooksFile(file, metadataMap, entriesBySize);
		}
		throw new RuntimeException("OPF file not found in " + file);
	}

	public boolean needToWriteMetadatas(Map<String, String> map) {
		if( ! getFakeItems().isEmpty()) {
			return true;
		}
		for(Entry<String, String> entry : map.entrySet()) {
			String expected = StringUtils.defaultString(entry.getValue());
			String current = StringUtils.defaultString(metadataMap.get(entry.getKey()));
			if( ! expected.equals(current) && ! "".equals(current)) {
				return true;
			}
		}
		return false;
	}

	public File getFile() {
		return file;
	}

	public String getMetadata(String name) {
		return metadataMap.get(name);
	}

	public String getAuthor() {
		return metadataMap.get("creator");
	}

	public String getTitle() {
		return metadataMap.get("title");
	}

	public File writeMetadatas(Map<String, String> metadataMap) throws IOException {
		Set<String> excludeEntries = getExcludeEntries();
		String fileName = file.getName();
		File outFile = File.createTempFile(
				FilenameUtils.getBaseName(fileName) + RandomStringUtils.randomAlphabetic(3),
				'.' + FilenameUtils.getExtension(fileName),
				file.getParentFile());

		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(file));
				ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile), StandardCharsets.UTF_8)) {
			ZipArchiveEntry zipEntry = null;
			while((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				// System.out.println(zipEntry.getName());

				ZipEntry newZipEntry = new ZipEntry(zipEntry.getName());
				newZipEntry.setTime(zipEntry.getTime());
				zipOutputStream.putNextEntry(newZipEntry);
				if(excludeEntries.contains(zipEntry.getName())) {
					System.out.println("        Exclude " + zipEntry.getName());
					IOUtils.copyLarge(zipArchiveInputStream, NullOutputStream.NULL_OUTPUT_STREAM);
					continue;
				}

				if(zipEntry.getName().endsWith(".opf")) {
					byte[] opfData = overwriteMetadata(new UnclosedInputStream(zipArchiveInputStream), metadataMap);
					zipEntry.setSize(opfData.length);
					try (ByteArrayInputStream bais = new ByteArrayInputStream(opfData)) {
						IOUtils.copyLarge(bais, zipOutputStream);
					}
				} else if(zipEntry.getName().endsWith(".epub")) {
					File f = new File(file.getParentFile(), new File(zipEntry.getName()).getName());
					System.out.println("        Extract " + f.getName());
					FileUtils.copyToFile(zipArchiveInputStream, f);
				} else {
					IOUtils.copyLarge(zipArchiveInputStream, zipOutputStream);
				}
				zipOutputStream.closeEntry();
			}
		}
		return outFile;
	}

	// **************************************************

	private byte[] overwriteMetadata(InputStream inputStream, Map<String, String> metadataMap) throws IOException {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputStream);
			Element rootElement = document.getRootElement();
			Element metadataElement = rootElement.element("metadata");

			@SuppressWarnings("unchecked")
			List<Element> elements = metadataElement.elements();
			for(Element element : elements) {
				String name = element.getName();
				if( ! "meta".equals(name)) {
					String value = metadataMap.get(name);
					if(value != null) {
						element.setText(value);
					}
				}
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(baos, format);
			writer.write(document);
			return baos.toByteArray();
		} catch(DocumentException e) {
			throw new IOException(e);
		}
	}

	private Set<String> getExcludeEntries() {
		Set<String> names = new HashSet<>();
		// entriesBySize.forEach((k, v) -> System.out.println(k + ": " + v));
		SortedMap<Long, String> ceilingEntries = getFakeItems();
		ifPresentAppend(ceilingEntries, names, "content");
		if( ! ceilingEntries.isEmpty()) {
			List<Long> toDeletes = new ArrayList<>();
			ceilingEntries.forEach((s, n) -> {
				String question = "Keep '" + n + "' (" + ByteSize.formatSize(s) + ')';
				if(YesNo.NO.equals(ScannerHelper.yesNo(question, YesNo.NO))) {
					toDeletes.add(s);
					names.add(n);
				}
			});
			toDeletes.forEach(ceilingEntries::remove);
		}
		return names;
	}

	private SortedMap<Long, String> getFakeItems() {
		return entriesBySize.headMap(FAKE_MIN_SIZE);
	}

	private void ifPresentAppend(SortedMap<Long, String> ceilingEntries, Set<String> excludeEntryNames, String name) {
		List<Long> toDeletes = new ArrayList<>();
		ceilingEntries.entrySet().stream()
				.filter(e -> name.equals(e.getValue()))
				.forEach(e -> {
					toDeletes.add(e.getKey());
					excludeEntryNames.add(e.getValue());
				});
		toDeletes.forEach(ceilingEntries::remove);
	}
}
