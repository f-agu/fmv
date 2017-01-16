
/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.io.IOException;

import org.fagu.fmv.soft.im.Convert;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ArrayListOutputConsumer;


/**
 * @author f.agu
 */
public class TestPipe {

	/**
	 * 
	 */
	public TestPipe() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Convert.search();

		IMOperation op = new IMOperation();
		op.addImage();
		op.autoOrient();
		op.quality(60D);
		op.addImage();

		OverrideRunConvertCmd convertCmd = new OverrideRunConvertCmd();
		// ConvertCmd convertCmd = new ConvertCmd();
		// convertCmd.setInputProvider(pInputProvider);

		ArrayListOutputConsumer outputConsumer = new ArrayListOutputConsumer();
		// convertCmd.setOutputConsumer(outputConsumer);
		try {
			convertCmd.run(op, "-", "jpg:-");
		} catch(IM4JavaException | InterruptedException e) {
			throw new IOException(e);
		}
	}

}
