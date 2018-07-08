package org.fagu.fmv.mymedia.movie.age;

import java.util.Map;
import java.util.Optional;


/**
 * @author Utilisateur
 * @created 7 juil. 2018 12:09:42
 */
public interface NameValidator {

	Optional<String> getMostValid(String title, Map<String, Ages> names);
}
