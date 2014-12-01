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
