/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * Class which tracks the current state of parsing - particularly which object should currently be parsed into
 */
public class FlatFileParseTrackerImpl implements FlatFileParseTracker {
	static Logger LOG = Logger.getLogger(FlatFileParseTrackerImpl.class);
	protected FlatFileSpecification classIdentifier;
	protected Stack<Object> parseStack;
	protected List<Object> parsedParentObjects;
	protected Map<Class<?>, FlatFileChildMapEntry> childrenMap;
	protected int completedLineCount = 0;

	/**
	 * Initializes a new FlatFileParseTracker
	 * @param flatFileSpecification the FlatFileSpecificationBase instance which will determine which object should be instantiated for a given line
	 * @param specifications the specifications for all objects that will be parsed into, to build a parent/child map out of
	 */
	@Override
    public void initialize(FlatFileSpecification flatFileClassIdentifier) {
		this.classIdentifier = flatFileClassIdentifier;
		this.parseStack = new Stack<Object>();
		this.parsedParentObjects = new ArrayList<Object>();
		constructChildrenMap();
	}

	/**
	 * Builds a parent/child map out of the given specifications
	 * @param specifications the specifications for the parse
	 */
	protected void constructChildrenMap() {
		childrenMap = new HashMap<Class<?>, FlatFileChildMapEntry>();

		for (FlatFileObjectSpecification specification : classIdentifier.getObjectSpecifications()) {
			if (specification.getParentBusinessObjectClass() != null) {
				final FlatFileChildMapEntry entry = new FlatFileChildMapEntry(specification.getParentBusinessObjectClass(), specification.getParentTargetProperty());
				childrenMap.put(specification.getBusinessObjectClass(), entry);
			}
		}
	}

	/**
	 * Determines which class should be parsed into and returns an instance of that
	 * @param lineToParse the line which is going to be parsed
	 * @return the object to parse into
	 */
	@Override
    public Object getObjectToParseInto(String lineToParse) {
		final Class<?> lineClass = classIdentifier.determineClassForLine(lineToParse);

		if (lineClass == null) {
			// the prefix was insignificant; skip it
			return null;
		}

		try {
			Object parseIntoObject = lineClass.newInstance();
			parseStack.push(parseIntoObject);
			return parseIntoObject;
		} catch (InstantiationException ie) {
			throw new RuntimeException("Could not instantiate object of class "+lineClass.getName()+" in FlatFileParse", ie);
		} catch (IllegalAccessException iae) {
			throw new RuntimeException("Illegal access attempting to instantiate object of class "+lineClass.getName()+" in FlatFileParse", iae);
		}
	}

	/**
	 * Called when a line has completed parsing. Throws an exception if a proper parent
	 * is not found for the line being parsed
	 */
	@Override
    @SuppressWarnings("unchecked")
	public void completeLineParse() {
		completedLineCount += 1;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Completing parse of line: "+completedLineCount);
		}

		Object currentObject = parseStack.pop();
		final FlatFileChildMapEntry entry = getEntryForParsedIntoObject(currentObject);

		final Class<?> parentClass = (entry == null) ? null : entry.getParentBeanClass();
		final String propertyName = (entry == null) ? null : entry.getPropertyName();

		while (!parseStack.isEmpty()) {
			Object checkingObject = parseStack.pop();
			if (parentClass != null && parentClass.isAssignableFrom(checkingObject.getClass())) {
				try {
					if (Collection.class.isAssignableFrom(PropertyUtils.getPropertyType(
							checkingObject, propertyName))) {
						Collection childrenList = ((Collection) PropertyUtils.getProperty(
								checkingObject, propertyName));
						childrenList.add(currentObject);
					} else {
						PropertyUtils.setProperty(checkingObject, propertyName,currentObject);
					}
					parseStack.push(checkingObject);
					parseStack.push(currentObject);
					return;
				} catch (Exception e) {
					LOG.error(e.getMessage() + "occured when completing line parse; attempting to set object of type "+currentObject.getClass().getName()+" to the following parent: "+parentClass.getName()+"#"+propertyName, e);
					throw new RuntimeException(e.getMessage() + "occured when completing line parse; attempting to set object of type "+currentObject.getClass().getName()+" to the following parent: "+parentClass.getName()+"#"+propertyName,e);
				}
			}
		}
		if (parentClass == null) {
			parseStack.push(currentObject);
			parsedParentObjects.add(currentObject);
		} else {
			throw new IllegalStateException("A line of class "+currentObject.getClass().getName()+" cannot exist without a proper parent");
		}
	}

	/**
	 * Looks up the FlatFileChildMapEntry for the given object
	 * @param parsedIntoObject the object which has just completed being parsed into
	 * @return the FlatFileChildMapEntry which has the given object as a child object, or null if the object is a base object
	 */
    public FlatFileChildMapEntry getEntryForParsedIntoObject(Object parsedIntoObject) {
    	final FlatFileChildMapEntry entry = childrenMap.get(parsedIntoObject.getClass());
    	return entry;
    }

	/**
	 * @return the List of parsed parent objects
	 */
	@Override
    public List<Object> getParsedObjects() {
		return parsedParentObjects;
	}

	/**
	 * Inner class to make holding parent/child relationships easier
	 *
	 */
	private class FlatFileChildMapEntry {
		 protected Class<?> parentBeanClass;
		 protected String propertyName;

		 public FlatFileChildMapEntry() {
			 super();
		 }

		 public FlatFileChildMapEntry(Class<?> parentBeanClass, String propertyName) {
			 this();
			 this.parentBeanClass = parentBeanClass;
			 this.propertyName = propertyName;
		 }

		 public Class<?> getParentBeanClass() {
			 return parentBeanClass;
		 }

		 public String getPropertyName() {
			 return propertyName;
		 }
	}
}
