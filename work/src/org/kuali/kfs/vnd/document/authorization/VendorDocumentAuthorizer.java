/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.document.authorization;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.vendor.VendorAuthorizationConstants;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.VendorRuleConstants;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorContractOrganization;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.bo.VendorSupplierDiversity;
import org.kuali.module.vendor.util.VendorUtils;

public class VendorDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        VendorDetail vendor = (VendorDetail)document.getNewMaintainableObject().getBusinessObject();
        VendorHeader vendorHeader = vendor.getVendorHeader();
        
        //If the vendor is not a parent, there are certain fields that should be readOnly
        if (!vendor.isVendorParentIndicator()) {
            //All the fields in VendorHeader should be readOnly if the vendor is not a parent.

            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TYPE_CODE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TAX_NUMBER);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_END_DATE);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_W9_RECEIVED_INDICATOR);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_W8_BEN_RECEIVED_INDICATOR);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_DEBARRED_INDICATOR);
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_FOREIGN_INDICATOR);
            //Supplier Diversities drop down menu is readOnly if the vendor is not a parent.
            List <VendorSupplierDiversity> supplierDivs = vendor.getVendorHeader().getVendorSupplierDiversities();
            if (ObjectUtils.isNotNull(supplierDivs)) {
                for(int div = 0; div < supplierDivs.size(); div++) {
                    VendorSupplierDiversity vsd = supplierDivs.get(div);
                    auths.addReadonlyAuthField(VendorUtils.assembleWithPosition(VendorPropertyConstants.VENDOR_HEADER_PREFIX + VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE, VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITIES, div));
                }
            }
        }
        //Else if the vendor is not a new vendor, if it is a parent, if vendor header and vendor type is not null 
        //and if the vendor type's changed allowed is set to 
        //N in the vendor type maintenance table, then we have to set the vendor type as readOnly field.
        else if (ObjectUtils.isNotNull(vendor.getVendorHeaderGeneratedIdentifier()) &&
                 ObjectUtils.isNotNull(vendorHeader) &&
                 ObjectUtils.isNotNull(vendorHeader.getVendorType()) && 
                 !vendorHeader.getVendorType().isVendorTypeChangeAllowedIndicator()) {
            auths.addReadonlyAuthField(VendorPropertyConstants.VENDOR_TYPE_CODE);
        }
        setVendorContractFieldsAuthorization(vendor, auths, user);
        return auths;
    }

    /**
     * @see org.kuali.core.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editMode = super.getEditMode(document, user);
        VendorDetail vendor = (VendorDetail)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        String taxNbrAccessibleWorkgroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( VendorRuleConstants.PURAP_ADMIN_GROUP, VendorConstants.Workgroups.WORKGROUP_TAXNBR_ACCESSIBLE );

        if (user.isMember(taxNbrAccessibleWorkgroup)) {
            editMode.put(VendorAuthorizationConstants.VendorEditMode.TAX_ENTRY, "TRUE");
        }
        return editMode;
    }

    /**
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        MaintenanceDocumentActionFlags docActionFlags = new MaintenanceDocumentActionFlags(super.getDocumentActionFlags(document, user));
        docActionFlags.setCanBlanketApprove(false);
        return docActionFlags;
    }
    /**
     * 
     * This method sets the vendor contract and vendor contract organization fields to be 
     * read only if the current user is not a member of purchasing workgroup.
     * 
     * @param vendor
     * @param auths
     * @param user
     */
    private void setVendorContractFieldsAuthorization(VendorDetail vendor, MaintenanceDocumentAuthorizations auths, UniversalUser user) {
        String purchasingWorkgroup = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( VendorRuleConstants.PURAP_ADMIN_GROUP, VendorConstants.Workgroups.WORKGROUP_PURCHASING); 
        if (!user.isMember(purchasingWorkgroup)) {
            List<VendorContract> contracts = vendor.getVendorContracts();
            int i = 0;
            for (VendorContract contract : contracts) {
                //contract fields
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractName");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractDescription");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorCampusCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractBeginningDate");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractEndDate");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].contractManagerCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorB2bIndicator");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].purchaseOrderCostSourceCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorPaymentTermsCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorShippingPaymentTermsCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorShippingTitleCode");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractExtensionDate");
                auths.addReadonlyAuthField("vendorContracts[" + i + "].organizationAutomaticPurchaseOrderLimit");
                
                //contract organization sub collection fields
                int j = 0;
                List<VendorContractOrganization> vendorContractOrganizations = contract.getVendorContractOrganizations();
                for (VendorContractOrganization org : vendorContractOrganizations) {
                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].chartOfAccountsCode");
                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].organizationCode");
                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].vendorContractPurchaseOrderLimitAmount");
                    auths.addReadonlyAuthField("vendorContracts[" + i + "].vendorContractOrganizations[" + j + "].vendorContractExcludeIndicator");
                    j++;
                }
                i++;
            }
        }
    }
}
