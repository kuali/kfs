/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

