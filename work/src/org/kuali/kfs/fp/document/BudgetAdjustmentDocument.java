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

import java.util.LinkedHashMap;

import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);

    private String financialDocumentNumber;
	private Integer financialDocumentNextFromLineNumber;
	private Integer financialDocumentNextToLineNumber;
	private Integer financialDocumentNextPositionFromLineNumber;
	private Integer financialDocumentNextPositionToLineNumber;
	private Integer financialDocumentPostingYear;
	private String financialDocumentPostingPeriodCode;
	private String financialDocumentExplanationText;

    private DocumentHeader financialDocument;

	/**
	 * Default constructor.
	 */
	public BudgetAdjustmentDocument() {
        sourceAccountingLines = new TypedArrayList(BudgetAdjustmentSourceAccountingLine.class);
        targetAccountingLines = new TypedArrayList(BudgetAdjustmentTargetAccountingLine.class);
	}

	/**
	 * Gets the financialDocumentNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNumber
	 * 
	 */
	public String getFinancialDocumentNumber() { 
		return financialDocumentNumber;
	}

	/**
	 * Sets the financialDocumentNumber attribute.
	 * 
	 * @param - financialDocumentNumber The financialDocumentNumber to set.
	 * 
	 */
	public void setFinancialDocumentNumber(String financialDocumentNumber) {
		this.financialDocumentNumber = financialDocumentNumber;
	}


	/**
	 * Gets the financialDocumentNextFromLineNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNextFromLineNumber
	 * 
	 */
	public Integer getFinancialDocumentNextFromLineNumber() { 
		return financialDocumentNextFromLineNumber;
	}

	/**
	 * Sets the financialDocumentNextFromLineNumber attribute.
	 * 
	 * @param - financialDocumentNextFromLineNumber The financialDocumentNextFromLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentNextFromLineNumber(Integer financialDocumentNextFromLineNumber) {
		this.financialDocumentNextFromLineNumber = financialDocumentNextFromLineNumber;
	}


	/**
	 * Gets the financialDocumentNextToLineNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNextToLineNumber
	 * 
	 */
	public Integer getFinancialDocumentNextToLineNumber() { 
		return financialDocumentNextToLineNumber;
	}

	/**
	 * Sets the financialDocumentNextToLineNumber attribute.
	 * 
	 * @param - financialDocumentNextToLineNumber The financialDocumentNextToLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentNextToLineNumber(Integer financialDocumentNextToLineNumber) {
		this.financialDocumentNextToLineNumber = financialDocumentNextToLineNumber;
	}


	/**
	 * Gets the financialDocumentNextPositionFromLineNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNextPositionFromLineNumber
	 * 
	 */
	public Integer getFinancialDocumentNextPositionFromLineNumber() { 
		return financialDocumentNextPositionFromLineNumber;
	}

	/**
	 * Sets the financialDocumentNextPositionFromLineNumber attribute.
	 * 
	 * @param - financialDocumentNextPositionFromLineNumber The financialDocumentNextPositionFromLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentNextPositionFromLineNumber(Integer financialDocumentNextPositionFromLineNumber) {
		this.financialDocumentNextPositionFromLineNumber = financialDocumentNextPositionFromLineNumber;
	}


	/**
	 * Gets the financialDocumentNextPositionToLineNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNextPositionToLineNumber
	 * 
	 */
	public Integer getFinancialDocumentNextPositionToLineNumber() { 
		return financialDocumentNextPositionToLineNumber;
	}

	/**
	 * Sets the financialDocumentNextPositionToLineNumber attribute.
	 * 
	 * @param - financialDocumentNextPositionToLineNumber The financialDocumentNextPositionToLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentNextPositionToLineNumber(Integer financialDocumentNextPositionToLineNumber) {
		this.financialDocumentNextPositionToLineNumber = financialDocumentNextPositionToLineNumber;
	}


	/**
	 * Gets the financialDocumentPostingYear attribute.
	 * 
	 * @return - Returns the financialDocumentPostingYear
	 * 
	 */
	public Integer getFinancialDocumentPostingYear() { 
		return financialDocumentPostingYear;
	}

	/**
	 * Sets the financialDocumentPostingYear attribute.
	 * 
	 * @param - financialDocumentPostingYear The financialDocumentPostingYear to set.
	 * 
	 */
	public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
		this.financialDocumentPostingYear = financialDocumentPostingYear;
	}


	/**
	 * Gets the financialDocumentPostingPeriodCode attribute.
	 * 
	 * @return - Returns the financialDocumentPostingPeriodCode
	 * 
	 */
	public String getFinancialDocumentPostingPeriodCode() { 
		return financialDocumentPostingPeriodCode;
	}

	/**
	 * Sets the financialDocumentPostingPeriodCode attribute.
	 * 
	 * @param - financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
	 * 
	 */
	public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
		this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
	}


	/**
	 * Gets the financialDocumentExplanationText attribute.
	 * 
	 * @return - Returns the financialDocumentExplanationText
	 * 
	 */
	public String getFinancialDocumentExplanationText() { 
		return financialDocumentExplanationText;
	}

	/**
	 * Sets the financialDocumentExplanationText attribute.
	 * 
	 * @param - financialDocumentExplanationText The financialDocumentExplanationText to set.
	 * 
	 */
	public void setFinancialDocumentExplanationText(String financialDocumentExplanationText) {
		this.financialDocumentExplanationText = financialDocumentExplanationText;
	}


	/**
	 * Gets the financialDocument attribute.
	 * 
	 * @return - Returns the financialDocument
	 * 
	 */
	public DocumentHeader getFinancialDocument() { 
		return financialDocument;
	}

	/**
	 * Sets the financialDocument attribute.
	 * 
	 * @param - financialDocument The financialDocument to set.
	 * @deprecated
	 */
	public void setFinancialDocument(DocumentHeader financialDocument) {
		this.financialDocument = financialDocument;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentNumber", this.financialDocumentNumber);
	    return m;
    }
}
