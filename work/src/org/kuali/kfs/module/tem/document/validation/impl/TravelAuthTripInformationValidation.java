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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthTripInformationValidation extends GenericValidation {
    private TravelService travelService;
    private TravelDocumentService travelDocumentService;
    private DictionaryValidationService dictionaryValidationService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelDocumentBase document = (TravelDocumentBase) event.getDocument();

        Boolean rulePassed = true;

        if ((!StringUtils.isBlank(document.getTraveler().getPhoneNumber())) && (!StringUtils.isBlank(document.getTraveler().getCountryCode()))) {
            String errorMessage = getTravelService().validatePhoneNumber(document.getTraveler().getCountryCode(), document.getTraveler().getPhoneNumber(), TemKeyConstants.ERROR_PHONE_NUMBER);
            if (!StringUtils.isBlank(errorMessage)) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TravelAuthorizationFields.TRAVELER_PHONENUMBER, errorMessage, new String[] { "Traveler Phone Number" });
                rulePassed = false;
            }
        }

        if (event.getDocument() instanceof TravelReimbursementDocument) {
            if (document.getTripEnd() != null) {
                Date endDate = KfsDateUtils.clearTimeFields(document.getTripEnd());
                Date today = KfsDateUtils.clearTimeFields(new Date());
                Boolean value = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.PRETRIP_REIMBURSEMENT_IND);

                if (endDate != null && today.before(endDate) && !value.booleanValue()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Travel Reimbursement Document cannot be submitted before the trip end date has passed.");
                    rulePassed = false;
                }
            }
        }

        if (document.getTripTypeCode() != null) {
            document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);

            if (!document.getTripType().getUsePerDiem() && document.getPerDiemExpenses() != null && !document.getPerDiemExpenses().isEmpty()) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_EXPENSES, KFSKeyConstants.ERROR_CUSTOM, "Per Diem entry is not allowed for this Trip Type [" + document.getTripType().getCode() + "].");
                rulePassed = false;
            }

            if (!document.isBlanketTravel() && (document.getDocumentGrandTotal().isLessEqual(KualiDecimal.ZERO))) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.TRVL_AUTH_TOTAL_ESTIMATE, TemKeyConstants.ERROR_DOCUMENT_TOTAL_ESTIMATED);
                rulePassed = false;
            }

            //validate the primary destination if selected indicator
            if (!document.getPrimaryDestinationIndicator()){
                PrimaryDestination destination = document.getPrimaryDestination();
                //primary destination trip type code should match the document
                if (!ObjectUtils.isNull(destination.getRegion()) && !document.getTripTypeCode().equals(destination.getRegion().getTripTypeCode())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TravelAuthorizationFields.TRIP_TYPE, TemKeyConstants.ERROR_TRIP_TYPE_CD_PRI_DEST_MISMATCH);
                    rulePassed = false;
                }
            }

            if (document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
                if (document.getTripType().getTravelAuthorizationRequired()) {
                    TravelAuthorizationDocument taDoc = getTravelDocumentService().findCurrentTravelAuthorization(document);
                    if (taDoc == null) {
                        GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Travel Authorization Document is required for this Trip Type [" + document.getTripType().getCode() + "].");
                        rulePassed = false;
                    }
                }
            }
        }

        return rulePassed;
    }

    /**
     * Gets the travelService attribute.
     *
     * @return Returns the travelService.
     */
    public TravelService getTravelService() {
        return travelService;
    }

    /**
     * Sets the travelService attribute value.
     *
     * @param travelService The travelService to set.
     */
    public void setTravelService(TravelService travelService) {
        this.travelService = travelService;
    }

    /**
     * Gets the dictionaryValidationService attribute.
     *
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     *
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    protected TravelDocumentService getTravelDocumentService() {
        if (travelDocumentService == null) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }
}
