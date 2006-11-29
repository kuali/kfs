/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/businessobject/inquiry/InquirableFinancialDocument.java,v $
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
package org.kuali.module.gl.web.inquirable;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Transaction;

/**
 * This class provides a placeholder that can connect General Ledger business object with financial document in the presentation
 * tier. The typical method is to generate url for the inquirable financial document.
 * 
 * 
 */
public class InquirableFinancialDocument {

    private KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();

    /**
     * get the url of inquirable financial document for the given transaction
     * 
     * @param transaction the business object that implements Transaction interface
     * @return the url of inquirable financial document for the given transaction if the document is inquirable; otherwise, return
     *         empty string
     */
    public String getInquirableDocumentUrl(Transaction transaction) {
        if (transaction == null) {
            return Constants.EMPTY_STRING;
        }

        String docNumber = transaction.getDocumentNumber();
        String originationCode = transaction.getFinancialSystemOriginationCode();

        return getUrl(originationCode, docNumber);
    }

    private String getUrl(String originCode, String docNumber) {
        if (Constants.ORIGIN_CODE_KUALI.equals(originCode) && !StringUtils.isBlank(docNumber)) {
            return kualiConfigurationService.getPropertyString("workflow.base.url") + "/DocHandler.do?docId=" + docNumber + "&command=displayDocSearchView";
        }
        return Constants.EMPTY_STRING;
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
            return Constants.EMPTY_STRING;
        }

        String docNumber = encumbrance.getDocumentNumber();
        String originationCode = encumbrance.getOriginCode();

        return getUrl(originationCode, docNumber);
    }
}