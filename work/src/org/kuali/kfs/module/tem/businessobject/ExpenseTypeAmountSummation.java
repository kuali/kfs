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
package org.kuali.kfs.module.tem.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Coded;

/**
 * The valid values that an ExpenseTypeObjectCode's maximum amount summation codes can be
 */
public enum ExpenseTypeAmountSummation implements Coded {
    PER_DAILY("D", "Per Day"),
    PER_OCCURRENCE("O", "Per Occurrence");

    private final String code;
    private final String name;

    ExpenseTypeAmountSummation(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * Retrieves the ExpenseTypeAmountSummation associated with the given code
     * @param code the code to find an ExpenseTypeAmountSummation value for
     * @return an ExpenseTypeAmountSummation value
     */
    public static ExpenseTypeAmountSummation fromCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (ExpenseTypeAmountSummation etas : values()) {
            if (etas.code.equals(code)) {
                return etas;
            }
        }
        throw new IllegalArgumentException("Failed to locate the ExpenseTypeAmountSummation with the given code: " + code);
    }
}
