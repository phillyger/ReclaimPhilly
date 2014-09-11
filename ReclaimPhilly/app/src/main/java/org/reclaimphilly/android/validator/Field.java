package org.reclaimphilly.android.validator;

import java.util.ArrayList;

import android.widget.TextView;

/*
 * A self validating entity. 
 * Implements IValidator.
 * 
 * You can attach any number of IValueValidators
 * Checks all the validators in the order indicated
 * Does a setError on the field if a validator fails
 * 
 * validation for the "required" field is built in.
 * 
 * See FormFragment to see how these fields are used
 * 
 */
public class Field
implements IValidator
{
	 //The underlying control this field is representing
     private TextView control;
     //Because whether required or not is so essential
     //give it a special status.
     private boolean required = true;
     
     //A list of value validators to be attached
     private ArrayList<IValueValidator> valueValidatorList 
     		= new ArrayList<IValueValidator>();

     public Field(TextView tv) {
    	 this(tv, true);
     }
     public Field(TextView tv, boolean inRequired) {
    	 control = tv;
    	 required = inRequired;
     }
     

     //Validate if it is a required field first.
     //Also run through all the value validators.
     //Stop on the first validator that fails.
     //Show the error message from the failed validator.
     //Use the android setError to show the errors.
     @Override
     public boolean validate() 
     {
		String value = getValue();
		if (StringUtils.invalidString(value))
		{
			//invalid string
			if (required)
			{
				warnRequiredField();
				return false;
			}
		}
		for(IValueValidator validator: valueValidatorList)
		{
			boolean result = validator.validateValue(getValue());
		    if (result == true) continue;
		    if (result == false)
		    {
		    	//this validator failed
		    	String errorMessage = validator.getErrorMessage();
		    	setErrorMessage(errorMessage);
		    	return false;
		    }
		}//eof-for
		//All validators passed
		return true;
     }//eof-validate
	
	private void warnRequiredField() {
		setErrorMessage("This is a required field");
	}
	public void setErrorMessage(String message)	{
		control.setError(message);
	}
	public String getValue() {
		return this.control.getText().toString();
	}
}//eof-class
