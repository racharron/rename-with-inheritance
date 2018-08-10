package rename_with_inheritance;

public final class RInterface {
	
	public String[] packagePath;
	public String interfaceName;
	
	public RInterface[] extendedInterfaces = new RInterface[] {};

	public RInterface(String[] packagePath, String interfaceName) {
		this.packagePath = packagePath;
		this.interfaceName = interfaceName;
	}

	public RInterface(String[] packagePath, String interfaceName, RInterface[] extendedInterfaces) {
		this.packagePath = packagePath;
		this.interfaceName = interfaceName;
		this.extendedInterfaces = extendedInterfaces;
	}
	
}
