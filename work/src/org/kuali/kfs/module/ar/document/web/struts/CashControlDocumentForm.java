/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;

public class CashControlDocumentForm extends KualiTransactionalDocumentFormBase {
    
    private CashControlDetail newCashControlDetail;
    private String processingChartOfAccCodeAndOrgCode;

    /**
     * Constructs a CashControlDocumentForm.java.
     */
    public CashControlDocumentForm() {
        super();
        setDocument(new CashControlDocument());
        setNewCashControlDetail(new CashControlDetail());
        
    }

    /**
     * This method...
     * @return
     */
    public CashControlDocument getCashControlDocument() {
        return (CashControlDocument) getDocument();
    }

    /**
     * This method gets the new cash control detail
     * @return cashControlDetail
     */
    public CashControlDetail getNewCashControlDetail() {
        return newCashControlDetail;
    }

    /**
     * This method sets the new cash control detail
     * @param newCashControlDetail
     */
    public void setNewCashControlDetail(CashControlDetail newCashControlDetail) {
        this.newCashControlDetail = newCashControlDetail;
    }

    public String getProcessingChartOfAccCodeAndOrgCode() {
        return this.getCashControlDocument().getAccountsReceivableDocumentHeader().getProcessingChartOfAccCodeAndOrgCode();
    }

    public void setProcessingChartOfAccCodeAndOrgCode(String processingChartOfAccCodeAndOrgCode) {
        this.processingChartOfAccCodeAndOrgCode = processingChartOfAccCodeAndOrgCode;
    }
  
    
}
