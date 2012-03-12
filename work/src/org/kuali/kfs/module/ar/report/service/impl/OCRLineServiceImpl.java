/*
 * Copyright 2008 The Kuali Foundation
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
