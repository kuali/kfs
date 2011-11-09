/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.inquiry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * This class provides a placeholder that can connect General Ledger business object with financial document in the presentation
 * tier. The typical method is to generate url for the inquirable financial document.
 */
public class InquirableFinancialDocument {

    private ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);

    /**
     * get the url of inquirable financial document for the given transaction
     * 
     * @param transaction the business object that implements Transaction interface
     * @return the url of inquirable financial document for the given transaction if the document is inquirable; otherwise, return
     *         empty string
     */
    public String getInquirableDocumentUrl(Transaction transaction) {
        if (transaction == null) {
            return KFSConstants.EMPTY_STRING;
        }

        String docNumber = transaction.getDocumentNumber();
        String originationCode = transaction.getFinancialSystemOriginationCode();

        return getUrl(originationCode, docNumber);
    }

    /**
     * Creates the url for a document drill down
     * 
     * @param originCode the originatino code of the document
     * @param docNumber the document number of the document to drill down on
     * @return the URL for the drill down
     */
    private String getUrl(String originCode, String docNumber) {
        if (KFSConstants.ORIGIN_CODE_KUALI.equals(originCode) && !StringUtils.isBlank(docNumber)) {
            return kualiConfigurationService.getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + docNumber + "&command=displayDocSearchView";
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * get the url of inquirable financial document for the given encumbrance
     * 
     * @param encumbrance the encumrbance record
     * @return the url of inquirable financial document for the given encumbrance if the document is inquirable; otherwise, return
     *         empty string
     */
    public String getInquirableDocumentUrl(Encumbrance encumbrance) {
        if (encumbrance == null) {
            return KFSConstants.EMPTY_STRING;
        }

        String docNumber = encumbrance.getDocumentNumber();
        String originationCode = encumbrance.getOriginCode();

        return getUrl(originationCode, docNumber);
    }
}
