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
package org.kuali.module.cams.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanOrReturnService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;


public class EquipmentLoanOrReturnDocument extends TransactionalDocumentBase {

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
    private boolean signatureCode;

    private Chart insuranceChartOfAccounts;
    private Account insuranceChargeAccount;
    private State borrowerState;
    private State borrowerStorageState;
    private Country borrowerCountry;
    private Country borrowerStorageCountry;
    private UniversalUser borrowerUniversalUser;

    private AssetHeader assetHeader;
    // Transient attributes
    private transient Asset asset;

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Country getBorrowerCountry() {
        return borrowerCountry;
    }

    public void setBorrowerCountry(Country borrowerCountry) {
        this.borrowerCountry = borrowerCountry;
    }

    public State getBorrowerState() {
        return borrowerState;
    }

    public void setBorrowerState(State borrowerState) {
        this.borrowerState = borrowerState;
    }

    public Country getBorrowerStorageCountry() {
        return borrowerStorageCountry;
    }

    public void setBorrowerStorageCountry(Country borrowerStorageCountry) {
        this.borrowerStorageCountry = borrowerStorageCountry;
    }

    public State getBorrowerStorageState() {
        return borrowerStorageState;
    }

    public void setBorrowerStorageState(State borrowerStorageState) {
        this.borrowerStorageState = borrowerStorageState;
    }

    public UniversalUser getBorrowerUniversalUser() {
        borrowerUniversalUser = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(borrowerUniversalIdentifier, borrowerUniversalUser);
        return borrowerUniversalUser;
    }

    public void setBorrowerUniversalUser(UniversalUser borrowerUniversalUser) {
        this.borrowerUniversalUser = borrowerUniversalUser;
    }

    public Account getInsuranceChargeAccount() {
        return insuranceChargeAccount;
    }

    public void setInsuranceChargeAccount(Account insuranceChargeAccount) {
        this.insuranceChargeAccount = insuranceChargeAccount;
    }

    public Chart getInsuranceChartOfAccounts() {
        return insuranceChartOfAccounts;
    }

    public void setInsuranceChartOfAccounts(Chart insuranceChartOfAccounts) {
        this.insuranceChartOfAccounts = insuranceChartOfAccounts;
    }

    public EquipmentLoanOrReturnDocument() {
        super();

    }

    public String getCampusTagNumber() {
        return campusTagNumber;
    }


    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }

    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    public String getBorrowerCityName() {
        return borrowerCityName;
    }

    public void setBorrowerCityName(String borrowerCityName) {
        this.borrowerCityName = borrowerCityName;
    }

    public String getBorrowerCountryCode() {
        return borrowerCountryCode;
    }

    public void setBorrowerCountryCode(String borrowerCountryCode) {
        this.borrowerCountryCode = borrowerCountryCode;
    }

    public String getBorrowerPhoneNumber() {
        return borrowerPhoneNumber;
    }

    public void setBorrowerPhoneNumber(String borrowerPhoneNumber) {
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }

    public String getBorrowerStateCode() {
        return borrowerStateCode;
    }

    public void setBorrowerStateCode(String borrowerStateCode) {
        this.borrowerStateCode = borrowerStateCode;
    }

    public String getBorrowerStorageAddress() {
        return borrowerStorageAddress;
    }

    public void setBorrowerStorageAddress(String borrowerStorageAddress) {
        this.borrowerStorageAddress = borrowerStorageAddress;
    }

    public String getBorrowerStorageCityName() {
        return borrowerStorageCityName;
    }

    public void setBorrowerStorageCityName(String borrowerStorageCityName) {
        this.borrowerStorageCityName = borrowerStorageCityName;
    }

    public String getBorrowerStorageCountryCode() {
        return borrowerStorageCountryCode;
    }

    public void setBorrowerStorageCountryCode(String borrowerStorageCountryCode) {
        this.borrowerStorageCountryCode = borrowerStorageCountryCode;
    }

    public String getBorrowerStoragePhoneNumber() {
        return borrowerStoragePhoneNumber;
    }

    public void setBorrowerStoragePhoneNumber(String borrowerStoragePhoneNumber) {
        this.borrowerStoragePhoneNumber = borrowerStoragePhoneNumber;
    }

    public String getBorrowerStorageStateCode() {
        return borrowerStorageStateCode;
    }

    public void setBorrowerStorageStateCode(String borrowerStorageStateCode) {
        this.borrowerStorageStateCode = borrowerStorageStateCode;
    }

    public String getBorrowerStorageZipCode() {
        return borrowerStorageZipCode;
    }

    public void setBorrowerStorageZipCode(String borrowerStorageZipCode) {
        this.borrowerStorageZipCode = borrowerStorageZipCode;
    }

    public String getBorrowerUniversalIdentifier() {
        return borrowerUniversalIdentifier;
    }

    public void setBorrowerUniversalIdentifier(String borrowerUniversalIdentifier) {
        this.borrowerUniversalIdentifier = borrowerUniversalIdentifier;
    }

    public String getBorrowerZipCode() {
        return borrowerZipCode;
    }

    public void setBorrowerZipCode(String borrowerZipCode) {
        this.borrowerZipCode = borrowerZipCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public String getInsuranceChargeAccountNumber() {
        return insuranceChargeAccountNumber;
    }

    public void setInsuranceChargeAccountNumber(String insuranceChargeAccountNumber) {
        this.insuranceChargeAccountNumber = insuranceChargeAccountNumber;
    }

    public String getInsuranceChartOfAccountsCode() {
        return insuranceChartOfAccountsCode;
    }

    public void setInsuranceChartOfAccountsCode(String insuranceChartOfAccountsCode) {
        this.insuranceChartOfAccountsCode = insuranceChartOfAccountsCode;
    }

    public Integer getInsuranceCode() {
        return insuranceCode;
    }

    public void setInsuranceCode(Integer insuranceCode) {
        this.insuranceCode = insuranceCode;
    }

    public Date getLoanDate() {
        if (ObjectUtils.isNotNull(loanDate)) {
            return loanDate;
        }
        else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getLoanReturnDate() {
        return loanReturnDate;
    }

    public void setLoanReturnDate(Date loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }

    public String getOrganizationTagNumber() {
        return organizationTagNumber;
    }

    public void setOrganizationTagNumber(String organizationTagNumber) {
        this.organizationTagNumber = organizationTagNumber;
    }

    public AssetHeader getAssetHeader() {
        return assetHeader;
    }

    public void setAssetHeader(AssetHeader assetHeader) {
        this.assetHeader = assetHeader;
    }

    /**
     * @see org.kuali.core.document.DocumentBase#postProcessSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            MaintenanceDocumentService maintenanceDocumentService = SpringContext.getBean(MaintenanceDocumentService.class);
            AssetService assetService = SpringContext.getBean(AssetService.class);

            maintenanceDocumentService.deleteLocks(this.getDocumentNumber());

            List<MaintenanceLock> maintenanceLocks = new ArrayList();
            maintenanceLocks.add(assetService.generateAssetLock(documentNumber, assetHeader.getCapitalAssetNumber()));
            maintenanceDocumentService.storeLocks(maintenanceLocks);
        }
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        
        if (workflowDocument.stateIsProcessed()) {
            SpringContext.getBean(EquipmentLoanOrReturnService.class).processApprovedEquipmentLoanOrReturn(this);

            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(assetHeader.getDocumentNumber());
        }
        
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(assetHeader.getDocumentNumber());
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public boolean isSignatureCode() {
        return signatureCode;
    }

    public void setSignatureCode(boolean signatureCode) {
        this.signatureCode = signatureCode;
    }


}
