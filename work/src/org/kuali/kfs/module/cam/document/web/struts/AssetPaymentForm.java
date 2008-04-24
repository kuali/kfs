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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetDepreciationDocument;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.kfs.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

public class AssetPaymentForm extends KualiAccountingDocumentFormBase {
    private static Log LOG = LogFactory.getLog(AssetPaymentForm.class);

    String capitalAssetNumber="";

    public AssetPaymentForm() {
        super();
        setDocument(new AssetPaymentDocument());        
        LOG.info("***AssetPaymentForm - Constructor()");
    }   

    public AssetPaymentDocument getAssetPaymentDocument() {
        return (AssetPaymentDocument) getDocument();
    }

    
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
/*        if (hasDocumentId()) {
            // populate workflowDocument in documentHeader, if needed
            try {
                KualiWorkflowDocument workflowDocument = null;
                if (GlobalVariables.getUserSession().getWorkflowDocument(getDocument().getDocumentNumber()) != null) {
                    workflowDocument = GlobalVariables.getUserSession().getWorkflowDocument(getDocument().getDocumentNumber());
                }
                else {
                    // gets the workflow document from doc service, doc service will also set the workflow document in the user's session
                    Document retrievedDocument = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(getDocument().getDocumentNumber());
                    if (retrievedDocument == null) {
                        throw new WorkflowException("Unable to get retrieve document # " + getDocument().getDocumentNumber() + " from document service getByDocumentHeaderId");
                }
                    workflowDocument = retrievedDocument.getDocumentHeader().getWorkflowDocument();
            }

                getDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
            }
            catch (WorkflowException e) {
                LOG.warn("Error while instantiating workflowDoc", e);
                throw new RuntimeException("error populating documentHeader.workflowDocument", e);
            }
        }*/
    }

    
    /*    @Override
    public Map getForcedReadOnlySourceFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, Boolean.TRUE);
        return map;
    }*/


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
        String worflowDocumentNumber="";
        try {
            if (GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()) != null) { 
                worflowDocumentNumber=GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()).getRouteHeaderId().toString();
                
            if (this.getDocument().getDocumentHeader().getWorkflowDocument().getRouteHeaderId() != null ) {
                LOG.info("ZZZZZZZZZZZ:"+this.getDocument().getDocumentHeader().getWorkflowDocument().getRouteHeaderId().toString());
            }
                
        }
        } catch(Exception e) {
            throw new RuntimeException("Error converting workflow document number to string:"+e.getMessage());
        }

        AssetPaymentDetail newSourceLine= (AssetPaymentDetail)super.getNewSourceLine();

        String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(AssetPaymentDocument.class);

        if (newSourceLine.getExpenditureFinancialDocumentTypeCode() == null || newSourceLine.getExpenditureFinancialDocumentTypeCode().trim().equals("")) {
            newSourceLine.setExpenditureFinancialDocumentTypeCode(documentTypeCode);
        }
        newSourceLine.setExpenditureFinancialDocumentNumber(worflowDocumentNumber);
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