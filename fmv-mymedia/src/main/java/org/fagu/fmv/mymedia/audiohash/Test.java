package org.fagu.fmv.mymedia.audiohash;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;


/**
 * @author f.agu
 * @created 29 août 2022 16:39:06
 */
public class Test {

	public static void main(String... args) throws Exception {
		File file = new File("C:\\Personnel\\2022-08\\Musiques\\Futuroscope - 2022\\Abraham Inc. - It's not the same (Figure it out).mp3");
		try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file)) {
			System.out.println(audioInputStream);
			TargetDataLine.Info info = new DataLine.Info(TargetDataLine.class, audioInputStream.getFormat(),
					((int)audioInputStream.getFrameLength() * audioInputStream.getFormat().getFrameSize()));
			TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
			System.out.println(line);

		}
	}

}
