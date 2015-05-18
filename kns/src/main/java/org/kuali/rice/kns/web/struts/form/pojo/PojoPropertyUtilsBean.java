/**
 * Copyright 2005-2014 The Kuali Foundation
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
// begin Kuali Foundation modification
package org.kuali.rice.kns.web.struts.form.pojo;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.collections.FastHashMap;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * begin Kuali Foundation modification
 * This class is used to access the properties of a Pojo bean.
 * deleted author tag
 * end Kuali Foundation modification
 */
// Kuali Foundation modification: class originally SLPropertyUtilsBean
public class PojoPropertyUtilsBean extends PropertyUtilsBean {

    public static final Logger LOG = Logger.getLogger(PojoPropertyUtilsBean.class.getName());

    /**
     * Thin interface for determining the appropriate item class for a collection property
     */
    public static interface CollectionItemClassProvider {
        public Class getCollectionItemClass(Object bean, String property);
    }

    /**
     * CollectionItemClassProvider backed by OJB metadata
     */
    public static class PersistenceStructureServiceProvider implements CollectionItemClassProvider {
        protected static PersistenceStructureService persistenceStructureService = null;
        protected static PersistenceStructureService getPersistenceStructureService() {
            if (persistenceStructureService == null) {
                persistenceStructureService = KRADServiceLocator.getPersistenceStructureService();
            }
            return persistenceStructureService;
        }

        @Override
        public Class getCollectionItemClass(Object bean, String property) {
            Map<String, Class> collectionObjectTypes = getPersistenceStructureService().listCollectionObjectTypes(bean.getClass());
            return collectionObjectTypes.get(property);
        }
    }

    // default is to consult OJB
    protected static CollectionItemClassProvider collectionItemClassProvider = new PersistenceStructureServiceProvider();

	// begin Kuali Foundation modification
    public PojoPropertyUtilsBean() {
        super();
    }
    // end Kuali Foundation modification

    public Object getProperty(Object bean, String key) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // begin Kuali Foundation modification
        if (!(bean instanceof PojoForm))
            return super.getProperty(bean, key);

        PojoForm form = (PojoForm) bean;
        Map unconvertedValues = form.getUnconvertedValues();

        if (unconvertedValues.containsKey(key))
            return unconvertedValues.get(key);

