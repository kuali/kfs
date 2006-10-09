/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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

        String docNumber = transaction.getFinancialDocumentNumber();
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