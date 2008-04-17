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
import org.kuali.core.bo.DocumentType;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;

public class AssetPaymentForm extends KualiAccountingDocumentFormBase {
    private static Log LOG = LogFactory.getLog(AssetPaymentForm.class);

    String capitalAssetNumber="";
    
    public AssetPaymentForm() {
        super();
        setDocument(new AssetPaymentDocument());        
    }   
        
    public AssetPaymentDocument getAssetPaymentDocument() {
        return (AssetPaymentDocument) getDocument();
    }

        
    @Override
    public Map getForcedLookupOptionalFields() {
        Map forcedLookupOptionalFields= super.getForcedLookupOptionalFields();
        String lookupField = KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE;
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE,lookupField + "," + DocumentType.class.getName());
        return forcedLookupOptionalFields;
    }
    
    public void setCapitalAssetNumber(String capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }
    
    public String getCapitalAssetNumber() {
        return this.capitalAssetNumber;
    }

    @Override
    public SourceAccountingLine getNewSourceLine() {
        AssetPaymentDetail newSourceLine= (AssetPaymentDetail)super.getNewSourceLine();

        if (newSourceLine.getExpenditureFinancialDocumentTypeCode() == null || newSourceLine.getExpenditureFinancialDocumentTypeCode().trim().equals("")) {
            // TODO Add a system parameter DEFAULT_ASSET_PAYMENT_DOCUMENT_TYPE = "MPAY"
            newSourceLine.setExpenditureFinancialDocumentTypeCode("MPAY");
            newSourceLine.setExpenditureFinancialDocumentNumber(this.getDocument().getDocumentNumber());
        }
        return (SourceAccountingLine)newSourceLine;
    }
        
    /**
     * 
     * This method stores in a Map the total amounts that need to be display on the asset payment screen and that will be passed as 
     * parameter to the accountingLine.tag in order to display them.
     * 
     * @return LinkedHashMap
     */
    public LinkedHashMap<String,String> getAssetPaymentTotals() {        
        LinkedHashMap<String,String> totals = new LinkedHashMap<String,String>();
        CurrencyFormatter cf = new CurrencyFormatter();
        
        totals.put("Total", (String) cf.format(getFinancialDocument().getSourceTotal()));
        totals.put("Previous cost",(String) cf.format(getAssetPaymentDocument().getAsset().getTotalCostAmount()));        
        totals.put("New total", (String) cf.format(getFinancialDocument().getSourceTotal().add(getAssetPaymentDocument().getAsset().getTotalCostAmount())));        
        return totals;
    }    
} 