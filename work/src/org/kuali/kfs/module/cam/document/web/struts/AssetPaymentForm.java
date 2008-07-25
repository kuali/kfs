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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

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
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    @Override
    public Map<String, String> getForcedLookupOptionalFields() {
        Map<String, String> forcedLookupOptionalFields = super.getForcedLookupOptionalFields();
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE + ";" + DocumentType.class.getName());
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE + ";" + OriginationCode.class.getName());

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
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getNewSourceLine()
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
        if (newSourceLine.getExpenditureFinancialDocumentNumber() == null || newSourceLine.getExpenditureFinancialDocumentNumber().trim().equals("")) {        
            newSourceLine.setExpenditureFinancialDocumentNumber(worflowDocumentNumber);
        }

        //Setting the default asset payment row origination code.
        if (newSourceLine.getExpenditureFinancialSystemOriginationCode() == null || newSourceLine.getExpenditureFinancialSystemOriginationCode().trim().equals("")) {        
            newSourceLine.setExpenditureFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
        }        
        return (SourceAccountingLine) newSourceLine;
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
      LOG.info("****************Request Parameters*************");
      Enumeration paramNames;
      paramNames = request.getParameterNames();
      while (paramNames.hasMoreElements()) {
          String name = (String) paramNames.nextElement();            
          String[] values = request.getParameterValues(name);
          
          for (int x=0;x < values.length;x++)
              LOG.info("******Request Parameters: "+name +"["+x+"]: "+values[x]);
                      
      }
      LOG.info("**********************************************");                    
        
        super.populate(request);
        //This would store the latest total cost of the asset into the asset payment document.
        if (this.getAssetPaymentDocument().getAsset() != null) {
            this.getAssetPaymentDocument().refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
            this.getAssetPaymentDocument().setPreviousTotalCostAmount(this.getAssetPaymentDocument().getAsset().getTotalCostAmount());
        }
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

        //KualiDecimal assetTotalCost = (ObjectUtils.isNull(getAssetPaymentDocument().getAsset().getTotalCostAmount()) ? new KualiDecimal(0) : getAssetPaymentDocument().getAsset().getTotalCostAmount());
        KualiDecimal assetTotalCost = (ObjectUtils.isNull(getAssetPaymentDocument().getPreviousTotalCostAmount()) ? new KualiDecimal(0) : getAssetPaymentDocument().getPreviousTotalCostAmount());

        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.TOTAL_LABEL), (String) cf.format(this.getAssetPaymentDocument().getSourceTotal()));
        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.PREVIOUS_COST_LABEL), (String) cf.format(assetTotalCost));
        totals.put(kualiConfiguration.getPropertyString(CamsKeyConstants.Payment.NEW_TOTAL_LABEL), (String) cf.format(getAssetPaymentDocument().getSourceTotal().add(assetTotalCost)));
        return totals;
    }
}
