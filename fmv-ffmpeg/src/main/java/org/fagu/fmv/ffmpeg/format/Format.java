package org.fagu.fmv.ffmpeg.format;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fagu.fmv.ffmpeg.ElementParameterized;
import org.fagu.fmv.ffmpeg.flags.Avioflags;
import org.fagu.fmv.ffmpeg.flags.Fdebug;
import org.fagu.fmv.ffmpeg.flags.Fflags;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.Processor;


/**
 * @author f.agu
 */
public abstract class Format<M> extends ElementParameterized<M> {

	private final Set<Avioflags> avioflagss;

	private final Set<Fflags> fflagss;

	private final Set<Fdebug> fdebugs;

	/**
	 * @param name
	 */
	public Format(String name) {
		super(name);
		avioflagss = new HashSet<>();
		fflagss = new HashSet<>();
		fdebugs = new HashSet<>();
	}

	/**
	 * (default 0)
	 * 
	 * @param avioflags
	 * @return
	 */
	public M avioflags(Avioflags... avioflagss) {
		return avioflags(Arrays.asList(avioflagss));
	}

	/**
	 * (default 0)
	 * 
	 * @param avioflags
	 * @return
	 */
	public M avioflags(Collection<Avioflags> avioflagss) {
		avioflagss.stream().filter(f -> getIO().accept(f.io())).forEach(f -> this.avioflagss.add(f));
		return getMThis();
	}

	/**
	 * (default 200)
	 * 
	 * @param fflags
	 * @return
	 */
	public M fflags(Fflags... fflagss) {
		return fflags(Arrays.asList(fflagss));
	}

	/**
	 * (default 200)
	 * 
	 * @param fflags
	 * @return
	 */
	public M fflags(Collection<Fflags> fflagss) {
		fflagss.stream().filter(f -> getIO().accept(f.io())).forEach(f -> this.fflagss.add(f));
		return getMThis();
	}

	/**
	 * Print specific debug info (default 0)
	 * 
	 * @param fdebug
	 * @return
	 */
	public M fdebug(Fdebug... fdebugs) {
		return fdebug(Arrays.asList(fdebugs));
	}

	/**
	 * Print specific debug info (default 0)
	 * 
	 * @param fdebug
	 * @return
	 */
	public M fdebug(Collection<Fdebug> fdebugs) {
		fdebugs.stream().filter(f -> getIO().accept(f.io())).forEach(f -> this.fdebugs.add(f));
		return getMThis();
	}

	/**
	 * Maximum muxing or demuxing delay in microseconds (from -1 to INT_MAX) (default -1)
	 * 
	 * @param maxDelay
	 * @return
	 */
	public M maxDelay(int maxDelay) {
		if(maxDelay < - 1) {
			throw new IllegalArgumentException("maxDelay must be at least -1: " + maxDelay);
		}
		parameter("-max_delay", Integer.toString(maxDelay));
		return getMThis();
	}

	/**
	 * How strictly to follow the standards (from INT_MIN to INT_MAX) (default 0)
	 * 
	 * @param strict
	 * @return
	 */
	public M strict(Strict strict) {
		parameter("-strict", strict.flag());
		return getMThis();
	}

	/**
	 * Set information dump field separator (default ", ")
	 * 
	 * @param dumpSeparator
	 * @return
	 */
	public M dumpSeparator(String dumpSeparator) {
		parameter("-dump_separator", dumpSeparator);
		return getMThis();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		super.eventAdded(processor, ioEntity);
		if( ! avioflagss.isEmpty()) {
			parameter(processor, ioEntity, "-avioflags", avioflagss);
		}
		if( ! fflagss.isEmpty()) {
			parameter(processor, ioEntity, "-fflags", fflagss);
		}
		if( ! fdebugs.isEmpty()) {
			parameter(processor, ioEntity, "-fdebug", fdebugs);
		}
	}

	// ***********************************************

	/**
	 * @return
	 */
	abstract public IO getIO();

}
