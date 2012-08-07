/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service;

import java.util.Collection;

import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;

/**
 * This interface provides its clients with access to labor position object benefit entries in the backend data store.
 */
public interface LaborPositionObjectBenefitService {

    /**
     * find the position object benefits matching the given information (fiscal year, chart oc account code and object code)
     * 
     * @param universityFiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param financialObjectCode the given object code
     * @return the position object benefits matching the given information
     */
    public Collection<PositionObjectBenefit> getPositionObjectBenefits(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
    public Collection<PositionObjectBenefit> getActivePositionObjectBenefits(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
}
