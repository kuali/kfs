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
package org.kuali.rice.kns.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.SelectiveReferenceRefresher;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiExceptionIncidentService;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public final class MaintenanceUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceUtils.class);

    private static MaintenanceDocumentService maintenanceDocumentService;
    private static WorkflowDocumentService workflowDocumentService;
    private static ConfigurationService kualiConfigurationService;
    private static KualiExceptionIncidentService kualiExceptionIncidentService;
    private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private static DataDictionaryService dataDictionaryService;

    private MaintenanceUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Returns the field templates defined in the maint dictionary xml files. Field templates are used in multiple value lookups.
     * When doing a MV lookup on a collection, the returned BOs are not necessarily of the same type as the elements of the
     * collection. Therefore, a means of mapping between the fields for the 2 BOs are necessary. The template attribute of
     * &lt;maintainableField&gt;s contained within &lt;maintainableCollection&gt;s tells us this mapping. Example: a
     * &lt;maintainableField name="collectionAttrib" template="lookupBOAttrib"&gt; definition means that when a list of BOs are
     * returned, the lookupBOAttrib value of the looked up BO will be placed into the collectionAttrib value of the BO added to the
     * collection
     *
     * @param sections       the sections of a document
     * @param collectionName the name of a collection. May be a nested collection with indices (e.g. collA[1].collB)
     * @return
     */
    public static Map<String, String> generateMultipleValueLookupBOTemplate(List<MaintainableSectionDefinition> sections, String collectionName) {
        MaintainableCollectionDefinition definition = findMaintainableCollectionDefinition(sections, collectionName);
        if (definition == null) {
            return null;
        }
        Map<String, String> template = null;

        for (MaintainableFieldDefinition maintainableField : definition.getMaintainableFields()) {
            String templateString = maintainableField.getTemplate();
            if (StringUtils.isNotBlank(templateString)) {
                if (template == null) {
                    template = new HashMap<String, String>();
                }
                template.put(maintainableField.getName(), templateString);
            }
        }
        return template;
    }

    /**
     * Finds the MaintainableCollectionDefinition corresponding to the given collection name. For example, if the collection name is
     * "A.B.C", it will attempt to find the MaintainableCollectionDefinition for C that is nested in B that is nested under A. This
     * may not work correctly if there are duplicate collection definitions within the sections
     *
     * @param sections       the sections of a maint doc
     * @param collectionName the name of a collection, relative to the root of the BO being maintained. This value may have index
     *                       values (e.g. [1]), but these are ignored.
     * @return
     */
    public static MaintainableCollectionDefinition findMaintainableCollectionDefinition(List<MaintainableSectionDefinition> sections, String collectionName) {
        String[] collectionNameParts = StringUtils.split(collectionName, ".");
        for (MaintainableSectionDefinition section : sections) {
            MaintainableCollectionDefinition collDefinition = findMaintainableCollectionDefinitionHelper(section.getMaintainableItems(), collectionNameParts, 0);
            if (collDefinition != null) {
                return collDefinition;
            }
        }
        return null;
    }

    private static <E extends MaintainableItemDefinition> MaintainableCollectionDefinition findMaintainableCollectionDefinitionHelper(Collection<E> items, String[] collectionNameParts, int collectionNameIndex) {
        if (collectionNameParts.length <= collectionNameIndex) {
            // we've gone too far down the nesting without finding it
            return null;
        }

        // we only care about the coll name, and not the index, since the coll definitions do not include the indexing characters,
        // i.e. [ and ]
        String collectionToFind = StringUtils.substringBefore(collectionNameParts[collectionNameIndex], "[");
        for (MaintainableItemDefinition item : items) {
            if (item instanceof MaintainableCollectionDefinition) {
                MaintainableCollectionDefinition collection = (MaintainableCollectionDefinition) item;
                if (collection.getName().equals(collectionToFind)) {
                    // we found an appropriate coll, now we have to see if we need to recurse even more (more nested collections),
                    // or just return the one we found.
                    if (collectionNameIndex == collectionNameParts.length - 1) {
                        // we're at the last part of the name, so we return
                        return collection;
                    } else {
                        // go deeper
                        return findMaintainableCollectionDefinitionHelper(collection.getMaintainableCollections(), collectionNameParts, collectionNameIndex + 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks to see if there has been an override lookup declared for the maintenance field. If so, the override will be used for
     * the quickfinder and lookup utils will not be called. If no override was given, LookupUtils.setFieldQuickfinder will be called
     * to set the system generated quickfinder based on the attributes relationship to the parent business object.
     *
     * @return Field with quickfinder set if one was found
     */
    public static final Field setFieldQuickfinder(BusinessObject businessObject, String attributeName, MaintainableFieldDefinition maintainableFieldDefinition, Field field, List<String> displayedFieldNames, SelectiveReferenceRefresher srr) {
        if (maintainableFieldDefinition.getOverrideLookupClass() != null && StringUtils.isNotBlank(maintainableFieldDefinition.getOverrideFieldConversions())) {
            field.setQuickFinderClassNameImpl(maintainableFieldDefinition.getOverrideLookupClass().getName());
            field.setFieldConversions(maintainableFieldDefinition.getOverrideFieldConversions());
            field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
            field.setReferencesToRefresh(LookupUtils.convertReferencesToSelectCollectionToString(
                    srr.getAffectedReferencesFromLookup(businessObject, attributeName, "")));
            return field;
        }
        if (maintainableFieldDefinition.isNoLookup()) {
            return field;
        }
        return LookupUtils.setFieldQuickfinder(businessObject, null, false, 0, attributeName, field, displayedFieldNames, maintainableFieldDefinition.isNoLookup());
    }

    public static final Field setFieldQuickfinder(BusinessObject businessObject, String collectionName, boolean addLine, int index,
                                                  String attributeName, Field field, List<String> displayedFieldNames, Maintainable maintainable, MaintainableFieldDefinition maintainableFieldDefinition) {
        if (maintainableFieldDefinition.getOverrideLookupClass() != null && StringUtils.isNotBlank(maintainableFieldDefinition.getOverrideFieldConversions())) {
            if (maintainable != null) {
                String collectionPrefix = "";
                if (collectionName != null) {
                    if (addLine) {
                        collectionPrefix = KRADConstants.MAINTENANCE_ADD_PREFIX + collectionName + ".";
                    } else {
                        collectionPrefix = collectionName + "[" + index + "].";
                    }
                }
                field.setQuickFinderClassNameImpl(maintainableFieldDefinition.getOverrideLookupClass().getName());

                String prefixedFieldConversions = prefixFieldConversionsDestinationsWithCollectionPrefix(maintainableFieldDefinition.getOverrideFieldConversions(), collectionPrefix);
                field.setFieldConversions(prefixedFieldConversions);
                field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
                field.setReferencesToRefresh(LookupUtils.convertReferencesToSelectCollectionToString(
                        maintainable.getAffectedReferencesFromLookup(businessObject, attributeName, collectionPrefix)));
            }
            return field;
        }
        if (maintainableFieldDefinition.isNoLookup()) {
            return field;
        }
        return LookupUtils.setFieldQuickfinder(businessObject, collectionName, addLine, index,
                attributeName, field, displayedFieldNames, maintainable);
    }

    private static String prefixFieldConversionsDestinationsWithCollectionPrefix(String originalFieldConversions, String collectionPrefix) {
        StringBuilder buf = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(originalFieldConversions, KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
        boolean needsSeparator = false;
        while (tok.hasMoreTokens()) {
            String conversionPair = tok.nextToken();
            if (StringUtils.isBlank(conversionPair)) {
                continue;
            }

            String fromValue = StringUtils.substringBefore(conversionPair, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR);
            String toValue = StringUtils.substringAfter(conversionPair, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR);

            if (needsSeparator) {
                buf.append(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            }
            needsSeparator = true;

            buf.append(fromValue).append(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR).append(collectionPrefix).append(toValue);
        }
        return buf.toString();
    }

    public static final void setFieldDirectInquiry(BusinessObject businessObject, String attributeName, MaintainableFieldDefinition maintainableFieldDefinition, Field field, List<String> displayedFieldNames) {
        LookupUtils.setFieldDirectInquiry(businessObject, attributeName, field);
    }

    public static final void setFieldDirectInquiry(BusinessObject businessObject, String collectionName, boolean addLine, int index,
                                                   String attributeName, Field field, List<String> displayedFieldNames, Maintainable maintainable, MaintainableFieldDefinition maintainableFieldDefinition) {
        LookupUtils.setFieldDirectInquiry(businessObject, attributeName, field);
    }

    /**
     * Given a section, returns a comma delimited string of all fields, representing the error keys that exist for a section
     *
     * @param section a section
     * @return
     */
    public static String generateErrorKeyForSection(Section section) {
        Set<String> fieldPropertyNames = new HashSet<String>();
        addRowsToErrorKeySet(section.getRows(), fieldPropertyNames);

        StringBuilder buf = new StringBuilder();
        buf.append(section.getSectionId()).append(",");

        Iterator<String> nameIter = fieldPropertyNames.iterator();
        while (nameIter.hasNext()) {
            buf.append(nameIter.next());
            if (nameIter.hasNext()) {
                buf.append(",");
            }
        }

        if (section.getContainedCollectionNames() != null && section.getContainedCollectionNames().size() > 0) {
            buf.append(",");

            Iterator<String> collectionIter = section.getContainedCollectionNames().iterator();
            while (collectionIter.hasNext()) {
                buf.append(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + collectionIter.next());
                if (collectionIter.hasNext()) {
                    buf.append(",");
                }
            }
        }

        return buf.toString();
    }

    /**
     * This method recurses through all the fields of the list of rows and adds each field's property name to the set if it starts
     * with Constants.MAINTENANCE_NEW_MAINTAINABLE
     *
     * @param listOfRows
     * @param errorKeys
     * @see KRADConstants#MAINTENANCE_NEW_MAINTAINABLE
     */
    protected static void addRowsToErrorKeySet(List<Row> listOfRows, Set<String> errorKeys) {
        if (listOfRows == null) {
            return;
        }
        for (Row row : listOfRows) {
            List<Field> fields = row.getFields();
            if (fields == null) {
                continue;
            }
            for (Field field : fields) {
                String fieldPropertyName = field.getPropertyName();
                if (fieldPropertyName != null && fieldPropertyName.startsWith(KRADConstants.MAINTENANCE_NEW_MAINTAINABLE)) {
                    errorKeys.add(field.getPropertyName());
                }
                addRowsToErrorKeySet(field.getContainerRows(), errorKeys);
            }
        }
    }

    /**
     * This method will throw a {@link ValidationException} if there is a valid locking document in existence and throwExceptionIfLocked is true.
     */
    public static void checkForLockingDocument(Maintainable maintainable, boolean throwExceptionIfLocked) {
        LOG.info("starting checkForLockingDocument (by Maintainable)");

        // get the docHeaderId of the blocking docs, if any are locked and blocking
        //String blockingDocId = getMaintenanceDocumentService().getLockingDocumentId(maintainable, null);
        String blockingDocId = maintainable.getLockingDocumentId();
        org.kuali.rice.krad.maintenance.MaintenanceUtils
                .checkDocumentBlockingDocumentId(blockingDocId, throwExceptionIfLocked);
    }

    private static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }

    private static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public static Map<String, AttributeSecurity> retrievePropertyPathToAttributeSecurityMappings(String docTypeName) {
        Map<String, AttributeSecurity> results = new HashMap<String, AttributeSecurity>();
        MaintenanceDocumentEntry entry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);
        String className = entry.getDataObjectClass().getName();

        for (MaintainableSectionDefinition section : entry.getMaintainableSections()) {
            for (MaintainableItemDefinition item : section.getMaintainableItems()) {
                if (item instanceof MaintainableFieldDefinition) {
                    MaintainableFieldDefinition field = (MaintainableFieldDefinition) item;
                    AttributeSecurity attributeSecurity = getDataDictionaryService().getAttributeSecurity(className, field.getName());
                    if (attributeSecurity != null) {
                        results.put(field.getName(), attributeSecurity);
                    }
                } else if (item instanceof MaintainableCollectionDefinition) {
                    addMaintenanceDocumentCollectionPathToSecurityMappings(results, "", (MaintainableCollectionDefinition) item);
                }
            }
        }
        return results;
    }

    private static void addMaintenanceDocumentCollectionPathToSecurityMappings(Map<String, AttributeSecurity> mappings, String propertyPathPrefix, MaintainableCollectionDefinition collectionDefinition) {
        propertyPathPrefix = propertyPathPrefix + collectionDefinition.getName() + ".";
        String boClassName = collectionDefinition.getBusinessObjectClass().getName();
        for (MaintainableFieldDefinition field : collectionDefinition.getMaintainableFields()) {
            AttributeSecurity attributeSecurity = getDataDictionaryService().getAttributeSecurity(boClassName, field.getName());
            if (attributeSecurity != null) {
                mappings.put(propertyPathPrefix + field.getName(), attributeSecurity);
            }
        }
        for (MaintainableCollectionDefinition nestedCollection : collectionDefinition.getMaintainableCollections()) {
            addMaintenanceDocumentCollectionPathToSecurityMappings(mappings, propertyPathPrefix, nestedCollection);
        }
    }

    private static MaintenanceDocumentService getMaintenanceDocumentService() {
        if (maintenanceDocumentService == null) {
            maintenanceDocumentService = KRADServiceLocatorWeb.getMaintenanceDocumentService();
        }
        return maintenanceDocumentService;
    }

    private static WorkflowDocumentService getWorkflowDocumentService() {
        if (workflowDocumentService == null) {
            workflowDocumentService = KRADServiceLocatorWeb.getWorkflowDocumentService();
        }
        return workflowDocumentService;
    }

    private static ConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null) {
            kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return kualiConfigurationService;
    }

    private static KualiExceptionIncidentService getKualiExceptionIncidentService() {
        if (kualiExceptionIncidentService == null) {
            kualiExceptionIncidentService = KRADServiceLocatorWeb.getKualiExceptionIncidentService();
        }
        return kualiExceptionIncidentService;
    }
}
