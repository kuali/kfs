/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public interface RefreshTransactionLineRule<E extends EndowmentTransactionLinesDocument, D extends EndowmentTransactionLine, I extends Number> extends BusinessRule {

    /**
     * This method...
     * 
     * @param EndowmentTransactionLinesDocument
     * @param EndowmentTransactionLine
     * @return
     */
    public boolean processRefreshTransactionLineRules(E endowmentTransactionLinesDocument, D endowmentTransactionLine, I index);

}
