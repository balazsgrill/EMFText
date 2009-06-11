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
package org.emftext.runtime.resource.impl;

import org.eclipse.emf.ecore.EObject;
import org.emftext.runtime.resource.IReferenceResolver;

/**
 * Base implementation for all generated reference resolvers. 
 * It implements the specifications from {@link IReferenceResolver}.
 * 
 * @param <ContainerType> the type of the container that contains
 * the reference that is resolved by this resolver
 * 
 * @param <ReferenceType> the type of the reference that is resolved 
 * by this resolver
 * 
 * @author Jendrik Johannes <jendrik.johannes@tu-dresden.de>
 */
public abstract class AbstractReferenceResolver<ContainerType extends EObject, ReferenceType extends EObject> implements IReferenceResolver<ContainerType, ReferenceType> {

}
