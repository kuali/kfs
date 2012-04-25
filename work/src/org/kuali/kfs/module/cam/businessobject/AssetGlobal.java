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
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobal.class);

    private String documentNumber;
    private String acquisitionTypeCode;
    private String capitalAssetDescription;
    private String inventoryStatusCode;
    private String conditionCode;
    private String capitalAssetTypeCode;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private KualiDecimal totalCostAmount;
    private String landCountyName;
    private Integer landAcreageSize;
    private String landParcelNumber;
    private String vendorName;
    private String organizationText;
    private Date createDate;
    private Date capitalAssetInServiceDate;
    private Date capitalAssetDepreciationDate;
    private String representativeUniversalIdentifier;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String agencyNumber;
    private Integer financialDocumentNextLineNumber;
    private Asset separateSourceCapitalAsset;
    private Integer separateSourcePaymentSequenceNumber;
    private boolean capitalAssetBuilderOriginIndicator;
    
    // CSU 6702 BEGIN
    private Integer postingYear;
    private String postingPeriodCode;
    private AccountingPeriod accountingPeriod;
    // CSU 6702 END

    // Not Persisted
    private Date lastInventoryDate;
    private ContractsAndGrantsAgency agency;
    private Person assetRepresentative;
    private AssetType capitalAssetType;
    private AssetCondition assetCondition;
    private AssetStatus inventoryStatus;
    private List<AssetGlobalDetail> assetGlobalDetails;
    private List<AssetGlobalDetail> assetSharedDetails;
    private List<AssetPaymentDetail> assetPaymentDetails;
    private AssetAcquisitionType acquisitionType;
    private Chart organizationOwnerChartOfAccounts;
    private Account organizationOwnerAccount;

    // field is here so that AssetLookupableHelperServiceImpl can pass action information
    private String financialDocumentTypeCode;
    private Long separateSourceCapitalAssetNumber;

    // Calculate Equal Source Amounts button
    private String calculateEqualSourceAmountsButton;

    // calculate remaining source amount
    private KualiDecimal separateSourceRemainingAmount;
    private KualiDecimal separateSourceTotalAmount;
    private String calculateSeparateSourceRemainingAmountButton;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;
    private FinancialSystemDocumentHeader documentHeader;
    private KualiDecimal totalAssetPaymentAmount;

    //
    private KualiDecimal minAssetTotalAmount;
    private KualiDecimal maxAssetTotalAmount;

    // CSU 6702 BEGIN
    static protected transient AccountingPeriodService accountingPeriodService;
    // CSU 6702 END    
    
    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new ArrayList<AssetGlobalDetail>();
        assetSharedDetails = new ArrayList<AssetGlobalDetail>();
        assetPaymentDetails = new ArrayList<AssetPaymentDetail>();
        this.generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
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
     * Gets the acquisitionTypeCode attribute.
     * 
     * @return Returns the acquisitionTypeCode
     */
    public String getAcquisitionTypeCode() {
        return acquisitionTypeCode;
    }

    /**
     * Sets the acquisitionTypeCode attribute.
     * 
     * @param acquisitionTypeCode The acquisitionTypeCode to set.
     */
    public void setAcquisitionTypeCode(String acquisitionTypeCode) {
        this.acquisitionTypeCode = acquisitionTypeCode;
    }


    /**
     * Gets the capitalAssetDescription attribute.
     * 
     * @return Returns the capitalAssetDescription
     */
    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     * 
     * @param capitalAssetDescription The capitalAssetDescription to set.
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }


    /**
     * Gets the inventoryStatusCode attribute.
     * 
     * @return Returns the inventoryStatusCode
     */
    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    /**
     * Sets the inventoryStatusCode attribute.
     * 
     * @param inventoryStatusCode The inventoryStatusCode to set.
     */
    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }


    /**
     * Gets the conditionCode attribute.
     * 
     * @return Returns the conditionCode
     */
    public String getConditionCode() {
        return conditionCode;
    }

    /**
     * Sets the conditionCode attribute.
     * 
     * @param conditionCode The conditionCode to set.
     */
    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }


    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }


    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     * 
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }


    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     * 
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }


    /**
     * Gets the totalCostAmount attribute.
     * 
     * @return Returns the totalCostAmount
     */
    public KualiDecimal getTotalCostAmount() {
        return totalCostAmount;
    }

    /**
     * Sets the totalCostAmount attribute.
     * 
     * @param totalCostAmount The totalCostAmount to set.
     */
    public void setTotalCostAmount(KualiDecimal totalCostAmount) {
        this.totalCostAmount = totalCostAmount;
    }


    /**
     * Gets the landCountyName attribute.
     * 
     * @return Returns the landCountyName
     */
    public String getLandCountyName() {
        return landCountyName;
    }

    /**
     * Sets the landCountyName attribute.
     * 
     * @param landCountyName The landCountyName to set.
     */
    public void setLandCountyName(String landCountyName) {
        this.landCountyName = landCountyName;
    }


    /**
     * Gets the landAcreageSize attribute.
     * 
     * @return Returns the landAcreageSize
     */
    public Integer getLandAcreageSize() {
        return landAcreageSize;
    }

    /**
     * Sets the landAcreageSize attribute.
     * 
     * @param landAcreageSize The landAcreageSize to set.
     */
    public void setLandAcreageSize(Integer landAcreageSize) {
        this.landAcreageSize = landAcreageSize;
    }


    /**
     * Gets the landParcelNumber attribute.
     * 
     * @return Returns the landParcelNumber
     */
    public String getLandParcelNumber() {
        return landParcelNumber;
    }

    /**
     * Sets the landParcelNumber attribute.
     * 
     * @param landParcelNumber The landParcelNumber to set.
     */
    public void setLandParcelNumber(String landParcelNumber) {
        this.landParcelNumber = landParcelNumber;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the organizationText attribute.
     * 
     * @return Returns the organizationText
     */
    public String getOrganizationText() {
        return organizationText;
    }

    /**
     * Sets the organizationText attribute.
     * 
     * @param organizationText The organizationText to set.
     */
    public void setOrganizationText(String organizationText) {
        this.organizationText = organizationText;
    }


    /**
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate
     */
    public Date getCreateDate() {
        if (createDate != null) {
            return createDate;
        }
        else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
    }

    /**
     * Sets the createDate attribute.
     * 
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the capitalAssetInServiceDate attribute.
     * 
     * @return Returns the capitalAssetInServiceDate
     */
    public Date getCapitalAssetInServiceDate() {
        return capitalAssetInServiceDate;
    }

    /**
     * Sets the capitalAssetInServiceDate attribute.
     * 
     * @param capitalAssetInServiceDate The capitalAssetInServiceDate to set.
     */
    public void setCapitalAssetInServiceDate(Date capitalAssetInServiceDate) {
        this.capitalAssetInServiceDate = capitalAssetInServiceDate;
    }


    /**
     * Gets the capitalAssetDepreciationDate attribute.
     * 
     * @return Returns the capitalAssetDepreciationDate
     */
    public Date getCapitalAssetDepreciationDate() {
        return capitalAssetDepreciationDate;
    }

    /**
     * Sets the capitalAssetDepreciationDate attribute.
     * 
     * @param capitalAssetDepreciationDate The capitalAssetDepreciationDate to set.
     */
    public void setCapitalAssetDepreciationDate(Date capitalAssetDepreciationDate) {
        this.capitalAssetDepreciationDate = capitalAssetDepreciationDate;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute value.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the organizationOwnerAccountNumber attribute.
     * 
     * @return Returns the organizationOwnerAccountNumber.
     */
    public String getOrganizationOwnerAccountNumber() {
        return organizationOwnerAccountNumber;
    }

    /**
     * Sets the organizationOwnerAccountNumber attribute value.
     * 
     * @param organizationOwnerAccountNumber The organizationOwnerAccountNumber to set.
     */
    public void setOrganizationOwnerAccountNumber(String organizationOwnerAccountNumber) {
        this.organizationOwnerAccountNumber = organizationOwnerAccountNumber;
    }

    /**
     * Gets the organizationOwnerChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccountsCode.
     */
    public String getOrganizationOwnerChartOfAccountsCode() {
        return organizationOwnerChartOfAccountsCode;
    }

    /**
     * Sets the organizationOwnerChartOfAccountsCode attribute value.
     * 
     * @param organizationOwnerChartOfAccountsCode The organizationOwnerChartOfAccountsCode to set.
     */
    public void setOrganizationOwnerChartOfAccountsCode(String organizationOwnerChartOfAccountsCode) {
        this.organizationOwnerChartOfAccountsCode = organizationOwnerChartOfAccountsCode;
    }

    /**
     * Gets the representativeUniversalIdentifier attribute.
     * 
     * @return Returns the representativeUniversalIdentifier.
     */
    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    /**
     * Sets the representativeUniversalIdentifier attribute value.
     * 
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }

    /**
     * Gets the assetCondition attribute.
     * 
     * @return Returns the assetCondition.
     */
    public AssetCondition getAssetCondition() {
        return assetCondition;
    }

    /**
     * Sets the assetCondition attribute value.
     * 
     * @param assetCondition The assetCondition to set.
     * @deprecated
     */
    public void setAssetCondition(AssetCondition assetCondition) {
        this.assetCondition = assetCondition;
    }

    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType.
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute value.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    /**
     * Gets the inventoryStatus attribute.
     * 
     * @return Returns the inventoryStatus.
     */
    public AssetStatus getInventoryStatus() {
        return inventoryStatus;
    }

    /**
     * Sets the inventoryStatus attribute value.
     * 
     * @param inventoryStatus The inventoryStatus to set.
     * @deprecated
     */
    public void setInventoryStatus(AssetStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    /**
     * Gets the organizationOwnerAccount attribute.
     * 
     * @return Returns the organizationOwnerAccount.
     */
    public Account getOrganizationOwnerAccount() {
        return organizationOwnerAccount;
    }

    /**
     * Sets the organizationOwnerAccount attribute value.
     * 
     * @param organizationOwnerAccount The organizationOwnerAccount to set.
     * @deprecated
     */
    public void setOrganizationOwnerAccount(Account organizationOwnerAccount) {
        this.organizationOwnerAccount = organizationOwnerAccount;
    }

    /**
     * Gets the organizationOwnerChartOfAccounts attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccounts.
     */
    public Chart getOrganizationOwnerChartOfAccounts() {
        return organizationOwnerChartOfAccounts;
    }

    /**
     * Sets the organizationOwnerChartOfAccounts attribute value.
     * 
     * @param organizationOwnerChartOfAccounts The organizationOwnerChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccounts) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccounts;
    }

    /**
     * Gets the assetGlobalDetails attribute.
     * 
     * @return Returns the assetGlobalDetails.
     */
    public List<AssetGlobalDetail> getAssetGlobalDetails() {
        return assetGlobalDetails;
    }

    /**
     * Sets the assetGlobalDetails attribute value.
     * 
     * @param assetGlobalDetails The assetGlobalDetails to set.
     */
    public void setAssetGlobalDetails(List<AssetGlobalDetail> assetGlobalDetails) {
        this.assetGlobalDetails = assetGlobalDetails;
    }

    /**
     * Gets the assetPaymentDetails attribute.
     * 
     * @return Returns the assetPaymentDetails.
     */
    public List<AssetPaymentDetail> getAssetPaymentDetails() {
        return assetPaymentDetails;
    }

    /**
     * Sets the assetPaymentDetails attribute value.
     * 
     * @param assetPaymentDetails The assetPaymentDetails to set.
     */
    public void setAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentDetails) {
        this.assetPaymentDetails = assetPaymentDetails;
    }

    /**
     * @see org.kuali.rice.kns.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Assets to Update and/or Add. Applicable to both create new and separate.
     * 
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);

        if (assetGlobalService.isAssetSeparate(this)) {
            persistables = assetGlobalService.getSeparateAssets(this);
        }
        else {
            persistables = assetGlobalService.getCreateNewAssets(this);
        }

        return persistables;
    }

    public boolean isPersistable() {
        return true;
    }


    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetGlobalDetails();
    }

    /**
     * Gets the lastInventoryDate attribute.
     * 
     * @return Returns the lastInventoryDate.
     */
    public Date getLastInventoryDate() {
        return lastInventoryDate;
    }

    /**
     * Sets the lastInventoryDate attribute value.
     * 
     * @param lastInventoryDate The lastInventoryDate to set.
     */
    public void setLastInventoryDate(Date lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * Gets the financialDocumentNextLineNumber attribute.
     * 
     * @return Returns the financialDocumentNextLineNumber.
     */
    public Integer getFinancialDocumentNextLineNumber() {
        return financialDocumentNextLineNumber;
    }

    /**
     * Sets the financialDocumentNextLineNumber attribute value.
     * 
     * @param financialDocumentNextLineNumber The financialDocumentNextLineNumber to set.
     */
    public void setFinancialDocumentNextLineNumber(Integer financialDocumentNextLineNumber) {
        this.financialDocumentNextLineNumber = financialDocumentNextLineNumber;
    }

    public Integer incrementFinancialDocumentLineNumber() {
        if (this.financialDocumentNextLineNumber == null) {
            this.financialDocumentNextLineNumber = 0;
        }
        this.financialDocumentNextLineNumber += 1;
        return financialDocumentNextLineNumber;
    }

    public List<AssetGlobalDetail> getAssetSharedDetails() {
        return assetSharedDetails;
    }

    public void setAssetSharedDetails(List<AssetGlobalDetail> assetSharedDetails) {
        this.assetSharedDetails = assetSharedDetails;
    }

    public AssetAcquisitionType getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(AssetAcquisitionType acquisitionType) {
        this.acquisitionType = acquisitionType;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute value.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the assetRepresentative attribute.
     * 
     * @return Returns the assetRepresentative.
     */
    public Person getAssetRepresentative() {
        assetRepresentative = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(representativeUniversalIdentifier, assetRepresentative);
        return assetRepresentative;
    }

    /**
     * Sets the assetRepresentative attribute value.
     * 
     * @param assetRepresentative The assetRepresentative to set.
     */
    public void setAssetRepresentative(Person assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency.
     */
    public ContractsAndGrantsAgency getAgency() {
        return agency = (ContractsAndGrantsAgency) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgency.class).retrieveExternalizableBusinessObjectIfNecessary(this, agency, "agency");
    }

    /**
     * Technically this is obsolete but necessary because MaintenanceDocumentBase.populateXmlDocumentContentsFromMaintainables has
     * the following hack:<br>
     * ObjectUtils.materializeAllSubObjects(oldBo); // hack to resolve XStream not dealing well with Proxies<br>
     * so as long as that is there we need this setter otherwise a NoSuchMethodException occurs.
     * 
     * @deprecated
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
        this.agency = agency;
    }

    /**
     * Gets the generalLedgerPendingEntries attribute.
     * 
     * @return Returns the generalLedgerPendingEntries.
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * Sets the generalLedgerPendingEntries attribute value.
     * 
     * @param generalLedgerPendingEntries The generalLedgerPendingEntries to set.
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    /**
     * Gets the separateSourceCapitalAsset attribute.
     * 
     * @return Returns the separateSourceCapitalAsset.
     */
    public Asset getSeparateSourceCapitalAsset() {
        return separateSourceCapitalAsset;
    }

    /**
     * Sets the separateSourceCapitalAsset attribute value.
     * 
     * @param separateSourceCapitalAsset The separateSourceCapitalAsset to set.
     */
    public void setSeparateSourceCapitalAsset(Asset separateSourceCapitalAsset) {
        this.separateSourceCapitalAsset = separateSourceCapitalAsset;
    }

    /**
     * Gets the separateSourceCapitalAssetNumber attribute.
     * 
     * @return Returns the separateSourceCapitalAssetNumber.
     */
    public Long getSeparateSourceCapitalAssetNumber() {
        return separateSourceCapitalAssetNumber;
    }

    /**
     * Sets the separateSourceCapitalAssetNumber attribute value.
     * 
     * @param separateSourceCapitalAssetNumber The separateSourceCapitalAssetNumber to set.
     */
    public void setSeparateSourceCapitalAssetNumber(Long separateSourceCapitalAssetNumber) {
        this.separateSourceCapitalAssetNumber = separateSourceCapitalAssetNumber;
    }

    public Integer getSeparateSourcePaymentSequenceNumber() {
        return separateSourcePaymentSequenceNumber;
    }

    public void setSeparateSourcePaymentSequenceNumber(Integer separateSourcePaymentSequenceNumber) {
        this.separateSourcePaymentSequenceNumber = separateSourcePaymentSequenceNumber;
    }

    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Small workaround to avoid KualiInquirableImpl.getInquiryUrl having think it needs to construct an inquiry url for this date.
     * This only returns a date if this is a separate.
     * 
     * @return
     */
    public Date getSeparateDocumentHeaderFinalDate() {
        if (this.documentNumber == null || !SpringContext.getBean(AssetGlobalService.class).isAssetSeparate(this)) {
            return null;
        }
//        DocumentRouteHeaderValue routeHeader = SpringContext.getBean(RouteHeaderService.class).getRouteHeader(this.documentNumber);
//        if (routeHeader != null && routeHeader.getApprovedDate() != null) {
//            return new Date(routeHeader.getApprovedDate().getTime());
//        }
//        
        String userId = GlobalVariables.getUserSession().getPrincipalId();
        WorkflowDocument workflowDocument = WorkflowDocumentFactory.loadDocument(userId, getDocumentNumber());

        // do not display not approved documents
        if (ObjectUtils.isNotNull(workflowDocument.getDateApproved())) {
            return getSqlDate(workflowDocument.getDateApproved().toCalendar(Locale.getDefault()));
        }
        
        return null;
    }

    protected Date getSqlDate(Calendar cal) {
        Date sqlDueDate = null;

        if (ObjectUtils.isNull(cal))
            return sqlDueDate;
        try {
            sqlDueDate = SpringContext.getBean(DateTimeService.class).convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        }
        catch (ParseException e) {
            // TODO: throw an error here, but don't die
        }
        return sqlDueDate;
    }

    
    public boolean isCapitalAssetBuilderOriginIndicator() {
        return capitalAssetBuilderOriginIndicator;
    }

    public void setCapitalAssetBuilderOriginIndicator(boolean capitalAssetBuilderOriginIndicator) {
        this.capitalAssetBuilderOriginIndicator = capitalAssetBuilderOriginIndicator;
    }

    /**
     * Gets the calculate equal source amounts button
     * 
     * @return
     */
    public String getCalculateEqualSourceAmountsButton() {
        return calculateEqualSourceAmountsButton;
    }

    /**
     * Gets the calculate equal source amounts button
     * 
     * @param calculateEqualSourceAmountsButton
     */
    public void setCalculateEqualSourceAmountsButton(String calculateEqualSourceAmountsButton) {
        this.calculateEqualSourceAmountsButton = calculateEqualSourceAmountsButton;
    }

    public String getCalculateSeparateSourceRemainingAmountButton() {
        return calculateSeparateSourceRemainingAmountButton;
    }

    public void setCalculateSeparateSourceRemainingAmountButton(String calculateSeparateSourceRemainingAmountButton) {
        this.calculateSeparateSourceRemainingAmountButton = calculateSeparateSourceRemainingAmountButton;
    }

    public KualiDecimal getSeparateSourceRemainingAmount() {
        if (separateSourceRemainingAmount == null && getTotalCostAmount() != null) {
            return getTotalCostAmount().subtract(getSeparateSourceTotalAmount());
        }
        return separateSourceRemainingAmount;
    }

    public void setSeparateSourceRemainingAmount(KualiDecimal separateSourceRemainingAmount) {
        this.separateSourceRemainingAmount = separateSourceRemainingAmount;
    }

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add( new ArrayList<PersistableBusinessObject>(getAssetGlobalDetails()));
        managedLists.add( new ArrayList<PersistableBusinessObject>(getAssetPaymentDetails()));
        return managedLists;
    }

    /**
     * Gets the totalAssetPaymentAmount attribute.
     * 
     * @return Returns the totalAssetPaymentAmount.
     */
    public KualiDecimal getTotalAssetPaymentAmount() {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentList = getAssetPaymentDetails();
        if (assetPaymentList != null && !assetPaymentList.isEmpty()) {
            for (AssetPaymentDetail assetPaymentDetail : assetPaymentList) {
                totalAmount = totalAmount.add(assetPaymentDetail.getAmount());
            }
        }
        return totalAmount;
    }

    /**
     * Sets the totalAssetPaymentAmount attribute value.
     * 
     * @param totalAssetPaymentAmount The totalAssetPaymentAmount to set.
     */
    public void setTotalAssetPaymentAmount(KualiDecimal totalAssetPaymentAmount) {
        this.totalAssetPaymentAmount = totalAssetPaymentAmount;
    }

    /**
     * Gets the separateSourceTotalAmount attribute value.
     * 
     * @return separateSourceTotalAmount
     */
    public KualiDecimal getSeparateSourceTotalAmount() {
        if (separateSourceTotalAmount == null) {
            this.separateSourceTotalAmount = KualiDecimal.ZERO;
            for (AssetGlobalDetail detail : this.assetGlobalDetails) {
                KualiDecimal separateSourceAmount = detail.getSeparateSourceAmount();
                if (separateSourceAmount != null) {
                    this.separateSourceTotalAmount = this.separateSourceTotalAmount.add(separateSourceAmount);
                }
            }
        }
        return separateSourceTotalAmount;
    }

    /**
     * Sets the separateSourceTotalAmount attribute value.
     * 
     * @param separateSourceTotalAmount
     */
    public void setSeparateSourceTotalAmount(KualiDecimal separateSourceTotalAmount) {
        this.separateSourceTotalAmount = separateSourceTotalAmount;
    }

    /**
     * Gets the minAssetTotalAmount attribute.
     * 
     * @return Returns the minAssetTotalAmount.
     */
    public KualiDecimal getMinAssetTotalAmount() {
        return minAssetTotalAmount;
    }

    /**
     * Sets the minAssetTotalAmount attribute value.
     * 
     * @param minAssetTotalAmount The minAssetTotalAmount to set.
     */
    public void setMinAssetTotalAmount(KualiDecimal minAssetTotalAmount) {
        this.minAssetTotalAmount = minAssetTotalAmount;
    }

    /**
     * Gets the maxAssetTotalAmount attribute.
     * 
     * @return Returns the maxAssetTotalAmount.
     */
    public KualiDecimal getMaxAssetTotalAmount() {
        return maxAssetTotalAmount;
    }

    /**
     * Sets the maxAssetTotalAmount attribute value.
     * 
     * @param maxAssetTotalAmount The maxAssetTotalAmount to set.
     */
    public void setMaxAssetTotalAmount(KualiDecimal maxAssetTotalAmount) {
        this.maxAssetTotalAmount = maxAssetTotalAmount;
    }

    
    // CSU 6702 BEGIN
    /**
     * Get Posting Year
     * @return postingYear
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * Set Posting year
     * @param postingYear
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * Get the {@link AccountingPeriodService}
     * @return {@link AccountingPeriodService}
     */
    public static AccountingPeriodService getAccountingPeriodService() {
        if ( accountingPeriodService == null ) {
            accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        }
        return accountingPeriodService;
    }
    
    
    /**
     * Creates a composite of postingPeriodCode and postingyear. 
     * @return composite or an empty string if either postingPeriodCode or postingYear is null
     */
    public String getAccountingPeriodCompositeString() {
        if (postingPeriodCode== null || postingYear == null ) {
            return "";
        }        
        return postingPeriodCode + postingYear;
    }

    /**
     * Sets the accountingPeriod if in period 13
     * @param accountingPeriodString
     * TODO remove hardcoding
     */
    public void setAccountingPeriodCompositeString(String accountingPeriodString) {
        String THIRTEEN = "13";
        if (StringUtils.isNotBlank(accountingPeriodString) && StringUtils.left(accountingPeriodString, 2).equals(THIRTEEN)) {
            String period = StringUtils.left(accountingPeriodString, 2);
            Integer year = new Integer(StringUtils.right(accountingPeriodString, 4));
            AccountingPeriod accountingPeriod = getAccountingPeriodService().getByPeriod(period, year);
            setAccountingPeriod(accountingPeriod);
        }
    }

    /**
     * Get the posting period code
     * @return postingPeriodCode
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    /**
     * Set the posting period code
     * @param postingPeriodCode
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }
    
    /**
     * Set postingYear and postingPeriodCode 
     * @param accountingPeriod
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;        
        if(ObjectUtils.isNotNull(accountingPeriod)) {
            setPostingYear(accountingPeriod.getUniversityFiscalYear());
            setPostingPeriodCode(accountingPeriod.getUniversityFiscalPeriodCode());
        }
    }
    
    /**
     * get the accountingPeriod
     * @return accountingPeriod
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }
    // CSU 6702 END    
}
