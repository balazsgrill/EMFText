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
package org.emftext.sdk;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.sdk.concretesyntax.CompleteTokenDefinition;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.Import;
import org.emftext.sdk.concretesyntax.QuotedTokenDefinition;
import org.emftext.sdk.concretesyntax.Rule;
import org.emftext.sdk.concretesyntax.resource.cs.ICsQuickFix;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsAnalysisProblem;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsAnalysisProblemType;
import org.emftext.sdk.concretesyntax.resource.cs.util.CsResourceUtil;
import org.emftext.sdk.regex.TokenSorter;

/**
 * An abstract super class for all post processors. It tries to resolve all 
 * proxy objects and if this succeeds analyse(CsResource, ConcreteSyntax)
 * is called.
 */
public abstract class AbstractPostProcessor {

	// We share the token sorter using a static field, which is bad design, but the
	// only way to globally make use of the shared automaton cache. This cache is
	// needed to substantially improve loading of concrete syntax files. The cache
	// has an upper limit for its size, which makes sure that this static field
	// does not end up as a memory leak.
	protected static final TokenSorter tokenSorter = new TokenSorter();
	
	private PostProcessingContext context;

	private boolean terminate;

	public final void process(PostProcessingContext context) {
		this.context = context;
		boolean hasErrors = getContext().hasErrors();
		if (hasErrors && !doAnalysisAfterPreviousErrors()) {
			return;
		}
		Resource resource = getContext().getResource();
		if (doResolveProxiesBeforeAnalysis()) {
			// it is actually sufficient to do this once (for the first post processor)
			// but, since all post processors work in isolation, we cannot pass on the
			// information that proxy objects have already been resolved. if this turns
			// out to be a performance problem one can attach an adapter to the resource
			// which carried the information. this adapter must also react to all changes
			// made to the resource in order to trigger proxy resolution again after the
			// resource has changed
			if (!CsResourceUtil.resolveAll(resource)) {
				return;
			}
		}
		List<EObject> objects = resource.getContents();
		for (EObject next : objects) {
			if (next instanceof ConcreteSyntax) {
				analyse((ConcreteSyntax) next);
			}
		}
	}

	protected PostProcessingContext getContext() {
		return context;
	}

	protected boolean doAnalysisAfterPreviousErrors() {
		return false;
	}

	protected boolean doResolveProxiesBeforeAnalysis() {
		return true;
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, EObject cause) {
		context.addProblem(new CsAnalysisProblem(message, problemType), cause);
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, EObject cause, ICsQuickFix quickFix) {
		context.addProblem(new CsAnalysisProblem(message, problemType, quickFix), cause);
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, EObject cause, Collection<ICsQuickFix> quickFixes) {
		context.addProblem(new CsAnalysisProblem(message, problemType, quickFixes), cause);
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, int column, int line, int charStart, int charEnd) {
		context.addProblem(new CsAnalysisProblem(message, problemType), column, line, charStart, charEnd);
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, int column, int line, int charStart, int charEnd, ICsQuickFix quickFix) {
		context.addProblem(new CsAnalysisProblem(message, problemType, quickFix), column, line, charStart, charEnd);
	}

	protected void addProblem(CsAnalysisProblemType problemType,
			final String message, int column, int line, int charStart, int charEnd, List<ICsQuickFix> quickFixes) {
		context.addProblem(new CsAnalysisProblem(message, problemType, quickFixes), column, line, charStart, charEnd);
	}

	protected void addTokenProblem(
			CsAnalysisProblemType type, 
			String message,
			ConcreteSyntax syntax,
			CompleteTokenDefinition definition) {
		Set<EObject> causes = new LinkedHashSet<EObject>();
		// problems that refer to quoted definition must be added to the placeholders,
		// because the tokens were created automatically and do thus not exist in the
		// syntax physically
		if (definition instanceof QuotedTokenDefinition) {
			QuotedTokenDefinition quotedDefinition = (QuotedTokenDefinition) definition;
			causes.addAll(quotedDefinition.getAttributeReferences());
		} else {
			causes.add(definition);
		}
		
		if (definition.isImported(syntax)) {
			causes.clear();
			ConcreteSyntax importedSyntax = definition.getContainingSyntax(syntax);
			causes.add(getImport(syntax, importedSyntax));
		}
		for (EObject cause : causes) {
			addProblem(type, message, cause);
		}
	}

	protected void addRuleProblem(CsAnalysisProblemType problemType,
			String message, ConcreteSyntax syntax, Rule rule) {
		EObject cause = rule;
		if (syntax.isImportedRule(rule)) {
			cause = getImport(syntax, rule.getSyntax());
		}
		addProblem(problemType, message, cause);
	}
	
	private Import getImport(ConcreteSyntax importingSyntax, ConcreteSyntax importedSyntax) {
		for (Import syntaxImport : importingSyntax.getImports()) {
			if (syntaxImport.getConcreteSyntax() == importedSyntax) {
				return syntaxImport;
			}
		}
		return null;
	}

	public abstract void analyse(ConcreteSyntax syntax);

	public void terminate() {
		this.terminate = true;
	}
	
	protected boolean doTerminate() {
		return terminate;
	}
}
