package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class EquipmentLoanOrReturn extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String campusTagNumber;
    private String organizationTagNumber;
    private String insuranceChartOfAccountsCode;
    private String insuranceChargeAccountNumber;
    private Date loanDate;
    private Date expectedReturnDate;
    private Date loanReturnDate;
    private String borrowerUniversalIdentifier;
    private String borrowerAddress;
    private String borrowerCityName;
    private String borrowerStateCode;
    private String borrowerZipCode;
    private String borrowerCountryCode;
    private String borrowerPhoneNumber;
    private String borrowerStorageAddress;
    private String borrowerStorageCityName;
    private String borrowerStorageStateCode;
    private String borrowerStorageZipCode;
    private String borrowerStorageCountryCode;
    private String borrowerStoragePhoneNumber;
    private Integer insuranceCode;
    private String signatureCode;
    private Long capitalAssetNumber;
    
    private DocumentHeader documentHeader;
    private Chart insuranceChartOfAccounts;
    private Account insuranceChargeAccount;
    private State borrowerState;
    private State borrowerStorageState;
    private Country borrowerCountry;
    private Country borrowerStorageCountry;
    private UniversalUser borrowerUniversalUser;
    private Asset asset;
    
    /**
     * Default constructor.
     */
    public EquipmentLoanOrReturn() {

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
     * Gets the campusTagNumber attribute.
     * 
     * @return Returns the campusTagNumber
     * 
     */
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    /**
     * Sets the campusTagNumber attribute.
     * 
     * @param campusTagNumber The campusTagNumber to set.
     * 
     */
    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }


    /**
     * Gets the organizationTagNumber attribute.
     * 
     * @return Returns the organizationTagNumber
     * 
     */
    public String getOrganizationTagNumber() {
        return organizationTagNumber;
    }

    /**
     * Sets the organizationTagNumber attribute.
     * 
     * @param organizationTagNumber The organizationTagNumber to set.
     * 
     */
    public void setOrganizationTagNumber(String organizationTagNumber) {
        this.organizationTagNumber = organizationTagNumber;
    }


    /**
     * Gets the insuranceChartOfAccountsCode attribute.
     * 
     * @return Returns the insuranceChartOfAccountsCode
     * 
     */
    public String getInsuranceChartOfAccountsCode() {
        return insuranceChartOfAccountsCode;
    }

    /**
     * Sets the insuranceChartOfAccountsCode attribute.
     * 
     * @param insuranceChartOfAccountsCode The insuranceChartOfAccountsCode to set.
     * 
     */
    public void setInsuranceChartOfAccountsCode(String insuranceChartOfAccountsCode) {
        this.insuranceChartOfAccountsCode = insuranceChartOfAccountsCode;
    }


    /**
     * Gets the insuranceChargeAccountNumber attribute.
     * 
     * @return Returns the insuranceChargeAccountNumber
     * 
     */
    public String getInsuranceChargeAccountNumber() {
        return insuranceChargeAccountNumber;
    }

    /**
     * Sets the insuranceChargeAccountNumber attribute.
     * 
     * @param insuranceChargeAccountNumber The insuranceChargeAccountNumber to set.
     * 
     */
    public void setInsuranceChargeAccountNumber(String insuranceChargeAccountNumber) {
        this.insuranceChargeAccountNumber = insuranceChargeAccountNumber;
    }


    /**
     * Gets the loanDate attribute.
     * 
     * @return Returns the loanDate
     * 
     */
    public Date getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loanDate attribute.
     * 
     * @param loanDate The loanDate to set.
     * 
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }


    /**
     * Gets the expectedReturnDate attribute.
     * 
     * @return Returns the expectedReturnDate
     * 
     */
    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    /**
     * Sets the expectedReturnDate attribute.
     * 
     * @param expectedReturnDate The expectedReturnDate to set.
     * 
     */
    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }


    /**
     * Gets the loanReturnDate attribute.
     * 
     * @return Returns the loanReturnDate
     * 
     */
    public Date getLoanReturnDate() {
        return loanReturnDate;
    }

    /**
     * Sets the loanReturnDate attribute.
     * 
     * @param loanReturnDate The loanReturnDate to set.
     * 
     */
    public void setLoanReturnDate(Date loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }


    /**
     * Gets the borrowerUniversalIdentifier attribute.
     * 
     * @return Returns the borrowerUniversalIdentifier
     * 
     */
    public String getBorrowerUniversalIdentifier() {
        return borrowerUniversalIdentifier;
    }

    /**
     * Sets the borrowerUniversalIdentifier attribute.
     * 
     * @param borrowerUniversalIdentifier The borrowerUniversalIdentifier to set.
     * 
     */
    public void setBorrowerUniversalIdentifier(String borrowerUniversalIdentifier) {
        this.borrowerUniversalIdentifier = borrowerUniversalIdentifier;
    }


    /**
     * Gets the borrowerAddress attribute.
     * 
     * @return Returns the borrowerAddress
     * 
     */
    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    /**
     * Sets the borrowerAddress attribute.
     * 
     * @param borrowerAddress The borrowerAddress to set.
     * 
     */
    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }


    /**
     * Gets the borrowerCityName attribute.
     * 
     * @return Returns the borrowerCityName
     * 
     */
    public String getBorrowerCityName() {
        return borrowerCityName;
    }

    /**
     * Sets the borrowerCityName attribute.
     * 
     * @param borrowerCityName The borrowerCityName to set.
     * 
     */
    public void setBorrowerCityName(String borrowerCityName) {
        this.borrowerCityName = borrowerCityName;
    }


    /**
     * Gets the borrowerStateCode attribute.
     * 
     * @return Returns the borrowerStateCode
     * 
     */
    public String getBorrowerStateCode() {
        return borrowerStateCode;
    }

    /**
     * Sets the borrowerStateCode attribute.
     * 
     * @param borrowerStateCode The borrowerStateCode to set.
     * 
     */
    public void setBorrowerStateCode(String borrowerStateCode) {
        this.borrowerStateCode = borrowerStateCode;
    }


    /**
     * Gets the borrowerZipCode attribute.
     * 
     * @return Returns the borrowerZipCode
     * 
     */
    public String getBorrowerZipCode() {
        return borrowerZipCode;
    }

    /**
     * Sets the borrowerZipCode attribute.
     * 
     * @param borrowerZipCode The borrowerZipCode to set.
     * 
     */
    public void setBorrowerZipCode(String borrowerZipCode) {
        this.borrowerZipCode = borrowerZipCode;
    }


    /**
     * Gets the borrowerCountryCode attribute.
     * 
     * @return Returns the borrowerCountryCode
     * 
     */
    public String getBorrowerCountryCode() {
        return borrowerCountryCode;
    }

    /**
     * Sets the borrowerCountryCode attribute.
     * 
     * @param borrowerCountryCode The borrowerCountryCode to set.
     * 
     */
    public void setBorrowerCountryCode(String borrowerCountryCode) {
        this.borrowerCountryCode = borrowerCountryCode;
    }


    /**
     * Gets the borrowerPhoneNumber attribute.
     * 
     * @return Returns the borrowerPhoneNumber
     * 
     */
    public String getBorrowerPhoneNumber() {
        return borrowerPhoneNumber;
    }

    /**
     * Sets the borrowerPhoneNumber attribute.
     * 
     * @param borrowerPhoneNumber The borrowerPhoneNumber to set.
     * 
     */
    public void setBorrowerPhoneNumber(String borrowerPhoneNumber) {
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }


    /**
     * Gets the borrowerStorageAddress attribute.
     * 
     * @return Returns the borrowerStorageAddress
     * 
     */
    public String getBorrowerStorageAddress() {
        return borrowerStorageAddress;
    }

    /**
     * Sets the borrowerStorageAddress attribute.
     * 
     * @param borrowerStorageAddress The borrowerStorageAddress to set.
     * 
     */
    public void setBorrowerStorageAddress(String borrowerStorageAddress) {
        this.borrowerStorageAddress = borrowerStorageAddress;
    }


    /**
     * Gets the borrowerStorageCityName attribute.
     * 
     * @return Returns the borrowerStorageCityName
     * 
     */
    public String getBorrowerStorageCityName() {
        return borrowerStorageCityName;
    }

    /**
     * Sets the borrowerStorageCityName attribute.
     * 
     * @param borrowerStorageCityName The borrowerStorageCityName to set.
     * 
     */
    public void setBorrowerStorageCityName(String borrowerStorageCityName) {
        this.borrowerStorageCityName = borrowerStorageCityName;
    }


    /**
     * Gets the borrowerStorageStateCode attribute.
     * 
     * @return Returns the borrowerStorageStateCode
     * 
     */
    public String getBorrowerStorageStateCode() {
        return borrowerStorageStateCode;
    }

    /**
     * Sets the borrowerStorageStateCode attribute.
     * 
     * @param borrowerStorageStateCode The borrowerStorageStateCode to set.
     * 
     */
    public void setBorrowerStorageStateCode(String borrowerStorageStateCode) {
        this.borrowerStorageStateCode = borrowerStorageStateCode;
    }


    /**
     * Gets the borrowerStorageZipCode attribute.
     * 
     * @return Returns the borrowerStorageZipCode
     * 
     */
    public String getBorrowerStorageZipCode() {
        return borrowerStorageZipCode;
    }

    /**
     * Sets the borrowerStorageZipCode attribute.
     * 
     * @param borrowerStorageZipCode The borrowerStorageZipCode to set.
     * 
     */
    public void setBorrowerStorageZipCode(String borrowerStorageZipCode) {
        this.borrowerStorageZipCode = borrowerStorageZipCode;
    }


    /**
     * Gets the borrowerStorageCountryCode attribute.
     * 
     * @return Returns the borrowerStorageCountryCode
     * 
     */
    public String getBorrowerStorageCountryCode() {
        return borrowerStorageCountryCode;
    }

    /**
     * Sets the borrowerStorageCountryCode attribute.
     * 
     * @param borrowerStorageCountryCode The borrowerStorageCountryCode to set.
     * 
     */
    public void setBorrowerStorageCountryCode(String borrowerStorageCountryCode) {
        this.borrowerStorageCountryCode = borrowerStorageCountryCode;
    }


    /**
     * Gets the borrowerStoragePhoneNumber attribute.
     * 
     * @return Returns the borrowerStoragePhoneNumber
     * 
     */
    public String getBorrowerStoragePhoneNumber() {
        return borrowerStoragePhoneNumber;
    }

    /**
     * Sets the borrowerStoragePhoneNumber attribute.
     * 
     * @param borrowerStoragePhoneNumber The borrowerStoragePhoneNumber to set.
     * 
     */
    public void setBorrowerStoragePhoneNumber(String borrowerStoragePhoneNumber) {
        this.borrowerStoragePhoneNumber = borrowerStoragePhoneNumber;
    }


    /**
     * Gets the insuranceCode attribute.
     * 
     * @return Returns the insuranceCode
     * 
     */
    public Integer getInsuranceCode() {
        return insuranceCode;
    }

    /**
     * Sets the insuranceCode attribute.
     * 
     * @param insuranceCode The insuranceCode to set.
     * 
     */
    public void setInsuranceCode(Integer insuranceCode) {
        this.insuranceCode = insuranceCode;
    }


    /**
     * Gets the signatureCode attribute.
     * 
     * @return Returns the signatureCode
     * 
     */
    public String getSignatureCode() {
        return signatureCode;
    }

    /**
     * Sets the signatureCode attribute.
     * 
     * @param signatureCode The signatureCode to set.
     * 
     */
    public void setSignatureCode(String signatureCode) {
        this.signatureCode = signatureCode;
    }

    /**
     * Gets the capitalAssetNumber attribute. 
     * @return Returns the capitalAssetNumber.
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the documentHeader attribute.
     * 
     * @return Returns the documentHeader
     * 
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute.
     * 
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the insuranceChartOfAccounts attribute.
     * 
     * @return Returns the insuranceChartOfAccounts
     * 
     */
    public Chart getInsuranceChartOfAccounts() {
        return insuranceChartOfAccounts;
    }

    /**
     * Sets the insuranceChartOfAccounts attribute.
     * 
     * @param insuranceChartOfAccounts The insuranceChartOfAccounts to set.
     * @deprecated
     */
    public void setInsuranceChartOfAccounts(Chart insuranceChartOfAccounts) {
        this.insuranceChartOfAccounts = insuranceChartOfAccounts;
    }

    /**
     * Gets the insuranceChargeAccount attribute.
     * 
     * @return Returns the insuranceChargeAccount
     * 
     */
    public Account getInsuranceChargeAccount() {
        return insuranceChargeAccount;
    }

    /**
     * Sets the insuranceChargeAccount attribute.
     * 
     * @param insuranceChargeAccount The insuranceChargeAccount to set.
     * @deprecated
     */
    public void setInsuranceChargeAccount(Account insuranceChargeAccount) {
        this.insuranceChargeAccount = insuranceChargeAccount;
    }

    /**
     * Gets the borrowerCountry attribute.
     * 
     * @return Returns the borrowerCountry.
     */
    public Country getBorrowerCountry() {
        return borrowerCountry;
    }

    /**
     * Sets the borrowerCountry attribute value.
     * 
     * @param borrowerCountry The borrowerCountry to set.
     * @deprecated
     */
    public void setBorrowerCountry(Country borrowerCountry) {
        this.borrowerCountry = borrowerCountry;
    }

    /**
     * Gets the borrowerState attribute.
     * 
     * @return Returns the borrowerState.
     */
    public State getBorrowerState() {
        return borrowerState;
    }

    /**
     * Sets the borrowerState attribute value.
     * 
     * @param borrowerState The borrowerState to set.
     * @deprecated
     */
    public void setBorrowerState(State borrowerState) {
        this.borrowerState = borrowerState;
    }

    /**
     * Gets the borrowerStorageCountry attribute.
     * 
     * @return Returns the borrowerStorageCountry.
     */
    public Country getBorrowerStorageCountry() {
        return borrowerStorageCountry;
    }

    /**
     * Sets the borrowerStorageCountry attribute value.
     * 
     * @param borrowerStorageCountry The borrowerStorageCountry to set.
     * @deprecated
     */
    public void setBorrowerStorageCountry(Country borrowerStorageCountry) {
        this.borrowerStorageCountry = borrowerStorageCountry;
    }

    /**
     * Gets the borrowerStorageState attribute.
     * 
     * @return Returns the borrowerStorageState.
     */
    public State getBorrowerStorageState() {
        return borrowerStorageState;
    }

    /**
     * Sets the borrowerStorageState attribute value.
     * 
     * @param borrowerStorageState The borrowerStorageState to set.
     * @deprecated
     */
    public void setBorrowerStorageState(State borrowerStorageState) {
        this.borrowerStorageState = borrowerStorageState;
    }

    /**
     * Gets the asset attribute. 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }    
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public UniversalUser getBorrowerUniversalUser() {
        return borrowerUniversalUser;
    }

    public void setBorrowerUniversalUser(UniversalUser borrowerUniversalUser) {
        this.borrowerUniversalUser = borrowerUniversalUser;
    }

}
