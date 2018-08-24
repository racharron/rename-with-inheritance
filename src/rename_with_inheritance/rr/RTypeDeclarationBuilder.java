package rename_with_inheritance.rr;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

public interface RTypeDeclarationBuilder {
	RTypeDeclaration buildRTypeDeclaration(HashMap<ITypeBinding, RClassBuilder> classMappings, 
			HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings);
}
