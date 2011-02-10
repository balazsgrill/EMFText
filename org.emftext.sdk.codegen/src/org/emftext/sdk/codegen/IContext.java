/*******************************************************************************
 * Copyright (c) 2006-2011
 * Software Technology Group, Dresden University of Technology
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany 
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.codegen;

import java.io.File;

import org.emftext.sdk.IPluginDescriptor;

/**
 * An interface that defines methods which must be implemented by all
 * code generation contexts.
 */
public interface IContext<ContextType> {

	public IFileSystemConnector getFileSystemConnector();
	
	public IProblemCollector getProblemCollector();

	public File getFile(IPluginDescriptor plugin, ArtifactDescriptor<ContextType, ?> artifact);
}
