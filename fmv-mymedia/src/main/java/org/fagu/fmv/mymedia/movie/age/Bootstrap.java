package org.fagu.fmv.mymedia.movie.age;

import java.util.Optional;

import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.age.AgesCache.AgesElement;


/**
 * @author f.agu
 * @created 6 juil. 2018 23:04:37
 */
public class Bootstrap {

	public static void main(String[] args) {
		AgesCache agesCache = AgesCache.getInstance();
		AgesFilm agesFilm = new AgesFilm(Loggers.noOperation(), agesCache);
		for(String title : agesCache.getTitles()) {
			AgesElement agesElement = agesCache.find(title).get();
			if( ! agesElement.isPresent()) {
				// System.out.println(title);
				Optional<Ages> ages = agesFilm.getAges(title);
				if(ages.isPresent()) {
					agesElement = AgesElement.knowAges(title, ages.get());
				} else {
					agesElement = AgesElement.unknowAges(title, true);
				}
			}
			System.out.println(agesElement.toString());
		}
	}

}
