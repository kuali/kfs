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
/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PaymentDetailSearch;
import org.kuali.module.pdp.dao.PaymentDetailSearchDao;
import org.kuali.module.pdp.service.PaymentDetailSearchService;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 */

@NonTransactional
public class PaymentDetailSearchServiceImpl implements PaymentDetailSearchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailSearchServiceImpl.class);

    private ParameterService parameterService;
    private PaymentDetailSearchDao paymentDetailSearchDao;

    public void setPaymentDetailSearchDao(PaymentDetailSearchDao p) {
        paymentDetailSearchDao = p;
    }

    public List getAllPaymentsForSearchCriteria(PaymentDetailSearch pds) {
        LOG.debug("getAllPaymentsForSearchCriteria() started");

        List finalResults = new ArrayList();
        List nonCancels = paymentDetailSearchDao.getAllPaymentsForSearchCriteria(pds, getMaxSearchTotal());
        finalResults.addAll(nonCancels);

        if (pds.getDisbursementNbr() != null) {
            finalResults.addAll(paymentDetailSearchDao.getAllPaymentsWithCancelReissueDisbNbr(pds));
        }

        Collections.sort(finalResults, new Comparator() {
            public int compare(Object o1, Object o2) {
                return (((org.kuali.module.pdp.bo.PaymentDetail) o1).getPaymentGroup().getPayeeName()).compareTo(((org.kuali.module.pdp.bo.PaymentDetail) o2).getPaymentGroup().getPayeeName());
            }
        });

        return finalResults;
    }

    private int getMaxSearchTotal() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_TOTAL);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
