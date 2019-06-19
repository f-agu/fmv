package org.fagu.fmv.soft.mplayer;

import java.util.Collection;


/**
 * @author fagu
 */
public interface SelectTitlesPolicy {

	Collection<MPlayerTitle> select(Collection<MPlayerTitle> titles);
}
