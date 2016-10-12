package edu.arizona.kfs.module.purap.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class is essentially a copy of the GeneralLedgerPendingEntry BO.
 * 
 * The purpose of this class is to archive GLPE's that were created for use tax on a PREQ (and maybe other AP docs in the future).
 * 
 * When a PREQ's accounting info is changed on a accounting line/item during PREQ routing (e.g. changed by the fiscal officer),
 * the pre-existing use tax GLPE's need to be reversed (because PREQ entries can hit the ledger before the doc is final).  Use tax
 * will then be completely regenerated from the PREQ.
 * 
 * However, the PREQ doesn't contain a pre-change historical information to allow for reversal of the use tax GLPE's (because it 
 * doesn't have a snapshot of the lines/items from before the change).  Therefore, to reverse the use tax GLPE's, GLPE's will be
 * stored in this BO.
 */
public class AccountsPayableUseTaxArchivedEntry extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 4041748389323105932L;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String financialDocumentTypeCode;
    private String organizationDocumentNumber;
    private String projectCode;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
    private String acctSufficientFundsFinObjCd;
    private boolean transactionEntryOffsetIndicator;
    
    public AccountsPayableUseTaxArchivedEntry() {
    }

    public AccountsPayableUseTaxArchivedEntry(GeneralLedgerPendingEntry original) {
        financialSystemOriginationCode = original.getFinancialSystemOriginationCode();
        documentNumber = original.getDocumentNumber();
        transactionLedgerEntrySequenceNumber = original.getTransactionLedgerEntrySequenceNumber();
        chartOfAccountsCode = original.getChartOfAccountsCode();
        accountNumber = original.getAccountNumber();
        subAccountNumber = original.getSubAccountNumber();
        financialObjectCode = original.getFinancialObjectCode();
        financialSubObjectCode = original.getFinancialSubObjectCode();
        financialBalanceTypeCode = original.getFinancialBalanceTypeCode();
        financialObjectTypeCode = original.getFinancialObjectTypeCode();
        universityFiscalYear = original.getUniversityFiscalYear();
        universityFiscalPeriodCode = original.getUniversityFiscalPeriodCode();
        transactionLedgerEntryDescription = original.getTransactionLedgerEntryDescription();
        transactionLedgerEntryAmount = original.getTransactionLedgerEntryAmount();
        transactionDebitCreditCode = original.getTransactionDebitCreditCode();
        transactionDate = original.getTransactionDate();
        financialDocumentTypeCode = original.getFinancialDocumentTypeCode();
        organizationDocumentNumber = original.getOrganizationDocumentNumber();
        projectCode = original.getProjectCode();
        organizationReferenceId = original.getOrganizationReferenceId();
        referenceFinancialDocumentTypeCode = original.getReferenceFinancialDocumentTypeCode();
        referenceFinancialSystemOriginationCode = original.getReferenceFinancialSystemOriginationCode();
        referenceFinancialDocumentNumber = original.getReferenceFinancialDocumentNumber();
        financialDocumentReversalDate = original.getFinancialDocumentReversalDate();
        transactionEncumbranceUpdateCode = original.getTransactionEncumbranceUpdateCode();
        acctSufficientFundsFinObjCd = original.getAcctSufficientFundsFinObjCd();
        transactionEntryOffsetIndicator = original.isTransactionEntryOffsetIndicator();
    }
    
    public GeneralLedgerPendingEntry toGeneralLedgerPendingEntry() {
        GeneralLedgerPendingEntry glpe = new GeneralLedgerPendingEntry();
        glpe.setFinancialSystemOriginationCode(this.getFinancialSystemOriginationCode());
        glpe.setDocumentNumber(this.getDocumentNumber());
        glpe.setTransactionLedgerEntrySequenceNumber(this.getTransactionLedgerEntrySequenceNumber());
        glpe.setChartOfAccountsCode(this.getChartOfAccountsCode());
        glpe.setAccountNumber(this.getAccountNumber());
        glpe.setSubAccountNumber(this.getSubAccountNumber());
        glpe.setFinancialObjectCode(this.getFinancialObjectCode());
        glpe.setFinancialSubObjectCode(this.getFinancialSubObjectCode());
        glpe.setFinancialBalanceTypeCode(this.getFinancialBalanceTypeCode());
        glpe.setFinancialObjectTypeCode(this.getFinancialObjectTypeCode());
        glpe.setUniversityFiscalYear(this.getUniversityFiscalYear());
        glpe.setUniversityFiscalPeriodCode(this.getUniversityFiscalPeriodCode());
        glpe.setTransactionLedgerEntryDescription(this.getTransactionLedgerEntryDescription());
        glpe.setTransactionLedgerEntryAmount(this.getTransactionLedgerEntryAmount());
        glpe.setTransactionDebitCreditCode(this.getTransactionDebitCreditCode());
        glpe.setTransactionDate(this.getTransactionDate());
        glpe.setFinancialDocumentTypeCode(this.getFinancialDocumentTypeCode());
        glpe.setOrganizationDocumentNumber(this.getOrganizationDocumentNumber());
        glpe.setProjectCode(this.getProjectCode());
        glpe.setOrganizationReferenceId(this.getOrganizationReferenceId());
        glpe.setReferenceFinancialDocumentTypeCode(this.getReferenceFinancialDocumentTypeCode());
        glpe.setReferenceFinancialSystemOriginationCode(this.getReferenceFinancialSystemOriginationCode());
        glpe.setReferenceFinancialDocumentNumber(this.getReferenceFinancialDocumentNumber());
        glpe.setFinancialDocumentReversalDate(this.getFinancialDocumentReversalDate());
        glpe.setTransactionEncumbranceUpdateCode(this.getTransactionEncumbranceUpdateCode());
        glpe.setAcctSufficientFundsFinObjCd(this.getAcctSufficientFundsFinObjCd());
        glpe.setTransactionEntryOffsetIndicator(this.isTransactionEntryOffsetIndicator());
        // don't set financialDocumentApprovedCode and transactionEntryProcessedTs
        return glpe;
    }
    
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    public String getAcctSufficientFundsFinObjCd() {
        return acctSufficientFundsFinObjCd;
    }

    public void setAcctSufficientFundsFinObjCd(String acctSufficientFundsFinObjCd) {
        this.acctSufficientFundsFinObjCd = acctSufficientFundsFinObjCd;
    }

    public boolean isTransactionEntryOffsetIndicator() {
        return transactionEntryOffsetIndicator;
    }

    public void setTransactionEntryOffsetIndicator(boolean transactionEntryOffsetIndicator) {
        this.transactionEntryOffsetIndicator = transactionEntryOffsetIndicator;
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (transactionLedgerEntrySequenceNumber == null) {
            m.put("transactionLedgerEntrySequenceNumber", null);
        }
        else {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
        return m;
    }
}
