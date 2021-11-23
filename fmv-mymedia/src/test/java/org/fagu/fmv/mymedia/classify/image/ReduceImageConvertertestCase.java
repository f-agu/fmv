package org.fagu.fmv.mymedia.classify.image;

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

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.utils.media.Size;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 26 août 2019 21:05:39
 */
class ReduceImageConvertertestCase {

	@Test
	void test() throws IOException {
		Soft convertSoft = Convert.search();
		System.out.println(convertSoft.getFirstFound().getFile());
		try (ReduceImageConverter converter = new ReduceImageConverter(new File("."))) {
			converter.setSize(Size.HD720);
			IMOperation op = new IMOperation();
			op.image("source.jpg", "[0]");
			converter.populateOperation(op);
			op.image("dest.jpg");

			System.out.println(CommandLineUtils.toLine(op.toList()));
		}
	}
}