        Object val = getNestedProperty(bean, key);
        Class type = (val!=null)?val.getClass():null;
        if ( type == null ) {
            try {
                type = getPropertyType(bean, key);
            } catch ( Exception ex ) {
                type = String.class;
                LOG.warn( "Unable to get property type for Class: " + bean.getClass().getName() + "/Property: " + key );
            }
        }
        return (Formatter.isSupportedType(type) ? form.formatValue(val, key, type) : val);
        // end Kuali Foundation modification
    }

    	// begin Kuali Foundation modification
    private Map<String,List<Method>> cache = new HashMap<String, List<Method>>();
    private static Map<String,Method> readMethodCache = new HashMap<String, Method>();
    private IntrospectionException introspectionException = new IntrospectionException( "" );

    public Object fastGetNestedProperty(Object obj, String propertyName) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        //logger.debug("entering fastGetNestedProperty");

        List<Method> methods = (List<Method>) cache.get(propertyName + obj.getClass().getName());
        if (methods == null) {
            methods = new ArrayList<Method>();
            Object currentObj = obj;
            Class<?> currentObjClass = currentObj.getClass();

            for (String currentPropertyName : propertyName.split("\\.") ) {
                String cacheKey = currentObjClass.getName() + currentPropertyName;
                Method readMethod = readMethodCache.get( cacheKey );
                if ( readMethod == null ) {
                	synchronized (readMethodCache) {
	                    // if the read method was resolved to an error, repeat the exception
	                    // rather than performing the reflection calls below
	                    if ( readMethodCache.containsKey(cacheKey) ) {
	                        throw introspectionException;
	                    }
	                    try {
	                        try {
	                            readMethod = currentObjClass.getMethod("get" + currentPropertyName.substring(0, 1).toUpperCase() + currentPropertyName.substring(1), (Class[])null);
	                        } catch (NoSuchMethodException e) {
	                            readMethod = currentObjClass.getMethod("is" + currentPropertyName.substring(0, 1).toUpperCase() + currentPropertyName.substring(1), (Class[])null);
	                        }
	                    } catch ( NoSuchMethodException ex ) {
	                        // cache failures to prevent re-checking of the parameter
	                        readMethodCache.put( cacheKey, null );
	                        throw introspectionException;
	                    }
	                    readMethodCache.put(cacheKey, readMethod );
					}
                }
                methods.add(readMethod);
                currentObjClass = readMethod.getReturnType();
            }
            synchronized (cache) {
                cache.put(propertyName + obj.getClass().getName(), methods);
			}
        }

        for ( Method method : methods ) {
            obj = method.invoke(obj, (Object[])null);
        }

        //logger.debug("exiting fastGetNestedProperty");

        return obj;
    }
	// end Kuali Foundation modification

    /*
     *  Kuali modification to make isWriteable work like it did in beanUtils 1.7.
     *  Checking for nested nulls caused exceptions in rice 2.0.
     */
    @Override
    public boolean isWriteable(Object bean, String name) {
        // Validate method parameters
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Remove any subscript from the final name value
        name = getResolver().getProperty(name);

        // Treat WrapDynaBean as special case - may be a read-only property
        // (see Jira issue# BEANUTILS-61)
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }

        // Return the requested result
        if (bean instanceof DynaBean) {
            // All DynaBean properties are writeable
            return (((DynaBean) bean).getDynaClass().getDynaProperty(name) != null);
        } else {
            try {
                PropertyDescriptor desc =
                        getPropertyDescriptor(bean, name);
                if (desc != null) {
                    Method writeMethod = desc.getWriteMethod();
                    if (writeMethod == null) {
                        if (desc instanceof IndexedPropertyDescriptor) {
                            writeMethod = ((IndexedPropertyDescriptor) desc).getIndexedWriteMethod();
                        } else if (desc instanceof MappedPropertyDescriptor) {
                            writeMethod = ((MappedPropertyDescriptor) desc).getMappedWriteMethod();
                        }
                        writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                    }
                    return (writeMethod != null);
                } else {
                    return (false);
                }
            } catch (IllegalAccessException e) {
                return (false);
            } catch (InvocationTargetException e) {
                return (false);
            } catch (NoSuchMethodException e) {
                return (false);
            }
        }

    }

    /**
     * begin Kuali Foundation modification
     * removed comments and @<no space>since javadoc attribute
     * end Kuali Foundation modification
     * @see PropertyUtilsBean#getNestedProperty(Object, String)
     */
    public Object getNestedProperty(Object arg0, String arg1) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// begin Kuali Foundation modification
        try {
            try {
                return fastGetNestedProperty(arg0, arg1);
            }
            catch (Exception e) {
                return super.getNestedProperty(arg0, arg1);
            }
        }
        catch (NestedNullException e) {
            return getUnreachableNestedProperty(arg0, arg1);
        }
        catch (InvocationTargetException e1) {
            return getUnreachableNestedProperty(arg0, arg1);
        }
        // removed commented code
        // end Kuali Foundation modification
    }

    /**
     * Customization of superclass getNestedProperty which transparently creates indexed property items
     * {@inheritDoc}
     */
    public Object getIndexedProperty(Object bean, String name, int index) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        try {
            return super.getIndexedProperty(bean, name, index);
        } catch (IndexOutOfBoundsException ioobe) {
            return generateIndexedProperty(bean, name, index, ioobe);
        }
    }

    protected Object generateIndexedProperty(Object nestedBean, String property, int index, IndexOutOfBoundsException ioobe)  throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (!(nestedBean instanceof PersistableBusinessObject)) throw ioobe;

        // we can only grow lists
        if (!List.class.isAssignableFrom(getPropertyType(nestedBean, property))) throw ioobe;

        List list= (List) getProperty(nestedBean, property);

        Class c = collectionItemClassProvider.getCollectionItemClass(nestedBean, property);

        if (c == null) {
            throw new RuntimeException("Unable to determined item class for collection '" + property + "' on bean of type '" + nestedBean.getClass() + "'");
        }

        Object value;
        try {
            value = c.newInstance();
        } catch (InstantiationException ie) {
            throw new RuntimeException("Error instantiating item class: " + c);
        }

        // fill any missing indices
        while (list.size() <= index) {
            list.add(null);
        }
        list.set(index, value);

        return super.getIndexedProperty(nestedBean, property, index);
    }

    // begin Kuali Foundation modification
    /**
     * helper method makes sure we don't return "" for collections
     */
    private Object getUnreachableNestedProperty(Object arg0, String arg1) {
        try {
            PropertyDescriptor propertyDescriptor  = getPropertyDescriptor(arg0, arg1);
            if (propertyDescriptor == null || Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                return null;
            }
        } catch (IllegalAccessException e) {
            // ignore
        } catch (InvocationTargetException e) {
            // ignore
        } catch (NoSuchMethodException e) {
            // ignore
        }

        return "";
    }
    // end Kuali Foundation modification


    // begin Kuali Foundation modification 
    /**
     * begin Kuali Foundation modification
     * Set the value of the (possibly nested) property of the specified name, for the specified bean, with no type conversions.
     *
     * @param bean Bean whose property is to be modified
     * @param name Possibly nested name of the property to be modified
     * @param value Value to which the property is to be set
     *
     * @exception IllegalAccessException if the caller does not have access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a property returns null
     * @exception InvocationTargetException if the property accessor method throws an exception
     * @exception NoSuchMethodException if an accessor method for this propety cannot be found
     * end Kuali Foundation modification
     */
    public void setNestedProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (bean == null) {
        	if (LOG.isDebugEnabled()) LOG.debug("No bean specified, name = " + name + ", value = " + value);
        	return;
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        Object propBean = null;
        int indexOfINDEXED_DELIM = -1;
        int indexOfMAPPED_DELIM = -1;
        while (true) {
            int delim = name.indexOf(PropertyUtils.NESTED_DELIM);
            if (delim < 0) {
                break;
            }
            String next = name.substring(0, delim);
            indexOfINDEXED_DELIM = next.indexOf(PropertyUtils.INDEXED_DELIM);
            indexOfMAPPED_DELIM = next.indexOf(PropertyUtils.MAPPED_DELIM);
            if (bean instanceof Map) {
                propBean = ((Map) bean).get(next);
            }
            else if (indexOfMAPPED_DELIM >= 0) {
                propBean = getMappedProperty(bean, next);
            }
            else if (indexOfINDEXED_DELIM >= 0) {
                propBean = getIndexedProperty(bean, next);
            }
            else {
                propBean = getSimpleProperty(bean, next);
            }
            if (ObjectUtils.isNull(propBean)) {
                Class propertyType = getPropertyType(bean, next);
                if (propertyType != null) {
                	Object newInstance = ObjectUtils.createNewObjectFromClass(propertyType);
                    setSimpleProperty(bean, next, newInstance);
                    propBean = getSimpleProperty(bean, next);
                }
            }
            bean = propBean;
            name = name.substring(delim + 1);
        }

        indexOfINDEXED_DELIM = name.indexOf(PropertyUtils.INDEXED_DELIM);
        indexOfMAPPED_DELIM = name.indexOf(PropertyUtils.MAPPED_DELIM);

        if (bean instanceof Map) {
            // check to see if the class has a standard property
            PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
            if (descriptor == null) {
                // no - then put the value into the map
                ((Map) bean).put(name, value);
            }
            else {
                // yes - use that instead
                setSimpleProperty(bean, name, value);
            }
        }
        else if (indexOfMAPPED_DELIM >= 0) {
            setMappedProperty(bean, name, value);
        }
        else if (indexOfINDEXED_DELIM >= 0) {
            setIndexedProperty(bean, name, value);
        }
        else {
            setSimpleProperty(bean, name, value);
        }
    }
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
    /**
     * <p>
     * Retrieve the property descriptor for the specified property of the specified bean, or return <code>null</code> if there is
     * no such descriptor. This method resolves indexed and nested property references in the same manner as other methods in this
     * class, except that if the last (or only) name element is indexed, the descriptor for the last resolved property itself is
     * returned.
     * </p>
     *
     * <p>
     * <strong>FIXME </strong>- Does not work with DynaBeans.
     * </p>
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for which a property descriptor is requested
     *
     * @exception IllegalAccessException if the caller does not have access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a property returns null
     * @exception InvocationTargetException if the property accessor method throws an exception
     * @exception NoSuchMethodException if an accessor method for this propety cannot be found
     */
    public PropertyDescriptor getPropertyDescriptor(Object bean, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {
        	if (LOG.isDebugEnabled()) LOG.debug("No bean specified, name = " + name);
        	return null;
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }
        try {
            // Resolve nested references
            Object propBean = null;
            while (true) {
                int delim = findNextNestedIndex(name);
                //int delim = name.indexOf(PropertyUtils.NESTED_DELIM);
                if (delim < 0) {
                    break;
                }
                String next = name.substring(0, delim);
                int indexOfINDEXED_DELIM = next.indexOf(PropertyUtils.INDEXED_DELIM);
                int indexOfMAPPED_DELIM = next.indexOf(PropertyUtils.MAPPED_DELIM);
                if (indexOfMAPPED_DELIM >= 0 && (indexOfINDEXED_DELIM < 0 || indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
                    propBean = getMappedProperty(bean, next);
                }
                else {
                    if (indexOfINDEXED_DELIM >= 0) {
                        propBean = getIndexedProperty(bean, next);
                    }
                    else {
                        propBean = getSimpleProperty(bean, next);
                    }
                }
                if (ObjectUtils.isNull(propBean)) {
                    Class propertyType = getPropertyType(bean, next);
                    if (propertyType != null) {
                    	Object newInstance = ObjectUtils.createNewObjectFromClass(propertyType);
                        setSimpleProperty(bean, next, newInstance);
                        propBean = getSimpleProperty(bean, next);
                    }
                }
                bean = propBean;
                name = name.substring(delim + 1);
            }
    
            // Remove any subscript from the final name value
            int left = name.indexOf(PropertyUtils.INDEXED_DELIM);
            if (left >= 0) {
                name = name.substring(0, left);
            }
            left = name.indexOf(PropertyUtils.MAPPED_DELIM);
            if (left >= 0) {
                name = name.substring(0, left);
            }
    
            // Look up and return this property from our cache
            // creating and adding it to the cache if not found.
            if ((bean == null) || (name == null)) {
                return (null);
            }
    
            PropertyDescriptor descriptors[] = getPropertyDescriptors(bean);
            if (descriptors != null) {
    
                for (int i = 0; i < descriptors.length; i++) {
                    if (name.equals(descriptors[i].getName()))
                        return (descriptors[i]);
                }
            }
    
            PropertyDescriptor result = null;
            FastHashMap mappedDescriptors = getMappedPropertyDescriptors(bean);
            if (mappedDescriptors == null) {
                mappedDescriptors = new FastHashMap();
                mappedDescriptors.setFast(true);
            }
            result = (PropertyDescriptor) mappedDescriptors.get(name);
            if (result == null) {
                // not found, try to create it
                try {
                    result = new MappedPropertyDescriptor(name, bean.getClass());
                }
                catch (IntrospectionException ie) {
                }
                if (result != null) {
                    mappedDescriptors.put(name, result);
                }
            }
    
            return result;
        } catch ( RuntimeException ex ) {
            LOG.error( "Unable to get property descriptor for " + bean.getClass().getName() + " . " + name
                    + "\n" + ex.getClass().getName() + ": " + ex.getMessage() );
            throw ex;
        }
    }
    // end Kuali Foundation modification

    private int findNextNestedIndex(String expression)
    {
        // walk back from the end to the start
        // and find the first index that
        int bracketCount = 0;
        for (int i=0, size=expression.length(); i<size ; i++) {
            char at = expression.charAt(i);
            switch (at) {
                case PropertyUtils.NESTED_DELIM:
                    if (bracketCount < 1) {
                        return i;
                    }
                    break;

                case PropertyUtils.MAPPED_DELIM:
                case PropertyUtils.INDEXED_DELIM:
                    // not bothered which
                    ++bracketCount;
                    break;

                case PropertyUtils.MAPPED_DELIM2:
                case PropertyUtils.INDEXED_DELIM2:
                    // not bothered which
                    --bracketCount;
                    break;
            }
        }
        // can't find any
        return -1;
    }

    /**
     * Set the value of the specified simple property of the specified bean,
     * with no type conversions.
     *
     * @param bean Bean whose property is to be modified
     * @param name Name of the property to be modified
     * @param value Value to which the property should be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if the property name is
     *  nested or indexed
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setSimpleProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
        	if (LOG.isDebugEnabled()) LOG.debug("No bean specified, name = " + name + ", value = " + value);
        	return;
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Validate the syntax of the property name
        if (name.indexOf(PropertyUtils.NESTED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        } else if (name.indexOf(PropertyUtils.INDEXED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");
        } else if (name.indexOf(PropertyUtils.MAPPED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Mapped property names are not allowed");
        }

        // Retrieve the property setter method for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }
        Method writeMethod = getWriteMethod(descriptor);
        if (writeMethod == null) {
            //throw new NoSuchMethodException("Property '" + name + "' has no setter method");
        	LOG.warn("Bean: " + bean.getClass().getName() + ", Property '" + name + "' has no setter method");
        	return;
        }

        // Call the property setter method
        Object values[] = new Object[1];
        values[0] = value;
        if (LOG.isDebugEnabled()) {
            String valueClassName =
                value == null ? "<null>" : value.getClass().getName();
            LOG.debug("setSimpleProperty: Invoking method " + writeMethod
                      + " with value " + value + " (class " + valueClassName + ")");
        }
        
        
        invokeMethod(writeMethod, bean, values);

    }
    
    /** This just catches and wraps IllegalArgumentException. */
    private Object invokeMethod(
                        Method method, 
                        Object bean, 
                        Object[] values) 
                            throws
                                IllegalAccessException,
                                InvocationTargetException {
        try {
            
            return method.invoke(bean, values);
        
        } catch (IllegalArgumentException e) {
            
            LOG.error("Method invocation failed.", e);
            throw new IllegalArgumentException(
                "Cannot invoke " + method.getDeclaringClass().getName() + "." 
                + method.getName() + " - " + e.getMessage());
            
        }
    }
    
    public Class getPropertyType(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (getResolver().hasNested(name)) {
            String next = getResolver().next(name);
            Object nestedBean = getProperty(bean, next);
            if (nestedBean == null) {
            	Class<?>[] paramTypes = {};
            	Method method = null;
            	try {
                    method = bean.getClass().getMethod("get" + next.substring(0, 1).toUpperCase() + next.substring(1), (Class[])null);
                } catch (NoSuchMethodException e) {
                    method = bean.getClass().getMethod("is" + next.substring(0, 1).toUpperCase() + next.substring(1), (Class[])null);
                }
            	try {
                    nestedBean = ObjectUtils.createNewObjectFromClass(method.getReturnType());
				} catch (RuntimeException e) {
					NestedNullException nne = new NestedNullException
                    ("Null property value for '" + next +
                    "' on bean class '" + bean.getClass() + "'");
                    nne.initCause(e);
                    throw nne;
				}
            }
            bean = nestedBean;
            name = getResolver().remove(name);
        }

        // Remove any subscript from the final name value
        name = getResolver().getProperty(name);

        // Special handling for DynaBeans
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                return (null);
            }
            Class type = descriptor.getType();
            if (type == null) {
                return (null);
            } else if (type.isArray()) {
                return (type.getComponentType());
            } else {
                return (type);
            }
        }

        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            return (null);
        } else if (descriptor instanceof IndexedPropertyDescriptor) {
            return (((IndexedPropertyDescriptor) descriptor).
                    getIndexedPropertyType());
        } else if (descriptor instanceof MappedPropertyDescriptor) {
            return (((MappedPropertyDescriptor) descriptor).
                    getMappedPropertyType());
        } else {
            return (descriptor.getPropertyType());
        }

    }
}
