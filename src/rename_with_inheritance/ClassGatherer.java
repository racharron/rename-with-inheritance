package rename_with_inheritance;

import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassGatherer extends ASTVisitor {
	
	HashSet<RClassBuilder> classes = new HashSet<>();
	HashSet<RInterfaceBuilder> interfaces = new HashSet<>();

	public ClassGatherer() {
	}
	
	public boolean visit(TypeDeclaration td) {
		if (td.isInterface()) {
			
			interfaces.add(new RInterfaceBuilder(packagePath, className, extendedInterfaces));
		} else {
			
		}
		return true;
	}

}
