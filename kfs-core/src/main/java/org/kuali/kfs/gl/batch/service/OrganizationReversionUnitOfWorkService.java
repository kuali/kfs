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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.businessobject.OrgReversionUnitOfWork;

/**
 * These services have to do with database interactions with an "Org Reversion Unit of Work" - which is basically a
 * Chart-Account-SubAccount that will be/has been reverted during the year end organization reversion process, which is the great
 * vacuum that sucks the contents out of accounts.
 */
public interface OrganizationReversionUnitOfWorkService {
    /**
     * This method is to rain the very blows of destruction on all org reversion unit of work data, so the tables can be rebuilt as
     * the OrgReversionProcess runs again
     */
    public void destroyAllUnitOfWorkSummaries();

    /**
     * This method takes a unit of work retrieved from the persistence store and loads its categories
     * 
     * @param orgRevUnitOfWork org reversion unit of work to load categories for
     * @return the org reversion unit of work with loaded categories
     */
    public OrgReversionUnitOfWork loadCategories(OrgReversionUnitOfWork orgRevUnitOfWork);

    /**
     * This save method is guaranteed to save the category data as well.
     * 
     * @param orgRevUnitOfWork organizationReversionUnitOfWork to save
     */
    public void save(OrgReversionUnitOfWork orgRevUnitOfWork);
}
