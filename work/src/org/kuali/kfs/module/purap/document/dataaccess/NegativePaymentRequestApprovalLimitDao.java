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
package org.kuali.kfs.module.purap.document.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Negative Payment Request Approval Limit DAO Interface.
 */
public interface NegativePaymentRequestApprovalLimitDao {

    /**
     * Find limits by chart.
     * 
     * @param chartCode - chart of accounts code
     * @return - collection of negative payment request approval limits
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode);

    /**
     * Find limits by chart and account.
     * 
     * @param chartCode - chart of accounts code
     * @param accountNumber
     * @return - collection of negative payment request approval limits
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber);

    /**
     * Find limits by chart and organization.
     * 
     * @param chartCode - chart of accounts code
     * @param organizationCode - organization code
     * @return - collection of negative payment request approval limits
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode);

    /**
     * Retrieve a collection of NegativePaymentRequestApprovalLimit where the NegativePaymentRequestApprovalLimitAmount is greater
     * than the limit passed in.  (Used for Testing.)
     * 
     * @param limit - lower limit
     * @return - collection of negative payment request approval limits
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit);

    /**
     * Retrieve a collection of NegativePaymentRequestApprovalLimit where the NegativePaymentRequestApprovalLimitAmount is less than
     * the limit passed in.  (Used for Testing.)
     * 
     * @param limit - upper limit
     * @return - collection of negative payment request approval limits
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit);

}
