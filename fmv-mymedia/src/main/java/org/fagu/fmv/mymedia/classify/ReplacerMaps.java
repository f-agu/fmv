package org.fagu.fmv.mymedia.classify;

/*
 * #%L
 * fmv-mymedia
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

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.classify.ByPatternClassifier.ReplacerMap;
import org.fagu.fmv.utils.Replacer;
import org.fagu.fmv.utils.Replacers;


/**
 * @author f.agu
 */
public class ReplacerMaps {

	private ReplacerMaps() {}

	public static <M extends Media> ReplacerMap<M> counterGlobal() {
		return counterGlobal(1);
	}

	public static <M extends Media> ReplacerMap<M> counterGlobal(final int start) {
		return new ReplacerMap<M>() {

			private int countTotal = 0;

			private int counter = start;

			@Override
			public void analyze(M media, ByPatternClassifier<?, M> byPatternClassifier) {
				++countTotal;
			}

			@Override
			public Replacer getReplacer(M media, String destPath, ByPatternClassifier<?, M> byPatternClassifier) {
				int cntLen = 1 + (int)Math.log10(countTotal + start);
				return Replacers.chain().map(Collections.singletonMap("counter", StringUtils.leftPad(Integer.toString(counter++), cntLen, '0')));
			}
		};
	}

	public static <M extends Media> ReplacerMap<M> counterByDay() {
		return new ReplacerMap<M>() {

			private Map<String, AtomicInteger> countTotal = new HashMap<>();

			private Map<String, AtomicInteger> counter = new HashMap<>();

			@Override
			public void analyze(M media, ByPatternClassifier<?, M> byPatternClassifier) {
				String key = getKey(media, byPatternClassifier);
				AtomicInteger atomicInteger = countTotal.get(key);
				if(atomicInteger == null) {
					countTotal.put(key, new AtomicInteger(1));
				} else {
					atomicInteger.incrementAndGet();
				}
			}

			@Override
			public Replacer getReplacer(M media, String destPath, ByPatternClassifier<?, M> byPatternClassifier) {
				String key = getKey(media, byPatternClassifier);
				AtomicInteger cntTot = countTotal.get(key);
				int cntLen = 1 + (int)Math.log10(cntTot.get());

				AtomicInteger cnt = counter.get(key);
				if(cnt == null) {
					cnt = new AtomicInteger(0);
					counter.put(key, cnt);
				}

				return Replacers.chain().map(Collections.singletonMap("counter", StringUtils.leftPad(Integer.toString(cnt.incrementAndGet()), cntLen,
						'0')));
			}

			// *****************************************

			private String getKey(M media, ByPatternClassifier<?, M> byPatternClassifier) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(byPatternClassifier.getMediaTimeComparator().getTime(media));
				StringBuilder buf = new StringBuilder();
				buf.append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DAY_OF_MONTH));
				return buf.toString();
			}
		};
	}
}
