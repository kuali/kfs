/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.vnd.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.vnd.VendorPropertyConstants;

/**
 * Authorizer class for Vendor maintenance document
 */
public class VendorDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        Set<String> readOnlySectionIds = super.getSecurePotentiallyReadOnlySectionIds();
        
        // vendor contracts and commodity codes are the potentially readonly sections
        readOnlySectionIds.add(VendorPropertyConstants.VENDOR_CONTRACT);
        readOnlySectionIds.add(VendorPropertyConstants.VENDOR_COMMODITIES_CODE);
        
        return readOnlySectionIds;
    }    
    
    // TODO fix for kim

//    /**
//     * By default, there are no restrictions for the fields in the superclass. This method is overridden here to makes all the
//     * fields in Vendor Header readOnly if the vendor is not a parent. If the vendor is not a new vendor, if it is a parent, if
//     * vendor header and vendor type is not null and if the vendor type's changed allowed is set to N in the vendor type maintenance
//     * table, then we have to set the vendor type as readOnly field.
//     * 
//     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer#addMaintenanceDocumentRestrictions(org.kuali.rice.kns.document.MaintenanceDocument,
//     *      org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document, Person user) {
//        VendorDetail vendor = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
//        VendorDetail oldVendor = (VendorDetail) document.getOldMaintainableObject().getBusinessObject();
//        VendorHeader vendorHeader = vendor.getVendorHeader();
//        VendorHeader oldVendorHeader = oldVendor.getVendorHeader();
//
//        if (vendor.isVendorParentIndicator()) {
//            //Vendor Parent Indicator should be readOnly if the vendor is a parent.
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_PARENT_INDICATOR);
//        }
//        // If the vendor is not a parent, there are certain fields that should be readOnly
//        if (!vendor.isVendorParentIndicator()) {
//            // All the fields in VendorHeader should be readOnly if the vendor is not a parent.
//
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TYPE_CODE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TAX_NUMBER);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_END_DATE);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_W9_RECEIVED_INDICATOR);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_W8_BEN_RECEIVED_INDICATOR);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_DEBARRED_INDICATOR);
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FOREIGN_INDICATOR);
//            
//            // Supplier Diversities drop down menu is readOnly if the vendor is not a parent.
//            List<VendorSupplierDiversity> supplierDivs = vendor.getVendorHeader().getVendorSupplierDiversities();
//            if (ObjectUtils.isNotNull(supplierDivs)) {
//                for (int div = 0; div < supplierDivs.size(); div++) {
//                    VendorSupplierDiversity vsd = supplierDivs.get(div);
//                    auths.addReadonlyAuthField(VendorUtils.assembleWithPosition(VendorPropertyConstants.VENDOR_HEADER_PREFIX + VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE, VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITIES, div));
//                    auths.addReadonlyAuthField(VendorUtils.assembleWithPosition(VendorPropertyConstants.VENDOR_HEADER_PREFIX + VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_ACTIVE, VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITIES, div));
//                }
//            }
//        }
//        // Else if the vendor is not a new vendor, if it is a parent, if vendor header and vendor type is not null
//        // and if the vendor type's changed allowed is set to
//        // N in the vendor type maintenance table, then we have to set the vendor type as readOnly field.
//        else if (ObjectUtils.isNotNull(vendor.getVendorHeaderGeneratedIdentifier()) && ObjectUtils.isNotNull(vendorHeader) && ObjectUtils.isNotNull(oldVendorHeader.getVendorType()) && !oldVendorHeader.getVendorType().isVendorTypeChangeAllowedIndicator()) {
//            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TYPE_CODE);
//        }
//
//        String purchasingWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, VendorConstants.Workgroups.WORKGROUP_PURCHASING);
//        
//        setVendorContractFieldsAuthorization(vendor, auths, user, purchasingWorkgroup);
//
//        setVendorCommodityCodeFieldsAuthorization(vendor, auths, user, purchasingWorkgroup);
//    }

    // TODO fix for kim
