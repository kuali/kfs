/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.format.FormatException;
import org.kuali.module.purap.PurapConstants;
import org.kuali.rice.KNSServiceLocator;
/**
 * Purap Object Utils.
 * Similar to the nervous system ObjectUtils this class contains methods to reflectively set and get values on
 * BusinessObjects that are passed in.
 */
public class PurApObjectUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApObjectUtils.class);

    /**
     * 
     * Populates a class using a base class to determine fields
     * 
     * @param base the class to determine what fields to copy
     * @param src the source class
     * @param target the target class
     * @param supplementalUncopyable a list of fields to never copy
     */
    public static void populateFromBaseClass(Class base, BusinessObject src, BusinessObject target, Map supplementalUncopyable) {
        List<String> fieldNames = new ArrayList<String>();
        Field[] fields = base.getDeclaredFields();


        for (Field field : fields) {
            if (!Modifier.isTransient(field.getModifiers())) {
                fieldNames.add(field.getName());
            }
            else {
                LOG.warn("field " + field.getName() + " is transient, skipping ");
            }
        }
        int counter = 0;
        for (String fieldName : fieldNames) {
            if ((isProcessableField(base, fieldName, PurapConstants.KNOWN_UNCOPYABLE_FIELDS)) && (isProcessableField(base, fieldName, supplementalUncopyable))) {
                attemptCopyOfFieldName(base.getName(), fieldName, src, target, supplementalUncopyable);
                counter++;
            }
        }
        LOG.debug("Population complete for " + counter + " fields out of a total of " + fieldNames.size() + " potential fields in object with base class '" + base + "'");
    }
    
    /**
     * 
     * True if a field is processable
     * 
     * @param baseClass the base class
     * @param fieldName the field name to detrmine if processable
     * @param excludedFieldNames field names to exclude
     * @return true if a field is processable
     */
    private static boolean isProcessableField(Class baseClass, String fieldName, Map excludedFieldNames) {
        if (excludedFieldNames.containsKey(fieldName)) {
            Class potentialClassName = (Class) excludedFieldNames.get(fieldName);
            if ((ObjectUtils.isNull(potentialClassName)) || (potentialClassName.equals(baseClass))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * Attempts to copy a field
     * @param baseClass the base class
     * @param fieldName the field name to determine if processable
     * @param sourceObject source object
     * @param targetObject target object
     * @param supplementalUncopyable
     */
    private static void attemptCopyOfFieldName(String baseClassName, String fieldName, BusinessObject sourceObject, BusinessObject targetObject, Map supplementalUncopyable) {
        try {

            Object propertyValue = ObjectUtils.getPropertyValue(sourceObject, fieldName);
            if ((ObjectUtils.isNotNull(propertyValue)) && (Collection.class.isAssignableFrom(propertyValue.getClass()))) {
                LOG.debug("attempting to copy collection field '" + fieldName + "' using base class '" + baseClassName + "' and property value class '" + propertyValue.getClass() + "'");
                copyCollection(fieldName, targetObject, propertyValue, supplementalUncopyable);
            }
            else {
                String propertyValueClass = (ObjectUtils.isNotNull(propertyValue)) ? propertyValue.getClass().toString() : "(null)";
                LOG.debug("attempting to set field '" + fieldName + "' using base class '" + baseClassName + "' and property value class '" + propertyValueClass + "'");
                ObjectUtils.setObjectProperty(targetObject, fieldName, propertyValue);
            }
        }
        catch (Exception e) {
            // purposefully skip for now
            // (I wish objectUtils getPropertyValue threw named errors instead of runtime) so I could
            // selectively skip
            LOG.debug("couldn't set field '" + fieldName + "' using base class '" + baseClassName + "' due to exception with class name '" + e.getClass().getName() + "'", e);
        }
    }

    /**
     * 
     * Copies a collection
     * 
     * @param fieldName field to copy
     * @param targetObject the object of the collection
     * @param propertyValue value to copy
     * @param supplementalUncopyable uncopyable fields
     * @throws FormatException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private static void copyCollection(String fieldName, BusinessObject targetObject, Object propertyValue, Map supplementalUncopyable) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Collection sourceList = (Collection) propertyValue;
        Collection listToSet = null;

        // materialize collections
        if (ObjectUtils.isNotNull(sourceList)) {
            ObjectUtils.materializeObjects(sourceList);
        }

        // TypedArrayList requires argument so handle differently than below
        if (sourceList instanceof TypedArrayList) {
            TypedArrayList typedArray = (TypedArrayList) sourceList;
            LOG.debug("collection will be typed using class '" + typedArray.getListObjectType() + "'");
            try {
                listToSet = new TypedArrayList(typedArray.getListObjectType());
            }
            catch (Exception e) {
                LOG.info("couldn't set class '" + propertyValue.getClass() + "' on collection... using TypedArrayList using ", e);
                listToSet = new ArrayList();
            }
        }
        else {
            try {
                listToSet = sourceList.getClass().newInstance();
            }
            catch (Exception e) {
                LOG.info("couldn't set class '" + propertyValue.getClass() + "' on collection..." + fieldName + " using " + sourceList.getClass());
                listToSet = new ArrayList();
            }
        }


        for (Iterator iterator = sourceList.iterator(); iterator.hasNext();) {
            BusinessObject sourceCollectionObject = (BusinessObject) iterator.next();
            LOG.debug("attempting to copy collection member with class '" + sourceCollectionObject.getClass() + "'");
            BusinessObject targetCollectionObject = (BusinessObject) ObjectUtils.createNewObjectFromClass(sourceCollectionObject.getClass());
            populateFromBaseWithSuper(sourceCollectionObject, targetCollectionObject, supplementalUncopyable, new HashSet<Class>());
            // BusinessObject targetCollectionObject = (BusinessObject)ObjectUtils.deepCopy((Serializable)sourceCollectionObject);
            Map pkMap = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(targetCollectionObject);
            Set<String> pkFields = pkMap.keySet();
            for (String field : pkFields) {
                ObjectUtils.setObjectProperty(targetCollectionObject, field, null);
            }
            listToSet.add(targetCollectionObject);
        }
        ObjectUtils.setObjectProperty(targetObject, fieldName, listToSet);
    }

    /**
     * Copies based on a class template it does not copy fields in Known Uncopyable Fields
     * 
     * @param base the base class
     * @param src source
     * @param target target
     */
    public static void populateFromBaseClass(Class base, BusinessObject src, BusinessObject target) {
        populateFromBaseClass(base, src, target, new HashMap());
    }

    /**
     * 
     * Populates from a base class traversing up the object hierarchy.
     * 
     * @param sourceObject object to copy from
     * @param targetObject object to copy to
     * @param supplementalUncopyableFieldNames fields to exclude
     * @param classesToExclude classes to exclude
     */
    public static void populateFromBaseWithSuper(BusinessObject sourceObject, BusinessObject targetObject, Map supplementalUncopyableFieldNames, Set<Class> classesToExclude) {
        List<Class> classesToCopy = new ArrayList<Class>();
        Class sourceObjectClass = sourceObject.getClass();
        classesToCopy.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            if (!classesToExclude.contains(sourceObjectClass)) {
                classesToCopy.add(sourceObjectClass);
            }
        }
        for (int i = (classesToCopy.size() - 1); i >= 0; i--) {
            Class temp = classesToCopy.get(i);
            populateFromBaseClass(temp, sourceObject, targetObject, supplementalUncopyableFieldNames);
        }
    }

    // ***** following changes are to work around an ObjectUtils bug and are copied from ObjectUtils.java
    /**
     * Compares a business object with a List of BOs to determine if an object with the same key as the BO exists in the list. If it
     * does, the item is returned.
     * 
     * @param controlList - The list of items to check
     * @param bo - The BO whose keys we are looking for in the controlList
     */
    public static BusinessObject retrieveObjectWithIdentitcalKey(Collection controlList, BusinessObject bo) {
        BusinessObject returnBo = null;

        for (Iterator i = controlList.iterator(); i.hasNext();) {
            BusinessObject listBo = (BusinessObject) i.next();
            if (equalByKeys(listBo, bo)) {
                returnBo = listBo;
            }
        }

        return returnBo;
    }

    /**
     * Compares two business objects for equality of type and key values.
     * 
     * @param bo1
     * @param bo2
     * @return boolean indicating whether the two objects are equal.
     */
    public static boolean equalByKeys(BusinessObject bo1, BusinessObject bo2) {
        boolean equal = true;

        if (bo1 == null && bo2 == null) {
            equal = true;
        }
        else if (bo1 == null || bo2 == null) {
            equal = false;
        }
        else if (!bo1.getClass().getName().equals(bo2.getClass().getName())) {
            equal = false;
        }
        else {
            Map bo1Keys = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(bo1);
            Map bo2Keys = KNSServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(bo2);
            for (Iterator iter = bo1Keys.keySet().iterator(); iter.hasNext();) {
                String keyName = (String) iter.next();
                if (bo1Keys.get(keyName) != null && bo2Keys.get(keyName) != null) {
                    if (!bo1Keys.get(keyName).toString().equals(bo2Keys.get(keyName).toString())) {
                        equal = false;
                    }
                }
                else {
                    // CHANGE FOR PurapOjbCollectionHelper change if one is null we are likely looking at a new object (sequence) which is definitely
                    // not equal
                    equal = false;
                }
            }
        }


        return equal;
    }
    // ***** END copied from ObjectUtils.java changes
}
