/*******************************************************************************
 * Copyright (c) 2006-2012
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

package org.emftext.sdk.concretesyntax.resource.cs.ui;

/**
 * The preference page for the bracket setting with following features:
 * <ul>
 * <li>enables bracket matching</li>
 * <li>chooses matching highlight color</li>
 * <li>customizes bracket set</li>
 * </ul>
 */
public class CsBracketPreferencePage extends org.eclipse.jface.preference.PreferencePage implements org.eclipse.ui.IWorkbenchPreferencePage {
	
	private static final String[] ALL_LEFT_BRACKETS = new String[] { "{", "(", "[", "<", "\"", "'", };
	private static final String[] ALL_RIGHT_BRACKETS = new String[] { "}", ")", "]", ">", "\"", "'", };
	
	private String BRACKETS_COLOR = org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;
	
	private java.util.Set<String> languageIDs = new java.util.LinkedHashSet<String>();
	
	private org.eclipse.jface.preference.ColorSelector matchingBracketsColorEditor;
	private org.eclipse.swt.widgets.Label colorEditorLabel;
	private org.eclipse.swt.widgets.Button enableCheckbox;
	private org.eclipse.swt.widgets.Button enableClosingInside;
	private org.eclipse.swt.widgets.Button enableCloseAfterEnter;
	private org.eclipse.swt.widgets.Button matchingBracketsColorButton;
	private org.eclipse.swt.widgets.Label bracketTokensLabel;
	private org.eclipse.swt.widgets.Combo leftBracketTokensCombo;
	private org.eclipse.swt.widgets.Combo rightBracketTokensCombo;
	private org.eclipse.swt.widgets.List bracketsList;
	private org.eclipse.swt.widgets.Button addBracketButton;
	private org.eclipse.swt.widgets.Button removeBracketButton;
	private java.util.Map<String, String> bracketSetTemp = new java.util.LinkedHashMap<String, String>();
	private String language = new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation().getSyntaxName();
	
	private org.emftext.sdk.concretesyntax.resource.cs.ui.CsBracketSet bracketsTmp;
	
	/**
	 * Creates a preference page for bracket setting.
	 */
	public CsBracketPreferencePage() {
		super();
		
		org.emftext.sdk.concretesyntax.resource.cs.ICsMetaInformation metaInformation = new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation();
		String languageId = metaInformation.getSyntaxName();
		languageIDs.add(languageId);
	}
	
