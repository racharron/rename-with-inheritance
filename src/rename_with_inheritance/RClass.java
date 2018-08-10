package rename_with_inheritance;

public final class RClass {
	
	public static RClass object = new RClass(new String[] {"java", "lang"}, "Object");
	
	public RClass superClass = object;
	public RInterface[] implementedInterfaces = new RInterface[] {};
	
	public String[] packagePath;
	public String className;
	
	public RClass(String[] packagePath, String className) {
		this.packagePath = packagePath;
		this.className = className;
	}
	
	public RClass(String[] packagePath, String className, RClass superClass) {
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
	}
	
	public RClass(String[] packagePath, String className, RInterface[] implementedInterfaces) {
		this.packagePath = packagePath;
		this.className = className;
		this.implementedInterfaces = implementedInterfaces;
	}

	public RClass(String[] packagePath, String className, RClass superClass, RInterface[] implementedInterfaces) {
		this.packagePath = packagePath;
		this.className = className;
		this.superClass = superClass;
		this.implementedInterfaces = implementedInterfaces;
	}
}
