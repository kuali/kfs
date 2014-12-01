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