	/**
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(org.eclipse.ui.IWorkbench workbench) {
		setPreferenceStore(org.emftext.sdk.concretesyntax.resource.cs.ui.CsUIPlugin.getDefault().getPreferenceStore());
		setDescription("Define the coloring of matching brackets.");
		
		bracketsTmp = new org.emftext.sdk.concretesyntax.resource.cs.ui.CsBracketSet();
		for (String languageID : languageIDs) {
			bracketSetTemp.put(languageID, getPreferenceStore().getString(languageID + org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_BRACKETS_SUFFIX));
		}
	}
	
	@Override	
	protected org.eclipse.swt.widgets.Control createContents(org.eclipse.swt.widgets.Composite parent) {
		
		// outer Composite
		org.eclipse.swt.widgets.Composite settingComposite = new org.eclipse.swt.widgets.Composite(parent, org.eclipse.swt.SWT.NONE);
		org.eclipse.swt.layout.GridLayout layout = new org.eclipse.swt.layout.GridLayout();
		org.eclipse.swt.layout.GridData gd;
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING);
		settingComposite.setLayout(layout);
		settingComposite.setLayoutData(gd);
		
		enableCheckbox = new org.eclipse.swt.widgets.Button(settingComposite, org.eclipse.swt.SWT.CHECK);
		enableCheckbox.setText("Enable");
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING);
		gd.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gd.horizontalSpan = 2;
		enableCheckbox.setLayoutData(gd);
		
		colorEditorLabel = new org.eclipse.swt.widgets.Label(settingComposite, org.eclipse.swt.SWT.LEFT);
		colorEditorLabel.setText("Color:");
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		colorEditorLabel.setLayoutData(gd);
		
		matchingBracketsColorEditor = new org.eclipse.jface.preference.ColorSelector(settingComposite);
		matchingBracketsColorButton = matchingBracketsColorEditor.getButton();
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_BEGINNING);
		matchingBracketsColorButton.setLayoutData(gd);
		
		org.eclipse.swt.widgets.Composite tokenSelectionComposite = new org.eclipse.swt.widgets.Composite(settingComposite, org.eclipse.swt.SWT.NONE);
		layout = new org.eclipse.swt.layout.GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 20;
		tokenSelectionComposite.setLayout(layout);
		tokenSelectionComposite.setLayoutData(gd);
		
		bracketTokensLabel = new org.eclipse.swt.widgets.Label(tokenSelectionComposite, org.eclipse.swt.SWT.LEFT);
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING);
		gd.horizontalSpan = 3;
		bracketTokensLabel.setText("Add new bracket pair");
		bracketTokensLabel.setLayoutData(gd);
		
		leftBracketTokensCombo = new org.eclipse.swt.widgets.Combo(tokenSelectionComposite,org.eclipse.swt.SWT.DROP_DOWN | org.eclipse.swt.SWT.READ_ONLY);
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING);
		leftBracketTokensCombo.setLayoutData(gd);
		
		rightBracketTokensCombo = new org.eclipse.swt.widgets.Combo(tokenSelectionComposite,org.eclipse.swt.SWT.DROP_DOWN | org.eclipse.swt.SWT.READ_ONLY);
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.FILL);
		rightBracketTokensCombo.setLayoutData(gd);
		
		addBracketButton = new org.eclipse.swt.widgets.Button(tokenSelectionComposite, org.eclipse.swt.SWT.PUSH);
		addBracketButton.setText("Add");
		addBracketButton.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING, org.eclipse.swt.layout.GridData.BEGINNING, false, false));
		
		org.eclipse.swt.widgets.Label configurePairsLabel = new org.eclipse.swt.widgets.Label(tokenSelectionComposite, org.eclipse.swt.SWT.LEFT);
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING);
		gd.horizontalSpan = 3;
		gd.verticalIndent = 20;
		configurePairsLabel.setText("Configure bracket pairs");
		configurePairsLabel.setLayoutData(gd);
		bracketsList = new org.eclipse.swt.widgets.List(tokenSelectionComposite, org.eclipse.swt.SWT.SINGLE);
		gd = new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.CENTER, org.eclipse.swt.layout.GridData.FILL, false, true);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 4;
		gd.widthHint = 100;
		gd.heightHint = 300;
		bracketsList.setLayoutData(gd);
		
		enableClosingInside = new org.eclipse.swt.widgets.Button(tokenSelectionComposite, org.eclipse.swt.SWT.CHECK);
		enableClosingInside.setText("Enable closing inside");
		enableClosingInside.setToolTipText("If this option is enabled, other bracket pair can close inside this pair automatically.");
		enableClosingInside.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING, org.eclipse.swt.layout.GridData.BEGINNING, false, false));
		enableClosingInside.setEnabled(false);
		
		enableCloseAfterEnter = new org.eclipse.swt.widgets.Button(tokenSelectionComposite, org.eclipse.swt.SWT.CHECK);
		enableCloseAfterEnter.setText("Enable close after enter");
		enableCloseAfterEnter.setToolTipText("If this option is enabled the closing bracket is only inserted when the enter key is pressed.");
		enableCloseAfterEnter.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING, org.eclipse.swt.layout.GridData.BEGINNING, false, false));
		enableCloseAfterEnter.setEnabled(false);
		
		removeBracketButton = new org.eclipse.swt.widgets.Button(tokenSelectionComposite, org.eclipse.swt.SWT.PUSH);
		removeBracketButton.setText("Remove");
		removeBracketButton.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.BEGINNING, org.eclipse.swt.layout.GridData.BEGINNING, false, false));
		
		addListenersToStyleButtons();
		
		settingComposite.layout(false);
		handleMatchingBracketsSelection();
		return settingComposite;
	}
	
	/**
	 * Initialize and handle the values of this preference page.
	 */
	private void handleMatchingBracketsSelection() {
		// not for the case of none existing language
		enableCheckbox.setSelection(getPreferenceStore().getBoolean(org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_MATCHING_BRACKETS_CHECKBOX));
		enableClosingInside.setSelection(false);
		matchingBracketsColorButton.setEnabled(getPreferenceStore().getBoolean(		org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_MATCHING_BRACKETS_CHECKBOX));
		org.eclipse.swt.graphics.RGB rgb = org.eclipse.jface.preference.PreferenceConverter.getColor(getPreferenceStore(), BRACKETS_COLOR);
		matchingBracketsColorEditor.setColorValue(rgb);
		removeBracketButton.setEnabled(false);
		
		initializeLanguage();
		bracketsTmp.deserialize(getPreferenceStore().getString(language + org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_BRACKETS_SUFFIX));
		String[] brackets = bracketsTmp.getBracketArray();
		if (brackets != null) {
			bracketsList.setItems(brackets);
		}
	}
	
	public void initializeLanguage() {
		bracketSetTemp.put(language, bracketsTmp.serialize());
		bracketsTmp.deserialize(bracketSetTemp.get(language));
		leftBracketTokensCombo.setItems(ALL_LEFT_BRACKETS);
		leftBracketTokensCombo.select(0);
		rightBracketTokensCombo.setItems(ALL_RIGHT_BRACKETS);
		rightBracketTokensCombo.select(0);
		bracketsList.setItems(bracketsTmp.getBracketArray());
	}
	
