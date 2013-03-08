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
package org.kuali.kfs.vnd.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorRoutingComparable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;

public interface VendorService {

    public void saveVendorHeader(VendorDetail vendorDetail);

    /**
     * get the vendor detail with the given vendor number
     * @param vendorNumber the given vendor number
     * @return the vendor detail with the given vendor number if the vendor exists, otherwise, return null
     */
    public VendorDetail getByVendorNumber(String vendorNumber);

    public VendorDetail getVendorDetail(String vendorNumber);

    public VendorDetail getVendorDetail(Integer headerId, Integer detailId);

    /**
     * Retrieves the VendorDetail which is the parent vendor with the given headerId. This is the method to use when working
     * backward from a division vendor to its parent vendor. This method throws RuntimeExceptions if there are found to be no parent
     * vendor or more than one parent vendor for the associated corporate structure.
     *
     * @param vendorHeaderGeneratedIdentifier The Header Id in Integer form
     * @return The VendorDetail of the parent vendor associated with the corporate structure indicated by the given Header Id, or
     *         null if there are no vendors associated with it.
     */
    public VendorDetail getParentVendor(Integer vendorHeaderGeneratedIdentifier);

    /**
     * Retrieves the VendorDetail using its vendorDunsNumber.
     * @param vendorDunsNumber the vendor's DUN number.
     * @return
     */
    public VendorDetail getVendorByDunsNumber(String vendorDunsNumber);

