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

import org.apache.log4j.Logger;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao;
import org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NegativePaymentRequestApprovalLimitServiceImpl implements NegativePaymentRequestApprovalLimitService {
    private static Logger LOG = Logger.getLogger(NegativePaymentRequestApprovalLimitServiceImpl.class);

    private NegativePaymentRequestApprovalLimitDao dao;

    public void setNegativePaymentRequestApprovalLimitDao(NegativePaymentRequestApprovalLimitDao dao) {
        this.dao = dao;
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChart(java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode) {
        LOG.debug("Entering findByChart(String)");
        LOG.debug("Leaving findByChart(String)");
        return dao.findByChart(chartCode);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChartAndAccount(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber) {
        LOG.debug("Entering findByChartAndAccount(String, String)");
        LOG.debug("Leaving findByChartAndAccount(String, String)");
        return dao.findByChartAndAccount(chartCode, accountNumber);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findByChartAndOrganization(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode) {
        LOG.debug("Entering findByChartAndOrganization(String, String)");
        LOG.debug("Leaving findByChartAndOrganization(String, String)");
        return dao.findByChartAndOrganization(chartCode, organizationCode);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findAboveLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit) {
        LOG.debug("Entering findAboveLimit(KualiDecimal)");
        LOG.debug("Leaving findAboveLimit(KualiDecimal)");
        return dao.findAboveLimit(limit);
    }

    /**
     * @see org.kuali.module.purap.service.NegativePaymentRequestApprovalLimitService#findBelowLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit) {
        LOG.debug("Entering findBelowLimit(KualiDecimal)");
        LOG.debug("Leaving findBelowLimit(KualiDecimal)");
        return dao.findBelowLimit(limit);
    }

}
