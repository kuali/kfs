/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.dataaccess.impl.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;

public class PdpDataaccessUtil {
    
    /**
     * This method creates additional criteria for the lookup when fileCreationTime is a single date (does not have any wildcards);
     * The file creation timestamp is a Timestamp in the BO but comes as a date from the GUI - we don't want to have the user enter
     * the time too.
     * 
     * @param fileCreationTimeValue
     * @return null if no criteria needs to be created, the additional criteria otherwise
     */
    public static Criteria createAdditionalRangeCriteriaForTimestampField(String fileCreationTimeValue) {
        Criteria additionalCriteria = null;
        try {
            if (StringUtils.isNotEmpty(fileCreationTimeValue)) {
                String wildCards = "";
                for (int i = 0; i < KFSConstants.QUERY_CHARACTERS.length; i++) {
                    wildCards += KFSConstants.QUERY_CHARACTERS[i];
                }
                if (StringUtils.containsNone(fileCreationTimeValue, wildCards)) {
                    additionalCriteria = new Criteria();
                    Date startDate = SpringContext.getBean(DateTimeService.class).convertToSqlDate(fileCreationTimeValue);
                    Timestamp startTime = new Timestamp(startDate.getTime());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    calendar.add(Calendar.HOUR, 24);
                    Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

                    additionalCriteria.addLessThan(PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME, endTime);
                    additionalCriteria.addGreaterOrEqualThan(PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME, startTime);
                }
            }
        }
        catch (Exception e) {
            GlobalVariables.getErrorMap().putError(PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME, KFSKeyConstants.ERROR_DATE_TIME, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME));
            throw new ValidationException("errors in search criteria");
        }

        return additionalCriteria;
    }


}

