/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.gl.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents a pending balances move
 * 
 */
public class PendingBalancesMove extends PersistableBusinessObjectBase {

    private String principalId;
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
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        return m;
    }
}

