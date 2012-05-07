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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private Long mergedTargetCapitalAssetNumber;
    private String mergedTargetCapitalAssetDescription;
    private String retirementReasonCode;
    private String retirementChartOfAccountsCode;
    private String retirementAccountNumber;
    private String retirementContactName;
    private String retirementInstitutionName;
    private String retirementStreetAddress;
    private String retirementCityName;
    private String retirementStateCode;
    private String retirementZipCode;
    private String retirementCountryCode;
    private String retirementPhoneNumber;
    private KualiDecimal estimatedSellingPrice;
    private KualiDecimal salePrice;
    private String cashReceiptFinancialDocumentNumber;
    private KualiDecimal handlingFeeAmount;
    private KualiDecimal preventiveMaintenanceAmount;
    private String buyerDescription;
    private String paidCaseNumber;
    // persistent relationship
    private Date retirementDate;
    private Asset mergedTargetCapitalAsset;
    private AssetRetirementReason retirementReason;
    private FinancialSystemDocumentHeader documentHeader;
    private List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails;
    private Account retirementAccount;
    private Chart retirementChartOfAccounts;
    private DocumentHeader cashReceiptFinancialDocument;
    private StateEbo retirementState;
    private CountryEbo retirementCountry;
    private PostalCodeEbo postalZipCode;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;

    private Integer postingYear;
    private String postingPeriodCode;
    private AccountingPeriod accountingPeriod;
    static protected transient AccountingPeriodService accountingPeriodService;
    
    // Non-persistent
    private KualiDecimal calculatedTotal;

    /**
     * Default constructor.
     */
    public AssetRetirementGlobal() {
        this.assetRetirementGlobalDetails = new ArrayList<AssetRetirementGlobalDetail>();
        this.generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }


    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        AssetRetirementService retirementService = SpringContext.getBean(AssetRetirementService.class);

        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        if (retirementService.isAssetRetiredByMerged(this) && mergedTargetCapitalAsset != null) {
            setMergeObjectsForPersist(persistables, retirementService);
        }

        for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
            setAssetForPersist(detail.getAsset(), persistables, retirementService);
        }

        return persistables;
    }

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedList = super.buildListOfDeletionAwareLists();
        managedList.add(new ArrayList<PersistableBusinessObject>(getAssetRetirementGlobalDetails()));
        return managedList;
    }

    /**
     * This method set asset fields for update
     * 
     * @param detail
     * @param persistables
     */
    private void setAssetForPersist(Asset asset, List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        // load the object by key
        asset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED);
        asset.setRetirementReasonCode(retirementReasonCode);

        // set retirement fiscal year and period code into asset
        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        if (ObjectUtils.isNotNull(currentUniversityDate)) {
            asset.setRetirementFiscalYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
            asset.setRetirementPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
        }


        if (retirementService.isAssetRetiredByTheft(this) && StringUtils.isNotBlank(this.getPaidCaseNumber())) {
            asset.setCampusPoliceDepartmentCaseNumber(this.getPaidCaseNumber());
        }
        else if (retirementService.isAssetRetiredBySold(this) || retirementService.isAssetRetiredByAuction(this)) {
            asset.setRetirementChartOfAccountsCode(this.getRetirementChartOfAccountsCode());
            asset.setRetirementAccountNumber(this.getRetirementAccountNumber());
            asset.setCashReceiptFinancialDocumentNumber(this.getCashReceiptFinancialDocumentNumber());
            asset.setSalePrice(this.getSalePrice());
            asset.setEstimatedSellingPrice(this.getEstimatedSellingPrice());
        }
        else if (retirementService.isAssetRetiredByMerged(this)) {
            asset.setTotalCostAmount(KualiDecimal.ZERO);
            asset.setSalvageAmount(KualiDecimal.ZERO);
        }
        else if (retirementService.isAssetRetiredByExternalTransferOrGift(this)) {
            persistables.add(setOffCampusLocationObjectsForPersist(asset));
        }
        asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        persistables.add(asset);
    }

    /**
     * This method set off campus location for persist
     * 
     * @param AssetGlobalDetail and Asset to populate AssetLocation
     * @return Returns the AssetLocation.
     */
    private AssetLocation setOffCampusLocationObjectsForPersist(Asset asset) {
        AssetLocation offCampusLocation = new AssetLocation();
        offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.RETIREMENT);
        offCampusLocation = (AssetLocation) SpringContext.getBean(BusinessObjectService.class).retrieve(offCampusLocation);
        if (offCampusLocation == null) {
            offCampusLocation = new AssetLocation();
            offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.RETIREMENT);
            asset.getAssetLocations().add(offCampusLocation);
        }

        offCampusLocation.setAssetLocationContactName(this.getRetirementContactName());
        offCampusLocation.setAssetLocationInstitutionName(this.getRetirementInstitutionName());
        offCampusLocation.setAssetLocationPhoneNumber(this.getRetirementPhoneNumber());
        offCampusLocation.setAssetLocationStreetAddress(this.getRetirementStreetAddress());
        offCampusLocation.setAssetLocationCityName(this.getRetirementCityName());
        offCampusLocation.setAssetLocationStateCode(this.getRetirementStateCode());
        offCampusLocation.setAssetLocationCountryCode(this.getRetirementCountryCode());
        offCampusLocation.setAssetLocationZipCode(this.getRetirementZipCode());

        return offCampusLocation;
    }

    /**
     * This method set target payment and source payment; set target/source asset salvageAmount/totalCostAmount
     * 
     * @param persistables
     */
    private void setMergeObjectsForPersist(List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        AssetPaymentService assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        Integer maxTargetSequenceNo = assetPaymentService.getMaxSequenceNumber(mergedTargetCapitalAssetNumber);

        KualiDecimal salvageAmount = KualiDecimal.ZERO;
        KualiDecimal totalCostAmount = KualiDecimal.ZERO;
        Asset sourceAsset;

        // update for each merge source asset
        for (AssetRetirementGlobalDetail detail : getAssetRetirementGlobalDetails()) {
            detail.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);
            sourceAsset = detail.getAsset();

            totalCostAmount = totalCostAmount.add(paymentSummaryService.calculatePaymentTotalCost(sourceAsset));
            salvageAmount = salvageAmount.add(sourceAsset.getSalvageAmount());

            retirementService.generateOffsetPaymentsForEachSource(sourceAsset, persistables, detail.getDocumentNumber());
            maxTargetSequenceNo = retirementService.generateNewPaymentForTarget(mergedTargetCapitalAsset, sourceAsset, persistables, maxTargetSequenceNo, detail.getDocumentNumber());

        }
        KualiDecimal mergedTargetSalvageAmount = (mergedTargetCapitalAsset.getSalvageAmount() != null ? mergedTargetCapitalAsset.getSalvageAmount() : KualiDecimal.ZERO);

        // update merget target asset
        mergedTargetCapitalAsset.setTotalCostAmount(totalCostAmount.add(paymentSummaryService.calculatePaymentTotalCost(mergedTargetCapitalAsset)));
        mergedTargetCapitalAsset.setSalvageAmount(salvageAmount.add(mergedTargetSalvageAmount));
        mergedTargetCapitalAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        mergedTargetCapitalAsset.setCapitalAssetDescription(this.getMergedTargetCapitalAssetDescription());
        persistables.add(mergedTargetCapitalAsset);
    }


    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetRetirementGlobalDetails();
    }

    public boolean isPersistable() {
        return true;
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
     * Gets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @return Returns the mergedTargetCapitalAssetNumber
     */
    public Long getMergedTargetCapitalAssetNumber() {
        return mergedTargetCapitalAssetNumber;
    }

    /**
     * Sets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @param mergedTargetCapitalAssetNumber The mergedTargetCapitalAssetNumber to set.
     */
    public void setMergedTargetCapitalAssetNumber(Long mergedTargetCapitalAssetNumber) {
        this.mergedTargetCapitalAssetNumber = mergedTargetCapitalAssetNumber;
    }


    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementDate attribute.
     * 
     * @return Returns the retirementDate
     */
    public Date getRetirementDate() {
        return retirementDate;
    }

    /**
     * Sets the retirementDate attribute.
     * 
     * @param retirementDate The retirementDate to set.
     */
    public void setRetirementDate(Date remeretirementDatentDate) {
        this.retirementDate = remeretirementDatentDate;
    }

    /**
     * Gets the mergedTargetCapitalAsset attribute.
     * 
     * @return Returns the mergedTargetCapitalAsset.
     */
    public Asset getMergedTargetCapitalAsset() {
        return mergedTargetCapitalAsset;
    }

    /**
     * Sets the mergedTargetCapitalAsset attribute value.
     * 
     * @param mergedTargetCapitalAsset The mergedTargetCapitalAsset to set.
     * @deprecated
     */
    public void setMergedTargetCapitalAsset(Asset mergedTargetCapitalAsset) {
        this.mergedTargetCapitalAsset = mergedTargetCapitalAsset;
    }

    /**
     * Gets the retirementReason attribute.
     * 
     * @return Returns the retirementReason.
     */
    public AssetRetirementReason getRetirementReason() {
        return retirementReason;
    }

    /**
     * Sets the retirementReason attribute value.
     * 
     * @param retirementReason The retirementReason to set.
     * @deprecated
     */
    public void setRetirementReason(AssetRetirementReason retirementReason) {
        this.retirementReason = retirementReason;
    }


    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public List<AssetRetirementGlobalDetail> getAssetRetirementGlobalDetails() {
        return assetRetirementGlobalDetails;
    }

    public void setAssetRetirementGlobalDetails(List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails) {
        this.assetRetirementGlobalDetails = assetRetirementGlobalDetails;
    }


    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }


    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> glPendingEntries) {
        this.generalLedgerPendingEntries = glPendingEntries;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }


    public String getMergedTargetCapitalAssetDescription() {
        return mergedTargetCapitalAssetDescription;
    }


    public void setMergedTargetCapitalAssetDescription(String mergedTargetCapitalAssetDescription) {
        this.mergedTargetCapitalAssetDescription = mergedTargetCapitalAssetDescription;
    }

    /**
     * Gets the retirementChartOfAccountsCode attribute.
     * 
     * @return Returns the retirementChartOfAccountsCode
     */
    public String getRetirementChartOfAccountsCode() {
        return retirementChartOfAccountsCode;
    }

    /**
     * Sets the retirementChartOfAccountsCode attribute.
     * 
     * @param retirementChartOfAccountsCode The retirementChartOfAccountsCode to set.
     */
    public void setRetirementChartOfAccountsCode(String retirementChartOfAccountsCode) {
        this.retirementChartOfAccountsCode = retirementChartOfAccountsCode;
    }


    /**
     * Gets the retirementAccountNumber attribute.
     * 
     * @return Returns the retirementAccountNumber
     */
    public String getRetirementAccountNumber() {
        return retirementAccountNumber;
    }

    /**
     * Sets the retirementAccountNumber attribute.
     * 
     * @param retirementAccountNumber The retirementAccountNumber to set.
     */
    public void setRetirementAccountNumber(String retirementAccountNumber) {
        this.retirementAccountNumber = retirementAccountNumber;
    }


    /**
     * Gets the retirementContactName attribute.
     * 
     * @return Returns the retirementContactName
     */
    public String getRetirementContactName() {
        return retirementContactName;
    }

    /**
     * Sets the retirementContactName attribute.
     * 
     * @param retirementContactName The retirementContactName to set.
     */
    public void setRetirementContactName(String retirementContactName) {
        this.retirementContactName = retirementContactName;
    }


    /**
     * Gets the retirementInstitutionName attribute.
     * 
     * @return Returns the retirementInstitutionName
     */
    public String getRetirementInstitutionName() {
        return retirementInstitutionName;
    }

    /**
     * Sets the retirementInstitutionName attribute.
     * 
     * @param retirementInstitutionName The retirementInstitutionName to set.
     */
    public void setRetirementInstitutionName(String retirementInstitutionName) {
        this.retirementInstitutionName = retirementInstitutionName;
    }


    /**
     * Gets the retirementStreetAddress attribute.
     * 
     * @return Returns the retirementStreetAddress
     */
    public String getRetirementStreetAddress() {
        return retirementStreetAddress;
    }

    /**
     * Sets the retirementStreetAddress attribute.
     * 
     * @param retirementStreetAddress The retirementStreetAddress to set.
     */
    public void setRetirementStreetAddress(String retirementStreetAddress) {
        this.retirementStreetAddress = retirementStreetAddress;
    }


    /**
     * Gets the retirementCityName attribute.
     * 
     * @return Returns the retirementCityName
     */
    public String getRetirementCityName() {
        return retirementCityName;
    }

    /**
     * Sets the retirementCityName attribute.
     * 
     * @param retirementCityName The retirementCityName to set.
     */
    public void setRetirementCityName(String retirementCityName) {
        this.retirementCityName = retirementCityName;
    }


    /**
     * Gets the retirementStateCode attribute.
     * 
     * @return Returns the retirementStateCode
     */
    public String getRetirementStateCode() {
        return retirementStateCode;
    }

    /**
     * Sets the retirementStateCode attribute.
     * 
     * @param retirementStateCode The retirementStateCode to set.
     */
    public void setRetirementStateCode(String retirementStateCode) {
        this.retirementStateCode = retirementStateCode;
    }


    /**
     * Gets the retirementZipCode attribute.
     * 
     * @return Returns the retirementZipCode
     */
    public String getRetirementZipCode() {
        return retirementZipCode;
    }

    /**
     * Sets the retirementZipCode attribute.
     * 
     * @param retirementZipCode The retirementZipCode to set.
     */
    public void setRetirementZipCode(String retirementZipCode) {
        this.retirementZipCode = retirementZipCode;
    }


    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode
     */
    public PostalCodeEbo getPostalZipCode() {
        if ( StringUtils.isBlank(retirementCountryCode) || StringUtils.isBlank(retirementZipCode) ) {
            postalZipCode = null;
        } else {
            if ( postalZipCode == null || !StringUtils.equals( postalZipCode.getCode(), retirementZipCode) || !StringUtils.equals(postalZipCode.getCountyCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, retirementCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, retirementZipCode);
                    postalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return postalZipCode;
      }

    /**
     * Sets the postalZipCode attribute.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCodeEbo postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the retirementCountryCode attribute.
     * 
     * @return Returns the retirementCountryCode
     */
    public String getRetirementCountryCode() {
        return retirementCountryCode;
    }

    /**
     * Sets the retirementCountryCode attribute.
     * 
     * @param retirementCountryCode The retirementCountryCode to set.
     */
    public void setRetirementCountryCode(String retirementCountryCode) {
        this.retirementCountryCode = retirementCountryCode;
    }


    /**
     * Gets the retirementPhoneNumber attribute.
     * 
     * @return Returns the retirementPhoneNumber
     */
    public String getRetirementPhoneNumber() {
        return retirementPhoneNumber;
    }

    /**
     * Sets the retirementPhoneNumber attribute.
     * 
     * @param retirementPhoneNumber The retirementPhoneNumber to set.
     */
    public void setRetirementPhoneNumber(String retirementPhoneNumber) {
        this.retirementPhoneNumber = retirementPhoneNumber;
    }


    /**
     * Gets the estimatedSellingPrice attribute.
     * 
     * @return Returns the estimatedSellingPrice
     */
    public KualiDecimal getEstimatedSellingPrice() {
        return estimatedSellingPrice;
    }

    /**
     * Sets the estimatedSellingPrice attribute.
     * 
     * @param estimatedSellingPrice The estimatedSellingPrice to set.
     */
    public void setEstimatedSellingPrice(KualiDecimal estimatedSellingPrice) {
        this.estimatedSellingPrice = estimatedSellingPrice;
    }


    /**
     * Gets the salePrice attribute.
     * 
     * @return Returns the salePrice
     */
    public KualiDecimal getSalePrice() {
        return salePrice;
    }

    /**
     * Sets the salePrice attribute.
     * 
     * @param salePrice The salePrice to set.
     */
    public void setSalePrice(KualiDecimal salePrice) {
        this.salePrice = salePrice;
    }


    /**
     * Gets the cashReceiptFinancialDocumentNumber attribute.
     * 
     * @return Returns the cashReceiptFinancialDocumentNumber
     */
    public String getCashReceiptFinancialDocumentNumber() {
        return cashReceiptFinancialDocumentNumber;
    }

    /**
     * Sets the cashReceiptFinancialDocumentNumber attribute.
     * 
     * @param cashReceiptFinancialDocumentNumber The cashReceiptFinancialDocumentNumber to set.
     */
    public void setCashReceiptFinancialDocumentNumber(String cashReceiptFinancialDocumentNumber) {
        this.cashReceiptFinancialDocumentNumber = cashReceiptFinancialDocumentNumber;
    }


    /**
     * Gets the handlingFeeAmount attribute.
     * 
     * @return Returns the handlingFeeAmount
     */
    public KualiDecimal getHandlingFeeAmount() {
        return handlingFeeAmount;
    }

    /**
     * Sets the handlingFeeAmount attribute.
     * 
     * @param handlingFeeAmount The handlingFeeAmount to set.
     */
    public void setHandlingFeeAmount(KualiDecimal handlingFeeAmount) {
        this.handlingFeeAmount = handlingFeeAmount;
    }


    /**
     * Gets the preventiveMaintenanceAmount attribute.
     * 
     * @return Returns the preventiveMaintenanceAmount
     */
    public KualiDecimal getPreventiveMaintenanceAmount() {
        return preventiveMaintenanceAmount;
    }

    /**
     * Sets the preventiveMaintenanceAmount attribute.
     * 
     * @param preventiveMaintenanceAmount The preventiveMaintenanceAmount to set.
     */
    public void setPreventiveMaintenanceAmount(KualiDecimal preventiveMaintenanceAmount) {
        this.preventiveMaintenanceAmount = preventiveMaintenanceAmount;
    }


    /**
     * Gets the buyerDescription attribute.
     * 
     * @return Returns the buyerDescription
     */
    public String getBuyerDescription() {
        return buyerDescription;
    }

    /**
     * Sets the buyerDescription attribute.
     * 
     * @param buyerDescription The buyerDescription to set.
     */
    public void setBuyerDescription(String buyerDescription) {
        this.buyerDescription = buyerDescription;
    }


    /**
     * Gets the paidCaseNumber attribute.
     * 
     * @return Returns the paidCaseNumber
     */
    public String getPaidCaseNumber() {
        return paidCaseNumber;
    }

    /**
     * Sets the paidCaseNumber attribute.
     * 
     * @param paidCaseNumber The paidCaseNumber to set.
     */
    public void setPaidCaseNumber(String paidCaseNumber) {
        this.paidCaseNumber = paidCaseNumber;
    }

    /**
     * Gets the retirementChartOfAccounts attribute.
     * 
     * @return Returns the retirementChartOfAccounts
     */
    public Chart getRetirementChartOfAccounts() {
        return retirementChartOfAccounts;
    }

    /**
     * Sets the retirementChartOfAccounts attribute.
     * 
     * @param retirementChartOfAccounts The retirementChartOfAccounts to set.
     * @deprecated
     */
    public void setRetirementChartOfAccounts(Chart retirementChartOfAccounts) {
        this.retirementChartOfAccounts = retirementChartOfAccounts;
    }

    /**
     * Gets the retirementAccount attribute.
     * 
     * @return Returns the retirementAccount
     */
    public Account getRetirementAccount() {
        return retirementAccount;
    }

    /**
     * Sets the retirementAccount attribute.
     * 
     * @param retirementAccount The retirementAccount to set.
     * @deprecated
     */
    public void setRetirementAccount(Account retirementAccount) {
        this.retirementAccount = retirementAccount;
    }


    /**
     * Gets the cashReceiptFinancialDocument attribute.
     * 
     * @return Returns the cashReceiptFinancialDocument.
     */
    public DocumentHeader getCashReceiptFinancialDocument() {
        return cashReceiptFinancialDocument;
    }

    /**
     * Sets the cashReceiptFinancialDocument attribute value.
     * 
     * @param cashReceiptFinancialDocument The cashReceiptFinancialDocument to set.
     * @deprecated
     */
    public void setCashReceiptFinancialDocument(DocumentHeader cashReceiptFinancialDocument) {
        this.cashReceiptFinancialDocument = cashReceiptFinancialDocument;
    }


    /**
     * Gets the retirementCountry attribute.
     * 
     * @return Returns the retirementCountry.
     */
    public CountryEbo getRetirementCountry() {
        if ( StringUtils.isBlank(retirementCountryCode) ) {
            retirementCountry = null;
        } else {
            if ( retirementCountry == null || !StringUtils.equals( retirementCountry.getCode(), retirementCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, retirementCountryCode);
                    retirementCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return retirementCountry;
    }
    
    /**
     * Sets the retirementCountry attribute value.
     * 
     * @param retirementCountry The retirementCountry to set.
     * @deprecated
     */
    public void setRetirementCountry(CountryEbo retirementCountry) {
        this.retirementCountry = retirementCountry;
    }

    /**
     * Gets the retirementState attribute.
     * 
     * @return Returns the retirementState.
     */
    public StateEbo getRetirementState() {
        if ( StringUtils.isBlank(retirementStateCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            retirementState = null;
        } else {
            if ( retirementState == null || !StringUtils.equals( retirementState.getCode(), retirementStateCode) || !StringUtils.equals(retirementState.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, retirementStateCode);
                    retirementState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return retirementState;
    }
    
    /**
     * Sets the retirementState attribute value.
     * 
     * @param retirementState The retirementState to set.
     * @deprecated
     */
    public void setRetirementState(StateEbo retirementState) {
        this.retirementState = retirementState;
    }


    /**
     * Gets the calculatedTotal attribute.
     * 
     * @return Returns the calculatedTotal.
     */
    public KualiDecimal getCalculatedTotal() {
        this.calculatedTotal = KualiDecimal.ZERO;
        if (this.handlingFeeAmount != null) {
            this.calculatedTotal = calculatedTotal.add(this.handlingFeeAmount);
        }
        if (this.preventiveMaintenanceAmount != null) {
            this.calculatedTotal = calculatedTotal.add(this.preventiveMaintenanceAmount);
        }
        if (this.salePrice != null) {
            this.calculatedTotal = calculatedTotal.add(this.salePrice);
        }
        return calculatedTotal;
    }
    
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

}
