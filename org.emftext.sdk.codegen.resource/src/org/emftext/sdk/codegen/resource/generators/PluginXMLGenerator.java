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
package org.emftext.sdk.codegen.resource.generators;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;

import org.emftext.sdk.IPluginDescriptor;
import org.emftext.sdk.codegen.GenerationProblem;
import org.emftext.sdk.codegen.IGenerator;
import org.emftext.sdk.codegen.OptionManager;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.composites.XMLComposite;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.codegen.resource.TextResourceArtifacts;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.OptionTypes;

/**
 * A generator for the plugin.xml file.
 * 
 * TODO mseifert: make this generator reusable
 */
public class PluginXMLGenerator extends ResourceBaseGenerator<Object> {

	private GenerationContext context;
	private String pluginID;
	private String builderID;

	public PluginXMLGenerator() {
		super();
	}

	private PluginXMLGenerator(GenerationContext context) {
		super();
		this.context = context;
		IPluginDescriptor resourcePlugin = context.getResourcePlugin();
		pluginID = resourcePlugin.getName();
		builderID = context.getBuilderID(context.getConcreteSyntax());
	}

	public boolean generate(PrintWriter out) {
		out.write(getContentOfPluginXML());
		out.flush();
		return true;
	}

	/**
	 * Generate the XML file describing the plug-in.
	 * 
	 * @return Generated code.
	 */
	private String getContentOfPluginXML() {
		final ConcreteSyntax concreteSyntax = context.getConcreteSyntax();
		final String primaryConcreteSyntaxName = getPrimarySyntaxName(concreteSyntax);
		final String secondaryConcreteSyntaxName = getSecondarySyntaxName(concreteSyntax);
		final String qualifiedResourceFactoryClassName;
		if (secondaryConcreteSyntaxName == null) {
			qualifiedResourceFactoryClassName = context.getQualifiedClassName(TextResourceArtifacts.RESOURCE_FACTORY_DELEGATOR);
		}
		else {
			qualifiedResourceFactoryClassName = context.getQualifiedClassName(TextResourceArtifacts.RESOURCE_FACTORY);
		}
		final boolean disableBuilder = OptionManager.INSTANCE.getBooleanOptionValue(concreteSyntax, OptionTypes.DISABLE_BUILDER);

		StringComposite sc = new XMLComposite();
		sc.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sc.add("<?eclipse version=\"3.2\"?>");
		sc.add("<plugin>");

		// register the syntax meta information
		String metaInformationClass = context.getQualifiedClassName(TextResourceArtifacts.META_INFORMATION);
		sc.add("<extension point=\"org.emftext.access.syntax\">");
		sc.add("<metaInformationProvider class=\"" + metaInformationClass + "\" id=\"" + metaInformationClass + "\">");
		sc.add("</metaInformationProvider>");
		sc.add("</extension>");
		sc.addLineBreak();
		
		sc.add("<extension id=\"" + context.getNatureID(concreteSyntax) + "\" name=\"" + concreteSyntax.getName() + " nature\" point=\"org.eclipse.core.resources.natures\">"); 
		sc.add("<runtime>");
		sc.add("<run class=\"" + context.getQualifiedClassName(TextResourceArtifacts.NATURE)+ "\" />"); 
		sc.add("</runtime>");
		if (!disableBuilder) {
			sc.add("<builder id=\"" + builderID + "\" />");
		}
		sc.add("</extension>");
		sc.addLineBreak();

		if (!disableBuilder) {
			sc.add("<extension point=\"org.eclipse.core.resources.builders\" id=\"" + builderID + "\" name=\"" + concreteSyntax.getName() + " Builder\">");
			sc.add("<builder hasNature=\"true\">");
			sc.add("<run class=\"" + context.getQualifiedClassName(TextResourceArtifacts.BUILDER_ADAPTER)+ "\" />");
			sc.add("</builder>");
			sc.add("</extension>");
			sc.addLineBreak();
		}
		
		sc.add("<extension-point id=\"" + pluginID + ".default_load_options\" name=\"Default Load Options\" schema=\"schema/default_load_options.exsd\"/>");
		sc.addLineBreak();
		
		String qualifiedBasePluginName = OptionManager.INSTANCE.getStringOptionValue(concreteSyntax, OptionTypes.BASE_RESOURCE_PLUGIN);
		if (secondaryConcreteSyntaxName == null) {
			// register the generated resource factory
			sc.add("<extension point=\"org.eclipse.emf.ecore.extension_parser\">");
			sc.add("<parser class=\"" + qualifiedResourceFactoryClassName + "\" type=\"" + primaryConcreteSyntaxName + "\">");
			sc.add("</parser>");
			sc.add("</extension>");
			sc.addLineBreak();
			
			sc.add("<extension-point id=\"" + pluginID + ".additional_extension_parser\" name=\"Additional Extension Parser\" schema=\"schema/additional_extension_parser.exsd\"/>");
			sc.addLineBreak();
		}
		else if (qualifiedBasePluginName != null) {
			sc.add("<extension point=\""+  qualifiedBasePluginName + ".additional_extension_parser\">");
			sc.add("<parser class=\"" + qualifiedResourceFactoryClassName + "\" type=\"" + secondaryConcreteSyntaxName + "\">");
			sc.add("</parser>");
			sc.add("</extension>");
			sc.addLineBreak();
		}
		
		sc.add("</plugin>");

		return sc.toString();
	}

	private String getPrimarySyntaxName(ConcreteSyntax concreteSyntax) {
		 String fullConcreteSyntaxName = concreteSyntax.getName();
		 int idx = fullConcreteSyntaxName.lastIndexOf(".");
		 if (idx != -1) {
			 return fullConcreteSyntaxName.substring(idx + 1);
		 }
		 return fullConcreteSyntaxName;
	}
	
	private String getSecondarySyntaxName(ConcreteSyntax concreteSyntax) {
		 String fullConcreteSyntaxName = concreteSyntax.getName();
		 int idx = fullConcreteSyntaxName.lastIndexOf(".");
		 if (idx != -1) {
			 return fullConcreteSyntaxName.substring(0, idx);
		 }
		 return null;
	}
	
	public Collection<GenerationProblem> getCollectedErrors() {
		return Collections.emptyList();
	}

	public Collection<GenerationProblem> getCollectedProblems() {
		return Collections.emptyList();
	}

	public IGenerator<GenerationContext, Object> newInstance(GenerationContext context, Object parameters) {
		return new PluginXMLGenerator(context);
	}
}