/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.document.TransactionalDocumentBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashManagementDocument extends TransactionalDocumentBase {
    private String workgroupName;
    private String financialDocumentReferenceNumber;

    private List deposits;


    /**
     * Default constructor.
     */
    public CashManagementDocument() {
        deposits = new ArrayList();
    }


    /**
     * @return current value of deposits.
     */
    public List getDeposits() {
        return deposits;
    }

    /**
     * Sets the deposits attribute value.
     * 
     * @param deposits The deposits to set.
     */
    public void setDeposits(List deposits) {
        this.deposits = deposits;
    }


    /**
     * @return current value of financialDocumentReferenceNumber.
     */
    public String getFinancialDocumentReferenceNumber() {
        return financialDocumentReferenceNumber;
    }

    /**
     * Sets the financialDocumentReferenceNumber attribute value.
     * 
     * @param financialDocumentReferenceNumber The financialDocumentReferenceNumber to set.
     */
    public void setFinancialDocumentReferenceNumber(String financialDocumentReferenceNumber) {
        this.financialDocumentReferenceNumber = financialDocumentReferenceNumber;
    }


    /**
     * @return current value of workgroupName.
     */
    public String getWorkgroupName() {
        return workgroupName;
    }

    /**
     * Sets the workgroupName attribute value.
     * 
     * @param workgroupName The workgroupName to set.
     */
    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }


    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", getFinancialDocumentNumber());
        m.put("workgroupName", getWorkgroupName());
        return m;
    }
}
