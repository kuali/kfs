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
package org.kuali.rice.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.exception.DuplicateEntryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
    The maintainableCollection element defines a set of data fields, nested
    collections, summaryFields and duplicateIdentificationsFields.

    JSTL: maintainableCollection is a Map which is accessed using a
    key of the name of the maintainableCollection.  Each entry
    contains the following keys and values:
        **Key**                **Value**
        collection             true
        name                   name of collection
        dataObjectClass    name of collection class
        
* name is the name of the collection
* dataObjectClass is the class name of the objects in the collection
* sourceClassName is the class name of the BO used in a lookup
* sourceAttributeName is the name of the attribute which returns the collection
* includeAddLine is true if the user is given the ability to add multiple lines.
* includeMultipleLookupLine whether to render a quickfinder icon for multiple value lookups on the collection.  Defaults to true
* summaryTitle is the label of the summary
* attributeToHighlightOnDuplicateKey is the name of an attribute to highlight
    if two records in the collection are the same based on the
    duplicateIdentificationFields element.

 *
 */
@Deprecated
public class MaintainableCollectionDefinition extends MaintainableItemDefinition implements CollectionDefinitionI {
    private static final long serialVersionUID = -5617868782623587053L;

	// logger
    //private static Log LOG = LogFactory.getLog(MaintainableCollectionDefinition.class);

	protected Class<? extends BusinessObject> businessObjectClass;

    protected Class<? extends BusinessObject> sourceClassName;
    protected String summaryTitle;
    protected String attributeToHighlightOnDuplicateKey;

    protected boolean includeAddLine = true;
    protected boolean includeMultipleLookupLine = true;
    private boolean alwaysAllowCollectionDeletion = false;

    protected Map<String,MaintainableFieldDefinition> maintainableFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    protected Map<String,MaintainableCollectionDefinition> maintainableCollectionMap = new HashMap<String, MaintainableCollectionDefinition>();
    protected Map<String,MaintainableFieldDefinition> summaryFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    protected Map<String,MaintainableFieldDefinition> duplicateIdentificationFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    protected List<MaintainableFieldDefinition> maintainableFields = new ArrayList<MaintainableFieldDefinition>();
    protected List<MaintainableCollectionDefinition> maintainableCollections = new ArrayList<MaintainableCollectionDefinition>();
    protected List<MaintainableFieldDefinition> summaryFields = new ArrayList<MaintainableFieldDefinition>();
    protected List<MaintainableFieldDefinition> duplicateIdentificationFields = new ArrayList<MaintainableFieldDefinition>();

    public MaintainableCollectionDefinition() {}



    /**
     * @return businessObjectClass
     */
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * The BusinessObject class used for each row of this collection.
     */
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return Collection of all lookupField MaintainableFieldDefinitions associated with this MaintainableCollectionDefinition, in
     *         the order in which they were added
     */
    public List<MaintainableFieldDefinition> getMaintainableFields() {
        return maintainableFields;
    }

