/*
 * Copyright 2007 The Kuali Foundation
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
