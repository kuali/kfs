/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration.cg;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * No-op methods for the CG module's interaction with other KFS modules.
 */
public class ContractsAndGrantsModuleUpdateServiceNoOp implements ContractsAndGrantsModuleUpdateService {

    private Logger LOG = Logger.getLogger(getClass());


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAwardAccount(java.util.Map,
     *      java.lang.String, java.sql.Date)
     */
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLastBilledDateToAward(java.lang.Long,
     *      java.sql.Date)
     */
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setBillsisItBilled(org.apache.ojb.broker.query.Criteria)
     */
    public void setBillsisItBilled(Criteria criteria, String value) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setMilestonesisItBilled(java.lang.Long,
     *      java.util.List, java.lang.String)
     */
    public void setMilestonesisItBilled(Long proposalNumber, List<Long> milestoneIds, String value) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");

    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLOCCreationTypeToAward(java.lang.Long,
     *      java.lang.String)
     */
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setAmountToDrawToAwardAccount(java.util.Map,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setLOCReviewIndicatorToAwardAccount(java.util.Map,
     *      boolean)
     */
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledToAwardAccount(java.util.Map, boolean)
     */
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setTestValuesToAward(java.lang.Long, java.util.Map)
     */
    public void setAwardAccountsToAward(Long proposalNumber, List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }


    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setAwardAccountInvoiceDocumentStatus(java.util.Map,
     *      java.lang.String)
     */
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

}
