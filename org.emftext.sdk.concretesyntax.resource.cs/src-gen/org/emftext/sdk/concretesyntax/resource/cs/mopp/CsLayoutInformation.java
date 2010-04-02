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

package org.emftext.sdk.concretesyntax.resource.cs.mopp;

public class CsLayoutInformation {
	
	private final org.emftext.sdk.concretesyntax.resource.cs.grammar.CsSyntaxElement syntaxElement;
	private final java.lang.String hiddenTokenText;
	private final java.lang.String visibleTokenText;
	private java.lang.Object object;
	private boolean wasResolved;
	
	public CsLayoutInformation(org.emftext.sdk.concretesyntax.resource.cs.grammar.CsSyntaxElement syntaxElement, java.lang.Object object, java.lang.String hiddenTokenText, java.lang.String visibleTokenText) {
		super();
		this.syntaxElement = syntaxElement;
		this.object = object;
		this.hiddenTokenText = hiddenTokenText;
		this.visibleTokenText = visibleTokenText;
	}
	
	public org.emftext.sdk.concretesyntax.resource.cs.grammar.CsSyntaxElement getSyntaxElement() {
		return syntaxElement;
	}
	
	public java.lang.Object getObject(org.eclipse.emf.ecore.EObject container) {
		if (wasResolved) {
			return object;
		}
		// we need to try to resolve proxy objects again, because the proxy
		// might have been resolve before this adapter existed, which means
		// we missed the replaceProxy() notification
		if (object instanceof org.eclipse.emf.ecore.InternalEObject) {
			org.eclipse.emf.ecore.InternalEObject internalObject = (org.eclipse.emf.ecore.InternalEObject) object;
			if (internalObject.eIsProxy()) {
				if (container instanceof org.eclipse.emf.ecore.InternalEObject) {
					org.eclipse.emf.ecore.InternalEObject internalContainer = (org.eclipse.emf.ecore.InternalEObject) container;
					org.eclipse.emf.ecore.EObject resolvedObject = internalContainer.eResolveProxy(internalObject);
					if (resolvedObject != internalObject) {
						object = resolvedObject;
						wasResolved = true;
					}
				}
			}
		} else {
			wasResolved = true;
		}
		return object;
	}
	
	public java.lang.String getHiddenTokenText() {
		return hiddenTokenText;
	}
	
	public java.lang.String getVisibleTokenText() {
		return visibleTokenText;
	}
	
	public void replaceProxy(org.eclipse.emf.ecore.EObject proxy, org.eclipse.emf.ecore.EObject target) {
		if (this.object == proxy) {
			this.object = target;
		}
	}
	
}