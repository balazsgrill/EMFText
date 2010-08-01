package org.emftext.sdk.quickfixes;

import java.io.File;

import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.resource.cs.ICsQuickFix;
import org.emftext.sdk.concretesyntax.resource.cs.mopp.CsQuickFix;

/**
 * A quick fix that removes an unused resolver class from the analysis package.
 */
public class RemoveResolverQuickFix extends CsQuickFix implements ICsQuickFix {

	private File resolverFile;

	public RemoveResolverQuickFix(ConcreteSyntax syntax, File resolverFile) {
		super("Remove unused resolver class", syntax);
		this.resolverFile = resolverFile;
	}

	@Override
	public void applyChanges() {
		resolverFile.delete();
		// TODO refresh resolver directory in workspace
	}
}
