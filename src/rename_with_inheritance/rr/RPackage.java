package rename_with_inheritance.rr;

import java.util.ArrayList;
import java.util.Arrays;

public class RPackage {
	
	String[] path;
	
	private static ArrayList<RPackage> packages = new ArrayList<>();

	private RPackage(String packagePath) {
		ArrayList<String> accumulatedComponents = new ArrayList<>();
		for (String component : packagePath.split("\\.")) {
			accumulatedComponents.add(component.trim());
		}
	}
	
	private RPackage(String[] components) {
		path = components;
	}
	
	public static RPackage fromComponents(String[] components) {
		return unique(new RPackage(components));
	}
	public static RPackage fromPath(String path) {
		return unique(new RPackage(path));
	}
	
	private static RPackage unique(RPackage pkg) {
		for (RPackage inSet : packages) {
			if (inSet.equals(pkg)) {
				return inSet;
			}
		}
		packages.add(pkg);
		return pkg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(path);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RPackage other = (RPackage) obj;
		if (!Arrays.equals(path, other.path))
			return false;
		return true;
	}
	
	

}
