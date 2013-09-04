/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Coded;

/**
 * The valid values that an ExpenseTypeObjectCode's maximum amount summation codes can be
 */
public enum ExpenseTypeAmountSummation implements Coded {
    PER_DAILY("D", "Per Daily"),
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
