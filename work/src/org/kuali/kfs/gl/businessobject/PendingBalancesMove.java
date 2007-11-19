/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * This class represents a pending balances move
 * 
 */
public class PendingBalancesMove extends PersistableBusinessObjectBase {

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
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the appropriationBudget attribute.
     * 
     * @return Returns the appropriationBudget
     */
    public KualiDecimal getAppropriationBudget() {
        return appropriationBudget;
    }

    /**
     * Sets the appropriationBudget attribute.
     * 
     * @param appropriationBudget The appropriationBudget to set.
     */
    public void setAppropriationBudget(KualiDecimal appropriationBudget) {
        this.appropriationBudget = appropriationBudget;
    }


    /**
     * Gets the appropriationActual attribute.
     * 
     * @return Returns the appropriationActual
     */
    public KualiDecimal getAppropriationActual() {
        return appropriationActual;
    }

    /**
     * Sets the appropriationActual attribute.
     * 
     * @param appropriationActual The appropriationActual to set.
     */
    public void setAppropriationActual(KualiDecimal appropriationActual) {
        this.appropriationActual = appropriationActual;
    }


    /**
     * Gets the appropriationEncumbrance attribute.
     * 
     * @return Returns the appropriationEncumbrance
     */
    public KualiDecimal getAppropriationEncumbrance() {
        return appropriationEncumbrance;
    }

    /**
     * Sets the appropriationEncumbrance attribute.
     * 
     * @param appropriationEncumbrance The appropriationEncumbrance to set.
     */
    public void setAppropriationEncumbrance(KualiDecimal appropriationEncumbrance) {
        this.appropriationEncumbrance = appropriationEncumbrance;
    }


    /**
     * Gets the pendingBudget attribute.
     * 
     * @return Returns the pendingBudget
     */
    public KualiDecimal getPendingBudget() {
        return pendingBudget;
    }

    /**
     * Sets the pendingBudget attribute.
     * 
     * @param pendingBudget The pendingBudget to set.
     */
    public void setPendingBudget(KualiDecimal pendingBudget) {
        this.pendingBudget = pendingBudget;
    }


    /**
     * Gets the pendingActual attribute.
     * 
     * @return Returns the pendingActual
     */
    public KualiDecimal getPendingActual() {
        return pendingActual;
    }

    /**
     * Sets the pendingActual attribute.
     * 
     * @param pendingActual The pendingActual to set.
     */
    public void setPendingActual(KualiDecimal pendingActual) {
        this.pendingActual = pendingActual;
    }


    /**
     * Gets the pendingEncumbrance attribute.
     * 
     * @return Returns the pendingEncumbrance
     */
    public KualiDecimal getPendingEncumbrance() {
        return pendingEncumbrance;
    }

    /**
     * Sets the pendingEncumbrance attribute.
     * 
     * @param pendingEncumbrance The pendingEncumbrance to set.
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
