/**
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
 *  
 */
package org.emftext.sdk.concretesyntax;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Gen Class Cache</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.emftext.sdk.concretesyntax.GenClassCache#get_qualifiedInterfaceNameCache <em>qualified Interface Name Cache</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.emftext.sdk.concretesyntax.ConcretesyntaxPackage#getGenClassCache()
 * @model
 * @generated
 */
public interface GenClassCache extends EObject {
	/**
	 * Returns the value of the '<em><b>qualified Interface Name Cache</b></em>' map.
	 * The key is of type {@link org.eclipse.emf.codegen.ecore.genmodel.GenClass},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>qualified Interface Name Cache</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>qualified Interface Name Cache</em>' map.
	 * @see org.emftext.sdk.concretesyntax.ConcretesyntaxPackage#getGenClassCache__qualifiedInterfaceNameCache()
	 * @model mapType="org.emftext.sdk.concretesyntax.GenClassCacheEntry<org.eclipse.emf.codegen.ecore.genmodel.GenClass, org.eclipse.emf.ecore.EString>"
	 * @generated
	 */
	EMap<GenClass, String> get_qualifiedInterfaceNameCache();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Return the qualified name of the interface for the given GenClass.
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='if (!get_qualifiedInterfaceNameCache().containsKey(genClass)) {\n\tjava.lang.String qualifiedInterfaceName = genClass.getQualifiedInterfaceName();\n\tget_qualifiedInterfaceNameCache().put(genClass, qualifiedInterfaceName);\n}\nreturn get_qualifiedInterfaceNameCache().get(genClass);'"
	 * @generated
	 */
	String getQualifiedInterfaceName(GenClass genClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 *  Returns an escaped version of the qualified name of the interface for 
	 *  the given GenClass. Underscores and dots are replaced to be able to use 
	 *  the returned name, for example, as method name.
	 * 
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='java.lang.String interfaceName = getQualifiedInterfaceName(genClass);\njava.lang.String escapedName = interfaceName.replace(\"_\", \"_005f\");\nescapedName = escapedName.replace(\".\", \"_\");\nreturn escapedName;'"
	 * @generated
	 */
	String getEscapedTypeName(GenClass genClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Checks whether the given GenClass has a map type.
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return java.util.Map.Entry.class.getName().equals(genClass.getEcoreClass().getInstanceClassName());'"
	 * @generated
	 */
	boolean hasMapType(GenClass genClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Checks whether the given list of GenClasses contains a GenClass
	 * with the same name and namespace URI as the given GenClass.
	 * <!-- end-model-doc -->
	 * @model listMany="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='for ( org.eclipse.emf.codegen.ecore.genmodel.GenClass entry : list) {\n\torg.eclipse.emf.ecore.EClass entryClass = entry.getEcoreClass();\n\torg.eclipse.emf.ecore.EClass oClass = genClass.getEcoreClass();\n\tif (entryClass.getName().equals(oClass.getName())\n\t\t\t&& entryClass.getEPackage().getNsURI().equals(\n\t\t\t\t\toClass.getEPackage().getNsURI())) {\n\t\treturn true;\n\t}\n}\nreturn false;'"
	 * @generated
	 */
	boolean containsEqualByName(EList<GenClass> list, GenClass genClass);

} // GenClassCache
