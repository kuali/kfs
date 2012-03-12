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
