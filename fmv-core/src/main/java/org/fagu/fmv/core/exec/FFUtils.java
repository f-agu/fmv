package org.fagu.fmv.core.exec;

import org.fagu.fmv.core.project.Project;
import org.fagu.fmv.core.project.Properties;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.operation.OperationListener;


/**
 * @author f.agu
 */
public class FFUtils {

	/**
	 *
	 */
	private FFUtils() {}

	/**
	 * @param project
	 * @return
	 */
	public static FFMPEGExecutorBuilder builder(Project project) {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		if(project.getProperty(Properties.SHOW_COMMAND_LINE)) {
			builder.addListener(new OperationListener() {

				/**
				 * @see org.fagu.fmv.ffmpeg.operation.OperationListenerAdaptor#eventCreate(org.fagu.fmv.ffmpeg.executor.FFExecutor)
				 */
				@Override
				public void eventCreate(FFExecutor<?> ffExecutor) {
					try {
						System.out.println(ffExecutor.getCommandLine());
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return builder;
	}

}
