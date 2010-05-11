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
package org.emftext.sdk.codegen.resource.ui.generators.ui;

import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.ARRAY_LIST;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.COLOR;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.DISPLAY;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.E_OBJECT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_DOCUMENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_PREFERENCE_STORE;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_SELECTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_SELECTION_CHANGED_LISTENER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_SELECTION_PROVIDER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.I_STRUCTURED_SELECTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.KEY_EVENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.KEY_LISTENER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.LIST;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.MOUSE_EVENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.MOUSE_LISTENER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.OBJECT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.POSITION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.PREFERENCE_CONVERTER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.PROJECTION_VIEWER;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.RESOURCE;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.RGB;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SELECTION_CHANGED_EVENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.STYLED_TEXT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.STYLE_RANGE;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.SWT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.TEXT_SELECTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.TREE_SELECTION;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.VERIFY_EVENT;
import static org.emftext.sdk.codegen.resource.ui.IUIClassNameConstants.VERIFY_LISTENER;

import org.emftext.sdk.codegen.IGenerator;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.codegen.resource.ui.TextResourceUIArtifacts;
import org.emftext.sdk.codegen.resource.ui.generators.UIJavaBaseGenerator;

public class HighlightingGenerator extends UIJavaBaseGenerator {

	public HighlightingGenerator() {
		super();
	}

	private HighlightingGenerator(GenerationContext context) {
		super(context, TextResourceUIArtifacts.HIGHLIGHTING);
	}

	public boolean generateJavaContents(JavaComposite sc) {
		
		sc.add("package " + getResourcePackageName() + ";");
		sc.addLineBreak();
		sc.addJavadoc("A manager class for the highlighting of occurrences and brackets.");
		sc.add("public class " + getResourceClassName() + " implements " + I_SELECTION_PROVIDER + ", " + I_SELECTION_CHANGED_LISTENER + " {");
		sc.addLineBreak();
		addFields(sc);
		addPositionHelperClass(sc);
		addConstructor(sc);
		addMethods(sc);
		sc.add("}");
		return true;
	}

	private void addMethods(JavaComposite sc) {
		addListenersMethod(sc);
		addSetHighlightingMethod(sc);
		addSetCategoryHighlightingMethod(sc);
		addRemoveHighlightingMethod(sc);
		addRemoveHighlightingCategoryMethod(sc);
		addSetEObjectSelectionMethod(sc);
		addResetValuesMethod(sc);
		addConvertToWidgetPositionMethod(sc);
		addGetStyleRangeAtPositionMethod(sc);
		addAddSelectionChangedListenerMethod(sc);
		addRemoveSelectionChangedListenerMethod(sc);
		addSetSelectionMethod(sc);
		addGetSelectionMethod(sc);
		addSelectionChangedMethod(sc);
		addHandleContentOutlineSelectionMethod(sc);
	}

