package org.fagu.fmv.soft.gs.exception;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
/**
 * <pre>
 * Process exited with an error: 1 (Exit value: 1)
 * gs "-sDEVICE=png16m" "-sOutputFile=cheese1543550052778276050.zip.jpg" -r200 -dNOPAUSE -dBATCH -dSAFER "-dFirstPage=1" "-dLastPage=1" -dUseCropBox cheese1543550052778276050.zip
 * GPL Ghostscript 9.16 (2015-03-30)
 * Copyright (C) 2015 Artifex Software, Inc.  All rights reserved.
 * This software comes with NO WARRANTY: see the file PUBLIC for details.
 * Error: /undefined in PK
 * GPL Ghostscript 9.16: Unrecoverable error, exit code 1
 * Operand stack:
 * 
 * Execution stack:
 *    %interp_exit   .runexec2   --nostringval--   --nostringval--   --nostringval--   2   %stopped_push   --nostringval--   --nostringval--   --nostringval--   false   1   %stopped_push   1983   1   3   %oparray_pop   1982   1   3   %oparray_pop   1966   1   3   %oparray_pop   1852   1   3   %oparray_pop   --nostringval--   %errorexec_pop   .runexec2   --nostringval--   --nostringval--   --nostringval--   2   %stopped_push   --nostringval--
 * Dictionary stack:
 *    --dict:1200/1684(ro)(G)--   --dict:0/20(G)--   --dict:78/200(L)--
 * Current allocation mode is local
 * Current file position is 3 (Exit value: 1. Caused by org.apache.commons.exec.ExecuteException: Process exited with an error: 1 (Exit value: 1))
 * </pre>
 * 
 * @author f.agu
 */
public class UndefinedFormatExceptionKnownAnalyzer extends GSExceptionKnownAnalyzer {

	public UndefinedFormatExceptionKnownAnalyzer() {
		super("Undefined format", "Error: /undefined in ");
	}

}
