/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;

/**
 * Accounting line for the asset payment document.
 */
public class AssetPaymentDetail extends SourceAccountingLine {
    private static Logger LOG = Logger.getLogger(AssetPaymentDetail.class);

    private String expenditureFinancialSystemOriginationCode;
    private Date expenditureFinancialDocumentPostedDate;
    private boolean transferPaymentIndicator;

    private String expenditureFinancialDocumentNumber;
    private String expenditureFinancialDocumentTypeCode;
    private String postingPeriodCode;
    private String purchaseOrderNumber;
    private String requisitionNumber;
    private KualiDecimal amount;

    // bo references
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentTypeEBO expenditureFinancialSystemDocumentTypeCode;
    private OriginationCode expenditureFinancialSystemOrigination;
    private Account account;



    /**
     * Default constructor.
     */
    public AssetPaymentDetail() {
        super();
    }

    public AssetPaymentDetail(AssetPayment assetPayment) {
        // populate AssetPaymentDetail with AssetPayment data
        this.setSequenceNumber(assetPayment.getPaymentSequenceNumber());
        this.setChartOfAccountsCode(assetPayment.getChartOfAccountsCode());
        this.setAccountNumber(assetPayment.getAccountNumber());
        this.setSubAccountNumber(assetPayment.getSubAccountNumber());
        this.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        this.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        this.setProjectCode(assetPayment.getProjectCode());
        this.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
        this.setExpenditureFinancialSystemOriginationCode(assetPayment.getFinancialSystemOriginationCode());
        this.setExpenditureFinancialDocumentNumber(assetPayment.getDocumentNumber());
        this.setExpenditureFinancialDocumentTypeCode(assetPayment.getFinancialDocumentTypeCode());
        this.setPurchaseOrderNumber(assetPayment.getPurchaseOrderNumber());
        this.setRequisitionNumber(assetPayment.getRequisitionNumber());
        this.setExpenditureFinancialDocumentPostedDate(assetPayment.getFinancialDocumentPostingDate());
        this.setPostingYear(assetPayment.getFinancialDocumentPostingYear());
        this.setPostingPeriodCode(assetPayment.getFinancialDocumentPostingPeriodCode());
        this.setAmount(assetPayment.getAccountChargeAmount());
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String,String> assetPaymentToStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("documentNumber", this.getDocumentNumber());
        m.put("sequenceNumber", this.getSequenceNumber() == null ? "" : this.getSequenceNumber().toString());
        m.put("chartOfAccountsCode",getChartOfAccountsCode());
        m.put("accountNumber",getAccountNumber());
        m.put("subAccountNumber",getSubAccountNumber());
        m.put("financialObjectCode",getFinancialObjectCode());
        m.put("financialSubObjectCode",getFinancialSubObjectCode());
        m.put("projectCode",getProjectCode());
        m.put("postingYear",this.getPostingYear().toString());
        return m;
    }

    /**
     * Create a key including the
     * <li><b>expenditureFinancialDocumentNumber</b></li>
     * <li><b>expenditureFinancialDocumentTypeCode</b></li>
     * with accounting information for asset payment distribution
     *
     * Make sure the full accounting line information is part of the key
     * chartOfAccount, accountNumber, subAccountNumber, objectCode, subObjectCode, projectCode
     *
     * @return
     */
    public String getAssetPaymentDetailKey() {
        LinkedHashMap<String,String> paymentMap = assetPaymentToStringMapper();
        paymentMap.put("expenditureFinancialDocumentTypeCode",this.getExpenditureFinancialDocumentTypeCode());
        paymentMap.put("expenditureFinancialDocumentNumber",this.getExpenditureFinancialDocumentNumber());

        //use SHORT_PREFIX_STYLE so that memory address is not part of the toString output
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        for (String key : paymentMap.keySet()){
            builder.append(key, paymentMap.get(key));
        }
        return paymentMap.toString();
    }

    public String getExpenditureFinancialSystemOriginationCode() {
        return expenditureFinancialSystemOriginationCode;
    }


    public void setExpenditureFinancialSystemOriginationCode(String expenditureFinancialSystemOriginationCode) {
        this.expenditureFinancialSystemOriginationCode = expenditureFinancialSystemOriginationCode;
    }


    public Date getExpenditureFinancialDocumentPostedDate() {
        return expenditureFinancialDocumentPostedDate;
    }


    public void setExpenditureFinancialDocumentPostedDate(Date expenditureFinancialDocumentPostedDate) {
        this.expenditureFinancialDocumentPostedDate = expenditureFinancialDocumentPostedDate;
    }


    public boolean isTransferPaymentIndicator() {
        return transferPaymentIndicator;
    }


    public void setTransferPaymentIndicator(boolean transferPaymentIndicator) {
        this.transferPaymentIndicator = transferPaymentIndicator;
    }


    public String getExpenditureFinancialDocumentNumber() {
        return expenditureFinancialDocumentNumber;
    }


    public void setExpenditureFinancialDocumentNumber(String expenditureFinancialDocumentNumber) {
        this.expenditureFinancialDocumentNumber = expenditureFinancialDocumentNumber;
    }


    public String getExpenditureFinancialDocumentTypeCode() {
        return expenditureFinancialDocumentTypeCode;
    }


    public void setExpenditureFinancialDocumentTypeCode(String expenditureFinancialDocumentTypeCode) {
        this.expenditureFinancialDocumentTypeCode = expenditureFinancialDocumentTypeCode;
    }


    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }


    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }


    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    public String getRequisitionNumber() {
        return requisitionNumber;
    }


    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }


    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }


    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }

    public DocumentTypeEBO getExpenditureFinancialSystemDocumentTypeCode() {
        if ( expenditureFinancialSystemDocumentTypeCode == null || !StringUtils.equals(expenditureFinancialSystemDocumentTypeCode.getName(), expenditureFinancialDocumentTypeCode) ) {
            expenditureFinancialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(expenditureFinancialDocumentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(expenditureFinancialDocumentTypeCode);
                if ( docType != null ) {
                    expenditureFinancialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return expenditureFinancialSystemDocumentTypeCode;
    }

    public OriginationCode getExpenditureFinancialSystemOrigination() {
        return expenditureFinancialSystemOrigination;
    }


    public void setExpenditureFinancialSystemOrigination(OriginationCode expenditureFinancialSystemOrigination) {
        this.expenditureFinancialSystemOrigination = expenditureFinancialSystemOrigination;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     *
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public KualiDecimal getAmount() {
        return amount;
    }


    @Override
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     *
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map simpleValues = super.getValuesMap();
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, getExpenditureFinancialSystemOriginationCode());
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE,getExpenditureFinancialDocumentPostedDate());

        return simpleValues;
    }
}
