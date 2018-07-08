package org.fagu.fmv.mymedia.movie.age.validator;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.util.concurrent.Future;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * @author f.agu
 * @created 7 juil. 2018 14:07:12
 */
public class ListFrame extends JFrame {

	private static final long serialVersionUID = - 6907981675697323183L;

	private final WritableFuture<String> writableFuture;

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public ListFrame(String title) throws HeadlessException {
		writableFuture = new WritableFuture<>();
		setTitle("Select near name");
		setSize(600, 400);
		setLocationRelativeTo(null);// center
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

		JLabel label = new JLabel(title);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(label);
		setVisible(true);
	}

	public void addButton(String text, String returnedValue) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		getContentPane().add(button);
		button.addActionListener(e -> {
			writableFuture.setValue(returnedValue);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});
	}

	public Future<String> getSelectedValue() {
		return writableFuture;
	}

}
