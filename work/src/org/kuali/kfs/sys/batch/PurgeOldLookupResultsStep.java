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
import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.lookup.LookupResultsService;

public class PurgeOldLookupResultsStep extends AbstractStep {
    private LookupResultsService lookupResultsService;

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeOldLookupResultsStep.class);

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        try {
            LOG.info("executing PurgeOldLookupResultsStep");

            String maxAgeInSecondsStr = getParameterService().getParameterValueAsString(KfsParameterConstants.NERVOUS_SYSTEM_LOOKUP.class, KFSConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_EXPIRATION_AGE);
            int maxAgeInSeconds = Integer.parseInt(maxAgeInSecondsStr);

            Calendar expirationCal = getDateTimeService().getCurrentCalendar();
            expirationCal.add(Calendar.SECOND, -maxAgeInSeconds);
            Timestamp expirationDate = new Timestamp(expirationCal.getTime().getTime());

            lookupResultsService.deleteOldLookupResults(expirationDate);
            lookupResultsService.deleteOldSelectedObjectIds(expirationDate);
            return true;
        }
        catch (Exception e) {
            LOG.error("error occured trying to purge old lookup results: ", e);
            return false;
        }
    }

    public LookupResultsService getLookupResultsService() {
        return lookupResultsService;
    }

    public void setLookupResultsService(LookupResultsService lookupResultsService) {
        this.lookupResultsService = lookupResultsService;
    }


}
