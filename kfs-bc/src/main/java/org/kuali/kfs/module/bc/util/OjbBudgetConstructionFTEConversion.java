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
