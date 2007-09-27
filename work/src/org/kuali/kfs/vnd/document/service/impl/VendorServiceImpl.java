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
package org.kuali.module.vendor.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.user.PersonTaxId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorContractOrganization;
import org.kuali.module.vendor.bo.VendorDefaultAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.dao.VendorDao;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorRoutingComparable;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class VendorServiceImpl implements VendorService {
    private static Logger LOG = Logger.getLogger(VendorServiceImpl.class);
    
    private VendorDao vendorDao;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private KualiConfigurationService configService;
    private UniversalUserService universalUserService;
    
    public UniversalUserService getUniversalUserService() {
        return universalUserService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setVendorDao(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void saveVendorHeader(VendorDetail vendorDetail) {
        businessObjectService.save(vendorDetail.getVendorHeader());
    }
    
    public VendorDetail getVendorDetail(Integer headerId, Integer detailId) {
        LOG.debug("Entering getVendorDetail for headerId:"+headerId+", detailId:"+detailId);
        Map keys = new HashMap();
        keys.put("vendorHeaderGeneratedIdentifier", headerId);
        keys.put("vendorDetailAssignedIdentifier", detailId);
        return (VendorDetail)businessObjectService.findByPrimaryKey(VendorDetail.class, keys);
    }
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#getApoLimitFromContract(Integer, String, String)
    */
    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org) {
        LOG.debug("Entering getApoLimitFromContract with contractId:"+contractId+", chart:"+chart+", org:"+org);
        
        // See if there is a contractOrg for this contract and look for the special case of APO limit in the contract orgs table, return the value found
        if (ObjectUtils.isNotNull(contractId) && ObjectUtils.isNotNull(chart) && ObjectUtils.isNotNull(org)) {
            VendorContractOrganization exampleContractOrg = new VendorContractOrganization();
            exampleContractOrg.setVendorContractGeneratedIdentifier(contractId);
            exampleContractOrg.setChartOfAccountsCode(chart);
            exampleContractOrg.setOrganizationCode(org);
            Map orgKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(exampleContractOrg);
            VendorContractOrganization contractOrg = (VendorContractOrganization) businessObjectService.findByPrimaryKey(VendorContractOrganization.class, orgKeys);
            if (ObjectUtils.isNotNull(contractOrg)) {
                if (!contractOrg.isVendorContractExcludeIndicator()) { // It's not excluded.
                    return contractOrg.getVendorContractPurchaseOrderLimitAmount();
                }
            }            
        }

        // didn't search the table or not found in the table and contract exists, return the default APO limit in contract
        if (ObjectUtils.isNotNull(contractId)) {
            VendorContract exampleContract = new VendorContract();
            exampleContract.setVendorContractGeneratedIdentifier(contractId);
            Map contractKeys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(exampleContract);
            VendorContract contract = (VendorContract) businessObjectService.findByPrimaryKey(VendorContract.class, contractKeys);
                
	        if (ObjectUtils.isNotNull(contract)) {
    	        return contract.getOrganizationAutomaticPurchaseOrderLimit();
        	}
        }
        
        // otherwise no APO limit found
        return null;
      }

    /**
     * @see org.kuali.module.vendor.service.VendorService#getParentVendor(java.lang.Integer)
     */
    public VendorDetail getParentVendor( Integer vendorHeaderGeneratedIdentifier ) {
        LOG.debug("Entering getParentVendor for vendorHeaderGeneratedIdentifier:"+vendorHeaderGeneratedIdentifier);
        Map criterion = new HashMap();
        criterion.put("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedIdentifier);
        List<VendorDetail> vendors = (List<VendorDetail>)businessObjectService.findMatching(
                VendorDetail.class, criterion);
        VendorDetail result = null;
        if (ObjectUtils.isNull(vendors)){
            LOG.warn("Error: No vendors exist with vendor header "+vendorHeaderGeneratedIdentifier+".");
        }
        else {
            for (VendorDetail vendor : vendors) {
                if ( vendor.isVendorParentIndicator() ) {
                    if (ObjectUtils.isNull(result)) {
                        result = vendor;
                    }
                    else {
                        LOG.error("Error: More than one parent vendor for vendor header "+vendorHeaderGeneratedIdentifier+".");
                        throw new RuntimeException("Error: More than one parent vendor for vendor header "+vendorHeaderGeneratedIdentifier+".");
                    }
                }
            }
            if (ObjectUtils.isNull(result)) {
                LOG.error("Error: No parent vendor for vendor header "+vendorHeaderGeneratedIdentifier+".");
                throw new RuntimeException("Error: No parent vendor for vendor header "+vendorHeaderGeneratedIdentifier+".");
            }
        }
        LOG.debug("Exiting getParentVendor normally.");
        return result;
    }
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#getVendorB2BContract(VendorDetail, String)
     */
    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus) {
        return vendorDao.getVendorB2BContract(vendorDetail, campus);
    }

    /**
     * @see org.kuali.module.vendor.service.VendorService#getVendorDefaultAddress(Integer, Integer, String, String)
     */
    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus) {
        LOG.debug("Entering getVendorDefaultAddress for vendorHeaderId:"+vendorHeaderId+", vendorDetailId:"+vendorDetailId+", addressType:"+addressType+", campus:"+campus);
        Map criteria = new HashMap();
        criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderId);
        criteria.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailId);
        criteria.put(VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, addressType);
        List<VendorAddress> addresses = (List)businessObjectService.findMatching(VendorAddress.class, criteria);
        LOG.debug("Exiting getVendorDefaultAddress.");
        return getVendorDefaultAddress(addresses, addressType, campus);
}
    /**
     * @see org.kuali.module.vendor.service.VendorService#getVendorDefaultAddress(List, String, String)
     */
    public VendorAddress getVendorDefaultAddress(List<VendorAddress> addresses, String addressType, String campus) {
        LOG.debug("Entering getVendorDefaultAddress.");
        VendorAddress allDefaultAddress = null;
        for (VendorAddress address : addresses) {
            // if address is of the right type, continue check
            if (addressType.equals(address.getVendorAddressTypeCode())) {
                // if campus was passed in and list of campuses on address exist, continue check
                if (StringUtils.isNotEmpty(campus) && address.getVendorDefaultAddresses() != null) {
                    // looping through list of campus defaults to find a match for the passed in campus
                    for (VendorDefaultAddress defaultCampus : address.getVendorDefaultAddresses()) {
                        if (campus.equals(defaultCampus.getVendorCampusCode())) {
                            // found campus default; return it
                            LOG.debug("Exiting getVendorDefaultAddress with single campus default.");
                            return address;
                        }
                    }// endfor campuses
                }

                // if this address is set as the default for this address type; keep it for possible future use
                if (address.isVendorDefaultAddressIndicator()) {
                    allDefaultAddress = address;
                }
            }
        }// endfor addresses

        // if we got this far, there is no campus default; so return the default set for all (could return null)
        LOG.debug("Exiting getVendorDefaultAddress with default set for all.");
        return allDefaultAddress;
    }
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#shouldVendorRouteForApproval(java.lang.String)
     */
    public boolean shouldVendorRouteForApproval(String documentId) {
        LOG.debug( "Entering shouldVendorRouteForApproval." );
        boolean shouldRoute = true;
        MaintenanceDocument newDoc = null;
        try {
            newDoc = (MaintenanceDocument)documentService.getByDocumentHeaderId( documentId );           
        } catch( WorkflowException we ) {
            throw new RuntimeException("A WorkflowException was thrown which prevented the loading of " + "the comparison document (" + documentId + ")", we);
        }
        
        if( ObjectUtils.isNotNull( newDoc ) ) {
            
            VendorDetail oldVDtl = (VendorDetail)( newDoc.getOldMaintainableObject().getBusinessObject() );
            VendorHeader oldVHdr = oldVDtl.getVendorHeader();
            VendorDetail newVDtl = (VendorDetail)( newDoc.getNewMaintainableObject().getBusinessObject() );
            VendorHeader newVHdr = newVDtl.getVendorHeader();          
            
            if( ( ObjectUtils.isNotNull( oldVHdr ) ) && ( ObjectUtils.isNotNull( oldVDtl ) ) &&
                ( ObjectUtils.isNotNull( newVHdr ) ) && ( ObjectUtils.isNotNull( newVDtl ) ) ) {
                shouldRoute = !( noRouteSignificantChangeOccurred( newVDtl, newVHdr, oldVDtl, oldVHdr ) );
            } else {
                shouldRoute = true;
            }
        }
        LOG.debug( "Exiting shouldVendorRouteForApproval." );
        return shouldRoute;
    }
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#noRouteSignificantChangeOccurred(org.kuali.module.vendor.bo.VendorDetail, org.kuali.module.vendor.bo.VendorHeader, org.kuali.module.vendor.bo.VendorDetail, org.kuali.module.vendor.bo.VendorHeader)
     */
    public boolean noRouteSignificantChangeOccurred( VendorDetail newVDtl, VendorHeader newVHdr, 
            VendorDetail oldVDtl, VendorHeader oldVHdr ) {
        LOG.debug( "Entering noRouteSignificantChangeOccurred." );
        
        //The subcollections which are being compared here must implement VendorRoutingComparable.
        boolean unchanged = ( ( oldVHdr.isEqualForRouting( newVHdr ) ) && 
        ( equalMemberLists( oldVHdr.getVendorSupplierDiversities(), 
                newVHdr.getVendorSupplierDiversities() ) ) &&
        ( oldVDtl.isEqualForRouting( newVDtl ) ) &&
        ( equalMemberLists( oldVDtl.getVendorAddresses(), newVDtl.getVendorAddresses() ) ) &&
        ( equalMemberLists( oldVDtl.getVendorContracts(), newVDtl.getVendorContracts() ) ) &&
        ( equalMemberLists( oldVDtl.getVendorShippingSpecialConditions(), 
                newVDtl.getVendorShippingSpecialConditions() ) ) );
        
        LOG.debug( "Exiting noRouteSignificantChangeOccurred." );
        return unchanged;
    }
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#equalMemberLists(java.util.List, java.util.List)
     */
    public boolean equalMemberLists( List<? extends VendorRoutingComparable> list_a, 
            List<? extends VendorRoutingComparable> list_b ) {
        LOG.debug( "Entering equalMemberLists." );
        boolean result = true;
        int listSize = list_a.size();
        if( listSize != list_b.size() ) {
            LOG.debug("Exiting equalMemberLists because list sizes are unequal.");
            return false;
        }       
        VendorRoutingComparable aMember = null;
        for( int i = 0; i < listSize; i++ ) { 
            aMember = list_a.get( i );
            if( !aMember.isEqualForRouting( list_b.get( i ) ) ) {
                result = false;
                break;
            }
        }
        LOG.debug( "Exiting equalMemberLists." );
        return result;
    } 
    
    /**
     * @see org.kuali.module.vendor.service.VendorService#isVendorInstitutionEmployee(java.lang.Integer)
     */
    // TODO delyea - is this right?  Logic copied from KualiPayeeTaxIdAttribute
    public boolean isVendorInstitutionEmployee(Integer vendorHeaderGeneratedIdentifier) {
        VendorDetail vendorToUse = getParentVendor(vendorHeaderGeneratedIdentifier);
        if (ObjectUtils.isNull(vendorToUse)) {
            String errorMsg = "Vendor with header generated id '" + vendorHeaderGeneratedIdentifier + "' cannot be found in the system";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        if (VendorConstants.TAX_TYPE_SSN.equals(vendorToUse.getVendorHeader().getVendorTaxTypeCode())) {
            String ssnTaxId = vendorToUse.getVendorHeader().getVendorTaxNumber();
            if (StringUtils.isNotBlank(ssnTaxId)) {
                try {
                    UniversalUser user = universalUserService.getUniversalUser(new PersonTaxId(ssnTaxId));
                    return (user.isFaculty() || user.isStaff() || user.isAffiliate()) && !configService.failsRule(KFSConstants.CORE_NAMESPACE, KFSConstants.Components.KUALI_MODULE_USER, KFSConstants.ALLOWED_EMPLOYEE_STATUS_RULE,user.getEmployeeStatusCode());
                }
                catch (UserNotFoundException e) {
                    // user is not in the system... assume non-employee
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.module.vendor.service.VendorService#isVendorNonResidentAlien(java.lang.Integer)
     */
    public boolean isVendorForeign(Integer vendorHeaderGeneratedIdentifier) {
        VendorDetail vendorToUse = getParentVendor(vendorHeaderGeneratedIdentifier);
        if (ObjectUtils.isNull(vendorToUse)) {
            String errorMsg = "Vendor with header generated id '" + vendorHeaderGeneratedIdentifier + "' cannot be found in the system";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return vendorToUse.getVendorHeader().getVendorForeignIndicator();
    }

    public KualiConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
}