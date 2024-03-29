package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.util.function.Consumer;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;


/**
 * @author f.agu
 * @created 23 janv. 2017 09:41:34
 */
public interface MetadatasBuilder<M extends Metadatas, B extends MetadatasBuilder<M, ?>> {

	B soft(Soft identifySoft);

	B customizeExecutor(Consumer<SoftExecutor> customizeExecutor);

	M extract() throws IOException;
}
