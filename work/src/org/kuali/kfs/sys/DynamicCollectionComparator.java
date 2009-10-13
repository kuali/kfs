/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;

/**
 * The comparator can dynamically implement java.util.Comparator and facilitate to sort a given colletion. This implementation is
 * based on an article by York Davis, which was published in Java Developer's Journal (http://java.sys-con.com/read/45837.htm).
 */
public class DynamicCollectionComparator<T> implements Comparator<T>, Serializable {
    private List<T> list;
    private String[] fieldNames;
    private SortOrder sortOrder;

    /**
     * enumerate the valid values of sort order
     */
    public enum SortOrder {
        ASC, DESC
    }

    /**
     * private constructs a DynamicCollectionComparator.java.
     * 
     * @param list the given collection that needs to be sorted
     * @param fieldName the field name ordered by
     * @param sortOrder the given sort order, either ascending or descending
     */
    private DynamicCollectionComparator(List<T> list, SortOrder sortOrder, String... fieldNames) {
        super();

        if (fieldNames == null || fieldNames.length <= 0) {
            throw new IllegalArgumentException("The input field names cannot be null or empty");
        }

        this.list = list;
        this.fieldNames = fieldNames;
        this.sortOrder = sortOrder;
    }

    /**
     * sort the given collection ordered by the given field name. Ascending order is used.
     * 
     * @param list the given collection that needs to be sorted
     * @param fieldName the field name ordered by
     */
    public static <C> void sort(List<C> list, String... fieldNames) {
        sort(list, SortOrder.ASC, fieldNames);
    }

    /**
     * sort the given collection ordered by the given field name
     * 
     * @param list the given collection that needs to be sorted
     * @param fieldName the field name ordered by
     * @param sortOrder the given sort order, either ascending or descending
     */
    public static <C> void sort(List<C> list, SortOrder sortOrder, String... fieldNames) {
        Comparator<C> comparator = new DynamicCollectionComparator<C>(list, sortOrder, fieldNames);
        Collections.sort(list, comparator);
    }

    /**
     * compare the two given objects for order. Returns a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object. If the objects implement Comparable interface, the objects compare with each
     * other based on the implementation; otherwise, the objects will be converted into Strings and compared as String.
     */
    public int compare(T object0, T object1) {
        int comparisonResult = 0;

        for (String fieldName : fieldNames) {
            comparisonResult = this.compare(object0, object1, fieldName);

            if (comparisonResult != 0) {
                break;
            }
        }

        return comparisonResult;
    }

    /**
     * compare the two given objects for order. Returns a negative integer, zero, or a positive integer as this object is less than,
     * equal to, or greater than the specified object. If the objects implement Comparable interface, the objects compare with each
     * other based on the implementation; otherwise, the objects will be converted into Strings and compared as String.
     */
    public int compare(T object0, T object1, String fieldName) {
        int comparisonResult = 0;
        
        try {
            Object propery0 = PropertyUtils.getProperty(object0, fieldName);
            Object propery1 = PropertyUtils.getProperty(object1, fieldName);

            if(propery0 == null && propery1 == null) {
                comparisonResult = 0;
            }
            else if(propery0 == null) {
                comparisonResult = -1;
            }
            else if(propery1 == null) {
                comparisonResult = 1;
            }            
            else if (propery0 instanceof Comparable) {
                Comparable comparable0 = (Comparable) propery0;
                Comparable comparable1 = (Comparable) propery1;

                comparisonResult = comparable0.compareTo(comparable1);
            }
            else {
                String property0AsString = ObjectUtils.toString(propery0);
                String property1AsString = ObjectUtils.toString(propery1);

                comparisonResult = property0AsString.compareTo(property1AsString);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("unable to compare property: " + fieldName, e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("unable to compare property: " + fieldName, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("unable to compare property: " + fieldName, e);
        }

        return comparisonResult * this.getSortOrderAsNumber();
    }

    /**
     * convert the sort order as an interger. If the sort order is "DESC" (descending order), reutrn -1; otherwise, return 1.
     * 
     * @return -1 if the sort order is "DESC" (descending order); otherwise, return 1.
     */
    public int getSortOrderAsNumber() {
        return sortOrder.equals(SortOrder.ASC) ? 1 : -1;
    }
}
