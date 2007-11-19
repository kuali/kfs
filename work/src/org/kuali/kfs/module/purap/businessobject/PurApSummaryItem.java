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

/**
 * Purap Summary Item Business Object.
 */
public class PurApSummaryItem extends PurApItemBase {

    private KualiDecimal estimatedEncumberanceAmount;

    public PurApSummaryItem() {
        this(KualiDecimal.ZERO);
    }

    public PurApSummaryItem(KualiDecimal estimatedEncumberanceAmount) {
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }

    public KualiDecimal getEstimatedEncumberanceAmount() {
        return estimatedEncumberanceAmount;
    }

    public void setEstimatedEncumberanceAmount(KualiDecimal estimatedEncumberanceAmount) {
        this.estimatedEncumberanceAmount = estimatedEncumberanceAmount;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItem#getAccountingLineClass()
     */
    public Class getAccountingLineClass() {
        return SourceAccountingLine.class;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItem#isConsideredEntered()
     */
    public boolean isConsideredEntered() {
        return false;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#resetAccount()
     */
    @Override
    public void resetAccount() {
        // do nothing
    }
}
