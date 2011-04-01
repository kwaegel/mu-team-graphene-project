package umleditor;

import java.io.File;

/**
 * Class to filter out all but *.xuml files.
 * 
 */
class FileExtensionFilter extends javax.swing.filechooser.FileFilter
{
	
	public static final String ACCEPTED_FILE_EXTENSION = ".xuml";
	
	@Override
	public boolean accept(File file)
	{
		String filename = file.getName();
		return filename.endsWith(ACCEPTED_FILE_EXTENSION);
	}

	@Override
	public String getDescription()
	{
		return ("*" + ACCEPTED_FILE_EXTENSION + " (XML formatted UML diagram)");
	}
}
