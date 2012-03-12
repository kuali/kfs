/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject.format;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.Formatter;

public class CashDrawerStatusCodeFormatter extends Formatter {
    private final String CLOSED_CD;
    private final String CLOSED_MSG;

    private final String OPEN_CD;
    private final String OPEN_MSG;

    private final String LOCKED_CD;
    private final String LOCKED_MSG;

    public CashDrawerStatusCodeFormatter() {
        CLOSED_CD = KFSConstants.CashDrawerConstants.STATUS_CLOSED;
        CLOSED_MSG = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.CashDrawer.CASH_DRAWER_STATUS_CLOSED);

        OPEN_CD = KFSConstants.CashDrawerConstants.STATUS_OPEN;
        OPEN_MSG = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.CashDrawer.CASH_DRAWER_STATUS_OPEN);

        LOCKED_CD = KFSConstants.CashDrawerConstants.STATUS_LOCKED;
        LOCKED_MSG = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.CashDrawer.CASH_DRAWER_STATUS_LOCKED);
    }


    /**
     * Converts the given statusCode into a status message.
     */
    protected Object convertToObject(String target) {
        Object result = target;

        if (target instanceof String) {
            String message = (String) target;

            if (StringUtils.equals(message, OPEN_MSG)) {
                result = OPEN_CD;
            }
            else if (StringUtils.equals(message, CLOSED_MSG)) {
                result = CLOSED_CD;
            }
            else if (StringUtils.equals(message, LOCKED_MSG)) {
                result = LOCKED_CD;
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

            if (StringUtils.equals(statusCode, CLOSED_CD)) {
                formatted = CLOSED_MSG;
            }
            else if (StringUtils.equals(statusCode, OPEN_CD)) {
                formatted = OPEN_MSG;
            }
            else if (StringUtils.equals(statusCode, LOCKED_CD)) {
                formatted = LOCKED_MSG;
            }
        }

        return formatted;
    }

}
