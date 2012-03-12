/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.batch.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.dataaccess.impl.FiscalYearMakerImpl;

/**
 * Performs custom criteria of object code records for a new year being created in the fiscal year maker process
 */
public class ObjectCodeFiscalYearMakerImpl extends FiscalYearMakerImpl {

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#createSelectionCriteria(java.lang.Integer)
     */
    @Override
    public Criteria createSelectionCriteria(Integer baseFiscalYear) {
        // get default criteria equal to base year and active
        Criteria criteria = super.createSelectionCriteria(baseFiscalYear);

        // or with criteria to pick up budget dummy object for base year (active or inactive)
        Criteria criteriaBdg = new Criteria();
        criteriaBdg.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, baseFiscalYear);
        // REMOVE: THIS IS REALLY, REALLY BAD!  We should not be hard-coding object code values ANYWHERE!
        criteriaBdg.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
        criteria.addOrCriteria(criteriaBdg);

        return criteria;
    }

}
