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
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class DecodersTestCase {

	@Test
	@Ignore
	public void generator() {
		Set<Decoders> available = new TreeSet<Decoders>((e1, e2) -> e1.getName().compareTo(e2.getName()));
		available.addAll(Decoders.available());
		// addDecoder(available, "...", MyDecoder.class);

		for(Decoders decoder : available) {
			String name = decoder.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("/**");
			System.out.println(" * " + decoder.getDescription());
			System.out.println(" */");
			System.out.println("public static final Decoders" + fieldName + " = new Decoders(\"" + name + "\");");
		}
	}

	@Test
	public void testCache() {
		assertFalse(Decoders.H264.isCodecExperimental());
		assertTrue(Decoders.H264.isFrameLevelMultithreading());
		assertSame(Type.VIDEO, Decoders.H264.getType());
		assertTrue(Decoders.PNG.isFrameLevelMultithreading());
	}

	// *************************************************

	/**
	 * @param name
	 * @param codecClass
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	// private void addDecoder(Set<Decoders<Coder>> available, String name, Class<? extends Coder> codecClass) {
	// Decoders<Coder> byName = Decoders.byName(name);
	// if(byName != null) {
	// available.remove(byName);
	// }
	// Decoders<Coder> encoder = new Decoders<Coder>(name, (Class<Coder>)codecClass);
	// available.add((Decoders<Coder>)(Decoders<?>)encoder);
	// }

}
