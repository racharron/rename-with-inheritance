package rename_with_inheritance.rr.printing;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class SourceFile {
	
	ICompilationUnit compilationUnit;
	CompilationUnit cuAst;
	
	ArrayList<Edit> edits = new ArrayList<>();
	
	public SourceFile(ICompilationUnit compilationUnit, CompilationUnit cuAst) {
		this.compilationUnit = compilationUnit;
		this.cuAst = cuAst;
	}
	
	public Edit replace(int startingPosition, int length, String replacement) throws OverlappingEditException {
		//	TODO: replace with binary search
		for (Edit edit : edits) {
			if (edit.startingPosition <= startingPosition && startingPosition < edit.startingPosition + edit.length
					|| startingPosition <= edit.startingPosition && edit.startingPosition < startingPosition + length) {
				throw new OverlappingEditException(edit, new Edit(startingPosition, length, replacement));
			}
		}
		Edit edit = new Edit(startingPosition, length, replacement);
		inserted: do {
			for (int i = 0; i < edits.size(); i++) {
				if (edits.get(i).startingPosition > startingPosition) {
					edits.add(i, edit);
					break inserted;
				}
			}
			edits.add(edit);
		} while (false);
		return edit;
	}
	
	public void applyEdits() throws ExecutionException, JavaModelException {
		ICompilationUnit workingCopy = this.compilationUnit.getWorkingCopy(null);
		String original = workingCopy.getSource();
		StringBuilder modified = new StringBuilder();
		int currentPosition = 0;
		for(Edit edit : edits) {
			modified.append(original.substring(currentPosition, edit.startingPosition));
			modified.append(edit.replacement);
			currentPosition = edit.startingPosition + edit.length;
		}
		modified.append(original.substring(currentPosition));
		
		String newText = modified.toString();
		System.out.println(newText);
		Document document = new Document(newText);
		
		cuAst.recordModifications();
		TextEdit edits = cuAst.rewrite(document, workingCopy.getJavaProject().getOptions(true));
		try {
			edits.apply(document);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException("Could not edit method name to name", e);
		}
		String newSource = document.get();
		workingCopy.getBuffer().setContents(newSource);
		workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
		//	I think this makes refactorings automatically save the file, but I'm not sure
		workingCopy.commitWorkingCopy(true, null);
		workingCopy.discardWorkingCopy();
	}
	
	public void commit() throws JavaModelException {
		ICompilationUnit workingCopy = compilationUnit.getWorkingCopy(null);
		workingCopy.commitWorkingCopy(true, null);
		workingCopy.discardWorkingCopy();
	}

	public class Edit {
		int startingPosition;
		int length;
		String replacement;
		public Edit(int startingPosition, int length, String replacement) {
			this.startingPosition = startingPosition;
			this.length = length;
			this.replacement = replacement;
		}
	}
	
	public class OverlappingEditException extends Exception {
		/**
		 * generated
		 */
		private static final long serialVersionUID = 92117004757708975L;
		public Edit preExistingEdit;
		public Edit attemptedEdit;
		public OverlappingEditException(Edit preExistingEdit, Edit attemptedEdit) {
			this.preExistingEdit = preExistingEdit;
			this.attemptedEdit = attemptedEdit;
		}
		
	}

}
