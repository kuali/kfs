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
package org.kuali.kfs.vnd.fixture;

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorShippingSpecialCondition;
import org.kuali.kfs.vnd.businessobject.VendorSupplierDiversity;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.VendorParts;

public enum VendorRoutingChangesFixture {

    COMPLETE_NO_CHANGES(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1), COMPLETE_VH_CHANGE(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE2, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1), COMPLETE_VSD1_CHANGE(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1,
            VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE2, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1), COMPLETE_VSSC1_CHANGE(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE2, VendorParts.CODE1), COMPLETE_VA2_CHANGE(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE2, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1,
            VendorParts.CODE1, VendorParts.CODE1), NULL_OLDS(null, null, null, null, null, null, null, null, null, null, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1), NULL_NEWS(VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.NAME, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, VendorParts.CODE1, null, null, null, null, null, null, null, null, null, null), NULL_ALL(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ;

    private String oldVHdr_vtCd;
    private String oldSupDiv1_vsdCd;
    private String oldSupDiv2_vsdCd;
    private String oldVDtl_remitName;
    private String oldVAddr1_vatCd;
    private String oldVAddr2_vatCd;
    private String oldVCtr1_pocsCd;
    private String oldVCtr2_pocsCd;
    private String oldVSSCond1_vsscCd;
    private String oldVSSCond2_vsscCd;

    private String newVHdr_vtCd;
    private String newSupDiv1_vsdCd;
    private String newSupDiv2_vsdCd;
    private String newVDtl_remitName;
    private String newVAddr1_vatCd;
    private String newVAddr2_vatCd;
    private String newVCtr1_pocsCd;
    private String newVCtr2_pocsCd;
    private String newVSSCond1_vsscCd;
    private String newVSSCond2_vsscCd;

    /**
     * This constructor can be used by broad tests of the change detection logic.
     */
    private VendorRoutingChangesFixture(String oldVHdr_vtCd, String oldSupDiv1_vsdCd, String oldSupDiv2_vsdCd, String oldVDtl_remitName, String oldVAddr1_vatCd, String oldVAddr2_vatCd, String oldVCtr1_pocsCd, String oldVCtr2_pocsCd, String oldVSSCond1_vsscCd, String oldVSSCond2_vsscCd, String newVHdr_vtCd, String newSupDiv1_vsdCd, String newSupDiv2_vsdCd, String newVDtl_remitName, String newVAddr1_vatCd, String newVAddr2_vatCd, String newVCtr1_pocsCd, String newVCtr2_pocsCd, String newVSSCond1_vsscCd, String newVSSCond2_vsscCd) {
        this.oldVHdr_vtCd = oldVHdr_vtCd;
        this.oldSupDiv1_vsdCd = oldSupDiv1_vsdCd;
        this.oldSupDiv2_vsdCd = oldSupDiv2_vsdCd;
        this.oldVDtl_remitName = oldVDtl_remitName;
        this.oldVAddr1_vatCd = oldVAddr1_vatCd;
        this.oldVAddr2_vatCd = oldVAddr2_vatCd;
        this.oldVCtr1_pocsCd = oldVCtr1_pocsCd;
        this.oldVCtr2_pocsCd = oldVCtr2_pocsCd;
        this.oldVSSCond1_vsscCd = oldVSSCond1_vsscCd;
        this.oldVSSCond2_vsscCd = oldVSSCond2_vsscCd;
        this.newVHdr_vtCd = newVHdr_vtCd;
        this.newSupDiv1_vsdCd = newSupDiv1_vsdCd;
        this.newSupDiv2_vsdCd = newSupDiv2_vsdCd;
        this.newVDtl_remitName = newVDtl_remitName;
        this.newVAddr1_vatCd = newVAddr1_vatCd;
        this.newVAddr2_vatCd = newVAddr2_vatCd;
        this.newVCtr1_pocsCd = newVCtr1_pocsCd;
        this.newVCtr2_pocsCd = newVCtr2_pocsCd;
        this.newVSSCond1_vsscCd = newVSSCond1_vsscCd;
        this.newVSSCond2_vsscCd = newVSSCond2_vsscCd;
    }

    public void populate(VendorDetail oldVDtl, VendorHeader oldVHdr, VendorDetail newVDtl, VendorHeader newVHdr) {

        // Old Vendor Header
        oldVHdr.setVendorTypeCode(this.oldVHdr_vtCd);

        VendorSupplierDiversity oldVsd1 = new VendorSupplierDiversity();
        oldVsd1.setVendorSupplierDiversityCode(this.oldSupDiv1_vsdCd);
        VendorSupplierDiversity oldVsd2 = new VendorSupplierDiversity();
        oldVsd2.setVendorSupplierDiversityCode(this.oldSupDiv2_vsdCd);
        oldVHdr.getVendorSupplierDiversities().add(0, oldVsd1);
        oldVHdr.getVendorSupplierDiversities().add(1, oldVsd2);

        // Old Vendor Detail
        oldVDtl.setVendorRemitName(this.oldVDtl_remitName);

        VendorAddress oldVa1 = new VendorAddress();
        oldVa1.setVendorAddressTypeCode(this.oldVAddr1_vatCd);
        VendorAddress oldVa2 = new VendorAddress();
        oldVa2.setVendorAddressTypeCode(this.oldVAddr2_vatCd);
        oldVDtl.getVendorAddresses().add(0, oldVa1);
        oldVDtl.getVendorAddresses().add(0, oldVa2);

        VendorContract oldVc1 = new VendorContract();
        oldVc1.setPurchaseOrderCostSourceCode(this.oldVCtr1_pocsCd);
        VendorContract oldVc2 = new VendorContract();
        oldVc2.setPurchaseOrderCostSourceCode(this.oldVCtr2_pocsCd);
        oldVDtl.getVendorContracts().add(0, oldVc1);
        oldVDtl.getVendorContracts().add(1, oldVc2);

        VendorShippingSpecialCondition oldVssc1 = new VendorShippingSpecialCondition();
        oldVssc1.setVendorShippingSpecialConditionCode(this.oldVSSCond1_vsscCd);
        VendorShippingSpecialCondition oldVssc2 = new VendorShippingSpecialCondition();
        oldVssc2.setVendorShippingSpecialConditionCode(this.oldVSSCond2_vsscCd);
        oldVDtl.getVendorShippingSpecialConditions().add(0, oldVssc1);
        oldVDtl.getVendorShippingSpecialConditions().add(1, oldVssc2);

        // New Vendor Header
        newVHdr.setVendorTypeCode(this.newVHdr_vtCd);

        VendorSupplierDiversity newVsd1 = new VendorSupplierDiversity();
        newVsd1.setVendorSupplierDiversityCode(this.newSupDiv1_vsdCd);
        VendorSupplierDiversity newVsd2 = new VendorSupplierDiversity();
        newVsd2.setVendorSupplierDiversityCode(this.newSupDiv2_vsdCd);
        newVHdr.getVendorSupplierDiversities().add(0, newVsd1);
        newVHdr.getVendorSupplierDiversities().add(1, newVsd2);

        // New Vendor Detail
        newVDtl.setVendorRemitName(this.newVDtl_remitName);

        VendorAddress newVa1 = new VendorAddress();
        newVa1.setVendorAddressTypeCode(this.newVAddr1_vatCd);
        VendorAddress newVa2 = new VendorAddress();
        newVa2.setVendorAddressTypeCode(this.newVAddr2_vatCd);
        newVDtl.getVendorAddresses().add(0, newVa1);
        newVDtl.getVendorAddresses().add(0, newVa2);

        VendorContract newVc1 = new VendorContract();
        newVc1.setPurchaseOrderCostSourceCode(this.newVCtr1_pocsCd);
        VendorContract newVc2 = new VendorContract();
        newVc2.setPurchaseOrderCostSourceCode(this.newVCtr2_pocsCd);
        newVDtl.getVendorContracts().add(0, newVc1);
        newVDtl.getVendorContracts().add(1, newVc2);

        VendorShippingSpecialCondition newVssc1 = new VendorShippingSpecialCondition();
        newVssc1.setVendorShippingSpecialConditionCode(this.newVSSCond1_vsscCd);
        VendorShippingSpecialCondition newVssc2 = new VendorShippingSpecialCondition();
        newVssc2.setVendorShippingSpecialConditionCode(this.newVSSCond2_vsscCd);
        newVDtl.getVendorShippingSpecialConditions().add(0, newVssc1);
        newVDtl.getVendorShippingSpecialConditions().add(1, newVssc2);
    }

}