	private void addHandleContentOutlineSelectionMethod(StringComposite sc) {
		sc.add("private void handleContentOutlineSelection(" + I_SELECTION + " selection) {");
		sc.add("if (!selection.isEmpty()) {");
		sc.add(OBJECT + " selectedElement = ((" + I_STRUCTURED_SELECTION + ") selection).getFirstElement();");
		sc.add("if (selectedElement instanceof " + E_OBJECT + ") {");
		sc.add(E_OBJECT + " selectedEObject = (" + E_OBJECT + ") selectedElement;");
		sc.add(RESOURCE + " resource = selectedEObject.eResource();");
		sc.add("if (resource instanceof " + iTextResourceClassName + ") {");
		sc.add(iTextResourceClassName + " textResource = (" + iTextResourceClassName + ") resource;");
		sc.add(iLocationMapClassName + " locationMap = textResource.getLocationMap();");
		sc.add("int elementCharStart = locationMap.getCharStart(selectedEObject);");
		sc.add("int elementCharEnd = locationMap.getCharEnd(selectedEObject);");
		sc.add(TEXT_SELECTION + " textEditorSelection = new " + TEXT_SELECTION + "(elementCharStart, elementCharEnd - elementCharStart + 1);");
		sc.add("projectionViewer.getSelectionProvider().setSelection(textEditorSelection);");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addSelectionChangedMethod(StringComposite sc) {
		sc.add("public void selectionChanged(" + SELECTION_CHANGED_EVENT + " event) {");
		sc.add("if (event.getSelection() instanceof " + TREE_SELECTION + ") {");
		sc.add("handleContentOutlineSelection(event.getSelection());");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetSelectionMethod(StringComposite sc) {
		sc.add("public " + I_SELECTION + " getSelection() {");
		sc.add("return selection;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addSetSelectionMethod(StringComposite sc) {
		sc.add("public void setSelection(" + I_SELECTION + " selection) {");
		sc.add("this.selection = selection;");
		sc.add("for (" + I_SELECTION_CHANGED_LISTENER + " listener : selectionChangedListeners) {");
		sc.add("listener.selectionChanged(new " + SELECTION_CHANGED_EVENT + "(this, selection));");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addRemoveSelectionChangedListenerMethod(StringComposite sc) {
		sc.add("public void removeSelectionChangedListener(" + I_SELECTION_CHANGED_LISTENER + " listener) {");
		sc.add("selectionChangedListeners.remove(listener);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addAddSelectionChangedListenerMethod(StringComposite sc) {
		sc.add("public void addSelectionChangedListener(" + I_SELECTION_CHANGED_LISTENER + " listener) {");
		sc.add("selectionChangedListeners.add(listener);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addFields(StringComposite sc) {
		sc.add("private " + LIST + "<" + I_SELECTION_CHANGED_LISTENER + "> selectionChangedListeners = new " + ARRAY_LIST + "<" + I_SELECTION_CHANGED_LISTENER + ">();");
		sc.add("private " + I_SELECTION + " selection = null;");
		sc.add("private final static " + positionHelperClassName + " positionHelper = new " + positionHelperClassName + "();");
		sc.add("private boolean isHighlightBrackets = true;");
		sc.add("private boolean isHighlightOccurrences = true;");
		sc.add("private " + tokenScannerClassName + " scanner;");
		sc.add("private " + colorManagerClassName + " colorManager;");
		sc.add("private " + COLOR + " definitionColor;");
		sc.add("private " + COLOR + " proxyColor;");
		sc.add("private " + COLOR + " bracketColor;");
		sc.add("private " + COLOR + " black;");
		sc.add("private " + STYLED_TEXT + " textWidget;");
		sc.add("private " + I_PREFERENCE_STORE + " preferenceStore;");
		sc.add("private " + PROJECTION_VIEWER + " projectionViewer;");
		sc.add("private " + occurenceClassName + " occurrence;");
		sc.add("private " + bracketSetClassName + " bracketSet;");
		sc.add("private " + DISPLAY + " display;");
		sc.addLineBreak();
	}

	private void addGetStyleRangeAtPositionMethod(StringComposite sc) {
		sc.add("private " + STYLE_RANGE + " getStyleRangeAtPosition(" + POSITION + " position) {");
		sc.add(STYLE_RANGE + " styleRange = null;");
		sc.add("try {");
		sc.add("styleRange = textWidget.getStyleRangeAtOffset(position.offset);");
		sc.add("} catch (IllegalArgumentException iae) {");
		// TODO handle exception?
		sc.add("}");
		sc.add("if (styleRange == null) {");
		sc.add("styleRange = new " + STYLE_RANGE + "(position.offset, position.length, black, null);");
		sc.add("} else {");
		sc.add("styleRange.length = position.length;");
		sc.add("}");
		sc.add("return styleRange;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addConvertToWidgetPositionMethod(StringComposite sc) {
		sc.add("private " + POSITION + " convertToWidgetPosition(" + POSITION + " position) {");
		sc.add("if (position == null) {");
		sc.add("return null;");
		sc.add("}");
		sc.add("int startOffset = projectionViewer.modelOffset2WidgetOffset(position.offset);");
		sc.add("int endOffset = projectionViewer.modelOffset2WidgetOffset(position.offset + position.length);");
		sc.add("if (endOffset - startOffset != position.length || startOffset == -1 || textWidget.getCharCount() < endOffset) {");
		sc.add("return null;");
		sc.add("}");
		sc.add("return new " + POSITION + "(startOffset, endOffset - startOffset);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addResetValuesMethod(JavaComposite sc) {
		sc.addJavadoc("Resets the changed values after setting the preference pages.");
		sc.add("public void resetValues() {");
		sc.add("isHighlightBrackets = preferenceStore.getBoolean(" + preferenceConstantsClassName + ".EDITOR_MATCHING_BRACKETS_CHECKBOX);");
		sc.add("isHighlightOccurrences = preferenceStore.getBoolean(" + preferenceConstantsClassName + ".EDITOR_OCCURRENCE_CHECKBOX);");
		sc.add("bracketColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_MATCHING_BRACKETS_COLOR));");
		sc.add("definitionColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_DEFINITION_COLOR));");
		sc.add("proxyColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_PROXY_COLOR));");
		sc.add("bracketSet.resetBrackets();");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addSetEObjectSelectionMethod(StringComposite sc) {
		sc.add("public void setEObjectSelection() {");
		sc.add("display.syncExec(new Runnable() {");
		sc.add("public void run() {");
		sc.add(E_OBJECT + " selectedEObject = occurrence.getEObjectAtCurrentPosition();");
		sc.add("if (selectedEObject != null) {");
		sc.add("setSelection(new " + eObjectSelectionClassName + "(selectedEObject, false));");
		sc.add("}");
		sc.add("}");
		sc.add("});");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addRemoveHighlightingCategoryMethod(StringComposite sc) {
		sc.add("private void removeHighlightingCategory(" + I_DOCUMENT + " document, String category) {");
		sc.add(POSITION + "[] positions = positionHelper.getPositions(document, category);");
		sc.add("boolean isOccurrence = (category.equals(" + positionCategoryClassName + ".DEFINTION.toString()) || category.equals(" + positionCategoryClassName + ".PROXY.toString()));");
		sc.add("if (category.equals(" + positionCategoryClassName + ".BRACKET.toString())) {");
		sc.add(STYLE_RANGE + " styleRange;");
		sc.add("for (" + POSITION + " position : positions) {");
		sc.add(POSITION + " tmpPosition = convertToWidgetPosition(position);");
		sc.add("if (tmpPosition != null) {");
		sc.add("styleRange = getStyleRangeAtPosition(tmpPosition);");
		sc.add("styleRange.borderStyle = " + SWT + ".NONE;");
		sc.add("styleRange.borderColor = null;");
		sc.add("styleRange.background = null;");
		sc.add("textWidget.setStyleRange(styleRange);");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
		sc.add("if (isOccurrence) {");
		sc.add("for (" + POSITION + " position : positions) {");
		sc.add(POSITION + " tmpPosition = convertToWidgetPosition(position);");
		sc.add("if (tmpPosition != null) {");
		sc.add("textWidget.setStyleRange(new " + STYLE_RANGE + "(tmpPosition.offset, tmpPosition.length, null, null));");
		sc.add("projectionViewer.invalidateTextPresentation(tmpPosition.offset, tmpPosition.length);");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
		sc.add("positionHelper.removePositions(document, category);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addRemoveHighlightingMethod(StringComposite sc) {
		sc.add("private void removeHighlighting() {");
		sc.add(I_DOCUMENT + " document = projectionViewer.getDocument();");
		sc.add("removeHighlightingCategory(document, " + positionCategoryClassName + ".BRACKET.toString());");
		sc.add("if (occurrence.isToRemoveHighlighting()) {");
		sc.add("removeHighlightingCategory(document, " + positionCategoryClassName + ".DEFINTION.toString());");
		sc.add("removeHighlightingCategory(document, " + positionCategoryClassName + ".PROXY.toString());");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addSetCategoryHighlightingMethod(StringComposite sc) {
		sc.add("private void setCategoryHighlighting(" + I_DOCUMENT + " document, String category) {");
		sc.add(STYLE_RANGE + " styleRange = null;");
		sc.add(POSITION + "[] positions = positionHelper.getPositions(document, category);");
		sc.addLineBreak();
		sc.add("if (category.equals(" + positionCategoryClassName + ".PROXY.toString())) {");
		sc.add("if (positions.length > 0) {");
		sc.add("styleRange = getStyleRangeAtPosition(positions[0]);");
		sc.add("if (styleRange.foreground == null) {");
		sc.add("styleRange.foreground = black;");
		sc.add("}");
		sc.add("}");
		sc.add("if (styleRange != null) {");
		sc.add("styleRange.background = proxyColor;");
		sc.add("}");
		sc.add("}");
		sc.add("for (" + POSITION + " position : positions) {");
		sc.add(POSITION + " tmpPosition = convertToWidgetPosition(position);");
		sc.add("if (tmpPosition != null) {");
		sc.add("if (category.equals(" + positionCategoryClassName + ".DEFINTION.toString())) {");
		sc.add("styleRange = getStyleRangeAtPosition(tmpPosition);");
		sc.add("if (styleRange.foreground == null) {");
		sc.add("styleRange.foreground = black;");
		sc.add("}");
		sc.add("styleRange.background = definitionColor;");
		sc.add("textWidget.setStyleRange(styleRange);");
		sc.add("}");
		sc.add("if (category.equals(" + positionCategoryClassName + ".PROXY.toString())) {");
		sc.add("if (styleRange == null) {");
		sc.add("return;");
		sc.add("}");
		sc.add("styleRange.start = tmpPosition.offset;");
		sc.add("textWidget.setStyleRange(styleRange);");
		sc.add("}");
		sc.add("if (category.equals(" + positionCategoryClassName + ".BRACKET.toString())) {");
		sc.add("styleRange = getStyleRangeAtPosition(tmpPosition);");
		sc.add("styleRange.borderStyle = " + SWT + ".BORDER_SOLID;");
		sc.add("styleRange.borderColor = bracketColor;");
		sc.add("if (styleRange.foreground == null) {");
		sc.add("styleRange.foreground = black;");
		sc.add("}");
		sc.add("textWidget.setStyleRange(styleRange);");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addSetHighlightingMethod(StringComposite sc) {
		sc.add("private void setHighlighting() {");
		sc.add(I_DOCUMENT + " document = projectionViewer.getDocument();");
		sc.add("if (isHighlightBrackets) {");
		sc.add("bracketSet.matchingBrackets();");
		sc.add("}");
		sc.add("if (isHighlightOccurrences) {");
		sc.add("occurrence.handleOccurrenceHighlighting(bracketSet);");
		sc.add("}");
		sc.add("if (occurrence.isPositionsChanged()) {");
		sc.add("setCategoryHighlighting(document,");
		sc.add(positionCategoryClassName + ".DEFINTION.toString());");
		sc.add("setCategoryHighlighting(document,");
		sc.add(positionCategoryClassName + ".PROXY.toString());");
		sc.add("}");
		sc.add("setCategoryHighlighting(document, " + positionCategoryClassName + ".BRACKET.toString());");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addListenersMethod(StringComposite sc) {
		sc.add("private void addListeners(" + editorClassName + " editor) {");
		sc.add("UpdateHighlightingListener hl = new UpdateHighlightingListener();");
		sc.add("textWidget.addKeyListener(hl);");
		sc.add("textWidget.addVerifyListener(hl);");
		sc.add("textWidget.addMouseListener(hl);");
		sc.add("editor.addBackgroundParsingListener(hl);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addConstructor(JavaComposite sc) {
		sc.addJavadoc(
			"Creates the highlighting manager class.",
			"@param textResource the text resource to be provided to other classes",
			"@param sourceviewer the source viewer converts offset between master and slave documents",
			"@param colorManager the color manager provides highlighting colors",
			"@param editor"
		);
		sc.add("public " + getResourceClassName() + "(" + iTextResourceClassName + " textResource, " + PROJECTION_VIEWER + " sourceviewer, " + colorManagerClassName + " colorManager, " + editorClassName + " editor) {");
		sc.add("this.display = " + DISPLAY + ".getCurrent();");
		sc.add("sourceviewer.getSelectionProvider();");
		sc.add("preferenceStore = " + uiPluginActivatorClassName + ".getDefault().getPreferenceStore();");
		sc.add("textWidget = sourceviewer.getTextWidget();");
		sc.add("projectionViewer = sourceviewer;");
		sc.add("scanner = new " + tokenScannerClassName + "(colorManager);");
		sc.add("occurrence = new " + occurenceClassName + "(textResource, sourceviewer, scanner);");
		sc.add("bracketSet = new " + bracketSetClassName + "(sourceviewer);");
		sc.add("this.colorManager = colorManager;");
		sc.add("isHighlightBrackets = preferenceStore.getBoolean(" + preferenceConstantsClassName + ".EDITOR_MATCHING_BRACKETS_CHECKBOX);");
		sc.add("isHighlightOccurrences = preferenceStore.getBoolean(" + preferenceConstantsClassName + ".EDITOR_OCCURRENCE_CHECKBOX);");
		sc.add("definitionColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_DEFINITION_COLOR));");
		sc.add("proxyColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_PROXY_COLOR));");
		sc.add("bracketColor = colorManager.getColor(" + PREFERENCE_CONVERTER + ".getColor(preferenceStore, " + preferenceConstantsClassName + ".EDITOR_MATCHING_BRACKETS_COLOR));");
		sc.add("black = colorManager.getColor(new " + RGB + "(0, 0, 0));");
		sc.addLineBreak();
		sc.add("addListeners(editor);");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addPositionHelperClass(JavaComposite sc) {
		sc.addJavadoc(
			"A key and mouse listener for the highlighting. It removes the " +
			"highlighting before documents change. No highlighting is set after " +
			"document changes to increase the performance. Occurrences are not searched " +
			"if the caret is still in the same token to increase the performance."
		);
		sc.add("private final class UpdateHighlightingListener implements " + KEY_LISTENER + ", " + VERIFY_LISTENER + ", " + MOUSE_LISTENER + ", " + iBackgroundParsingListenerClassName + " {");
		sc.addLineBreak();
		sc.add("private boolean changed = false;");
		sc.add("private int caret = -1;");
		sc.addLineBreak();
		sc.add("public void keyPressed(" + KEY_EVENT + " e) {");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void keyReleased(" + KEY_EVENT + " e) {");
		sc.add("if (changed) {");
		sc.add("changed = false;");
		sc.add("return;");
		sc.add("}");
		sc.add("refreshHighlighting();");
		sc.add("}");
		sc.addLineBreak();
		sc.add("private void refreshHighlighting() {");
		sc.add("int textCaret = textWidget.getCaretOffset();");
		sc.add("if (textCaret < 0 || textCaret > textWidget.getCharCount()) {");
		sc.add("return;");
		sc.add("}");
		sc.add("if (textCaret != caret) {");
		sc.add("caret = textCaret;");
		sc.add("removeHighlighting();");
		sc.add("setHighlighting();");
		sc.add("setEObjectSelection();");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void verifyText(" + VERIFY_EVENT + " e) {");
		sc.add("occurrence.resetTokenRegion();");
		sc.add("removeHighlighting();");
		sc.add("changed = true;");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void mouseDoubleClick(" + MOUSE_EVENT + " e) {");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void mouseDown(" + MOUSE_EVENT + " e) {");
		sc.add("}");
		sc.addLineBreak();
		sc.addComment("1-left click, 2-middle click,");
		sc.add("public void mouseUp(" + MOUSE_EVENT + " e) {");
		sc.addComment("3-right click");
		sc.add("if (e.button != 1) {");
		sc.add("return;");
		sc.add("}");
		sc.add("refreshHighlighting();");
		sc.add("}");
		sc.addLineBreak();
		sc.add("public void parsingCompleted(" + RESOURCE + " resource) {");
		sc.add("display.syncExec(new Runnable() {");
		sc.addLineBreak();
		sc.add("public void run() {");
		sc.add("refreshHighlighting();");
		sc.add("}");
		sc.add("});");
		sc.add("}");
		sc.add("}");
		sc.addLineBreak();
	}

	public IGenerator<GenerationContext, Object> newInstance(GenerationContext context, Object parameters) {
		return new HighlightingGenerator(context);
	}
}