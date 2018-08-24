package rename_with_inheritance.rr;

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.JavaModelException;

import rename_with_inheritance.rr.printing.SourceFile;

public class Context {
	HashSet<SourceFile> allEditedFiles = new HashSet<>();
	public Context() {
	}
	public void commit() throws ExecutionException, JavaModelException {
		for (SourceFile file : allEditedFiles) {
			file.applyEdits();
			file.commit();
		}
	}

}
