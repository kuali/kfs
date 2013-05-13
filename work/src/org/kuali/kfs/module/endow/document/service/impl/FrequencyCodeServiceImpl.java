/*
 * Copyright 2009 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.FrequencyCode;
import org.kuali.kfs.module.endow.document.service.FrequencyCodeService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class FrequencyCodeServiceImpl implements FrequencyCodeService {
    
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.FrequencyCodeService#getByPrimaryKey(java.lang.String)
     */
    public FrequencyCode getByPrimaryKey(String code) {
        FrequencyCode frequencyCode = null;
        if (StringUtils.isNotBlank(code)) {
            Map criteria = new HashMap();
            criteria.put("code", code);

            frequencyCode = (FrequencyCode) businessObjectService.findByPrimaryKey(FrequencyCode.class, criteria);
        }
        
        return frequencyCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.FrequencyCodeService#calculateProcessDate(String)
     */
    public Date calculateProcessDate(String frequencyCode) {
        Date currentDate = kemService.getCurrentDate();
        
        String frequencyType = frequencyCode.substring(0, 1);
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.DAILY)) {
            return currentDate;
        }
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.WEEKLY)) {
            String dayOfWeek =  frequencyCode.substring(1, 4).toUpperCase();
            return calculateNextWeeklyDate(dayOfWeek, currentDate);
        }
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_MONTHLY)) {
            String dayOfSemiMonthly =  frequencyCode.substring(1, 3);
            return calculateNextSemiMonthlyDate(dayOfSemiMonthly, currentDate);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.MONTHLY)) {
            String dayOfMonth =  frequencyCode.substring(1, 3);
            return calculateNextMonthlyDate(dayOfMonth, currentDate);
        }

        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY) || 
            frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY) ||
            frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) {
            String month = frequencyCode.substring(1, 2);
            String dayOfMonth =  frequencyCode.substring(2, 4);
            return calculateNextQuarterlyOrSemiAnnuallyOrAnnuallyProcessDate(month, dayOfMonth, frequencyType, currentDate);
        }
        
        return currentDate;
    }
    
    /**
     * Method to calculate the next processing week date based on the frequency type
     * adds the appropriate number of days to the current date
     * @param dayOfWeek
     * @return next processing date
     */
    protected Date calculateNextWeeklyDate(String dayOfWeekFromFrequencyCode, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        
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
    protected Date calculateNextSemiMonthlyDate(String dayOfSemiMonthly, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        
        int dayOfMonthToSet = Integer.parseInt(dayOfSemiMonthly);
        int dayOfMonthNextToSet = dayOfMonthToSet + 15;
        
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);
        
        if (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
            calendar.add(Calendar.DAY_OF_MONTH, dayOfMonthNextToSet);
        }
        if (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthToSet);
            calendar.add(Calendar.MONTH, 1);
        }
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }
    
    /**
     * Method to calculate the next processing monthly date based on the frequency type
     * Sets the day in the calendar based on day part of the frequency code.
     * @param dayOfMonth
     * @return next processing date
     */
    protected Date calculateNextMonthlyDate(String dayOfMonth, Date currentDate) {
        int dayInMonthToSet;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        setCalendarWithDays(calendar, dayOfMonth);
        while (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
            calendar.add(Calendar.MONTH, 1);  
        }
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Method to calculate the next processing quarterly or semi-annually or annually date based on the frequency type
     * Sets the day in the calendar based on day part of the frequency code.
     * @param frequencyType frequency code for quarterly, month, dayOfMonth
     * @return next processing date
     */
    protected Date calculateNextQuarterlyOrSemiAnnuallyOrAnnuallyProcessDate(String month, String dayOfMonth, String frequencyType, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar = setCaledarWithMonth(month, currentDate);        
        setCalendarWithDays(calendar, dayOfMonth);
        
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.QUARTERLY)) {
            while (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
                calendar.add(Calendar.MONTH, 3);  
                setCalendarWithDays(calendar, dayOfMonth);
            }
        }
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.SEMI_ANNUALLY)) { 
            while (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
                calendar.add(Calendar.MONTH, 6);  
                setCalendarWithDays(calendar, dayOfMonth);
            }
        }
        if (frequencyType.equalsIgnoreCase(EndowConstants.FrequencyTypes.ANNUALLY)) { 
            while (new java.sql.Date(calendar.getTimeInMillis()).before(currentDate)) {
                calendar.add(Calendar.MONTH, 12); 
                setCalendarWithDays(calendar, dayOfMonth);
            }
        }
        
        return new java.sql.Date(calendar.getTimeInMillis());
    }
    
    /**
     * This method will check the current month and set the calendar to that month
     * @param month month to set the calendar
     * @return calendar calendar is set to the month selected
     */
    protected Calendar setCaledarWithMonth(String month, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
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
    protected void setCalendarWithDays(Calendar calendar, String dayOfMonth) {
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
    protected int checkMaximumDaysInFebruary() {
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
    protected int checkMaximumDaysInMonth(int monthNumber) {
        int maxDays;   
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNumber);
        maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        return maxDays;
    }
    
    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }
    
    protected KEMService getKemService() {
        return kemService;
    }
    
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
    
}
