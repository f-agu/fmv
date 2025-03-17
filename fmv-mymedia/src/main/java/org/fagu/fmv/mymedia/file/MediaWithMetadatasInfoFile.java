package org.fagu.fmv.mymedia.file;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatasFactory;
import org.fagu.fmv.im.IMIdentifyImageMetadatas;
import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.ImageMetadatasFactory;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasFactory;
import org.fagu.fmv.mymedia.classify.movie.Movie;
import org.fagu.fmv.mymedia.reduce.MediaFactory;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public class MediaWithMetadatasInfoFile implements InfoFile {

	private final MetadatasFactory metadatasFactory;

	private final MediaFactory<Media> mediaFactory;

	private final List<InfoFile> subInfoFiles;

	private MediaWithMetadatasInfoFile(MetadatasFactory metadatasFactory, MediaFactory<Media> mediaFactory, InfoFile... subInfoFiles) {
		this.metadatasFactory = metadatasFactory;
		this.mediaFactory = mediaFactory;
		this.subInfoFiles = subInfoFiles != null ? Arrays.asList(subInfoFiles) : List.of();
	}

	// *************

	public static MediaWithMetadatasInfoFile image() {
		Identify.search(); // load cache
		return new MediaWithMetadatasInfoFile(new ImageMetadatasFactory(), (file, metadatas) -> new Image(file, (IMIdentifyImageMetadatas)metadatas));
	}

	public static MediaWithMetadatasInfoFile movie() {
		return new MediaWithMetadatasInfoFile(new MovieMetadatasFactory(), (file, metadatas) -> new Movie(file, (MovieMetadatas)metadatas));
	}

	@Override
	public List<Character> getCodes() {
		List<Character> list = new ArrayList<>(subInfoFiles.size() + 1);
		list.add(Character.valueOf('M'));
		subInfoFiles.forEach(inff -> list.addAll(inff.getCodes()));
		return list;
	}

	@Override
	public boolean isMine(Object object) {
		return object instanceof Media;
	}

	@Override
	public List<Line> toLines(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		Metadatas metadatas = infosFile.getMain().getMetadatas();
		String json = metadatas.toJSON();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(500);

		try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(baos)) {
			deflaterOutputStream.write(json.getBytes("UTF-8"));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return List.of(new Line('M', Base64.encodeBase64String(baos.toByteArray())));
	}

	@Override
	public Object parse(File file, String line) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(line));
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (InflaterInputStream inflaterInputStream = new InflaterInputStream(bais)) {
			IOUtils.copy(inflaterInputStream, buffer);
		}

		Metadatas metadatas = metadatasFactory.parseJSON(new String(buffer.toByteArray(), "UTF-8"));
		return mediaFactory.create(file, metadatas);
	}

}
