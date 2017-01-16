package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author f.agu
 */
public enum MediaType {

	/**
	 *
	 */
	AUDIO() {

		/**
		 * @see org.fagu.fmv.core.project.MediaType#addType(org.fagu.fmv.core.project.MediaType)
		 */
		@Override
		protected MediaType addType(MediaType mediaType) {
			switch(mediaType) {
				case VIDEO:
				case AUDIO_VIDEO:
					return AUDIO_VIDEO;
				case AUDIO:
				default:
			}
			return null;
		}
	},
	/**
	 *
	 */
	VIDEO() {

		/**
		 * @see org.fagu.fmv.core.project.MediaType#addType(org.fagu.fmv.core.project.MediaType)
		 */
		@Override
		protected MediaType addType(MediaType mediaType) {
			switch(mediaType) {
				case AUDIO:
				case AUDIO_VIDEO:
					return AUDIO_VIDEO;
				case VIDEO:
				default:
			}
			return null;
		}
	},
	/**
	 *
	 */
	AUDIO_VIDEO(AUDIO, VIDEO) {

		/**
		 * @see org.fagu.fmv.core.project.MediaType#addType(org.fagu.fmv.core.project.MediaType)
		 */
		@Override
		protected MediaType addType(MediaType mediaType) {
			return null;
		}
	};

	private final List<MediaType> depends;

	/**
	 *
	 */
	private MediaType() {
		depends = Collections.emptyList();
	}

	/**
	 * @param mediaTypes
	 */
	private MediaType(MediaType... mediaTypes) {
		if(mediaTypes.length == 0) {
			throw new IllegalArgumentException();
		}
		depends = Arrays.asList(mediaTypes);
	}

	/**
	 * @return the depends
	 */
	public List<MediaType> depends() {
		return depends;
	}

	/**
	 * @param mediaType
	 * @return
	 */
	public MediaType add(MediaType mediaType) {
		if(mediaType == null || mediaType == this) {
			return this;
		}
		MediaType type = addType(mediaType);
		return type == null ? this : type;
	}

	// **************************************************

	/**
	 * @param mediaType
	 * @return
	 */
	abstract protected MediaType addType(MediaType mediaType);

}
