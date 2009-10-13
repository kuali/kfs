/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.bc.util;

import java.math.BigDecimal;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * Ensures that the database FTE value is rounded to 5 decimal places when reading. Any saved FTE value will automatically be forced
 * to 5 decimals during validation.
 */
public class OjbBudgetConstructionFTEConversion implements FieldConversion {

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object source) throws ConversionException {
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;
            return converted;
        }
        else {
            return null;
        }
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object source) throws ConversionException {
        if (source != null && source instanceof BigDecimal) {
            BigDecimal converted = (BigDecimal) source;

            return converted.setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        else {
            return null;
        }
    }
}
