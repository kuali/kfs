package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {
    public static final String ASSET_RETIREMENT_DOCTYPE_CD = "AMRG";
    private String documentNumber;
    private Long mergedTargetCapitalAssetNumber;
    private String inventoryStatusCode;
    private String retirementReasonCode;
    private Date retirementDate;
    private Asset mergedTargetCapitalAsset;
    private AssetRetirementReason retirementReason;
    private AssetStatus inventoryStatus;
    private DocumentHeader documentHeader;
    private List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails;
    // non-persistent relation
    private AssetRetirementGlobalDetail sharedRetirementInfo;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;


    public AssetRetirementGlobalDetail getSharedRetirementInfo() {
        return sharedRetirementInfo;
    }


    public void setSharedRetirementInfo(AssetRetirementGlobalDetail sharedRetirementInfo) {
        this.sharedRetirementInfo = sharedRetirementInfo;
    }


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
     * @see org.kuali.core.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        AssetRetirementService retirementService = SpringContext.getBean(AssetRetirementService.class);

        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        if (retirementService.isAssetRetiredByMerged(this) && mergedTargetCapitalAsset != null) {
            setMergeObjectsForPersist(persistables, retirementService);
        }


        for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
            setAssetForPersist(detail, persistables, retirementService);
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
     * 
     * This method set asset fields for update
     * 
     * @param detail
     * @param persistables
     */
    private void setAssetForPersist(AssetRetirementGlobalDetail detail, List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        // load the object by key
        Asset asset = detail.getAsset();
        asset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED);
        asset.setRetirementReasonCode(retirementReasonCode);
        if (retirementDate != null) {
            asset.setRetirementFiscalYear(universityDateService.getFiscalYear(retirementDate));
        }


        if (retirementService.isAssetRetiredByTheft(this) && StringUtils.isNotBlank(sharedRetirementInfo.getPaidCaseNumber())) {
            asset.setCampusPoliceDepartmentCaseNumber(sharedRetirementInfo.getPaidCaseNumber());
        }
        else if (retirementService.isAssetRetiredBySoldOrAuction(this)) {
            asset.setRetirementChartOfAccountsCode(detail.getRetirementChartOfAccountsCode());
            asset.setRetirementAccountNumber(detail.getRetirementAccountNumber());
            asset.setCashReceiptFinancialDocumentNumber(detail.getCashReceiptFinancialDocumentNumber());
            asset.setSalePrice(detail.getSalePrice());
            asset.setEstimatedSellingPrice(detail.getEstimatedSellingPrice());
        }
        else if (retirementService.isAssetRetiredByMerged(this)) {
            asset.setTotalCostAmount(KualiDecimal.ZERO);
            asset.setSalvageAmount(KualiDecimal.ZERO);
        }
        persistables.add(asset);
    }


    /**
     * 
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

        // update merget target asset
        mergedTargetCapitalAsset.setTotalCostAmount(totalCostAmount.add(paymentSummaryService.calculatePaymentTotalCost(mergedTargetCapitalAsset)));
        mergedTargetCapitalAsset.setSalvageAmount(salvageAmount.add(mergedTargetCapitalAsset.getSalvageAmount()));
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


    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}
