/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAllocationType;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

public class AssetPaymentForm extends KualiAccountingDocumentFormBase {
	protected static Log LOG = LogFactory.getLog(AssetPaymentForm.class);

	// Indicates which result set we are using when refreshing/returning from a
	// multi-value lookup.
	protected String lookupResultsSequenceNumber;

	// Type of result returned by the multi-value lookup. ?to be persisted in
	// the lookup results service instead?
	protected String lookupResultsBOClassName;

	// The name of the collection looked up (by a multiple value lookup)
	protected String lookedUpCollectionName;

	String capitalAssetNumber = "";

	/**
	 * Constructs a AssetPaymentForm.java.
	 */
	public AssetPaymentForm() {
		super();
		getAssetPaymentDocument().setAssetPaymentAllocationTypeCode("2");
	}

	@Override
	protected String getDefaultDocumentTypeName() {
		return "MPAY";
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
		forcedLookupOptionalFields.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE + ";" + DocumentTypeEBO.class.getName());
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
	    SessionDocumentService sessionService = SpringContext.getBean(SessionDocumentService.class);
	    WorkflowDocument document = sessionService.getDocumentFromSession(GlobalVariables.getUserSession(), this.getAssetPaymentDocument().getDocumentNumber());

	    String worflowDocumentNumber = (document!= null)? document.getDocumentId() : "";
		AssetPaymentDetail newSourceLine = (AssetPaymentDetail) super.getNewSourceLine();

		// Setting default document type.
		if (newSourceLine.getExpenditureFinancialDocumentTypeCode() == null || newSourceLine.getExpenditureFinancialDocumentTypeCode().trim().equals("")) {
			newSourceLine.setExpenditureFinancialDocumentTypeCode(KFSConstants.FinancialDocumentTypeCodes.ASSET_PAYMENT);
		}

		// Setting the default asset payment row document number.
		if (newSourceLine.getExpenditureFinancialDocumentNumber() == null || newSourceLine.getExpenditureFinancialDocumentNumber().trim().equals("")) {
			newSourceLine.setExpenditureFinancialDocumentNumber(worflowDocumentNumber);
		}

		// Setting the default asset payment row origination code.
		if (newSourceLine.getExpenditureFinancialSystemOriginationCode() == null || newSourceLine.getExpenditureFinancialSystemOriginationCode().trim().equals("")) {
			newSourceLine.setExpenditureFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
		}

		return newSourceLine;
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
		registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
	}

	/**
	 * Get the distribution (allocation) code in the
	 * {@link AssetPaymentDocument}
	 */
	public String getAllocationCode() {
		return getAssetPaymentDocument().getAssetPaymentAllocationTypeCode();
	}

	/**
	 * Set the distribution (allocation) code in the
	 * {@link AssetPaymentDocument}
	 */
	public void setAllocationCode(String distributionCode) {
		getAssetPaymentDocument().setAssetPaymentAllocationTypeCode(distributionCode);
	}

	/**
	 * Get the allocation label that appears on the asset table
	 */
	public String getAllocationLabel() {
		AssetPaymentAllocationType assetPaymentAllocationType = getAssetPaymentDocument().getAssetPaymentAllocationType();
		return assetPaymentAllocationType==null ? "" : assetPaymentAllocationType.getAllocationColumnName();
	}

	/**
	 * Returns whether the user can edit allocations.
	 */
	public boolean isAllocationEditable() {
	    boolean allocationEditable = false;
	    AssetPaymentAllocationType assetPaymentAllocationType = getAssetPaymentDocument().getAssetPaymentAllocationType();
	    if (!this.getAssetPaymentDocument().isAllocationFromFPDocuments()) {
	        allocationEditable = assetPaymentAllocationType==null ? false : assetPaymentAllocationType.isAllocationEditable();
	    }
		return allocationEditable;
	}

	public boolean isAllocationEditablePct() {
	    AssetPaymentAllocationType assetPaymentAllocationType = getAssetPaymentDocument().getAssetPaymentAllocationType();
	    if (assetPaymentAllocationType==null) {
	        return false;
	    } else {
	        return isAllocationEditable() & (assetPaymentAllocationType.getAllocationCode().equals(CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_BY_PERCENTAGE_CODE));
	    }
	}

	/**
	 * Returns the number of source lines in the document
	 */
	public int getSourceLineCount() {
		return getAssetPaymentDocument().getAssetPaymentAssetDetail().size();
	}
}
