/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;

public class PurApSummaryItem extends PurApItemBase {

    private KualiDecimal estimatedEncumberanceAmount;
	//TODO: abstract PurApItemBase so that this inherits less
    public PurApSummaryItem() {
        this(KualiDecimal.ZERO);
    }
    
    
    public PurApSummaryItem(KualiDecimal estimatedEncumberanceAmount){
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }
    /**
     * Gets the estimatedEncumberanceAmount attribute. 
     * @return Returns the estimatedEncumberanceAmount.
     */
    public KualiDecimal getEstimatedEncumberanceAmount() {
        return estimatedEncumberanceAmount;
    }

    /**
     * Sets the estimatedEncumberanceAmount attribute value.
     * @param estimatedEncumberanceAmount The estimatedEncumberanceAmount to set.
     */
    public void setEstimatedEncumberanceAmount(KualiDecimal estimatedEncumberanceAmount) {
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }

    @Override
    public Class getAccountingLineClass() {
        return SourceAccountingLine.class;
    }

    public boolean isConsideredEntered() {
        return false;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#resetAccount()
     */
    @Override
    public void resetAccount() {
        //do nothing
    }

}
