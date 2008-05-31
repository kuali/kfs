package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetPayment extends PersistableBusinessObjectBase {

    private Long capitalAssetNumber;
    private Integer paymentSequenceNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSystemOriginationCode;
    private String financialDocumentTypeCode;
    private String documentNumber;
    private Integer financialDocumentPostingYear;
    private String financialDocumentPostingPeriodCode;
    private Date financialDocumentPostingDate;
    private String projectCode;
    private String organizationReferenceId;
    private KualiDecimal accountChargeAmount;
    private String purchaseOrderNumber;
    private String requisitionNumber;
    private KualiDecimal primaryDepreciationBaseAmount;
    private KualiDecimal secondaryDepreciationBaseAmount;
    private KualiDecimal accumulatedPrimaryDepreciationAmount;
    private KualiDecimal accumulatedSecondaryDepreciationAmount;
    private KualiDecimal previousYearPrimaryDepreciationAmount;
    private KualiDecimal previousYearSecondaryDepreciationAmount;
    private KualiDecimal period1Depreciation1Amount;
    private KualiDecimal period1Depreciation2Amount;
    private KualiDecimal period2Depreciation1Amount;
    private KualiDecimal period2Depreciation2Amount;
    private KualiDecimal period3Depreciation1Amount;
    private KualiDecimal period3Depreciation2Amount;
    private KualiDecimal period4Depreciation1Amount;
    private KualiDecimal period4Depreciation2Amount;
    private KualiDecimal period5Depreciation1Amount;
    private KualiDecimal period5Depreciation2Amount;
    private KualiDecimal period6Depreciation1Amount;
    private KualiDecimal period6Depreciation2Amount;
    private KualiDecimal period7Depreciation1Amount;
    private KualiDecimal period7Depreciation2Amount;
    private KualiDecimal period8Depreciation1Amount;
    private KualiDecimal period8Depreciation2Amount;
    private KualiDecimal period9Depreciation1Amount;
    private KualiDecimal period9Depreciation2Amount;
    private KualiDecimal period10Depreciation1Amount;
    private KualiDecimal period10Depreciation2Amount;
    private KualiDecimal period11Depreciation1Amount;
    private KualiDecimal period11Depreciation2Amount;
    private KualiDecimal period12Depreciation1Amount;
    private KualiDecimal period12Depreciation2Amount;
    private String transferPaymentCode;

    private Asset asset;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private Account account;
    private SubObjCd financialSubObject;
    private ProjectCode project;
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentType documentType;
    private DocumentHeader documentHeader;
    private OriginationCode financialSystemOrigination;
    private Options option;

    // Non-persisted attributes:
    private KualiDecimal yearToDate;

    /**
     * Default constructor.
     */
    public AssetPayment() {

    }

    public AssetPayment(AssetPaymentDetail assetPaymentDetail) {
        setChartOfAccountsCode(assetPaymentDetail.getChartOfAccountsCode());
        setAccountNumber(assetPaymentDetail.getAccountNumber());
        setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        setFinancialObjectCode(assetPaymentDetail.getFinancialObjectCode());
        setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        setFinancialSystemOriginationCode(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode());
        setFinancialDocumentTypeCode(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode());
        setFinancialDocumentPostingYear(assetPaymentDetail.getFinancialDocumentPostingYear());
        setFinancialDocumentPostingPeriodCode(assetPaymentDetail.getFinancialDocumentPostingPeriodCode());
        setFinancialDocumentPostingDate(assetPaymentDetail.getPaymentApplicationDate());
        setProjectCode(assetPaymentDetail.getProjectCode());
        setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());
        setAccountChargeAmount(assetPaymentDetail.getAmount());
        setPurchaseOrderNumber(assetPaymentDetail.getPurchaseOrderNumber());
        setRequisitionNumber(assetPaymentDetail.getReferenceNumber());
    }
    
    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     * 
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     * 
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }


    /**
     * Gets the paymentSequenceNumber attribute.
     * 
     * @return Returns the paymentSequenceNumber
     * 
     */
    public Integer getPaymentSequenceNumber() {
        return paymentSequenceNumber;
    }

    /**
     * Sets the paymentSequenceNumber attribute.
     * 
     * @param paymentSequenceNumber The paymentSequenceNumber to set.
     * 
     */
    public void setPaymentSequenceNumber(Integer paymentSequenceNumber) {
        this.paymentSequenceNumber = paymentSequenceNumber;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     * 
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     * 
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     * 
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     * 
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     * 
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     * 
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     * 
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     * 
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode
     * 
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     * 
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }


    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear
     * 
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     * 
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }


    /**
     * Gets the financialDocumentPostingPeriodCode attribute.
     * 
     * @return Returns the financialDocumentPostingPeriodCode
     * 
     */
    public String getFinancialDocumentPostingPeriodCode() {
        return financialDocumentPostingPeriodCode;
    }

    /**
     * Sets the financialDocumentPostingPeriodCode attribute.
     * 
     * @param financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
     * 
     */
    public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
        this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
    }


    /**
     * Gets the financialDocumentPostingDate attribute.
     * 
     * @return Returns the financialDocumentPostingDate
     * 
     */
    public Date getFinancialDocumentPostingDate() {
        return financialDocumentPostingDate;
    }

    /**
     * Sets the financialDocumentPostingDate attribute.
     * 
     * @param financialDocumentPostingDate The financialDocumentPostingDate to set.
     * 
     */
    public void setFinancialDocumentPostingDate(Date financialDocumentPostingDate) {
        this.financialDocumentPostingDate = financialDocumentPostingDate;
    }


    /**
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode
     * 
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute.
     * 
     * @param projectCode The projectCode to set.
     * 
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    /**
     * Gets the organizationReferenceId attribute.
     * 
     * @return Returns the organizationReferenceId
     * 
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute.
     * 
     * @param organizationReferenceId The organizationReferenceId to set.
     * 
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }


    /**
     * Gets the accountChargeAmount attribute.
     * 
     * @return Returns the accountChargeAmount
     * 
     */
    public KualiDecimal getAccountChargeAmount() {
        return accountChargeAmount;
    }

    /**
     * Sets the accountChargeAmount attribute.
     * 
     * @param accountChargeAmount The accountChargeAmount to set.
     * 
     */
    public void setAccountChargeAmount(KualiDecimal accountChargeAmount) {
        this.accountChargeAmount = accountChargeAmount;
    }


    /**
     * Gets the purchaseOrderNumber attribute.
     * 
     * @return Returns the purchaseOrderNumber
     * 
     */
    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    /**
     * Sets the purchaseOrderNumber attribute.
     * 
     * @param purchaseOrderNumber The purchaseOrderNumber to set.
     * 
     */
    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    /**
     * Gets the requisitionNumber attribute.
     * 
     * @return Returns the requisitionNumber
     * 
     */
    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    /**
     * Sets the requisitionNumber attribute.
     * 
     * @param requisitionNumber The requisitionNumber to set.
     * 
     */
    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }


    /**
     * Gets the primaryDepreciationBaseAmount attribute.
     * 
     * @return Returns the primaryDepreciationBaseAmount
     * 
     */
    public KualiDecimal getPrimaryDepreciationBaseAmount() {
        return primaryDepreciationBaseAmount;
    }

    /**
     * Sets the primaryDepreciationBaseAmount attribute.
     * 
     * @param primaryDepreciationBaseAmount The primaryDepreciationBaseAmount to set.
     * 
     */
    public void setPrimaryDepreciationBaseAmount(KualiDecimal primaryDepreciationBaseAmount) {
        this.primaryDepreciationBaseAmount = primaryDepreciationBaseAmount;
    }


    /**
     * Gets the secondaryDepreciationBaseAmount attribute.
     * 
     * @return Returns the secondaryDepreciationBaseAmount
     * 
     */
    public KualiDecimal getSecondaryDepreciationBaseAmount() {
        return secondaryDepreciationBaseAmount;
    }

    /**
     * Sets the secondaryDepreciationBaseAmount attribute.
     * 
     * @param secondaryDepreciationBaseAmount The secondaryDepreciationBaseAmount to set.
     * 
     */
    public void setSecondaryDepreciationBaseAmount(KualiDecimal secondaryDepreciationBaseAmount) {
        this.secondaryDepreciationBaseAmount = secondaryDepreciationBaseAmount;
    }


    /**
     * Gets the accumulatedPrimaryDepreciationAmount attribute.
     * 
     * @return Returns the accumulatedPrimaryDepreciationAmount
     * 
     */
    public KualiDecimal getAccumulatedPrimaryDepreciationAmount() {
        return accumulatedPrimaryDepreciationAmount;
    }

    /**
     * Sets the accumulatedPrimaryDepreciationAmount attribute.
     * 
     * @param accumulatedPrimaryDepreciationAmount The accumulatedPrimaryDepreciationAmount to set.
     * 
     */
    public void setAccumulatedPrimaryDepreciationAmount(KualiDecimal accumulatedPrimaryDepreciationAmount) {
        this.accumulatedPrimaryDepreciationAmount = accumulatedPrimaryDepreciationAmount;
    }


    /**
     * Gets the accumulatedSecondaryDepreciationAmount attribute.
     * 
     * @return Returns the accumulatedSecondaryDepreciationAmount
     * 
     */
    public KualiDecimal getAccumulatedSecondaryDepreciationAmount() {
        return accumulatedSecondaryDepreciationAmount;
    }

    /**
     * Sets the accumulatedSecondaryDepreciationAmount attribute.
     * 
     * @param accumulatedSecondaryDepreciationAmount The accumulatedSecondaryDepreciationAmount to set.
     * 
     */
    public void setAccumulatedSecondaryDepreciationAmount(KualiDecimal accumulatedSecondaryDepreciationAmount) {
        this.accumulatedSecondaryDepreciationAmount = accumulatedSecondaryDepreciationAmount;
    }


    /**
     * Gets the previousYearPrimaryDepreciationAmount attribute.
     * 
     * @return Returns the previousYearPrimaryDepreciationAmount
     * 
     */
    public KualiDecimal getPreviousYearPrimaryDepreciationAmount() {
        return previousYearPrimaryDepreciationAmount;
    }

    /**
     * Sets the previousYearPrimaryDepreciationAmount attribute.
     * 
     * @param previousYearPrimaryDepreciationAmount The previousYearPrimaryDepreciationAmount to set.
     * 
     */
    public void setPreviousYearPrimaryDepreciationAmount(KualiDecimal previousYearPrimaryDepreciationAmount) {
        this.previousYearPrimaryDepreciationAmount = previousYearPrimaryDepreciationAmount;
    }


    /**
     * Gets the previousYearSecondaryDepreciationAmount attribute.
     * 
     * @return Returns the previousYearSecondaryDepreciationAmount
     * 
     */
    public KualiDecimal getPreviousYearSecondaryDepreciationAmount() {
        return previousYearSecondaryDepreciationAmount;
    }

    /**
     * Sets the previousYearSecondaryDepreciationAmount attribute.
     * 
     * @param previousYearSecondaryDepreciationAmount The previousYearSecondaryDepreciationAmount to set.
     * 
     */
    public void setPreviousYearSecondaryDepreciationAmount(KualiDecimal previousYearSecondaryDepreciationAmount) {
        this.previousYearSecondaryDepreciationAmount = previousYearSecondaryDepreciationAmount;
    }


    /**
     * Gets the period1Depreciation1Amount attribute.
     * 
     * @return Returns the period1Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod1Depreciation1Amount() {
        return period1Depreciation1Amount;
    }

    /**
     * Sets the period1Depreciation1Amount attribute.
     * 
     * @param period1Depreciation1Amount The period1Depreciation1Amount to set.
     * 
     */
    public void setPeriod1Depreciation1Amount(KualiDecimal period1Depreciation1Amount) {
        this.period1Depreciation1Amount = period1Depreciation1Amount;
    }


    /**
     * Gets the period1Depreciation2Amount attribute.
     * 
     * @return Returns the period1Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod1Depreciation2Amount() {
        return period1Depreciation2Amount;
    }

    /**
     * Sets the period1Depreciation2Amount attribute.
     * 
     * @param period1Depreciation2Amount The period1Depreciation2Amount to set.
     * 
     */
    public void setPeriod1Depreciation2Amount(KualiDecimal period1Depreciation2Amount) {
        this.period1Depreciation2Amount = period1Depreciation2Amount;
    }


    /**
     * Gets the period2Depreciation1Amount attribute.
     * 
     * @return Returns the period2Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod2Depreciation1Amount() {
        return period2Depreciation1Amount;
    }

    /**
     * Sets the period2Depreciation1Amount attribute.
     * 
     * @param period2Depreciation1Amount The period2Depreciation1Amount to set.
     * 
     */
    public void setPeriod2Depreciation1Amount(KualiDecimal period2Depreciation1Amount) {
        this.period2Depreciation1Amount = period2Depreciation1Amount;
    }


    /**
     * Gets the period2Depreciation2Amount attribute.
     * 
     * @return Returns the period2Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod2Depreciation2Amount() {
        return period2Depreciation2Amount;
    }

    /**
     * Sets the period2Depreciation2Amount attribute.
     * 
     * @param period2Depreciation2Amount The period2Depreciation2Amount to set.
     * 
     */
    public void setPeriod2Depreciation2Amount(KualiDecimal period2Depreciation2Amount) {
        this.period2Depreciation2Amount = period2Depreciation2Amount;
    }


    /**
     * Gets the period3Depreciation1Amount attribute.
     * 
     * @return Returns the period3Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod3Depreciation1Amount() {
        return period3Depreciation1Amount;
    }

    /**
     * Sets the period3Depreciation1Amount attribute.
     * 
     * @param period3Depreciation1Amount The period3Depreciation1Amount to set.
     * 
     */
    public void setPeriod3Depreciation1Amount(KualiDecimal period3Depreciation1Amount) {
        this.period3Depreciation1Amount = period3Depreciation1Amount;
    }


    /**
     * Gets the period3Depreciation2Amount attribute.
     * 
     * @return Returns the period3Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod3Depreciation2Amount() {
        return period3Depreciation2Amount;
    }

    /**
     * Sets the period3Depreciation2Amount attribute.
     * 
     * @param period3Depreciation2Amount The period3Depreciation2Amount to set.
     * 
     */
    public void setPeriod3Depreciation2Amount(KualiDecimal period3Depreciation2Amount) {
        this.period3Depreciation2Amount = period3Depreciation2Amount;
    }


    /**
     * Gets the period4Depreciation1Amount attribute.
     * 
     * @return Returns the period4Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod4Depreciation1Amount() {
        return period4Depreciation1Amount;
    }

    /**
     * Sets the period4Depreciation1Amount attribute.
     * 
     * @param period4Depreciation1Amount The period4Depreciation1Amount to set.
     * 
     */
    public void setPeriod4Depreciation1Amount(KualiDecimal period4Depreciation1Amount) {
        this.period4Depreciation1Amount = period4Depreciation1Amount;
    }


    /**
     * Gets the period4Depreciation2Amount attribute.
     * 
     * @return Returns the period4Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod4Depreciation2Amount() {
        return period4Depreciation2Amount;
    }

    /**
     * Sets the period4Depreciation2Amount attribute.
     * 
     * @param period4Depreciation2Amount The period4Depreciation2Amount to set.
     * 
     */
    public void setPeriod4Depreciation2Amount(KualiDecimal period4Depreciation2Amount) {
        this.period4Depreciation2Amount = period4Depreciation2Amount;
    }


    /**
     * Gets the period5Depreciation1Amount attribute.
     * 
     * @return Returns the period5Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod5Depreciation1Amount() {
        return period5Depreciation1Amount;
    }

    /**
     * Sets the period5Depreciation1Amount attribute.
     * 
     * @param period5Depreciation1Amount The period5Depreciation1Amount to set.
     * 
     */
    public void setPeriod5Depreciation1Amount(KualiDecimal period5Depreciation1Amount) {
        this.period5Depreciation1Amount = period5Depreciation1Amount;
    }


    /**
     * Gets the period5Depreciation2Amount attribute.
     * 
     * @return Returns the period5Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod5Depreciation2Amount() {
        return period5Depreciation2Amount;
    }

    /**
     * Sets the period5Depreciation2Amount attribute.
     * 
     * @param period5Depreciation2Amount The period5Depreciation2Amount to set.
     * 
     */
    public void setPeriod5Depreciation2Amount(KualiDecimal period5Depreciation2Amount) {
        this.period5Depreciation2Amount = period5Depreciation2Amount;
    }


    /**
     * Gets the period6Depreciation1Amount attribute.
     * 
     * @return Returns the period6Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod6Depreciation1Amount() {
        return period6Depreciation1Amount;
    }

    /**
     * Sets the period6Depreciation1Amount attribute.
     * 
     * @param period6Depreciation1Amount The period6Depreciation1Amount to set.
     * 
     */
    public void setPeriod6Depreciation1Amount(KualiDecimal period6Depreciation1Amount) {
        this.period6Depreciation1Amount = period6Depreciation1Amount;
    }


    /**
     * Gets the period6Depreciation2Amount attribute.
     * 
     * @return Returns the period6Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod6Depreciation2Amount() {
        return period6Depreciation2Amount;
    }

    /**
     * Sets the period6Depreciation2Amount attribute.
     * 
     * @param period6Depreciation2Amount The period6Depreciation2Amount to set.
     * 
     */
    public void setPeriod6Depreciation2Amount(KualiDecimal period6Depreciation2Amount) {
        this.period6Depreciation2Amount = period6Depreciation2Amount;
    }


    /**
     * Gets the period7Depreciation1Amount attribute.
     * 
     * @return Returns the period7Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod7Depreciation1Amount() {
        return period7Depreciation1Amount;
    }

    /**
     * Sets the period7Depreciation1Amount attribute.
     * 
     * @param period7Depreciation1Amount The period7Depreciation1Amount to set.
     * 
     */
    public void setPeriod7Depreciation1Amount(KualiDecimal period7Depreciation1Amount) {
        this.period7Depreciation1Amount = period7Depreciation1Amount;
    }


    /**
     * Gets the period7Depreciation2Amount attribute.
     * 
     * @return Returns the period7Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod7Depreciation2Amount() {
        return period7Depreciation2Amount;
    }

    /**
     * Sets the period7Depreciation2Amount attribute.
     * 
     * @param period7Depreciation2Amount The period7Depreciation2Amount to set.
     * 
     */
    public void setPeriod7Depreciation2Amount(KualiDecimal period7Depreciation2Amount) {
        this.period7Depreciation2Amount = period7Depreciation2Amount;
    }


    /**
     * Gets the period8Depreciation1Amount attribute.
     * 
     * @return Returns the period8Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod8Depreciation1Amount() {
        return period8Depreciation1Amount;
    }

    /**
     * Sets the period8Depreciation1Amount attribute.
     * 
     * @param period8Depreciation1Amount The period8Depreciation1Amount to set.
     * 
     */
    public void setPeriod8Depreciation1Amount(KualiDecimal period8Depreciation1Amount) {
        this.period8Depreciation1Amount = period8Depreciation1Amount;
    }


    /**
     * Gets the period8Depreciation2Amount attribute.
     * 
     * @return Returns the period8Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod8Depreciation2Amount() {
        return period8Depreciation2Amount;
    }

    /**
     * Sets the period8Depreciation2Amount attribute.
     * 
     * @param period8Depreciation2Amount The period8Depreciation2Amount to set.
     * 
     */
    public void setPeriod8Depreciation2Amount(KualiDecimal period8Depreciation2Amount) {
        this.period8Depreciation2Amount = period8Depreciation2Amount;
    }


    /**
     * Gets the period9Depreciation1Amount attribute.
     * 
     * @return Returns the period9Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod9Depreciation1Amount() {
        return period9Depreciation1Amount;
    }

    /**
     * Sets the period9Depreciation1Amount attribute.
     * 
     * @param period9Depreciation1Amount The period9Depreciation1Amount to set.
     * 
     */
    public void setPeriod9Depreciation1Amount(KualiDecimal period9Depreciation1Amount) {
        this.period9Depreciation1Amount = period9Depreciation1Amount;
    }


    /**
     * Gets the period9Depreciation2Amount attribute.
     * 
     * @return Returns the period9Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod9Depreciation2Amount() {
        return period9Depreciation2Amount;
    }

    /**
     * Sets the period9Depreciation2Amount attribute.
     * 
     * @param period9Depreciation2Amount The period9Depreciation2Amount to set.
     * 
     */
    public void setPeriod9Depreciation2Amount(KualiDecimal period9Depreciation2Amount) {
        this.period9Depreciation2Amount = period9Depreciation2Amount;
    }


    /**
     * Gets the period10Depreciation1Amount attribute.
     * 
     * @return Returns the period10Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod10Depreciation1Amount() {
        return period10Depreciation1Amount;
    }

    /**
     * Sets the period10Depreciation1Amount attribute.
     * 
     * @param period10Depreciation1Amount The period10Depreciation1Amount to set.
     * 
     */
    public void setPeriod10Depreciation1Amount(KualiDecimal period10Depreciation1Amount) {
        this.period10Depreciation1Amount = period10Depreciation1Amount;
    }


    /**
     * Gets the period10Depreciation2Amount attribute.
     * 
     * @return Returns the period10Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod10Depreciation2Amount() {
        return period10Depreciation2Amount;
    }

    /**
     * Sets the period10Depreciation2Amount attribute.
     * 
     * @param period10Depreciation2Amount The period10Depreciation2Amount to set.
     * 
     */
    public void setPeriod10Depreciation2Amount(KualiDecimal period10Depreciation2Amount) {
        this.period10Depreciation2Amount = period10Depreciation2Amount;
    }


    /**
     * Gets the period11Depreciation1Amount attribute.
     * 
     * @return Returns the period11Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod11Depreciation1Amount() {
        return period11Depreciation1Amount;
    }

    /**
     * Sets the period11Depreciation1Amount attribute.
     * 
     * @param period11Depreciation1Amount The period11Depreciation1Amount to set.
     * 
     */
    public void setPeriod11Depreciation1Amount(KualiDecimal period11Depreciation1Amount) {
        this.period11Depreciation1Amount = period11Depreciation1Amount;
    }


    /**
     * Gets the period11Depreciation2Amount attribute.
     * 
     * @return Returns the period11Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod11Depreciation2Amount() {
        return period11Depreciation2Amount;
    }

    /**
     * Sets the period11Depreciation2Amount attribute.
     * 
     * @param period11Depreciation2Amount The period11Depreciation2Amount to set.
     * 
     */
    public void setPeriod11Depreciation2Amount(KualiDecimal period11Depreciation2Amount) {
        this.period11Depreciation2Amount = period11Depreciation2Amount;
    }


    /**
     * Gets the period12Depreciation1Amount attribute.
     * 
     * @return Returns the period12Depreciation1Amount
     * 
     */
    public KualiDecimal getPeriod12Depreciation1Amount() {
        return period12Depreciation1Amount;
    }

    /**
     * Sets the period12Depreciation1Amount attribute.
     * 
     * @param period12Depreciation1Amount The period12Depreciation1Amount to set.
     * 
     */
    public void setPeriod12Depreciation1Amount(KualiDecimal period12Depreciation1Amount) {
        this.period12Depreciation1Amount = period12Depreciation1Amount;
    }


    /**
     * Gets the period12Depreciation2Amount attribute.
     * 
     * @return Returns the period12Depreciation2Amount
     * 
     */
    public KualiDecimal getPeriod12Depreciation2Amount() {
        return period12Depreciation2Amount;
    }

    /**
     * Sets the period12Depreciation2Amount attribute.
     * 
     * @param period12Depreciation2Amount The period12Depreciation2Amount to set.
     * 
     */
    public void setPeriod12Depreciation2Amount(KualiDecimal period12Depreciation2Amount) {
        this.period12Depreciation2Amount = period12Depreciation2Amount;
    }


    /**
     * Gets the transferPaymentCode attribute.
     * 
     * @return Returns the transferPaymentCode
     * 
     */
    public String getTransferPaymentCode() {
        return transferPaymentCode;
    }

    /**
     * Sets the transferPaymentCode attribute.
     * 
     * @param transferPaymentCode The transferPaymentCode to set.
     * 
     */
    public void setTransferPaymentCode(String transferPaymentCode) {
        this.transferPaymentCode = transferPaymentCode;
    }


    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset
     * 
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute.
     * 
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount
     * 
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute.
     * 
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     * 
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject
     * 
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute.
     * 
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the project attribute.
     * 
     * @return Returns the project
     * 
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project attribute.
     * 
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the documentHeader attribute.
     * 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * 
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the documentType attribute.
     * 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * 
     * @param documentType The documentType to set.
     * @deprecated
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the financialDocumentPostingPeriod attribute.
     * 
     * @return Returns the financialDocumentPostingPeriod.
     */
    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }

    /**
     * Sets the financialDocumentPostingPeriod attribute value.
     * 
     * @param financialDocumentPostingPeriod The financialDocumentPostingPeriod to set.
     * @deprecated
     */
    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }

    /**
     * Gets the financialSystemOrigination attribute.
     * 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination attribute value.
     * 
     * @param financialSystemOrigination The financialSystemOrigination to set.
     * @deprecated
     */
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }

    /**
     * Gets the option attribute.
     * 
     * @return Returns the option.
     */
    public Options getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * 
     * @param option The option to set.
     * @deprecated
     */
    public void setOption(Options option) {
        this.option = option;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        if (this.paymentSequenceNumber != null) {
            m.put("paymentSequenceNumber", this.paymentSequenceNumber.toString());
        }
        return m;
    }

    /**
     * Get the yearToDate attribute
     * 
     * @return Returns the yearToDate
     */
    public KualiDecimal getYearToDate() {
        return yearToDate;
    }

    /**
     * Sets the yearToDate attribute value.
     * 
     * @param yearToDate The yearToDate to set.
     */
    public void setYearToDate(KualiDecimal yearToDate) {
        this.yearToDate = yearToDate;
    }
}
