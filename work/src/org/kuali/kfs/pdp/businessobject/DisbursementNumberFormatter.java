/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2006-2014 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.web.format.CurrencyFormatter;

public class DisbursementNumberFormatter extends CurrencyFormatter {

    @Override
    protected Object convertToObject(String target) {
        if (StringUtils.isEmpty(target)) {
            return null;
        }
         return new KualiInteger(target);
    }

    /**
     * Returns a string representation of its argument formatted as a currency value.
     */
    @Override
    public Object format(Object obj) {
        return (obj == null ? null : obj.toString());
    }
}
