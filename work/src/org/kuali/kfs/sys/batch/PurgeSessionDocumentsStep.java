/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.service.SessionDocumentService;

public class PurgeSessionDocumentsStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeSessionDocumentsStep.class);

    protected SessionDocumentService sessionDocumentService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        try {
            LOG.info("executing PurgeSessionDocumentsStep");
            String maxAgeInDaysStr = parameterService.getParameterValueAsString(PurgeSessionDocumentsStep.class, KFSConstants.SystemGroupParameterNames.NUMBER_OF_DAYS_SINCE_LAST_UPDATE);
            int maxAgeInDays = Integer.parseInt(maxAgeInDaysStr);

            Timestamp expirationDate = new Timestamp(DateUtils.addDays(getDateTimeService().getCurrentDate(), -maxAgeInDays).getTime());

            sessionDocumentService.purgeAllSessionDocuments(expirationDate);
            return true;
        }
        catch (Exception e) {
            LOG.error("error occured trying to purge session document from DB: ", e);
        }
        return false;
    }

    public void setSessionDocumentService(SessionDocumentService sessionDocumentService) {
        this.sessionDocumentService = sessionDocumentService;
    }

}
