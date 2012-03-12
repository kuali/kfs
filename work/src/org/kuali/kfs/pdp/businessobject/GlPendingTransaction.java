/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.FlexibleAccountUpdateable;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * General Ledger Pending Table for PDP
 */
public class GlPendingTransaction extends PersistableBusinessObjectBase implements FlexibleAccountUpdateable {
    private KualiInteger id; // GL_PENDING_ENTRY_ID NUMBER 8 0
    private String fsOriginCd; // FS_ORIGIN_CD VARCHAR2 2
    private String fdocNbr; // FDOC_NBR VARCHAR2 9
    private KualiInteger sequenceNbr; // TRN_ENTR_SEQ_NBR NUMBER 5 0
    private String chartOfAccountsCode; // FIN_COA_CD VARCHAR2 2
    private String accountNumber; // ACCOUNT_NBR VARCHAR2 7
    private String subAccountNumber; // SUB_ACCOUNT_NBR VARCHAR2 5
    private String financialObjectCode; // FIN_OBJECT_CD VARCHAR2 4
    private String financialSubObjectCode; // FIN_SUB_OBJ_CD VARCHAR2 3
    private String financialBalanceTypeCode; // FIN_BALANCE_TYP_CD VARCHAR2 2
    private String finObjTypCd; // FIN_OBJ_TYP_CD VARCHAR2 2
    private Integer universityFiscalYear; // UNIV_FISCAL_YR NUMBER 4 0
    private String univFiscalPrdCd; // UNIV_FISCAL_PRD_CD VARCHAR2 2
    private String description; // TRN_LDGR_ENTR_DESC VARCHAR2 40
    private KualiDecimal amount; // TRN_LDGR_ENTR_AMT NUMBER 19 2
    private String debitCrdtCd; // TRN_DEBIT_CRDT_CD VARCHAR2 1
    private Date transactionDt; // TRANSACTION_DT DATE 7
    private String financialDocumentTypeCode; // FDOC_TYP_CD VARCHAR2 4
    private String orgDocNbr; // ORG_DOC_NBR VARCHAR2 10
    private String projectCd; // PROJECT_CD VARCHAR2 10
    private String orgReferenceId; // ORG_REFERENCE_ID VARCHAR2 8
    private String fdocRefTypCd; // FDOC_REF_TYP_CD VARCHAR2 4
    private String fsRefOriginCd; // FS_REF_ORIGIN_CD VARCHAR2 2
    private String fdocRefNbr; // FDOC_REF_NBR VARCHAR2 9
    private Date fdocReversalDt; // FDOC_REVERSAL_DT DATE 7
    private String trnEncumUpdtCd; // TRN_ENCUM_UPDT_CD VARCHAR2 1
    private String fdocApprovedCd; // FDOC_APPROVED_CD VARCHAR2 1
    private String acctSfFinObjCd; // ACCT_SF_FINOBJ_CD VARCHAR2 4
    private String trnEntrOfstCd; // TRN_ENTR_OFST_CD VARCHAR2 1
    private boolean processInd; // TRN_EXTRT_IND VARCHAR2 7

    public GlPendingTransaction() {
        super();
        
        processInd = false;
    }

    public OriginEntryFull getOriginEntry() {
        OriginEntryFull oe = new OriginEntryFull();

        oe.setFinancialSystemOriginationCode(fsOriginCd);
        oe.setDocumentNumber(fdocNbr);
        oe.setTransactionLedgerEntrySequenceNumber(sequenceNbr.intValue());
        oe.setChartOfAccountsCode(chartOfAccountsCode);
        oe.setAccountNumber(accountNumber);
        oe.setSubAccountNumber(subAccountNumber);
        oe.setFinancialObjectCode(financialObjectCode);
        oe.setFinancialSubObjectCode(financialSubObjectCode);
        oe.setFinancialBalanceTypeCode(financialBalanceTypeCode);
        oe.setFinancialObjectTypeCode(finObjTypCd);
        oe.setUniversityFiscalYear(universityFiscalYear);
        oe.setUniversityFiscalPeriodCode(univFiscalPrdCd);
        oe.setTransactionLedgerEntryDescription(description);
        if (amount != null) {
            oe.setTransactionLedgerEntryAmount(amount);
        }
        oe.setTransactionDebitCreditCode(debitCrdtCd);
        if (transactionDt != null) {
            oe.setTransactionDate(new Date(transactionDt.getTime()));
        }
        oe.setFinancialDocumentTypeCode(financialDocumentTypeCode);
        oe.setOrganizationDocumentNumber(orgDocNbr);
        oe.setProjectCode(projectCd);
        oe.setOrganizationReferenceId(orgReferenceId);
        oe.setReferenceFinancialDocumentTypeCode(fdocRefTypCd);
        oe.setReferenceFinancialSystemOriginationCode(fsRefOriginCd);
        oe.setReferenceFinancialDocumentNumber(fdocRefNbr);
        if (fdocReversalDt != null) {
            oe.setFinancialDocumentReversalDate(new Date(fdocReversalDt.getTime()));
        }
        oe.setTransactionEncumbranceUpdateCode(trnEncumUpdtCd);

        return oe;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNbr) {
        this.accountNumber = accountNbr;
    }

