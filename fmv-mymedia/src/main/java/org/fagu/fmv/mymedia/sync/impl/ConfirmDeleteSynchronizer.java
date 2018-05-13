package org.fagu.fmv.mymedia.sync.impl;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.IOException;

import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Synchronizer;
import org.fagu.fmv.mymedia.utils.ScannerHelper;


/**
 * @author f.agu
 */
public class ConfirmDeleteSynchronizer extends WrappedSynchronizer {

	/**
	 * @param synchronizer
	 */
	public ConfirmDeleteSynchronizer(Synchronizer synchronizer) {
		super(synchronizer);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#delete(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public boolean delete(Item item) throws IOException {
		if(ScannerHelper.yesNo("> Delete " + item)) {
			return synchronizer.delete(item);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "confirm delete, " + super.toString();
	}

}