    public List<? extends FieldDefinitionI> getFields() {
        return maintainableFields;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isCollectionPropertyOf(rootBusinessObjectClass, getName())) {
            throw new AttributeValidationException("unable to find collection named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (dissallowDuplicateKey()) {
            if (!DataDictionary.isPropertyOf(businessObjectClass, attributeToHighlightOnDuplicateKey)) {
                throw new AttributeValidationException("unable to find attribute named '" + attributeToHighlightOnDuplicateKey + "'in dataObjectClass '" + businessObjectClass.getName() + "' of collection '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
        
        for (MaintainableFieldDefinition maintainableField : maintainableFields ) {
            maintainableField.completeValidation(businessObjectClass, null);
        }

        for (MaintainableCollectionDefinition maintainableCollection : maintainableCollections ) {
            maintainableCollection.completeValidation(businessObjectClass, null);
        }

//        for (MaintainableFieldDefinition summaryField : summaryFields ) {
//            summaryField.completeValidation(dataObjectClass, null, validationCompletionUtils);
//        }
//        
//        for (MaintainableFieldDefinition identifierField : duplicateIdentificationFields) {
//            identifierField.completeValidation(dataObjectClass, null, validationCompletionUtils);
//        }
    }


    /**
     * @see Object#toString()
     */
    public String toString() {
        return "MaintainableCollectionDefinition for " + getName();
    }


    public Class<? extends BusinessObject> getSourceClassName() {
        return sourceClassName;
    }


    /** BusinessObject class which should be used for multiple value lookups for this collection.
     */
    public void setSourceClassName(Class<? extends BusinessObject> sourceClass) {
        this.sourceClassName = sourceClass;
    }

    public boolean getIncludeAddLine() {
        return includeAddLine;
    }


    /** Control whether an "add" line should be included at the top of this collection. */
    public void setIncludeAddLine(boolean includeAddLine) {
        this.includeAddLine = includeAddLine;
    }

    /**
     * @return Collection of all lookupField MaintainableCollectionDefinitions associated with this
     *         MaintainableCollectionDefinition, in the order in which they were added
     */
    public List<MaintainableCollectionDefinition> getMaintainableCollections() {
        return maintainableCollections;
    }

    public List<? extends CollectionDefinitionI> getCollections() {
        return maintainableCollections;
    }

    
    /**
     * @return Collection of all SummaryFieldDefinitions associated with this SummaryFieldDefinition, in the order in which they
     *         were added
     */
    public List<? extends FieldDefinitionI> getSummaryFields() {
        return summaryFields;
    }

    public boolean hasSummaryField(String key) {
        return summaryFieldMap.containsKey(key);
    }

    public boolean isIncludeMultipleLookupLine() {
        return includeMultipleLookupLine;
    }

    /** Set whether the multiple lookup line (and link) should appear above this collection. */
    public void setIncludeMultipleLookupLine(boolean includeMultipleLookupLine) {
        this.includeMultipleLookupLine = includeMultipleLookupLine;
    }

    public String getSummaryTitle() {
        return summaryTitle;
    }

    /**
summaryTitle is the label of the summary
     */
    public void setSummaryTitle(String overrideSummaryName) {
        this.summaryTitle = overrideSummaryName;
    }


    public String getAttributeToHighlightOnDuplicateKey() {
        return attributeToHighlightOnDuplicateKey;
    }

    /**
 attributeToHighlightOnDuplicateKey is the name of an attribute to highlight
                            if two records in the collection are the same based on the
                            duplicateIdentificationFields element.
    */
    public void setAttributeToHighlightOnDuplicateKey(String attributeToHighlightOnDuplicate) {
        this.attributeToHighlightOnDuplicateKey = attributeToHighlightOnDuplicate;
    }

    public boolean dissallowDuplicateKey() {
        return StringUtils.isNotBlank(getAttributeToHighlightOnDuplicateKey());
    }

    public List<MaintainableFieldDefinition> getDuplicateIdentificationFields() {
        return duplicateIdentificationFields;
    }

    /** The list of fields to include in this collection. */
    public void setMaintainableFields(List<MaintainableFieldDefinition> maintainableFields) {
        maintainableFieldMap.clear();
        for ( MaintainableFieldDefinition maintainableField : maintainableFields ) {
            if (maintainableField == null) {
                throw new IllegalArgumentException("invalid (null) maintainableField");
            }

            String fieldName = maintainableField.getName();
            if (maintainableFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            maintainableFieldMap.put(fieldName, maintainableField);
        }
        this.maintainableFields = maintainableFields;
    }

    /** The list of sub-collections to include in this collection. */
    public void setMaintainableCollections(List<MaintainableCollectionDefinition> maintainableCollections) {
        maintainableCollectionMap.clear();
        for (MaintainableCollectionDefinition maintainableCollection : maintainableCollections ) {
            if (maintainableCollection == null) {
                throw new IllegalArgumentException("invalid (null) maintainableCollection");
            }

            String fieldName = maintainableCollection.getName();
            if (maintainableCollectionMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            maintainableCollectionMap.put(fieldName, maintainableCollection);
        }
        this.maintainableCollections = maintainableCollections;
    }

    /**

                        The summaryFields element defines a set of summaryField
                        elements.
     */
    public void setSummaryFields(List<MaintainableFieldDefinition> summaryFields) {
        summaryFieldMap.clear();
        for (MaintainableFieldDefinition summaryField : summaryFields ) {
            if (summaryField == null) {
                throw new IllegalArgumentException("invalid (null) summaryField");
            }

            String fieldName = summaryField.getName();
            if (summaryFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            summaryFieldMap.put(fieldName, summaryField);
        }
        this.summaryFields = summaryFields;
    }

    /**
    The duplicateIdentificationFields element is used to define a set of
    fields that will be used to determine if two records in the collection
    are duplicates.
    */
    public void setDuplicateIdentificationFields(List<MaintainableFieldDefinition> duplicateIdentificationFields) {
        duplicateIdentificationFieldMap.clear();
        for (MaintainableFieldDefinition identifierField : duplicateIdentificationFields) {
            if (identifierField == null) {
                throw new IllegalArgumentException("invalid (null) identifierField");
            }

            String fieldName = identifierField.getName();
            if (duplicateIdentificationFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            duplicateIdentificationFieldMap.put(fieldName, identifierField);            
        }
        this.duplicateIdentificationFields = duplicateIdentificationFields;
    }



	public boolean isAlwaysAllowCollectionDeletion() {
		return this.alwaysAllowCollectionDeletion;
	}



	public void setAlwaysAllowCollectionDeletion(
			boolean alwaysAllowCollectionDeletion) {
		this.alwaysAllowCollectionDeletion = alwaysAllowCollectionDeletion;
	}
    
}
