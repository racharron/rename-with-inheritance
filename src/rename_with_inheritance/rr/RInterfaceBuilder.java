package rename_with_inheritance.rr;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

public class RInterfaceBuilder implements RTypeDeclarationBuilder {

	public String[] packagePath;
	public String interfaceName;
	
	ITypeBinding self;
	RInterface built = null;
	ITypeBinding[] extendedInterfaces = new ITypeBinding[] {};
	
	public RInterfaceBuilder(ITypeBinding self, String[] packagePath, String interfaceName, ITypeBinding[] extendedInterfaces) {
		this.self = self;
		this.packagePath = packagePath;
		this.interfaceName = interfaceName;
		this.extendedInterfaces = extendedInterfaces;
	}
	
	public RInterface build(HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings) {
		if (built == null) {
			built = new RInterface(packagePath, interfaceName);
			ArrayList<RInterface> eis = new ArrayList<>();
			for (ITypeBinding tb : extendedInterfaces) {
				eis.add(interfaceMappings.get(tb).build(interfaceMappings));
			}
			built.extendedInterfaces = eis.toArray(new RInterface[] {});
		}
		return built;
	}
	
	@Override
	public RTypeDeclaration buildRTypeDeclaration(HashMap<ITypeBinding, RClassBuilder> classMappings,
			HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings) {
		// TODO Auto-generated method stub
		return this.build(interfaceMappings);
	}
}
