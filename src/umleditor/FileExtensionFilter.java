package umleditor;

import java.io.File;

/**
 * Class to filter out all but *.xuml files.
 * 
 */
class FileExtensionFilter extends javax.swing.filechooser.FileFilter
{
	/**
	 * The file extension accepted by this file filter
	 */
	public static final String ACCEPTED_FILE_EXTENSION = ".xuml";

	/**
	 * Returns whether the file the file should be displayed or not
	 */
	public boolean accept(File file)
	{
		if (file.isDirectory ())
		{
			return (true);
		}
		String filename = file.getName();
		return filename.endsWith(ACCEPTED_FILE_EXTENSION);
	}

	/**
	 * Returns the string to be displayed in the JFileChooser
	 */
	public String getDescription()
	{
		return ("*" + ACCEPTED_FILE_EXTENSION + " (XML formatted UML diagram)");
	}
}
