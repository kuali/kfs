/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Rules for the Per Diem maintenance document.
 */
public class PerDiemRule extends MaintenanceDocumentRuleBase {
    static Pattern seasonBeginMonthDayPattern = Pattern.compile("^(\\d{1,2})/(\\d{1,2})$");

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final PerDiem perDiem = (PerDiem)document.getNewMaintainableObject().getBusinessObject();
        checkSeasonBeginMonthDayBeginParsability(perDiem);

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final PerDiem perDiem = (PerDiem)document.getNewMaintainableObject().getBusinessObject();
        result &= checkSeasonBeginMonthDayBeginParsability(perDiem);

        return result;
    }

    /**
     * Determines if the given season begin month/day can be parsed with a year as a date
     * @param perDiem the per diem record to check
     * @return true if it can be parsed as a date successfully, false otherwise
     */
    protected boolean checkSeasonBeginMonthDayBeginParsability(PerDiem perDiem) {
        boolean valid = true;
        if (!StringUtils.isBlank(perDiem.getSeasonBeginMonthAndDay())) { // let the required validation catch the case of a blank begin month/day
            Matcher seasonBeginMonthDayMatcher = seasonBeginMonthDayPattern.matcher(perDiem.getSeasonBeginMonthAndDay());
            if (seasonBeginMonthDayMatcher.matches()) {
                final Integer month = new Integer(seasonBeginMonthDayMatcher.group(1));
                final Integer day = new Integer(seasonBeginMonthDayMatcher.group(2));
                if (month.intValue() > 12 || month.intValue() < 1 || day.intValue() < 1 || day.intValue() > 31) {
                    valid = false;
                    putFieldError(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY, TemKeyConstants.ERROR_PER_DIEM_INVALID_DATE_SEASON_BEGIN_MONTH_DAY, new String[] { perDiem.getSeasonBeginMonthAndDay() });
                }
            } else {
                valid = false;
                putFieldError(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY, TemKeyConstants.ERROR_PER_DIEM_INVALID_DATE_SEASON_BEGIN_MONTH_DAY, new String[] { perDiem.getSeasonBeginMonthAndDay() });
            }
        }
        return valid;
    }
}
