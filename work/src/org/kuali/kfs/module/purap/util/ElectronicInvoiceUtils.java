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
package org.kuali.kfs.module.purap.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

public class ElectronicInvoiceUtils {
    
    private final static Logger LOG = Logger.getLogger(ElectronicInvoiceUtils.class);
    
    public static Date getDate(String invoiceDateString){
        
        boolean formatInvalid = true;
        String formattedDateString = "";
        String stringToParse = null;
        
        if (StringUtils.isNotEmpty(invoiceDateString)) {

            String dateToConvert = null;
            // get a copy of given date with 0's for all numbers to check format
            formattedDateString = invoiceDateString.replaceAll("\\d", "0");

            if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
                // Date is in 0000-00-00 format
                formatInvalid = false;
                stringToParse = invoiceDateString;
            }
            else if (PurapConstants.ElectronicInvoice.KUALI_DATE_FORMAT.equals(formattedDateString)) {
                //We need to minus the month by one since the date string has 1 month added for display purposes 
                try {
                    java.util.Date javaDate = SpringContext.getBean(DateTimeService.class).convertToDate(invoiceDateString);
                    javaDate = DateUtils.addMonths(javaDate, -1);
                    return org.kuali.rice.kns.util.DateUtils.convertToSqlDate(javaDate);
                }
                catch (ParseException e) {
                    return null;
                }
            }
            else if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length() != formattedDateString.length()) {
                // strings are not the same length... must parse down given string from cXML for validation
                formattedDateString = formattedDateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
                // strings should now be same length
                if (PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.equals(formattedDateString)) {
                    // if strings are equal we can process date
                    formatInvalid = false;
                    stringToParse = invoiceDateString.substring(0, PurapConstants.ElectronicInvoice.CXML_DATE_FORMAT.length());
                }
                else {
                    // strings are same size and both only use 0 characters so date is invalid
                }
            }
            else {
                /*
                 * strings are of same length but are not equal this can only occur if date separators are invalid so we have an
                 * invalid format
                 */
            }
        }
        
        if (formatInvalid) {
            return null;
        }
        else {
            // try to parse date
            SimpleDateFormat sdf = new SimpleDateFormat(PurapConstants.ElectronicInvoice.CXML_SIMPLE_DATE_FORMAT, Locale.US);
            try {
                return org.kuali.rice.kns.util.DateUtils.convertToSqlDate(sdf.parse(stringToParse));
            }
            catch (ParseException e) {
                return null;
            }
        }
        
    }
    
    public static String getDateDisplayText(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // we add one to the month below because January = 0, February = 1, March = 2, and so on
        String monthPart = (c.get(Calendar.MONTH) + 1) + "";
        String dayPart = c.get(Calendar.DATE) + "";
        if (monthPart.length() == 1){
            monthPart = "0" + monthPart;
        }
        
        if (dayPart.length() == 1){
            dayPart = "0" + dayPart;
        }
        
        String useDate =  monthPart + "/" + dayPart + "/" + c.get(Calendar.YEAR);
        String actualDate = (date != null) ? date.toString() : "empty given date";
        return useDate;
    }
    
    public static String stripSplChars(String data){
        if (data != null){
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < data.length(); i++) {
              if (Character.isLetterOrDigit(data.charAt(i))){
                  result.append(data.charAt(i));
              }
            }
            return result.toString();
        }else{
            return null;
        }
    }
    
    public static void main(String s[]){
        //TestCase 1 - cdw.xml 
        String invoiceDate = "2008-08-11T00:00:00-06:00";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate));
        
        //TestCase 2 - vwr.xml
        invoiceDate = "2008-07-29";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate));
        
        //TestCase 3 - guybrown.xml
        invoiceDate = "2008-07-29T12:00:00";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate));
        
        //TestCase 4 - barnesandnoble.xml
        invoiceDate = "2008-07-23T12:00:00-12:00";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate));
        
        //TestCase 5 - For reject doc date (in kuali format)
        invoiceDate = "07/23/2008";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate) + "  (KualiFormat check) ");
        
        //TestCase 6 - For invalid format 1
        invoiceDate = "2008|07|23";
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate) + "  (InvalidFormat check) ");
        
        //TestCase 7 - For invalid format 2
        invoiceDate = null;
        System.out.println("Actual Date= " + invoiceDate + ", Converted Date = " + getDate(invoiceDate) + "  (InvalidFormat check) ");
        
        //Invoice Id Check
        String rawInvoiceId = "A1!B2#C3$D4%";
        System.out.println("Processed InvId " + stripSplChars(rawInvoiceId));
        
        
        BigDecimal d1 = new BigDecimal("0");
        BigDecimal d2 = new BigDecimal("-50");
        
        System.out.println(d2.compareTo(d1) < 0);
        if (d2.compareTo(d1) < 0){
            System.out.println("D2 greater");
        }else{
            System.out.println("D1 greater");
        }
    }
}
