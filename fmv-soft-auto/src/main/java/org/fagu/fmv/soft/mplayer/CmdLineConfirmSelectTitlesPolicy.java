/**
 * 
 */
package org.fagu.fmv.soft.mplayer;

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

	/**
	 * @param delegated
	 */
	public CmdLineConfirmSelectTitlesPolicy(SelectTitlesPolicy delegated) {
		this.delegated = Objects.requireNonNull(delegated);
	}

	/**
	 * @see org.fagu.fmv.soft.mplayer.SelectTitlesPolicy#select(java.util.Collection)
	 */
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

	/**
	 * @param line
	 * @param titles
	 * @return
	 */
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