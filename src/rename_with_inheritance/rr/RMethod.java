package rename_with_inheritance.rr;

import rename_with_inheritance.rr.printing.SourceFile;
import rename_with_inheritance.rr.printing.SourceFile.OverlappingEditException;

public class RMethod {
	
	String name;
	public RMethod[] overridenMethods = new RMethod[] {};
	public RMethod[] overridingMethods = new RMethod[] {};
	public RTypeDeclaration declaringClass;
	
	int nameStartingLocation;
	int nameLength;
	
	SourceFile file;
	
	public RMethod(SourceFile file, RTypeDeclaration declaringClass, String name, int nameStartingLocation, int nameLength) {
		this.file = file;
		this.declaringClass = declaringClass;
		this.name = name;
		this.nameStartingLocation = nameStartingLocation;
		this.nameLength = nameLength;
	}
	
	public RMethod(RTypeDeclaration declaringClass, String name) {
		this.declaringClass = declaringClass;
		this.name = name;
		file = null;
	}

	public void rename(String newName) throws OverlappingEditException, HasNoSourceException {
		if (file == null) {
			throw new HasNoSourceException("Could not rename " + declaringClass.name + '.' + name + " to " + newName);
		} else {
			name = newName;
			file.replace(nameStartingLocation, nameLength, newName);
		}
	}

	public boolean overrides(RMethod method) {
		for (RMethod m : overridenMethods) if (method == m) return true;
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasSource() {
		return file != null;
	}

}
