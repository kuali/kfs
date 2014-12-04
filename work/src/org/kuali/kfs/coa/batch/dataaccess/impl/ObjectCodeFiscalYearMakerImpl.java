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
