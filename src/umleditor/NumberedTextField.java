package umleditor;

import javax.swing.JTextField;

/**
 * TextField that also is associated with a FieldType and an integer. Used in the {@link EditPanel} to make editing
 * classes easier. The number in a NumberedTextField is not relevant if it is of type ClassName. The number associated
 * with a NumberedTextField is the index of the attribute or method it contains - editing the field will cause that
 * attribute or method to be edited.
 */
public class NumberedTextField extends JTextField
{
	/**
	 * Type associated with a {@link NumberedTextField}.
	 */
	public enum FieldType
	{
		/**
		 * Attribute type.
		 */
		Attribute,
		/**
		 * Method type.
		 */
		Method,
		/**
		 * ClassName type.
		 */
		ClassName
	}

	private static final long serialVersionUID = 3673040587471673525L;
	private int numberIndex;
	private FieldType myType;

	/**
	 * Constructs a new NumberedTextField which contains the given initial text, and is associated with the number and
	 * FieldType provided. Number is only meaningful if FieldType is Attribute or Method
	 * 
	 * @param initialContents
	 * @param number
	 * @param type
	 */
	public NumberedTextField(String initialContents, int number, FieldType type)
	{
		super(initialContents);
		numberIndex = number;
		myType = type;
	}

	/**
	 * Returns the integer associated with this text field.
	 * 
	 * @return - this text field's associated integer.
	 */
	public int getNumberIndex()
	{
		return (numberIndex);
	}

	/**
	 * Returns the type of this field.
	 * 
	 * @return - the text field's type
	 */
	public FieldType getType()
	{
		return (myType);
	}
}
