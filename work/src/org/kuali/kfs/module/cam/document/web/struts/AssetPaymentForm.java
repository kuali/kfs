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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class AssetPaymentForm extends KualiAccountingDocumentFormBase {
    private static Log LOG = LogFactory.getLog(AssetPaymentForm.class);

    // Indicates which result set we are using when refreshing/returning from a multi-value lookup.
    private String lookupResultsSequenceNumber;

    // Type of result returned by the multi-value lookup. ?to be persisted in the lookup results service instead?
    private String lookupResultsBOClassName;

    // The name of the collection looked up (by a multiple value lookup)
    private String lookedUpCollectionName;

    String capitalAssetNumber = "";

    /**
     * Constructs a AssetPaymentForm.java.
     */
    public AssetPaymentForm() {
        super();
        setDocument(new AssetPaymentDocument());
    }

    /**
     * This method gets the asset payment document
     * 
     * @return AssetPaymentDocument
     */
    public AssetPaymentDocument getAssetPaymentDocument() {
        return (AssetPaymentDocument) getDocument();
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getForcedLookupOptionalFields()
     */
    @Override
    public Map<String, String> getForcedLookupOptionalFields() {
        Map<String, String> forcedLookupOptionalFields = super.getForcedLookupOptionalFields();
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE + ";" + DocumentType.class.getName());
        forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE + ";" + OriginationCode.class.getName());

        return forcedLookupOptionalFields;
    }

    /**
     * This method sets the asset# selected
     * 
     * @param capitalAssetNumber
     */
    public void setCapitalAssetNumber(String capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * gets the asset# that was previously selected
     * 
     * @return
     */
    public String getCapitalAssetNumber() {
        return this.capitalAssetNumber;
    }


    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getNewSourceLine()
     */
    @Override
    public SourceAccountingLine getNewSourceLine() {
        // Getting the workflow document number created for the asset payment document.
        String worflowDocumentNumber = "";
        try {
            if (GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()) != null)
                worflowDocumentNumber = GlobalVariables.getUserSession().getWorkflowDocument(this.getAssetPaymentDocument().getDocumentNumber()).getRouteHeaderId().toString();
        }
        catch (Exception e) {
            throw new RuntimeException("Error converting workflow document number to string:" + e.getMessage());
        }

        AssetPaymentDetail newSourceLine = (AssetPaymentDetail) super.getNewSourceLine();

        // Getting the document type code in order set it as default in the new source accounting line.
        String documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(AssetPaymentDocument.class);

        // Setting default document type.
        if (newSourceLine.getExpenditureFinancialDocumentTypeCode() == null || newSourceLine.getExpenditureFinancialDocumentTypeCode().trim().equals("")) {
            newSourceLine.setExpenditureFinancialDocumentTypeCode(documentTypeCode);
        }

        // Setting the default asset payment row document number.
        if (newSourceLine.getExpenditureFinancialDocumentNumber() == null || newSourceLine.getExpenditureFinancialDocumentNumber().trim().equals("")) {
            newSourceLine.setExpenditureFinancialDocumentNumber(worflowDocumentNumber);
        }

        // Setting the default asset payment row origination code.
        if (newSourceLine.getExpenditureFinancialSystemOriginationCode() == null || newSourceLine.getExpenditureFinancialSystemOriginationCode().trim().equals("")) {
            newSourceLine.setExpenditureFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
        }

        return (SourceAccountingLine) newSourceLine;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // Refreshing reference to the asset table.
        for (AssetPaymentAssetDetail assetPaymentAssetDetail : this.getAssetPaymentDocument().getAssetPaymentAssetDetail()) {
            assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDocument.ASSET);
        }
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.AccountingDocumentFormBase#getRefreshCaller()
     */
    @Override
    public String getRefreshCaller() {
        return KFSConstants.MULTIPLE_VALUE;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(KNSConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
    }
}