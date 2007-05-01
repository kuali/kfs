/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;

public class CashReceiptDepositTypeFormatter extends Formatter {
    private final String INTERIM_CD;
    private final String INTERIM_MSG;

    private final String FINAL_CD;
    private final String FINAL_MSG;

    public CashReceiptDepositTypeFormatter() {
        INTERIM_CD = KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM;
        INTERIM_MSG = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KFSKeyConstants.Deposit.DEPOSIT_TYPE_INTERIM);

        FINAL_CD = KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL;
        FINAL_MSG = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KFSKeyConstants.Deposit.DEPOSIT_TYPE_FINAL);
    }


    /**
     * Converts the given statusCode into a status message.
     */
    protected Object convertToObject(String target) {
        Object result = target;

        if (target instanceof String) {
            String message = (String) target;

            if (StringUtils.equals(message, INTERIM_MSG)) {
                result = INTERIM_CD;
            }
            else if (StringUtils.equals(message, FINAL_MSG)) {
                result = FINAL_CD;
            }
        }

        return result;
    }

    /**
     * Converts the given status message into a status code.
     */
    public Object format(Object value) {
        Object formatted = value;

        if (value instanceof String) {
            String statusCode = (String) value;

            if (StringUtils.equals(statusCode, FINAL_CD)) {
                formatted = FINAL_MSG;
            }
            else if (StringUtils.equals(statusCode, INTERIM_CD)) {
                formatted = INTERIM_MSG;
            }
        }

        return formatted;
    }

}
