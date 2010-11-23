/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.sql.Date;
import org.kuali.kfs.module.endow.businessobject.FrequencyCode;

public interface FrequencyCodeService {

    /**
     * Gets a frequency code by primary key.
     * 
     * @param code
     * @return a frequency code
     */
    public FrequencyCode getByPrimaryKey(String code);

    /**
     * This method uses frequency code to derive the next processing date
     * @param frequecyCode
     * @return next process date
     */
    public Date calculateProcessDate(String frequencyCode);
    
    /**
     * Gets the next due date according to the frequency code
     * @param frequenctCode
     * @param currentDate
     * @return next due date
     */
    public Date calculateNextDueDate(String frequenctCode, Date currentDate);

}