//    /**
//     * If the current user is a member of TAXNBR_ACCESSIBLE_GROUP then user is allowed to edit tax number.
//     * 
//     * @see org.kuali.rice.kns.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public Map getEditMode(Document document, Person user) {
//        Map editMode = new HashMap();
//        if (!document.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
//            editMode = super.getEditMode(document, user);
//        }
//        else {
//            editMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, "TRUE");
//        }
//        VendorDetail vendor = (VendorDetail) ((MaintenanceDocument) document).getNewMaintainableObject().getBusinessObject();
//        String taxNbrAccessibleWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(VendorDetail.class, VendorConstants.Workgroups.WORKGROUP_TAXNBR_ACCESSIBLE);
//
//        if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, taxNbrAccessibleWorkgroup)) {
//            editMode.put(VendorAuthorizationConstants.VendorEditMode.TAX_ENTRY, "TRUE");
//        }
//
//        return editMode;
//    }
    // TODO fix for kim

//    /**
//     * Disables blanket approve for Vendor maintenance document
//     * 
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemDocumentActionFlags docActionFlags = super.getDocumentActionFlags(document, user);
//        docActionFlags.setCanBlanketApprove(false);
//        return docActionFlags;
//    }

    // TODO fix for kim - framework will do these for you now - your authorizer will say commodities and contracts are secure potentially hidden section ids and kim will check the perms that are set up for them
//    /**
//     * Sets the vendor contract and vendor contract organization fields to be read only if the current user is not a member of
//     * purchasing workgroup.
//     * 
//     * @param vendor an instance of VendorDetail document
//     * @param auths an instance of MaintenanceDocumentAuthorizations which is used to define the read only fields
//     * @param user current logged-in user
//     * @param purchasingWorkgroup the String representation of purchasing workgroup which was obtained from ParameterService
//     */
//    private void setVendorContractFieldsAuthorization(VendorDetail vendor, MaintenanceDocumentAuthorizations auths, Person user, String purchasingWorkgroup) {
//        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, purchasingWorkgroup)) {
//            List<VendorContract> contracts = vendor.getVendorContracts();
//            int i = 0;
//            for (VendorContract contract : contracts) {
//                // contract fields
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractName");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractDescription");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorCampusCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractBeginningDate");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractEndDate");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].contractManagerCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorB2bIndicator");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].purchaseOrderCostSourceCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorPaymentTermsCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorShippingPaymentTermsCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorShippingTitleCode");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractExtensionDate");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].organizationAutomaticPurchaseOrderLimit");
//                auths.addReadonlyAuthField("vendorContracts[" + i + "].active");
//
//                // contract organization sub collection fields
//                int j = 0;
//                List<VendorContractOrganization> vendorContractOrganizations = contract.getVendorContractOrganizations();
//                for (VendorContractOrganization org : vendorContractOrganizations) {
//                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].chartOfAccountsCode");
//                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].organizationCode");
//                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].vendorContractPurchaseOrderLimitAmount");
//                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].vendorContractExcludeIndicator");
//                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].active");
//                    j++;
//                }
//                i++;
//            }
//        }
//    }
//    
//    /**
//     * Sets the vendor commodity code fields to be read only and remove the add line on the vendor commodity collection if
//     * the user is not a member of purchasing workgroup.
//     * 
//     * @param vendor an instance of VendorDetail document
//     * @param auths an instance of MaintenanceDocumentAuthorizations which is used to define the read only fields
//     * @param user current logged-in user
//     * @param purchasingWorkgroup the String representation of purchasing workgroup which was obtained from ParameterService
//     */
//    private void setVendorCommodityCodeFieldsAuthorization(VendorDetail vendor, MaintenanceDocumentAuthorizations auths, Person user, String purchasingWorkgroup) {
//        //If the user is not in purchasing workgroup, we need to set the includeAddLine to false for vendorCommodities collection
//        //and set the commodity default indicator and active indicator to be read only.
//        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, purchasingWorkgroup)) {
//            MaintainableCollectionDefinition collDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection("VendorDetailMaintenanceDocument", VendorPropertyConstants.VENDOR_COMMODITIES_CODE);
//            collDef.setIncludeAddLine(false);
//            
//            List<VendorCommodityCode>vendorCommodities = vendor.getVendorCommodities();
//            int i = 0;
//            for (VendorCommodityCode vendorCommodityCode : vendorCommodities) {
//                auths.addReadonlyAuthField("vendorCommodities[" + i + "].commodityDefaultIndicator");
//                auths.addReadonlyAuthField("vendorCommodities[" + i + "].active");
//                i++;
//            }
//        }
//        else {
//            MaintainableCollectionDefinition collDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection("VendorDetailMaintenanceDocument", VendorPropertyConstants.VENDOR_COMMODITIES_CODE);
//            collDef.setIncludeAddLine(true);
//        }
//    }
}

