package rename_with_inheritance.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class RWIHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iProjectList = wr.getProjects();
		for (IProject iProject : iProjectList) {
			IJavaProject iJavaProject = JavaCore.create(iProject);
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						ICompilationUnit workingCopy = iCompilationUnit.getWorkingCopy(null);
						ASTParser astParser = ASTParser.newParser(AST.JLS10);
						astParser.setResolveBindings(true);
						astParser.setSource(workingCopy);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
						
						
						
						//	TODO: fixme (code copied drom rename_to_name
						compilationUnit.accept(mnlg);
						if (mnlg.found) {
							String original = workingCopy.getSource();
							System.out.println(original);
							Document document = new Document(
									original.substring(0, mnlg.location)
									+ "name"
											+ original.substring(mnlg.location + mnlg.length, original.length()));
							compilationUnit.recordModifications();
							TextEdit edits = compilationUnit.rewrite(document, workingCopy.getJavaProject().getOptions(true));
							try {
								edits.apply(document);
							} catch (Exception e) {
								e.printStackTrace();
								throw new ExecutionException("Could not edit method name to name", e);
							}
							String newSource = document.get();
							workingCopy.getBuffer().setContents(newSource);
							workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
							workingCopy.commitWorkingCopy(true, null);
							workingCopy.discardWorkingCopy();
							return null;
						}
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Rename_with_inheritance",
				"Done");
		return null;
	}
}
