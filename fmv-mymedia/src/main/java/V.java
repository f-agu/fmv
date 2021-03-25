import java.io.File;

import org.fagu.fmv.ffmpeg.FFHelper;


public class V {

	public static void main(String[] args) throws Exception {
		File folder = new File("C:\\Personnel\\TODO\\Charge mentale\\1");
		File destFolder = new File("C:\\Personnel\\TODO\\Charge mentale\\audio");
		for(File f1 : folder.listFiles()) {
			for(File f2 : f1.listFiles()) {
				FFHelper.extractAudios(f2);
			}
			for(File f : f1.listFiles(f -> f.getName().endsWith(".mp3"))) {
				File dest = new File(destFolder, f1.getName());
				dest.mkdirs();
				dest = new File(dest, f.getName());
				f.renameTo(dest);
			}
		}
	}

}
