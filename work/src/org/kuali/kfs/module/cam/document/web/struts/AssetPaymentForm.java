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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.module.labor.bo.LaborAccountingLineOverride;
import org.kuali.module.labor.bo.PositionData;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

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
        // TODO ADD this field name in the CAMSPropertu
        
        String lookupField = RicePropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE; // "financialDocumentTypeCode";
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE,"financialDocumentTypeCode" + "," + DocumentType.class.getName());
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

        // TODO Add a system parameter DEFAULT_ASSET_PAYMENT_DOCUMENT_TYPE = "MPAY"
        newSourceLine.setExpenditureFinancialDocumentTypeCode("MPAY");
        
        return (SourceAccountingLine)newSourceLine;
    }
} 