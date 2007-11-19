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
package org.kuali.module.gl.dao;

import org.kuali.module.gl.bo.CollectorDetail;

/**
 * 
 */
public interface CollectorDetailDao {
    /**
     * Purge the table by year/chart
     * 
     * @param chartOfAccountsCode chart of accounts code criteria to purge
     * @param universityFiscalYear university fiscal year criteria to purge
     */
    public void purgeYearByChart(String chartOfAccountsCode, int universityFiscalYear);

    /**
     * Saves a specific collector detail
     * 
     * @param detail
     */
    public void save(CollectorDetail detail);

    /**
     * Retrieves the DB table name that's mapped to instances of CollectorDetail
     * 
     * @return String representing DB table name for CollectorDetails
     */
    public String retrieveCollectorDetailTableName();
}
