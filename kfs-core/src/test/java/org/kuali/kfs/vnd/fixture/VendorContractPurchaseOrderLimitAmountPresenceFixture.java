/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.vnd.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorContractOrganization;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.ContractPOLimitAmts;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public enum VendorContractPurchaseOrderLimitAmountPresenceFixture {

    NO_EXCLUDES(ContractPOLimitAmts.highLimit, ContractPOLimitAmts.lowLimit, null, null, null, null), 
    TWO_N_EXCLUDES_ON_FIRST_TWO_LIMITS(ContractPOLimitAmts.highLimit, ContractPOLimitAmts.lowLimit, false, false, false, false), 
    TWO_N_EXCLUDES_ON_FIRST_LIMIT_ON_FIRST(ContractPOLimitAmts.highLimit, null, false, false, null, null), 
    ONE_Y_EXCLUDE_ON_FIRST_NO_LIMIT_ON_FIRST(null, null, true, false, null, null), ;

    private KualiDecimal limit1;
    private KualiDecimal limit2;
    private Boolean exclude11;
    private Boolean exclude12;
    private Boolean exclude21;
    private Boolean exclude22;

    private VendorContractPurchaseOrderLimitAmountPresenceFixture(KualiDecimal limit1, KualiDecimal limit2, Boolean exclude11, Boolean exclude12, Boolean exclude21, Boolean exclude22) {
        this.limit1 = limit1;
        this.limit2 = limit2;
        this.exclude11 = exclude11;
        this.exclude12 = exclude12;
        this.exclude21 = exclude21;
        this.exclude22 = exclude22;
    }

    public List populateContracts() {
        VendorContract contract1 = new VendorContract();
        VendorContract contract2 = new VendorContract();
        VendorContractOrganization org11 = new VendorContractOrganization();
        VendorContractOrganization org12 = new VendorContractOrganization();
        VendorContractOrganization org21 = new VendorContractOrganization();
        VendorContractOrganization org22 = new VendorContractOrganization();
        org11.setVendorContractPurchaseOrderLimitAmount(limit1);
        org12.setVendorContractPurchaseOrderLimitAmount(limit1);
        org21.setVendorContractPurchaseOrderLimitAmount(limit2);
        org22.setVendorContractPurchaseOrderLimitAmount(limit2);
        if (ObjectUtils.isNotNull(exclude11)) {
            org11.setVendorContractExcludeIndicator(exclude11);
        }
        if (ObjectUtils.isNotNull(exclude12)) {
            org12.setVendorContractExcludeIndicator(exclude12);
        }
        if (ObjectUtils.isNotNull(exclude21)) {
            org21.setVendorContractExcludeIndicator(exclude21);
        }
        if (ObjectUtils.isNotNull(exclude22)) {
            org22.setVendorContractExcludeIndicator(exclude22);
        }
        List<VendorContractOrganization> orgList1 = new ArrayList();
        orgList1.add(org11);
        orgList1.add(org12);
        List<VendorContractOrganization> orgList2 = new ArrayList();
        orgList2.add(org21);
        orgList2.add(org22);
        contract1.setVendorContractOrganizations(orgList1);
        contract2.setVendorContractOrganizations(orgList2);
        List<VendorContract> contracts = new ArrayList();
        contracts.add(contract1);
        contracts.add(contract2);
        return contracts;
    }
}
