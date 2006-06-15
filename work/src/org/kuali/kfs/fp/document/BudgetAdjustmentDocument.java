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

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.rules.BudgetAdjustmentRuleConstants;

/**
 * This is the business object that represents the BudgetAdjustment document in Kuali.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);

    private Integer nextPositionSourceLineNumber;
    private Integer nextPositionTargetLineNumber;

    /**
     * Default constructor.
     */
    public BudgetAdjustmentDocument() {
        super();
    }

    /**
     * generic, shared logic used to iniate a ba document
     */
    public void initiateDocument() {

        // setting default posting year
        Integer currentYearParam = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        Integer defaultYearParam = null;

        try {
            defaultYearParam = new Integer(Integer.parseInt(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(BudgetAdjustmentRuleConstants.GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, BudgetAdjustmentRuleConstants.DEFAULT_FISCAL_YEAR_PARM_NM)));
        }
        catch (ApplicationParameterException e) {
            // DO NOTHING: we don't want to throw an error if the default value is not found, just don't set it in the list
        }

        if (defaultYearParam != null) {
            setPostingYear(defaultYearParam);
        }
        else {
            setPostingYear(currentYearParam);
        }

    }

    public Integer getNextPositionSourceLineNumber() {
        return nextPositionSourceLineNumber;
    }

    public void setNextPositionSourceLineNumber(Integer nextPositionSourceLineNumber) {
        this.nextPositionSourceLineNumber = nextPositionSourceLineNumber;
    }

    public Integer getNextPositionTargetLineNumber() {
        return nextPositionTargetLineNumber;
    }

    public void setNextPositionTargetLineNumber(Integer nextPositionTargetLineNumber) {
        this.nextPositionTargetLineNumber = nextPositionTargetLineNumber;
    }

    /**
     * Returns the total current budget amount from the source lines.
     */
    public KualiDecimal getSourceCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * Returns the total current budget amount from the target lines.
     */
    public KualiDecimal getTargetCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * Returns the total base budget amount from the source lines.
     */
    public KualiInteger getSourceBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }

    /**
     * Returns the total base budget amount from the target lines.
     */
    public KualiInteger getTargetBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }

    @Override
    public Class getSourceAccountingLineClass() {
        return BudgetAdjustmentSourceAccountingLine.class;
    }

    @Override
    public Class getTargetAccountingLineClass() {
        return BudgetAdjustmentTargetAccountingLine.class;
    }

}
