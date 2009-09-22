/*******************************************************************************
 * Copyright (c) 2006-2009 
 * Software Technology Group, Dresden University of Technology
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA  02111-1307 USA
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany 
 *   - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.syntax_analysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.emftext.sdk.AbstractPostProcessor;
import org.emftext.sdk.ECsProblemType;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.CsString;
import org.emftext.sdk.concretesyntax.TokenDefinition;
import org.emftext.sdk.concretesyntax.resource.cs.CsResource;

/**
 * The HiddenTokenAnalyser searches for unused token definitions
 * in a syntax and adds a warning for each unused definition.
 */
public class UnusedTokenAnalyser extends AbstractPostProcessor {

	@Override
	public void analyse(CsResource resource, ConcreteSyntax syntax) {
		List<TokenDefinition> activeTokens = syntax.getActiveTokens();
		TreeIterator<EObject> allContents = syntax.eAllContents();
		List<String> keywordTokens = new ArrayList<String>();
		while (allContents.hasNext()) {
			EObject object = (EObject) allContents.next();
			if (object instanceof CsString) {
				CsString s = (CsString) object;
				keywordTokens.add(s.getValue());
			}
		}
		for (TokenDefinition definition : activeTokens) {
			String assumeKeyword = definition.getRegex().substring(1, definition.getRegex().length()-1);
			
			if (!definition.isUsed() && !keywordTokens.contains(assumeKeyword)) {
				addProblem(resource, ECsProblemType.UNUSED_TOKEN, "Token " + definition.getName() + " is not used and will be discarded during parsing.", definition);
			}
		}
	}
}