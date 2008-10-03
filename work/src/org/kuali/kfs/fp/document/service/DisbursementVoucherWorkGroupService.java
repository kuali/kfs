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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.sys.businessobject.FinancialSystemUser;

public interface DisbursementVoucherWorkGroupService {

    /**
     * Checks if the given user is a member of the disbursement voucher tax workgroup.
     * 
     * @param financialSystemUser the given financial system user
     * @return true if user is in group
     */
    public boolean isUserInTaxGroup(FinancialSystemUser financialSystemUser);

    /**
     * Checks if the given user is a member of the disbursement voucher travel workgroup.
     * 
     * @param financialSystemUser the given financial system user
     * @return true if user is in group
     */
    public boolean isUserInTravelGroup(FinancialSystemUser financialSystemUser);

    /**
     * Checks if the given user is a member of the disbursement voucher foreign draft workgroup.
     * 
     * @param financialSystemUser the given financial system user
     * @return true if user is in group
     */
    public boolean isUserInFRNGroup(FinancialSystemUser financialSystemUser);

    /**
     * Checks if the given user is a member of the disbursement voucher wire transfer workgroup.
     * 
     * @param financialSystemUser the given financial system user
     * @return true if user is in group
     */
    public boolean isUserInWireGroup(FinancialSystemUser financialSystemUser);

    /**
     * Checks if the given user is a member of the disbursement voucher admin workgroup.
     * 
     * @param financialSystemUser the given financial system user
     * @return true if user is in group
     */
    public boolean isUserInDvAdminGroup(FinancialSystemUser financialSystemUser);
}
