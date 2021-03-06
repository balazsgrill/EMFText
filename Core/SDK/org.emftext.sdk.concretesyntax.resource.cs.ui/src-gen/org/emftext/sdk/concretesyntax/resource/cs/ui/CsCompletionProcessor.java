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

public class CsCompletionProcessor implements org.eclipse.jface.text.contentassist.IContentAssistProcessor {
	
	private org.emftext.sdk.concretesyntax.resource.cs.ICsResourceProvider resourceProvider;
	
	public CsCompletionProcessor(org.emftext.sdk.concretesyntax.resource.cs.ICsResourceProvider resourceProvider) {
		super();
		this.resourceProvider = resourceProvider;
	}
	
	public org.eclipse.jface.text.contentassist.ICompletionProposal[] computeCompletionProposals(org.eclipse.jface.text.ITextViewer viewer, int offset) {
		org.emftext.sdk.concretesyntax.resource.cs.ICsTextResource textResource = resourceProvider.getResource();
		if (textResource == null) {
			return new org.eclipse.jface.text.contentassist.ICompletionProposal[0];
		}
		String content = viewer.getDocument().get();
		org.emftext.sdk.concretesyntax.resource.cs.ui.CsCodeCompletionHelper helper = new org.emftext.sdk.concretesyntax.resource.cs.ui.CsCodeCompletionHelper();
		org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal[] computedProposals = helper.computeCompletionProposals(textResource, content, offset);
		
		// call completion proposal post processor to allow for customizing the proposals
		org.emftext.sdk.concretesyntax.resource.cs.ui.CsProposalPostProcessor proposalPostProcessor = new org.emftext.sdk.concretesyntax.resource.cs.ui.CsProposalPostProcessor();
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal> computedProposalList = java.util.Arrays.asList(computedProposals);
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal> extendedProposalList = proposalPostProcessor.process(computedProposalList);
		if (extendedProposalList == null) {
			extendedProposalList = java.util.Collections.emptyList();
		}
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal> finalProposalList = new java.util.ArrayList<org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal>();
		for (org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal proposal : extendedProposalList) {
			if (proposal.isMatchesPrefix()) {
				finalProposalList.add(proposal);
			}
		}
		org.eclipse.jface.text.contentassist.ICompletionProposal[] result = new org.eclipse.jface.text.contentassist.ICompletionProposal[finalProposalList.size()];
		int i = 0;
		for (org.emftext.sdk.concretesyntax.resource.cs.ui.CsCompletionProposal proposal : finalProposalList) {
			String proposalString = proposal.getInsertString();
			String displayString = proposal.getDisplayString();
			String prefix = proposal.getPrefix();
			org.eclipse.swt.graphics.Image image = proposal.getImage();
			org.eclipse.jface.text.contentassist.IContextInformation info;
			info = new org.eclipse.jface.text.contentassist.ContextInformation(image, proposalString, proposalString);
			int begin = offset - prefix.length();
			int replacementLength = prefix.length();
			result[i++] = new org.eclipse.jface.text.contentassist.CompletionProposal(proposalString, begin, replacementLength, proposalString.length(), image, displayString, info, proposalString);
		}
		return result;
	}
	
	public org.eclipse.jface.text.contentassist.IContextInformation[] computeContextInformation(org.eclipse.jface.text.ITextViewer viewer, int offset) {
		return null;
	}
	
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}
	
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}
	
	public org.eclipse.jface.text.contentassist.IContextInformationValidator getContextInformationValidator() {
		return null;
	}
	
	public String getErrorMessage() {
		return null;
	}
}
