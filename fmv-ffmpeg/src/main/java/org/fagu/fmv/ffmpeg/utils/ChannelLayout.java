package org.fagu.fmv.ffmpeg.utils;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.LinesFFMPEGOperation;
import org.fagu.fmv.ffmpeg.utils.AvailableHelp.Reader;


/**
 * ffmpeg -v quiet -layouts<br>
 * 
 * @author f.agu
 */
public class ChannelLayout {

	private static final HelpCache<ChannelLayout, ChannelLayoutHelp> HELP_CACHE = new HelpCache<>(runnable(), ChannelLayout::new);

	/**
	 * Same channel layout
	 */
	public static final ChannelLayout SAME = new ChannelLayout("same");

	/**
	 * FL+FR+LFE
	 */
	public static final ChannelLayout _2_1 = new ChannelLayout("2.1");

	/**
	 * FL+FR+FC
	 */
	public static final ChannelLayout _3_0 = new ChannelLayout("3.0");

	/**
	 * FL+FR+BC
	 */
	public static final ChannelLayout _3_0_BACK = new ChannelLayout("3.0(back)");

	/**
	 * FL+FR+FC+LFE
	 */
	public static final ChannelLayout _3_1 = new ChannelLayout("3.1");

	/**
	 * FL+FR+FC+BC
	 */
	public static final ChannelLayout _4_0 = new ChannelLayout("4.0");

	/**
	 * FL+FR+FC+LFE+BC
	 */
	public static final ChannelLayout _4_1 = new ChannelLayout("4.1");

	/**
	 * FL+FR+FC+BL+BR
	 */
	public static final ChannelLayout _5_0 = new ChannelLayout("5.0");

	/**
	 * FL+FR+FC+SL+SR
	 */
	public static final ChannelLayout _5_0_SIDE = new ChannelLayout("5.0(side)");

	/**
	 * FL+FR+FC+LFE+BL+BR
	 */
	public static final ChannelLayout _5_1 = new ChannelLayout("5.1");

	/**
	 * FL+FR+FC+LFE+SL+SR
	 */
	public static final ChannelLayout _5_1_SIDE = new ChannelLayout("5.1(side)");

	/**
	 * FL+FR+FC+BC+SL+SR
	 */
	public static final ChannelLayout _6_0 = new ChannelLayout("6.0");

	/**
	 * FL+FR+FLC+FRC+SL+SR
	 */
	public static final ChannelLayout _6_0_FRONT = new ChannelLayout("6.0(front)");

	/**
	 * FL+FR+FC+LFE+BC+SL+SR
	 */
	public static final ChannelLayout _6_1 = new ChannelLayout("6.1");

	/**
	 * FL+FR+LFE+FLC+FRC+SL+SR
	 */
	public static final ChannelLayout _6_1_FRONT = new ChannelLayout("6.1(front)");

	/**
	 * FL+FR+FC+BL+BR+SL+SR
	 */
	public static final ChannelLayout _7_0 = new ChannelLayout("7.0");

	/**
	 * FL+FR+FC+FLC+FRC+SL+SR
	 */
	public static final ChannelLayout _7_0_FRONT = new ChannelLayout("7.0(front)");

	/**
	 * FL+FR+FC+LFE+BL+BR+SL+SR
	 */
	public static final ChannelLayout _7_1 = new ChannelLayout("7.1");

	/**
	 * FL+FR+FC+LFE+BL+BR+FLC+FRC
	 */
	public static final ChannelLayout _7_1_WIDE = new ChannelLayout("7.1(wide)");

	/**
	 * FL+FR+FC+LFE+FLC+FRC+SL+SR
	 */
	public static final ChannelLayout _7_1_WIDE_SIDE = new ChannelLayout("7.1(wide-side)");

	/**
	 * back center
	 */
	public static final ChannelLayout BC = new ChannelLayout("BC");

	/**
	 * back left
	 */
	public static final ChannelLayout BL = new ChannelLayout("BL");

	/**
	 * back right
	 */
	public static final ChannelLayout BR = new ChannelLayout("BR");

	/**
	 * downmix left
	 */
	public static final ChannelLayout DL = new ChannelLayout("DL");

	/**
	 * downmix right
	 */
	public static final ChannelLayout DR = new ChannelLayout("DR");

	/**
	 * front center
	 */
	public static final ChannelLayout FC = new ChannelLayout("FC");

	/**
	 * front left
	 */
	public static final ChannelLayout FL = new ChannelLayout("FL");

	/**
	 * front left-of-center
	 */
	public static final ChannelLayout FLC = new ChannelLayout("FLC");

	/**
	 * front right
	 */
	public static final ChannelLayout FR = new ChannelLayout("FR");

	/**
	 * front right-of-center
	 */
	public static final ChannelLayout FRC = new ChannelLayout("FRC");

	/**
	 * low frequency
	 */
	public static final ChannelLayout LFE = new ChannelLayout("LFE");

	/**
	 * low frequency 2
	 */
	public static final ChannelLayout LFE2 = new ChannelLayout("LFE2");

	/**
	 * surround direct left
	 */
	public static final ChannelLayout SDL = new ChannelLayout("SDL");

	/**
	 * surround direct right
	 */
	public static final ChannelLayout SDR = new ChannelLayout("SDR");

	/**
	 * side left
	 */
	public static final ChannelLayout SL = new ChannelLayout("SL");

	/**
	 * side right
	 */
	public static final ChannelLayout SR = new ChannelLayout("SR");

	/**
	 * top back center
	 */
	public static final ChannelLayout TBC = new ChannelLayout("TBC");

