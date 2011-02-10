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
package org.emftext.sdk.concretesyntax.resource.cs.mopp;

public class CsMetaInformation implements org.emftext.sdk.concretesyntax.resource.cs.ICsMetaInformation {
	
	public String getSyntaxName() {
		return "cs";
	}
	
	public String getURI() {
		return "http://www.emftext.org/sdk/concretesyntax";
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsTextScanner createLexer() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsAntlrScanner(new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsLexer());
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsTextParser createParser(java.io.InputStream inputStream, String encoding) {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsParser().createInstance(inputStream, encoding);
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsTextPrinter createPrinter(java.io.OutputStream outputStream, org.emftext.sdk.concretesyntax.resource.cs.ICsTextResource resource) {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsPrinter2(outputStream, resource);
	}
	
	public org.eclipse.emf.ecore.EClass[] getClassesWithSyntax() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsSyntaxCoverageInformationProvider().getClassesWithSyntax();
	}
	
	public org.eclipse.emf.ecore.EClass[] getStartSymbols() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsSyntaxCoverageInformationProvider().getStartSymbols();
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsReferenceResolverSwitch getReferenceResolverSwitch() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsReferenceResolverSwitch();
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsTokenResolverFactory getTokenResolverFactory() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsTokenResolverFactory();
	}
	
	public String getPathToCSDefinition() {
		return "org.emftext.sdk.concretesyntax/metamodel/concretesyntax.cs";
	}
	
	public String[] getTokenNames() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsParser(null).getTokenNames();
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.ICsTokenStyle getDefaultTokenStyle(String tokenName) {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsTokenStyleInformationProvider().getDefaultTokenStyle(tokenName);
	}
	
	public java.util.Collection<org.emftext.sdk.concretesyntax.resource.cs.ICsBracketPair> getBracketPairs() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsBracketInformationProvider().getBracketPairs();
	}
	
	public org.eclipse.emf.ecore.EClass[] getFoldableClasses() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsFoldingInformationProvider().getFoldableClasses();
	}
	
	public org.eclipse.emf.ecore.resource.Resource.Factory createResourceFactory() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsResourceFactory();
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.mopp.CsNewFileContentProvider getNewFileContentProvider() {
		return new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsNewFileContentProvider();
	}
	
	public void registerResourceFactory() {
		org.eclipse.emf.ecore.resource.Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(getSyntaxName(), new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsResourceFactory());
	}
	
	/**
	 * Returns the key of the option that can be used to register a preprocessor that
	 * is used as a pipe when loading resources. This key is language-specific. To
	 * register one preprocessor for multiple resource types, it must be registered
	 * individually using all keys.
	 */
	public String getInputStreamPreprocessorProviderOptionKey() {
		return getSyntaxName() + "_" + "INPUT_STREAM_PREPROCESSOR_PROVIDER";
	}
	
	/**
	 * Returns the key of the option that can be used to register a post-processors
	 * that are invoked after loading resources. This key is language-specific. To
	 * register one post-processor for multiple resource types, it must be registered
	 * individually using all keys.
	 */
	public String getResourcePostProcessorProviderOptionKey() {
		return getSyntaxName() + "_" + "RESOURCE_POSTPROCESSOR_PROVIDER";
	}
	
}
