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
package org.kuali.kfs.sys.document;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.TransactionalDocument;

public interface FinancialSystemTransactionalDocument extends TransactionalDocument {
    /**
     * @see org.kuali.rice.krad.document.Document#getDocumentHeader()
     */
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader();

    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException;

    Boolean canEdit(Person user);
    void setCanEdit( Person user, Boolean canEdit );
}
