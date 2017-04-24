package org.fagu.fmv.ffmpeg;

import org.fagu.fmv.utils.PropertyValue;
import org.fagu.fmv.utils.PropertyValues.IntegerPropertyValue;
import org.fagu.fmv.utils.PropertyValues.StringPropertyValue;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 avr. 2017 10:32:04
 */
public class Properties {

	public static final String PROPERTY_PREFIX = "fmv.ffmpeg.";

	public static final PropertyValue<String> X264_PRESET = new StringPropertyValue(PROPERTY_PREFIX + "x264.preset", "medium");

	public static final PropertyValue<Integer> X264_CRF = new IntegerPropertyValue(PROPERTY_PREFIX + "x264.crf", 22);

	private Properties() {}

}
