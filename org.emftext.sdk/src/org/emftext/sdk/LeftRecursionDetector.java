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
package org.emftext.sdk;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.common.util.EList;
import org.emftext.sdk.codegen.util.ConcreteSyntaxUtil;
import org.emftext.sdk.codegen.util.GenClassCache;
import org.emftext.sdk.concretesyntax.Choice;
import org.emftext.sdk.concretesyntax.CompoundDefinition;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.Containment;
import org.emftext.sdk.concretesyntax.Definition;
import org.emftext.sdk.concretesyntax.Rule;
import org.emftext.sdk.concretesyntax.Sequence;

/** 
 * Checks whether the given Concrete Syntax definition contains left-recursive rules.
 * It currently detects direct left recursion and a subset of indirect left recursive rules.
 * 
 */
public class LeftRecursionDetector {

	private final ConcreteSyntaxUtil csUtil = new ConcreteSyntaxUtil();
	private final GenClassCache genClassCache = new GenClassCache();
	
	private final Map<String, Collection<String>> genClassNames2superNames;
	private final ConcreteSyntax grammar;

	public LeftRecursionDetector(
			Map<String, Collection<String>> genClassNames2superNames, ConcreteSyntax concreteSyntax) {
		this.genClassNames2superNames = genClassNames2superNames;
		this.grammar = concreteSyntax;
	}

	public Rule findLeftRecursion(Rule rule) {
    	Rule leftProducingRule = findLeftProducingRule(rule.getMetaclass(), rule);
    	return leftProducingRule; 
	}
    
    private Rule findLeftProducingRule(GenClass metaclass, Rule rule) {
    	if (rule == null) return null;
    	return findLeftProducingRule(metaclass, rule.getDefinition(), rule); 
	}

	private Rule findLeftProducingRule(GenClass metaclass, Choice c, Rule currentRule) {
		for (Sequence sequence : c.getOptions()) {
			Rule leftProducingRule = findLeftProducingRule(metaclass, sequence, currentRule);
			if (leftProducingRule != null) return leftProducingRule;
		}
		return null;
	}

	public Rule findLeftProducingRule(GenClass metaclass, Sequence sequence, Rule currentRule) {
		for (Definition definition : sequence.getParts()) {
			if (definition instanceof Containment) {
				Containment containment = (Containment) definition;
				final GenFeature feature = containment.getFeature();
				if (feature == null) {
					continue;
				}
				if (feature.getEcoreFeature() == null) {
					continue;
				}
				
				Collection<GenClass> featureTypes = csUtil.getAllowedSubTypes(containment);
				if (featureTypes.contains(metaclass) || 
						isSubtypeofOneOf(metaclass, featureTypes)) {
					return currentRule;
				} else {
					Rule featureRule = null;
					EList<Rule> allRules = this.grammar.getAllRules();
					for (Rule rule : allRules) {
						if (featureTypes.contains(rule.getMetaclass())) {
							featureRule = rule;
						}
					}
					if (currentRule.equals(featureRule) ) {
						if (genClassNames2superNames.get(genClassCache.getQualifiedInterfaceName(metaclass)).contains(genClassCache.getQualifiedInterfaceName(currentRule.getMetaclass()))) {
							return featureRule;
						}
						else return null; // we have found a recursion, but not for the type we started from
					}
					
					Rule leftProducingRule = findLeftProducingRule(metaclass, featureRule);
					if (leftProducingRule != null) {
						return leftProducingRule;
					}
				}
			}
			else if (definition instanceof CompoundDefinition) {
				CompoundDefinition compound = (CompoundDefinition) definition;
				Rule leftProducingRule = findLeftProducingRule(metaclass, compound.getDefinitions(), currentRule);
				if (leftProducingRule != null) {
					return leftProducingRule;
				}
			} 
			if (csUtil.hasMinimalCardinalityOneOrHigher(definition)) {
				break;
			}
		}
		return null;
	}

	private boolean isSubtypeofOneOf(GenClass metaclass, Collection<GenClass> featureTypes) {
		Collection<String> superNames = genClassNames2superNames.get(genClassCache.getQualifiedInterfaceName(metaclass));
		for (GenClass featureType : featureTypes) {
			if (superNames.contains(genClassCache.getQualifiedInterfaceName(featureType))) return true;
		}
	
		return false;
	}

	public boolean isDirectLeftRecursive(Rule rule) {
		boolean isDirectLeftRecursive = false;
		GenClass metaclass = rule.getMetaclass();
		EList<Sequence> options = rule.getDefinition().getOptions();
		GenFeature recursiveFeature = null;
		for (Sequence sequence : options) {
			Rule leftProducingRule = findLeftProducingRule(metaclass, sequence, rule);
			if (leftProducingRule == null) {
				continue; // choice option not lr
			} else if (!rule.equals(leftProducingRule)) {
				return false; // choice option indirect lr 
			} else if (sequence.getParts().get(0) instanceof Containment) {
				// we found a recursion, now it
				// needs to start with recursive 
				// containment to be direct left recursive
				Containment containment = (Containment) sequence.getParts().get(0);
				GenFeature feature = containment.getFeature();
				GenClass featureType = feature.getTypeGenClass();
				if (metaclass.equals(featureType) || 
						genClassNames2superNames.get(genClassCache.getQualifiedInterfaceName(metaclass)).contains(genClassCache.getQualifiedInterfaceName(featureType))) {
					isDirectLeftRecursive = true;
					
					if (recursiveFeature == null) {
						recursiveFeature = feature;
					} else if (!recursiveFeature.equals(feature)) {
						return false; // found recursion on another feature, this is not supported
					}
					continue;
				} 
				else {
					return false; // wrong containment type
				}
			}
			else {
				return false; // recursive option which does not start with recursive containment
			}
		}
		
		return isDirectLeftRecursive;
	}
}
