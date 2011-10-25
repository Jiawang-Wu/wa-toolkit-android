package com.windowsazure.samples.android.storageclient.tests;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SuperClassTypeParameterCreator {
	@SuppressWarnings("unchecked")
	public static <T> T create(Object object, int index) 
	{
		ParameterizedType objectSuperClass = (ParameterizedType) object.getClass().getGenericSuperclass();
		Type superClassTypeArgument = objectSuperClass.getActualTypeArguments()[index];
		
		Class<T> instanceType;
		
		if (superClassTypeArgument instanceof ParameterizedType) 
		{
			instanceType = (Class<T>) ((ParameterizedType) superClassTypeArgument).getRawType();
		}
		else 
		{
			instanceType = (Class<T>) superClassTypeArgument;
		}
		try 
		{
			return instanceType.newInstance();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
}
