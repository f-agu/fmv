package org.fagu.fmv.ffmpeg.coder;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Set;
import java.util.TreeSet;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class EncodersTestCase {

	@Test
	@Ignore
	public void generator() {
		Set<Encoders> available = new TreeSet<Encoders>((e1, e2) -> e1.getName().compareTo(e2.getName()));
		available.addAll(Encoders.available());
		addEncoder(available, "libx264", Libx264.class);
		addEncoder(available, "libfaac", LibFAAC.class);
		addEncoder(available, "libfdk_aac", LibFDK_AAC.class);

		for(Encoders encoder : available) {
			String name = encoder.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("/**");
			System.out.println(" * " + encoder.getDescription());
			System.out.println(" */");
			System.out.println("public static final Encoders" + fieldName + " = new Encoders(\"" + name + "\");");
		}
	}

	@Test
	public void testCache() {
		assertFalse(Encoders.LIBX264.isCodecExperimental());
		assertSame(Type.VIDEO, Encoders.LIBX264.getType());
		assertSame(Type.AUDIO, Encoders.FLAC.getType());
		assertFalse(Encoders.FLAC.isSliceLevelMultithreading());
	}

	// *************************************************

	private void addEncoder(Set<Encoders> available, String name, Class<?> codecClass) {
		Encoders byName = Encoders.byName(name);
		if(byName != null) {
			available.remove(byName);
		}
		Encoders encoder = new Encoders(name);
		available.add(encoder);
	}
}
