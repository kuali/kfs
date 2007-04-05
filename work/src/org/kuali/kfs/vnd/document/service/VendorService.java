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
package org.kuali.module.vendor.service;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.util.VendorRoutingComparable;

public interface VendorService {

    public void saveVendorHeader(VendorDetail vendorDetail);

    public VendorDetail getVendorDetail(Integer headerId, Integer detailId);

    /**
     * This method gets the apo limit for a given vendor contract. If the contract has a contractOrg and the contractOrg is not
     * excluded, then return the apo limit for the contractOrg. If there is no contractOrg for this contract or the contractOrg is
     * excluded, then return the apo limit from the contract.
     */
    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org);

    /**
     * This method retrieves the B2B Contract for the given Vendor (see method in VendorDao for criteria).
     * 
     * @param vendorDetail      Vendor info
     * @param campus            Campus
     * @return VendorContract   B2B Contract for given vendor
     */
    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus);

    /**
     * This method finds the addresses for the given vendor and then calls the method to determine the default address from this
     * list.
     * 
     * @param vendorHeaderId    Integer - Header ID of vendor.
     * @param vendorDetailId    Integer - Detail ID of vendor.
     * @param addressType       String - Address type of desired default.
     * @param campus            String - Campus of desired default.
     * @return VendorAddress    Desired default address; return null is possible if no defaults set.
     */
    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus);

    /**
     * This method finds the default address for the given addressType and campus from the address list passed in based on the
     * following logic: 
     * 
     * 1) The allDefaultAddress is defined by defaultAddressIndicator on VendorAddress. 
     * 2) If campus passed in is null, return allDefaultAddress if found. 
     * 3) If campus passed in is not null, look in campus lists of addresses to see if given campus is found for the 
     *    given address type. If match found, return address. If no match found, return allDefaultAddress.
     * 
     * @param addresses         List of addresses for a vendor.
     * @param addressType       String - Address type of the desired default sought.
     * @param campus            String - Campus of the desired default sought.
     * @return VendorAddress    Desired default address; return null is possible if no defaults set.
     */
    public VendorAddress getVendorDefaultAddress(List<VendorAddress> addresses, String addressType, String campus);

    /**
     * This method checks to see if a the Vendor Document associated with the given document ID should route
     * to the route path branch in workflow where the document will stop for approvals.
     * 
     * @param documentId
     * @return true if the vendor should be take the approval patch... false if the vendor can be 'auto approved'
     */
    public boolean shouldVendorRouteForApproval(String documentId);
    
    /**
     * This method compares lists which have an isEqualForRouting method by using that method.
     * An Equals() method would be wrong for the purpose of comparing these because we want to
     * compare only using certain specified attributes, which is what our isEqualForRouting
     * methods will do. 
     * 
     * @param   list_a  A List which implements VendorRoutingComparable (specifies isEqualForRouting)
     * @param   list_b  Another such list
     * @return  True if all the member objects in the given lists are equal (as far as routing 
     *          is concerned) at the same locations in the lists.
     */
    public boolean equalMemberLists( List<? extends VendorRoutingComparable> list_a, 
            List<? extends VendorRoutingComparable> list_b );
    
    /**
     * This method is the place to put the calls to equality checks that are needed when deciding 
     * whether to route a vendor for approval or directly to final status on the basis of what has
     * changed.  This method has been split out from shouldVendorRouteForApproval for the
     * convenience of unit testing, but might be useful for other purposes.
     * 
     * @param   newVDtl   A VendorDetail object representing the state of the proposed change
     * @param   newVHdr   A VendorHeader object representing the state of the proposed change
     * @param   oldVDtl   A VendorDetail object from before the change
     * @param   oldVHdr   A VendorHeader object from before the change
     * @return  True if no route-significant change occurred
     */
    public boolean noRouteSignificantChangeOccurred( VendorDetail newVDtl, VendorHeader newVHdr, 
            VendorDetail oldVDtl, VendorHeader oldVHdr );
}
