package org.fagu.fmv.mymedia.utils;

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

import java.util.Scanner;

import org.fagu.fmv.utils.io.UnclosedInputStream;


/**
 * @author Utilisateur
 * @created 13 mai 2018 14:37:36
 */
public class ScannerHelper {

	private ScannerHelper() {}

	/**
	 * @param question
	 * @return
	 */
	public static boolean yesNo(String question) {
		System.out.print(question + " ? [y/n] ");
		try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
			String line = null;
			while((line = scanner.nextLine()) != null) {
				line = line.trim().toLowerCase();
				if("y".equals(line) || "yes".equals(line)) {
					return true;
				} else if("n".equals(line) || "no".equals(line)) {
					return false;
				}
			}
		}
		return false;

	}

}
