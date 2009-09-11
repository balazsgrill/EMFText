package org.emftext.runtime.ui.editor.bg_parsing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.text.DocumentEvent;
import org.emftext.runtime.resource.ITextResource;
import org.emftext.runtime.ui.editor.EMFTextEditor;

public class DelayedBackgroundParsingStrategy implements IBackgroundParsingStrategy {
	
	private static long DELAY = 1000;
	
	private Object parseLock = new Object();
	private Timer timer = new Timer();
	private TimerTask task;

	public boolean isParsingRequired(DocumentEvent event) {
		// parsing is always required
		return true;
	}

	public void parse(DocumentEvent event, final ITextResource resource, final EMFTextEditor editor) {
		final String contents = event.getDocument().get();

		synchronized (timer) {
			// cancel old task
			if (task != null) {
				task.cancel();
				// TODO stop current parser (if there is one)
			}
			timer.purge();
	
			// schedule new task
			task = new TimerTask() {
				
				@Override
				public void run() {
					synchronized (parseLock) {
						System.out.print("starting background parsing...");
						try {
							resource.reload(new ByteArrayInputStream(contents.getBytes()), null);
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("finished.");
						editor.notifyBackgroundParsingFinished();
					}
				}

				@Override
				public boolean cancel() {
					resource.cancelReload();
					return true;
				}
			};
			timer.schedule(task, DELAY);
		}
	}
}