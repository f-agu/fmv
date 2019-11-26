package org.fagu.fmv.ffmpeg.filter.impl;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterCombined;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OperationListener;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.soft.FFInfo;
import org.fagu.fmv.ffmpeg.soft.FFMpeg;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class AutoRotate extends FilterCombined {

	/**
	 * @param name
	 */
	private AutoRotate(Rotate rotate) {
		super("auto-rotate", Collections.singletonList(rotate));
	}

	/**
	 * @param movieMetadatas
	 * @return
	 */
	public static AutoRotate create(MovieMetadatas movieMetadatas) {
		Rotation rotation = Rotation.R_0;
		if(movieMetadatas != null && ! isAutoRotateObsolete()) {
			VideoStream videoStream = movieMetadatas.getVideoStream();
			if(videoStream != null) {
				rotation = videoStream.rotation();
			}
		}
		return new AutoRotate(Rotate.create(rotation));
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.AbstractFilter#upgradeOutputProcessor(org.fagu.fmv.ffmpeg.operation.OutputProcessor)
	 */
	@Override
	public void upgradeOutputProcessor(OutputProcessor outputProcessor) {
		Transpose.addMetadataRotate(outputProcessor, Rotation.R_0);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

	/**
	 * @return
	 */
	public static boolean isAutoRotateObsolete() {
		return isAutoRotateObsolete(FFMpeg.search());
	}

	/**
	 * @param soft
	 * @return
	 */
	public static boolean isAutoRotateObsolete(Soft soft) {
		SoftFound softFound = soft.getFirstFound();
		FFInfo ffInfo = (FFInfo)softFound.getSoftInfo();
		// version
		Version version = ffInfo.getVersion().orElse(null);
		if(version != null) {
			return version.isUpperOrEqualsThan(new Version(2, 7));
		}
		// build number
		Integer builtVersion = ffInfo.getBuiltVersion();
		if(builtVersion != null) {
			return builtVersion.intValue() > 73010;
		}
		// build date
		Date builtDate = ffInfo.getBuiltDate();
		if(builtDate != null) {
			@SuppressWarnings("deprecation")
			Date minDate = new Date(2015 - 1900, 6 - 1, 12);
			return minDate.before(builtDate);
		}

		return false;
	}
	// *******************************************

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterCombined#beforeAddAround(org.fagu.fmv.ffmpeg.operation.Operation,
	 *      org.fagu.fmv.ffmpeg.filter.FilterNaming)
	 */
	@Override
	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {
		super.beforeAddAround(operation, filterNaming);
		operation.addListener(new OperationListener() {

			@Override
			public void eventPreToArguments(Operation<?, ?> operation) {
				upgrade(operation);
			}
		});
	}

}
