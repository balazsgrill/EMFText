/*******************************************************************************
 * Copyright (c) 2006-2010 
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
package org.emftext.sdk.codegen.resource.creators;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.emftext.sdk.IPluginDescriptor;
import org.emftext.sdk.OptionManager;
import org.emftext.sdk.codegen.ArtifactDescriptor;
import org.emftext.sdk.codegen.IArtifactCreator;
import org.emftext.sdk.codegen.ICodeGenerationComponent;
import org.emftext.sdk.codegen.creators.BuildPropertiesCreator;
import org.emftext.sdk.codegen.creators.DotClasspathCreator;
import org.emftext.sdk.codegen.creators.DotProjectCreator;
import org.emftext.sdk.codegen.creators.FoldersCreator;
import org.emftext.sdk.codegen.creators.ManifestCreator;
import org.emftext.sdk.codegen.parameters.BuildPropertiesParameters;
import org.emftext.sdk.codegen.parameters.ClassPathParameters;
import org.emftext.sdk.codegen.parameters.ManifestParameters;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.codegen.resource.GeneratorUtil;
import org.emftext.sdk.codegen.resource.TextResourceArtifacts;
import org.emftext.sdk.codegen.util.NameUtil;
import org.emftext.sdk.concretesyntax.CompleteTokenDefinition;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.OptionTypes;
import org.emftext.sdk.util.ConcreteSyntaxUtil;

/**
 * A creator that uses multiple other creators to create
 * a resource plug-in from a CS specification and a meta 
 * model.
 */
public class ResourcePluginContentCreator extends AbstractPluginCreator<Object> {

	private final NameUtil nameUtil = new NameUtil();
	private ConcreteSyntaxUtil csUtil = new ConcreteSyntaxUtil();
	private GeneratorUtil genUtil = new GeneratorUtil();
	
	public ResourcePluginContentCreator(ICodeGenerationComponent parent) {
		super(parent);
	}
	
	public String getPluginName() {
		return "resource";
	}

