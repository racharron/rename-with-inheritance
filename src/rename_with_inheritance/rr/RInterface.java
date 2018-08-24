package rename_with_inheritance.rr;

public final class RInterface extends RTypeDeclaration {
	
	public RInterface[] extendedInterfaces = new RInterface[] {};

	public RInterface(String[] packagePath, String interfaceName) {
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = interfaceName;
	}

	public RInterface(String[] packagePath, String interfaceName, RInterface[] extendedInterfaces) {
		this.pkg = RPackage.fromComponents(packagePath);
		this.name = interfaceName;
		this.extendedInterfaces = extendedInterfaces;
	}
	
}