	/**
	 * top back left
	 */
	public static final ChannelLayout TBL = new ChannelLayout("TBL");

	/**
	 * top back right
	 */
	public static final ChannelLayout TBR = new ChannelLayout("TBR");

	/**
	 * top center
	 */
	public static final ChannelLayout TC = new ChannelLayout("TC");

	/**
	 * top front center
	 */
	public static final ChannelLayout TFC = new ChannelLayout("TFC");

	/**
	 * top front left
	 */
	public static final ChannelLayout TFL = new ChannelLayout("TFL");

	/**
	 * top front right
	 */
	public static final ChannelLayout TFR = new ChannelLayout("TFR");

	/**
	 * wide left
	 */
	public static final ChannelLayout WL = new ChannelLayout("WL");

	/**
	 * wide right
	 */
	public static final ChannelLayout WR = new ChannelLayout("WR");

	/**
	 * DL+DR
	 */
	public static final ChannelLayout DOWNMIX = new ChannelLayout("downmix");

	/**
	 * FL+FR+FC+BL+BR+BC
	 */
	public static final ChannelLayout HEXAGONAL = new ChannelLayout("hexagonal");

	/**
	 * FC
	 */
	public static final ChannelLayout MONO = new ChannelLayout("mono");

	/**
	 * FL+FR+FC+BL+BR+BC+SL+SR
	 */
	public static final ChannelLayout OCTAGONAL = new ChannelLayout("octagonal");

	/**
	 * FL+FR+BL+BR
	 */
	public static final ChannelLayout QUAD = new ChannelLayout("quad");

	/**
	 * FL+FR+SL+SR
	 */
	public static final ChannelLayout QUAD_SIDE = new ChannelLayout("quad(side)");

	/**
	 * FL+FR
	 */
	public static final ChannelLayout STEREO = new ChannelLayout("stereo");

	private final String name;

	private final List<ChannelLayout> channelLayouts;

	/**
	 * @param channel
	 */
	protected ChannelLayout(String name) {
		this.name = name;
		this.channelLayouts = null;
	}

	/**
	 * @param channelLayouts
	 */
	protected ChannelLayout(List<ChannelLayout> channelLayouts) {
		this.name = null;
		this.channelLayouts = channelLayouts;
	}

	/**
	 * @param channels
	 * @return
	 */
	public static final ChannelLayout of(ChannelLayout... channels) {
		if(ArrayUtils.isEmpty(channels)) {
			throw new IllegalArgumentException("Need at least one channel layout");
		}
		if(channels.length == 1) {
			return channels[0];
		}
		return new ChannelLayout(Arrays.asList(channels));
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return cache().text;
	}

	/**
	 * @return
	 */
	public boolean isIndividual() {
		return cache().individual;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelLayouts == null) ? 0 : channelLayouts.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ChannelLayout other = (ChannelLayout)obj;
		if(channelLayouts == null) {
			if(other.channelLayouts != null)
				return false;
		} else if( ! channelLayouts.equals(other.channelLayouts))
			return false;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if( ! name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(channelLayouts != null) {
			return StringUtils.join(channelLayouts, '+');
		}
		return name;
	}

	// **************************************************

	/**
	 * @return
	 */
	public static boolean exists(String name) {
		return HELP_CACHE.exists(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public static ChannelLayout byName(String name) {
		return HELP_CACHE.byName(name);
	}

	/**
	 * @return
	 */
	public static Set<String> availableNames() {
		return HELP_CACHE.availableNames();
	}

	/**
	 * @return
	 */
	public static List<ChannelLayout> available() {
		return HELP_CACHE.available();
	}

	// **************************************************

	/**
	 * @return
	 */
	private ChannelLayoutHelp cache() {
		return HELP_CACHE.cache(name).get(0);
	}

	/**
	 * @return
	 */
	private static Runnable runnable() {
		return () -> {
			LinesFFMPEGOperation operation = new LinesFFMPEGOperation();
			operation.addParameter("-layouts");
			try {
				FFExecutor<List<String>> executor = new FFExecutor<>(operation);
				Consumer<ChannelLayoutHelp> cacheConsumer = HELP_CACHE.consumer();
				AvailableHelp<ChannelLayoutHelp> availableHelp = AvailableHelp.create();

				Reader individualReader = l -> {
					if(StringUtils.isBlank(l)) {
						return false;
					}
					String name = StringUtils.substringBefore(l, " ").trim();
					ChannelLayoutHelp channelLayoutHelp = new ChannelLayoutHelp(name, true);
					channelLayoutHelp.text = StringUtils.substringAfter(l, " ").trim();
					cacheConsumer.accept(channelLayoutHelp);
					return true;
				};
				Reader standardReader = l -> {
					if(StringUtils.isBlank(l)) {
						return false;
					}
					String name = StringUtils.substringBefore(l, " ").trim();
					ChannelLayoutHelp channelLayoutHelp = new ChannelLayoutHelp(name, false);
					channelLayoutHelp.text = StringUtils.substringAfter(l, " ").trim();
					cacheConsumer.accept(channelLayoutHelp);
					return true;
				};

				availableHelp.title().unreadLine().reader(individualReader).unreadLines(3).reader(standardReader).parse(executor.execute()
						.getResult());
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	// ---------------------------------------------

	/**
	 * @return
	 */
	private static class ChannelLayoutHelp extends Help {

		private final boolean individual;

		/**
		 * @param name
		 * @param individual
		 */
		protected ChannelLayoutHelp(String name, boolean individual) {
			super(name);
			this.individual = individual;
		}
	}

}
