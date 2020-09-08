package org.fagu.fmv.ffmpeg.soft;

/*-
 * #%L
 * fmv-ffmpeg
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.fagu.fmv.soft.find.SoftFound;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 sept. 2019 09:52:11
 */
public class BuildMappingBuildDateGenerator {

	public static void main(String[] args) throws Exception {
		String surl = "https://ffmpeg.zeranoe.com/builds/win64/static/";
		URL url = new URL(surl);
		Map<String, LocalDate> map = new HashMap<>();
		HttpURLConnection hc = openConnection(url);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(hc.getInputStream())))) {
			Pattern pattern = Pattern.compile(".*href=\"(ffmpeg-\\d{8}.*-win64-static\\.zip)\" title.*(\\d{4})-(\\w{3})-(\\d{2}).*");
			reader.lines()
					.map(pattern::matcher)
					.filter(m -> m.matches())
					.forEach(s -> map.put(s.group(1),
							LocalDate.of(
									Integer.parseInt(s.group(2)),
									Month3Letters.valueOf(s.group(3)).ordinal() + 1,
									Integer.parseInt(s.group(4)))));
		}

		System.out.println("// Generated by " + BuildMappingBuildDateGenerator.class.getName());
		System.out.println();
		for(Entry<String, LocalDate> entry : map.entrySet()) {
			URL uf = new URL(surl + entry.getKey());
			hc = openConnection(uf);
			hc.setRequestProperty("Referer", surl);
			try (ZipInputStream zipInputStream = new ZipInputStream(hc.getInputStream())) {
				ZipEntry zipEntry;
				while((zipEntry = zipInputStream.getNextEntry()) != null) {
					if(zipEntry.getName().endsWith("bin/ffmpeg.exe")) {
						File tmpFile = File.createTempFile("ffmpeg-", ".exe");
						try {
							try (OutputStream outputStream = new FileOutputStream(tmpFile)) {
								IOUtils.copyLarge(zipInputStream, outputStream);
							}
							SoftFound softFound = new FFMpegSoftProvider().createSoftFoundFactory(null).create(tmpFile, null, null);
							FFInfo ffInfo = (FFInfo)softFound.getSoftInfo();
							Integer builtVersion = ffInfo.getBuiltVersion();
							if(builtVersion != null) {
								LocalDate dat = entry.getValue();
								System.out.println(
										new StringBuilder()
												.append("map.put(").append(builtVersion)
												.append(", LocalDate.of(").append(dat.getYear()).append(", ").append(dat.getMonthValue())
												.append(", ").append(dat.getDayOfMonth()).append("));"));
							}
						} finally {
							tmpFile.delete();
						}
					}
				}
			}
		}

	}

	private static HttpURLConnection openConnection(URL url) throws IOException {
		HttpURLConnection hc = (HttpURLConnection)url.openConnection();
		hc.setRequestMethod("GET");

		hc.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		hc.setRequestProperty("Accept-Encoding", "gzip");
		hc.setRequestProperty("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3");
		hc.setRequestProperty("Cache-Control", "no-cache");
		hc.setRequestProperty("Connection", "keep-alive");
		// hc.setRequestProperty("Cookie", "__cfduid=d98508eebc2e005af1d857af0145ed8431560157915");
		hc.setRequestProperty("DNT", "1");
		hc.setRequestProperty("Host", "ffmpeg.zeranoe.com");
		hc.setRequestProperty("Pragma", "no-cache");
		hc.setRequestProperty("Upgrade-Insecure-Requests", "1");
		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:69.0) Gecko/20100101 Firefox/69.0");

		hc.setUseCaches(false);
		hc.setDoInput(true);
		hc.setDoOutput(true);
		return hc;
	}

	private enum Month3Letters {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
	}

}
