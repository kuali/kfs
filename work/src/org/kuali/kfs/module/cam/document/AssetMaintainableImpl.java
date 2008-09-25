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
package org.kuali.kfs.module.cam.document;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class implements custom data preparation for displaying asset edit screen.
 */
public class AssetMaintainableImpl extends KualiMaintainableImpl implements Maintainable {
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

    private Asset newAsset;
    private Asset copyAsset;

    private Set<RoutingData> routingInfo;

    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     * @param document Maintenance Document used for editing
     * @param parameters Parameters available
     */
    public void processAfterEdit(MaintenanceDocument document, Map parameters) {
        initializeAttributes(document);
        // Identifies the latest location information
        AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
        assetlocationService.setOffCampusLocation(copyAsset);
        assetlocationService.setOffCampusLocation(newAsset);

        // Calculates payment summary and depreciation summary based on available payment records
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(copyAsset);
        paymentSummaryService.calculateAndSetPaymentSummary(newAsset);

        // Identifies the merge history and separation history based on asset disposition records
        assetService.setSeparateHistory(copyAsset);
        assetService.setSeparateHistory(newAsset);

        // Finds out the latest retirement info, is asset is currently retired.
        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(copyAsset);
        retirementInfoService.setRetirementInfo(newAsset);

        retirementInfoService.setMergeHistory(copyAsset);
        retirementInfoService.setMergeHistory(newAsset);

        // Finds out the latest equipment loan or return information if available
        EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(copyAsset);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(newAsset);

        super.processAfterEdit(document, parameters);
    }

    /**
     * Hide a few sections if this is a create new (fabrication request) or vice versa. Also hide payments if there are more then the allowable number.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(oldMaintainable);

        Asset asset = (Asset) getBusinessObject();
        if (isAssetFabrication()) {
            // fabrication request asset creation. Hide sections that are only applicable to asset edit. For fields
            // that are to be hidden for asset edit, see AssetAuthorizer.getFieldAuthorizations
            for (Section section : sections) {
                String sectionId = section.getSectionId();
                if (CamsConstants.Asset.SECTION_ID_LAND_INFORMATION.equals(sectionId) || CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(sectionId) || CamsConstants.Asset.SECTION_ID_DEPRECIATION_INFORMATION.equals(sectionId) || CamsConstants.Asset.SECTION_ID_HISTORY.equals(sectionId) || CamsConstants.Asset.SECTION_ID_RETIREMENT_INFORMATION.equals(sectionId) || CamsConstants.Asset.SECTION_ID_EQUIPMENT_LOAN_INFORMATION.equals(sectionId) || CamsConstants.Asset.SECTION_ID_WARRENTY.equals(sectionId) || CamsConstants.Asset.SECTION_ID_REPAIR_HISTORY.equals(sectionId) || CamsConstants.Asset.SECTION_ID_COMPONENTS.equals(sectionId) || CamsConstants.Asset.SECTION_ID_MERGE_HISTORY.equals(sectionId)) {
                    section.setHidden(true);
                }
            }
        }
        else {
            // asset edit. Hide sections that are only applicable to fabrication request
            for (Section section : sections) {
                if (CamsConstants.Asset.SECTION_ID_FABRICATION_INFORMATION.equals(section.getSectionId())) {
                    section.setHidden(true);
                }
                // if asset is not retired, hide retirement information
                if (CamsConstants.Asset.SECTION_ID_RETIREMENT_INFORMATION.equals(section.getSectionId()) && !assetService.isAssetRetired(asset)) {
                    section.setHidden(true);
                }
                if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId()) && asset.getAssetPayments().size() == 0) {
                    section.setSectionTitle(section.getSectionTitle() + CamsConstants.Asset.SECTION_TITLE_NO_PAYMENT + asset.getCapitalAssetNumber());
                } else if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId()) && asset.getAssetPayments().size() > CamsConstants.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY) {
                    // Hide the payment section if there are more then CamsConstants.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY
                    section.setHidden(true);
                }
                // If asset is not loaned, hide the section
                if (CamsConstants.Asset.SECTION_ID_LOAN_INFORMATION.equals(section.getSectionId()) && (asset.getExpectedReturnDate() == null || asset.getLoanReturnDate() != null)) {
                    section.setHidden(true);
                }

            }
        }

        return sections;
    }

    /**
     * Checks if the maintainable is for asset fabrication as opposed "create new".
     * @return
     */
    private boolean isAssetFabrication() {
        return KNSConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(getMaintenanceAction());
    }
    
    /**
     * This method gets old and new maintainable objects and creates convenience handles to them
     * 
     * @param document Asset Edit Document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newAsset == null) {
            newAsset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        }
        if (copyAsset == null) {
            copyAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }
    }


    @Override
    public void saveBusinessObject() {
        Asset asset = ((Asset) businessObject);
        if (asset.getCapitalAssetNumber() == null) {
            asset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
        }        
        super.saveBusinessObject();
    }


    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        initializeAttributes(document);
        document.getNewMaintainableObject().setGenerateDefaultValues(false);
        if (newAsset.getCreateDate() == null) {
            newAsset.setCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            newAsset.setAcquisitionTypeCode(CamsConstants.ACQUISITION_TYPE_CODE_C);
            newAsset.setVendorName(CamsConstants.VENDOR_NAME_CONSTRUCTED);
            newAsset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION);
            newAsset.setPrimaryDepreciationMethodCode(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
            newAsset.setCapitalAssetTypeCode(SpringContext.getBean(ParameterService.class).getParameterValue(Asset.class, CamsConstants.Parameters.DEFAULT_FABRICATION_ASSET_TYPE_CODE));
            assetService.setFiscalPeriod(newAsset);
        }
    }

        
    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {        
        if (this.isAssetFabrication()) {
            routingInfo = new HashSet<RoutingData>();
            
            Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();
            Set<RoutingAccount> accountRoutingSet = new HashSet<RoutingAccount>();
    
            Asset asset = (Asset) getBusinessObject();

            //Asset information
            organizationRoutingSet.add(new OrgReviewRoutingData(asset.getOrganizationOwnerChartOfAccountsCode(), asset.getOrganizationOwnerAccount().getOrganizationCode()));
            accountRoutingSet.add(new RoutingAccount(asset.getOrganizationOwnerChartOfAccountsCode(),asset.getOrganizationOwnerAccountNumber()));
                                
            //Storing data
            RoutingData organizationRoutingData = new RoutingData();
            organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
            organizationRoutingData.setRoutingSet(organizationRoutingSet);
            routingInfo.add(organizationRoutingData);
                    
            RoutingData accountRoutingData = new RoutingData();
            accountRoutingData.setRoutingType(KualiAccountAttribute.class.getSimpleName());
            accountRoutingData.setRoutingSet(accountRoutingSet);
            routingInfo.add(accountRoutingData);
        }
    }    

}
