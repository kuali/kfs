/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.vnd.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.KFSConstants;
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
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class VendorServiceImpl implements VendorService {
    private static final Logger LOG = Logger.getLogger(VendorServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected VendorDao vendorDao;
    protected NoteService noteService;

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#saveVendorHeader(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    @Override
    public void saveVendorHeader(VendorDetail vendorDetail) {
        businessObjectService.save(vendorDetail.getVendorHeader());
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.getByVendorNumber(String)
     */
    @Override
    public VendorDetail getByVendorNumber(String vendorNumber) {
        return getVendorDetail(vendorNumber);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDetail(String)
     */
    @Override
    public VendorDetail getVendorDetail(String vendorNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getVendorDetail for vendorNumber: " + vendorNumber);
        }
        if (StringUtils.isBlank(vendorNumber)) {
            return null;
        }

        int dashInd = vendorNumber.indexOf('-');
        // make sure there's at least one char before and after '-'
        if (dashInd > 0 && dashInd < vendorNumber.length() - 1) {
            try {
                Integer headerId = new Integer(vendorNumber.substring(0, dashInd));
                Integer detailId = new Integer(vendorNumber.substring(dashInd + 1));
                return getVendorDetail(headerId, detailId);
            }
            catch (NumberFormatException e) {
                // in case of invalid number format
                return null;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDetail(java.lang.Integer, java.lang.Integer)
     */
    @Override
    public VendorDetail getVendorDetail(Integer headerId, Integer detailId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getVendorDetail for headerId:" + headerId + ", detailId:" + detailId);
        }
        HashMap<String, Integer> keys = new HashMap<String, Integer>();
        keys.put("vendorHeaderGeneratedIdentifier", headerId);
        keys.put("vendorDetailAssignedIdentifier", detailId);
        return businessObjectService.findByPrimaryKey(VendorDetail.class, keys);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getApoLimitFromContract(Integer, String, String)
     */
    @Override
    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getApoLimitFromContract with contractId:" + contractId + ", chart:" + chart + ", org:" + org);
        }

        // check for the special case of a contractOrg for this contract in the contract-orgs table
        if (ObjectUtils.isNotNull(contractId) && ObjectUtils.isNotNull(chart) && ObjectUtils.isNotNull(org)) {
            Map<String,Object> pkFields = new HashMap<String, Object>(3);
            pkFields.put("vendorContractGeneratedIdentifier", contractId);
            pkFields.put("chartOfAccountsCode", chart);
            pkFields.put("organizationCode", org);
            VendorContractOrganization contractOrg = businessObjectService.findByPrimaryKey(VendorContractOrganization.class, pkFields);
            // if the contractOrg is found
            if (ObjectUtils.isNotNull(contractOrg)) {
                // if the contractOrg is excluded, return the special value of the APO limit from the table
                if (!contractOrg.isVendorContractExcludeIndicator()) {
                    return contractOrg.getVendorContractPurchaseOrderLimitAmount();
                }
                // otherwise return null, as if there's no contract
                else {
                    return null;
                }
            }
        }

        // didn't search the contract-org table or not found in the table but contract exists, return the default APO limit in
        // contract
        if ( contractId != null ) {
            VendorContract contract = businessObjectService.findBySinglePrimaryKey(VendorContract.class, contractId);
            if (contract != null) {
                return contract.getOrganizationAutomaticPurchaseOrderLimit();
            }
        }

        // otherwise no APO limit found from contract
        return null;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getParentVendor(java.lang.Integer)
     */
    @Override
    public VendorDetail getParentVendor(Integer vendorHeaderGeneratedIdentifier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getParentVendor for vendorHeaderGeneratedIdentifier:" + vendorHeaderGeneratedIdentifier);
        }
        Collection<VendorDetail> vendors = businessObjectService.findMatching(VendorDetail.class,
                Collections.singletonMap("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedIdentifier));
        VendorDetail result = null;
        if (vendors == null || vendors.isEmpty() ) {
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
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorByDunsNumber(String)
     */
    @Override
    public VendorDetail getVendorByDunsNumber(String vendorDunsNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getVendorByDunsNumber for vendorDunsNumber:" + vendorDunsNumber);
        }
        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put(VendorPropertyConstants.VENDOR_DUNS_NUMBER, vendorDunsNumber);
        Collection<VendorDetail> vds = businessObjectService.findMatching(VendorDetail.class, criteria);
        LOG.debug("Exiting getVendorByDunsNumber.");
        if (vds.size() < 1) {
            return null;
        }
        else {
            return vds.iterator().next();
        }
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDefaultAddress(Integer, Integer, String, String)
     */
    @Override
    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering getVendorDefaultAddress for vendorHeaderId:" + vendorHeaderId + ", vendorDetailId:" + vendorDetailId + ", addressType:" + addressType + ", campus:" + campus);
        }
        HashMap<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderId);
        criteria.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailId);
        criteria.put(VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, addressType);
        Collection<VendorAddress> addresses = businessObjectService.findMatching(VendorAddress.class, criteria);
        LOG.debug("Exiting getVendorDefaultAddress.");
        return getVendorDefaultAddress(addresses, addressType, campus);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#getVendorDefaultAddress(List, String, String)
     */
    @Override
    public VendorAddress getVendorDefaultAddress(Collection<VendorAddress> addresses, String addressType, String campus) {
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
    @Override
    public boolean shouldVendorRouteForApproval(String documentId) {
        MaintenanceDocument document = null;
        try {
            document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentId);
        } catch (WorkflowException we) {
            throw new RuntimeException("A WorkflowException was thrown which prevented the loading of the comparison document (" + documentId + ")", we);
        }

        if (document == null) {
            // this should never happen - unable to load document in routing
            LOG.error( "Unable to retrieve document in workflow: " + documentId);
            return false;
        }
        String maintenanceAction = document.getNewMaintainableObject().getMaintenanceAction();
        if ( StringUtils.equals(KRADConstants.MAINTENANCE_NEW_ACTION, maintenanceAction)
                || StringUtils.equals(KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION, maintenanceAction)
                || StringUtils.equals(KRADConstants.MAINTENANCE_COPY_ACTION, maintenanceAction) ) {
            return true;  // New vendor - impacting change by definition
        }
        VendorDetail oldVendorDetail = (VendorDetail)document.getOldMaintainableObject().getBusinessObject();
        if ( oldVendorDetail == null ) {
            // we can't compare - route for safety
            return true;
        }
        VendorHeader oldVendorHeader = oldVendorDetail.getVendorHeader();
        if ( ObjectUtils.isNull(oldVendorHeader) ) {
            // we can't compare - route for safety
            return true;
        }

        VendorDetail newVendorDetail = (VendorDetail)document.getNewMaintainableObject().getBusinessObject();
        if ( newVendorDetail == null ) {
            // we can't compare - route for safety
            return true;
        }
        VendorHeader newVendorHeader = newVendorDetail.getVendorHeader();

        if ( ObjectUtils.isNull(newVendorHeader) ) {
            // we can't compare - route for safety
            return true;
        }
        return !noRouteSignificantChangeOccurred(newVendorDetail, newVendorHeader, oldVendorDetail, oldVendorHeader);
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#noRouteSignificantChangeOccurred(org.kuali.kfs.vnd.businessobject.VendorDetail,
     *      org.kuali.kfs.vnd.businessobject.VendorHeader, org.kuali.kfs.vnd.businessobject.VendorDetail,
     *      org.kuali.kfs.vnd.businessobject.VendorHeader)
     */
    @Override
    public boolean noRouteSignificantChangeOccurred(VendorDetail newVDtl, VendorHeader newVHdr, VendorDetail oldVDtl, VendorHeader oldVHdr) {
        LOG.debug("Entering noRouteSignificantChangeOccurred.");

        // The subcollections which are being compared here must implement VendorRoutingComparable.
        boolean unchanged = ((oldVHdr.isEqualForRouting(newVHdr))
                && (equalMemberLists(oldVHdr.getVendorSupplierDiversities(), newVHdr.getVendorSupplierDiversities()))
                && (oldVDtl.isEqualForRouting(newVDtl))
                && (equalMemberLists(oldVDtl.getVendorAddresses(), newVDtl.getVendorAddresses()))
                && (equalMemberLists(oldVDtl.getVendorContracts(), newVDtl.getVendorContracts()))
                && (equalMemberLists(oldVDtl.getVendorShippingSpecialConditions(), newVDtl.getVendorShippingSpecialConditions())));

        LOG.debug("Exiting noRouteSignificantChangeOccurred.");
        return unchanged;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#equalMemberLists(java.util.List, java.util.List)
     */
    @Override
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
    @Override
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
                List<Person> personList = SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(org.kuali.rice.kim.api.KimConstants.PersonExternalIdentifierTypes.TAX, ssnTaxId);
                if (personList != null && !personList.isEmpty()) {
                    return ObjectUtils.isNotNull(personList.get(0));
                }
                else {
                    // user is not in the system... assume non-person
                    return false;
                }
            }
        }
        return false;
    }

    public void createVendorNote(VendorDetail vendorDetail, String vendorNote) {
        try {
            if (StringUtils.isNotBlank(vendorNote)) {
                Note newBONote = new Note();
                newBONote.setNoteText(vendorNote);
                newBONote.setNotePostedTimestampToCurrent();
                newBONote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                Note note = noteService.createNote(newBONote, vendorDetail, GlobalVariables.getUserSession().getPrincipalId());
                noteService.save(note);
            }
        } catch (Exception e){
            throw new RuntimeException("Problems creating note for Vendor " + vendorDetail);
        }
    }

    @Override
    public List<Note> getVendorNotes(VendorDetail vendorDetail) {
        List<Note> notes = new ArrayList<Note>();
        if (ObjectUtils.isNotNull(vendorDetail)) {
            notes = noteService.getByRemoteObjectId(vendorDetail.getObjectId());
        }
        return notes;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#isVendorNonResidentAlien(java.lang.Integer)
     */
    @Override
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
     * @see org.kuali.kfs.vnd.document.service.VendorService#isSubjectPaymentVendor(java.lang.Integer)
     */
    @Override
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
     * @see org.kuali.kfs.vnd.document.service.VendorService#isRevolvingFundCodeVendor(java.lang.Integer)
     */
    @Override
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
     * @see org.kuali.kfs.vnd.document.service.VendorService#isVendorContractExpired(org.kuali.kfs.vnd.businessobject.VendorDetail)
     */
    @Override
    public boolean isVendorContractExpired(PurchasingDocument document, VendorDetail vendorDetail) {
        boolean isExpired = false;

        if (ObjectUtils.isNotNull(document.getVendorContractGeneratedIdentifier())) {
            VendorContract vendorContract = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(VendorContract.class, document.getVendorContractGeneratedIdentifier());
            Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
            List<Note> notes = document.getNotes();

            if ((currentDate.compareTo(vendorContract.getVendorContractEndDate()) > 0 && (vendorContract.getVendorContractExtensionDate() == null || currentDate.compareTo(vendorContract.getVendorContractExtensionDate()) > 0)) || !vendorContract.isActive()) {
                Note newNote = new Note();
                newNote.setNoteText("Vendor Contract: " + vendorContract.getVendorContractName() + " contract has expired contract end date.");
                newNote.setNotePostedTimestampToCurrent();
                newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                Note note = noteService.createNote(newNote, vendorDetail, GlobalVariables.getUserSession().getPrincipalId());
                notes.add(note);
                isExpired = true;
            }
        }

        return isExpired;
    }

    @Override
    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus) {
        return vendorDao.getVendorB2BContract(vendorDetail, campus, dateTimeService.getCurrentSqlDate());
    }
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;
    }
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    public void setVendorDao(VendorDao vendorDao) {
        this.vendorDao = vendorDao;
    }
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
