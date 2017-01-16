package org.fagu.fmv.cli.command;

/*
 * #%L
 * fmv-cli
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

import java.util.Optional;

import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.core.exec.BaseIdentifiable;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.filter.FadeAudioVideoFilterExec;
import org.fagu.fmv.ffmpeg.filter.impl.FadeType;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
@Command("fadeav")
public class FadeAudioVideo extends AbstractCommand {

	/**
	 * 
	 */
	public FadeAudioVideo() {
		super();
	}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length != 4) {
			help();
			return;
		}
		// TEST
		Optional<Identifiable> identifiableOpt = BaseIdentifiable.findById(project, args[1]);
		if( ! identifiableOpt.isPresent()) {
			println("not found: " + args[1]);
			return;
		}

		FadeType fadeType = FadeType.valueOf(args[0].toUpperCase());
		Time startTime = Time.parse(args[2]);
		Duration duration = Duration.parse(args[2]);

		Identifiable identifiable = identifiableOpt.get();
		FadeAudioVideoFilterExec filterExec = new FadeAudioVideoFilterExec(project, fadeType, startTime, duration);
		filterExec.add(identifiable);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Fade audio/video";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "fadeav <in/out> <id> <starttime> <duration>";
	}

}
