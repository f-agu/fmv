package org.fagu.fmv.mymedia.classify.image;

/*
 * #%L
 * fmv-mymedia
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.IMIdentifyImageMetadatas;


/**
 * @author f.agu
 */
public class TimeOffsetImageComparator implements ImageTimeComparator {

	private final Map<Predicate<IMIdentifyImageMetadatas>, Long> filterMap = new LinkedHashMap<>();

	/**
	 * 
	 */
	public TimeOffsetImageComparator() {}

	/**
	 * @param filter
	 * @param timeDiff
	 */
	public void addFilter(Predicate<IMIdentifyImageMetadatas> filter, long timeDiff) {
		filterMap.put(filter, timeDiff);
	}

	/**
	 * @param device
	 * @param model
	 * @param timeDiff
	 */
	public void addDevice(String device, String model, long timeDiff) {
		addFilter(new DeviceImageMetadatasFilter(device, model), timeDiff);
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.image.ImageTimeComparator#getTime(org.fagu.fmv.im.Image)
	 */
	@Override
	public long getTime(Image image) {
		long time = 0;
		for(Entry<Predicate<IMIdentifyImageMetadatas>, Long> entry : filterMap.entrySet()) {
			if(entry.getKey().test(image.getMetadatas())) {
				time += entry.getValue();
			}
		}
		return image.getTime() + time;
	}
}
