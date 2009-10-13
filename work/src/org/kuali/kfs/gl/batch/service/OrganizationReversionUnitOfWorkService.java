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
