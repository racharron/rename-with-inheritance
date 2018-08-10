package rename_with_inheritance;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class RClassBuilder {
	
	public String[] packagePath;
	public String className;
	
	ITypeBinding superClass;
	ITypeBinding[] implementedInterfaces = new ITypeBinding[] {};
	
	public RClassBuilder(String[] packagePath, String className, ITypeBinding superClass) {
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
	}
	public RClassBuilder(String[] packagePath, String className, ITypeBinding superClass, ITypeBinding[] implementedInterfaces) {
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
		this.implementedInterfaces = implementedInterfaces;
	}
	
	RClass build(HashMap<ITypeBinding, RClass> classMappings, HashMap<ITypeBinding, RInterface> interfaceMappings) {
		RClass superClass = classMappings.get(this.superClass);
		ArrayList<RInterface> iis = new ArrayList<>();
		for (ITypeBinding tb : implementedInterfaces) {
			iis.add(interfaceMappings.get(tb));
		}
		return new RClass(packagePath, className, superClass, iis.toArray(new RInterface[] {}));
	}
	
}
