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
