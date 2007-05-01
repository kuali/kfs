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
package org.kuali.kfs.batch;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.kuali.core.dao.PersistedLookupMetadataDao;
import org.kuali.core.lookup.LookupResultsService;
import org.kuali.kfs.KFSConstants;

public class PurgeOldLookupResultsStep extends AbstractStep {
    private LookupResultsService lookupResultsService;
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeOldLookupResultsStep.class);
    
    public boolean execute() {
        try {
            LOG.info("executing PurgeOldLookupResultsStep");
            
            String maxAgeInSecondsStr = getConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM,
                    KFSConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_EXPIRATION_AGE);
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
