/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.integration.bo;

import org.kuali.core.util.KualiDecimal;

/**
 * Information about fringe benefits needed by the BudgetAdjustment document to create fringe benefit lines
 */
public interface LaborFringeBenefitInformation {
    /**
     * The object code that an accounting line using this fringe benefit information should use
     * @return an object code
     */
    public abstract String getPositionFringeBenefitObjectCode();
    
    /**
     * the percent of an amount that qualifies as the fringe benefit
     * @return a percentage of an amount that fringe benefits should be
     */
    public abstract KualiDecimal getPositionFringeBenefitPercent();
}
