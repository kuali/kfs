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
package org.kuali.kfs.sys.document.workflow;
//RICE20 Hook to document type is not working right now but needs to be changed to support pre-rice2.0 release
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomizer;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kew.framework.document.search.StandardResultField;

public class KFSDocumentSearchCustomizer implements SearchableAttribute, DocumentSearchCustomizer {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSDocumentSearchCustomizer.class);

    protected SearchableAttribute searchableAttribute;

    public KFSDocumentSearchCustomizer() {
        this(new FinancialSystemSearchableAttribute());
    }

    public KFSDocumentSearchCustomizer(SearchableAttribute searchableAttribute) {
        this.searchableAttribute = searchableAttribute;
    }

    @Override
    public DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria, List<DocumentSearchResult> defaultResults) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "customizeResults( " + documentSearchCriteria + ", " + defaultResults + " )" );
        }
        return null;
    }

    @Override
    public final String generateSearchContent(ExtensionDefinition extensionDefinition,
            String documentTypeName,
            WorkflowAttributeDefinition attributeDefinition) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "generateSearchContent( " + extensionDefinition + ", " + documentTypeName + ", " + attributeDefinition + " )" );
        }
        return getSearchableAttribute().generateSearchContent(extensionDefinition, documentTypeName, attributeDefinition);
    }

    @Override
    public final List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition,
            DocumentWithContent documentWithContent) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "extractDocumentAttributes( " + extensionDefinition + ", " + documentWithContent + " )" );
        }
        return getSearchableAttribute().extractDocumentAttributes(extensionDefinition, documentWithContent);
    }

    @Override
    public final List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition,
            String documentTypeName) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "getSearchFields( " + extensionDefinition + ", " + documentTypeName + " )" );
        }
        return getSearchableAttribute().getSearchFields(extensionDefinition, documentTypeName);
    }

    @Override
    public final List<RemotableAttributeError> validateDocumentAttributeCriteria(ExtensionDefinition extensionDefinition,
            DocumentSearchCriteria documentSearchCriteria) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "validateDocumentAttributeCriteria( " + extensionDefinition + ", " + documentSearchCriteria + " )" );
        }
        return getSearchableAttribute().validateDocumentAttributeCriteria(extensionDefinition, documentSearchCriteria);
    }

    protected SearchableAttribute getSearchableAttribute() {
        return this.searchableAttribute;
    }

    public void setSearchableAttribute(SearchableAttribute searchableAttribute) {
        this.searchableAttribute = searchableAttribute;
    }

    @Override
    public DocumentSearchCriteria customizeCriteria(DocumentSearchCriteria documentSearchCriteria) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "customizeCriteria( " + documentSearchCriteria + " )" );
        }
        // since this is a result display option, we need to remove it from the criteria to prevent
        // the query from blowing up or returning no results.
        if ( documentSearchCriteria.getDocumentAttributeValues().containsKey( FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME ) ) {
            DocumentSearchCriteria.Builder newCriteria = DocumentSearchCriteria.Builder.create(documentSearchCriteria);
            Map<String, List<String>> searchOptions = new HashMap<String, List<String>>();
            searchOptions.put(FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME,
                            newCriteria.getDocumentAttributeValues().get(FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME));
            newCriteria.setSearchOptions(searchOptions);
            newCriteria.getDocumentAttributeValues().remove( FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME );
            return newCriteria.build();
        }
        return null;
    }

    @Override
    public DocumentSearchCriteria customizeClearCriteria(DocumentSearchCriteria documentSearchCriteria) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "customizeClearCriteria( " + documentSearchCriteria + " )" );
        }
        DocumentSearchCriteria.Builder newCriteria = DocumentSearchCriteria.Builder.create();
        newCriteria.setDocumentTypeName(documentSearchCriteria.getDocumentTypeName());
        return newCriteria.build();
    }

    protected static final List<StandardResultField> standardResultsToRemove = new ArrayList<StandardResultField>();
    static {
        standardResultsToRemove.add(StandardResultField.DOCUMENT_TYPE);
        standardResultsToRemove.add(StandardResultField.TITLE);
        //standardResultsToRemove.add(StandardResultField.STATUS);
        standardResultsToRemove.add(StandardResultField.INITIATOR);
        standardResultsToRemove.add(StandardResultField.DATE_CREATED);
    }

    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(DocumentSearchCriteria documentSearchCriteria) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "customizeResultSetConfiguration( " + documentSearchCriteria + " )" );
        }
        DocumentSearchResultSetConfiguration.Builder config = DocumentSearchResultSetConfiguration.Builder.create();
        config.setOverrideSearchableAttributes(false);
        config.setStandardResultFieldsToRemove(standardResultsToRemove);

        List<String> displayTypeList = documentSearchCriteria.getSearchOptions().get(FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME);
        if ( displayTypeList != null && !displayTypeList.isEmpty() ) {

            String displayType =  displayTypeList.get(0);
            if ( StringUtils.equals(displayType, FinancialSystemSearchableAttribute.WORKFLOW_DISPLAY_TYPE_VALUE)) {
                config.setOverrideSearchableAttributes(true);
                config.setStandardResultFieldsToRemove(null);
            }
        }
        return config.build();
    }

    @Override
    public boolean isCustomizeCriteriaEnabled(String documentTypeName) {
        return true;
    }

    @Override
    public boolean isCustomizeClearCriteriaEnabled(String documentTypeName) {
        return true;
    }

    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        return false;
    }

    @Override
    public boolean isCustomizeResultSetFieldsEnabled(String documentTypeName) {
        return true;
    }


}
