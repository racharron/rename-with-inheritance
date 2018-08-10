package rename_with_inheritance;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class RInterfaceBuilder {

	public String[] packagePath;
	public String className;
	
	ITypeBinding[] extendedInterfaces = new ITypeBinding[] {};
	
	public RInterfaceBuilder(String[] packagePath, String className, ITypeBinding[] extendedInterfaces) {
		this.packagePath = packagePath;
		this.className = className;
		this.extendedInterfaces = extendedInterfaces;
	}
	
	RInterface build(HashMap<ITypeBinding, RInterface> interfaceMappings) {
		ArrayList<RInterface> eis = new ArrayList<>();
		for (ITypeBinding tb : extendedInterfaces) {
			eis.add(interfaceMappings.get(tb));
		}
		return new RInterface(packagePath, className, eis.toArray(new RInterface[] {}));
	}
}
