/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;

public interface FeeMethodDao {

    /**
     * Gets FeeMethod whose next income pay date is equal to the current date and whose frequency code is valid
     * 
     * @param currentDate current date
     * @return List<FeeMethod>
     */
    public List<FeeMethod> getFeeMethodWithNextPayDateEqualToCurrentDate(Date currentDate);
}
