/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.web.struts.form;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

public class AssetPaymentForm extends KualiAccountingDocumentFormBase {
    private static Log LOG = LogFactory.getLog(AssetPaymentForm.class);

    String capitalAssetNumber = "";

    /**
     * 
     * Constructs a AssetPaymentForm.java.
     */
    public AssetPaymentForm() {
        super();
        setDocument(new AssetPaymentDocument());
    }

    /**
     * 
     * This method gets the asset payment document
     * 
     * @return AssetPaymentDocument
     */
    public AssetPaymentDocument getAssetPaymentDocument() {
        return (AssetPaymentDocument) getDocument();
    }

    /**
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    @Override
    public Map<String, String> getForcedLookupOptionalFields() {
        Map<String, String> forcedLookupOptionalFields = super.getForcedLookupOptionalFields();
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE + ";" + DocumentType.class.getName());
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, RicePropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE + ";" + OriginationCode.class.getName());

        return forcedLookupOptionalFields;
    }

    /**
     * 
     * This method sets the asset# selected
     * 
     * @param capitalAssetNumber
     */
    public void setCapitalAssetNumber(String capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * 
     * gets the asset# that was previously selected
     * 
     * @return
     */
    public String getCapitalAssetNumber() {
        return this.capitalAssetNumber;
    }

    /**
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getNewSourceLine()
     */
    @Override
    public SourceAccountingLine getNewSourceLine() {
        
        //Getting the workflow document number created for the asset payment document.
        String worflowDocumentNumber = "";
        try {
            if (GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()) != null)
                worflowDocumentNumber = GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()).getRouteHeaderId().toString();
        }
        catch (Exception e) {
            throw new RuntimeException("Error converting workflow document number to string:" + e.getMessage());
        }

        AssetPaymentDetail newSourceLine = (AssetPaymentDetail) super.getNewSourceLine();

        //Getting the document type code in order set it as default in the new source accounting line.
        String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(AssetPaymentDocument.class);
        
        //Setting default document type.
        if (newSourceLine.getExpenditureFinancialDocumentTypeCode() == null || newSourceLine.getExpenditureFinancialDocumentTypeCode().trim().equals("")) {
            newSourceLine.setExpenditureFinancialDocumentTypeCode(documentTypeCode);
        }
        
        //Setting the default asset payment row document number.
        newSourceLine.setExpenditureFinancialDocumentNumber(worflowDocumentNumber);
        
        return (SourceAccountingLine) newSourceLine;
    }

    /**
     * 
     * This method stores in a Map the total amounts that need to be display on the asset payment screen and that will be passed as
     * parameter to the accountingLine.tag in order to display them.
     * 
     * @return LinkedHashMap
     */
    public LinkedHashMap<String, String> getAssetPaymentTotals() {
        LinkedHashMap<String, String> totals = new LinkedHashMap<String, String>();
        CurrencyFormatter cf = new CurrencyFormatter();
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        KualiDecimal assetTotalCost = (ObjectUtils.isNull(getAssetPaymentDocument().getAsset().getTotalCostAmount()) ? new KualiDecimal(0) : getAssetPaymentDocument().getAsset().getTotalCostAmount());

        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.TOTAL_LABEL), (String) cf.format(this.getAssetPaymentDocument().getSourceTotal()));
        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.PREVIOUS_COST_LABEL), (String) cf.format(assetTotalCost));
        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.NEW_TOTAL_LABEL), (String) cf.format(getAssetPaymentDocument().getSourceTotal().add(assetTotalCost)));
        return totals;
    }
}