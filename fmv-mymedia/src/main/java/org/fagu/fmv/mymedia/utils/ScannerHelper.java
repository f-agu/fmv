package org.fagu.fmv.mymedia.utils;

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
		System.out.println(question + " ? [y/n] ");
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
