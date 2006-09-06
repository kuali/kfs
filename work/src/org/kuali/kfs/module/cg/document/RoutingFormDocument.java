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
package org.kuali.module.kra.routingform.document;

import java.util.LinkedHashMap;

import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.exceptions.IllegalObjectStateException;
import org.kuali.module.kra.budget.document.ResearchDocumentBase;

public class RoutingFormDocument extends ResearchDocumentBase {
    
    public RoutingFormDocument() {
        super();
    }
    
    /**
     * @see org.kuali.module.chart.bo.ResearchAdministrationDocumentBase#generatePEsfromAL(org.kuali.bo.AccountingLineBase, boolean,
     *      int)
     */
    public void generatePEsfromAL(AccountingLineBase line, boolean isSource, int counter) throws IllegalObjectStateException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.document.Document#populateDocumentForRouting()
     */
    public void populateDocumentForRouting() {
        // TODO Auto-generated method stub
        super.populateDocumentForRouting();
    }

    /**
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        // TODO Auto-generated method stub
        return "BudgetDocument";
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.financialDocumentNumber);

        return m;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#validate()
     */
    public void validate() throws IllegalObjectStateException {
        // TODO Auto-generated method stub
        super.validate();
    }

}
