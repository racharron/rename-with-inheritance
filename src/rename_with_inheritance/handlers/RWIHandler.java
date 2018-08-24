package rename_with_inheritance.handlers;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import rename_with_inheritance.ClassGatherer;
import rename_with_inheritance.rr.*;
import rename_with_inheritance.rr.printing.SourceFile;
import rename_with_inheritance.rr.printing.SourceFile.OverlappingEditException;

import org.eclipse.jface.dialogs.MessageDialog;

public class RWIHandler extends AbstractHandler {
	
	static final String[] PACKAGE_PATH = {"p"};
	static final String CLASS_NAME = "A";
	static final String METHOD_NAME = "m";
	
	static final String PROJECT_NAME = "Refactoring Test";

	static final String NEW_METHOD_NAME = "n";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] iProjectList = wr.getProjects();
		boolean refactoringDone = false;
		for (IProject iProject : iProjectList) {
			if (! iProject.getName().equals(PROJECT_NAME)) break;
			IJavaProject iJavaProject = JavaCore.create(iProject);
			ClassGatherer cg = new ClassGatherer(METHOD_NAME);
			ArrayList<RMethod> allMethods = new ArrayList<>();
			ArrayList<SourceFile> sourceFiles = new ArrayList<>();
			ASTParser astParser = ASTParser.newParser(AST.JLS10);
			astParser.setResolveBindings(true);
			try {
				IPackageFragment[] iPackageFragmentList = iJavaProject.getPackageFragments();
				System.out.println("package fragments: " + iPackageFragmentList);
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						astParser.setSource(iCompilationUnit);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
						compilationUnit.accept(new ASTVisitor() {
							public boolean visit(PackageDeclaration pd) {
								var pkg = ((CompilationUnit)pd.getRoot()).getPackage();
								System.out.println("pkg name: " + pkg.getName());
								var binding = pkg.resolveBinding();
								if (binding == null) {
									System.out.println("binding for " + pkg + " is null");
								} else {
									System.out.println("getName " + binding.getName());
									System.out.println("toString " + binding.toString());
									System.out.println("getNameComponents " + binding.getNameComponents());
								}
								return true;
							}
						});
					}
				}
				for (IPackageFragment iPackageFragment : iPackageFragmentList) {
					if (iPackageFragment.getKind() != IPackageFragmentRoot.K_SOURCE) {
						continue;
					}
					ICompilationUnit[] iCompilationUnitList = iPackageFragment.getCompilationUnits();
					for (ICompilationUnit iCompilationUnit : iCompilationUnitList) {
						astParser.setSource(iCompilationUnit);
						CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
						cg.file = new SourceFile(iCompilationUnit, compilationUnit);
						sourceFiles.add(cg.file);
						compilationUnit.accept(cg);
						allMethods.addAll(cg.buildMethods());
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}

			RPackage selectedPackage = RPackage.fromComponents(PACKAGE_PATH);
			RMethod selectedMethod = allMethods.stream().reduce(null, (stored, current) -> {
				RMethod ret;
				if (current.declaringClass.pkg == selectedPackage && current.declaringClass.name.equals(CLASS_NAME)) 
					ret = current; 
				else 
					ret = stored;
				if (ret != null) System.out.println("stream: " + ret.declaringClass.name + '.' + ret.getName());
				else System.out.println("stream: null");
				return ret;
			});
			
			if (selectedMethod == null) {
				MessageDialog.openInformation(
						HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(),
						"Rename_with_inheritance",
						"Targeted method does not exist");
				throw new ExecutionException("Targeted method does not exist");
			}
			
			ArrayList<RMethod> methodsToBeRenamed = new ArrayList<RMethod>(Arrays.asList(selectedMethod.overridenMethods));
			methodsToBeRenamed.addAll(Arrays.asList(selectedMethod.overridingMethods));
			methodsToBeRenamed.add(selectedMethod);
			for (var m : methodsToBeRenamed) System.out.println(m.declaringClass.name + '.' + m.getName() + "  hasSource() =" + m.hasSource());
			System.out.println();
			for (RMethod method : methodsToBeRenamed) {
				System.out.println(method.declaringClass.name + "::" + method.getName());
				try {
					method.rename(NEW_METHOD_NAME);
				} catch (OverlappingEditException oee) {
					throw new ExecutionException("Could not rename exception", oee);
				} catch (HasNoSourceException hnse) {
					MessageDialog.openInformation(
							HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(),
							"Rename_with_inheritance",
							"Could not rename method due to a lack of a source\n"
							+ hnse.getMessage());
					return null;
				}
			}
			for (SourceFile file : sourceFiles) {
				try {
					file.applyEdits();
					//file.commit();
				} catch (JavaModelException jme) {
					throw new ExecutionException("Could not apply edits to " + file, jme);
				}
			}
			refactoringDone = true;
		}
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (refactoringDone) {
			MessageDialog.openInformation(
					window.getShell(),
					"Rename_with_inheritance",
					"Done");
		} else {
			MessageDialog.openInformation(
					window.getShell(),
					"Rename_with_inheritance",
					"Target project not found");
		}
		return null;
	}
	
}
