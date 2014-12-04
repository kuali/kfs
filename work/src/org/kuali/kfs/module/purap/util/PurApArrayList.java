/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.util;

import java.beans.Beans;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
/**
 * 
 * This class is setup to allow an argument to a created ArrayList (it could be a 
 * possible extension to the other ArrayList
 */
public class PurApArrayList extends ArrayList {
    // private static final long serialVersionUID = 6238521951259126730L;
    private final Class listObjectType;
    private final Class[] argumentClasses;
    private final Object[] arguments;

    /**
     * 
     * Default Constructor
     * @param listObjectType the class
     */
    public PurApArrayList(Class listObjectType) {
        this(listObjectType, null, null);
    }

    /**
     * 
     * Constructor
     * @param listObjectType the object type
     * @param methodClasses classes
     * @param methodArguments arguments
     */
    public PurApArrayList(Class listObjectType, Class[] methodClasses, Object[] methodArguments) {
        super();

        Class[] assignArgumentClasses = null;
        Object[] assignArguments = null;

        if (listObjectType == null) {
            throw new RuntimeException("class type for list is required.");
        }

        // attempt to get an instance of the class to check it has a visible default constructor
        if (methodClasses == null && methodArguments == null) {
            try {

                Object listObj = listObjectType.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
        }
        else {
            try {
                listObjectType.getConstructor(methodClasses).newInstance(methodArguments);
            }
            catch (SecurityException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("unable to get instance of class" + listObjectType.getName());
            }
            assignArgumentClasses = methodClasses;
            assignArguments = methodArguments;
        }

        this.listObjectType = listObjectType;
        this.argumentClasses = assignArgumentClasses;
        this.arguments = assignArguments;
    }

    /**
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, Object element) {
        checkType(element);
        super.add(index, element);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
        checkType(o);
        return super.add(o);
    }

    /**
     * @see java.util.List#get(int)
     */
    public Object get(int index) {
        growArray(index);
        return super.get(index);
    }

    /**
     * @see java.util.List#set(int, java.lang.Object)
     */
    public Object set(int index, Object element) {
        growArray(index);
        return super.set(index, element);
    }


    /**
     * Adds new instances of type listObjectType to the arraylist until the size of the list is greater than the index required.
     * @param index the index to grow to
     */
    private void growArray(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be positive.");
        }
        // ensureCapacity(index); // Increments modCount

        while (size() <= index) {
            try {
                if (this.arguments == null && this.argumentClasses == null) {
                    super.add(listObjectType.newInstance());
                }
                else {
                    super.add(listObjectType.getConstructor(argumentClasses).newInstance(arguments));
                }
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (SecurityException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot get new instance of class " + listObjectType.getName());
            }
        }
    }


    /**
     * Checks the type of an element matches the underlying list type.
     */
    private void checkType(Object element) {
        if (element != null) {
            if (!Beans.isInstanceOf(element, listObjectType)) {
                throw new RuntimeException("element is not of correct type.");
            }
        }
    }

    public Class getListObjectType() {
        return listObjectType;
    }

}
