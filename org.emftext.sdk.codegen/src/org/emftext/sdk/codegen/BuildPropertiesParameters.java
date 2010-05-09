package org.emftext.sdk.codegen;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.emftext.sdk.IPluginDescriptor;

public class BuildPropertiesParameters<ContextType> {
	
	private Collection<String> sourceFolders = new LinkedHashSet<String>();
	private Collection<String> binIncludes = new LinkedHashSet<String>();
	private IPluginDescriptor<ContextType> plugin;

	public BuildPropertiesParameters(IPluginDescriptor<ContextType> plugin) {
		super();
		this.plugin = plugin;
	}

	public Collection<String> getSourceFolders() {
		return sourceFolders;
	}

	public Collection<String> getBinIncludes() {
		return binIncludes;
	}

	public IPluginDescriptor<ContextType> getProject() {
		return plugin;
	}
}