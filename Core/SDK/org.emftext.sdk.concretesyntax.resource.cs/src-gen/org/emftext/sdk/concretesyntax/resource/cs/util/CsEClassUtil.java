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

package org.emftext.sdk.concretesyntax.resource.cs.util;

/**
 * A utility class that provides methods to handle EClasses.
 */
public class CsEClassUtil {
	
	public boolean isSubClass(org.eclipse.emf.ecore.EClass subClassCandidate, org.eclipse.emf.ecore.EClass superClass) {
		for (org.eclipse.emf.ecore.EClass superClassCandidate : subClassCandidate.getEAllSuperTypes()) {
			// There seem to be multiple instances of meta classes when accessed through the
			// generator model. Therefore, we compare by name.
			if (namesAndPackageURIsAreEqual(superClassCandidate, superClass)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all subclasses of 'superClass' that are contained in 'availableClasses'.
	 * 
	 * @param superClass the superclass
	 * @param availableClasses the set of classes to search in
	 * 
	 * @return a list of all subclasses of 'superClass'
	 */
	public java.util.List<org.eclipse.emf.ecore.EClass> getSubClasses(org.eclipse.emf.ecore.EClass superClass, org.eclipse.emf.ecore.EClass[] availableClasses) {
		
		java.util.List<org.eclipse.emf.ecore.EClass> result = new java.util.ArrayList<org.eclipse.emf.ecore.EClass>();
		for (org.eclipse.emf.ecore.EClass next : availableClasses) {
			if (isSubClass(next, superClass) &&			isConcrete(next)) {
				result.add(next);
			}
		}
		return result;
	}
	
	public boolean namesAndPackageURIsAreEqual(org.eclipse.emf.ecore.EClass classA,
	org.eclipse.emf.ecore.EClass classB) {
		return namesAreEqual(classA, classB) &&		packageURIsAreEqual(classA, classB);
	}
	
	public boolean packageURIsAreEqual(org.eclipse.emf.ecore.EClass classA,
	org.eclipse.emf.ecore.EClass classB) {
		String nsURI_A = classA.getEPackage().getNsURI();
		String nsURI_B = classB.getEPackage().getNsURI();
		if (nsURI_A == null && nsURI_B == null) {
			return true;
		}
		if (nsURI_A != null) {
			return nsURI_A.equals(nsURI_B);
		} else {
			return false;
		}
	}
	
	public boolean namesAreEqual(org.eclipse.emf.ecore.EClass classA, org.eclipse.emf.ecore.EClass classB) {
		return classA.getName().equals(classB.getName());
	}
	
	public boolean isConcrete(org.eclipse.emf.ecore.EClass eClass) {
		return !eClass.isAbstract() && !eClass.isInterface();
	}
	
	public boolean isNotConcrete(org.eclipse.emf.ecore.EClass eClass) {
		return !isConcrete(eClass);
	}
	
	/**
	 * Returns true if the given object is an instance of one of the EClasses.
	 */
	public boolean isInstance(Object object, org.eclipse.emf.ecore.EClass[] allowedTypes) {
		for (org.eclipse.emf.ecore.EClass allowedType : allowedTypes) {
			if (allowedType.isInstance(object)) {
				return true;
			}
		}
		return false;
	}
	
}
