package org.fagu.fmv.core.project;

import org.fagu.fmv.utils.PropertyValue;
import org.fagu.fmv.utils.PropertyValues.BooleanPropertyValue;
import org.fagu.fmv.utils.PropertyValues.IntegerPropertyValue;


/**
 * @author f.agu
 */
public class Properties {

	public static final PropertyValue<Boolean> PREPARE_BACKGROUND = new BooleanPropertyValue("prepare.background", true);

	public static final PropertyValue<Boolean> SHOW_COMMAND_LINE = new BooleanPropertyValue("commandline.show", false);

	public static final PropertyValue<Integer> VIEW_LAST_MEDIA = new IntegerPropertyValue("view.last.media", null);

	public static final PropertyValue<Integer> PREVIEW_X264_CRF = new IntegerPropertyValue("preview.x264.crf", 23);

	public static final PropertyValue<Integer> PREPARE_MAKE_X264_CRF = new IntegerPropertyValue("make-prepare.x264.crf", 5);

	public static final PropertyValue<Integer> MAKE_X264_CRF = new IntegerPropertyValue("make.x264.crf", 20);

	/**
	 *
	 */
	private Properties() {}

}
