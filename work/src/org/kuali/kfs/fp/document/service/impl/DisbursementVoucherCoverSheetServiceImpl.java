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
package org.kuali.module.financial.service.impl;

import static org.kuali.module.financial.rules.DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation;
import org.kuali.module.financial.bo.PaymentReasonCode;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.lookup.keyvalues.PaymentMethodValuesFinder;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherCoverSheetService;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * 
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 */
public class DisbursementVoucherCoverSheetServiceImpl implements DisbursementVoucherCoverSheetService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherCoverSheetServiceImpl.class);

    public static final String DV_COVERSHEET_TEMPLATE_RELATIVE_DIR = "static/help/templates/financial";
    public static final String DV_COVERSHEET_TEMPLATE_NM = "disbursementVoucherCoverSheetTemplate.pdf";

    public static String DV_COVER_SHEET_TEMPLATE_LINES_PARM_NM = "DV_COVER_SHEET_TEMPLATE_LINES";
    public static String DV_COVER_SHEET_TEMPLATE_RLINES_PARM_NM = "DV_COVER_SHEET_TEMPLATE_RLINES";
    public static String DV_COVER_SHEET_TEMPLATE_ALIEN_PARM_NM = "DV_COVER_SHEET_TEMPLATE_ALIEN";
    public static String DV_COVER_SHEET_TEMPLATE_ATTACHMENT_PARM_NM = "DV_COVER_SHEET_TEMPLATE_ATTACHMENT";
    public static String DV_COVER_SHEET_TEMPLATE_HANDLING_PARM_NM = "DV_COVER_SHEET_TEMPLATE_HANDLING";
    public static String DV_COVER_SHEET_TEMPLATE_BAR_PARM_NM = "DV_COVER_SHEET_TEMPLATE_BAR";

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private PersistenceStructureService persistenceStructureService;

    /**
     * 
     * @see org.kuali.module.financial.service.DisbursementVoucherCoverSheetService#generateDisbursementVoucherCoverSheet(java.lang.String,
     *      java.lang.String, org.kuali.module.financial.document.DisbursementVoucherDocument, java.io.OutputStream)
     */
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException {
        DisbursementVoucherDocumentRule documentRule = new DisbursementVoucherDocumentRule();
        if (documentRule.isCoverSheetPrintable(document)) {
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
                attachment = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_ATTACHMENT_PARM_NM);
            }
            // retrieve handling label
            if (document.isDisbVchrSpecialHandlingCode()) {
                handling = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_HANDLING_PARM_NM);
            }
            // retrieve data for alien payment code
            if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
                String taxDocumentationLocationCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM, DisbursementVoucherRuleConstants.TAX_DOCUMENTATION_LOCATION_CODE_PARM_NM);

                address = retrieveAddress(taxDocumentationLocationCode);
                alien = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_ALIEN_PARM_NM);
                lines = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_LINES_PARM_NM);
            }
            // retrieve data for travel payment reasons
            String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM, DisbursementVoucherRuleConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);
            if (RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
                bar = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_BAR_PARM_NM);
                rlines = kualiConfigurationService.getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, DV_COVER_SHEET_TEMPLATE_RLINES_PARM_NM);
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
     * used to retrieve BO's that have a single primary key field without hardcoding the key field name.
     * 
     * @param clazz
     * @param keyValue
     * @return
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
     * helper method to retrieve values from a list
     * 
     * @param keyValuesFinder
     * @param key
     * @return
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
     * contains logic to determine adress the cover sheet should be sent to
     * 
     * @param docLocCd
     * @return
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
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

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
}