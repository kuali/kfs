/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cg.document;

import org.kuali.core.document.TransactionalDocumentBase;

import java.sql.Date;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Apr 17, 2007
 * Time: 5:33:17 PM
 */
public class CloseDocument extends TransactionalDocumentBase {
    private Date userInitiatedCloseDate;
    private Date closeOnOrBeforeDate;

    public Date getUserInitiatedCloseDate() {
        return userInitiatedCloseDate;
    }

    public void setUserInitiatedCloseDate(Date userInitiatedCloseDate) {
        this.userInitiatedCloseDate = userInitiatedCloseDate;
    }

    public Date getCloseOnOrBeforeDate() {
        return closeOnOrBeforeDate;
    }

    public void setCloseOnOrBeforeDate(Date closeOnOrBeforeDate) {
        this.closeOnOrBeforeDate = closeOnOrBeforeDate;
    }
}
