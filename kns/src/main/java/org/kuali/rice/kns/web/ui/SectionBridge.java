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
package org.kuali.rice.kns.web.ui;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kns.datadictionary.CollectionDefinitionI;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.datadictionary.FieldDefinitionI;
import org.kuali.rice.kns.datadictionary.InquiryCollectionDefinition;
import org.kuali.rice.kns.datadictionary.InquirySectionDefinition;
import org.kuali.rice.kns.datadictionary.InquirySubSectionHeaderDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.rice.kns.datadictionary.SubSectionHeaderDefinitionI;
import org.kuali.rice.kns.document.authorization.FieldRestriction;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.exception.ClassNotPersistableException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class SectionBridge {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SectionBridge.class);
    private static BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
    	if (businessObjectAuthorizationService == null) {
    		businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
    	}
    	return businessObjectAuthorizationService;
    }
    private static DataDictionaryService dataDictionaryService;
    private static DataDictionaryService getDataDictionaryService() {
    	if (dataDictionaryService == null) {
    		dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
    	}
    	return dataDictionaryService;
    }
    private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    /**
     * This method creates a Section for display on an Inquiry Screen.
     * 
     * @param sd The DD definition from which to construct the Section.
     * @param o The BusinessObject from which to populate the Section values.
     * @return A populated Section.
     */
    public static final Section toSection(Inquirable inquirable, InquirySectionDefinition sd, BusinessObject o, InquiryRestrictions auths) {
        Section section = new Section();
        section.setSectionId( sd.getId() );
        section.setSectionTitle(sd.getTitle());
        section.setRows(new ArrayList());
        section.setDefaultOpen(sd.isDefaultOpen());
        
        if (sd.getNumberOfColumns() != null) {
            section.setNumberOfColumns(sd.getNumberOfColumns());
        }
        else {
            section.setNumberOfColumns(KRADConstants.DEFAULT_NUM_OF_COLUMNS);
        }

        List<Field> sectionFields = new ArrayList();
        for (FieldDefinition fieldDefinition : sd.getInquiryFields()) {
            List row = new ArrayList();

            Field f = null;
            if (fieldDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition inquiryCollectionDefinition = (InquiryCollectionDefinition) fieldDefinition;

                List<Row> sectionRows = new ArrayList();
                sectionRows = getContainerRows(section, inquiryCollectionDefinition, o, null, null, new ArrayList(), new HashSet<String>(), new StringBuffer(section.getErrorKey()), inquiryCollectionDefinition.getNumberOfColumns(), inquirable);
                section.setRows(sectionRows);
            }
            else if (fieldDefinition instanceof InquirySubSectionHeaderDefinition) {
                f = createMaintainableSubSectionHeader((InquirySubSectionHeaderDefinition) fieldDefinition);
            }
            else {
                f = FieldBridge.toField(fieldDefinition, o, section);
            }

            if (null != f) {
                sectionFields.add(f);
            }

        }

        if (!sectionFields.isEmpty()) {
            section.setRows(FieldUtils.wrapFields(sectionFields, section.getNumberOfColumns()));
        }

        applyInquirySectionAuthorizations(section, auths);
        
        section.setRows(reArrangeRows(section.getRows(), section.getNumberOfColumns()));
        
        return section;
    }
    
   
    private static final void applyInquirySectionAuthorizations(Section section, InquiryRestrictions inquiryRestrictions) {
    	applyInquiryRowsAuthorizations(section.getRows(), inquiryRestrictions);
    }
    
    private static final void applyInquiryRowsAuthorizations(List<Row> rows, InquiryRestrictions inquiryRestrictions) {
    	for (Row row : rows) {
    		List<Field> rowFields = row.getFields();
    		for (Field field : rowFields) {
    			applyInquiryFieldAuthorizations(field, inquiryRestrictions);
    		}
    	}
    }
    
    protected static final void applyInquiryFieldAuthorizations(Field field, InquiryRestrictions inquiryRestrictions) {
    	if (Field.CONTAINER.equals(field.getFieldType())) {
    		applyInquiryRowsAuthorizations(field.getContainerRows(), inquiryRestrictions);
    		field.setContainerRows(reArrangeRows(field.getContainerRows(), field.getNumberOfColumnsForCollection()));
    	}
    	else if (!Field.IMAGE_SUBMIT.equals(field.getFieldType())) {
    		FieldRestriction fieldRestriction = inquiryRestrictions.getFieldRestriction(field.getPropertyName());
    		if (fieldRestriction.isHidden()) {
    			field.setFieldType(Field.HIDDEN);
    			field.setPropertyValue(null);
    		}
    		// KULRICE-8271: partially masked field can't be masked properly 
    		else if (fieldRestriction.isMasked() || fieldRestriction.isPartiallyMasked()) {
            	field.setSecure(true);
            	MaskFormatter maskFormatter = fieldRestriction.getMaskFormatter();
            	String displayMaskValue = maskFormatter.maskValue(field.getPropertyValue());
            	field.setDisplayMaskValue(displayMaskValue);
            	// since it's an inquiry, let's wipe out the encrypted field value since we don't need to post it back
            	field.setEncryptedValue("");
    		}
       	}
    }
    
    //This method is used to remove hidden fields (To fix JIRA KFSMI-2449)
    private static final List<Row> reArrangeRows(List<Row> rows, int numberOfColumns){
    	List<Row> rearrangedRows = new ArrayList<Row>();
    	
    	for (Row row : rows) {
    		List<Field> fields = new ArrayList<Field>();
    		List<Field> rowFields = row.getFields();
    		for (Field field : rowFields) {
    			if(!Field.HIDDEN.equals(field.getFieldType()) && !Field.BLANK_SPACE.equals(field.getFieldType())){
    				fields.add(field);
    			}
    		}
    		List<Row> rewrappedFieldRows = FieldUtils.wrapFields(fields, numberOfColumns);
    		if (row.isHidden()) {
    			for (Row rewrappedRow : rewrappedFieldRows) {
    				rewrappedRow.setHidden(true);
    			}
    		}
    		rearrangedRows.addAll(rewrappedFieldRows);
    	}
    	
    	return rearrangedRows;
    }

    
    /**
     * This method creates a Section for a MaintenanceDocument.
     * 
     * @param sd The DD definition of the Section.
     * @param o The BusinessObject from which the Section will be populated.
     * @param maintainable
     * @param maintenanceAction The action (new, newwithexisting, copy, edit, etc) requested from the UI.
     * @param autoFillDefaultValues Should default values be auto-filled?
     * @param autoFillBlankRequiredValues Should required values left blank on the UI be auto-filled?
     * @param displayedFieldNames What fields are displayed on the UI?
     * @return A populated Section.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static final Section toSection(MaintainableSectionDefinition sd, BusinessObject o, Maintainable maintainable, Maintainable oldMaintainable, String maintenanceAction,  List<String> displayedFieldNames, Set<String> conditionallyRequiredMaintenanceFields) throws InstantiationException, IllegalAccessException {
    	Section section = new Section();

        section.setSectionId( sd.getId() );
        section.setSectionTitle(sd.getTitle());
        section.setSectionClass(o.getClass());
        section.setHidden( sd.isHidden() );
        section.setDefaultOpen(sd.isDefaultOpen());
        section.setHelpUrl(sd.getHelpUrl());

        // iterate through section maint items and contruct Field UI objects
        Collection<MaintainableItemDefinition> maintItems = sd.getMaintainableItems();
        List<Row> sectionRows = new ArrayList<Row>();
        List<Field> sectionFields = new ArrayList<Field>();

        for (MaintainableItemDefinition maintItem : maintItems) {
            Field field = FieldBridge.toField(maintItem, sd, o, maintainable, section, displayedFieldNames, conditionallyRequiredMaintenanceFields);
            boolean skipAdd = false;

            // if CollectionDefiniton, then have a many section
            if (maintItem instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition definition = (MaintainableCollectionDefinition) maintItem;
                section.getContainedCollectionNames().add(maintItem.getName());

                StringBuffer containerRowErrorKey = new StringBuffer();
                sectionRows = getContainerRows(section, definition, o, maintainable, oldMaintainable, displayedFieldNames, conditionallyRequiredMaintenanceFields, containerRowErrorKey, KRADConstants.DEFAULT_NUM_OF_COLUMNS, null);
            } else if (maintItem instanceof MaintainableSubSectionHeaderDefinition) {
                MaintainableSubSectionHeaderDefinition definition = (MaintainableSubSectionHeaderDefinition) maintItem;
                field = createMaintainableSubSectionHeader(definition);
            }

            if (!skipAdd) {
                sectionFields.add(field);
            }
        }

        // populate field values from business object
        //if (o != null && !autoFillDefaultValues) {
        if (o != null) {
            sectionFields = FieldUtils.populateFieldsFromBusinessObject(sectionFields, o);

            /* if maintenance action is copy, clear out secure fields */
            if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                for (Iterator iterator = sectionFields.iterator(); iterator.hasNext();) {
                    Field element = (Field) iterator.next();
                    if (element.isSecure()) {
                        element.setPropertyValue("");
                    }
                }
            }
        }

        sectionRows.addAll(FieldUtils.wrapFields(sectionFields));
        section.setRows(sectionRows);

        return section;

    }
    
    
    /**
     * @see #getContainerRows(Section, CollectionDefinitionI, BusinessObject, Maintainable, List<String>, StringBuffer, String,
     *      boolean, int)
     */
    public static final List<Row> getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, Maintainable oldMaintainable, List<String> displayedFieldNames, Set<String> conditionallyRequiredMaintenanceFields, StringBuffer containerRowErrorKey, int numberOfColumns, Inquirable inquirable) {
        return getContainerRows(s, collectionDefinition, o, m, oldMaintainable, displayedFieldNames, conditionallyRequiredMaintenanceFields, containerRowErrorKey, "", false, numberOfColumns, inquirable);
    }

    /**
     * Builds a list of Rows with Fields of type containers for a many section.
     * 
     * @param s The Section containing the Collection/Container.
     * @param collectionDefinition The DD definition of the Collection.
     * @param o The BusinessObject from which the Container/Collection will be populated.
     * @param m The Maintainable for the BO (needed by some methods called on FieldBridge, FieldUtils etc.)
     * @param displayedFieldNames
     * @param containerRowErrorKey The error key for the Container/Collection.
     * @param parents
     * @param hideAdd Should the add line be added to the Container/Collection?
     * @param numberOfColumns In how many columns in the UI will the fields in the Container/Collection be shown?
     * @return
     */
     public static final List<Row> getContainerRows(Section s, CollectionDefinitionI collectionDefinition, BusinessObject o, Maintainable m, Maintainable oldMaintainable, List<String> displayedFieldNames, Set<String> conditionallyRequiredMaintenanceFields, StringBuffer containerRowErrorKey, String parents, boolean hideAdd, int numberOfColumns, Inquirable inquirable) {
        List<Row> containerRows = new ArrayList<Row>();
        List<Field> collFields = new ArrayList<Field>();
        
        String collectionName = collectionDefinition.getName();
        
        // add the toggle inactive record display button for the collection
        if (m != null && Inactivatable.class.isAssignableFrom(collectionDefinition.getBusinessObjectClass()) && StringUtils.isBlank(parents)) {
            addShowInactiveButtonField(s, collectionName, !m.getShowInactiveRecords(collectionName));
        }
        if (inquirable != null && Inactivatable.class.isAssignableFrom(collectionDefinition.getBusinessObjectClass()) && StringUtils.isBlank(parents)) {
            addShowInactiveButtonField(s, collectionName, !inquirable.getShowInactiveRecords(collectionName));
        }
        
        // first need to populate the containerRows with the "new" form if available
        if (!hideAdd) {
            List<Field> newFormFields = new ArrayList<Field>();
            if (collectionDefinition.getIncludeAddLine()) {


                newFormFields = FieldBridge.getNewFormFields(collectionDefinition, o, m, displayedFieldNames, conditionallyRequiredMaintenanceFields, containerRowErrorKey, parents, hideAdd, numberOfColumns);


            } else if(collectionDefinition instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition mcd = (MaintainableCollectionDefinition)collectionDefinition;
                if(FieldUtils.isCollectionMultipleLookupEnabled(mcd)) {
                    //do just the top line for collection if add is not allowed
                  newFormFields = FieldBridge.constructContainerField(collectionDefinition, parents, o, hideAdd, numberOfColumns, mcd.getName(), new ArrayList<Field>());
                }
            }
            if (null != newFormFields) {
                containerRows.add(new Row(newFormFields));
            }
        }

        Collection<? extends CollectionDefinitionI> collections = collectionDefinition.getCollections();
        for (CollectionDefinitionI collection : collections) {
            int subCollectionNumberOfColumn = numberOfColumns;
            if (collectionDefinition instanceof InquiryCollectionDefinition) {
                InquiryCollectionDefinition icd = (InquiryCollectionDefinition) collection;
                if (icd.getNumberOfColumns() != null) {
                    subCollectionNumberOfColumn = icd.getNumberOfColumns();
                }
            }
            // no colNum for add rows
             containerRows.addAll(getContainerRows(s, collection, o, m, oldMaintainable, displayedFieldNames, conditionallyRequiredMaintenanceFields, containerRowErrorKey, parents + collectionDefinition.getName() + ".", true, subCollectionNumberOfColumn, inquirable));
        }

        // then we need to loop through the existing collection and add those fields
        Collection<? extends FieldDefinitionI> collectionFields = collectionDefinition.getFields();
        // get label for collection
        String collectionLabel = getDataDictionaryService().getCollectionLabel(o.getClass(), collectionDefinition.getName());
        
        // retrieve the summary label either from the override or from the DD
        String collectionElementLabel = collectionDefinition.getSummaryTitle();
        if (StringUtils.isEmpty(collectionElementLabel)) {
            collectionElementLabel = getDataDictionaryService().getCollectionElementLabel(o.getClass().getName(), collectionDefinition.getName(), collectionDefinition.getBusinessObjectClass());
        }

        boolean translateCodes = getMaintenanceDocumentDictionaryService().translateCodes(o.getClass());

        if (o != null) {
            if (PropertyUtils.isWriteable(o, collectionDefinition.getName()) && ObjectUtils.getPropertyValue(o, collectionDefinition.getName()) != null) {
                Object obj = ObjectUtils.getPropertyValue(o, collectionName);
                
                Object oldObj = null;
                if (oldMaintainable != null && oldMaintainable.getBusinessObject() != null) {
                    oldObj = ObjectUtils.getPropertyValue(oldMaintainable.getBusinessObject(), collectionName);
                }

                if (obj instanceof List) {
                    Map summaryFields = new HashMap();
                    boolean hidableRowsPresent = false;
                    for (int i = 0; i < ((List) obj).size(); i++) {
                        BusinessObject lineBusinessObject = (BusinessObject) ((List) obj).get(i);
                        
                        if (lineBusinessObject instanceof PersistableBusinessObject) {
                        	((PersistableBusinessObject) lineBusinessObject).refreshNonUpdateableReferences();
                        }
                        
                        /*
                         * Handle display of inactive records. The old maintainable is used to compare the old side (if it exists). If the row should not be displayed, it is set as
                         * hidden and will be handled in the maintenance rowDisplay.tag.   
                         */  
                        boolean setRowHidden = false;
                        BusinessObject oldLineBusinessObject = null;
                        if (oldObj != null && ((List) oldObj).size() > i) {
                            oldLineBusinessObject = (BusinessObject) ((List) oldObj).get(i);
                        }
                        
                        if (lineBusinessObject instanceof Inactivatable && !((Inactivatable) lineBusinessObject).isActive()) {
                            if (m != null) {
                                // rendering a maint doc
                                if (!hidableRowsPresent) {
                                    hidableRowsPresent = isRowHideableForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject);
                                    }
                                setRowHidden = isRowHiddenForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject, m, collectionName);
                                    }
                            if (inquirable != null) {
                                // rendering an inquiry screen
                                if (!hidableRowsPresent) {
                                    hidableRowsPresent = isRowHideableForInquiry(lineBusinessObject);
                                }
                                setRowHidden = isRowHiddenForInquiry(lineBusinessObject, inquirable, collectionName);
                            }
                        }

                        collFields = new ArrayList<Field>();
                        List<String> duplicateIdentificationFieldNames = new ArrayList<String>(); 
                        //We only need to do this if the collection definition is a maintainable collection definition, 
                        //don't need it for inquiry collection definition.
                        if (collectionDefinition instanceof MaintainableCollectionDefinition) {
                            Collection<MaintainableFieldDefinition> duplicateFieldDefs = ((MaintainableCollectionDefinition)collectionDefinition).getDuplicateIdentificationFields();
                            for (MaintainableFieldDefinition eachFieldDef : duplicateFieldDefs) {
                                duplicateIdentificationFieldNames.add(eachFieldDef.getName());
                            }
                        }
                        
                        for (FieldDefinitionI collectionField : collectionFields) {

                            // construct Field UI object from definition
                            Field collField = FieldUtils.getPropertyField(collectionDefinition.getBusinessObjectClass(), collectionField.getName(), false);
                            
            				if (translateCodes) {
            					FieldUtils.setAdditionalDisplayPropertyForCodes(lineBusinessObject.getClass(), collField.getPropertyName(), collField);
            				}
                            
                            FieldBridge.setupField(collField, collectionField, conditionallyRequiredMaintenanceFields);
                            setPrimaryKeyFieldsReadOnly(collectionDefinition.getBusinessObjectClass(), collField);

                            //If the duplicateIdentificationFields were specified in the maint. doc. DD, we'll need
                            //to set the fields to be read only as well, in addition to the primary key fields.
                            if (duplicateIdentificationFieldNames.size() > 0) {
                                setDuplicateIdentificationFieldsReadOnly(collField, duplicateIdentificationFieldNames);
                            }
                            
                            FieldUtils.setInquiryURL(collField, lineBusinessObject, collectionField.getName());
                            // save the simple property name
                            String name = collField.getPropertyName();

                            // prefix name for multi line (indexed)
                            collField.setPropertyName(collectionDefinition.getName() + "[" + (new Integer(i)).toString() + "]." + collField.getPropertyName());

                            // commenting out codes for sub-collections show/hide for now
                            // subCollField.setContainerName(collectionDefinition.getName() + "["+i+"]" +"." +
                            // subCollectionDefinition.getName() + "[" + j + "]");

                            if (collectionField instanceof MaintainableFieldDefinition) {
                                MaintenanceUtils.setFieldQuickfinder(lineBusinessObject, collectionDefinition.getName(), false, i, name, collField, displayedFieldNames, m, (MaintainableFieldDefinition) collectionField);
                                MaintenanceUtils.setFieldDirectInquiry(lineBusinessObject, name, (MaintainableFieldDefinition) collectionField, collField, displayedFieldNames);
                            } else {
                                LookupUtils
                                        .setFieldQuickfinder(lineBusinessObject, collectionDefinition.getName(), false,
                                                i, name, collField, displayedFieldNames, m);
                                LookupUtils.setFieldDirectInquiry(lineBusinessObject, name, collField);
                            }

                            String propertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineBusinessObject, collectionField.getName());
                            // For files the FormFile is not being persisted instead the file data is stored in
                            // individual fields as defined by PersistableAttachment.  However, newly added rows contain all data
                            // in the FormFile, so check if it's empty.
                            if (StringUtils.isBlank(propertyValue) && Field.FILE.equals(collField.getFieldType())) {
                                Object fileName = ObjectUtils.getNestedValue(lineBusinessObject, KRADConstants.BO_ATTACHMENT_FILE_NAME);
                                collField.setPropertyValue(fileName);
                            } else {
                                collField.setPropertyValue(propertyValue);

                            }

                            if (Field.FILE.equals(collField.getFieldType())) {
                                Object fileType = ObjectUtils.getNestedValue(lineBusinessObject, KRADConstants.BO_ATTACHMENT_FILE_CONTENT_TYPE);
                                if (fileType == null
                                        && collField.getPropertyName().contains(".")) {
                                    // fileType not found on bo, so check in the attachment field on bo
                                    String tempName = collField.getPropertyName().substring(collField.getPropertyName().lastIndexOf('.')+1);
                                    fileType =  ObjectUtils.getNestedValue(lineBusinessObject, (tempName + "." + KRADConstants.BO_ATTACHMENT_FILE_CONTENT_TYPE));
                                }
                                collField.setImageSrc(WebUtils.getAttachmentImageForUrl((String) fileType));
                            }
                            
							if (StringUtils.isNotBlank(collField.getAlternateDisplayPropertyName())) {
								String alternateDisplayPropertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineBusinessObject,
										collField.getAlternateDisplayPropertyName());
								collField.setAlternateDisplayPropertyValue(alternateDisplayPropertyValue);
							}
							
							if (StringUtils.isNotBlank(collField.getAdditionalDisplayPropertyName())) {
								String additionalDisplayPropertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineBusinessObject,
										collField.getAdditionalDisplayPropertyName());
								collField.setAdditionalDisplayPropertyValue(additionalDisplayPropertyValue);
							}
                                
							//update user fields with universal id and/or name
							updateUserFields(collField, lineBusinessObject);
                                
                            // the the field as read only (if appropriate)
                            if (collectionField.isReadOnlyAfterAdd()) {
                                collField.setReadOnly(true);
                            }

                            // check if this is a summary field
                            if (collectionDefinition.hasSummaryField(collectionField.getName())) {
                                summaryFields.put(collectionField.getName(), collField);
                            }

                            collFields.add(collField);
                        }

                        Field containerField;
                        containerField = FieldUtils.constructContainerField(
                                KRADConstants.EDIT_PREFIX + "[" + (new Integer(i)).toString() + "]", collectionLabel + " " + (i + 1), collFields, numberOfColumns);
                        // why is this only on collections and not subcollections any significance or just oversight?
                        containerField.setContainerName(collectionDefinition.getName() + "[" + (new Integer(i)).toString() + "].");

                        /* If the collection line is pending (meaning added by this document) the isNewCollectionRecord will be set to true. In this
                           case we give an option to delete the line. The parameters for the delete action method are embedded into the button name. */
                        if (lineBusinessObject instanceof PersistableBusinessObject && 
                        		(((PersistableBusinessObject) lineBusinessObject).isNewCollectionRecord() 
                        				|| collectionDefinition.isAlwaysAllowCollectionDeletion())) {
                            containerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName(), (new Integer(i)).toString())));
                        }

                        if (StringUtils.isNotEmpty(collectionElementLabel)) {
                            //We don't want to associate any indexes to the containerElementName anymore so that
                            //when the element is deleted, the currentTabIndex won't be associated with the
                            //wrong tab for the remaining tab.
                            //containerField.setContainerElementName(collectionElementLabel + " " + (i + 1));
                            containerField.setContainerElementName(collectionElementLabel);
                            // reorder summaryFields to make sure they are in the order specified in the summary section
                            List orderedSummaryFields = getSummaryFields(summaryFields, collectionDefinition);
                            containerField.setContainerDisplayFields(orderedSummaryFields);
                        }
                        
                        Row containerRow = new Row(containerField);
                        if (setRowHidden) {
                            containerRow.setHidden(true);
                        }
                        containerRows.add(containerRow);
                        
                        

                        Collection<? extends CollectionDefinitionI> subCollections = collectionDefinition.getCollections();
                        List<Field> subCollFields = new ArrayList<Field>();

                        summaryFields = new HashMap();
                        // iterate over the subCollections directly on this collection
                        for (CollectionDefinitionI subCollection : subCollections) {
                            Collection<? extends FieldDefinitionI> subCollectionFields = subCollection.getFields();
                            int subCollectionNumberOfColumns = numberOfColumns;

                            if (!s.getContainedCollectionNames().contains(collectionDefinition.getName() + "." + subCollection.getName())) {
                                s.getContainedCollectionNames().add(collectionDefinition.getName() + "." + subCollection.getName());
                            }

                            if (subCollection instanceof InquiryCollectionDefinition) {
                                InquiryCollectionDefinition icd = (InquiryCollectionDefinition) subCollection;
                                if (icd.getNumberOfColumns() != null) {
                                    subCollectionNumberOfColumns = icd.getNumberOfColumns();
                                }
                            }
                            // get label for collection
                            String subCollectionLabel = getDataDictionaryService().getCollectionLabel(o.getClass(), subCollection.getName());

                            // retrieve the summary label either from the override or from the DD
                            String subCollectionElementLabel = subCollection.getSummaryTitle();
                            if (StringUtils.isEmpty(subCollectionElementLabel)) {
                                subCollectionElementLabel = getDataDictionaryService().getCollectionElementLabel(o.getClass().getName(), subCollection.getName(), subCollection.getBusinessObjectClass());
                            }
                            // make sure it's really a collection (i.e. list)

                            String subCollectionName = subCollection.getName();
                            Object subObj = ObjectUtils.getPropertyValue(lineBusinessObject, subCollectionName);
                            
                            Object oldSubObj = null;
                            if (oldLineBusinessObject != null) {
                                oldSubObj = ObjectUtils.getPropertyValue(oldLineBusinessObject, subCollectionName);
                            }
                            
                            if (subObj instanceof List) {
                                /* recursively call this method to get the add row and exisiting members of the subCollections subcollections containerRows.addAll(getContainerRows(subCollectionDefinition,
                                   displayedFieldNames,containerRowErrorKey, parents+collectionDefinition.getName()+"["+i+"]"+".","[0]",false, subCollectionNumberOfColumn)); */
                                containerField.getContainerRows().addAll(getContainerRows(s, subCollection, o, m, oldMaintainable, displayedFieldNames, conditionallyRequiredMaintenanceFields, containerRowErrorKey, parents + collectionDefinition.getName() + "[" + i + "]" + ".", false, subCollectionNumberOfColumns, inquirable));
                             
                                // iterate over the fields
                                for (int j = 0; j < ((List) subObj).size(); j++) {
                                    BusinessObject lineSubBusinessObject = (BusinessObject) ((List) subObj).get(j);
                                    
                                    if (lineSubBusinessObject instanceof PersistableBusinessObject) {
                                    	((PersistableBusinessObject) lineSubBusinessObject).refreshNonUpdateableReferences();
                                    }
                                    
                                    // determine if sub collection line is inactive and should be hidden
                                    boolean setSubRowHidden = false;
                                    if (lineSubBusinessObject instanceof Inactivatable && !((Inactivatable) lineSubBusinessObject).isActive()) {
                                        if (oldSubObj != null) { 
                                            // get corresponding elements in both the new list and the old list
                                            BusinessObject oldLineSubBusinessObject = (BusinessObject) ((List) oldSubObj).get(j);
                                            if (m != null) {
                                                    if (!hidableRowsPresent) {
                                                        hidableRowsPresent = isRowHideableForMaintenanceDocument(lineSubBusinessObject, oldLineSubBusinessObject);
                                                }
                                                    setSubRowHidden = isRowHiddenForMaintenanceDocument(lineSubBusinessObject, oldLineSubBusinessObject, m, collectionName);
                                                }
                                            }
                                        if (inquirable != null) {
                                            if (!hidableRowsPresent) {
                                                hidableRowsPresent = isRowHideableForInquiry(lineSubBusinessObject);
                                        }
                                            setSubRowHidden = isRowHiddenForInquiry(lineSubBusinessObject, inquirable, collectionName);
                                    }
                                    }

                                    
                                    subCollFields = new ArrayList<Field>();
                                    // construct field objects based on fields
                                    for (FieldDefinitionI subCollectionField : subCollectionFields) {

                                        // construct Field UI object from definition
                                        Field subCollField = FieldUtils.getPropertyField(subCollection.getBusinessObjectClass(), subCollectionField.getName(), false);

                                        String subCollectionFullName = collectionDefinition.getName() + "[" + i + "]" + "." + subCollection.getName();
                                        
                        				if (translateCodes) {
                        					FieldUtils.setAdditionalDisplayPropertyForCodes(lineSubBusinessObject.getClass(), subCollField.getPropertyName(), subCollField);
                        				}

                                        FieldBridge.setupField(subCollField, subCollectionField, conditionallyRequiredMaintenanceFields);
                                        setPrimaryKeyFieldsReadOnly(subCollection.getBusinessObjectClass(), subCollField);
                                       
                                        // save the simple property name
                                        String name = subCollField.getPropertyName();

                                        // prefix name for multi line (indexed)
                                        subCollField.setPropertyName(subCollectionFullName + "[" + j + "]." + subCollField.getPropertyName());

                                        // commenting out codes for sub-collections show/hide for now
                                        if (subCollectionField instanceof MaintainableFieldDefinition) {
                                            MaintenanceUtils.setFieldQuickfinder(lineSubBusinessObject, subCollectionFullName, false, j, name, subCollField, displayedFieldNames, m, (MaintainableFieldDefinition) subCollectionField);
                                            MaintenanceUtils
                                                    .setFieldDirectInquiry(lineSubBusinessObject, subCollectionFullName,
                                                            false, j, name, subCollField, displayedFieldNames, m,
                                                            (MaintainableFieldDefinition) subCollectionField);
                                        } else {
                                            LookupUtils.setFieldQuickfinder(lineSubBusinessObject, subCollectionFullName, false, j, name, subCollField, displayedFieldNames);
                                            LookupUtils.setFieldDirectInquiry(lineBusinessObject, name, subCollField);
                                        }

                                        String propertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineSubBusinessObject, subCollectionField.getName());
                                        subCollField.setPropertyValue(propertyValue);
                                        
            							if (StringUtils.isNotBlank(subCollField.getAlternateDisplayPropertyName())) {
            								String alternateDisplayPropertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineSubBusinessObject,
            										subCollField.getAlternateDisplayPropertyName());
            								subCollField.setAlternateDisplayPropertyValue(alternateDisplayPropertyValue);
            							}
                                        
            							if (StringUtils.isNotBlank(subCollField.getAdditionalDisplayPropertyName())) {
            								String additionalDisplayPropertyValue = ObjectUtils.getFormattedPropertyValueUsingDataDictionary(lineSubBusinessObject,
            										subCollField.getAdditionalDisplayPropertyName());
            								subCollField.setAdditionalDisplayPropertyValue(additionalDisplayPropertyValue);
            							}
                                     
                                        // check if this is a summary field
                                        if (subCollection.hasSummaryField(subCollectionField.getName())) {
                                            summaryFields.put(subCollectionField.getName(), subCollField);
                                        }

                                        if (subCollectionField.isReadOnlyAfterAdd()) {
                                            subCollField.setReadOnly(true);
                                        }

                                        subCollFields.add(subCollField);
                                    }

                                    Field subContainerField = FieldUtils.constructContainerField(
                                            KRADConstants.EDIT_PREFIX + "[" + (new Integer(j)).toString() + "]", subCollectionLabel, subCollFields);
                                    if (lineSubBusinessObject instanceof PersistableBusinessObject && (((PersistableBusinessObject) lineSubBusinessObject).isNewCollectionRecord() || subCollection.isAlwaysAllowCollectionDeletion())) {
                                        subContainerField.getContainerRows().add(new Row(getDeleteRowButtonField(parents + collectionDefinition.getName() + "[" + i + "]" + "." + subCollectionName, (new Integer(j)).toString())));
                                    }

                                    // summary line code
                                    if (StringUtils.isNotEmpty(subCollectionElementLabel)) {
                                        //We don't want to associate any indexes to the containerElementName anymore so that
                                        //when the element is deleted, the currentTabIndex won't be associated with the
                                        //wrong tab for the remaining tab.
                                        //subContainerField.setContainerElementName(subCollectionElementLabel + " " + (j + 1));
                                        subContainerField.setContainerElementName(collectionElementLabel + "-" + subCollectionElementLabel);
                                    }
                                    subContainerField.setContainerName(collectionDefinition.getName() + "." + subCollectionName);
                                    if (!summaryFields.isEmpty()) {
                                        // reorder summaryFields to make sure they are in the order specified in the summary section
                                        List orderedSummaryFields = getSummaryFields(summaryFields, subCollection);
                                        subContainerField.setContainerDisplayFields(orderedSummaryFields);
                                    }
                                    
                                    Row subContainerRow = new Row(subContainerField);
                                    if (setRowHidden || setSubRowHidden) {
                                        subContainerRow.setHidden(true);
                                    }
                                    containerField.getContainerRows().add(subContainerRow);
                                }
                            }
                        }
                    }
                    if ( !hidableRowsPresent ) {
                        s.setExtraButtonSource( "" );
                    }
                }
            }
        }
        
        return containerRows;
    }

    /**
      * Updates fields of type kualiuser sets the universal user id and/or name if required. 
      * 
      * @param field
      * @param businessObject
      */
     private static final void updateUserFields(Field field, BusinessObject businessObject){
         // for user fields, attempt to pull the principal ID and person's name from the source object
         if ( field.getFieldType().equals(Field.KUALIUSER) ) {
             // this is supplemental, so catch and log any errors
             try {
                 if ( StringUtils.isNotBlank(field.getUniversalIdAttributeName()) ) {
                     Object principalId = ObjectUtils.getNestedValue(businessObject, field.getUniversalIdAttributeName());
                     if ( principalId != null ) {
                         field.setUniversalIdValue(principalId.toString());
                     }
                 }
                 if ( StringUtils.isNotBlank(field.getPersonNameAttributeName()) ) {
                     Object personName = ObjectUtils.getNestedValue(businessObject, field.getPersonNameAttributeName());
                     if ( personName != null ) {
                         field.setPersonNameValue( personName.toString() );
                     }
                 }
             } catch ( Exception ex ) {
                 LOG.warn( "Unable to get principal ID or person name property in SectionBridge.", ex );
             }
         }
     }
     
    /**
     * Helper method to build up a Field containing a delete button mapped up to remove the collection record identified by the
     * given collection name and index.
     * 
     * @param collectionName - name of the collection
     * @param rowIndex - index of the record to associate delete button
     * @return Field - of type IMAGE_SUBMIT
     */
    private static final Field getDeleteRowButtonField(String collectionName, String rowIndex) {
        Field deleteButtonField = new Field();

        String deleteButtonName = KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.DELETE_LINE_METHOD + "." + collectionName + "." + KRADConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL + ".line" + rowIndex;
        deleteButtonField.setPropertyName(deleteButtonName);
        deleteButtonField.setFieldType(Field.IMAGE_SUBMIT);
        deleteButtonField.setPropertyValue("images/tinybutton-delete1.gif");

        return deleteButtonField;
    }
    
    /**
     * Helper method to build up the show inactive button source and place in the section.
     * 
     * @param section - section that will display the button
     * @param collectionName - name of the collection to toggle setting
     * @param showInactive - boolean indicating whether inactive rows should be displayed
     * @return Field - of type IMAGE_SUBMIT
     */
    private static final void addShowInactiveButtonField(Section section, String collectionName, boolean showInactive) {
    	String methodName = KRADConstants.DISPATCH_REQUEST_PARAMETER + "." + KRADConstants.TOGGLE_INACTIVE_METHOD + "." + collectionName.replace( '.', '_' );
        methodName += "." + KRADConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL + showInactive + ".anchorshowInactive." + collectionName + KRADConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL;
           
        String imageSource = showInactive ? "tinybutton-showinact.gif" : "tinybutton-hideinact.gif";

        String showInactiveButton = "property=" + methodName + ";src=" + imageSource + ";alt=show(hide) inactive" + ";title=show(hide) inactive";

        section.setExtraButtonSource(showInactiveButton);
    }
    
    /**
     * Retrieves the primary key property names for the given class. If the field's property is one of those keys, makes the field
     * read-only. This is called for collection lines. Since deletion is not allowed for existing lines, the pk fields must be
     * read-only, otherwise a user could change the pk value which would be equivalent to deleting the line and adding a new line.
     */
    private static final void setPrimaryKeyFieldsReadOnly(Class businessObjectClass, Field field) {
    	try{
    		//TODO: Revisit this. Changing since getPrimaryKeys and listPrimaryKeyFieldNames are apparently same.
    		//May be we might want to replace listPrimaryKeyFieldNames with getPrimaryKeys... Not sure.
	    	List primaryKeyPropertyNames = 
	    		KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(businessObjectClass);
	        if (primaryKeyPropertyNames.contains(field.getPropertyName())) {
	            field.setReadOnly(true);
	        }
    	} catch(ClassNotPersistableException ex){
    		//Not all classes will be persistable in a collection. For e.g. externalizable business objects.
    		LOG.info("Not persistable dataObjectClass: "+businessObjectClass+", field: "+field);
    	}
    }
    
    private static void setDuplicateIdentificationFieldsReadOnly(Field field, List<String>duplicateIdentificationFieldNames) {
        if (duplicateIdentificationFieldNames.contains(field.getPropertyName())) {
            field.setReadOnly(true);
        }
    }

    /**
     * This method returns an ordered list of fields.
     * 
     * @param collSummaryFields
     * @param collectionDefinition
     * @return
     */
    private static final List<Field> getSummaryFields(Map collSummaryFields, CollectionDefinitionI collectionDefinition) {
        List<Field> orderedSummaryFields = new ArrayList<Field>();
        for (FieldDefinitionI summaryField : collectionDefinition.getSummaryFields()) {
            String name = summaryField.getName();
            boolean found = false;
            Field addField = (Field) collSummaryFields.get(name);
            if (!(addField == null)) {
                orderedSummaryFields.add(addField);
                found = true;
            }

            if (!found) {
                // should we throw a real error here?
                LOG.error("summaryField " + summaryField + " not present in the list");
            }

        }
        return orderedSummaryFields;
    }

    /**
     * This is a helper method to create a sub section header
     * 
     * @param definition the MaintainableSubSectionHeaderDefinition that we'll use to create the sub section header
     * @return the Field, which is the sub section header
     */
    private static final Field createMaintainableSubSectionHeader(SubSectionHeaderDefinitionI definition) {
        Field separatorField = new Field();
        separatorField.setFieldLabel(definition.getName());
        separatorField.setFieldType(Field.SUB_SECTION_SEPARATOR);
        separatorField.setReadOnly(true);

        return separatorField;
    }
    
    /**
     * Determines whether a business object is hidable on a maintenance document.  Hidable means that if the user chose to hide the inactive
     * elements in the collection in which the passed in BOs reside, then the BOs would be hidden
     * 
     * @param lineBusinessObject the BO in the new maintainable, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @param oldLineBusinessObject the corresponding BO in the old maintainable, should be of type {@link PersistableBusinessObject} and 
     * {@link Inquirable}
     * @return whether the BOs are eligible to be hidden if the user decides to hide them
     */
    protected static boolean isRowHideableForMaintenanceDocument(BusinessObject lineBusinessObject, BusinessObject oldLineBusinessObject) {
        if (oldLineBusinessObject != null) {
            if (((PersistableBusinessObject) lineBusinessObject).isNewCollectionRecord()) {
                // new records are never hidden, regardless of active status
                return false;
}
            if (!((Inactivatable) lineBusinessObject).isActive() && !((Inactivatable) oldLineBusinessObject).isActive()) {
                // records with an old and new collection elements of NOT active are eligible to be hidden
                return true;
            }
        }
        return false;
    }
    /**
     * Determines whether a business object is hidden on a maintenance document.
     * 
     * @param lineBusinessObject the BO in the new maintainable, should be of type {@link PersistableBusinessObject}
     * @param oldLineBusinessObject the corresponding BO in the old maintainable
     * @param newMaintainable the new maintainable from the maintenace document
     * @param collectionName the name of the collection from which these BOs come
     * @return
     */
    protected static boolean isRowHiddenForMaintenanceDocument(BusinessObject lineBusinessObject, BusinessObject oldLineBusinessObject,
            Maintainable newMaintainable, String collectionName) {
        return isRowHideableForMaintenanceDocument(lineBusinessObject, oldLineBusinessObject) && !newMaintainable.getShowInactiveRecords(collectionName);
    }
    
    /**
     * Determines whether a business object is hidable on an inquiry screen.  Hidable means that if the user chose to hide the inactive
     * elements in the collection in which the passed in BO resides, then the BO would be hidden
     * 
     * @param lineBusinessObject the collection element BO, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @return whether the BO is eligible to be hidden if the user decides to hide them
     */
    protected static boolean isRowHideableForInquiry(BusinessObject lineBusinessObject) {
        return !((Inactivatable) lineBusinessObject).isActive();
    }
    
    /**
     * Determines whether a business object is hidden on an inquiry screen.
     * 
     * @param lineBusinessObject the BO in the collection, should be of type {@link PersistableBusinessObject} and {@link Inquirable}
     * @param inquirable the inquirable
     * @param collectionName the name of the collection from which the BO comes
     * @return true if the business object is to be hidden; false otherwise
     */
    protected static boolean isRowHiddenForInquiry(BusinessObject lineBusinessObject, Inquirable inquirable, String collectionName) {
        return isRowHideableForInquiry(lineBusinessObject) && !inquirable.getShowInactiveRecords(collectionName);
    }
    
	public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
    	if (maintenanceDocumentDictionaryService == null) {
    		maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
    	}
		return maintenanceDocumentDictionaryService; 
	}
}

