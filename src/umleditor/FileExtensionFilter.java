package umleditor;

import java.io.File;

/**
 * Class to filter out all but *.xuml files.
 * 
 */
class FileExtensionFilter extends javax.swing.filechooser.FileFilter
{
	@Override
	public boolean accept(File file)
	{
		String filename = file.getName();
		return filename.endsWith(".xuml");
	}

	@Override
	public String getDescription()
	{
		return "*.xuml (XML formatted UML diagram)";
	}
}
