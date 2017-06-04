package org.fagu.fmv.mymedia.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;

import javax.swing.filechooser.FileSystemView;

import org.junit.Test;


/**
 * @author fagu
 */
public class FileSystemTest {

	@Test
	public void test() throws IOException {
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		for(File f : File.listRoots()) {
			System.out.println(f);
			System.out.println("    SystemDisplayName: " + fileSystemView.getSystemDisplayName(f));
			System.out.println("    SystemTypeDescription: " + fileSystemView.getSystemTypeDescription(f));
			System.out.println("    SystemIcon: " + fileSystemView.getSystemIcon(f));
			System.out.println("    ComputerNode: " + fileSystemView.isComputerNode(f));
			System.out.println("    isDrive: " + fileSystemView.isDrive(f));
			System.out.println("    isFileSystem: " + fileSystemView.isFileSystem(f));
			System.out.println("    isFileSystemRoot: " + fileSystemView.isFileSystemRoot(f));
			System.out.println("    isFloppyDrive: " + fileSystemView.isFloppyDrive(f));
			System.out.println("    isHiddenFile: " + fileSystemView.isHiddenFile(f));
			System.out.println("    isRoot: " + fileSystemView.isRoot(f));
			System.out.println("    isTraversable: " + fileSystemView.isTraversable(f));
			try {
				FileStore store = Files.getFileStore(f.toPath());
				System.out.println("    storeName: " + store.name());
				System.out.println("    storeType: " + store.type());
				System.out.println("    isReadOnly: " + store.isReadOnly());
			} catch(IOException e) {
				// ignore
			}
		}

	}
}
