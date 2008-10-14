package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants.CAPITAL_ASSETS_BATCH;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

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
    private String primaryDepreciationMethodCode;
    private KualiDecimal primaryDepreciationBaseAmount;
    private String acquisitionSourceName;
    private String capitalAssetTypeCode;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private Date capitalizationDate;
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

    // Not Persisted
    private Date lastInventoryDate;
    private ContractsAndGrantsAgency agency;
    private UniversalUser assetRepresentative;
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

    // calculate equal totals button
    private String calculateEqualSourceAmountsButton;
    // calculate remaining source amount
    private String calculateSeparateSourceRemainingAmountButton;
    private KualiDecimal separateSourceRemainingAmount;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;
    private FinancialSystemDocumentHeader documentHeader;

    /**
     * Default constructor.
     */
    public AssetGlobal() {
        assetGlobalDetails = new TypedArrayList(AssetGlobalDetail.class);
        assetSharedDetails = new TypedArrayList(AssetGlobalDetail.class);
        assetPaymentDetails = new TypedArrayList(AssetPaymentDetail.class);
        this.generalLedgerPendingEntries = new TypedArrayList(GeneralLedgerPendingEntry.class);
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
     * Gets the primaryDepreciationMethodCode attribute.
     * 
     * @return Returns the primaryDepreciationMethodCode
     */
    public String getPrimaryDepreciationMethodCode() {
        return primaryDepreciationMethodCode;
    }

    /**
     * Sets the primaryDepreciationMethodCode attribute.
     * 
     * @param primaryDepreciationMethodCode The primaryDepreciationMethodCode to set.
     */
    public void setPrimaryDepreciationMethodCode(String primaryDepreciationMethodCode) {
        this.primaryDepreciationMethodCode = primaryDepreciationMethodCode;
    }


    /**
     * Gets the primaryDepreciationBaseAmount attribute.
     * 
     * @return Returns the primaryDepreciationBaseAmount
     */
    public KualiDecimal getPrimaryDepreciationBaseAmount() {
        return primaryDepreciationBaseAmount;
    }

    /**
     * Sets the primaryDepreciationBaseAmount attribute.
     * 
     * @param primaryDepreciationBaseAmount The primaryDepreciationBaseAmount to set.
     */
    public void setPrimaryDepreciationBaseAmount(KualiDecimal primaryDepreciationBaseAmount) {
        this.primaryDepreciationBaseAmount = primaryDepreciationBaseAmount;
    }


    /**
     * Gets the acquisitionSourceName attribute.
     * 
     * @return Returns the acquisitionSourceName
     */
    public String getAcquisitionSourceName() {
        return acquisitionSourceName;
    }

    /**
     * Sets the acquisitionSourceName attribute.
     * 
     * @param acquisitionSourceName The acquisitionSourceName to set.
     */
    public void setAcquisitionSourceName(String acquisitionSourceName) {
        this.acquisitionSourceName = acquisitionSourceName;
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
     * Gets the capitalizationDate attribute.
     * 
     * @return Returns the capitalizationDate
     */
    public Date getCapitalizationDate() {
        return capitalizationDate;
    }

    /**
     * Sets the capitalizationDate attribute.
     * 
     * @param capitalizationDate The capitalizationDate to set.
     */
    public void setCapitalizationDate(Date capitalizationDate) {
        this.capitalizationDate = capitalizationDate;
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
     * This returns a list of Object Codes to Update and/or Add
     * 
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist() becomes an asset
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {

        // LOG.info("called generateGlobalChangesToPersist()....");

        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
        String financialObjectSubTypeCode = null;
        int newAssetCount = 0;
        KualiDecimal depreciationPaymentAmount = KualiDecimal.ZERO;
        KualiDecimal actualDepreciationAmount = KualiDecimal.ZERO;
        boolean isDepreciablePayment = false;
        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        AssetPaymentService assetPaymentService = SpringContext.getBean(AssetPaymentService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        if (!assetPaymentDetails.isEmpty() && ObjectUtils.isNotNull(assetPaymentDetails.get(0).getObjectCode())) {
            financialObjectSubTypeCode = assetPaymentDetails.get(0).getObjectCode().getFinancialObjectSubTypeCode();
            // LOG.info("financialObjectSubTypeCode: '" + financialObjectSubTypeCode + "'");
        }

        // LOG.info("set payment detail data...");
        // Setting the postingYear and month
        for (AssetPaymentDetail assetPaymentDetail : this.getAssetPaymentDetails()) {
            // LOG.info("in assetPaymentDetail loop: key: '" + assetPaymentDetail.getAccountKey() + "'");
            // LOG.info("in assetPaymentDetail loop: amt: '" + assetPaymentDetail.getAmount() + "'");
            if (assetGlobalService.existsInGroup(CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_CODE_GROUP, getAcquisitionTypeCode())) {
                UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
                assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(currentUniversityDate.getUniversityDate());
                assetPaymentDetail.setPostingYear(currentUniversityDate.getUniversityFiscalYear());
                assetPaymentDetail.setPostingPeriodCode(currentUniversityDate.getUniversityFiscalAccountingPeriod());
            }
            else {
                assetPaymentService.extractPostedDatePeriod(assetPaymentDetail);
            }
            persistables.add(assetPaymentDetail);
        }

        // LOG.info("set asset global detail data...");
        // set new asset data
        for (Iterator iterator = assetGlobalDetails.iterator(); iterator.hasNext();) {

            AssetGlobalDetail detail = (AssetGlobalDetail) iterator.next();

            /** @TODO check into a better way to do the below other then getting / setting a dozen fields -- deepCopy? */

            // Asset never exists since per location we don't look up Asset numbers.
            Asset asset = new Asset();
            // AssetPayment assetPayment = new AssetPayment();
            // newAssetGlobalDetail = (AssetGlobalDetail) ObjectUtils.deepCopy(locationDetail);
            // LOG.info("in assetGlobalDetail loop: asset number: '" + detail.getCapitalAssetNumber() + "'");
            asset.setCapitalAssetNumber(detail.getCapitalAssetNumber());
            asset.setCapitalAssetDescription(capitalAssetDescription);
            asset.setInventoryStatusCode(inventoryStatusCode);
            asset.setCapitalAssetTypeCode(capitalAssetTypeCode);
            asset.setConditionCode(conditionCode);
            asset.setAcquisitionTypeCode(acquisitionTypeCode);
            asset.setPrimaryDepreciationMethodCode(primaryDepreciationMethodCode); // ??
            asset.setManufacturerName(manufacturerName);
            asset.setManufacturerModelNumber(manufacturerModelNumber);
            asset.setLandCountyName(landCountyName);
            asset.setLandAcreageSize(landAcreageSize);
            asset.setLandParcelNumber(landParcelNumber);
            asset.setVendorName(vendorName);
            asset.setOrganizationOwnerAccountNumber(organizationOwnerAccountNumber);
            asset.setOrganizationOwnerChartOfAccountsCode(organizationOwnerChartOfAccountsCode);
            asset.setAgencyNumber(agencyNumber);
            asset.setRepresentativeUniversalIdentifier(representativeUniversalIdentifier);
            asset.setCapitalAssetInServiceDate(capitalAssetInServiceDate);
            asset.setDepreciationDate(capitalAssetDepreciationDate);
            asset.setCreateDate(createDate);
            asset.setCampusCode(detail.getCampusCode());
            asset.setBuildingCode(detail.getBuildingCode());
            asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
            asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
            asset.setActive(true);
            asset.setTotalCostAmount(assetGlobalService.totalPaymentByAsset(this, !iterator.hasNext()));
            asset.setFinancialObjectSubTypeCode(financialObjectSubTypeCode);
            asset.setFinancialDocumentPostingYear(SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate().getUniversityFiscalYear());
            asset.setFinancialDocumentPostingPeriodCode(SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());

            asset.setSerialNumber(detail.getSerialNumber());
            asset.setOrganizationInventoryName(detail.getOrganizationInventoryName());
            asset.setGovernmentTagNumber(detail.getGovernmentTagNumber());
            asset.setCampusTagNumber(detail.getCampusTagNumber());
            asset.setNationalStockNumber(detail.getNationalStockNumber());
            asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
            // set specific values for new asset if document is Asset Separate
            // capitalAssetTypeCode is over written from above
            if (assetGlobalService.isAssetSeparateDocument(this)) {
                asset.setRepresentativeUniversalIdentifier(detail.getRepresentativeUniversalIdentifier());
                asset.setCapitalAssetTypeCode(detail.getCapitalAssetTypeCode());
                asset.setCapitalAssetDescription(detail.getCapitalAssetDescription());
                asset.setManufacturerName(detail.getManufacturerName());
                asset.setManufacturerModelNumber(detail.getManufacturerModelNumber());
                // set AssetOrganization data
                AssetOrganization assetOrganization = new AssetOrganization();
                assetOrganization.setCapitalAssetNumber(detail.getCapitalAssetNumber());
                assetOrganization.setOrganizationText(detail.getOrganizationText());
                assetOrganization.setOrganizationAssetTypeIdentifier(detail.getOrganizationAssetTypeIdentifier());
                asset.setAssetOrganization(assetOrganization);
            }
            else {
                // set AssetOrganization data, this time from the main object though since it's not coming from separate
                AssetOrganization assetOrganization = new AssetOrganization();
                assetOrganization.setCapitalAssetNumber(detail.getCapitalAssetNumber());
                assetOrganization.setOrganizationText(this.getOrganizationText());
                assetOrganization.setOrganizationAssetTypeIdentifier(detail.getOrganizationAssetTypeIdentifier());
                asset.setAssetOrganization(assetOrganization);
            }

            // create off campus location for each detail records
            boolean offCampus = StringUtils.isNotBlank(detail.getOffCampusName()) || StringUtils.isNotBlank(detail.getOffCampusAddress()) || StringUtils.isNotBlank(detail.getOffCampusCityName()) || StringUtils.isNotBlank(detail.getOffCampusStateCode()) || StringUtils.isNotBlank(detail.getOffCampusZipCode()) || StringUtils.isNotBlank(detail.getOffCampusCountryCode());
            if (offCampus) {
                AssetLocation offCampusLocation = setOffCampusLocationObjectsForPersist(detail, asset);
                persistables.add(asset);
                persistables.add(offCampusLocation);
            }
            else {
                persistables.add(asset);
            }
        }

        // LOG.info("setup asset payment detail....");
        // set new AssetPayment(s) from each AssetPaymentDetails
        for (AssetPaymentDetail payment : assetPaymentDetails) {
            newAssetCount = assetGlobalDetails.size();
            // LOG.info("in assetPaymentDetail loop: newAssetCount: '" + newAssetCount + "'");
            isDepreciablePayment = false;
            depreciationPaymentAmount = payment.getAmount();
            // LOG.info("in assetPaymentDetail loop: depreciationPaymentAmount: '" + depreciationPaymentAmount + "'");
            if (ObjectUtils.isNotNull(payment.getObjectCode()) && !Arrays.asList(parameterService.getParameterValue(CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES).split(";")).contains(payment.getObjectCode().getFinancialObjectSubTypeCode())) {
                isDepreciablePayment = true;
                actualDepreciationAmount = depreciationPaymentAmount.divide(new KualiDecimal(newAssetCount));
            }

            // LOG.info("setup location detail....");
            // Distribute asset payments from AssetPaymentDetails to AssetPayment
            // Divide each payment to records in Asset AssetGlobalDetails
            for (AssetGlobalDetail location : assetGlobalDetails) {
                AssetPayment assetPayment = new AssetPayment();
                //LOG.info("in location loop: asset:seq: '" + location.getCapitalAssetNumber() + ":" + payment.getSequenceNumber() + "'");
                assetPayment.setCapitalAssetNumber(location.getCapitalAssetNumber());
                assetPayment.setPaymentSequenceNumber(payment.getSequenceNumber());
                assetPayment.setChartOfAccountsCode(payment.getChartOfAccountsCode());
                // LOG.info("in location loop: account: '" + payment.getAccountNumber() + "'");
                assetPayment.setAccountNumber(payment.getAccountNumber());
                assetPayment.setSubAccountNumber(payment.getSubAccountNumber());
                assetPayment.setFinancialObjectCode(payment.getFinancialObjectCode());
                assetPayment.setFinancialSubObjectCode(payment.getFinancialSubObjectCode());
                assetPayment.setProjectCode(payment.getProjectCode());
                assetPayment.setOrganizationReferenceId(payment.getOrganizationReferenceId());
                assetPayment.setFinancialSystemOriginationCode(payment.getExpenditureFinancialSystemOriginationCode());
                assetPayment.setDocumentNumber(payment.getExpenditureFinancialDocumentNumber());
                assetPayment.setFinancialDocumentTypeCode(payment.getExpenditureFinancialDocumentTypeCode());
                assetPayment.setRequisitionNumber(payment.getRequisitionNumber());
                assetPayment.setPurchaseOrderNumber(payment.getPurchaseOrderNumber());

                if (assetGlobalService.existsInGroup(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE, acquisitionTypeCode)) {
                    assetPayment.setFinancialDocumentPostingDate(payment.getExpenditureFinancialDocumentPostedDate());
                    assetPayment.setFinancialDocumentPostingYear(payment.getPostingYear());
                    assetPayment.setFinancialDocumentPostingPeriodCode(payment.getPostingPeriodCode());
                }
                else {
                    UniversityDate currentUniversityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
                    assetPayment.setFinancialDocumentPostingDate(currentUniversityDate.getUniversityDate());
                    assetPayment.setFinancialDocumentPostingYear(currentUniversityDate.getUniversityFiscalYear());
                    assetPayment.setFinancialDocumentPostingPeriodCode(currentUniversityDate.getUniversityFiscalAccountingPeriod());
                }


                // !!!! don't modify existing asset payments !!!!!
                // new payment is created for each existing payment containing the new separate source amount

                /* ORIGINAL - 10/13/08
                // set specific values for new assets if document is Asset Separate
                if (assetGlobalService.isAssetSeparateDocument(this)) {
                    assetPayment.setAccountChargeAmount(payment.getAmount().subtract(location.getSeparateSourceAmount()));
                    assetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
                    assetPayment.setPrimaryDepreciationBaseAmount(primaryDepreciationBaseAmount);
                }
                else {
                 */
                // LEO - needed for Asset Global doc
                if (isDepreciablePayment) {
                    if (newAssetCount == 1) {
                        assetPayment.setAccountChargeAmount(depreciationPaymentAmount);
                        depreciationPaymentAmount = KualiDecimal.ZERO;
                    }
                    else {
                        assetPayment.setAccountChargeAmount(actualDepreciationAmount);
                        depreciationPaymentAmount = depreciationPaymentAmount.subtract(actualDepreciationAmount);
                        newAssetCount -= 1;
                    }
                }
                else {
                    assetPayment.setAccountChargeAmount(KualiDecimal.ZERO);
                }
                assetPayment.setPrimaryDepreciationBaseAmount(assetPayment.getAccountChargeAmount());
                /* } */

                persistables.add(assetPayment);
            }
        }

        /* ORIGINAL - 10/13/08
        // reduce source total amount and asset payment if document is Asset Separate
        if (assetGlobalService.isAssetSeparateDocument(this)) {
            Asset separateSourceCapitalAsset = this.getSeparateSourceCapitalAsset();
            separateSourceCapitalAsset.setTotalCostAmount(getTotalCostAmount().subtract(assetGlobalService.getUniqueAssetsTotalAmount(this)));
            persistables.add(separateSourceCapitalAsset);

            // copy and set AssetPayment from source Asset into new AssetPayment object
            for (AssetPayment assetPayment : separateSourceCapitalAsset.getAssetPayments()) {
                AssetPayment offsetAssetPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(assetPayment, offsetAssetPayment);
                offsetAssetPayment.setAccountChargeAmount(assetPaymentService.getProratedAssetPayment(this, assetPayment));
                offsetAssetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
                persistables.add(offsetAssetPayment);
            }
        }
         */

        /* LEO - 10/13/08
        //LOG.info("setup original asset....");
        // if doc is Asset Separate, modify the original asset amount and add all payments from new assets
        if (assetGlobalService.isAssetSeparateDocument(this)) {
            Asset sourceAsset = this.getSeparateSourceCapitalAsset();
            sourceAsset.setTotalCostAmount(getTotalCostAmount().subtract(assetGlobalService.getUniqueAssetsTotalAmount(this)));
            //LOG.info("source asset:new amount: '" + sourceAsset.getCapitalAssetNumber() + ":" + getTotalCostAmount().subtract(assetGlobalService.getUniqueAssetsTotalAmount(this)) + "'");
            persistables.add(sourceAsset);
            
            //LOG.info("setup original asset new payments....");
            // get each new asset, create a new payment for it, copy/set specific data 
            for (AssetGlobalDetail assetSharedDetail : assetSharedDetails) {
                for (AssetGlobalDetail assetUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                    AssetPayment assetPayment = new AssetPayment();
                    //LOG.info("in unique loop: account: '" + assetPayment.getAccountNumber() + "'");
                    ObjectValueUtils.copySimpleProperties(assetPaymentDetails, assetPayment);
                    assetPayment.setAccountChargeAmount(assetUniqueDetail.getSeparateSourceAmount());
                    //LOG.info("in unique loop: new amount: '" + assetUniqueDetail.getSeparateSourceAmount() + "'");
                    assetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
                    // need to set more data?
                    //assetPayment.setPrimaryDepreciationBaseAmount(?);
                    //assetPayment.getYearToDate(?);
                    //assetPayment.getPreviousYearPrimaryDepreciationAmount(?);
                    //assetPayment.getPeriod1Depreciation1Amount(?);
                    persistables.add(assetPayment);
                }
            }
        }
         */

        // LOG.info("finished generateGlobalChangesToPersist()....");
        return persistables;
    }

    /**
     * This method set off campus location for persist
     * 
     * @param AssetGlobalDetail and Asset to populate AssetLocation
     * @return Returns the AssetLocation.
     */
    private AssetLocation setOffCampusLocationObjectsForPersist(AssetGlobalDetail detail, Asset asset) {
        AssetLocation offCampusLocation = new AssetLocation();
        offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        offCampusLocation = (AssetLocation) SpringContext.getBean(BusinessObjectService.class).retrieve(offCampusLocation);
        if (offCampusLocation == null) {
            offCampusLocation = new AssetLocation();
            offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
            asset.getAssetLocations().add(offCampusLocation);
        }

        offCampusLocation.setAssetLocationContactName(detail.getOffCampusName());
        offCampusLocation.setAssetLocationContactIdentifier(detail.getRepresentativeUniversalIdentifier());
        offCampusLocation.setAssetLocationInstitutionName(detail.getAssetRepresentative().getPrimaryDepartmentCode());
        offCampusLocation.setAssetLocationPhoneNumber(null);
        offCampusLocation.setAssetLocationStreetAddress(detail.getOffCampusAddress());
        offCampusLocation.setAssetLocationCityName(detail.getOffCampusCityName());
        offCampusLocation.setAssetLocationStateCode(detail.getOffCampusStateCode());
        offCampusLocation.setAssetLocationCountryCode(detail.getOffCampusCountryCode());
        offCampusLocation.setAssetLocationZipCode(detail.getOffCampusZipCode());

        return offCampusLocation;
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
        if (lastInventoryDate != null) {
            return lastInventoryDate;
        }
        else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
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
    protected LinkedHashMap<String, String> toStringMapper() {
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
    public UniversalUser getAssetRepresentative() {
        assetRepresentative = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(representativeUniversalIdentifier, assetRepresentative);
        return assetRepresentative;
    }

    /**
     * Sets the assetRepresentative attribute value.
     * 
     * @param assetRepresentative The assetRepresentative to set.
     */
    public void setAssetRepresentative(UniversalUser assetRepresentative) {
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
     * Small workaround to avoid KualiInquirableImpl.getInquiryUrl having think it needs to construct an inquiry url for this date. This only returns a date if this is a separate.
     * 
     * @return
     */
    public Date getSeparateDocumentHeaderFinalDate() {
        if (ObjectUtils.isNull(documentHeader)) {
            return null;
        }
        else {
            AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);

            return assetGlobalService.isAssetSeparateDocument(this) ? documentHeader.getDocumentFinalDate() : null;
        }
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
        return separateSourceRemainingAmount;
    }

    public void setSeparateSourceRemainingAmount(KualiDecimal separateSourceRemainingAmount) {
        this.separateSourceRemainingAmount = separateSourceRemainingAmount;
    }
}