/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.batch;

import org.kuali.core.batch.Step;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;

/**
 */
public class EncumbranceForwardStep implements Step {

    private YearEndService yearEndService;

    public EncumbranceForwardStep() {
        super();
    }

    /**
     * @see org.kuali.core.batch.Step#getName()
     */
    public String getName() {
        return "General Ledger Encumbrance Forward Step";
    }

    /**
     * @see org.kuali.core.batch.Step#performStep()
     */
    public boolean performStep() {
        yearEndService.forwardEncumbrances();
        return true;
    }

    /**
     * @param yearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

}
