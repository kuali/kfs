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
import org.kuali.core.web.format.DateFormatter;
import org.kuali.module.cg.document.CloseDocument;
import org.kuali.module.cg.bo.Close;
import org.kuali.kfs.util.SpringServiceLocator;

import java.util.Date;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Apr 19, 2007
 * Time: 12:01:34 PM
 */
public class CloseForm extends KualiTransactionalDocumentFormBase {

    public CloseForm() {
        super();
        setDocument(new Close());
        setFormatterType("document.userInitiatedCloseDate", DateFormatter.class);
        setFormatterType("document.closeOnOrBeforeDate", DateFormatter.class);
    }

    public Close getMostRecentClose() {
        return SpringServiceLocator.getCloseService().getMostRecentClose();
    }

    public Close getCloseDocument() {
        return (Close) getDocument();
    }

    public void setClose(Close document) {
        setDocument(document);
    }

    public Date getUserInitiatedCloseDate() {
        return getCloseDocument().getUserInitiatedCloseDate();
    }

    public void setUserInitiatedCloseDate(Date date) {
        getCloseDocument().setUserInitiatedCloseDate(new java.sql.Date(date.getTime()));
    }

    public Date getCloseOnOrBeforeDate() {
        return getCloseDocument().getCloseOnOrBeforeDate();
    }

    public void setCloseOnOrBeforeDate(Date date) {
        getCloseDocument().setCloseOnOrBeforeDate(new java.sql.Date(date.getTime()));
    }

}
