package org.fagu.fmv.soft;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.find.ExecSoftFoundFactory.ExecSoftFoundFactoryBuilder;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftProvider;


/**
 * @author f.agu
 * @created 5 déc. 2019 10:38:46
 */
public class SoftFile {

	private final String groupName;

	public SoftFile(String groupName) {
		this.groupName = Objects.requireNonNull(groupName);
	}

	public Soft with(String name, String url, String logMessage, String filePath, Consumer<String> logger) {
		AtomicReference<File> fileRef = new AtomicReference<>();
		SoftProvider softProvider = softProvider(name, url, logMessage, fileRef::get);

		if(StringUtils.isBlank(filePath)) {
			logger.accept(softProvider.getName() + " undefined");
			return notFound(softProvider);
		}
		File file = new File(filePath);
		if( ! file.exists()) {
			logger.accept("File not found : " + filePath);
			return notFound(softProvider);
		}
		if( ! file.canRead()) {
			logger.accept("File not readable : " + filePath);
			return notFound(softProvider);
		}
		fileRef.set(file);
		return new Soft(
				new Founds(softProvider.getName(), new TreeSet<>(Collections.singleton(SoftFound.found(file))), null, null),
				softProvider);
	}

	public SoftProvider softProvider(String name, String url, String logMessage, Supplier<File> fileSupplier) {
		return new SoftProvider(name, null) {

			@Override
			public SoftFoundFactory createSoftFoundFactory(Properties searchProperties, Consumer<ExecSoftFoundFactoryBuilder> builderConsumer) {
				return (f, l, p) -> {
					File file = fileSupplier.get();
					if(file == null) {
						return SoftFound.notFound();
					}
					return SoftFound.found(file);
				};
			}

			@Override
			public String getGroupName() {
				return groupName;
			}

			@Override
			public String getDownloadURL() {
				return url;
			}

			@Override
			public String getLogMessageIfNotFound() {
				return logMessage != null ? logMessage : super.getLogMessageIfNotFound();
			}
		};
	}

	// ************************************************

	private static Soft notFound(SoftProvider softProvider) {
		return new Soft(
				new Founds(softProvider.getName(), new TreeSet<>(Collections.singleton(SoftFound.notFound())), null, null),
				softProvider);
	}

}
