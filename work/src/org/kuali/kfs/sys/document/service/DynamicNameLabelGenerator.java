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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.sys.businessobject.AccountingLine;

/**
 * Methods needed by a service which creates non-standard dynamic name labels for accounting line views
 */
public interface DynamicNameLabelGenerator {
    /**
     * Builds the Javascript that should appear in the onblur attribute of the field associated with this dynamic name label
     * @param line the accounting line this label is associated with
     * @param accountingLineProperty the property of the accounting line
     * @return a Javascript call to be performed onblur from the field
     */
    public abstract String getDynamicNameLabelOnBlur(AccountingLine line, String accountingLineProperty);

    /**
     * Returns the value of the dynamic name label
     * @param line the accounting line this label is associated with
     * @param accountingLineProperty the property of the accounting line
     * @return the value, or an blank String ("" or null) if no value exists 
     */
    public abstract String getDynamicNameLabelValue(AccountingLine line, String accountingLineProperty);
    
    /**
     * Returns the field name of the dynamic name label
     * @param line the accounting line this label is associated with
     * @param accountingLineProperty the property of the accounting line
     * @return the field name
     */
    public abstract String getDynamicNameLabelFieldName(AccountingLine line, String accountingLineProperty);
}
