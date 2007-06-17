/*
 * Created on Apr 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

/**
 * Title:        PS Purchasing Web Application
 * Description:  Purchasing Web Application
 * Copyright:    Copyright (c) 2001
 * Company:      Indiana University
 * @author       Ailish Byrne
 * @version 1.0
 */

public class DateHandler {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DateHandler.class);

    private static final String DATEFORMAT = "MM/dd/yyyy";
    private static final String DATEFORMAT2 = "dd-MMM-yyyy hh:mm:ss";

    public DateHandler() {
    }

    public static String getTodayString() {
        return (new SimpleDateFormat(DateHandler.DATEFORMAT)).format(new java.util.Date());
    }

    public static java.sql.Timestamp getTodayTimestamp() {
        return new java.sql.Timestamp((new java.util.Date()).getTime());
    }

    public static java.sql.Date getTodaySqlDate() {
        return new java.sql.Date((new java.util.Date()).getTime());
    }

    public static String makeTimestampString(java.sql.Timestamp timestamp) {
        return (new SimpleDateFormat(DateHandler.DATEFORMAT)).format(new java.util.Date(timestamp.getTime()));
    }

    public static String makeSqlDateString(java.sql.Date date) {
        return (new SimpleDateFormat(DateHandler.DATEFORMAT)).format(new java.util.Date(date.getTime()));
    }

    public static String sqlDateToString(java.sql.Date date) {
        return (new SimpleDateFormat(DateHandler.DATEFORMAT2)).format(new java.util.Date(date.getTime()));
    }

    public static String makeDateString(java.util.Date date) {
        return (new SimpleDateFormat(DateHandler.DATEFORMAT)).format(date);
    }

    public static java.sql.Timestamp makeStringTimestamp(String string) throws Exception {
        java.sql.Timestamp timestamp = null;
        if((string != null) && !(string.trim().equals(""))){
            try {
                timestamp = new java.sql.Timestamp(((new SimpleDateFormat(DateHandler.DATEFORMAT)).parse(string)).getTime());
            }
            catch (ParseException pE) {
                throw new Exception(pE.getMessage());
            }
        }
        return timestamp;
    }

    public static java.sql.Date makeStringSqlDate(String string) throws Exception {
        java.sql.Date date = null;
        if((string != null) && !(string.trim().equals(""))){
            try {
                date = new java.sql.Date(((new SimpleDateFormat(DateHandler.DATEFORMAT)).parse(string)).getTime());
            }
            catch (ParseException pE) {
                throw new Exception(pE.getMessage());
            }
        }
        return date;
    }

    public static java.sql.Date makeStringFormat2ToSqlDate(String string){
        java.sql.Date date = null;
        if((string != null) && !(string.trim().equals(""))){
            try {
                long val = ((new SimpleDateFormat(DateHandler.DATEFORMAT2)).parse(string)).getTime();
                date = new java.sql.Date(val);
            }
            catch (ParseException pE) {
               return null;
            }
        }
        return date;
    }

    public static java.util.Date makeStringDate(String string) throws Exception {
        java.util.Date date;
        try {
            date = (new SimpleDateFormat(DateHandler.DATEFORMAT)).parse(string);
        }
        catch (ParseException pE) {
            throw new Exception(pE.getMessage());
        }
        return date;
    }

    public static java.sql.Date utilDate2sqlDate(java.util.Date uDate) {
      if ( uDate == null ) {
        return null;
      }
      return new java.sql.Date(uDate.getTime());
    }

    /**
     * Converts a String to an int.
     */
    public static int convertToNbr(String myString) throws Exception {
        // convert the String to an Integer
        Integer myInt = new Integer(0);
        int myNbr = 0;
        try {
//            myNbr = myInt.parseInt(myString);
          myNbr = Integer.parseInt(myString);
        }
        catch (NumberFormatException e) {
            throw new Exception("You must use integers: mm/dd/yyyy.");
        }
        return myNbr;
    }

    /**
     * Takes a variable name and a date String, and an ActionErrors object and returns the ActionErrors object,
     * containing errors detected during date validation.
     * This method should be called from the validate method of your forms.
     */
    public static ActionErrors validDate(ActionErrors errors, String variableName, String date) {
        date = date.trim();

        int firstSlash = date.indexOf("/");
        int secondSlash = date.indexOf("/", firstSlash + 1);

        // format validation
        // check for slashes
        if (firstSlash <= 0 && secondSlash <= 0) {
            errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Date: mm/dd/yyyy."));
        } else if (date.length() - secondSlash != 5) {
            // validate a 4 digit year and use of '/'
            errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Date: mm/dd/yyyy."));
        }
        // validate a 1 or 2 digit month
        if(firstSlash > 2 || firstSlash == 0) {
            errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Month: mm/dd/yyyy."));
        }
        // validate a 1 or 2 digit day
        if(secondSlash - firstSlash > 3) {
            errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Day: mm/dd/yyyy."));
        }

        // check the validity of the day, month, and year integers
        if (firstSlash > 0 && secondSlash > 0) {
            String month = date.substring(0, firstSlash);
            String day = date.substring(firstSlash + 1, secondSlash);
            String year = date.substring(secondSlash + 1);

            int monthNbr = 0;
            int dayNbr = 0;
            int yearNbr = 0;

            // Convert a String of Date into integer numbers for month, day, and year
            try {
                monthNbr = convertToNbr(month);
                dayNbr = convertToNbr(day);
                yearNbr = convertToNbr(year);
            }
            catch (Exception aE){
                errors.add(variableName, new ActionMessage("error.blank.br", "You have an invalid number in a date field"));
            }

            // check year for positive, non-zero digit
            if(yearNbr < 1) {
                errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Year:  mm/dd/yyyy."));
            }
            // check month for digit between 1 and 12
            if(monthNbr < 1 || monthNbr > 12) {
                errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Month 1-12: mm/dd/yyyy."));
            }

            // check for february
            if(monthNbr == 2) {
                // check for leap year
                if(yearNbr%400 == 0 || (yearNbr%100 != 0 && yearNbr%4 == 0)) {
                    // february with leap year
                    if(dayNbr < 1 || dayNbr > 29) {
                        errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Day 1-29: mm/dd/yyyy."));
                    }
                }
                else {
                    // february without leap year
                    if(dayNbr < 1 || dayNbr > 28) {
                        errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Day 1-28: mm/dd/yyyy."));
                    }
                }
            }
            // months with 30 days
            else if(monthNbr == 4 || monthNbr == 6 || monthNbr == 9 || monthNbr == 11) {
                if(dayNbr < 1 || dayNbr > 30) {
                    errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Day 1-30: mm/dd/yyyy."));
                }
            }
            // months with 31 days
            else {
                if(dayNbr < 1 || dayNbr > 31) {
                    errors.add(variableName, new ActionMessage("error.blank.br", "Invalid Day 1-31: mm/dd/yyyy."));
                }
            }
        }
        return errors;
    }

  public static String getDateAsString(String date){
    int firstSlash = date.indexOf("/");
    int secondSlash = date.indexOf("/", firstSlash + 1);
    int month = Integer.parseInt(date.substring(0, firstSlash));
    int day = Integer.parseInt(date.substring(firstSlash + 1, secondSlash));
    int year = Integer.parseInt(date.substring(secondSlash + 1));

    switch (month) {
      case 1: return "January " + day + ", " + year;
      case 2: return "February " + day + ", " + year;
      case 3: return "March " + day + ", " + year;
      case 4: return "April " + day + ", " + year;
      case 5: return "May " + day + ", " + year;
      case 6: return "June " + day + ", " + year;
      case 7: return "July " + day + ", " + year;
      case 8: return "August " + day + ", " + year;
      case 9: return "September " + day + ", " + year;
      case 10: return "October " + day + ", " + year;
      case 11: return "November " + day + ", " + year;
      case 12: return "December " + day + ", " + year;
      default: return "Date unknown";
    }
  }
}
