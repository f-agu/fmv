package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
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

import java.util.Map;

import org.fagu.fmv.media.NavigableMapMetadatasContainer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:55:09
 */
public abstract class MapImageMetadatas extends NavigableMapMetadatasContainer implements ImageMetadatas {

	private final long createTime;

	protected MapImageMetadatas(Map<String, Object> metadatas) {
		super(metadatas);
		createTime = System.currentTimeMillis();
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

}
