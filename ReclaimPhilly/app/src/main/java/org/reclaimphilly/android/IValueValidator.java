package org.reclaimphilly.android;

public interface IValueValidator 
{
	boolean validateValue(String value);
	String getErrorMessage();
}
