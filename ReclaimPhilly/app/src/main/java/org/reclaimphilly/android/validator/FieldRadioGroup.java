package org.reclaimphilly.android.validator;

import java.util.ArrayList;

import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by gosullivan on 2/19/14.
 */
public class FieldRadioGroup implements IValidator
{
    //The underlying control this field is representing
    private RadioGroup control;
    //Because whether required or not is so essential
    //give it a special status.
    private boolean required = true;

    //A list of value validators to be attached
    private ArrayList<IValueValidator> valueValidatorList
            = new ArrayList<IValueValidator>();

    public FieldRadioGroup(RadioGroup rg) {
        this(rg, true);
    }
    public FieldRadioGroup(RadioGroup rg, boolean inRequired) {
        control = rg;
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

        int id = control.getCheckedRadioButtonId();

        if (id == -1)
        {
            //invalid string
            if (required)
            {
                warnRequiredField();
                return false;
            }
        }

        //All validators passed
        return true;
    }//eof-validate

    private void warnRequiredField() {
        setErrorMessage("This is a required field");
    }
    public void setErrorMessage(String message)	{
//        control.setError(message);
    }
    public String getValue() {

        RadioButton radioBttn = (RadioButton) control.getChildAt(control.getCheckedRadioButtonId());
        return radioBttn.getText().toString();
    }
}//eof-class