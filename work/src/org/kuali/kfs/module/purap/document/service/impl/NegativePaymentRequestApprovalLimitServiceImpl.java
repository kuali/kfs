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
package org.kuali.module.purap.service.impl;

import java.util.Collection;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao;
import org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService;

@NonTransactional
public class NegativePaymentRequestApprovalLimitServiceImpl implements NegativePaymentRequestApprovalLimitService {
    //private static Logger LOG = Logger.getLogger(NegativePaymentRequestApprovalLimitServiceImpl.class);

    private NegativePaymentRequestApprovalLimitDao dao;

    public void setNegativePaymentRequestApprovalLimitDao(NegativePaymentRequestApprovalLimitDao dao) {
        this.dao = dao;
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChart(java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode) {
        return dao.findByChart(chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChartAndAccount(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber) {
        return dao.findByChartAndAccount(chartCode, accountNumber);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChartAndOrganization(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode) {
        return dao.findByChartAndOrganization(chartCode, organizationCode);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findAboveLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit) {
        return dao.findAboveLimit(limit);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findBelowLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit) {
        return dao.findBelowLimit(limit);
    }

}
