/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.kuali.kfs.sys.util.KfsDateUtils;

/**
 * Defines a utility class used by Contracts and Grants Invoice Reports. *
 */
public class ContractsGrantsReportUtils {

    /**
     * Checks if the date field is in range.
     *
     * @param fieldsForLookup
     * @param dateData
     * @param fieldName
     * @return true if date field is within range, false otherwise.
     */
    public static boolean isDateFieldInRange(String dateFromFieldValues, String dateToFieldValues, Date propertyValue, String fieldName) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        Date dateFrom;
        Date dateTo;

        // Clearing time field for date only comparison
        propertyValue = KfsDateUtils.clearTimeFields(propertyValue);

        try {
            // Both are blank or null
            if (dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                return true;
            }

            // Only set Date from
            if (dateToFieldValues.trim().equals("") && !dateFromFieldValues.trim().equals("")) {
                dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
                if (propertyValue.after(dateFrom) || propertyValue.equals(dateFrom)) {
                    return true;
                }
                else {
                    return false;
                }
            }

            // Only set Date to
            if (!dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                dateTo = new Date(format.parse(dateToFieldValues).getTime());
                if (propertyValue.before(dateTo) || propertyValue.equals(dateTo)) {
                    return true;
                }
                else {
                    return false;
                }
            }

            dateTo = new Date(format.parse(dateToFieldValues).getTime());
            dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
            if ((propertyValue.after(dateFrom) || propertyValue.equals(dateFrom)) && (propertyValue.before(dateTo) || propertyValue.equals(dateTo))) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

}
