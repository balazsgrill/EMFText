package org.emftext.sdk.codegen.generators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.emftext.sdk.EPlugins;
import org.emftext.sdk.codegen.EArtifact;
import org.emftext.sdk.codegen.GenerationContext;

public class AntlrPluginManifestGenerator extends ManifestGenerator {

	public AntlrPluginManifestGenerator(GenerationContext context) {
		super(context);
	}

	@Override
	protected Collection<String> getExportedPackages(GenerationContext context) {
		Set<String> exports = new LinkedHashSet<String>();
		
		// export the generated packages
		exports.add(context.getPackageName(EArtifact.PACKAGE_ANTLR_RUNTIME));
		exports.add(context.getPackageName(EArtifact.PACKAGE_ANTLR_RUNTIME_DEBUG));
		exports.add(context.getPackageName(EArtifact.PACKAGE_ANTLR_RUNTIME_MISC));
		exports.add(context.getPackageName(EArtifact.PACKAGE_ANTLR_RUNTIME_TREE));
		return exports;
	}

	@Override
	protected Collection<String> getRequiredBundles(GenerationContext context) {
		return Collections.emptyList();
	}

	@Override
	protected EPlugins getPlugin() {
		return EPlugins.ANTLR_PLUGIN;
	}
}