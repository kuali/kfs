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
package org.kuali.kfs.module.cg.web.struts;

import java.util.Date;

import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.web.format.DateFormatter;

public class CloseForm extends FinancialSystemTransactionalDocumentFormBase {

    public CloseForm() {
        super();
        setFormatterType("document.userInitiatedCloseDate", DateFormatter.class);
        setFormatterType("document.closeOnOrBeforeDate", DateFormatter.class);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CLOS";
    }
    
    public ProposalAwardCloseDocument getMostRecentClose() {
        return SpringContext.getBean(CloseService.class).getMostRecentClose();
    }

    public ProposalAwardCloseDocument getCloseDocument() {
        return (ProposalAwardCloseDocument) getDocument();
    }

    public void setClose(ProposalAwardCloseDocument document) {
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
