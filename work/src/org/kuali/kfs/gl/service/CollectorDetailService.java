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
package org.kuali.module.gl.service;

import org.kuali.module.gl.bo.CollectorDetail;

/**
 * Services that deal with Collector Details
 */
public interface CollectorDetailService {
    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart chart of CollectorDetails to purge
     * @param year year of CollectorDetails to purage
     */
    public void purgeYearByChart(String chartOfAccountsCode, int universityFiscalYear);

    /**
     * Saves a CollectorDetail
     * 
     * @param detail the detail to save
     */
    public void save(CollectorDetail detail);

}
