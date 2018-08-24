package rename_with_inheritance.rr;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class RClassBuilder implements RTypeDeclarationBuilder {
	
	public String[] packagePath;
	public String className;
	
	RClass built = null;
	ITypeBinding self;
	ITypeBinding superClass;
	ITypeBinding[] implementedInterfaces = new ITypeBinding[] {};
	
	public RClassBuilder(ITypeBinding self, String[] packagePath, String className, ITypeBinding superClass) {
		this.self = self;
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
	}
	public RClassBuilder(ITypeBinding self, String[] packagePath, String className, ITypeBinding superClass, ITypeBinding[] implementedInterfaces) {
		this.self = self;
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
		this.implementedInterfaces = implementedInterfaces;
	}
	
	public RClass build(HashMap<ITypeBinding, RClassBuilder> classMappings, HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings) {
		if (built == null ) {
			built = new RClass(packagePath, className, new String[] {});
			RClassBuilder b = classMappings.get(superClass);
			if (b == null) {
				if (this.superClass == null) {
					built = RClass.object;
					return built;
				} else {
					RClassBuilder superClassBuilder = new RClassBuilder(
							superClass,
							superClass.getPackage().getNameComponents(),
							superClass.getName(),
							superClass.getSuperclass(),
							superClass.getInterfaces());
					classMappings.put(superClass, superClassBuilder);
					b = superClassBuilder;
				}
			}
			built.superClass = b.build(classMappings, interfaceMappings);
			ArrayList<RInterface> iis = new ArrayList<>();
			for (ITypeBinding tb : implementedInterfaces) {
				RInterfaceBuilder builder = interfaceMappings.get(tb);
				if (builder == null) {
					RInterfaceBuilder interfaceBuilder = new RInterfaceBuilder(
							tb, 
							tb.getPackage().getNameComponents(), 
							tb.getName(), 
							tb.getInterfaces());
					interfaceMappings.put(tb, interfaceBuilder);
					builder = interfaceBuilder;
				}
				iis.add(builder.build(interfaceMappings));
			}
			built.implementedInterfaces = iis.toArray(new RInterface[] {});
		}
		return built;
	}
	
	@Override
	public RTypeDeclaration buildRTypeDeclaration(HashMap<ITypeBinding, RClassBuilder> classMappings,
			HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings) {
		return this.build(classMappings, interfaceMappings);
	}
	
}
