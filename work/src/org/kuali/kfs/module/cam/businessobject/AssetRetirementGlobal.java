package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

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
    private State retirementState;
    private Country retirementCountry;
    private PostalCode postalZipCode;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;

    // Non-persistent
    private KualiDecimal calculatedTotal;
    private transient String hiddenFieldForError;

    /**
     * Default constructor.
     */
    public AssetRetirementGlobal() {
        this.assetRetirementGlobalDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);
        this.generalLedgerPendingEntries = new TypedArrayList(GeneralLedgerPendingEntry.class);
    }


    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
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
    public List buildListOfDeletionAwareLists() {
        List<List> managedList = super.buildListOfDeletionAwareLists();

        managedList.add(getAssetRetirementGlobalDetails());

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
        // TODO Auto-generated method stub
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
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
    public PostalCode getPostalZipCode() {
        postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(this, retirementCountryCode, retirementZipCode, postalZipCode);
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCode postalZipCode) {
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
    public Country getRetirementCountry() {
        retirementCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, retirementCountryCode, retirementCountry);
        return retirementCountry;
    }

    /**
     * Sets the retirementCountry attribute value.
     * 
     * @param retirementCountry The retirementCountry to set.
     * @deprecated
     */
    public void setRetirementCountry(Country retirementCountry) {
        this.retirementCountry = retirementCountry;
    }

    /**
     * Gets the retirementState attribute.
     * 
     * @return Returns the retirementState.
     */
    public State getRetirementState() {
        retirementState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, retirementCountryCode, retirementStateCode, retirementState);
        return retirementState;
    }

    /**
     * Sets the retirementState attribute value.
     * 
     * @param retirementState The retirementState to set.
     * @deprecated
     */
    public void setRetirementState(State retirementState) {
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
        if (this.estimatedSellingPrice != null) {
            this.calculatedTotal = calculatedTotal.add(this.estimatedSellingPrice);
        }
        return calculatedTotal;
    }


    /**
     * Gets the hiddenFieldForError attribute.
     * 
     * @return Returns the hiddenFieldForError.
     */
    public String getHiddenFieldForError() {
        return hiddenFieldForError;
    }


    /**
     * Sets the hiddenFieldForError attribute value.
     * 
     * @param hiddenFieldForError The hiddenFieldForError to set.
     */
    public void setHiddenFieldForError(String hiddenFieldForError) {
        this.hiddenFieldForError = hiddenFieldForError;
    }


}
