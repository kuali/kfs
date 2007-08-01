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
package org.kuali.module.purap.service;

import java.util.Collection;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;

public interface NegativePaymentRequestApprovalLimitService {

    /**
     * Find limits by chart.
     * 
     * @param chartCode
     * @return
     */
    Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode);
    
    /**
     * Find limits by chart and account.
     * 
     * @param chartCode
     * @param accountNumber
     * @return
     */
    Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber);

    /**
     * Find limits by chart and organization.
     * 
     * @param chartCode
     * @param organizationCode
     * @return
     */
    Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode);
    
    /**
     * @param limit
     * @return
     */
    Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit);
    
    /**
     * @param limit
     * @return
     */
    Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit);
}
