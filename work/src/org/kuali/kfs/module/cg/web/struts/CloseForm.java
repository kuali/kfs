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
package org.kuali.module.cg.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.cg.document.CloseDocument;
import org.kuali.module.cg.bo.Close;
import org.kuali.kfs.util.SpringServiceLocator;

import java.sql.Date;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Apr 19, 2007
 * Time: 12:01:34 PM
 */
public class CloseForm extends KualiTransactionalDocumentFormBase {
    private Date closeDate;
    private Date closeOnOrBeforeDate;

    public CloseForm() {
        super();
        setDocument(new CloseDocument());
    }

    public Close getMostRecentClose() {
        return SpringServiceLocator.getCloseService().getMostRecentClose();
    }

    public Close getLastClose() {
        return SpringServiceLocator.getCloseService().getMostRecentClose();
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getCloseOnOrBeforeDate() {
        return closeOnOrBeforeDate;
    }

    public void setCloseOnOrBeforeDate(Date closeOnOrBeforeDate) {
        this.closeOnOrBeforeDate = closeOnOrBeforeDate;
    }

}
