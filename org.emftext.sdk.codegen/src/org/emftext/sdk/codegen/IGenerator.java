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
package org.emftext.sdk.codegen;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * A basic generator interface which should be implemented by all generators 
 * in org.emftext.sdk.codegen.generators. Generators can create content for
 * arbitrary artifacts (e.g., Java classes). They do not care about whether 
 * this content is eventually put into a file or somewhere else.
 * 
 * @author Sven Karol (Sven.Karol@tu-dresden.de)
 */
public interface IGenerator {
	
	public boolean generate(PrintWriter out);
	
	public Collection<GenerationProblem> getCollectedProblems();
	public Collection<GenerationProblem> getCollectedErrors();

	public IGenerator newInstance(GenerationContext context);
}