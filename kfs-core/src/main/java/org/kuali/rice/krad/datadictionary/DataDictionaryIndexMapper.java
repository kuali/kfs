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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.View;
import org.springframework.beans.PropertyValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A DataDictionaryMapper that simply consults the statically initialized
 * DataDictionaryIndex mappings
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionaryIndexMapper implements DataDictionaryMapper {
    private static final Logger LOG = Logger.getLogger(DataDictionaryIndexMapper.class);

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getAllInactivationBlockingMetadatas(DictionaryIndex, java.lang.Class)
     */
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(DictionaryIndex index, Class<?> blockedClass) {
        return index.getInactivationBlockersForClass().get(blockedClass);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getBusinessObjectClassNames(DictionaryIndex)
     */
    public List<String> getBusinessObjectClassNames(DictionaryIndex index) {
        List classNames = new ArrayList();
        classNames.addAll(index.getBusinessObjectEntries().keySet());

        return Collections.unmodifiableList(classNames);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getBusinessObjectEntries(DictionaryIndex)
     */
    public Map<String, BusinessObjectEntry> getBusinessObjectEntries(DictionaryIndex index) {
        return index.getBusinessObjectEntries();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDataObjectEntryForConcreteClass(DictionaryIndex, java.lang.String)
     */
    @Override
    public DataObjectEntry getDataObjectEntryForConcreteClass(DictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getDataObjectEntry '" + className + "'");
        }

        String trimmedClassName = className;
        int index = className.indexOf("$$");
        if (index >= 0) {
            trimmedClassName = className.substring(0, index);
        }
        return ddIndex.getBusinessObjectEntries().get(trimmedClassName);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getBusinessObjectEntryForConcreteClass(java.lang.String)
     */
    public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(DictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getBusinessObjectEntry '" + className + "'");
        }
        int index = className.indexOf("$$");
        if (index >= 0) {
            className = className.substring(0, index);
        }
        return ddIndex.getBusinessObjectEntries().get(className);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDictionaryObjectEntry(DictionaryIndex, java.lang.String)
     */
    public DataDictionaryEntry getDictionaryObjectEntry(DictionaryIndex ddIndex, String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getDictionaryObjectEntry '" + className + "'");
        }
        int index = className.indexOf("$$");
        if (index >= 0) {
            className = className.substring(0, index);
        }

        // look in the JSTL key cache
        DataDictionaryEntry entry = ddIndex.getEntriesByJstlKey().get(className);

        // check the Object list
        if (entry == null){
            entry = ddIndex.getBusinessObjectEntries().get(className);
        }
        // KULRICE-8005 Breaks when override business object classes
        // check the BO list
        if ( entry == null ) {
            entry = getBusinessObjectEntry(ddIndex, className);
        }
        // check the document list
        if ( entry == null ) {
            entry = getDocumentEntry(ddIndex, className);
        }

        return entry;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDataObjectEntry(DictionaryIndex, java.lang.String)
     */
    @Override
    public DataObjectEntry getDataObjectEntry(DictionaryIndex index, String className) {
        DataObjectEntry entry = getDataObjectEntryForConcreteClass(index, className);

        if (entry == null) {
            Class<?> boClass = null;
            try{
                boClass = Class.forName(className);
                ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(boClass);
                if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(boClass)) {
                    entry = responsibleModuleService.getExternalizableBusinessObjectDictionaryEntry(boClass);
                }
            } catch(ClassNotFoundException cnfex){
                // swallow so we can return null
            }
        }

        return entry;
    }

    public BusinessObjectEntry getBusinessObjectEntry(DictionaryIndex index, String className ) {
        BusinessObjectEntry entry = getBusinessObjectEntryForConcreteClass(index, className);
        if (entry == null) {
            Class boClass = null;
            try{
                boClass = Class.forName(className);
                ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(boClass);
                if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(boClass)) {
                    return responsibleModuleService.getExternalizableBusinessObjectDictionaryEntry(boClass);
                }
            } catch(ClassNotFoundException cnfex){
            }
            return null;
        }
        else {
            return entry;
        }
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDocumentEntries(DictionaryIndex)
     */
    public Map<String, DocumentEntry> getDocumentEntries(DictionaryIndex index) {
        return Collections.unmodifiableMap(index.getDocumentEntries());
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDocumentEntry(DictionaryIndex, java.lang.String)
     */
    public DocumentEntry getDocumentEntry(DictionaryIndex index, String documentTypeDDKey) {

        if (StringUtils.isBlank(documentTypeDDKey)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeDDKey + "'");
        }

        DocumentEntry de = index.getDocumentEntries().get(documentTypeDDKey);

        if ( de == null ) {
            try {
                Class<?> clazz = Class.forName( documentTypeDDKey );
                de = index.getDocumentEntriesByBusinessObjectClass().get(clazz);
                if ( de == null ) {
                    de = index.getDocumentEntriesByMaintainableClass().get(clazz);
                }
            } catch ( ClassNotFoundException ex ) {
                LOG.warn( "Unable to find document entry for key: " + documentTypeDDKey );
            }
        }

        return de;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getDocumentTypeName(DictionaryIndex, java.lang.String)
     */
    public String getDocumentTypeName(DictionaryIndex index,
                                      String documentTypeName) {
        // TODO arh14 - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getMaintenanceDocumentEntryForBusinessObjectClass(DictionaryIndex, java.lang.Class)
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(DictionaryIndex index, Class<?> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getDocumentEntry by dataObjectClass '" + businessObjectClass + "'");
        }

        return (MaintenanceDocumentEntry) index.getDocumentEntriesByBusinessObjectClass().get(businessObjectClass);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getViewById(org.kuali.rice.krad.datadictionary.view.ViewDictionaryIndex,
     *      java.lang.String)
     */
    public View getViewById(UifDictionaryIndex index, String viewId) {
        if (StringUtils.isBlank(viewId)) {
            throw new IllegalArgumentException("invalid (blank) view id");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling getViewById by id '" + viewId + "'");
        }

        return index.getViewById(viewId);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getViewByTypeIndex(UifDictionaryIndex,
     *      java.lang.String, java.util.Map)
     */
    public View getViewByTypeIndex(UifDictionaryIndex index, UifConstants.ViewType viewTypeName, Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.getViewByTypeIndex(viewTypeName, indexKey);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryIndexMapper#viewByTypeExist(UifDictionaryIndex,
     *      java.lang.String, java.util.Map)
     */
    public boolean viewByTypeExist(UifDictionaryIndex index, UifConstants.ViewType viewTypeName,
                                   Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.viewByTypeExist(viewTypeName, indexKey);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getViewPropertiesById(org.kuali.rice.krad.datadictionary.view.ViewDictionaryIndex,
     *      java.lang.String)
     */
    public PropertyValues getViewPropertiesById(UifDictionaryIndex index, String viewId) {
        if (StringUtils.isBlank(viewId)) {
            throw new IllegalArgumentException("invalid (blank) view id");
        }

        return index.getViewPropertiesById(viewId);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryIndexMapper#getViewPropertiesByType(UifDictionaryIndex,
     *      java.lang.String, java.util.Map)
     */
    public PropertyValues getViewPropertiesByType(UifDictionaryIndex index, UifConstants.ViewType viewTypeName,
                                                  Map<String, String> indexKey) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }
        if ((indexKey == null) || indexKey.isEmpty()) {
            throw new IllegalArgumentException("index key must have at least one entry");
        }

        return index.getViewPropertiesByType(viewTypeName, indexKey);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryMapper#getViewsForType(UifDictionaryIndex,
     *      java.lang.String)
     */
    public List<View> getViewsForType(UifDictionaryIndex index, UifConstants.ViewType viewTypeName) {
        if (viewTypeName == null) {
            throw new IllegalArgumentException("invalid (blank) view type name");
        }

        return index.getViewsForType(viewTypeName);
    }

}
