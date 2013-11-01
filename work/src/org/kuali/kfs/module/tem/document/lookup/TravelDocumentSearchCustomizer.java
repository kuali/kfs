/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValue;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.krad.service.DocumentService;

public class TravelDocumentSearchCustomizer extends KFSDocumentSearchCustomizer {

    public static Logger LOG = Logger.getLogger(TravelDocumentSearchCustomizer.class);

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#customizeResults(org.kuali.rice.kew.api.document.search.DocumentSearchCriteria,
     *      java.util.List)
     */
    @Override
    public DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria, List<DocumentSearchResult> defaultResults) {

        DocumentSearchResultValues.Builder customResultsBuilder = DocumentSearchResultValues.Builder.create();
        List<DocumentSearchResultValue.Builder> customResultValueBuilders = new ArrayList<DocumentSearchResultValue.Builder>();

        for (DocumentSearchResult result : defaultResults) {
            // add the action attribute for the TEM documents for processed documents
            List<DocumentAttribute.AbstractBuilder<?>> custAttrBuilders = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
            Document document = result.getDocument();

            DocumentAttributeString.Builder attributeBuilder = DocumentAttributeString.Builder.create(TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS);
            attributeBuilder.setValue(buildCustomActionHTML(result, getDocument(document.getDocumentId())));
            custAttrBuilders.add(attributeBuilder);

            DocumentSearchResultValue.Builder builder = DocumentSearchResultValue.Builder.create(document.getDocumentId());
            builder.setDocumentAttributes(custAttrBuilders);
            customResultValueBuilders.add(builder);
        }
        customResultsBuilder.setResultValues(customResultValueBuilders);
        return customResultsBuilder.build();
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#customizeResultSetConfiguration(org.kuali.rice.kew.api.document.search.DocumentSearchCriteria)
     */
    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(DocumentSearchCriteria documentSearchCriteria) {

        DocumentSearchResultSetConfiguration.Builder config = DocumentSearchResultSetConfiguration.Builder.create(super.customizeResultSetConfiguration(documentSearchCriteria));
        // do not remove any standard fields
        config.setStandardResultFieldsToRemove(null);

        final String ACTION = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
        List<String> customFieldNames = new ArrayList<String>();
        customFieldNames.add(ACTION);
        config.setCustomFieldNamesToAdd(customFieldNames);

        // set the additional attribute field for action
        List<RemotableAttributeField.Builder> attributeBuilderList = new ArrayList<RemotableAttributeField.Builder>();
        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(ACTION);
        builder.setDataType(DataType.MARKUP);
        builder.setLongLabel(WordUtils.capitalize(ACTION));
        builder.setShortLabel(WordUtils.capitalize(ACTION));
        builder.setMaxLength(1000);
        attributeBuilderList.add(builder);
        config.setAdditionalAttributeFields(attributeBuilderList);
        return config.build();
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#isCustomizeResultsEnabled(java.lang.String)
     */
    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        return true;
    }

    /**
     * @param documentNumber
     * @return
     * @throws WorkflowException
     */
    public TravelDocument getDocument(String documentNumber) {
        TravelDocument document = null;
        try {
            document = (TravelDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return document;
    }

    /**
     * Build the full action URL.
     * NOTE: status check should be done in the override class
     *
     * @param documentSearchResult
     * @param document
     * @return
     */
    String buildCustomActionHTML(DocumentSearchResult documentSearchResult, TravelDocument document){
        TravelDocumentCustomActionBuilder actionBuilder = null;
        if (TravelDocTypes.getAuthorizationDocTypes().contains(document.getDocumentTypeName())){
            actionBuilder = new TravelAuthorizationDocumentCustomActionBuilder();
        }else if (TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(document.getDocumentTypeName())){
            actionBuilder = new TravelReimbursementDocumentCustomActionBuilder();
        }else if (TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT.equals(document.getDocumentTypeName())){
            actionBuilder = new TravelEntertainmentDocumentCustomActionBuilder();
        }else if (TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT.equals(document.getDocumentTypeName())){
            actionBuilder = new TravelRelocationDocumentCustomActionBuilder();
        }else{
            LOG.error("Travel Document [" + document.getDocumentTypeName() + "] does not have a customActionURL defined");
        }
        return actionBuilder != null? actionBuilder.buildCustomActionHTML(documentSearchResult, document) : "";
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TemRoleService getTemRoleService() {
        return SpringContext.getBean(TemRoleService.class);
    }

    protected TemProfileService getTemProfileService() {
        return SpringContext.getBean(TemProfileService.class);
    }

    protected DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }
}