	public List<IArtifactCreator<GenerationContext>> getCreators(GenerationContext context) {
		ConcreteSyntax syntax = context.getConcreteSyntax();
		IPluginDescriptor resourcePlugin = context.getResourcePlugin();

		List<IArtifactCreator<GenerationContext>> creators = new ArrayList<IArtifactCreator<GenerationContext>>();
	    creators.add(new FoldersCreator<GenerationContext>(this, new File[] {
	    		context.getSourceFolder(resourcePlugin, false),
	    		context.getSourceFolder(resourcePlugin, true),
	    		context.getSchemaFolder(resourcePlugin)
	    }));
	    ClassPathParameters cpp = new ClassPathParameters(resourcePlugin);
		String sourceFolderName = csUtil.getSourceFolderName(context.getConcreteSyntax(), OptionTypes.SOURCE_FOLDER);
		String sourceGenFolderName = csUtil.getSourceFolderName(context.getConcreteSyntax(), OptionTypes.SOURCE_GEN_FOLDER);
		
		cpp.getSourceFolders().add(sourceFolderName);
		cpp.getSourceFolders().add(sourceGenFolderName);
	    creators.add(new DotClasspathCreator<GenerationContext>(this, TextResourceArtifacts.DOT_CLASSPATH, cpp, doOverride(syntax, TextResourceArtifacts.DOT_CLASSPATH)));
	    creators.add(new DotProjectCreator<GenerationContext>(this, TextResourceArtifacts.DOT_PROJECT, resourcePlugin, doOverride(syntax, TextResourceArtifacts.DOT_PROJECT)));
	    
		ArtifactDescriptor<GenerationContext, BuildPropertiesParameters> buildProperties = TextResourceArtifacts.BUILD_PROPERTIES;

		BuildPropertiesParameters bpp = new BuildPropertiesParameters(resourcePlugin);
	    bpp.getSourceFolders().add(sourceFolderName + "/");
		bpp.getSourceFolders().add(sourceGenFolderName + "/");
		bpp.getBinIncludes().add("META-INF/");
		bpp.getBinIncludes().add(".");
		bpp.getBinIncludes().add("plugin.xml");
		creators.add(new BuildPropertiesCreator<GenerationContext>(this, buildProperties, bpp, doOverride(syntax, buildProperties)));
		
	    if (OptionManager.INSTANCE.useScalesParser(syntax)) {
	    	creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SCANNERLESS_SCANNER));
	    	creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SCANNERLESS_PARSER));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.ANTLR_SCANNER), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.ANTLR_SCANNER), OptionTypes.OVERRIDE_SCANNER));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.ANTLR_LEXER), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.ANTLR_LEXER), OptionTypes.OVERRIDE_SCANNER));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.ANTLR_PARSER), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.ANTLR_PARSER), OptionTypes.OVERRIDE_PARSER));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.ANTLR_PARSER_BASE), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.ANTLR_PARSER_BASE), OptionTypes.OVERRIDE_PARSER));
	    } else {
	    	creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ANTLR_SCANNER));
		    creators.add(new ANTLRGrammarCreator(this));
		    creators.add(new ANTLRParserCreator());
		    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ANTLR_PARSER_BASE));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.SCANNERLESS_SCANNER), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.SCANNERLESS_SCANNER), OptionTypes.OVERRIDE_SCANNER));
	    	creators.add(new EmptyClassCreator(this, context.getFile(resourcePlugin, TextResourceArtifacts.SCANNERLESS_PARSER), TextResourceArtifacts.PACKAGE_MOPP, context.getClassName(TextResourceArtifacts.SCANNERLESS_PARSER), OptionTypes.OVERRIDE_PARSER));
	    }
	    creators.add(new PluginXMLCreator(this));
	    creators.add(new DefaultLoadOptionsExtensionPointSchemaCreator(this));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.RESOURCE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.RESOURCE_FACTORY));
	    if (!syntax.getName().contains(".")) {
		    creators.add(new AdditionalExtensionParserExtensionPointSchemaCreator(this));
		    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.RESOURCE_FACTORY_DELEGATOR));
	    }
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PRINTER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PRINTER2));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.LAYOUT_INFORMATION));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.LAYOUT_INFORMATION_ADAPTER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SYNTAX_ELEMENT_DECORATOR));
	    creators.add(new ReferenceResolversCreator(this));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.REFERENCE_RESOLVER_SWITCH));
	    
		for (CompleteTokenDefinition tokenDefinition : syntax.getActiveTokens()) {
			if (tokenDefinition.isUsed()) {
			    creators.add(new TokenResolversCreator(this, tokenDefinition));
			}
		}
	    
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TOKEN_RESOLVER_FACTORY));

		File project = getFileSystemConnector().getProjectFolder(resourcePlugin);
		File metaFolder = new File(project.getAbsolutePath() + File.separator +  "META-INF");
	    creators.add(new FoldersCreator<GenerationContext>(this, metaFolder));

	    ManifestParameters manifestParameters = new ManifestParameters();
	    Collection<String> exports = manifestParameters.getExportedPackages();
		// export the generated packages
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_ROOT));
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_ANALYSIS));
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_CC));
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_GRAMMAR));
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_MOPP));
		exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_UTIL));
		// do not export the analysis package if the are no resolvers
		if (nameUtil.getResolverFileNames(syntax).size() > 0) {
			exports.add(context.getPackageName(TextResourceArtifacts.PACKAGE_ANALYSIS));
		}
		exports.addAll(OptionManager.INSTANCE.getStringOptionValueAsCollection(syntax, OptionTypes.ADDITIONAL_EXPORTS));
		manifestParameters.getRequiredBundles().addAll(getRequiredBundles(context));
		manifestParameters.setPlugin(resourcePlugin);
		manifestParameters.setActivatorClass(context.getQualifiedClassName(TextResourceArtifacts.PLUGIN_ACTIVATOR));
		manifestParameters.setBundleName("EMFText Parser Plugin: " + context.getConcreteSyntax().getName());
		creators.add(new ManifestCreator<GenerationContext>(this, TextResourceArtifacts.MANIFEST, manifestParameters, OptionManager.INSTANCE.doOverride(context.getConcreteSyntax(), OptionTypes.OVERRIDE_MANIFEST)));
		
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.META_INFORMATION));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TOKEN_STYLE_INFORMATION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.FOLDING_INFORMATION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.BRACKET_INFORMATION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SYNTAX_COVERAGE_INFORMATION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.DEFAULT_RESOLVER_DELEGATE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PROBLEM));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CONTEXT_DEPENDENT_URI_FRAGMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CONTEXT_DEPENDENT_URI_FRAGMENT_FACTORY));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.DELEGATING_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.DUMMY_E_OBJECT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ELEMENT_MAPPING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.FUZZY_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.LOCATION_MAP));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.DEFAULT_TOKEN_RESOLVER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.REFERENCE_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TOKEN_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.URI_MAPPING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PARSE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PLUGIN_ACTIVATOR));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TERMINATE_PARSING_EXCEPTION));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.UNEXPECTED_CONTENT_TYPE_EXCEPTION));

	    // add grammar information generators
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CARDINALITY));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SYNTAX_ELEMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.KEYWORD));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TERMINAL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PLACEHOLDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CHOICE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CONTAINMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.COMPOUND));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.SEQUENCE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.LINE_BREAK));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.WHITE_SPACE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.FORMATTING_ELEMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.GRAMMAR_INFORMATION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.FOLLOW_SET_PROVIDER));
	    
	    
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_INPUT_STREAM_PROCESSOR_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.INPUT_STREAM_PROCESSOR));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_OPTION_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_OPTIONS));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_RESOURCE_POST_PROCESSOR));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_RESOURCE_POST_PROCESSOR_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_BRACKET_PAIR));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_COMMAND));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_CONFIGURABLE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_CONTEXT_DEPENDENT_URI_FRAGMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_CONTEXT_DEPENDENT_URI_FRAGMENT_FACTORY));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_ELEMENT_MAPPING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_EXPECTED_ELEMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_HOVER_TEXT_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_LOCATION_MAP));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_PARSE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_PROBLEM));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_REFERENCE_CACHE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_REFERENCE_MAPPING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_REFERENCE_RESOLVER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_REFERENCE_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_REFERENCE_RESOLVER_SWITCH));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_DIAGNOSTIC));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_PARSER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_PRINTER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_RESOURCE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_META_INFORMATION));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_RESOURCE_PLUGIN_PART));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_SCANNER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TEXT_TOKEN));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TOKEN_RESOLVER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TOKEN_RESOLVE_RESULT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TOKEN_RESOLVER_FACTORY));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_TOKEN_STYLE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_URI_MAPPING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_BACKGROUND_PARSING_LISTENER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.E_PROBLEM_TYPE));

	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ABSTRACT_EXPECTED_ELEMENT));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.EXPECTED_CS_STRING));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.EXPECTED_STRUCTURAL_FEATURE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.EXPECTED_TERMINAL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ATTRIBUTE_VALUE_PROVIDER));

	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.CAST_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.COPIED_E_LIST));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.COPIED_E_OBJECT_INTERNAL_E_LIST));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.E_CLASS_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.E_OBJECT_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.LIST_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.MAP_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.PAIR));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.MINIMAL_MODEL_HELPER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.RESOURCE_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.STREAM_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.STRING_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TEXT_RESOURCE_UTIL));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.UNICODE_CONVERTER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.NEW_FILE_CONTENT_PROVIDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.BUILDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.BUILDER_ADAPTER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.I_BUILDER));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.NATURE));
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.ABSTRACT_INTERPRETER));
	    
	    creators.add(new SyntaxArtifactCreator<Object>(this, TextResourceArtifacts.TEXT_TOKEN));
		return creators;
	}

	private boolean doOverride(
			ConcreteSyntax syntax,
			ArtifactDescriptor<GenerationContext, ?> artifact) {
		return OptionManager.INSTANCE.getBooleanOptionValue(syntax, artifact.getOverrideOption());
	}

	private Collection<String> getRequiredBundles(GenerationContext context) {
		ConcreteSyntax syntax = context.getConcreteSyntax();
		
		Set<String> imports = new LinkedHashSet<String>();
		imports.add("org.eclipse.core.resources");
		imports.add("org.eclipse.emf");
		imports.add("org.eclipse.emf.codegen.ecore");
		imports.add("org.eclipse.emf.ecore");
		imports.add("org.eclipse.emf.ecore.edit");
		imports.add("org.eclipse.emf.validation");
		imports.add("org.eclipse.emf.workspace");
		imports.add("org.emftext.access;resolution:=optional");
		
		// TODO implement extension mechanism to allow code generation plug-ins to add
		// more imports here 

		String qualifiedBasePluginName = 
			OptionManager.INSTANCE.getStringOptionValue(syntax, OptionTypes.BASE_RESOURCE_PLUGIN);
		if (qualifiedBasePluginName != null) {
			imports.add(qualifiedBasePluginName);
		}
		
		imports.addAll(OptionManager.INSTANCE.getStringOptionValueAsCollection(syntax, OptionTypes.ADDITIONAL_DEPENDENCIES));

		genUtil.addImports(context, imports, syntax);
		
		// remove the current plug-in, because we do not
		// need to import it
		imports.remove(context.getResourcePlugin().getName());
		
		return imports;
	}
}
