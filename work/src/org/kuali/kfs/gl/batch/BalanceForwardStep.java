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
package org.kuali.module.gl.batch;

import java.sql.Date;

import org.kuali.core.batch.Step;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.service.YearEndService;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */
public class BalanceForwardStep implements Step {

    private DateTimeService dateTimeService;
    private YearEndService yearEndService;
    
    public BalanceForwardStep() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.kuali.core.batch.Step#getName()
     */
    public String getName() {
        return "General Ledger Balance Forward Step";
    }
    
    /* (non-Javadoc)
     * @see org.kuali.core.batch.Step#performStep()
     */
    public boolean performStep() {

        Integer closingFiscalYear = dateTimeService.getCurrentFiscalYear();
        
        boolean selectActiveFlag = true;
        boolean selectGeneralFlag = true;
        Date transactionDate = null;
        
        // FIXME FIXME FIXME Hack alert!
        yearEndService = SpringServiceLocator.getGeneralLedgerYearEndService();
        yearEndService.setDateTimeService(dateTimeService);
        // FIXME FIXME FIXME End hack alert.
        yearEndService.forwardBalancesForFiscalYear(closingFiscalYear, selectActiveFlag, selectGeneralFlag, transactionDate);
        return true;
    }

    /**
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @param yearEndService The yearEndService to set.
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }
    
}
