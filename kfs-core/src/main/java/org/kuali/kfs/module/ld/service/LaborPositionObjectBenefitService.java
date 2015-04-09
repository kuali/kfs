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
