package org.reuseware.emftextedit.runtime.ui;

import org.eclipse.emf.ecore.resource.Resource;

public interface SaveListener {
	public void savePerformed(Resource resource);
}
