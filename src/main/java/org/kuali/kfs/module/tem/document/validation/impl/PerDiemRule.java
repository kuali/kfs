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
