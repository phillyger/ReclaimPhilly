package org.reclaimphilly.android.validator;

import java.util.ArrayList;
import android.view.View;

/*
 * Uses the field validator framework to do form validation
 * for derived activities.
 * Most useful for activities that are mainly forms.
 * 
 * What it does
 * ************
 * 1. overrides the setContentView() to call
 * initializeFormFields()
 * 2. In that function gather your fields and
 * register them as validators
 * 3. Knows how to validate the validator set
 * 
 * How to use it
 * ****************
 * 1. Extend FormFragment
 * 2. Override initializeFormFields()
 * 3. Gather your fields in step2
 * 4. Register fields through addValidator()
 * 5. Call validate() of form activity
 * 6. The field validators will set errors based on Android framework
 * 
 * initializeFormFields()
 * ************************************** 
 * int min=5;
 * int max=10;
 * String error = "Should be between 5 and 10";
 * MinMaxValidator mmv = new MinMaxValidator(5,10,error);
 * 
 * TextView control; boolean required=true;
 * Field f = new Field(control, true);
 * f.addValidator(mmv);
 * f.addValidatort(..others..);
 * 
 * See SignupSuccessActivity to see how this is used
 * 
 */
public abstract class FormFragment
extends BaseFragment
{
	public FormFragment(String inTag) {
		super(inTag);
	}

	//Provide an opportunity to add fields
	//to this form. This is called a hook method
	protected abstract void initializeFormFields(View v);
	
	//See how the above hook method is called
	//whenever the content view is set on this activity
	//containing the layout fields.
//	@Override
//	public void setContentView(int viewid) {
//		super.setContentView(viewid);
//		initializeFormFields();
//	}
	
	//A set of fields or validators to call validation on
	private ArrayList<IValidator> ruleSet = new ArrayList<IValidator>();
	
	//Add a field which is also a validator
	public void addValidator(IValidator v)	{
		ruleSet.add(v);
	}
	
	//Validate the every field in the form
	//Call this method when a form is submitted.
	public boolean validateForm()
	{
		boolean finalResult = true;
		for(IValidator v: ruleSet)
		{
			boolean result = v.validate();
			if (result == false)
			{
				finalResult = false;
			}
			//if true go around
			//if all true it should stay true
		}
		return finalResult;
	}
}//eof-class
