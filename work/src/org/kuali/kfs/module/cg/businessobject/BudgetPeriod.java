package org.kuali.module.kra.budget.bo;

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

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class BudgetPeriod extends BusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -479888264761887048L;

    private String documentHeaderId;
    private Integer budgetPeriodSequenceNumber;
    private Date budgetPeriodBeginDate;
    private Date budgetPeriodEndDate;
    private Integer budgetPeriodParticipantsNumber;

    /**
     * Default no-arg constructor.
     */
    public BudgetPeriod() {
        super();
    }
    
    public BudgetPeriod(BudgetPeriod template) {
        this();
        this.documentHeaderId = template.getDocumentHeaderId();
        this.budgetPeriodSequenceNumber = template.getBudgetPeriodSequenceNumber();
        this.budgetPeriodBeginDate = template.getBudgetPeriodBeginDate();
        this.budgetPeriodEndDate = template.getBudgetPeriodEndDate();
        this.budgetPeriodParticipantsNumber = template.getBudgetPeriodParticipantsNumber();
    }
    
    /**
     * @return Returns the budgetNumber.
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * @param budgetNumber The budgetNumber to set.
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * @return Returns the budgetPeriodBeginDate.
     */
    public Date getBudgetPeriodBeginDate() {
        return budgetPeriodBeginDate;
    }

    /**
     * @param budgetPeriodBeginDate The budgetPeriodBeginDate to set.
     */
    public void setBudgetPeriodBeginDate(Date budgetPeriodBeginDate) {
        this.budgetPeriodBeginDate = budgetPeriodBeginDate;
    }

    /**
     * @return Returns the budgetPeriodEndDate.
     */
    public Date getBudgetPeriodEndDate() {
        return budgetPeriodEndDate;
    }

    /**
     * @param budgetPeriodEndDate The budgetPeriodEndDate to set.
     */
    public void setBudgetPeriodEndDate(Date budgetPeriodEndDate) {
        this.budgetPeriodEndDate = budgetPeriodEndDate;
    }

    /**
     * @return Returns the budgetPeriodSequenceNumber.
     */
    public Integer getBudgetPeriodSequenceNumber() {
        if (budgetPeriodSequenceNumber == null) {
            budgetPeriodSequenceNumber = Integer.valueOf("0");
        }
        return budgetPeriodSequenceNumber;
    }

    /**
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * @return Returns the budgetPeriodParticipantsNumber.
     */
    public Integer getBudgetPeriodParticipantsNumber() {
        return budgetPeriodParticipantsNumber;
    }

    /**
     * @param budgetPeriodParticipantsNumber The budgetPeriodParticipantsNumber to set.
     */
    public void setBudgetPeriodParticipantsNumber(Integer budgetPeriodParticipantsNumber) {
        this.budgetPeriodParticipantsNumber = budgetPeriodParticipantsNumber;
    }


    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentHeaderId", this.documentHeaderId);
        m.put("budgetPeriodSequenceNumber", this.budgetPeriodSequenceNumber);
        return m;
    }

    public String getBudgetPeriodLabel() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String startDate = "(no start date)";
        String endDate = "(no end date)";

        if (this.getBudgetPeriodBeginDate() != null) {
            startDate = dateFormat.format(this.getBudgetPeriodBeginDate());
        }

        if (this.getBudgetPeriodEndDate() != null) {
            endDate = dateFormat.format(this.getBudgetPeriodEndDate());
        }

        return startDate + " - " + endDate;
    }

    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                BudgetPeriod other = (BudgetPeriod) obj;

                if (this.getDocumentHeaderId().equals(other.getDocumentHeaderId()) && this.getBudgetPeriodSequenceNumber().equals(other.getBudgetPeriodSequenceNumber()) && this.getBudgetPeriodBeginDate().equals(other.getBudgetPeriodBeginDate()) && this.getBudgetPeriodEndDate().equals(other.getBudgetPeriodEndDate())) {
                    equal = true;
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of documentHeaderId and budgetPeriodSequenceNumber fields. Somewhat dangerous,
     * since those fields are mutable, but I don't expect people to be editing those values directly for Periods stored in hashed
     * datastructures. (this is based on Account.hashCode()) - TAD
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getDocumentHeaderId() + "|" + getBudgetPeriodSequenceNumber() + getBudgetPeriodBeginDate() + "|" + getBudgetPeriodEndDate();

        return hashString.hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getBudgetPeriodSequenceNumber().compareTo(((BudgetPeriod) o).getBudgetPeriodSequenceNumber());
    }
}
