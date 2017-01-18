package org.fagu.fmv.soft.exec;

/**
 * @author Oodrive
 * @author f.agu
 * @created 18 janv. 2017 12:19:26
 */
public class EmptyReadLine implements ReadLine {

	public static final EmptyReadLine INSTANCE = new EmptyReadLine();

	/**
	 * 
	 */
	public EmptyReadLine() {}

	/**
	 * @see org.fagu.fmv.soft.exec.ReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {}

}
