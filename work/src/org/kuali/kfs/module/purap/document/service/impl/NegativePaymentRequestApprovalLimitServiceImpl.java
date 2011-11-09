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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.Collection;

import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao;
import org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@NonTransactional
public class NegativePaymentRequestApprovalLimitServiceImpl implements NegativePaymentRequestApprovalLimitService {

    private NegativePaymentRequestApprovalLimitDao dao;

    public void setNegativePaymentRequestApprovalLimitDao(NegativePaymentRequestApprovalLimitDao dao) {
        this.dao = dao;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService#findByChart(java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode) {
        return dao.findByChart(chartCode);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService#findByChartAndAccount(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber) {
        return dao.findByChartAndAccount(chartCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService#findByChartAndOrganization(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode) {
        return dao.findByChartAndOrganization(chartCode, organizationCode);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService#findAboveLimit(org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit) {
        return dao.findAboveLimit(limit);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.NegativePaymentRequestApprovalLimitService#findBelowLimit(org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit) {
        return dao.findBelowLimit(limit);
    }

}
