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
/*
 * Created on Aug 16, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.util.Date;

import org.kuali.kfs.pdp.businessobject.FormatProcess;


/**
 * The DAO interface that provides data access methods for the FormatProcess
 */
public interface FormatProcessDao {
    
    /**
     * This method gets a format process by campus.
     * @param campus the format process campus
     * @return the format process for the given campus
     */
    public FormatProcess getByCampus(String campus);

    /**
     * This method removes an entry from the format process tbale by campus.
     * @param campus the campus of the format process to be removed
     */
    public void removeByCampus(String campus);

    /**
     * This method adds a new entry in the format process table.
     * @param campus the campus for which the format process runs
     * @param now the time the format process starts
     */
    public void add(String campus, Date now);
}
