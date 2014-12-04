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
package org.kuali.kfs.module.endow.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;

/**
 * in DB, the data of the percent is stored in the format x.xxxx
 * which is always not bigger than 1.0000 and not less than 0.0000
 * For the display, we'd like to have xxx.xx%
 * 
 */
public class KEMPercentageFormatter extends Formatter {
 // begin Kuali Foundation modification
 // ToDo: Borrow the code from PercentageFormatter, shall I change
 // the value of serialVersionUID?
 private static final long serialVersionUID = 1323889942436009589L;
 // end Kuali Foundation modification

 /**
  * The default scale for percentage values
  */
 public final static int PERCENTAGE_SCALE = 2;

 /**
  * The default format for percentage values
  */
 public final static String PERCENTAGE_FORMAT = "0.####%";

 // begin Kuali Foundation modification
 // removed PARSE_MSG
 // end Kuali Foundation modification
 
 /**
  * Unformats its argument and returns a BigDecimal instance initialized with the resulting string value
  * 
  * @return a BigDecimal initialized with the provided string
  */
 protected Object convertToObject(String target) {
      try {
         DecimalFormat formatter = new DecimalFormat(PERCENTAGE_FORMAT);
         
         Number parsedNumber = formatter.parse(target);
         System.out.println(">>parsedNumber ="+parsedNumber);
         return new KualiPercent(parsedNumber.doubleValue());

     }
     catch (NumberFormatException e) {
         throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, target, e);
     }
     catch (ParseException e) {
         throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, target, e);
     }

 }

 /**
  * Returns a string representation of its argument, formatted as a percentage value.
  * 
  * @return a formatted String
  */
 public Object format(Object value) {
     if (value == null)
         return "N/A";

     if (value instanceof KualiDecimal) {
          value = ((KualiDecimal)value).bigDecimalValue();
          DecimalFormat df = new DecimalFormat ( PERCENTAGE_FORMAT ) ;
          String theFormattedValue = df.format(((BigDecimal) value).doubleValue());
          System.out.println(">>theFormattedValue="+theFormattedValue);
          return theFormattedValue;
     }        
     else
          throw new RuntimeException("Error: the date type is not KualiDecimal");     
     
         
 }
}
