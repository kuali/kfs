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
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;

/**
 * define the service methods that are related to budget construction administrative post class
 * 
 * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost
 */
public interface BudgetConstructionAdministrativePostService {
    /**
     * retrieve a Budget Construction Administrative Post object by its primary key.
     * 
     * @param emplid the given employee id
     * @param positionNumber the given position number
     * @return a Budget Construction Administrative Post object retrived by its primary key
     */
    public BudgetConstructionAdministrativePost getByPrimaryId(String emplid, String positionNumber);
}