    /**
     * Gets the apo limit for the given parameters using the following logic:<br>
     * <br>
     * First it checks to see if an existing {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} object exists for the
     * associated parameters. If one exists and it is not excluded (see
     * {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization#isVendorContractExcludeIndicator()}) this will return the value
     * of {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization#getVendorContractPurchaseOrderLimitAmount()}.<br>
     * <br>
     * If an associated {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} object cannot be found then a valid
     * {@link org.kuali.kfs.vnd.businessobject.VendorContract} object will be sought. If one is found this method will return the value of
     * {@link org.kuali.kfs.vnd.businessobject.VendorContract#getOrganizationAutomaticPurchaseOrderLimit()}.<br>
     * <br>
     * If no valid {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} or
     * {@link org.kuali.kfs.vnd.businessobject.VendorContract} objects can be found for the given parameters this method will return null.
     *
     * @param contractId id used to find {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} object and
     *        {@link org.kuali.kfs.vnd.businessobject.VendorContract} object
     * @param chart chart code for use in finding {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} object
     * @param org org code for use in finding {@link org.kuali.kfs.vnd.businessobject.VendorContractOrganization} object
     * @return the automatic purchase order limit amount from the contract found using the parameters. If parameters do not find
     *         valid vendor contract objects then null is returned.
     */
    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org);

    /**
     * Finds the addresses for the given vendor and then calls the method to determine the default address from this list.
     *
     * @param vendorHeaderId Integer - Header ID of vendor.
     * @param vendorDetailId Integer - Detail ID of vendor.
     * @param addressType String - Address type of desired default.
     * @param campus String - Campus of desired default.
     * @return VendorAddress Desired default address; return null is possible if no defaults set.
     */
    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus);

    /**
     * Finds the default address for the given addressType and campus from the address list passed in based on the following logic:
     * 1) The allDefaultAddress is defined by defaultAddressIndicator on VendorAddress. 2) If campus passed in is null, return
     * allDefaultAddress if found. 3) If campus passed in is not null, look in campus lists of addresses to see if given campus is
     * found for the given address type. If match found, return address. If no match found, return allDefaultAddress.
     *
     * @param addresses List of addresses for a vendor.
     * @param addressType String - Address type of the desired default sought.
     * @param campus String - Campus of the desired default sought.
     * @return VendorAddress Desired default address; return null is possible if no defaults set.
     */
    public VendorAddress getVendorDefaultAddress(Collection<VendorAddress> addresses, String addressType, String campus);

    /**
     * Checks to see if a the Vendor Document associated with the given document ID should route to the route path branch in
     * workflow where the document will stop for approvals.
     *
     * @param documentId
     * @return true if the vendor should be take the approval patch... false if the vendor can be 'auto approved'
     */
    public boolean shouldVendorRouteForApproval(String documentId);

    /**
     * Compares lists which have an isEqualForRouting method by using that method. An Equals() method would be wrong for the purpose
     * of comparing these because we want to compare only using certain specified attributes, which is what our isEqualForRouting
     * methods will do.
     *
     * @param list_a A List which implements VendorRoutingComparable (specifies isEqualForRouting)
     * @param list_b Another such list
     * @return True if all the member objects in the given lists are equal (as far as routing is concerned) at the same locations in
     *         the lists.
     */
    public boolean equalMemberLists(List<? extends VendorRoutingComparable> list_a, List<? extends VendorRoutingComparable> list_b);

    /**
     * This method is the place to put the calls to equality checks that are needed when deciding whether to route a vendor for
     * approval or directly to final status on the basis of what has changed. This method has been split out from
     * shouldVendorRouteForApproval for the convenience of unit testing, but might be useful for other purposes.
     *
     * @param newVDtl A VendorDetail object representing the state of the proposed change
     * @param newVHdr A VendorHeader object representing the state of the proposed change
     * @param oldVDtl A VendorDetail object from before the change
     * @param oldVHdr A VendorHeader object from before the change
     * @return True if no route-significant change occurred
     */
    public boolean noRouteSignificantChangeOccurred(VendorDetail newVDtl, VendorHeader newVHdr, VendorDetail oldVDtl, VendorHeader oldVHdr);

    /**
     * Indicates whether the vendor identified by the given <code>vendorHeaderGeneratedIdentifier</code> is an employee of the
     * institution. The vendor must have a valid tax id and it must be of type SSN (see
     * {@link org.kuali.kfs.vnd.VendorConstants#TAX_TYPE_SSN}).
     *
     * @param vendorHeaderGeneratedIdentifier The Header Id in Integer form
     * @return true if the vendor identified by the <code>vendorHeaderGeneratedIdentifier</code> given is an employee of the
     *         institution
     */
    public boolean isVendorInstitutionEmployee(Integer vendorHeaderGeneratedIdentifier);

    /**
     * Indicates whether the vendor identified by the given <code>vendorHeaderGeneratedIdentifier</code> is a non-resident alien
     * by checking the value of {@link org.kuali.kfs.vnd.businessobject.VendorHeader#getVendorForeignIndicator()}.
     *
     * @param vendorHeaderGeneratedIdentifier The Header Id in Integer form
     * @return true if the vendor identified by the <code>vendorHeaderGeneratedIdentifier</code> given is valid and is marked as a
     *         foreign vendor
     */
    public boolean isVendorForeign(Integer vendorHeaderGeneratedIdentifier);

    /**
     * Indicates whether the vendor identified by the given <code>vendorHeaderGeneratedIdentifier</code> is a subject payment vendor
     * by checking the value of {@link org.kuali.kfs.vnd.businessobject.VendorHeader#getVendorTypeCode()} to see if it equals "SP".
     *
     * @param vendorHeaderGeneratedIdentifier The Header Id in Integer form
     * @return true if the vendor identified by the <code>vendorHeaderGeneratedIdentifier</code> given is valid and has a vendor type code of "SP"
     */
    public boolean isSubjectPaymentVendor(Integer vendorHeaderGeneratedIdentifier);

    /**
     * Indicates whether the vendor identified by the given <code>vendorHeaderGeneratedIdentifier</code> is a revolving fund code vendor
     * by checking the value of {@link org.kuali.kfs.vnd.businessobject.VendorHeader#getVendorTypeCode()} to see if it equals "RF".
     *
     * @param vendorHeaderGeneratedIdentifier The Header Id in Integer form
     * @return true if the vendor identified by the <code>vendorHeaderGeneratedIdentifier</code> given is valid and has a vendor type code of "RF"
     */
    public boolean isRevolvingFundCodeVendor(Integer vendorHeaderGeneratedIdentifier);

    /**
     * This method retrieves the B2B Contract for the given Vendor (see method in VendorDao for criteria).
     *
     * @param vendorDetail Vendor info
     * @param campus Campus
     * @return VendorContract B2B Contract for given vendor
     */
    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus);

    List<Note> getVendorNotes(VendorDetail vendorDetail);

    /**
     * REQ should have failed APO rules and routed to Contract Mgr Assignment since the REQ had a contracted vendor
     * used where the contract end date is expired (regardless of the Active Indicator).
     * Notes tab should reflect that the vendor on REQ has an expired contract.
     *
     * @param poDocument
     * @param vendorContractGeneratedIdentifier
     * @param vendorDetail
     * @return true if vendor contract expired end date is not expired else return false.
     */
    public boolean isVendorContractExpired(Document document, Integer vendorContractGeneratedIdentifier, VendorDetail vendorDetail);

}
