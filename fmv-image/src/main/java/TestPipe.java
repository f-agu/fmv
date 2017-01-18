
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.fagu.fmv.im.IMOperation;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.im.Convert;


/**
 * @author f.agu
 */
public class TestPipe {

	public static void main(String[] args) throws Exception {
		new File("d:\\tmp\\out-pipe3.jpg").delete();

		Soft convertSoft = Convert.search();
		IMOperation op = new IMOperation();
		op.image("-").autoOrient().quality(60D).image("jpg:-");
		try (InputStream inputStream = new FileInputStream("d:\\tmp\\Nexway.png");
				OutputStream outputStream = new FileOutputStream("d:\\tmp\\out-pipe3.jpg")) {
			convertSoft.withParameters(op.toList())
					.logCommandLine(System.out::println)
					.input(inputStream)
					.out(outputStream)
					.err(System.out)
					.execute();
		}
	}

	/**
	 * @param args
	 */
	public static void main0(String[] args) throws Exception {

		new File("d:\\tmp\\out-pipe2.jpg").delete();

		ProcessBuilder pb = new ProcessBuilder()
				.command("C:\\Program Files\\ImageMagick-7.0.3-Q16\\magick.exe", "convert", "-", "-auto-orient", "-quality", "60.0", "jpg:-");
		// .redirectInput(Redirect.PIPE);
		// .redirectErrorStream(true);
		Process p = pb.start();

		// input
		new Thread(() -> {
			try (OutputStream outputStream = p.getOutputStream();
					FileInputStream inputStream = new FileInputStream(new File("d:\\tmp\\0.png"))) {
				IOUtils.copy(inputStream, outputStream);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}).start();

		InputStream inputStream = p.getInputStream();
		// ouput
		new Thread(() -> {

			try (OutputStream outputStream = new FileOutputStream("d:\\tmp\\out-pipe2.jpg")) {
				IOUtils.copy(inputStream, outputStream);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}).start();

		System.out.println(p.waitFor());
	}

}
