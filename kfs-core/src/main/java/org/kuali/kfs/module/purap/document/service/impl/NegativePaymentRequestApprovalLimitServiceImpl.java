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
