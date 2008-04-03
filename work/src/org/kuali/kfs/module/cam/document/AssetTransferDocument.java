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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.service.GeneralLedgerPendingEntryGenerationProcess;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetTransferDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {
    private static final String ASSET_TRANSFER_DOCTYPE_CD = "AT";
    private final static String GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "kfsGenericGeneralLedgerPostingHelper";
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
    private UniversalUser assetRepresentative;

    private AssetHeader assetHeader;
    private Campus campus;
    private Account organizationOwnerAccount;
    private Chart organizationOwnerChartOfAccounts;
    private Org organization;
    private DocumentHeader transferOfFundsFinancialDocument;
    private State offCampusState;
    private Building building;
    private Room buildingRoom;
    private List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPostables;

    // Transient attributes
    private Asset asset;
    private UniversityDateService universityDateService;

    public AssetTransferDocument() {
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
    public DocumentHeader getTransferOfFundsFinancialDocument() {
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
     * Gets the interdepartmentalSalesIndicator attribute.
     * 
     * @return Returns the interdepartmentalSalesIndicator
     */
    public boolean isInterdepartmentalSalesIndicator() {
        return interdepartmentalSalesIndicator;
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
    public void setTransferOfFundsFinancialDocument(DocumentHeader transferOfFundsFinancialDocument) {
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


    public AssetHeader getAssetHeader() {
        return assetHeader;
    }


    public void setAssetHeader(AssetHeader assetHeader) {
        this.assetHeader = assetHeader;
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        String financialDocumentStatusCode = getDocumentHeader().getFinancialDocumentStatusCode();

        // if status is approved
        if (CamsConstants.DOC_APPROVED.equals(financialDocumentStatusCode)) {
            // save new asset location details to asset table, inventory date
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            List<PersistableBusinessObject> persistableObjects = new ArrayList<PersistableBusinessObject>();
            Asset saveAsset = new Asset();
            saveAsset.setCapitalAssetNumber(getAssetHeader().getCapitalAssetNumber());
            saveAsset = (Asset) boService.retrieve(saveAsset);
            changeAssetOwner(saveAsset, persistableObjects);
            if (saveAsset.getAssetPayments() == null) {
                saveAsset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
            }

            List<AssetPayment> originalPayments = saveAsset.getAssetPayments();
            if (this.universityDateService == null) {
                this.universityDateService = SpringContext.getBean(UniversityDateService.class);
            }
            Integer maxSequence = createOffsetPayments(persistableObjects, originalPayments);
            maxSequence = createNewPayments(persistableObjects, originalPayments, maxSequence);
            updateOriginalPayments(persistableObjects, originalPayments);

            // create new asset payment records and offset payment records
            boService.save(persistableObjects);
        }
    }


    private void updateOriginalPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                try {
                    // change payment code
                    assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                    persistableObjects.add(assetPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private Integer createNewPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments, Integer maxSequence) {
        Integer maxSequenceNo = maxSequence;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create new payment
                AssetPayment newPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment);
                    }
                    // create new payment info
                    newPayment = (AssetPayment) ObjectUtils.fromByteArray(ObjectUtils.toByteArray(assetPayment));
                    newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    newPayment.setAccountNumber(getOrganizationOwnerAccountNumber());
                    newPayment.setChartOfAccountsCode(getOrganizationOwnerChartOfAccountsCode());
                    newPayment.setSubAccountNumber(null);
                    newPayment.setDocumentNumber(getDocumentNumber());
                    newPayment.setFinancialDocumentTypeCode(ASSET_TRANSFER_DOCTYPE_CD);
                    newPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    newPayment.setFinancialDocumentPostingYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
                    newPayment.setFinancialDocumentPostingPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    adjustAmounts(newPayment, false);
                    // add new payment
                    persistableObjects.add(newPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return maxSequenceNo;
    }


    private Integer createOffsetPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        Integer maxSequenceNo = null;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create an offset payment
                AssetPayment offsetPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment);
                    }
                    offsetPayment = (AssetPayment) ObjectUtils.fromByteArray(ObjectUtils.toByteArray(assetPayment));
                    offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    offsetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                    offsetPayment.setDocumentNumber(getDocumentNumber());
                    offsetPayment.setFinancialDocumentTypeCode(ASSET_TRANSFER_DOCTYPE_CD);
                    offsetPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    offsetPayment.setFinancialDocumentPostingYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
                    offsetPayment.setFinancialDocumentPostingPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    adjustAmounts(offsetPayment, true);
                    // add offset payment
                    persistableObjects.add(offsetPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return maxSequenceNo;
    }


    private void adjustAmounts(AssetPayment offsetPayment, boolean reverseAmount) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method[] methods = AssetPayment.class.getMethods();
        for (Method method : methods) {
            if (method.getReturnType().isAssignableFrom(KualiDecimal.class)) {
                String setterName = "set" + method.getName().substring(3);
                Method setter = AssetPayment.class.getMethod(setterName, KualiDecimal.class);
                KualiDecimal amount = (KualiDecimal) method.invoke(offsetPayment);
                if (setter != null && amount != null) {
                    if (setterName.contains("Depreciation")) {
                        Object[] nullVal = new Object[] { null };
                        setter.invoke(offsetPayment, nullVal);
                    }
                    else if (reverseAmount) {
                        // reverse the amounts
                        setter.invoke(offsetPayment, (amount).multiply(new KualiDecimal(-1)));
                    }
                }
            }
        }
    }

    private void changeAssetOwner(Asset saveAsset, List<PersistableBusinessObject> objects) {
        saveAsset.setOrganizationOwnerAccountNumber(getOrganizationOwnerAccountNumber());
        saveAsset.setOrganizationOwnerChartOfAccountsCode(getOrganizationOwnerChartOfAccountsCode());
        objects.add(saveAsset);
    }

    public Asset getAsset() {
        return asset;
    }


    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    @Override
    public void prepareForSave() {
        // generate postables
        super.prepareForSave();
        this.generalLedgerPostables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        createGLPostables();

        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }

    }

    /**
     * Creates GL Postables using the source plant account number and target plant account number
     */
    private void createGLPostables() {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        this.asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
        refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
        AssetService assetService = SpringContext.getBean(AssetService.class);
        boolean movableAsset = assetService.isAssetMovable(this.asset);
        if (assetService.isCapitalAsset(this.asset) && isGLPostable(movableAsset)) {
            List<AssetPayment> assetPayments = getAsset().getAssetPayments();
            createSourceGLPostables(universityDateService, assetPayments, movableAsset);
            createTargetGLPostables(universityDateService, assetPayments, movableAsset);
        }
    }


    /**
     * Checks if it is ready for GL Posting by validating the accounts and plant account numbers
     * 
     * @return true if all accounts are valid
     */
    private boolean isGLPostable(boolean movableAsset) {
        boolean isGLPostable = true;

        Account srcPlantAcct = null;

        if (ObjectUtils.isNotNull(this.asset.getOrganizationOwnerAccount())) {
            if (movableAsset) {
                srcPlantAcct = this.asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
            }
            else {
                srcPlantAcct = this.asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
            }
        }

        if (ObjectUtils.isNull(srcPlantAcct)) {
            isGLPostable &= false;
        }
        Account targetPlantAcct = null;
        if (ObjectUtils.isNotNull(getOrganizationOwnerAccount())) {
            if (movableAsset) {
                targetPlantAcct = getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
            }
            else {
                targetPlantAcct = getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
            }
        }
        if (ObjectUtils.isNull(targetPlantAcct)) {
            isGLPostable &= false;

        }
        return isGLPostable;
    }


    /**
     * Creates target GL Postable for the receiving organization
     * 
     * @param universityDateService University Date Service to get the current fiscal year and period
     * @param assetPayments Payments for which GL entries needs to be created
     */
    private void createTargetGLPostables(UniversityDateService universityDateService, List<AssetPayment> assetPayments, boolean movableAsset) {
        Account targetPlantAcct = null;

        if (movableAsset) {
            targetPlantAcct = getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
        }
        else {
            targetPlantAcct = getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
        }
        for (AssetPayment assetPayment : assetPayments) {
            if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode()) && ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
                addGeneralLedgerPostables(createAssetGlpePostable(universityDateService, targetPlantAcct, assetPayment, false));
            }
        }
    }


    /**
     * Creates GL Postables for the source organization
     * 
     * @param universityDateService University Date Service to get the current fiscal year and period
     * @param assetPayments Payments for which GL entries needs to be created
     */
    private void createSourceGLPostables(UniversityDateService universityDateService, List<AssetPayment> assetPayments, boolean movableAsset) {
        Account srcPlantAcct = null;

        if (movableAsset) {
            srcPlantAcct = this.asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
        }
        else {
            srcPlantAcct = this.asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
        }
        for (AssetPayment assetPayment : assetPayments) {
            if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
                if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
                    addGeneralLedgerPostables(createAssetGlpePostable(universityDateService, srcPlantAcct, assetPayment, true));
                }
            }
        }
    }

    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     * 
     * @param universityDateService University Date Service
     * @param plantAccount Plant account for the organization
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    private AssetGlpeSourceDetail createAssetGlpePostable(UniversityDateService universityDateService, Account plantAccount, AssetPayment assetPayment, boolean isSource) {
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
        postable.setSource(isSource);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setAmount(assetPayment.getAccountChargeAmount());
        // TODO Balance type code
        postable.setBalanceTypeCode("");
        if (isSource) {
            postable.setChartOfAccountsCode(this.asset.getOrganizationOwnerChartOfAccountsCode());
        }
        else {
            postable.setChartOfAccountsCode(this.getOrganizationOwnerChartOfAccountsCode());
        }
        postable.setDocumentNumber(getDocumentNumber());
        postable.setFinancialDocumentLineDescription("Asset Transfer " + (isDebit(postable) ? "Debit" : "Credit") + " Line");
        postable.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        postable.setObjectCode(assetPayment.getFinancialObject());
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
        return postable;
    }

    public UniversalUser getAssetRepresentative() {
        return assetRepresentative;
    }


    public void setAssetRepresentative(UniversalUser assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
    }


    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {

    }


    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return false;
    }


    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }


    public KualiDecimal getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount();
    }


    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPostables() {
        return this.generalLedgerPostables;
    }


    public GeneralLedgerPendingEntryGenerationProcess getGeneralLedgerPostingHelper() {
        Map<String, GeneralLedgerPendingEntryGenerationProcess> glPostingHelpers = SpringContext.getBeansOfType(GeneralLedgerPendingEntryGenerationProcess.class);
        return glPostingHelpers.get(GENERAL_LEDGER_POSTING_HELPER_BEAN_ID);
    }


    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AssetGlpeSourceDetail srcDetail = (AssetGlpeSourceDetail) postable;
        boolean isDebit = false;
        // If source org and amount is negative then true
        if (srcDetail.isSource() && srcDetail.getAmount().isNegative()) {
            isDebit = true;
        }
        // If target and amount is positive then true
        if (!srcDetail.isSource() && srcDetail.getAmount().isPositive()) {
            isDebit = true;
        }
        return isDebit;
    }


    public void setGeneralLedgerPostables(List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPostables) {
        this.generalLedgerPostables = generalLedgerPostables;
    }

    public void addGeneralLedgerPostables(GeneralLedgerPendingEntrySourceDetail generalLedgerPendingEntrySourceDetail) {
        if (this.generalLedgerPostables != null) {
            this.generalLedgerPostables.add(generalLedgerPendingEntrySourceDetail);
        }
    }


    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }


    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}
