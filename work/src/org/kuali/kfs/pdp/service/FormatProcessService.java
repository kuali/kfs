/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.service;

import java.util.Date;

import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.rice.kns.bo.user.UniversalUser;

/**
 * This interface declares methods for the format process.
 */
public interface FormatProcessService {
    
    /**
     * This method gets the data for the format process
     * @param user the user that initiated the format process
     * @return FormatSelection
     */
    public FormatSelection getDataForFormat(UniversalUser user);
    
    /**
     * This method gets the format process by campus code and returns the start date for that process.
     * @param campus the campus code
     * @return the format process start date if any process found for the given campus code, null otherwise
     */
    public Date getFormatProcessStartDate(String campus);

}
