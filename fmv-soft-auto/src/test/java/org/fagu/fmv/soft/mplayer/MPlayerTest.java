package org.fagu.fmv.soft.mplayer;

import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.LogExecuteDelegate;
import org.fagu.fmv.soft.find.SoftFound;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author fagu
 */
@Ignore
public class MPlayerTest {

	@Test
	public void testSearch() {
		ExecuteDelegateRepository.set(new LogExecuteDelegate(System.out::println));
		MPlayer.search();
	}

	@Test
	public void testMPlayer() {
		for(SoftFound softFound : MPlayer.search().getFounds()) {
			System.out.println(softFound);
		}
	}

	@Test
	public void testMEncoder() {
		for(SoftFound softFound : MEncoder.search().getFounds()) {
			System.out.println(softFound);
		}
	}

}
