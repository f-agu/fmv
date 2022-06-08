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

import java.util.function.Predicate;

import org.fagu.fmv.im.IMIdentifyImageMetadatas;


/**
 * @author f.agu
 */
public class DeviceImageMetadatasFilter implements Predicate<IMIdentifyImageMetadatas> {

	private final String device;

	private final String model;

	public DeviceImageMetadatasFilter(String device, String model) {
		this.device = device;
		this.model = model;
	}

	@Override
	public boolean test(IMIdentifyImageMetadatas t) {
		if(t == null) {
			return false;
		}
		return device.equals(t.getDevice()) && model.equals(t.getDeviceModel());
	}

}
