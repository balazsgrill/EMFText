package org.emftext.sdk.codegen.generators;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.emftext.sdk.EPlugins;
import org.emftext.sdk.codegen.EArtifact;
import org.emftext.sdk.codegen.GenerationContext;
import org.emftext.sdk.codegen.OptionManager;
import org.emftext.sdk.codegen.util.ConcreteSyntaxUtil;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.Import;
import org.emftext.sdk.concretesyntax.OptionTypes;

public class ResourcePluginManifestGenerator extends ManifestGenerator {

	private final ConcreteSyntaxUtil csUtil = new ConcreteSyntaxUtil();

	public ResourcePluginManifestGenerator(GenerationContext context) {
		super(context);
	}

	@Override
	protected Collection<String> getExportedPackages(GenerationContext context) {
		ConcreteSyntax syntax = context.getConcreteSyntax();
		
		Set<String> exports = new LinkedHashSet<String>();
		
		// export the generated packages
		exports.add(context.getPackageName(EArtifact.PACKAGE_ROOT));
		exports.add(context.getPackageName(EArtifact.PACKAGE_CC));
		exports.add(context.getPackageName(EArtifact.PACKAGE_MOPP));
		exports.add(context.getPackageName(EArtifact.PACKAGE_UI));
		exports.add(context.getPackageName(EArtifact.PACKAGE_UTIL));
		// do not export the analysis package if the are no resolvers
		if (csUtil.getResolverFileNames(syntax).size() > 0) {
			exports.add(context.getPackageName(EArtifact.PACKAGE_ANALYSIS));
		}
		return exports;
	}

	@Override
	protected Collection<String> getRequiredBundles(GenerationContext context) {
		ConcreteSyntax syntax = context.getConcreteSyntax();
		
		Set<String> imports = new LinkedHashSet<String>();
		imports.add("org.eclipse.core.resources");
		imports.add("org.emftext.access;resolution:=optional");
		imports.add("org.eclipse.emf");
		imports.add("org.eclipse.emf.codegen.ecore");
		imports.add("org.eclipse.emf.ecore");
		imports.add("org.eclipse.emf.ecore.edit");
		imports.add("org.eclipse.emf.edit.ui");
		imports.add("org.eclipse.emf.workspace");
		imports.add("org.eclipse.jface");
		imports.add("org.eclipse.jface.text");
		imports.add("org.eclipse.ui");
		imports.add("org.eclipse.ui.editors");
		imports.add("org.eclipse.ui.ide");
		imports.add("org.eclipse.ui.views");
		
		String qualifiedBasePluginName = 
			OptionManager.INSTANCE.getStringOptionValue(syntax, OptionTypes.BASE_RESOURCE_PLUGIN);
		if (qualifiedBasePluginName != null) {
			imports.add(qualifiedBasePluginName);
		}
		
		if (context.isGenerateTestActionEnabled()) {
			imports.add("org.emftext.sdk.ui");
		}

		addImports(imports, syntax);
		
		// remove the current plug-in, because we do not
		// need to import it
		imports.remove(EPlugins.RESOURCE_PLUGIN.getName(syntax));
		
		return imports;
	}

	private void addImports(Collection<String> requiredBundles,
			ConcreteSyntax syntax) {

		// first add the syntax itself
		String syntaxPluginID = EPlugins.RESOURCE_PLUGIN.getName(syntax);
		requiredBundles.add(syntaxPluginID);
		String antlrPluginID = EPlugins.ANTLR_PLUGIN.getName(syntax);
		requiredBundles.add(antlrPluginID);
		
		// second add the main generator package
		GenPackage mainPackage = syntax.getPackage();
		addImports(requiredBundles, mainPackage);
		
		// third add imported generator packages and syntaxes recursively
		for (Import nextImport : syntax.getImports()) {
			GenPackage importedPackage = nextImport.getPackage();
			addImports(requiredBundles, importedPackage);

			ConcreteSyntax importedSyntax = nextImport.getConcreteSyntax();
			if (importedSyntax != null) {
				addImports(requiredBundles, importedSyntax);
			}
		}
	}

	/**
	 * Adds imports for the given generator package and all used
	 * generator packages.
	 * 
	 * @param requiredBundles
	 * @param genPackage
	 * @return
	 */
	private GenModel addImports(Collection<String> requiredBundles, GenPackage genPackage) {
		// add the package itself
		addImport(requiredBundles, genPackage);
		
		// add used generator packages
		GenModel genModel = genPackage.getGenModel();
		for (GenPackage usedGenPackage : genModel.getUsedGenPackages()) {
			addImport(requiredBundles, usedGenPackage);
		}
		return genModel;
	}

	/**
	 * Adds one import for the given generator package.
	 * 
	 * @param requiredBundles
	 * @param genPackage
	 * @return
	 */
	private void addImport(Collection<String> requiredBundles, GenPackage genPackage) {
		GenModel genModel = genPackage.getGenModel();
		String modelPluginID = genModel.getModelPluginID();
		requiredBundles.add(modelPluginID);
	}


	@Override
	protected EPlugins getPlugin() {
		return EPlugins.RESOURCE_PLUGIN;
	}
}