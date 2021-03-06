/*******************************************************************************
 * Copyright (c) 2006-2013
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.codegen.resource.ui.generators.ui;

import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.ABSTRACT_DECORATED_TEXT_EDITOR_PREFERENCE_CONSTANTS;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.ACTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.BAD_LOCATION_EXCEPTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.CONTENT_ASSISTANT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.DEFAULT_ANNOTATION_HOVER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.DEFAULT_DAMAGER_REPAIRER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.EDITORS_UI;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_ANNOTATION_HOVER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_AUTO_EDIT_STRATEGY;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_CONTENT_ASSISTANT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_DOCUMENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_HYPERLINK_DETECTOR;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_PRESENTATION_RECONCILER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_QUICK_ASSIST_ASSISTANT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_RECONCILER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_RECONCILING_STRATEGY;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_SOURCE_VIEWER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_SPELLING_PROBLEM_COLLECTOR;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_TEXT_HOVER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_TOKEN_SCANNER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.MONO_RECONCILER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.PRESENTATION_RECONCILER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SPELLING_PROBLEM;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SPELLING_RECONCILE_STRATEGY;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SPELLING_SERVICE;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SWT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.TEXT_SOURCE_VIEWER_CONFIGURATION;

import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.parameters.ArtifactParameter;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.codegen.resource.ui.generators.UIJavaBaseGenerator;

public class SourceViewerConfigurationGenerator extends UIJavaBaseGenerator<ArtifactParameter<GenerationContext>> {

	public void generateJavaContents(JavaComposite sc) {
		
		sc.add("package " + getResourcePackageName() + ";");
		sc.addLineBreak();
		sc.addJavadoc(
			"This class provides the configuration for the generated editor. " +
			"It registers content assistance and syntax highlighting."
		);
		sc.add("public class " + getResourceClassName() + " extends " + TEXT_SOURCE_VIEWER_CONFIGURATION + " {");
		sc.addLineBreak();
		addFields(sc);
		addConstructor(sc);
		addMethods(sc);
		
		sc.add("}");
	}

	private void addFields(StringComposite sc) {
		sc.add("private " + colorManagerClassName + " colorManager;");
		sc.add("private " + iResourceProviderClassName + " resourceProvider;");
		sc.add("private " + iAnnotationModelProviderClassName + " annotationModelProvider;");
		sc.add("private " + quickAssistAssistantClassName + " quickAssistAssistant;");
		sc.addLineBreak();
	}

	private void addConstructor(JavaComposite sc) {
		sc.addJavadoc(
			"Creates a new editor configuration.",
			"@param resourceProvider the provider for the resource (usually this is the editor)",
			"@param colorManager the color manager to use"
		);
		sc.add("public " + getResourceClassName() + "(" + iResourceProviderClassName + " resourceProvider, " + iAnnotationModelProviderClassName + " annotationModelProvider, " + colorManagerClassName + " colorManager) {");
		sc.add("super(" + uiPluginActivatorClassName + ".getDefault().getPreferenceStore());");
		sc.add("this.fPreferenceStore.setDefault(" + SPELLING_SERVICE + ".PREFERENCE_SPELLING_ENABLED, true);");
		sc.add("this.fPreferenceStore.setDefault(" + ABSTRACT_DECORATED_TEXT_EDITOR_PREFERENCE_CONSTANTS + ".EDITOR_TAB_WIDTH, 4);");
		sc.add("this.fPreferenceStore.setDefault(" + ABSTRACT_DECORATED_TEXT_EDITOR_PREFERENCE_CONSTANTS + ".EDITOR_HYPERLINK_KEY_MODIFIER, " + ACTION + ".findModifierString(" + SWT + ".MOD1));");
		sc.add("this.resourceProvider = resourceProvider;");
		sc.add("this.annotationModelProvider = annotationModelProvider;");
		sc.add("this.colorManager = colorManager;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addMethods(JavaComposite sc) {
		addGetAutoEditStrategies(sc);
		addGetContentAssistantMethod(sc);
		addGetConfiguredContentTypesMethod(sc);
		addGetScannerMethod(sc);
		addGetPresentationReconcilerMethod(sc);
		addGetAnnotationHoverMethod(sc);
		addGetTextHoverMethod(sc);
		addGetHyperlinkDetectorsMethod(sc);
		addGetQuickAssistAssistantMethod(sc);
		addGetReconcilerMethod(sc);
	}

	private void addGetAutoEditStrategies(JavaComposite sc) {
		sc.addJavadoc("Returns an instance of class " + 
			autoEditStrategyClassName + ".");
		sc.add("public " + I_AUTO_EDIT_STRATEGY + "[] getAutoEditStrategies(" + I_SOURCE_VIEWER + " sourceViewer, String contentType) {");
		sc.add("return new " + I_AUTO_EDIT_STRATEGY + "[] {new " + autoEditStrategyClassName +"()};");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetReconcilerMethod(JavaComposite sc) {
		sc.add("public " + I_RECONCILER + " getReconciler(final " + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.add("if (fPreferenceStore == null || !fPreferenceStore.getBoolean(" + SPELLING_SERVICE + ".PREFERENCE_SPELLING_ENABLED)) {");
		sc.add("return null;");
		sc.add("}");
		sc.addLineBreak();
		sc.add(SPELLING_SERVICE + " spellingService = " + EDITORS_UI + ".getSpellingService();");
		sc.add("if (spellingService.getActiveSpellingEngineDescriptor(fPreferenceStore) == null) {");
		sc.add("return null;");
		sc.add("}");
		sc.addLineBreak();
		sc.add(I_RECONCILING_STRATEGY + " strategy = new " + SPELLING_RECONCILE_STRATEGY + "(sourceViewer, spellingService) {");
		sc.add("@Override").addLineBreak();
		sc.add("protected " + I_SPELLING_PROBLEM_COLLECTOR + " createSpellingProblemCollector() {");
		sc.add("final " + I_SPELLING_PROBLEM_COLLECTOR + " collector = super.createSpellingProblemCollector();");
		sc.addLineBreak();
		sc.add("return new " + I_SPELLING_PROBLEM_COLLECTOR + "() {");
		sc.addLineBreak();
		sc.add("public void accept(" + SPELLING_PROBLEM + " problem) {");
		sc.add("int offset = problem.getOffset();");
		sc.add("int length = problem.getLength();");
		sc.add("if (sourceViewer == null) {");
		sc.add("return;");
		sc.add("}");
		sc.add(I_DOCUMENT + " document = sourceViewer.getDocument();");
		sc.add("if (document == null) {");
		sc.add("return;");
		sc.add("}");
		sc.add("String text;");
		sc.add("try {");
		sc.add("text = document.get(offset, length);");
		sc.add("} catch (" + BAD_LOCATION_EXCEPTION + " e) {");
		sc.add("return;");
		sc.add("}");
		sc.add("if (new " + ignoredWordsFilterClassName + "().ignoreWord(text)) {");
		sc.add("return;");
		sc.add("}");
		sc.add("collector.accept(problem);");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void beginCollecting() {");
		sc.add("collector.beginCollecting();");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void endCollecting() {");
		sc.add("collector.endCollecting();");
		sc.add("}");
		sc.add("};");
		sc.add("}");
		sc.add("};");
		sc.add(MONO_RECONCILER + " reconciler = new " + MONO_RECONCILER + "(strategy, false);");
		sc.add("reconciler.setDelay(500);");
		sc.add("return reconciler;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetQuickAssistAssistantMethod(JavaComposite sc) {
		sc.add("public " + I_QUICK_ASSIST_ASSISTANT + " getQuickAssistAssistant(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.add("if (quickAssistAssistant == null) {");
		sc.add("quickAssistAssistant = new " + quickAssistAssistantClassName + "(resourceProvider, annotationModelProvider);");
		sc.add("}");
		sc.add("return quickAssistAssistant;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetHyperlinkDetectorsMethod(StringComposite sc) {
		sc.add("public " + I_HYPERLINK_DETECTOR + "[] getHyperlinkDetectors(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.add("if (sourceViewer == null) {");
		sc.add("return null;");
		sc.add("}");
		sc.add("return new " + I_HYPERLINK_DETECTOR + "[] { new " + hyperlinkDetectorClassName + "(resourceProvider.getResource()) };");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetTextHoverMethod(StringComposite sc) {
		sc.add("public " + I_TEXT_HOVER + " getTextHover(" + I_SOURCE_VIEWER + " sourceViewer, String contentType) {");
		sc.add("return new " + textHoverClassName + "(resourceProvider);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetAnnotationHoverMethod(StringComposite sc) {
		sc.add("public " + I_ANNOTATION_HOVER + " getAnnotationHover(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.add("return new " + DEFAULT_ANNOTATION_HOVER + "();");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetPresentationReconcilerMethod(StringComposite sc) {
		sc.add("public " + I_PRESENTATION_RECONCILER + " getPresentationReconciler(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.addLineBreak();
		sc.add(PRESENTATION_RECONCILER + " reconciler = new " + PRESENTATION_RECONCILER + "();");
		sc.add(DEFAULT_DAMAGER_REPAIRER + " repairer = new " + DEFAULT_DAMAGER_REPAIRER + "(getScanner());");
		sc.add("reconciler.setDamager(repairer, " + I_DOCUMENT + ".DEFAULT_CONTENT_TYPE);");
		sc.add("reconciler.setRepairer(repairer, " + I_DOCUMENT + ".DEFAULT_CONTENT_TYPE);");
		sc.addLineBreak();
		sc.add("return reconciler;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetScannerMethod(JavaComposite sc) {
		sc.add("protected " + I_TOKEN_SCANNER + " getScanner() {");
		sc.add("return new " + uiMetaInformationClassName + "().createTokenScanner(resourceProvider.getResource(), colorManager);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetConfiguredContentTypesMethod(StringComposite sc) {
		sc.add("public String[] getConfiguredContentTypes(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.add("return new String[] {");
		sc.add(I_DOCUMENT + ".DEFAULT_CONTENT_TYPE,");
		sc.add("};");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetContentAssistantMethod(StringComposite sc) {
		sc.add("public " + I_CONTENT_ASSISTANT + " getContentAssistant(" + I_SOURCE_VIEWER + " sourceViewer) {");
		sc.addLineBreak();
		sc.add(CONTENT_ASSISTANT + " assistant = new " + CONTENT_ASSISTANT + "();");
		sc.add("assistant.setContentAssistProcessor(new " + completionProcessorClassName + "(resourceProvider), " + I_DOCUMENT + ".DEFAULT_CONTENT_TYPE);");
		sc.add("assistant.enableAutoActivation(true);");
		sc.add("assistant.setAutoActivationDelay(500);");
		sc.add("assistant.setProposalPopupOrientation(" + I_CONTENT_ASSISTANT + ".PROPOSAL_OVERLAY);");
		sc.add("assistant.setContextInformationPopupOrientation(" + I_CONTENT_ASSISTANT + ".CONTEXT_INFO_ABOVE);");
		sc.addLineBreak();
		sc.add("return assistant;");
		sc.add("}");
		sc.addLineBreak();
	}
}
