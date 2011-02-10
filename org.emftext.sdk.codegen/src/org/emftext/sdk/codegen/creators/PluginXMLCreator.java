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
package org.emftext.sdk.codegen.creators;

import java.io.File;
import java.util.Collection;

import org.emftext.sdk.IPluginDescriptor;
import org.emftext.sdk.codegen.IContext;
import org.emftext.sdk.codegen.IGenerator;
import org.emftext.sdk.codegen.generators.PluginXMLGenerator;
import org.emftext.sdk.codegen.parameters.XMLParameters;

/**
 * Creates a plugin.xml file using the content provided by the 
 * PluginXMLGenerator class.
 */
public class PluginXMLCreator<ContextType extends IContext<ContextType>> extends GenericArtifactCreator<ContextType, XMLParameters<ContextType>> {

	public static final String FILENAME = "plugin.xml";
	
	private boolean override;

	public PluginXMLCreator(XMLParameters<ContextType> parameters, boolean override) {
		super(parameters);
		this.override = override;
	}

	@Override
	public Collection<IArtifact> getArtifactsToCreate(IPluginDescriptor plugin, ContextType context, XMLParameters<ContextType> parameters) {
		IPluginDescriptor targetPlugin = parameters.getPlugin();
		File project = context.getFileSystemConnector().getProjectFolder(targetPlugin);
		File pluginXMLFile = new File(project.getAbsolutePath() + File.separator + FILENAME);

		IGenerator<ContextType, XMLParameters<ContextType>> generator = new PluginXMLGenerator<ContextType>();

	    return createArtifact(
	    		context,
	    		parameters,
	    		generator,
	    		pluginXMLFile,
	    		"Exception while generating " + FILENAME + " file."
	    );
	}

	@Override
	public boolean doOverride(ContextType context) {
		return override;
	}
}
