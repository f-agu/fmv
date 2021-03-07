package org.fagu.fmv.mymedia.m3u;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * @author fagu
 * @created 7 mars 2021 15:08:01
 */
public class M3U8Writer implements Closeable {

	private final BufferedWriter writer;

	private boolean headerWritten;

	public M3U8Writer(OutputStream outputStream) {
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
	}

	public M3U8Writer addTitle(String path) throws IOException {
		return addTitle(null, path, null);
	}

	public M3U8Writer addTitle(String name, String path) throws IOException {
		return addTitle(name, path, null);
	}

	public M3U8Writer addTitle(String name, String path, Integer durationInSeconds) throws IOException {
		Objects.requireNonNull(path);
		writeHeader();
		String n = name != null ? name : new File(path).getName();
		writer.write("#EXTINF:" + (durationInSeconds != null ? durationInSeconds.toString() : "-1") + "," + n);
		writer.newLine();
		writer.write(path);
		writer.newLine();
		return this;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	// *******************************

	private void writeHeader() throws IOException {
		if( ! headerWritten) {
			headerWritten = true;
			writer.write("#EXTM3U");
			writer.newLine();
		}
	}

}
