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
package org.kuali.kfs.module.tem.document.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.exception.WorkflowException;
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

            if(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT.equals(document.getDocumentTypeName())) {
                for (DocumentAttribute documentAttribute : result.getDocumentAttributes()) {
                    if (TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER.equals(documentAttribute.getName())) {
                        if (maskOrgDocNumberAndTravelDocumentIdentifier(document) ) {
                               DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER);
                                builder.setValue("********");
                                custAttrBuilders.add(builder);

                        }
                    }

                    if (TemPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER.equals(documentAttribute.getName())) {
                        if (maskOrgDocNumberAndTravelDocumentIdentifier(document) ) {
                            DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(TemPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);
                             builder.setValue("********");
                             custAttrBuilders.add(builder);

                     }
                    }

                }
            }

            DocumentSearchResultValue.Builder builder = DocumentSearchResultValue.Builder.create(document.getDocumentId());
            builder.setDocumentAttributes(custAttrBuilders);
            customResultValueBuilders.add(builder);
        }
        customResultsBuilder.setResultValues(customResultValueBuilders);
        return customResultsBuilder.build();

    }

    public boolean maskOrgDocNumberAndTravelDocumentIdentifier (Document document) {
        boolean vendorPaymentAllowedBeforeFinal = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);
        if(!vendorPaymentAllowedBeforeFinal && !(KFSConstants.DocumentStatusCodes.PROCESSED.equals(document.getStatus().getCode()) || KFSConstants.DocumentStatusCodes.FINAL.equals(document.getStatus().getCode()))) {
            return true;
        }

        return false;
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

    protected DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }
}
