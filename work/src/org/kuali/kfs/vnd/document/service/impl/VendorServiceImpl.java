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
package org.kuali.kfs.vnd.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorContractOrganization;
import org.kuali.kfs.vnd.businessobject.VendorDefaultAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorRoutingComparable;
import org.kuali.kfs.vnd.dataaccess.VendorDao;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class VendorServiceImpl implements VendorService {
    private static Logger LOG = Logger.getLogger(VendorServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PersistenceService persistenceService;
    private VendorDao vendorDao;

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#saveVendorHeader(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    public void saveVendorHeader(VendorDetail vendorDetail) {
        businessObjectService.save(vendorDetail.getVendorHeader());
    }
    
    /**
     * @see org.kuali.kfs.vnd.document.service.getByVendorNumber(String)
     */
    public VendorDetail getByVendorNumber(String vendorNumber) {
        return this.getVendorDetail(vendorNumber);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDetail(String)
     */
    public VendorDetail getVendorDetail(String vendorNumber) {
        LOG.debug("Entering getVendorDetail for vendorNumber: " + vendorNumber);
        int dashInd = vendorNumber.indexOf("-");
        if (vendorNumber.length() >= dashInd) {
            return getVendorDetail(new Integer(vendorNumber.substring(0, dashInd)), new Integer(vendorNumber.substring(dashInd + 1)));
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDetail(java.lang.Integer, java.lang.Integer)
     */
    public VendorDetail getVendorDetail(Integer headerId, Integer detailId) {
        LOG.debug("Entering getVendorDetail for headerId:" + headerId + ", detailId:" + detailId);
        Map keys = new HashMap();
        keys.put("vendorHeaderGeneratedIdentifier", headerId);
        keys.put("vendorDetailAssignedIdentifier", detailId);
        return (VendorDetail) businessObjectService.findByPrimaryKey(VendorDetail.class, keys);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getApoLimitFromContract(Integer, String, String)
     */
    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org) {
        LOG.debug("Entering getApoLimitFromContract with contractId:" + contractId + ", chart:" + chart + ", org:" + org);

        // check for the special case of a contractOrg for this contract in the contract-orgs table
        if (ObjectUtils.isNotNull(contractId) && ObjectUtils.isNotNull(chart) && ObjectUtils.isNotNull(org)) {
            VendorContractOrganization exampleContractOrg = new VendorContractOrganization();
            exampleContractOrg.setVendorContractGeneratedIdentifier(contractId);
            exampleContractOrg.setChartOfAccountsCode(chart);
            exampleContractOrg.setOrganizationCode(org);
            Map orgKeys = persistenceService.getPrimaryKeyFieldValues(exampleContractOrg);
            VendorContractOrganization contractOrg = (VendorContractOrganization) businessObjectService.findByPrimaryKey(VendorContractOrganization.class, orgKeys);
            // if the contractOrg is found 
            if (ObjectUtils.isNotNull(contractOrg)) {
                // if the contractOrg is excluded, return the special value of the APO limit from the table                     
                if (!contractOrg.isVendorContractExcludeIndicator()) {
                    return contractOrg.getVendorContractPurchaseOrderLimitAmount();
                }
                // otherwise return null, as if there's no contract
                else return null;
            }
        }

        // didn't search the contract-org table or not found in the table but contract exists, return the default APO limit in contract
        if (ObjectUtils.isNotNull(contractId)) {
            VendorContract exampleContract = new VendorContract();
            exampleContract.setVendorContractGeneratedIdentifier(contractId);
            Map contractKeys = persistenceService.getPrimaryKeyFieldValues(exampleContract);
            VendorContract contract = (VendorContract) businessObjectService.findByPrimaryKey(VendorContract.class, contractKeys);
            if (ObjectUtils.isNotNull(contract)) {
                return contract.getOrganizationAutomaticPurchaseOrderLimit();
            }
        }

        // otherwise no APO limit found from contract
        return null;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getParentVendor(java.lang.Integer)
     */
    public VendorDetail getParentVendor(Integer vendorHeaderGeneratedIdentifier) {
        LOG.debug("Entering getParentVendor for vendorHeaderGeneratedIdentifier:" + vendorHeaderGeneratedIdentifier);
        Map criterion = new HashMap();
        criterion.put("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedIdentifier);
        List<VendorDetail> vendors = (List<VendorDetail>) businessObjectService.findMatching(VendorDetail.class, criterion);
        VendorDetail result = null;
        if (ObjectUtils.isNull(vendors)) {
            LOG.warn("Error: No vendors exist with vendor header " + vendorHeaderGeneratedIdentifier + ".");
        }
        else {
            for (VendorDetail vendor : vendors) {
                if (vendor.isVendorParentIndicator()) {
                    if (ObjectUtils.isNull(result)) {
                        result = vendor;
                    }
                    else {
                        LOG.error("Error: More than one parent vendor for vendor header " + vendorHeaderGeneratedIdentifier + ".");
                        throw new RuntimeException("Error: More than one parent vendor for vendor header " + vendorHeaderGeneratedIdentifier + ".");
                    }
                }
            }
            if (ObjectUtils.isNull(result)) {
                LOG.error("Error: No parent vendor for vendor header " + vendorHeaderGeneratedIdentifier + ".");
                throw new RuntimeException("Error: No parent vendor for vendor header " + vendorHeaderGeneratedIdentifier + ".");
            }
        }
        LOG.debug("Exiting getParentVendor normally.");
        return result;
    }
    
    /**
     *  @see org.kuali.kfs.vnd.document.service.VendorService#getVendorByDunsNumber(String)
     */
    public VendorDetail getVendorByDunsNumber(String vendorDunsNumber) {
        LOG.debug("Entering getVendorByDunsNumber for vendorDunsNumber:" + vendorDunsNumber);
        Map criteria = new HashMap();
        criteria.put(VendorPropertyConstants.VENDOR_DUNS_NUMBER, vendorDunsNumber);
        List<VendorDetail> vds = (List) businessObjectService.findMatching(VendorDetail.class, criteria);
        LOG.debug("Exiting getVendorByDunsNumber.");
        if (vds.size() < 1) {
            return null;
        }
        else {
            return vds.get(0);
        }
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDefaultAddress(Integer, Integer, String, String)
     */
    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus) {
        LOG.debug("Entering getVendorDefaultAddress for vendorHeaderId:" + vendorHeaderId + ", vendorDetailId:" + vendorDetailId + ", addressType:" + addressType + ", campus:" + campus);
        Map criteria = new HashMap();
        criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderId);
        criteria.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailId);
        criteria.put(VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, addressType);
        List<VendorAddress> addresses = (List) businessObjectService.findMatching(VendorAddress.class, criteria);
        LOG.debug("Exiting getVendorDefaultAddress.");
        return getVendorDefaultAddress(addresses, addressType, campus);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDefaultAddress(List, String, String)
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
     * @see org.kuali.kfs.vnd.document.service.VendorService#shouldVendorRouteForApproval(java.lang.String)
     */
    public boolean shouldVendorRouteForApproval(String documentId) {
        LOG.debug("Entering shouldVendorRouteForApproval.");
        boolean shouldRoute = true;
        MaintenanceDocument newDoc = null;
        try {
            newDoc = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentId);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("A WorkflowException was thrown which prevented the loading of " + "the comparison document (" + documentId + ")", we);
        }

        if (ObjectUtils.isNotNull(newDoc)) {

            VendorDetail oldVDtl = (VendorDetail) (newDoc.getOldMaintainableObject().getBusinessObject());
            VendorHeader oldVHdr = oldVDtl.getVendorHeader();
            VendorDetail newVDtl = (VendorDetail) (newDoc.getNewMaintainableObject().getBusinessObject());
            VendorHeader newVHdr = newVDtl.getVendorHeader();

            if ((ObjectUtils.isNotNull(oldVHdr)) && (ObjectUtils.isNotNull(oldVDtl)) && (ObjectUtils.isNotNull(newVHdr)) && (ObjectUtils.isNotNull(newVDtl))) {
                shouldRoute = !(noRouteSignificantChangeOccurred(newVDtl, newVHdr, oldVDtl, oldVHdr));
            }
            else {
                shouldRoute = true;
            }
        }
        LOG.debug("Exiting shouldVendorRouteForApproval.");
        return shouldRoute;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#noRouteSignificantChangeOccurred(org.kuali.kfs.vnd.businessobject.VendorDetail,
     *      org.kuali.kfs.vnd.businessobject.VendorHeader, org.kuali.kfs.vnd.businessobject.VendorDetail,
     *      org.kuali.kfs.vnd.businessobject.VendorHeader)
     */
    public boolean noRouteSignificantChangeOccurred(VendorDetail newVDtl, VendorHeader newVHdr, VendorDetail oldVDtl, VendorHeader oldVHdr) {
        LOG.debug("Entering noRouteSignificantChangeOccurred.");

        // The subcollections which are being compared here must implement VendorRoutingComparable.
        boolean unchanged = ((oldVHdr.isEqualForRouting(newVHdr)) && (equalMemberLists(oldVHdr.getVendorSupplierDiversities(), newVHdr.getVendorSupplierDiversities())) && (oldVDtl.isEqualForRouting(newVDtl)) && (equalMemberLists(oldVDtl.getVendorAddresses(), newVDtl.getVendorAddresses())) && (equalMemberLists(oldVDtl.getVendorContracts(), newVDtl.getVendorContracts())) && (equalMemberLists(oldVDtl.getVendorShippingSpecialConditions(), newVDtl.getVendorShippingSpecialConditions())));

        LOG.debug("Exiting noRouteSignificantChangeOccurred.");
        return unchanged;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#equalMemberLists(java.util.List, java.util.List)
     */
    public boolean equalMemberLists(List<? extends VendorRoutingComparable> list_a, List<? extends VendorRoutingComparable> list_b) {
        LOG.debug("Entering equalMemberLists.");
        boolean result = true;
        int listSize = list_a.size();
        if (listSize != list_b.size()) {
            LOG.debug("Exiting equalMemberLists because list sizes are unequal.");
            return false;
        }
        VendorRoutingComparable aMember = null;
        for (int i = 0; i < listSize; i++) {
            aMember = list_a.get(i);
            if (!aMember.isEqualForRouting(list_b.get(i))) {
                result = false;
                break;
            }
        }
        LOG.debug("Exiting equalMemberLists.");
        return result;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#isVendorInstitutionEmployee(java.lang.Integer)
     */
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
                List<Person> personList = SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(org.kuali.rice.kim.util.KimConstants.PersonExternalIdentifierTypes.TAX, ssnTaxId);
                if (personList != null && !personList.isEmpty()) {
                    return ObjectUtils.isNotNull(personList.get(0));
                } else {
                    // user is not in the system... assume non-person
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#isVendorNonResidentAlien(java.lang.Integer)
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
    
    /**
     * 
     * @see org.kuali.kfs.vnd.document.service.VendorService#isSubjectPaymentVendor(java.lang.Integer)
     */
    public boolean isSubjectPaymentVendor(Integer vendorHeaderGeneratedIdentifier) {
        VendorDetail vendorToUse = getParentVendor(vendorHeaderGeneratedIdentifier);
        if (ObjectUtils.isNull(vendorToUse)) {
            String errorMsg = "Vendor with header generated id '" + vendorHeaderGeneratedIdentifier + "' cannot be found in the system";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return VendorConstants.VendorTypes.SUBJECT_PAYMENT.equals(vendorToUse.getVendorHeader().getVendorTypeCode());
    }

    /**
     * 
     * @see org.kuali.kfs.vnd.document.service.VendorService#isRevolvingFundCodeVendor(java.lang.Integer)
     */
    public boolean isRevolvingFundCodeVendor(Integer vendorHeaderGeneratedIdentifier) {
        VendorDetail vendorToUse = getParentVendor(vendorHeaderGeneratedIdentifier);
        if (ObjectUtils.isNull(vendorToUse)) {
            String errorMsg = "Vendor with header generated id '" + vendorHeaderGeneratedIdentifier + "' cannot be found in the system";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return VendorConstants.VendorTypes.REVOLVING_FUND.equals(vendorToUse.getVendorHeader().getVendorTypeCode());
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorB2BContract(org.kuali.kfs.vnd.businessobject.VendorDetail, java.lang.String)
     */
    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus) {
        return vendorDao.getVendorB2BContract(vendorDetail, campus);
    }

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setVendorDao(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }
}

