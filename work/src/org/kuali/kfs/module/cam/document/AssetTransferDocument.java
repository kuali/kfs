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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.AssetTransferService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.financial.document.TransferOfFundsDocument;

public class AssetTransferDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetTransferDocument.class);
    private String representativeUniversalIdentifier;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private String organizationTagNumber;
    private String organizationOwnerChartOfAccountsCode;
    private String organizationOwnerAccountNumber;
    private String organizationText;
    private String organizationInventoryName;
    private String transferOfFundsFinancialDocumentNumber;
    private String organizationCode;
    private String offCampusAddress;
    private String offCampusCityName;
    private String offCampusStateCode;
    private String offCampusZipCode;
    private boolean interdepartmentalSalesIndicator;
    private Long capitalAssetNumber;
    private UniversalUser assetRepresentative;
    private Campus campus;
    private Account organizationOwnerAccount;
    private Chart organizationOwnerChartOfAccounts;
    private Org organization;
    private TransferOfFundsDocument transferOfFundsFinancialDocument;
    private State offCampusState;
    private Building building;
    private Room buildingRoom;
    private transient List<AssetGlpeSourceDetail> sourceAssetGlpeSourceDetails;
    private transient List<AssetGlpeSourceDetail> targetAssetGlpeSourceDetails;
    private Asset asset;


    public AssetTransferDocument() {
        super();
        this.sourceAssetGlpeSourceDetails = new ArrayList<AssetGlpeSourceDetail>();
        this.targetAssetGlpeSourceDetails = new ArrayList<AssetGlpeSourceDetail>();
    }


    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {

    }


    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#customizeOffsetGeneralLedgerPendingEntry(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.bo.GeneralLedgerPendingEntry, org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return false;
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }


    public Asset getAsset() {
        return asset;
    }


    public UniversalUser getAssetRepresentative() {
        return assetRepresentative;
    }


    /**
     * Gets the building attribute.
     * 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Gets the buildingRoom attribute.
     * 
     * @return Returns the buildingRoom.
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }


    /**
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Gets the buildingSubRoomNumber attribute.
     * 
     * @return Returns the buildingSubRoomNumber
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }


    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }


    /**
     * Gets the documentHeader attribute.
     * 
     * @return Returns the documentHeader
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
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
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount();
    }

    public List<AssetGlpeSourceDetail> getSourceAssetGlpeSourceDetails() {
        return this.sourceAssetGlpeSourceDetails;
    }
    
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateExplicitGeneralLedgerPendingEntry(this, postable, sequenceHelper, explicitEntry);
        customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        addPendingEntry(explicitEntry);
        return true;
    }

    /**
     * Gets the offCampusAddress attribute.
     * 
     * @return Returns the offCampusAddress
     */
    public String getOffCampusAddress() {
        return offCampusAddress;
    }


    /**
     * Gets the offCampusCityName attribute.
     * 
     * @return Returns the offCampusCityName
     */
    public String getOffCampusCityName() {
        return offCampusCityName;
    }

    /**
     * Gets the offCampusState attribute.
     * 
     * @return Returns the offCampusState.
     */
    public State getOffCampusState() {
        return offCampusState;
    }


    /**
     * Gets the offCampusStateCode attribute.
     * 
     * @return Returns the offCampusStateCode
     */
    public String getOffCampusStateCode() {
        return offCampusStateCode;
    }

    /**
     * Gets the offCampusZipCode attribute.
     * 
     * @return Returns the offCampusZipCode
     */
    public String getOffCampusZipCode() {
        return offCampusZipCode;
    }


    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }


    /**
     * Gets the organizationInventoryName attribute.
     * 
     * @return Returns the organizationInventoryName
     */
    public String getOrganizationInventoryName() {
        return organizationInventoryName;
    }

    /**
     * Gets the organizationOwnerAccount attribute.
     * 
     * @return Returns the organizationOwnerAccount
     */
    public Account getOrganizationOwnerAccount() {
        return organizationOwnerAccount;
    }


    /**
     * Gets the organizationOwnerAccountNumber attribute.
     * 
     * @return Returns the organizationOwnerAccountNumber
     */
    public String getOrganizationOwnerAccountNumber() {
        return organizationOwnerAccountNumber;
    }

    /**
     * Gets the organizationOwnerChartOfAccounts attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccounts
     */
    public Chart getOrganizationOwnerChartOfAccounts() {
        return organizationOwnerChartOfAccounts;
    }


    /**
     * Gets the organizationOwnerChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccountsCode
     */
    public String getOrganizationOwnerChartOfAccountsCode() {
        return organizationOwnerChartOfAccountsCode;
    }

    /**
     * Gets the organizationTagNumber attribute.
     * 
     * @return Returns the organizationTagNumber
     */
    public String getOrganizationTagNumber() {
        return organizationTagNumber;
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
     * Gets the representativeUniversalIdentifier attribute.
     * 
     * @return Returns the representativeUniversalIdentifier
     */
    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    /**
     * Gets the transferOfFundsFinancialDocument attribute.
     * 
     * @return Returns the transferOfFundsFinancialDocument.
     */
    public TransferOfFundsDocument getTransferOfFundsFinancialDocument() {
        return transferOfFundsFinancialDocument;
    }

    /**
     * Gets the transferOfFundsFinancialDocumentNumber attribute.
     * 
     * @return Returns the transferOfFundsFinancialDocumentNumber
     */
    public String getTransferOfFundsFinancialDocumentNumber() {
        return transferOfFundsFinancialDocumentNumber;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber.
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
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

            List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
            maintenanceLocks.add(assetService.generateAssetLock(documentNumber, getCapitalAssetNumber()));
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
            SpringContext.getBean(AssetTransferService.class).saveApprovedChanges(this);
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(getDocumentNumber());
        }

        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(getDocumentNumber());
        }
    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AssetGlpeSourceDetail srcDetail = (AssetGlpeSourceDetail) postable;
        boolean isDebit = false;
        // For source account, debit line when capitalization amount is negative or accumulated depreciation is positive or offset
        // amount is positive
        if (srcDetail.isSource()) {
            if ((srcDetail.isCapitalization() && srcDetail.getAmount().isNegative()) || (srcDetail.isAccumulatedDepreciation() && srcDetail.getAmount().isPositive()) || (srcDetail.isOffset() && srcDetail.getAmount().isPositive())) {
                isDebit = true;
            }
        }
        // For target account, debit line when capitalization is positive or accumulated depreciation is negative or offset amount
        // is negative
        if (!srcDetail.isSource()) {
            if ((srcDetail.isCapitalization() && srcDetail.getAmount().isPositive()) || (srcDetail.isAccumulatedDepreciation() && srcDetail.getAmount().isNegative()) || (srcDetail.isOffset() && srcDetail.getAmount().isNegative())) {
                isDebit = true;
            }
        }
        return isDebit;

    }


    /**
     * Gets the interdepartmentalSalesIndicator attribute.
     * 
     * @return Returns the interdepartmentalSalesIndicator
     */
    public boolean isInterdepartmentalSalesIndicator() {
        return interdepartmentalSalesIndicator;
    }


    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    public void setAssetRepresentative(UniversalUser assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
    }


    /**
     * Sets the building attribute value.
     * 
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }


    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Sets the buildingRoom attribute value.
     * 
     * @param buildingRoom The buildingRoom to set.
     * @deprecated
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     * 
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Sets the buildingSubRoomNumber attribute.
     * 
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }


    /**
     * Sets the campus attribute.
     * 
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }


    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
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
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public void setGeneralLedgerPostables(List<AssetGlpeSourceDetail> generalLedgerPostables) {
        this.sourceAssetGlpeSourceDetails = generalLedgerPostables;
    }


    /**
     * Sets the interdepartmentalSalesIndicator attribute.
     * 
     * @param interdepartmentalSalesIndicator The interdepartmentalSalesIndicator to set.
     */
    public void setInterdepartmentalSalesIndicator(boolean interdepartmentalSalesIndicator) {
        this.interdepartmentalSalesIndicator = interdepartmentalSalesIndicator;
    }


    /**
     * Sets the offCampusAddress attribute.
     * 
     * @param offCampusAddress The offCampusAddress to set.
     */
    public void setOffCampusAddress(String offCampusAddress) {
        this.offCampusAddress = offCampusAddress;
    }

    /**
     * Sets the offCampusCityName attribute.
     * 
     * @param offCampusCityName The offCampusCityName to set.
     */
    public void setOffCampusCityName(String offCampusCityName) {
        this.offCampusCityName = offCampusCityName;
    }

    /**
     * Sets the offCampusState attribute value.
     * 
     * @param offCampusState The offCampusState to set.
     * @deprecated
     */
    public void setOffCampusState(State offCampusState) {
        this.offCampusState = offCampusState;
    }


    /**
     * Sets the offCampusStateCode attribute.
     * 
     * @param offCampusStateCode The offCampusStateCode to set.
     */
    public void setOffCampusStateCode(String offCampusStateCode) {
        this.offCampusStateCode = offCampusStateCode;
    }


    /**
     * Sets the offCampusZipCode attribute.
     * 
     * @param offCampusZipCode The offCampusZipCode to set.
     */
    public void setOffCampusZipCode(String offCampusZipCode) {
        this.offCampusZipCode = offCampusZipCode;
    }


    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }


    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Sets the organizationInventoryName attribute.
     * 
     * @param organizationInventoryName The organizationInventoryName to set.
     */
    public void setOrganizationInventoryName(String organizationInventoryName) {
        this.organizationInventoryName = organizationInventoryName;
    }


    /**
     * Sets the organizationOwnerAccount attribute.
     * 
     * @param organizationOwnerAccount The organizationOwnerAccount to set.
     * @deprecated
     */
    public void setOrganizationOwnerAccount(Account organizationOwnerAccount) {
        this.organizationOwnerAccount = organizationOwnerAccount;
    }


    /**
     * Sets the organizationOwnerAccountNumber attribute.
     * 
     * @param organizationOwnerAccountNumber The organizationOwnerAccountNumber to set.
     */
    public void setOrganizationOwnerAccountNumber(String organizationOwnerAccountNumber) {
        this.organizationOwnerAccountNumber = organizationOwnerAccountNumber;
    }


    /**
     * Sets the organizationOwnerChartOfAccounts attribute.
     * 
     * @param organizationOwnerChartOfAccounts The organizationOwnerChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccounts) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccounts;
    }


    /**
     * Sets the organizationOwnerChartOfAccountsCode attribute.
     * 
     * @param organizationOwnerChartOfAccountsCode The organizationOwnerChartOfAccountsCode to set.
     */
    public void setOrganizationOwnerChartOfAccountsCode(String organizationOwnerChartOfAccountsCode) {
        this.organizationOwnerChartOfAccountsCode = organizationOwnerChartOfAccountsCode;
    }

    /**
     * Sets the organizationTagNumber attribute.
     * 
     * @param organizationTagNumber The organizationTagNumber to set.
     */
    public void setOrganizationTagNumber(String organizationTagNumber) {
        this.organizationTagNumber = organizationTagNumber;
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
     * Sets the representativeUniversalIdentifier attribute.
     * 
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }


    /**
     * Sets the transferOfFundsFinancialDocument attribute value.
     * 
     * @param transferOfFundsFinancialDocument The transferOfFundsFinancialDocument to set.
     * @deprecated
     */
    public void setTransferOfFundsFinancialDocument(TransferOfFundsDocument transferOfFundsFinancialDocument) {
        this.transferOfFundsFinancialDocument = transferOfFundsFinancialDocument;
    }


    /**
     * Sets the transferOfFundsFinancialDocumentNumber attribute.
     * 
     * @param transferOfFundsFinancialDocumentNumber The transferOfFundsFinancialDocumentNumber to set.
     */
    public void setTransferOfFundsFinancialDocumentNumber(String transferOfFundsFinancialDocumentNumber) {
        this.transferOfFundsFinancialDocumentNumber = transferOfFundsFinancialDocumentNumber;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostables()
     */
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPostables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        generalLedgerPostables.addAll(this.sourceAssetGlpeSourceDetails);
        generalLedgerPostables.addAll(this.targetAssetGlpeSourceDetails);
        return generalLedgerPostables;
    }


    public List<AssetGlpeSourceDetail> getTargetAssetGlpeSourceDetails() {
        return targetAssetGlpeSourceDetails;
    }


    public void setTargetAssetGlpeSourceDetails(List<AssetGlpeSourceDetail> targetAssetGlpeSourceDetails) {
        this.targetAssetGlpeSourceDetails = targetAssetGlpeSourceDetails;
    }


    public void setSourceAssetGlpeSourceDetails(List<AssetGlpeSourceDetail> sourceAssetGlpeSourceDetails) {
        this.sourceAssetGlpeSourceDetails = sourceAssetGlpeSourceDetails;
    }
}
