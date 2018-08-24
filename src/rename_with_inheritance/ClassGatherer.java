package rename_with_inheritance;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.*;

import rename_with_inheritance.rr.RClass;
import rename_with_inheritance.rr.RClassBuilder;
import rename_with_inheritance.rr.RInterface;
import rename_with_inheritance.rr.RInterfaceBuilder;
import rename_with_inheritance.rr.RMethod;
import rename_with_inheritance.rr.RMethodBuilder;
import rename_with_inheritance.rr.printing.SourceFile;


public class ClassGatherer extends ASTVisitor {
	
	String gatheredMethodName;
	
	HashMap<ITypeBinding, RClassBuilder> classBuilders = new HashMap<>();
	HashMap<ITypeBinding, RInterfaceBuilder> interfaceBuilders = new HashMap<>();
	HashMap<IMethodBinding, RMethodBuilder> methodBuilders = new HashMap<>();
	
	public SourceFile file;

	public ClassGatherer(String gatheredMethodName) {
		this.gatheredMethodName = gatheredMethodName;
	}
	
	public boolean visit(TypeDeclaration td) {
		String[] packagePath = ((CompilationUnit)td.getRoot()).getPackage().getName().toString().trim().split(".");
		String className = td.getName().getIdentifier();
		ITypeBinding[] extendedInterfaces = td.resolveBinding().getInterfaces();
		if (td.isInterface()) {
			interfaceBuilders.put(td.resolveBinding(), new RInterfaceBuilder(td.resolveBinding(), packagePath, className, extendedInterfaces));
		} else {
			Type sc = td.getSuperclassType();
			ITypeBinding superClass;
			if (sc == null) {
				superClass = td.getAST().resolveWellKnownType("java.lang.Object");
			} else {
				superClass = sc.resolveBinding();
			}
			classBuilders.put(td.resolveBinding(), new RClassBuilder(td.resolveBinding(), packagePath , className, superClass, extendedInterfaces));
		}
		return true;
	}
	public boolean visit(MethodDeclaration md) {
		if (!Modifier.isStatic(md.getModifiers()) && md.getName().getIdentifier().equals(gatheredMethodName)) {
			methodBuilders.put(md.resolveBinding(), new RMethodBuilder(md, file));
		}
		return true;
	}
	
	public HashSet<RClass> buildClasses() {
		HashSet<RClass> builtClasses = new HashSet<>();
		for (RClassBuilder rcb : classBuilders.values()) {
			builtClasses.add(rcb.build(classBuilders, interfaceBuilders));
		}
		return builtClasses;
	}
	
	public HashSet<RInterface> buildInterfaces() {
		HashSet<RInterface> builtInterfaces = new HashSet<>();
		for (RInterfaceBuilder rib : interfaceBuilders.values()) {
			builtInterfaces.add(rib.build(interfaceBuilders));
		}
		return builtInterfaces;
	}
	
	public HashSet<RMethod> buildMethods() {
		HashSet<RMethod> builtMethods = new HashSet<>();
		for (RMethodBuilder rmb : methodBuilders.values()) {
			builtMethods.add(rmb.build(classBuilders, interfaceBuilders, methodBuilders));
		}
		return builtMethods;
	}

}
