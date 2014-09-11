package org.reclaimphilly.android.validator;

public class EmailValueValidator 
implements IValueValidator
{
	@Override
	public boolean validateValue(String value) {
		if (value.indexOf('@') < 0)
		{
			//not found
			return false;
		}
		return true;
	}
	@Override
	public String getErrorMessage() {
		return "An email address must contain an @ sign";
	}
}
