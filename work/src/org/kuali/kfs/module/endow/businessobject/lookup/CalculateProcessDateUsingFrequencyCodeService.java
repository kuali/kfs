/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.util.List;
import java.sql.Date;
import java.util.Calendar;
import org.kuali.rice.kns.service.DateTimeService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;

public class CalculateProcessDateUsingFrequencyCodeService {

    private DateTimeService dateTimeService;
    protected KEMService kemService;
    
    /**
     * This method uses frequency code to derive the next processing date
     * 
     * @param frquencyCode frequencyCode
     * @return returns the processing date
     */
    public Date calculateProcessDate(String frequencyCode) {
        
    //    Date processDate = dateTimeService.getCurrentSqlDate();
        Date processDate = kemService.getCurrentDate();
        
        String frequencyType = frequencyCode.substring(0, 1);
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            return processDate;
        }
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            String dayOfWeek =  frequencyCode.substring(1, 4).toUpperCase();
            return calculateNextWeekDate(dayOfWeek);
        }
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {
            String dayOfSemiMonthly =  frequencyCode.substring(1, 3);
            return calculateNextSemiMonthlyDate(dayOfSemiMonthly);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            String dayOfMonth =  frequencyCode.substring(1, 3);
            return calculateNextMonthlyDate(dayOfMonth);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY) || 
            frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY) ||
            frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            String month = frequencyCode.substring(1, 2);
            String dayOfMonth =  frequencyCode.substring(2, 4);
            return calculateNextProcessDate(month, dayOfMonth);
        }
        
        return processDate;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public KEMService getKemService() {
        return kemService;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Method to calculate the next processing week date based on the frequency type
     * adds the appropriate number of days to the current date
     * @param dayOfWeek
     * @return next processing date
     */
    private Date calculateNextWeekDate(String dayOfWeekFromFrequencyCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(kemService.getCurrentDate());
        
        int daysToAdd = 0;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);  // today's day of the week
        int maximumDaysInWeek = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
                
        if (dayOfWeekFromFrequencyCode.equalsIgnoreCase(EndowConstants.FrequencyWeekDays.MONDAY)) {
            if (dayOfWeek < Calendar.MONDAY)
                daysToAdd = Calendar.MONDAY - dayOfWeek;
            else
                daysToAdd = maximumDaysInWeek - dayOfWeek + Calendar.MONDAY;
        } else if (dayOfWeekFromFrequencyCode.equalsIgnoreCase(EndowConstants.FrequencyWeekDays.TUESDAY)) {
                   if (dayOfWeek < Calendar.TUESDAY)
                       daysToAdd = Calendar.TUESDAY - dayOfWeek;
                   else
                       daysToAdd = maximumDaysInWeek - dayOfWeek + Calendar.TUESDAY;
              } else if (dayOfWeekFromFrequencyCode.equalsIgnoreCase(EndowConstants.FrequencyWeekDays.WEDNESDAY)) {
                         if (dayOfWeek < Calendar.WEDNESDAY)
                             daysToAdd = Calendar.WEDNESDAY - dayOfWeek;
                         else
                             daysToAdd = maximumDaysInWeek - dayOfWeek + Calendar.WEDNESDAY;
                } else if (dayOfWeekFromFrequencyCode.equalsIgnoreCase(EndowConstants.FrequencyWeekDays.THURSDAY)) {
                           if (dayOfWeek < Calendar.THURSDAY)
                               daysToAdd = Calendar.THURSDAY - dayOfWeek;
                           else   
                               daysToAdd = maximumDaysInWeek - dayOfWeek + Calendar.THURSDAY;
                      } else if (dayOfWeekFromFrequencyCode.equalsIgnoreCase(EndowConstants.FrequencyWeekDays.FRIDAY)) {
                                 if (dayOfWeek < Calendar.FRIDAY)
                                     daysToAdd = Calendar.FRIDAY - dayOfWeek;
                                 else   
                                     daysToAdd = maximumDaysInWeek - dayOfWeek + Calendar.FRIDAY;
                        }

        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the next processing semi-monthly date based on the frequency type
     * Sets the day of the month and then returns the processing date
     * @param dayOfSemiMonthly
     * @return next processing date
     */
    private Date calculateNextSemiMonthlyDate(String dayOfSemiMonthly) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(kemService.getCurrentDate());
        
        int dayOfMonthToSet = Integer.parseInt(dayOfSemiMonthly);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }
   
    /**
     * Method to calculate the next processing monthly date based on the frequency type
     * Sets the day in the calendar based on day part of the frequency code.
     * @param dayOfMonth
     * @return next processing date
     */
    private Date calculateNextMonthlyDate(String dayOfMonth) {
        int dayInMonthToSet;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(kemService.getCurrentDate());
        setCalendarWithDays(calendar, dayOfMonth);
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the next processing quarterly or semi-annually or annually date based on the frequency type
     * Sets the day in the calendar based on day part of the frequency code.
     * @param frequencyType frequency code for quarterly, month, dayOfMonth
     * @return next processing date
     */
    private Date calculateNextProcessDate(String month, String dayOfMonth) {
        Calendar calendar = setCaledarWithMonth(month);
        calendar.setTime(kemService.getCurrentDate());
        setCalendarWithDays(calendar, dayOfMonth);
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }
    
    /**
     * This method will check the current month and set the calendar to that month
     * @param month month to set the calendar
     * @return calendar calendar is set to the month selected
     */
    private Calendar setCaledarWithMonth(String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(kemService.getCurrentDate());
        int calendarMonth = 1;
        
        if (EndowConstants.FrequencyMonths.JANUARY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.JANUARY;
        } else if (EndowConstants.FrequencyMonths.FEBRUARY.equalsIgnoreCase(month)) {
            calendarMonth = Calendar.FEBRUARY;
          } else if (EndowConstants.FrequencyMonths.MARCH.equalsIgnoreCase(month)) {
              calendarMonth = Calendar.MARCH;
            } else if (EndowConstants.FrequencyMonths.APRIL.equalsIgnoreCase(month)) {
                calendarMonth = Calendar.APRIL;
              } else if (EndowConstants.FrequencyMonths.MAY.equalsIgnoreCase(month)) {
                  calendarMonth = Calendar.MAY;
                } else if (EndowConstants.FrequencyMonths.JUNE.equalsIgnoreCase(month)) {
                    calendarMonth = Calendar.JUNE;
                  } else if (EndowConstants.FrequencyMonths.JULY.equalsIgnoreCase(month)) {
                      calendarMonth = Calendar.JULY;
                    } else if (EndowConstants.FrequencyMonths.AUGUST.equalsIgnoreCase(month)) {
                        calendarMonth = Calendar.AUGUST;
                      } else if (EndowConstants.FrequencyMonths.SEPTEMBER.equalsIgnoreCase(month)) {
                          calendarMonth = Calendar.SEPTEMBER;
                        } else if (EndowConstants.FrequencyMonths.OCTOBER.equalsIgnoreCase(month)) {
                            calendarMonth = Calendar.OCTOBER;
                          } else if (EndowConstants.FrequencyMonths.NOVEMBER.equalsIgnoreCase(month)) {
                              calendarMonth = Calendar.NOVEMBER;
                            } else if (EndowConstants.FrequencyMonths.DECEMBER.equalsIgnoreCase(month)) {
                                calendarMonth = Calendar.DECEMBER;
                              }
        
        calendar.set(Calendar.MONTH, calendarMonth);
        
        return calendar;
    }

    /**
     * This method will check the current month and set the calendar to that month
     * @param month, dayOfMonth month to set the calendar, dayOfMonth day of the month to set to
     * @return calendar calendar is set to the month selected
     */
    private void setCalendarWithDays(Calendar calendar, String dayOfMonth) {
        int dayInMonthToSet;
        int calendarMonth = calendar.get(Calendar.MONTH);
        
        if (StringUtils.equalsIgnoreCase(dayOfMonth, EndowConstants.FrequencyMonthly.MONTH_END)) { // month end for the month so need to get max days...
            dayInMonthToSet = checkMaximumDaysInMonth(calendar.get(Calendar.MONTH));
        } else {
            dayInMonthToSet = Integer.parseInt(dayOfMonth);
            
            if (dayInMonthToSet > 29 && calendarMonth == Calendar.FEBRUARY) {
                dayInMonthToSet = checkMaximumDaysInFebruary();
            } else if (dayInMonthToSet > 30 && (calendarMonth == Calendar.APRIL || calendarMonth == Calendar.JUNE ||
                       calendarMonth == Calendar.SEPTEMBER || calendarMonth == Calendar.NOVEMBER)) {
                       dayInMonthToSet = 30;
                       dayInMonthToSet = checkMaximumDaysInMonth(calendarMonth);                       
              }
          }
            
        calendar.set(Calendar.DAY_OF_MONTH, dayInMonthToSet);
    }
    
    /**
     * This method will check and return either maximum days in the month as 28 or 29 for leap year.
     * It first sets the month to February and then checks the maximum days..
     * @return maxDays Maximum number of days in the month of February for calendar.
     */
    private int checkMaximumDaysInFebruary() {
        int maxDays;      
        Calendar februaryMonthlyDateCalendar = Calendar.getInstance();
        februaryMonthlyDateCalendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        maxDays = februaryMonthlyDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        return maxDays;
    }
    
    /**
     * This method will check and return maximum days in a month.
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
