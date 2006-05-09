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
package org.kuali.module.financial.bo;

import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.KualiDecimal;


/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentTargetAccountingLine extends TargetAccountingLine {

	private String budgetAdjustmentPeriodCode;
    private KualiDecimal currentBudgetAdjustmentAmount;
    private KualiDecimal baseBudgetAdjustmentAmount;

    //add monthly BO 

    /**
	 * This constructor needs to initialize the ojbConcreteClass attribute such
	 * that it sets it to its class name. This is how OJB knows what grouping of
	 * objects to work with.
	 */
	public BudgetAdjustmentTargetAccountingLine() {
		super();
		super.ojbConcreteClass = this.getClass().getName();
	}

	public KualiDecimal getBaseBudgetAdjustmentAmount() {
		return baseBudgetAdjustmentAmount;
	}

	public void setBaseBudgetAdjustmentAmount(
			KualiDecimal baseBudgetAdjustmentAmount) {
		this.baseBudgetAdjustmentAmount = baseBudgetAdjustmentAmount;
	}

	public String getBudgetAdjustmentPeriodCode() {
		return budgetAdjustmentPeriodCode;
	}

	public void setBudgetAdjustmentPeriodCode(String budgetAdjustmentPeriodCode) {
		this.budgetAdjustmentPeriodCode = budgetAdjustmentPeriodCode;
	}

	public KualiDecimal getCurrentBudgetAdjustmentAmount() {
		return currentBudgetAdjustmentAmount;
	}

	public void setCurrentBudgetAdjustmentAmount(
			KualiDecimal currentBudgetAdjustmentAmount) {
		this.currentBudgetAdjustmentAmount = currentBudgetAdjustmentAmount;
	}
	
}