package rename_with_inheritance.rr;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import rename_with_inheritance.rr.printing.SourceFile;

public class RMethodBuilder {
	
	public IMethodBinding self;
	int nameStartingLocation;
	int nameLength;
	SourceFile file;
	
	RMethod built = null;
	HashSet<RMethod> overridenMethods = new HashSet<>();
	HashSet<RMethod> overridingMethods = new HashSet<>();

	public RMethodBuilder(MethodDeclaration method, SourceFile file) {
		self = method.resolveBinding();
		nameStartingLocation = method.getName().getStartPosition();
		nameLength = method.getName().getLength();
		this.file = file;
	}
	public RMethodBuilder(IMethodBinding self) {
		this.self = self;
		file = null;
	}
	
	public RMethod build(HashMap<ITypeBinding, RClassBuilder> classMappings, HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings, 
			HashMap<IMethodBinding, RMethodBuilder> methodMappings) {
		if (built == null) {
			HashMap<ITypeBinding, RTypeDeclarationBuilder> types = new HashMap<>();
			types.putAll(classMappings);
			types.putAll(interfaceMappings);
			RTypeDeclaration declaringClass = types.get(self.getDeclaringClass()).buildRTypeDeclaration(classMappings, interfaceMappings);
			if (file == null) {
				built = new RMethod(declaringClass, self.getName());
			} else {
				built = new RMethod(
						file, 
						declaringClass, 
						self.getName(), 
						nameStartingLocation, 
						nameLength);
			}
			addOverridenMethods(self.getDeclaringClass(), classMappings, interfaceMappings, methodMappings);
			addOverridingMethods(classMappings, interfaceMappings, methodMappings);
			built.overridenMethods = overridenMethods.toArray(new RMethod[] {});
			built.overridingMethods = overridingMethods.toArray(new RMethod[] {});
		}
		return built;
	}
	void addOverridenMethods(ITypeBinding above, HashMap<ITypeBinding, RClassBuilder> classMappings, 
			HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings, HashMap<IMethodBinding, RMethodBuilder> methodMappings) {
		for (IMethodBinding imb : above.getDeclaredMethods()) {
			if (self.overrides(imb)) {
				RMethodBuilder builder = methodMappings.get(imb);
				if (builder == null) {
					builder = new RMethodBuilder(imb);
				}
				overridenMethods.add(builder.build(classMappings, interfaceMappings, methodMappings));
			}
		}
		ITypeBinding superClass = above.getSuperclass();
		if (superClass != null) {
			addOverridenMethods(superClass, classMappings, interfaceMappings, methodMappings);
		}
		
		for (ITypeBinding i : above.getInterfaces()) 
			addOverridenMethods(i, classMappings, interfaceMappings, methodMappings);
	}
	
	@SuppressWarnings("unchecked")
	void addOverridingMethods(HashMap<ITypeBinding, RClassBuilder> classMappings, 
			HashMap<ITypeBinding, RInterfaceBuilder> interfaceMappings, HashMap<IMethodBinding, RMethodBuilder> methodMappings) {
		HashSet<IMethodBinding> overriding = new HashSet<>();
		for (IMethodBinding imb : methodMappings.keySet()) {
			if (imb.overrides(self)) {
				overriding.add(imb);
			}
		}
		//	Iterate until a fixed point.
		HashSet<RMethod> last = new HashSet<>();
		while (!last.equals(overriding)) {
			last = (HashSet<RMethod>) overriding.clone();
			for (IMethodBinding possiblyOverriding : methodMappings.keySet()) {
				for (IMethodBinding possiblyOverriden : (HashSet<IMethodBinding>)overriding.clone()) {
					if (possiblyOverriding.overrides(possiblyOverriden)) {
						overriding.add(possiblyOverriding);
					}
				}
			}
		}
		for (IMethodBinding imb : overriding) {
			overridingMethods.add(methodMappings.get(imb).build(classMappings, interfaceMappings, methodMappings));
		}
	}
	public boolean isEditable() {
		return file == null;
	}
}
