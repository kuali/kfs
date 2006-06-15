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

package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PendingBalancesMove extends BusinessObjectBase {

    private String personUniversalIdentifier;
    private KualiDecimal appropriationBudget;
    private KualiDecimal appropriationActual;
    private KualiDecimal appropriationEncumbrance;
    private KualiDecimal pendingBudget;
    private KualiDecimal pendingActual;
    private KualiDecimal pendingEncumbrance;

    /**
     * Default constructor.
     */
    public PendingBalancesMove() {

    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the appropriationBudget attribute.
     * 
     * @return Returns the appropriationBudget
     * 
     */
    public KualiDecimal getAppropriationBudget() {
        return appropriationBudget;
    }

    /**
     * Sets the appropriationBudget attribute.
     * 
     * @param appropriationBudget The appropriationBudget to set.
     * 
     */
    public void setAppropriationBudget(KualiDecimal appropriationBudget) {
        this.appropriationBudget = appropriationBudget;
    }


    /**
     * Gets the appropriationActual attribute.
     * 
     * @return Returns the appropriationActual
     * 
     */
    public KualiDecimal getAppropriationActual() {
        return appropriationActual;
    }

    /**
     * Sets the appropriationActual attribute.
     * 
     * @param appropriationActual The appropriationActual to set.
     * 
     */
    public void setAppropriationActual(KualiDecimal appropriationActual) {
        this.appropriationActual = appropriationActual;
    }


    /**
     * Gets the appropriationEncumbrance attribute.
     * 
     * @return Returns the appropriationEncumbrance
     * 
     */
    public KualiDecimal getAppropriationEncumbrance() {
        return appropriationEncumbrance;
    }

    /**
     * Sets the appropriationEncumbrance attribute.
     * 
     * @param appropriationEncumbrance The appropriationEncumbrance to set.
     * 
     */
    public void setAppropriationEncumbrance(KualiDecimal appropriationEncumbrance) {
        this.appropriationEncumbrance = appropriationEncumbrance;
    }


    /**
     * Gets the pendingBudget attribute.
     * 
     * @return Returns the pendingBudget
     * 
     */
    public KualiDecimal getPendingBudget() {
        return pendingBudget;
    }

    /**
     * Sets the pendingBudget attribute.
     * 
     * @param pendingBudget The pendingBudget to set.
     * 
     */
    public void setPendingBudget(KualiDecimal pendingBudget) {
        this.pendingBudget = pendingBudget;
    }


    /**
     * Gets the pendingActual attribute.
     * 
     * @return Returns the pendingActual
     * 
     */
    public KualiDecimal getPendingActual() {
        return pendingActual;
    }

    /**
     * Sets the pendingActual attribute.
     * 
     * @param pendingActual The pendingActual to set.
     * 
     */
    public void setPendingActual(KualiDecimal pendingActual) {
        this.pendingActual = pendingActual;
    }


    /**
     * Gets the pendingEncumbrance attribute.
     * 
     * @return Returns the pendingEncumbrance
     * 
     */
    public KualiDecimal getPendingEncumbrance() {
        return pendingEncumbrance;
    }

    /**
     * Sets the pendingEncumbrance attribute.
     * 
     * @param pendingEncumbrance The pendingEncumbrance to set.
     * 
     */
    public void setPendingEncumbrance(KualiDecimal pendingEncumbrance) {
        this.pendingEncumbrance = pendingEncumbrance;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        return m;
    }
}
