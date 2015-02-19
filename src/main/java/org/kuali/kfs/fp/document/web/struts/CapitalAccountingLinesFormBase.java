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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class is the struts base form for financial form documents that need
 * capital accounting lines.
 */
public abstract class CapitalAccountingLinesFormBase extends CapitalAssetInformationFormBase {
    //transient objects
    protected transient CapitalAccountingLine capitalAccountingLine;
    protected transient boolean editCreateOrModify;
    protected transient KualiDecimal systemControlAmount;
    protected transient KualiDecimal createdAssetsControlAmount;
    protected transient boolean distributeEqualAmount;

    /**
     * Constructs a AdvanceDepositForm.java.
     */
    public CapitalAccountingLinesFormBase() {
        super();
        
        this.setSystemControlAmount(KualiDecimal.ZERO);
        capitalAccountingLine = new CapitalAccountingLine();
        editCreateOrModify = true;
        distributeEqualAmount = true;
    }
    
    /**
     * Gets the capitalAccountingLine attribute.
     * 
     * @return Returns the capitalAccountingLine
     */
    
    public CapitalAccountingLine getCapitalAccountingLine() {
        return capitalAccountingLine;
    }

    /** 
     * Sets the capitalAccountingLine attribute.
     * 
     * @param capitalAccountingLine The capitalAccountingLine to set.
     */
    public void setCapitalAccountingLine(CapitalAccountingLine capitalAccountingLine) {
        this.capitalAccountingLine = capitalAccountingLine;
    }
    
    /**
     * Gets the editCreateOrModify attribute.
     * 
     * @return Returns the editCreateOrModify
     */
    
    public boolean isEditCreateOrModify() {
        return editCreateOrModify;
    }

    /** 
     * Sets the editCreateOrModify attribute.
     * 
     * @param editCreateOrModify The editCreateOrModify to set.
     */
    public void setEditCreateOrModify(boolean editCreateOrModify) {
        this.editCreateOrModify = editCreateOrModify;
    }

    /**
     * Gets the systemControlAmount attribute.
     * 
     * @return Returns the systemControlAmount
     */
    
    public KualiDecimal getSystemControlAmount() {
        return systemControlAmount;
    }

    /** 
     * Sets the systemControlAmount attribute.
     * 
     * @param systemControlAmount The systemControlAmount to set.
     */
    public void setSystemControlAmount(KualiDecimal systemControlAmount) {
        this.systemControlAmount = systemControlAmount;
    }
    
    /**
     * Gets the createdAssetsControlAmount attribute.
     * 
     * @return Returns the createdAssetsControlAmount
     */
    
    public KualiDecimal getCreatedAssetsControlAmount() {
        return createdAssetsControlAmount;
    }

    /** 
     * Sets the createdAssetsControlAmount attribute.
     * 
     * @param createdAssetsControlAmount The createdAssetsControlAmount to set.
     */
    public void setCreatedAssetsControlAmount(KualiDecimal createdAssetsControlAmount) {
        this.createdAssetsControlAmount = createdAssetsControlAmount;
    }
    
    /**
     * Gets the distributeEqualAmount attribute.
     * 
     * @return Returns the distributeEqualAmount
     */
    
    public boolean isDistributeEqualAmount() {
        return distributeEqualAmount;
    }

    /** 
     * Sets the distributeEqualAmount attribute.
     * 
     * @param distributeEqualAmount The distributeEqualAmount to set.
     */
    public void setDistributeEqualAmount(boolean distributeEqualAmount) {
        this.distributeEqualAmount = distributeEqualAmount;
    }
    
}
