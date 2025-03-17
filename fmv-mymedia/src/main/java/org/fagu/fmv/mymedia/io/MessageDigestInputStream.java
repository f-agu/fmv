package org.fagu.fmv.mymedia.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

import org.fagu.fmv.mymedia.utils.IOStringUtils;


public class MessageDigestInputStream extends FilterInputStream {

	private byte[] digest;

	private final MessageDigest messageDigest;

	public MessageDigestInputStream(InputStream inputStream, MessageDigest messageDigest) {
		super(Objects.requireNonNull(inputStream));
		this.messageDigest = Objects.requireNonNull(messageDigest);
	}

	@Override
	public int read() throws IOException {
		int d = in.read();
		if(d >= 0) {
			messageDigest.update((byte)d);
		}
		return d;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int len = in.read(b);
		if(len > 0) {
			messageDigest.update(b, 0, len);
		}
		return len;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int l = in.read(b, off, len);
		if(l > 0) {
			messageDigest.update(b, off, l);
		}
		return l;
	}

	public MessageDigest getMessageDigest() {
		return messageDigest;
	}

	public byte[] getDigest() {
		if(digest == null) {
			digest = messageDigest.digest();
		}
		return Arrays.copyOf(digest, digest.length);
	}

	public String getStringDigest() {
		return IOStringUtils.toHex(getDigest());
	}

	@Override
	public String toString() {
		return "digest[" + messageDigest.getAlgorithm() + ']';
	}
}
