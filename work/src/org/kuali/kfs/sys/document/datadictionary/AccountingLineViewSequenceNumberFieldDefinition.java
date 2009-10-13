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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewSequenceNumberField;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * Data dictionary definition of a field which represents a sequence number on an accounting line.
 */
public class AccountingLineViewSequenceNumberFieldDefinition extends AccountingLineViewFieldDefinition {

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition#createLayoutElement(java.lang.Class)
     */
    @Override
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return new AccountingLineViewSequenceNumberField();
    }
    
}
