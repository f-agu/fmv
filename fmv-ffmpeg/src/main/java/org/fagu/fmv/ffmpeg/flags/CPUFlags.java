package org.fagu.fmv.ffmpeg.flags;

/*
 * #%L
 * fmv-ffmpeg
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

import org.fagu.fmv.ffmpeg.format.IO;


/**
 * @author f.agu
 */
public class CPUFlags extends Flags<CPUFlags> {

	private static int index;

	public static final CPUFlags MMX = new CPUFlags("mmx", null);

	public static final CPUFlags MMXEXT = new CPUFlags("mmxext", null);

	public static final CPUFlags SSE = new CPUFlags("sse", null);

	public static final CPUFlags SSE2 = new CPUFlags("sse2", null);

	public static final CPUFlags SSE2SLOW = new CPUFlags("sse2slow", null);

	public static final CPUFlags SSE3 = new CPUFlags("sse3", null);

	public static final CPUFlags SSE3SLOW = new CPUFlags("sse3slow", null);

	public static final CPUFlags SSSE3 = new CPUFlags("ssse3", null);

	public static final CPUFlags ATOM = new CPUFlags("atom", null);

	public static final CPUFlags SSE4_1 = new CPUFlags("sse4.1", null);

	public static final CPUFlags SSE4_2 = new CPUFlags("sse4.2", null);

	public static final CPUFlags AVX = new CPUFlags("avx", null);

	public static final CPUFlags XOP = new CPUFlags("xop", null);

	public static final CPUFlags FMA4 = new CPUFlags("fma4", null);

	public static final CPUFlags _3DNOW = new CPUFlags("3dnow", null);

	public static final CPUFlags _3DNOWEXT = new CPUFlags("3dnowext", null);

	public static final CPUFlags CMOV = new CPUFlags("cmov", null);

	public static final CPUFlags ARMV5TE = new CPUFlags("armv5te", null);

	public static final CPUFlags ARMV6 = new CPUFlags("armv6", null);

	public static final CPUFlags ARMV6T2 = new CPUFlags("armv6t2", null);

	public static final CPUFlags VFP = new CPUFlags("vfp", null);

	public static final CPUFlags VFPV3 = new CPUFlags("vfpv3", null);

	public static final CPUFlags NEON = new CPUFlags("neon", null);

	public static final CPUFlags ALTIVEC = new CPUFlags("altivec", null);

	public static final CPUFlags PENTIUM2 = new CPUFlags("pentium2", null);

	public static final CPUFlags PENTIUM3 = new CPUFlags("pentium3", null);

	public static final CPUFlags PENTIUM4 = new CPUFlags("pentium4", null);

	public static final CPUFlags K6 = new CPUFlags("k6", null);

	public static final CPUFlags K62 = new CPUFlags("k62", null);

	public static final CPUFlags ATHLON = new CPUFlags("athlon", null);

	public static final CPUFlags ATHLONXP = new CPUFlags("athlonxp", null);

	public static final CPUFlags K8 = new CPUFlags("k8", null);

	public CPUFlags(String name, IO io) {
		super(CPUFlags.class, index++, name, io);
	}

}
