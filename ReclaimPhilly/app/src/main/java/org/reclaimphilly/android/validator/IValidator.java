package org.reclaimphilly.android.validator;
/*
 * Ability to self validate.
 * Not only validate but also display any errors.
 * 
 * Typically one or more fields implement this behavior 
 * 
 * Implementing Classes:
 * Field
 * PasswordFieldRule
 * 
 * @see Field, IValueValidator, PasswordFieldRule, FormFragment
 */
public interface IValidator {
	public boolean validate();
}
