package rename_with_inheritance.rr;

public final class RClass extends RTypeDeclaration {

	public static RClass object = new RClass(new String[] { "java", "lang" }, "Object", (RClass)null, new String[] {});

	public RClass superClass = object;
	public RInterface[] implementedInterfaces = new RInterface[] {};

	public String[] declaredMethodNames = null;

	public RClass(String[] packagePath, String className, String[] declaredMethodNames) {
		this.declaredMethodNames = declaredMethodNames;
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = className;
	}

	public RClass(String[] packagePath, String className, RClass superClass, String[] declaredMethodNames) {
		this.declaredMethodNames = declaredMethodNames;
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = className;
		this.superClass = superClass;
	}

	public RClass(String[] packagePath, String className, RInterface[] implementedInterfaces,
			String[] declaredMethodNames) {
		this.declaredMethodNames = declaredMethodNames;
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = className;
		this.implementedInterfaces = implementedInterfaces;
	}

	public RClass(String[] packagePath, String className, RClass superClass, RInterface[] implementedInterfaces,
			String[] declaredMethodNames) {
		this.declaredMethodNames = declaredMethodNames;
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = className;
		this.superClass = superClass;
		this.implementedInterfaces = implementedInterfaces;
	}
	
	public boolean isSubclassOf(RClass rclass) {
		for (RClass current = superClass; current != null; current = current.superClass) {
			if (rclass == current) return true;
		}
		return false;
	}
}
