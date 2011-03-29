package umleditor;

import javax.swing.JTextField;

public class NumberedTextField extends JTextField
{
	public enum FieldType {Attribute, Method, ClassName}
	private static final long serialVersionUID = 3673040587471673525L;
	private int numberIndex;
	private FieldType myType;

	public NumberedTextField(String initialContents, int number, FieldType type)
	{
		super(initialContents);
		numberIndex = number;
		myType = type;
	}
	
	public int getNumberIndex()
	{
		return (numberIndex);
	}
	
	public FieldType getType()
	{
		return (myType);
	}
}
