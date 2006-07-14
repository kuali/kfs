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

import java.util.Properties;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;

/**
 * This class provides a placeholder that can connect General Ledger business object with
 * financial document in the presentation tier. The typical method is to generate url for
 * the inquirable financial document.
 * 
 * @author Bin Gao
 */
public class InquirableFinancialDocument {

    /**
     * get the url of inquirable financial document for the given transaction
     * 
     * @param transaction the business object that implements Transaction interface 
     * @return the url of inquirable financial document for the given transaction if the
     * document is inquirable; otherwise, return empty string
     */
    public String getInquirableDocumentUrl(Transaction transaction) {
        DataDictionaryService dataDictionary = SpringServiceLocator.getDataDictionaryService();

        if (this.isInquirableDocument(transaction)) {
            Properties parameters = this.addParameters(transaction);

            String docTypeCode = transaction.getFinancialDocumentTypeCode();
            String docTypeName = dataDictionary.getDocumentTypeNameByTypeCode(docTypeCode);
            if (docTypeName != null) {
                String baseUrl = this.getBaseUrl(docTypeName);
                return UrlFactory.parameterizeUrl(baseUrl, parameters);
            }
        }
        return Constants.EMPTY_STRING;
    }

    // determine if the document of the given transaction is inquirable
    private boolean isInquirableDocument(Transaction transaction) {
        int lengthOfDocumentNumber = transaction.getFinancialDocumentNumber() == null ? 0 : transaction.getFinancialDocumentNumber().length();
        
        boolean isInquirable = Constants.ORIGIN_CODE_KUALI.equals(transaction.getFinancialSystemOriginationCode());
        isInquirable = isInquirable && (lengthOfDocumentNumber == 6);        
        return isInquirable;
    }

    // build the parameter list that can be the query strings of inquiry url
    private Properties addParameters(Transaction transaction) {
        Properties parameters = new Properties();
        parameters.put("methodToCall", "docHandler");
        parameters.put("command", "displayDocSearchView");
        parameters.put("docId", transaction.getFinancialDocumentNumber());

        return parameters;
    }

    // get the base url of the inquiry url that is based on the document type name
    private String getBaseUrl(String documentTypeName) {
        String baseUrl = documentTypeName.substring(5, documentTypeName.indexOf("Document"));
        return "financial" + baseUrl + ".do";
    }
}