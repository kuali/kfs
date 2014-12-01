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
package org.kuali.kfs.module.ar.report.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.report.service.OCRLineService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class...
 */
public class OCRLineServiceImpl implements OCRLineService {

    /**
     * @see org.kuali.kfs.module.ar.report.service.OCRLineService#generateOCRL(java.lang.String, java.lang.String)
     */
    public String generateOCRLine(KualiDecimal amountDue, String customerNumber, String docNumber) {
       StringBuilder builder = new StringBuilder(70);
       String amount = StringUtils.remove(amountDue.toString(), '.');
       builder.append(StringUtils.leftPad(amount, 12, '0'));
       builder.append(" ");
       if (docNumber != null) {
           builder.append(StringUtils.leftPad(docNumber, 11, '0'));
           builder.append(" ");
       }
       builder.append(StringUtils.leftPad(customerNumber, 9, '0'));
       builder.append(" ");
       builder.append(calculateCheckDigit(amount, customerNumber, docNumber));
       
        return builder.toString();
    }

    protected int calculateCheckDigit(String... args) {
        for(String str: args) {
            //do something with the Strings
        }
        return 0;
    }
}
