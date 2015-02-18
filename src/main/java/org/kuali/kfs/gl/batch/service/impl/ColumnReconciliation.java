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
package org.kuali.kfs.gl.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Data for reconciliation of columns
 */
public class ColumnReconciliation {
    private String fieldName;
    private String[] tokenizedFieldNames;
    private KualiDecimal dollarAmount;

    public static final String COLUMN_NAME_DELIMITERS = "+";

    /**
     * Constructs a ColumnReconciliation.java.
     */
    public ColumnReconciliation() {
    }

    /**
     * Gets the dollarAmount attribute.
     * 
     * @return Returns the dollarAmount.
     */
    public KualiDecimal getDollarAmount() {
        return dollarAmount;
    }

    /**
     * Sets the dollarAmount attribute value.
     * 
     * @param dollarAmount The dollarAmount to set.
     */
    public void setDollarAmount(KualiDecimal dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    /**
     * Gets the fieldName attribute.
     * 
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the tokenizedFieldNames attribute
     * 
     * @return
     */
    public String[] getTokenizedFieldNames() {
        return tokenizedFieldNames;
    }

    /**
     * Sets the fieldName and tokenizedFieldNames attribute values.
     * 
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        this.tokenizedFieldNames = StringUtils.split(fieldName, COLUMN_NAME_DELIMITERS);
    }
}
