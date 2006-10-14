/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.kra.budget.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;


/**
 * Account Business Object
 * 
 * @author Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetTask extends BusinessObjectBase implements Comparable {

    private static final long serialVersionUID = 2905826718156063909L;
    private String documentHeaderId;
    private Integer budgetTaskSequenceNumber;
    private String budgetTaskName;
    private boolean budgetTaskOnCampus;

    public BudgetTask() {
        super();
    }

    public BudgetTask(String documentHeaderId, Integer budgetTaskSequenceNumber) {
        this();
        this.documentHeaderId = documentHeaderId;
        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
    }
    
    public BudgetTask(BudgetTask template) {
        this.documentHeaderId = template.getDocumentHeaderId();
        this.budgetTaskSequenceNumber = template.getBudgetTaskSequenceNumber();
        this.budgetTaskName = template.getBudgetTaskName();
        this.setBudgetTaskOnCampus(template.isBudgetTaskOnCampus());
    }

    /**
     * @return Returns the documentHeaderId.
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * @param documentHeaderId The documentHeaderId to set.
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * @return Returns the budgetTaskName.
     */
    public String getBudgetTaskName() {
        return budgetTaskName;
    }

    /**
     * @param budgetTaskName The budgetTaskName to set.
     */
    public void setBudgetTaskName(String budgetTaskName) {
        this.budgetTaskName = budgetTaskName;
    }

    /**
     * @param budgetTaskOnCampus
     */
    public void setBudgetTaskOnCampus(boolean budgetTaskOnCampus) {
        this.budgetTaskOnCampus = budgetTaskOnCampus;
    }

    /**
     * @return Returns boolean saying whether or not a budgetTask is on campus.
     */
    public boolean isBudgetTaskOnCampus() {
        return budgetTaskOnCampus;
    }

    /**
     * @return Returns the budgetTaskSequenceNumber.
     */
    public Integer getBudgetTaskSequenceNumber() {
        return budgetTaskSequenceNumber;
    }

    /**
     * @param budgetTaskSequenceNumber The budgetTaskSequenceNumber to set.
     */
    public void setBudgetTaskSequenceNumber(Integer budgetTaskSequenceNumber) {
        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentHeaderId", this.documentHeaderId);
        m.put("budgetTaskSequenceNumber", this.budgetTaskSequenceNumber);
        return m;

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
                BudgetTask other = (BudgetTask) obj;

                if (this.getDocumentHeaderId().equals(other.getDocumentHeaderId()) && this.getBudgetTaskSequenceNumber().equals(other.getBudgetTaskSequenceNumber()) && StringUtils.equals(getBudgetTaskName(), other.getBudgetTaskName())) {
                    equal = true;
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of documentHeaderId, budgetTaskSequenceNumber and budgetTaskName fields. Somewhat
     * dangerous, since those fields are mutable, but I don't expect people to be editing those values directly for Tasks stored in
     * hashed datastructures. (this is based on Account.hashCode()) - TAD
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getDocumentHeaderId() + "|" + getBudgetTaskSequenceNumber() + "|" + getBudgetTaskName();

        return hashString.hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getBudgetTaskSequenceNumber().compareTo(((BudgetTask) o).getBudgetTaskSequenceNumber());
    }
}