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
package org.kuali.module.pdp.service.impl;

import java.sql.Date;
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.pdp.bo.GlPendingTransaction;
import org.kuali.module.pdp.service.ExtractGlTransactionService;
import org.kuali.module.pdp.service.GlPendingTransactionService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExtractGlTransactionServiceImpl implements ExtractGlTransactionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractGlTransactionServiceImpl.class);

    private GlPendingTransactionService glPendingTransactionService;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.module.pdp.service.ExtractGlTransactionService#extractGlTransactions()
     */
    public void extractGlTransactions() {
        LOG.debug("extractGlTransactions() started");

        Date processDate = dateTimeService.getCurrentSqlDate();
        OriginEntryGroup oeg = null;
        Iterator transactions = glPendingTransactionService.getUnextractedTransactions();
        while ( transactions.hasNext() ) {
            GlPendingTransaction tran = (GlPendingTransaction)transactions.next();

            // We only want to create the group if there are transactions in the group
            if ( oeg == null ) {
                oeg = originEntryGroupService.createGroup(processDate, OriginEntrySource.PDP,true,true,true);
            }

            originEntryService.createEntry(tran.getOriginEntry(), oeg);

            tran.setProcessInd("Y");
            glPendingTransactionService.save(tran);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setGlPendingTransactionService(GlPendingTransactionService glPendingTransactionService) {
        this.glPendingTransactionService = glPendingTransactionService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
}
