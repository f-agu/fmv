package org.fagu.fmv.mymedia.classify.image;

import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.ImageMetadatas;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.mymedia.classify.AskTimeOffsetComparator;
import org.fagu.fmv.mymedia.file.ImageFinder;


/**
 * @author f.agu
 */
public class AskTimeOffsetImageComparator extends AskTimeOffsetComparator<Image> implements ImageTimeComparator {

	/**
	 * @param imageFinder
	 */
	public AskTimeOffsetImageComparator(ImageFinder imageFinder) {
		super(imageFinder);
	}

	/**
	 * @param metadatas
	 * @return
	 */
	public String getMetadatasKey(Metadatas metadatas) {
		ImageMetadatas imgMetadatas = (ImageMetadatas)metadatas;
		StringBuilder key = new StringBuilder();
		String device = imgMetadatas.getDevice();
		String deviceModel = imgMetadatas.getDeviceModel();
		if(device != null) {
			key.append(device);
			if(deviceModel != null) {
				key.append('/');
			}
		}
		if(deviceModel != null) {
			key.append(deviceModel);
		}
		return key.toString();
	}

}
