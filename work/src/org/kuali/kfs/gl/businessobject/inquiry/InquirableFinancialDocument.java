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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.gl.bo.Encumbrance;
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

    private DataDictionaryService dataDictionary = SpringServiceLocator.getDataDictionaryService();

    /**
     * get the url of inquirable financial document for the given transaction
     * 
     * @param transaction the business object that implements Transaction interface 
     * @return the url of inquirable financial document for the given transaction if the
     * document is inquirable; otherwise, return empty string
     */
    public String getInquirableDocumentUrl(Transaction transaction) {
        if(transaction == null){
            return Constants.EMPTY_STRING;
        }
        
        String docNumber   = transaction.getFinancialDocumentNumber();
        String originationCode = transaction.getFinancialSystemOriginationCode();
        
        if (this.isInquirableDocument(docNumber, originationCode)) {
            String docTypeCode = transaction.getFinancialDocumentTypeCode();
            String docTypeName = dataDictionary.getDocumentTypeNameByTypeCode(docTypeCode);
            
            Properties parameters = this.addParameters(docNumber);
            if (docTypeName != null) {
                return UrlFactory.buildDocumentActionUrl(docTypeName, parameters);
            }
        }
        return Constants.EMPTY_STRING;
    }
    
    /**
     * get the url of inquirable financial document for the given encumbrance
     * 
     * @param encumbrance the encumrbance record 
     * @return the url of inquirable financial document for the given encumbrance if the
     * document is inquirable; otherwise, return empty string
     */
    public String getInquirableDocumentUrl(Encumbrance encumbrance) {
        if(encumbrance == null){
            return Constants.EMPTY_STRING;
        }
        
        String docNumber   = encumbrance.getDocumentNumber();
        String originationCode = encumbrance.getOriginCode();
        
        if (this.isInquirableDocument(docNumber, originationCode)) {            
            String docTypeCode = encumbrance.getDocumentTypeCode();
            String docTypeName = dataDictionary.getDocumentTypeNameByTypeCode(docTypeCode);
            
            Properties parameters = this.addParameters(docNumber);          
            if (docTypeName != null) {
                return UrlFactory.buildDocumentActionUrl(docTypeName, parameters);
            }
        }
        return Constants.EMPTY_STRING;
    }
    
    // determine if the given document is inquirable
    private boolean isInquirableDocument(String documentNumber, String originationCode) {        
        return Constants.ORIGIN_CODE_KUALI.equals(originationCode) && !StringUtils.isBlank(documentNumber);       
    }
    
    // build the parameter list that can be the query strings of inquiry url
    private Properties addParameters(String documentNumber) {
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.DOC_HANDLER_METHOD);
        parameters.put(Constants.PARAMETER_COMMAND, Constants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        parameters.put(Constants.PARAMETER_DOC_ID, documentNumber);

        return parameters;
    }
}