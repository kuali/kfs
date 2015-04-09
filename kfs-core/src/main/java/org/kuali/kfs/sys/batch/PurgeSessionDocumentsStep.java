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
