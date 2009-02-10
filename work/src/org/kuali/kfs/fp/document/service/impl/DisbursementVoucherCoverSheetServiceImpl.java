/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherDocumentationLocation;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.fp.businessobject.options.PaymentMethodValuesFinder;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * This is the default implementation of the DisbursementVoucherCoverSheetService interface.
 */
public class DisbursementVoucherCoverSheetServiceImpl implements DisbursementVoucherCoverSheetService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherCoverSheetServiceImpl.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private PersistenceStructureService persistenceStructureService;

    /**
     * This method uses the values provided to build and populate a cover sheet associated with a given DisbursementVoucher.
     * 
     * @param templateDirectory The directory where the cover sheet template can be found.
     * @param templateName The name of the cover sheet template to be used to build the cover sheet.
     * @param document The DisbursementVoucher the cover sheet will be populated from.
     * @param outputStream The stream the cover sheet file will be written to.
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService#generateDisbursementVoucherCoverSheet(java.lang.String,
     *      java.lang.String, org.kuali.kfs.fp.document.DisbursementVoucherDocument, java.io.OutputStream)
     */
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException {
        if (this.isCoverSheetPrintable(document)) {
            String attachment = "";
            String handling = "";
            String alien = "";
            String lines = "";
            String bar = "";
            String rlines = "";

            String docNumber = document.getDocumentNumber();
            String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            String payee = document.getDvPayeeDetail().getDisbVchrPayeePersonName();

            String reason = ((PaymentReasonCode) retrieveObjectByKey(PaymentReasonCode.class, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())).getName();
            String check_total = document.getDisbVchrCheckTotalAmount().toString();

            String currency = getValueForKey(new PaymentMethodValuesFinder(), document.getDisbVchrPaymentMethodCode());

            String address = retrieveAddress(document.getDisbursementVoucherDocumentationLocationCode());

            // retrieve attachment label
            if (document.isDisbVchrAttachmentCode()) {
                attachment = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_ATTACHMENT_PARM_NM);
            }
            // retrieve handling label
            if (document.isDisbVchrSpecialHandlingCode()) {
                handling = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_HANDLING_PARM_NM);
            }
            // retrieve data for alien payment code
            if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
                String taxDocumentationLocationCode = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM);

                address = retrieveAddress(taxDocumentationLocationCode);
                alien = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_ALIEN_PARM_NM);
                lines = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_LINES_PARM_NM);
            }
            
            // determine if non-employee travel payment reasons
            String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
            ParameterEvaluator travelNonEmplPaymentReasonEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, paymentReasonCode);
            boolean isTravelNonEmplPaymentReason = travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();

            if (isTravelNonEmplPaymentReason) {
                bar = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_BAR_PARM_NM);
                rlines = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_RLINES_PARM_NM);
            }

            try {
                PdfReader reader = new PdfReader(templateDirectory + File.separator + templateName);

                // populate form with document values
                PdfStamper stamper = new PdfStamper(reader, outputStream);

                AcroFields populatedCoverSheet = stamper.getAcroFields();
                populatedCoverSheet.setField("initiator", initiator);
                populatedCoverSheet.setField("attachment", attachment);
                populatedCoverSheet.setField("currency", currency);
                populatedCoverSheet.setField("handling", handling);
                populatedCoverSheet.setField("alien", alien);
                populatedCoverSheet.setField("payee_name", payee);
                populatedCoverSheet.setField("check_total", check_total);
                populatedCoverSheet.setField("docNumber", docNumber);
                populatedCoverSheet.setField("payment_reason", reason);
                populatedCoverSheet.setField("destination_address", address);
                populatedCoverSheet.setField("lines", lines);
                populatedCoverSheet.setField("bar", bar);
                populatedCoverSheet.setField("rlines", rlines);

                stamper.setFormFlattening(true);
                stamper.close();
            }
            catch (DocumentException e) {
                LOG.error("Error creating coversheet for: " + docNumber + ". ::" + e);
                throw e;
            }
            catch (IOException e) {
                LOG.error("Error creating coversheet for: " + docNumber + ". ::" + e);
                throw e;
            }
        }

    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService#isCoverSheetPrintable(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public boolean isCoverSheetPrintable(DisbursementVoucherDocument document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }

    /**
     * This method is used to retrieve business objects that have a single primary key field without hard-coding the key field name.
     * 
     * @param clazz The class type that will be used to retrieve the primary key field names.
     * @param keyValue The primary key value to be used to lookup the object by.
     * @return An instance of a business object matching the class type and primary key value given.
     */
    private PersistableBusinessObject retrieveObjectByKey(Class clazz, String keyValue) {
        List primaryKeyFields = persistenceStructureService.listPrimaryKeyFieldNames(clazz);
        if (primaryKeyFields.size() != 1) {
            throw new IllegalArgumentException("multi-part key found. expecting single key field for " + clazz.getName());
        }
        Map primaryKeys = new HashMap();
        primaryKeys.put(primaryKeyFields.get(0), keyValue);
        PersistableBusinessObject b = businessObjectService.findByPrimaryKey(clazz, primaryKeys);

        return b;
    }

    /**
     * This method is a helper method to retrieve values from a list based on a primary key provided.
     * 
     * @param keyValuesFinder KeyValuesFinder that the value will be retrieved from.
     * @param key The key to the value being retrieved.
     * @return The value associated with the key provided, or empty string if no value is found.
     */
    private String getValueForKey(KeyValuesFinder keyValuesFinder, String key) {
        for (Iterator i = keyValuesFinder.getKeyValues().iterator(); i.hasNext();) {
            KeyLabelPair pair = (KeyLabelPair) i.next();
            if (StringUtils.equals((String) pair.getKey(), key)) {
                return pair.getLabel();
            }
        }
        return "";
    }

    /**
     * This method contains logic to determine the address the cover sheet should be sent to.
     * 
     * @param docLocCd A key used to retrieve the document location.
     * @return The address the cover sheet will be sent to or empty string if no location is found.
     */
    private String retrieveAddress(String docLocCd) {
        String address = "";
        try {
            address = ((DisbursementVoucherDocumentationLocation) retrieveObjectByKey(DisbursementVoucherDocumentationLocation.class, docLocCd)).getDisbursementVoucherDocumentationLocationAddress();
        }
        catch (NullPointerException e) {
            // ignored
        }

        return address;
    }

    // spring injected services

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
