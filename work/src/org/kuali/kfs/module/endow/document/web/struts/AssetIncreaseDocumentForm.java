/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;

public class AssetIncreaseDocumentForm extends FinancialSystemTransactionalDocumentFormBase {

    public AssetIncreaseDocumentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "EAI";
    }

    /**
     * This method gets the cash control document
     * 
     * @return the CashControlDocument
     */
    public AssetIncreaseDocument getCashControlDocument() {
        return (AssetIncreaseDocument) getDocument();
    }

}
