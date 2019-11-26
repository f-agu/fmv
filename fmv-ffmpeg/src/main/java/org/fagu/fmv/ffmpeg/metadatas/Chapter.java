package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class Chapter extends InfoBase {

	protected Chapter(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public String getName() {
		return "chapter";
	}

	public Optional<Long> id() {
		return getLong("id");
	}

	public Optional<Fraction> timeBase() {
		return getFraction("time_base");
	}

	public Optional<Long> start() {
		return getLong("start");
	}

	public Optional<Long> end() {
		return getLong("end");
	}

	public Optional<Time> endTime() {
		return getTime("end_time");
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		String title = title().orElse(null);
		if(title != null) {
			buf.append(title).append('=');
		}
		Time startTime = startTime().orElse(null);
		Time endTime = endTime().orElse(null);
		if(startTime != null && endTime != null) {
			buf.append(startTime).append('>').append(endTime);
		} else {
			Optional<Long> start = start();
			Optional<Long> end = end();
			if(start.isPresent() && end.isPresent()) {
				buf.append(start.get()).append('>').append(end.get());
			}
		}
		return buf.toString();
	}
}
