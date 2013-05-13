/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.sql.Date;
import java.util.Calendar;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.document.service.ValidateDateBasedOnFrequencyCodeService;

/**
 * This class...
 */
public class ValidateDateBasedOnFrequencyCodeServiceImpl implements ValidateDateBasedOnFrequencyCodeService {

    /**
     * @see org.kuali.kfs.module.endow.document.service.ValidateDateBasedOnFrequencyCodeService#validateDateBasedOnFrequencyCode(java.sql.Date,
     *      java.lang.String)
     */
    public boolean validateDateBasedOnFrequencyCode(Date date, String frequencyCode) {
        boolean isValid = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String frequencyType = frequencyCode.substring(0, 1);

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            // any date is valid
            return true;
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            String dayOfWeek = frequencyCode.substring(1, 4).toUpperCase();
            int calendarDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            return validateWeekly(dayOfWeek, calendarDayOfWeek);

        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY) || frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            String dayOfMonth = frequencyCode.substring(1, 3);
            int calendarDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            return validateDayOfMonth(dayOfMonth, calendarDayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY)) {
            String month = frequencyCode.substring(1, 2);
            int calendarMonth = calendar.get(Calendar.MONTH);
            String dayOfMonth = frequencyCode.substring(2, 4);
            int calendarDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            return validateQuarterly(month, calendarMonth, dayOfMonth, calendarDayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) {

            String month = frequencyCode.substring(1, 2);
            int calendarMonth = calendar.get(Calendar.MONTH);
            String dayOfMonth = frequencyCode.substring(2, 4);
            int calendarDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            return validateSemiAnnually(month, calendarMonth, dayOfMonth, calendarDayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            String month = frequencyCode.substring(1, 2);
            int calendarMonth = calendar.get(Calendar.MONTH);
            String dayOfMonth = frequencyCode.substring(2, 4);
            int calendarDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            return validateDayAndMonth(month, calendarMonth, dayOfMonth, calendarDayOfMonth);

        }

        return isValid;
    }

    /**
     * Validates that the day of the given date corresponds to the day part of the weekly frequency code.
     * 
     * @param dayOfWeek
     * @param calendarDayOfWeek
     * @return true if valid, false otherwise
     */
    private boolean validateWeekly(String dayOfWeek, int calendarDayOfWeek) {
        boolean isValid = true;

        if (EndowConstants.FrequencyWeekDays.MONDAY.equalsIgnoreCase(dayOfWeek)) {
            if (calendarDayOfWeek != Calendar.MONDAY) {
                return false;
            }
        }
        if (EndowConstants.FrequencyWeekDays.TUESDAY.equalsIgnoreCase(dayOfWeek)) {
            if (calendarDayOfWeek != Calendar.TUESDAY) {
                return false;
            }
        }
        if (EndowConstants.FrequencyWeekDays.WEDNESDAY.equalsIgnoreCase(dayOfWeek)) {
            if (calendarDayOfWeek != Calendar.WEDNESDAY) {
                return false;
            }
        }
        if (EndowConstants.FrequencyWeekDays.THURSDAY.equalsIgnoreCase(dayOfWeek)) {
            if (calendarDayOfWeek != Calendar.THURSDAY) {
                return false;
            }
        }
        if (EndowConstants.FrequencyWeekDays.FRIDAY.equalsIgnoreCase(dayOfWeek)) {
            if (calendarDayOfWeek != Calendar.FRIDAY) {
                return false;
            }
        }

        return isValid;
    }

    /**
     * Validates the the day of the month part of the given date matches the day of the month part of the frequency code.
     * 
     * @param dayOfMonth
     * @param calendarDayOfMonth
     * @return true if valid, false otherwise
     */
    private boolean validateDayOfMonth(String dayOfMonth, int calendarDayOfMonth) {
        boolean isValid = true;
        int frequencyCodeDayOfMonth = Integer.parseInt(dayOfMonth);

        if (frequencyCodeDayOfMonth != calendarDayOfMonth) {
            return false;
        }
        return isValid;
    }


    /**
     * Validates that the day and month of the given date match the day and month of the frequency code.
     * 
     * @param month
     * @param calendarMonth
     * @param dayOfMonth
     * @param calendarDayOfMonth
     * @return true if valid, false otherwise
     */
    private boolean validateDayAndMonth(String month, int calendarMonth, String dayOfMonth, int calendarDayOfMonth) {
        boolean isValid = true;

        if (EndowConstants.FrequencyMonthly.MONTH_END.equalsIgnoreCase(dayOfMonth)) {
            dayOfMonth = String.valueOf(checkMaximumDaysInMonth(calendarMonth));
        }

        if (!validateDayOfMonth(dayOfMonth, calendarDayOfMonth)) {
            return false;
        }
        else {

            if (EndowConstants.FrequencyMonths.JANUARY.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.JANUARY) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.FEBRUARY.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.FEBRUARY) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.MARCH.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.MARCH) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.APRIL.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.APRIL) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.MAY.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.MAY) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.JUNE.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.JUNE) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.JULY.equalsIgnoreCase(month)) {
                if (calendarDayOfMonth != Calendar.JULY) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.AUGUST.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.AUGUST) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.SEPTEMBER.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.SEPTEMBER) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.OCTOBER.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.OCTOBER) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.NOVEMBER.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.NOVEMBER) {
                    return false;
                }
            }
            if (EndowConstants.FrequencyMonths.DECEMBER.equalsIgnoreCase(month)) {
                if (calendarMonth != Calendar.DECEMBER) {
                    return false;
                }
            }
        }
        return isValid;
    }

    /**
     * Validates that the given date day and month coincide with valid values for the quarterly frequency code. For the Quarterly
     * frequency code the month part can be J=January, F=February and M=March. Any of this months plus 3, 6, 9 months will be a
     * valid value for the month.
     * 
     * @param month
     * @param calendarMonth
     * @param dayOfMonth
     * @param calendarDayOfMonth
     * @return true if valid, false otherwise
     */
    private boolean validateQuarterly(String month, int calendarMonth, String dayOfMonth, int calendarDayOfMonth) {
        boolean isValid = true;

        if (EndowConstants.FrequencyMonthly.MONTH_END.equalsIgnoreCase(dayOfMonth)) {
            dayOfMonth = String.valueOf(checkMaximumDaysInMonth(calendarMonth));
        }

        if (!validateDayOfMonth(dayOfMonth, calendarDayOfMonth)) {
            return false;
        }

        if (EndowConstants.FrequencyMonths.JANUARY.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.JANUARY || calendarMonth == Calendar.APRIL || calendarMonth == Calendar.JULY || calendarMonth == Calendar.OCTOBER)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.FEBRUARY.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.FEBRUARY || calendarMonth == Calendar.MAY || calendarMonth == Calendar.AUGUST || calendarMonth == Calendar.NOVEMBER)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.MARCH.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.MARCH || calendarMonth == Calendar.JUNE || calendarMonth == Calendar.SEPTEMBER || calendarMonth == Calendar.DECEMBER)) {
                return false;
            }
        }

        return isValid;
    }

    /**
     * Validates the given date day and month coincide with a valid value for the semi-annually frequency code. The month part of
     * the frequency code can be J=January, F=February, M=March, A=April, Y=May, U=June. Any of these month plus 6 months is a valid
     * value.
     * 
     * @param month
     * @param calendarMonth
     * @param dayOfMonth
     * @param calendarDayOfMonth
     * @return true if valid, false otherwise
     */
    private boolean validateSemiAnnually(String month, int calendarMonth, String dayOfMonth, int calendarDayOfMonth) {
        boolean isValid = true;

        if (EndowConstants.FrequencyMonthly.MONTH_END.equalsIgnoreCase(dayOfMonth)) {
            dayOfMonth = String.valueOf(checkMaximumDaysInMonth(calendarMonth));
        }

        if (!validateDayOfMonth(dayOfMonth, calendarDayOfMonth)) {
            return false;
        }

        if (EndowConstants.FrequencyMonths.JANUARY.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.JANUARY || calendarMonth == Calendar.JULY)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.FEBRUARY.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.FEBRUARY || calendarMonth == Calendar.AUGUST)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.MARCH.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.MARCH || calendarMonth == Calendar.SEPTEMBER)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.APRIL.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.APRIL || calendarMonth == Calendar.OCTOBER)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.MAY.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.MAY || calendarMonth == Calendar.NOVEMBER)) {
                return false;
            }
        }

        if (EndowConstants.FrequencyMonths.JUNE.equalsIgnoreCase(month)) {
            if (!(calendarMonth == Calendar.JUNE || calendarMonth == Calendar.DECEMBER)) {
                return false;
            }
        }

        return isValid;
    }

    /**
     * This method will check and return maximum days in a month.
     * 
     * @param monthNumber The number of the month to test for maximum days..
     * @return maxDays Maximum number of days in the month of February for calendar.
     */
    private int checkMaximumDaysInMonth(int monthNumber) {
        int maxDays;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);
        maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return maxDays;
    }
}