    public String getAcctSfFinObjCd() {
        return acctSfFinObjCd;
    }

    public void setAcctSfFinObjCd(String acctSfFinObjCd) {
        this.acctSfFinObjCd = acctSfFinObjCd;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public String getDebitCrdtCd() {
        return debitCrdtCd;
    }

    public void setDebitCrdtCd(String debitCrdtCd) {
        this.debitCrdtCd = debitCrdtCd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFdocApprovedCd() {
        return fdocApprovedCd;
    }

    public void setFdocApprovedCd(String fdocApprovedCd) {
        this.fdocApprovedCd = fdocApprovedCd;
    }

    public String getFdocNbr() {
        return fdocNbr;
    }

    public void setFdocNbr(String fdocNbr) {
        this.fdocNbr = fdocNbr;
    }

    public String getFdocRefNbr() {
        return fdocRefNbr;
    }

    public void setFdocRefNbr(String fdocRefNbr) {
        this.fdocRefNbr = fdocRefNbr;
    }

    public String getFdocRefTypCd() {
        return fdocRefTypCd;
    }

    public void setFdocRefTypCd(String fdocRefTypCd) {
        this.fdocRefTypCd = fdocRefTypCd;
    }

    public Date getFdocReversalDt() {
        return fdocReversalDt;
    }

    public void setFdocReversalDt(Date fdocReversalDt) {
        this.fdocReversalDt = fdocReversalDt;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String fdocTypCd) {
        this.financialDocumentTypeCode = fdocTypCd;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String finBalanceTypCd) {
        this.financialBalanceTypeCode = finBalanceTypCd;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String finCoaCd) {
        this.chartOfAccountsCode = finCoaCd;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String finObjectCd) {
        this.financialObjectCode = finObjectCd;
    }

    public String getFinObjTypCd() {
        return finObjTypCd;
    }

    public void setFinObjTypCd(String finObjTypCd) {
        this.finObjTypCd = finObjTypCd;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String finSubObjCd) {
        this.financialSubObjectCode = finSubObjCd;
    }

    public String getFsOriginCd() {
        return fsOriginCd;
    }

    public void setFsOriginCd(String fsOriginCd) {
        this.fsOriginCd = fsOriginCd;
    }

    public String getFsRefOriginCd() {
        return fsRefOriginCd;
    }

    public void setFsRefOriginCd(String fsRefOriginCd) {
        this.fsRefOriginCd = fsRefOriginCd;
    }

    public KualiInteger getId() {
        return id;
    }

    public void setId(KualiInteger id) {
        this.id = id;
    }

    public String getOrgDocNbr() {
        return orgDocNbr;
    }

    public void setOrgDocNbr(String orgDocNbr) {
        this.orgDocNbr = orgDocNbr;
    }

    public String getOrgReferenceId() {
        return orgReferenceId;
    }

    public void setOrgReferenceId(String orgReferenceId) {
        this.orgReferenceId = orgReferenceId;
    }

    /**
     * Gets the processInd attribute.
     * 
     * @return Returns the processInd.
     */
    public boolean isProcessInd() {
        return processInd;
    }

    /**
     * Sets the processInd attribute value.
     * 
     * @param processInd The processInd to set.
     */
    public void setProcessInd(boolean processInd) {
        this.processInd = processInd;
    }

    public String getProjectCd() {
        return projectCd;
    }

    public void setProjectCd(String projectCd) {
        this.projectCd = projectCd;
    }

    public KualiInteger getSequenceNbr() {
        return sequenceNbr;
    }

    public void setSequenceNbr(KualiInteger sequenceNbr) {
        this.sequenceNbr = sequenceNbr;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNbr) {
        this.subAccountNumber = subAccountNbr;
    }

    public Date getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(Date transactionDt) {
        this.transactionDt = transactionDt;
    }

    public String getTrnEncumUpdtCd() {
        return trnEncumUpdtCd;
    }

    public void setTrnEncumUpdtCd(String trnEncumUpdtCd) {
        this.trnEncumUpdtCd = trnEncumUpdtCd;
    }

    public String getTrnEntrOfstCd() {
        return trnEntrOfstCd;
    }

    public void setTrnEntrOfstCd(String trnEntrOfstCd) {
        this.trnEntrOfstCd = trnEntrOfstCd;
    }

    public String getUnivFiscalPrdCd() {
        return univFiscalPrdCd;
    }

    public void setUnivFiscalPrdCd(String univFiscalPrdCd) {
        this.univFiscalPrdCd = univFiscalPrdCd;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer univFiscalYr) {
        this.universityFiscalYear = univFiscalYr;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GlPendingTransaction)) {
            return false;
        }
        GlPendingTransaction o = (GlPendingTransaction) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 5).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", id).toString();
    }

    public void setAccount(Account a) { /* don't do nada; we're just fulfilling the contract of FlexibleAccountUpdateable */
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }
}
