package org.fagu.fmv.soft.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.lang3.mutable.MutableObject;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.ReadLine;


/**
 * @author f.agu
 */
public class ErrorReadLine implements ReadLine {

	private final int maxLineBuffer;

	private final int killProcessWhenNumberOfLines;

	private int countErrLines;

	private ProcessDestroyer processDestroyer;

	private MutableObject<Process> processMutableObject;

	private List<String> errList;

	/**
	 * @param maxLineBuffer
	 * @param killProcessWhenNumberOfLines
	 */
	public ErrorReadLine(int maxLineBuffer, int killProcessWhenNumberOfLines) {
		this.maxLineBuffer = maxLineBuffer;
		this.killProcessWhenNumberOfLines = killProcessWhenNumberOfLines;
		errList = new ArrayList<>(maxLineBuffer);

		if(killProcessWhenNumberOfLines > 1) {
			processMutableObject = new MutableObject<>();
			processDestroyer = createProcessDestroyer(processMutableObject);
		}
	}

	/**
	 * @see org.fagu.fmv.utils.exec.ReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		++countErrLines;
		if(countErrLines < maxLineBuffer) {
			errList.add(line);
		} else if(countErrLines == maxLineBuffer) {
			errList.add("...");
		}
		if(countErrLines == killProcessWhenNumberOfLines && processMutableObject != null) {
			Process process = processMutableObject.getValue();
			if(process != null) {
				process.destroyForcibly();
			}
		}
	}

	/**
	 * @return
	 */
	public List<String> getErrList() {
		return errList;
	}

	/**
	 * @param executor
	 */
	public ErrorReadLine applyProcessDestroyer(FMVExecutor executor) {
		if(processDestroyer != null) {
			executor.addProcessDestroyer(processDestroyer);
		}
		return this;
	}

	// *******************************************************************

	/**
	 * @param mutableObject
	 * @return
	 */
	private static ProcessDestroyer createProcessDestroyer(MutableObject<Process> mutableObject) {
		return new ProcessDestroyer() {

			/**
			 * @see org.apache.commons.exec.ProcessDestroyer#size()
			 */
			@Override
			public int size() {
				return 0;
			}

			/**
			 * @see org.apache.commons.exec.ProcessDestroyer#remove(java.lang.Process)
			 */
			@Override
			public boolean remove(Process process) {
				return false;
			}

			/**
			 * @see org.apache.commons.exec.ProcessDestroyer#add(java.lang.Process)
			 */
			@Override
			public boolean add(Process process) {
				mutableObject.setValue(process);
				return false;
			}
		};
	}

}