	private void addListenersToStyleButtons() {
		enableCheckbox.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				matchingBracketsColorButton.setEnabled(enableCheckbox.getSelection());
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
		
		addBracketButton.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				String open = leftBracketTokensCombo.getText();
				String close = rightBracketTokensCombo.getText();
				if (bracketsTmp.isBracket(open) || bracketsTmp.isBracket(close)) {
					setErrorMessage("One or both bracket parts are set!");
				} else {
					bracketsTmp.addBracketPair(open, close, enableClosingInside.getSelection(), enableCloseAfterEnter.getSelection());
					bracketsList.setItems(bracketsTmp.getBracketArray());
					setErrorMessage(null);
					bracketSetTemp.put(language, bracketsTmp.serialize());
				}
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
		
		removeBracketButton.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				bracketsTmp.removeBracketPairs(bracketsList.getSelection());
				setErrorMessage(null);
				bracketsList.setItems(bracketsTmp.getBracketArray());
				bracketSetTemp.put(language, bracketsTmp.serialize());
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
		
		bracketsList.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				org.emftext.sdk.concretesyntax.resource.cs.ICsBracketPair bracketPair = getSelectedBracketPair();
				if (bracketPair == null) {
					removeBracketButton.setEnabled(false);
					return;
				}
				enableClosingInside.setEnabled(true);
				enableCloseAfterEnter.setEnabled(true);
				enableClosingInside.setSelection(bracketPair.isClosingEnabledInside());
				enableCloseAfterEnter.setSelection(bracketPair.isCloseAfterEnter());
				removeBracketButton.setEnabled(true);
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
		
		enableClosingInside.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				org.emftext.sdk.concretesyntax.resource.cs.ICsBracketPair bracketPair = getSelectedBracketPair();
				if (bracketPair != null) {
					boolean closingEnabledInside = enableClosingInside.getSelection();
					bracketPair.setClosingEnabledInside(closingEnabledInside);
				}
				bracketSetTemp.put(language, bracketsTmp.serialize());
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
		
		enableCloseAfterEnter.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				org.emftext.sdk.concretesyntax.resource.cs.ICsBracketPair bracketPair = getSelectedBracketPair();
				if (bracketPair != null) {
					boolean closeAfterEnter = enableCloseAfterEnter.getSelection();
					bracketPair.setCloseAfterEnter(closeAfterEnter);
				}
				bracketSetTemp.put(language, bracketsTmp.serialize());
			}
			
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				// do nothing
			}
		});
	}
	
	/**
	 * Sets the default values for this preference page.
	 */
	protected void performDefaults() {
		org.eclipse.jface.preference.IPreferenceStore preferenceStore = getPreferenceStore();
		enableCheckbox.setSelection(preferenceStore.getDefaultBoolean(org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_MATCHING_BRACKETS_CHECKBOX));
		matchingBracketsColorButton.setEnabled(enableCheckbox.getSelection());
		matchingBracketsColorEditor.setColorValue(org.eclipse.jface.preference.PreferenceConverter.getDefaultColor(preferenceStore, BRACKETS_COLOR));
		String defaultBrackets = preferenceStore.getDefaultString(language + org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_BRACKETS_SUFFIX);
		bracketSetTemp.put(language, defaultBrackets);
		bracketsTmp.deserialize(bracketSetTemp.get(language));
		bracketsList.setItems(bracketsTmp.getBracketArray());
		// Reset check boxes and disable them because no item is selected in the
		// bracketsList component.
		enableClosingInside.setSelection(false);
		enableCloseAfterEnter.setSelection(false);
		enableClosingInside.setEnabled(false);
		enableCloseAfterEnter.setEnabled(false);
	}
	
	public boolean performOk() {
		if (!super.performOk()) {
			return false;
		}
		updateActiveEditor();
		return true;
	}
	
	protected void performApply() {
		updateActiveEditor();
	}
	
	/**
	 * Sets the chosen options to the preference store and refreshes it in the editor.
	 */
	private void updateActiveEditor() {
		// set the values after ok or apply
		org.eclipse.jface.preference.IPreferenceStore preferenceStore = getPreferenceStore();
		org.eclipse.jface.preference.PreferenceConverter.setValue(preferenceStore, BRACKETS_COLOR, matchingBracketsColorEditor.getColorValue());
		preferenceStore.setValue(org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_MATCHING_BRACKETS_CHECKBOX, enableCheckbox.getSelection());
		preferenceStore.setValue(language + org.emftext.sdk.concretesyntax.resource.cs.ui.CsPreferenceConstants.EDITOR_BRACKETS_SUFFIX, bracketSetTemp.get(language));
		org.eclipse.ui.IWorkbench workbench = org.eclipse.ui.PlatformUI.getWorkbench();
		org.eclipse.ui.IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null && editor instanceof org.emftext.sdk.concretesyntax.resource.cs.ui.CsEditor) {
			((org.emftext.sdk.concretesyntax.resource.cs.ui.CsEditor) editor).invalidateTextRepresentation();
		}
	}
	
	protected org.emftext.sdk.concretesyntax.resource.cs.ICsBracketPair getSelectedBracketPair() {
		int[] itemIndices = bracketsList.getSelectionIndices();
		if (itemIndices == null || itemIndices.length != 1) {
			// The bracketsList component is set to single selection. Thus, we expect exactly
			// one selected item.
			return null;
		}
		int index = itemIndices[0];
		return bracketsTmp.getBracketPair(index);
	}
	
}
