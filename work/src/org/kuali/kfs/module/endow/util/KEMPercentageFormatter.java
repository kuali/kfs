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
