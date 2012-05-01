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
package org.kuali.kfs.module.cam.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;


public class EquipmentLoanOrReturnDocument extends FinancialSystemTransactionalDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocument.class);

    protected String hiddenFieldForError;
    protected String documentNumber;
    protected Date loanDate;
    protected Date expectedReturnDate;
    protected Date loanReturnDate;
    protected String borrowerUniversalIdentifier;
    protected String borrowerAddress;
    protected String borrowerCityName;
    protected String borrowerStateCode;
    protected String borrowerZipCode;
    protected String borrowerCountryCode;
    protected String borrowerPhoneNumber;
    protected String borrowerStorageAddress;
    protected String borrowerStorageCityName;
    protected String borrowerStorageStateCode;
    protected String borrowerStorageZipCode;
    protected String borrowerStorageCountryCode;
    protected String borrowerStoragePhoneNumber;
    protected Long capitalAssetNumber;

    protected StateEbo borrowerState;
    protected StateEbo borrowerStorageState;
    protected CountryEbo borrowerCountry;
    protected CountryEbo borrowerStorageCountry;
    protected Person borrowerPerson;
    protected Asset asset;
    protected PostalCodeEbo borrowerPostalZipCode;
    protected PostalCodeEbo borrowerStoragePostalZipCode;

    // sets document status (i.e. new loan, return, or renew)
    protected boolean newLoan;
    protected boolean returnLoan;

    /**
     * Default constructor.
     */
    public EquipmentLoanOrReturnDocument() {
        super();
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute.
     * 
     * @param asset The asset to set.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the borrowerCountry attribute.
     * 
     * @return Returns the borrowerCountry
     */
    public CountryEbo getBorrowerCountry() {
        if ( StringUtils.isBlank(borrowerCountryCode) ) {
            borrowerCountry = null;
        } else {
            if ( borrowerCountry == null || !StringUtils.equals( borrowerCountry.getCode(),borrowerCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerCountryCode);
                    borrowerCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return borrowerCountry;       
     }

    /**
     * Sets the borrowerCountry attribute.
     * 
     * @param borrowerCountry The borrowerCountry to set.
     */
    public void setBorrowerCountry(CountryEbo borrowerCountry) {
        this.borrowerCountry = borrowerCountry;
    }

    /**
     * Gets the borrowerState attribute.
     * 
     * @return Returns the borrowerState
     */
    public StateEbo getBorrowerState() {
        if ( StringUtils.isBlank(borrowerCountryCode) || StringUtils.isBlank(borrowerStateCode) ) {
            borrowerState = null;
        } else {
            if ( borrowerState == null || !StringUtils.equals( borrowerState.getCountryCode(), borrowerCountryCode) || !StringUtils.equals(borrowerState.getCode(), borrowerStateCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, borrowerCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerStateCode);
                    borrowerState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }        
        return borrowerState;
    }

    /**
     * Sets the borrowerState attribute.
     * 
     * @param borrowerState The borrowerState to set.
     */
    public void setBorrowerState(StateEbo borrowerState) {
        this.borrowerState = borrowerState;
    }

    /**
     * Gets the borrowerStorageCountry attribute.
     * 
     * @return Returns the borrowerStorageCountry
     */
    public CountryEbo getBorrowerStorageCountry() {
        if ( StringUtils.isBlank(borrowerStorageCountryCode) ) {
            borrowerStorageCountry = null;
        } else {
            if ( borrowerStorageCountry == null || !StringUtils.equals( borrowerStorageCountry.getCode(),borrowerStorageCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerStorageCountryCode);
                    borrowerStorageCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return borrowerStorageCountry;
    }

    /**
     * Sets the borrowerStorageCountry attribute.
     * 
     * @param borrowerStorageCountry The borrowerStorageCountry to set.
     */
    public void setBorrowerStorageCountry(CountryEbo borrowerStorageCountry) {
        this.borrowerStorageCountry = borrowerStorageCountry;
    }

    /**
     * Gets the getBorrowerStorageState attribute.
     * 
     * @return Returns the getBorrowerStorageState
     */
    public StateEbo getBorrowerStorageState() {
        if ( StringUtils.isBlank(borrowerStorageCountryCode) || StringUtils.isBlank(borrowerStorageStateCode) ) {
            borrowerStorageState = null;
        } else {
            if ( borrowerStorageState == null || !StringUtils.equals( borrowerStorageState.getCountryCode(),borrowerStorageCountryCode) || !StringUtils.equals( borrowerStorageState.getCode(), borrowerStorageStateCode)) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, borrowerStorageCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerStorageStateCode);
                    borrowerStorageState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return borrowerStorageState;
    }

    /**
     * Sets the borrowerStorageState attribute.
     * 
     * @param borrowerStorageState The borrowerStorageState to set.
     */
    public void setBorrowerStorageState(StateEbo borrowerStorageState) {
        this.borrowerStorageState = borrowerStorageState;
    }

    /**
     * Gets the borrowerPerson attribute.
     * 
     * @return Returns the borrowerPerson
     */
    public Person getBorrowerPerson() {
        borrowerPerson = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(borrowerUniversalIdentifier, borrowerPerson);
        return borrowerPerson;
    }

    /**
     * Sets the borrowerPerson attribute.
     * 
     * @param borrowerPerson The borrowerPerson to set.
     */
    public void setBorrowerPerson(Person borrowerPerson) {
        this.borrowerPerson = borrowerPerson;
    }

    /**
     * Gets the borrowerAddress attribute.
     * 
     * @return Returns the borrowerAddress
     */
    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    /**
     * Sets the borrowerAddress attribute.
     * 
     * @param borrowerAddress The borrowerAddress to set.
     */
    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    /**
     * Gets the borrowerCityName attribute.
     * 
     * @return Returns the borrowerCityName
     */
    public String getBorrowerCityName() {
        return borrowerCityName;
    }

    /**
     * Sets the borrowerCityName attribute.
     * 
     * @param borrowerCityName The borrowerCityName to set.
     */
    public void setBorrowerCityName(String borrowerCityName) {
        this.borrowerCityName = borrowerCityName;
    }

    /**
     * Gets the borrowerCountryCode attribute.
     * 
     * @return Returns the borrowerCountryCode
     */
    public String getBorrowerCountryCode() {
        return borrowerCountryCode;
    }

    /**
     * Sets the borrowerCountryCode attribute.
     * 
     * @param borrowerCountryCode The borrowerCountryCode to set.
     */
    public void setBorrowerCountryCode(String borrowerCountryCode) {
        this.borrowerCountryCode = borrowerCountryCode;
    }

    /**
     * Gets the borrowerPhoneNumber attribute.
     * 
     * @return Returns the borrowerPhoneNumber
     */
    public String getBorrowerPhoneNumber() {
        return borrowerPhoneNumber;
    }

    /**
     * Sets the borrowerPhoneNumber attribute.
     * 
     * @param borrowerPhoneNumber The borrowerPhoneNumber to set.
     */
    public void setBorrowerPhoneNumber(String borrowerPhoneNumber) {
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }

    /**
     * Gets the borrowerStateCode attribute.
     * 
     * @return Returns the borrowerStateCode
     */
    public String getBorrowerStateCode() {
        return borrowerStateCode;
    }

    /**
     * Sets the borrowerStateCode attribute.
     * 
     * @param borrowerStateCode The borrowerStateCode to set.
     */
    public void setBorrowerStateCode(String borrowerStateCode) {
        this.borrowerStateCode = borrowerStateCode;
    }

    /**
     * Gets the borrowerStorageAddress attribute.
     * 
     * @return Returns the borrowerStorageAddress
     */
    public String getBorrowerStorageAddress() {
        return borrowerStorageAddress;
    }

    /**
     * Sets the borrowerStorageAddress attribute.
     * 
     * @param borrowerStorageAddress The borrowerStorageAddress to set.
     */
    public void setBorrowerStorageAddress(String borrowerStorageAddress) {
        this.borrowerStorageAddress = borrowerStorageAddress;
    }

    /**
     * Gets the borrowerStorageCityName attribute.
     * 
     * @return Returns the borrowerStorageCityName
     */
    public String getBorrowerStorageCityName() {
        return borrowerStorageCityName;
    }

    /**
     * Sets the borrowerStorageCityName attribute.
     * 
     * @param borrowerStorageCityName The borrowerStorageCityName to set.
     */
    public void setBorrowerStorageCityName(String borrowerStorageCityName) {
        this.borrowerStorageCityName = borrowerStorageCityName;
    }

    /**
     * Gets the borrowerStorageCountryCode attribute.
     * 
     * @return Returns the borrowerStorageCountryCode
     */
    public String getBorrowerStorageCountryCode() {
        return borrowerStorageCountryCode;
    }

    /**
     * Sets the borrowerStorageCountryCode attribute.
     * 
     * @param borrowerStorageCountryCode The borrowerStorageCountryCode to set.
     */
    public void setBorrowerStorageCountryCode(String borrowerStorageCountryCode) {
        this.borrowerStorageCountryCode = borrowerStorageCountryCode;
    }

    /**
     * Gets the borrowerStoragePhoneNumber attribute.
     * 
     * @return Returns the borrowerStoragePhoneNumber
     */
    public String getBorrowerStoragePhoneNumber() {
        return borrowerStoragePhoneNumber;
    }

    /**
     * Sets the borrowerStoragePhoneNumber attribute.
     * 
     * @param borrowerStoragePhoneNumber The borrowerStoragePhoneNumber to set.
     */
    public void setBorrowerStoragePhoneNumber(String borrowerStoragePhoneNumber) {
        this.borrowerStoragePhoneNumber = borrowerStoragePhoneNumber;
    }

    /**
     * Gets the borrowerStorageStateCode attribute.
     * 
     * @return Returns the borrowerStorageStateCode
     */
    public String getBorrowerStorageStateCode() {
        return borrowerStorageStateCode;
    }

    /**
     * Sets the borrowerStorageStateCode attribute.
     * 
     * @param borrowerStorageStateCode The borrowerStorageStateCode to set.
     */
    public void setBorrowerStorageStateCode(String borrowerStorageStateCode) {
        this.borrowerStorageStateCode = borrowerStorageStateCode;
    }

    /**
     * Gets the borrowerStorageZipCode attribute.
     * 
     * @return Returns the borrowerStorageZipCode
     */
    public String getBorrowerStorageZipCode() {
        return borrowerStorageZipCode;
    }

    /**
     * Sets the borrowerStorageZipCode attribute.
     * 
     * @param borrowerStorageZipCode The borrowerStorageZipCode to set.
     */
    public void setBorrowerStorageZipCode(String borrowerStorageZipCode) {
        this.borrowerStorageZipCode = borrowerStorageZipCode;
    }

    /**
     * Gets the borrowerPostalZipCode attribute.
     * 
     * @return Returns the borrowerPostalZipCode
     */
    public PostalCodeEbo getBorrowerPostalZipCode() {
        if ( StringUtils.isBlank(borrowerCountryCode) || StringUtils.isBlank(borrowerZipCode) ) {
            borrowerPostalZipCode = null;
        } else {
            if ( borrowerPostalZipCode == null || !StringUtils.equals( borrowerPostalZipCode.getCountryCode(),borrowerCountryCode)|| !StringUtils.equals( borrowerPostalZipCode.getCode(), borrowerZipCode)) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, borrowerCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerZipCode);
                    borrowerPostalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }        
        return borrowerPostalZipCode;
    }

    /**
     * Sets the borrowerPostalZipCode attribute.
     * 
     * @param borrowerPostalZipCode The borrowerPostalZipCode to set.
     */
    public void setBorrowerPostalZipCode(PostalCodeEbo borrowerPostalZipCode) {
        this.borrowerPostalZipCode = borrowerPostalZipCode;
    }

    /**
     * Sets the borrowerStoragePostalZipCode attribute.
     * 
     * @param borrowerStoragePostalZipCode The borrowerStoragePostalZipCode to set.
     */
    public PostalCodeEbo getBorrowerStoragePostalZipCode() {
        if ( StringUtils.isBlank(borrowerStorageCountryCode) || StringUtils.isBlank(borrowerStorageZipCode) ) {
            borrowerStoragePostalZipCode = null;
        } else {
            if (  borrowerStoragePostalZipCode == null || !StringUtils.equals( borrowerStoragePostalZipCode.getCountryCode(),borrowerStorageCountryCode)|| !StringUtils.equals( borrowerStoragePostalZipCode.getCode(), borrowerStorageZipCode)) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, borrowerStorageCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, borrowerStorageZipCode);
                    borrowerStoragePostalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return borrowerStoragePostalZipCode;
    }

    /**
     * Gets the borrowerStoragePostalZipCode attribute.
     * 
     * @return Returns the borrowerStoragePostalZipCode
     */
    public void setborrowerStoragePostalZipCode(PostalCodeEbo borrowerStoragePostalZipCode) {
        this.borrowerStoragePostalZipCode = borrowerStoragePostalZipCode;
    }

    /**
     * Gets the borrowerUniversalIdentifier attribute.
     * 
     * @return Returns the borrowerUniversalIdentifier
     */
    public String getBorrowerUniversalIdentifier() {
        return borrowerUniversalIdentifier;
    }

    /**
     * Sets the borrowerUniversalIdentifier attribute.
     * 
     * @param borrowerUniversalIdentifier The borrowerUniversalIdentifier to set.
     */
    public void setBorrowerUniversalIdentifier(String borrowerUniversalIdentifier) {
        this.borrowerUniversalIdentifier = borrowerUniversalIdentifier;
    }

    /**
     * Gets the borrowerZipCode attribute.
     * 
     * @return Returns the borrowerZipCode
     */
    public String getBorrowerZipCode() {
        return borrowerZipCode;
    }

    /**
     * Sets the borrowerZipCode attribute.
     * 
     * @param borrowerZipCode The borrowerZipCode to set.
     */
    public void setBorrowerZipCode(String borrowerZipCode) {
        this.borrowerZipCode = borrowerZipCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the expectedReturnDate attribute.
     * 
     * @return Returns the expectedReturnDate
     */
    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    /**
     * Sets the expectedReturnDate attribute.
     * 
     * @param expectedReturnDate The expectedReturnDate to set.
     */
    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    /**
     * Gets the loanDate attribute.
     * 
     * @return Returns the loanDate
     */
    public Date getLoanDate() {
        if (loanDate != null) {
            return loanDate;
        }
        else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
    }

    /**
     * Sets the loanDate attribute.
     * 
     * @param loanDate The loanDate to set.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the loanReturnDate attribute.
     * 
     * @return Returns the loanReturnDate
     */
    public Date getLoanReturnDate() {
        return loanReturnDate;
    }

    /**
     * Sets the loanReturnDate attribute.
     * 
     * @param loanReturnDate The loanReturnDate to set.
     */
    public void setLoanReturnDate(Date loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            ArrayList capitalAssetNumbers = new ArrayList<Long>();
            if (this.getCapitalAssetNumber() != null) {
                capitalAssetNumbers.add(this.getCapitalAssetNumber());
            }
            // check and lock on asset numbers exclude approve event.
            if (!this.getCapitalAssetManagementModuleService().storeAssetLocks(capitalAssetNumbers, this.getDocumentNumber(), CamsConstants.DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN, null)) {
                throw new ValidationException("Asset " + capitalAssetNumbers.toString() + " is being locked by other documents.");
            }
        }
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    /**
     * If the document final, unlock the document
     * 
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isProcessed()) {
            SpringContext.getBean(EquipmentLoanOrReturnService.class).processApprovedEquipmentLoanOrReturn(this);
        }

        // Remove asset lock when doc status change. We don't include isFinal since document always go to 'processed' first.
        if (workflowDocument.isCanceled() || workflowDocument.isDisapproved() || workflowDocument.isProcessed()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(this.getDocumentNumber(), null);
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public boolean isNewLoan() {
        return newLoan;
    }

    public void setNewLoan(boolean newLoan) {
        this.newLoan = newLoan;
    }

    public boolean isReturnLoan() {
        return returnLoan;
    }

    public void setReturnLoan(boolean returnLoan) {
        this.returnLoan = returnLoan;
    }

    public String getHiddenFieldForError() {
        return hiddenFieldForError;
    }

    public void setHiddenFieldForError(String hiddenFieldForError) {
        this.hiddenFieldForError = hiddenFieldForError;
    }

}
