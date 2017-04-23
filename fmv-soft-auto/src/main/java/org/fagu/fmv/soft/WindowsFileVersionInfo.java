package org.fagu.fmv.soft;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;


public class WindowsFileVersionInfo {

	interface Version extends Library {

		Version INSTANCE = Native.loadLibrary("Version", Version.class, W32APIOptions.UNICODE_OPTIONS);

		public int GetFileVersionInfoSizeW(String lptstrFilename, int dwDummy);

		public boolean GetFileVersionInfoW(String lptstrFilename, int dwHandle, int dwLen, Pointer lpData);

		public int VerQueryValueW(Pointer pBlock, String lpSubBlock, PointerByReference lplpBuffer, IntByReference puLen);

	}

	public static class VS_FIXEDFILEINFO extends com.sun.jna.Structure {

		public int dwSignature;

		public int dwStrucVersion;

		public int dwFileVersionMS;

		public int dwFileVersionLS;

		public int dwProductVersionMS;

		public int dwProductVersionLS;

		public int dwFileFlagsMask;

		public int dwFileFlags;

		public int dwFileOS;

		public int dwFileType;

		public int dwFileSubtype;

		public int dwFileDateMS;

		public int dwFileDateLS;

		public VS_FIXEDFILEINFO(com.sun.jna.Pointer p) {
			super(p);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.stream(getClass().getDeclaredFields())
					.map(Field::getName)
					.collect(Collectors.toList());
		}
	}

	static String getVersion(String file) {
		int dwDummy = 0;
		int versionlength = Version.INSTANCE.GetFileVersionInfoSizeW(file, dwDummy);

		byte[] bufferarray = new byte[versionlength];
		Pointer lpData = new Memory(bufferarray.length);

		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();
		Version.INSTANCE.GetFileVersionInfoW(file, 0, versionlength, lpData);
		Version.INSTANCE.VerQueryValueW(lpData, "\\", lplpBuffer, puLen);

		VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
		lplpBufStructure.read();

		short[] rtnData = new short[4];
		rtnData[0] = (short)(lplpBufStructure.dwFileVersionMS >> 16);
		rtnData[1] = (short)(lplpBufStructure.dwFileVersionMS & 0xffff);
		rtnData[2] = (short)(lplpBufStructure.dwFileVersionLS >> 16);
		rtnData[3] = (short)(lplpBufStructure.dwFileVersionLS & 0xffff);

		return rtnData[0] + "." + rtnData[1] + "." + rtnData[2] + "." + rtnData[3];
	}

}
