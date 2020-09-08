/**
 * 
 */
package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


/**
 * @author fagu
 *
 */
public class CmdLineConfirmSelectTitlesPolicy implements SelectTitlesPolicy {

	private final SelectTitlesPolicy delegated;

	public CmdLineConfirmSelectTitlesPolicy(SelectTitlesPolicy delegated) {
		this.delegated = Objects.requireNonNull(delegated);
	}

	@Override
	public Collection<MPlayerTitle> select(Collection<MPlayerTitle> titles) {
		Collection<MPlayerTitle> select = delegated.select(titles);
		System.out.println();
		for(MPlayerTitle title : titles) {
			StringBuilder line = new StringBuilder();
			if(select.contains(title)) {
				line.append("  > ");
			} else {
				line.append("    ");
			}
			line.append(StringUtils.leftPad(Integer.toString(title.getNum()), 3));
			line.append("  ");
			line.append(title.getLength());
			System.out.println(line.toString());
		}
		Scanner scanner = new Scanner(System.in);
		System.out.println();
		System.out.print("Titles selected [" + select.stream().map(t -> Integer.toString(t.getNum())).collect(Collectors.joining(", ")) + "] ?");
		String line = scanner.nextLine();
		if(StringUtils.isNotBlank(line)) {
			select = parseLine(line, titles);
		}
		return select;
	}

	private Collection<MPlayerTitle> parseLine(String line, Collection<MPlayerTitle> titles) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		Set<Integer> nums = new HashSet<>();
		while(tokenizer.hasMoreTokens()) {
			try {
				int num = Integer.parseInt(tokenizer.nextToken().trim());
				nums.add(num);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return titles.stream()
				.filter(t -> nums.contains(t.getNum()))
				.collect(Collectors.toList());
	}

}
