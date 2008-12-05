package org.reuseware.emftextedit.sdk.concretesyntax.resource.cs;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.reuseware.emftextedit.runtime.resource.ReferenceResolver;
import org.reuseware.emftextedit.runtime.resource.ResolveResult;
import org.reuseware.emftextedit.runtime.resource.impl.EMFTextTreeAnalyserImpl;
import org.reuseware.emftextedit.sdk.concretesyntax.ConcreteSyntax;
import org.reuseware.emftextedit.sdk.concretesyntax.DefinedPlaceholder;
import org.reuseware.emftextedit.sdk.concretesyntax.Import;
import org.reuseware.emftextedit.sdk.concretesyntax.Rule;
import org.reuseware.emftextedit.sdk.concretesyntax.Terminal;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.ConcreteSyntaxPackageReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.ConcreteSyntaxStartSymbolsReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.DefinedPlaceholderTokenReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.ImportConcreteSyntaxReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.ImportPackageReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.RuleMetaclassReferenceResolver;
import org.reuseware.emftextedit.sdk.concretesyntax.resource.cs.analysis.TerminalFeatureReferenceResolver;

public class CsTreeAnalyser extends EMFTextTreeAnalyserImpl {

	protected ConcreteSyntaxPackageReferenceResolver concreteSyntaxPackageReferenceResolver = new ConcreteSyntaxPackageReferenceResolver();

	protected DefinedPlaceholderTokenReferenceResolver definedPlaceholderTokenReferenceResolver = new DefinedPlaceholderTokenReferenceResolver();

	protected RuleMetaclassReferenceResolver ruleMetaclassReferenceResolver = new RuleMetaclassReferenceResolver();

	protected ImportConcreteSyntaxReferenceResolver importConcreteSyntaxReferenceResolver = new ImportConcreteSyntaxReferenceResolver();

	protected ConcreteSyntaxStartSymbolsReferenceResolver concreteSyntaxStartSymbolsReferenceResolver = new ConcreteSyntaxStartSymbolsReferenceResolver();

	protected ImportPackageReferenceResolver importPackageReferenceResolver = new ImportPackageReferenceResolver();

	protected TerminalFeatureReferenceResolver terminalFeatureReferenceResolver = new TerminalFeatureReferenceResolver();

	public void setOptions(Map<?, ?> options) {
		concreteSyntaxPackageReferenceResolver.setOptions(options);
		definedPlaceholderTokenReferenceResolver.setOptions(options);
		ruleMetaclassReferenceResolver.setOptions(options);
		importConcreteSyntaxReferenceResolver.setOptions(options);
		concreteSyntaxStartSymbolsReferenceResolver.setOptions(options);
		importPackageReferenceResolver.setOptions(options);
		terminalFeatureReferenceResolver.setOptions(options);
	}

	public void resolve(String proxy, EObject container,
			EReference reference, int position, boolean resolveFuzzy,
			ResolveResult result) {
		if (resolveFuzzy) {
			resolveFuzzy(proxy, container, result);
		} else {
			resolveStrict(proxy, container, reference, result);
		}
	}

	private void resolveStrict(String proxy,
			EObject container, EReference reference, 
			ResolveResult result) {
		if (container instanceof ConcreteSyntax && reference.getFeatureID() == 1) {
			concreteSyntaxPackageReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof DefinedPlaceholder && reference.getFeatureID() == 2) {
			definedPlaceholderTokenReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof Rule && reference.getFeatureID() == 1) {
			ruleMetaclassReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof Import && reference.getFeatureID() == 1) {
			importConcreteSyntaxReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof ConcreteSyntax && reference.getFeatureID() == 3) {
			concreteSyntaxStartSymbolsReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof Import && reference.getFeatureID() == 2) {
			importPackageReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
		if (container instanceof Terminal && reference.getFeatureID() == 1) {
			terminalFeatureReferenceResolver.resolve(proxy, container,
					reference, 0, false, result);
		}
	}

	public String deResolve(EObject refObject, EObject container,
			EReference reference) {
		if (container instanceof ConcreteSyntax
				&& reference.getFeatureID() == 1) {
			return concreteSyntaxPackageReferenceResolver.deResolve(refObject,
					container, reference);
		}
		if (container instanceof DefinedPlaceholder
				&& reference.getFeatureID() == 2) {
			return definedPlaceholderTokenReferenceResolver.deResolve(refObject,
					container, reference);
		}
		if (container instanceof Rule && reference.getFeatureID() == 1) {
			return ruleMetaclassReferenceResolver.deResolve(refObject, container,
					reference);
		}
		if (container instanceof Import && reference.getFeatureID() == 1) {
			return importConcreteSyntaxReferenceResolver.deResolve(refObject,
					container, reference);
		}
		if (container instanceof ConcreteSyntax
				&& reference.getFeatureID() == 3) {
			return concreteSyntaxStartSymbolsReferenceResolver.deResolve(refObject,
					container, reference);
		}
		if (container instanceof Import && reference.getFeatureID() == 2) {
			return importPackageReferenceResolver.deResolve(refObject, container,
					reference);
		}
		if (container instanceof Terminal && reference.getFeatureID() == 1) {
			return terminalFeatureReferenceResolver.deResolve(refObject, container,
					reference);
		}
		return null;
	}

	public void resolveFuzzy(String proxyURIFragment, EObject container, ResolveResult result) {
		resolveFuzzy(ConcreteSyntax.class, proxyURIFragment, container, 3, concreteSyntaxStartSymbolsReferenceResolver, result);
		resolveFuzzy(ConcreteSyntax.class, proxyURIFragment, container, 1, concreteSyntaxPackageReferenceResolver, result);
		resolveFuzzy(DefinedPlaceholder.class, proxyURIFragment, container, 2, definedPlaceholderTokenReferenceResolver, result);
		resolveFuzzy(Rule.class, proxyURIFragment, container, 1, ruleMetaclassReferenceResolver, result);
		resolveFuzzy(Import.class, proxyURIFragment, container, 1, importConcreteSyntaxReferenceResolver, result);
		resolveFuzzy(Import.class, proxyURIFragment, container, 2, importPackageReferenceResolver, result);
		resolveFuzzy(Terminal.class, proxyURIFragment, container, 1, terminalFeatureReferenceResolver, result);
	}

	protected void resolveFuzzy(Class<?> clazz, String identifier, EObject container,
			int featureID, ReferenceResolver resolver, ResolveResult result) {
		
		if (clazz.isInstance(container)) {
			EStructuralFeature feature = container.eClass().getEStructuralFeature(featureID);
			if (!(feature instanceof EReference)) {
				return;
			}
			resolver.resolve(
					identifier, container, (EReference) feature, 0, 
					true, result);
		}
	}
}
