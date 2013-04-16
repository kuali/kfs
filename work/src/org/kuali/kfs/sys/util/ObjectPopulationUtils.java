/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.util;

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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.ObjectUtils;


public class ObjectPopulationUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectPopulationUtils.class);

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
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("field " + field.getName() + " is transient, skipping ");
                }
            }
        }
        int counter = 0;
        for (String fieldName : fieldNames) {
            if ((isProcessableField(base, fieldName, PurapConstants.KNOWN_UNCOPYABLE_FIELDS)) && (isProcessableField(base, fieldName, supplementalUncopyable))) {
                attemptCopyOfFieldName(base.getName(), fieldName, src, target, supplementalUncopyable);
                counter++;
            }
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Population complete for " + counter + " fields out of a total of " + fieldNames.size() + " potential fields in object with base class '" + base + "'");
        }
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
    protected static void attemptCopyOfFieldName(String baseClassName, String fieldName, BusinessObject sourceObject, BusinessObject targetObject, Map supplementalUncopyable) {
        try {

            Object propertyValue = ObjectUtils.getPropertyValue(sourceObject, fieldName);
            if ((ObjectUtils.isNotNull(propertyValue)) && (Collection.class.isAssignableFrom(propertyValue.getClass()))) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("attempting to copy collection field '" + fieldName + "' using base class '" + baseClassName + "' and property value class '" + propertyValue.getClass() + "'");
                }
                copyCollection(fieldName, targetObject, (Collection)propertyValue, supplementalUncopyable);
            }
            else {
                String propertyValueClass = (ObjectUtils.isNotNull(propertyValue)) ? propertyValue.getClass().toString() : "(null)";
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("attempting to set field '" + fieldName + "' using base class '" + baseClassName + "' and property value class '" + propertyValueClass + "'");
                }
                ObjectUtils.setObjectProperty(targetObject, fieldName, propertyValue);
            }
        }
        catch (Exception e) {
            // purposefully skip for now
            // (I wish objectUtils getPropertyValue threw named errors instead of runtime) so I could
            // selectively skip
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("couldn't set field '" + fieldName + "' using base class '" + baseClassName + "' due to exception with class name '" + e.getClass().getName() + "'", e);
            }
        }
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
    protected static boolean isProcessableField(Class baseClass, String fieldName, Map excludedFieldNames) {
        if (excludedFieldNames.containsKey(fieldName)) {
            Class potentialClassName = (Class) excludedFieldNames.get(fieldName);
            if ((ObjectUtils.isNull(potentialClassName)) || (potentialClassName.equals(baseClass))) {
                return false;
            }
        }
        return true;
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
    protected static <T extends BusinessObject> void copyCollection(String fieldName, BusinessObject targetObject, Collection<T> sourceList, Map supplementalUncopyable) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Collection listToSet = null;

        // materialize collections
        if (ObjectUtils.isNotNull(sourceList)) {
            ObjectUtils.materializeObjects(sourceList);
        }

        // ArrayList requires argument so handle differently than below
        try {
            listToSet = sourceList.getClass().newInstance();
        }
        catch (Exception e) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("couldn't set class '" + sourceList.getClass() + "' on collection..." + fieldName + " using " + sourceList.getClass());
            }
            listToSet = new ArrayList<T>();
        }

        for (Iterator iterator = sourceList.iterator(); iterator.hasNext();) {
            BusinessObject sourceCollectionObject = (BusinessObject) iterator.next();
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("attempting to copy collection member with class '" + sourceCollectionObject.getClass() + "'");
            }
            BusinessObject targetCollectionObject = (BusinessObject) createNewObjectFromClass(sourceCollectionObject.getClass());
            populateFromBaseWithSuper(sourceCollectionObject, targetCollectionObject, supplementalUncopyable, new HashSet<Class>());
            // BusinessObject targetCollectionObject = (BusinessObject)ObjectUtils.deepCopy((Serializable)sourceCollectionObject);
            Map pkMap = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(targetCollectionObject);
            Set<String> pkFields = pkMap.keySet();
            for (String field : pkFields) {
                ObjectUtils.setObjectProperty(targetCollectionObject, field, null);
            }
            listToSet.add(targetCollectionObject);
        }
        ObjectUtils.setObjectProperty(targetObject, fieldName, listToSet);
    }

    /**
     * This method safely creates a object from a class
     * Convenience method to create new object and throw a runtime exception if it cannot
     * If the class is an {@link ExternalizableBusinessObject}, this method will determine the interface for the EBO and query the
     * appropriate module service to create a new instance.
     *
     * @param boClass
     *
     * @return a newInstance() of clazz
     */
    protected static Object createNewObjectFromClass(Class clazz) {
        if (clazz == null) {
            throw new RuntimeException("BO class was passed in as null");
        }
        try {
            if (clazz.getSuperclass().equals(ExternalizableBusinessObject.class)) {
                Class eboInterface = ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(clazz);
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(eboInterface);
                return moduleService.createNewObjectFromExternalizableClass(eboInterface);
            }
            else {
                return clazz.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occured while trying to create a new instance for class " + clazz);
        }
    }
}
