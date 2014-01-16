/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.sql.Date;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kra.external.award.AwardWebService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ContractsAndGrantsModuleUpdateServiceImpl implements ContractsAndGrantsModuleUpdateService {

    private AwardWebService awardWebService;

    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        getAwardWebService().updateAwardBillingStatus(proposalNumber, lastBilledDate, null);
    }

    @Override
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        // TODO Auto-generated method stub

    }

    public AwardWebService getAwardWebService() {
        return awardWebService;
    }

    public void setAwardWebService(AwardWebService awardWebService) {
        this.awardWebService = awardWebService;
    }

    @Override
    public void setBillsisItBilled(Criteria criteria, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {
        // TODO Auto-generated method stub

    }

}
