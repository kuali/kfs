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
package org.kuali.kfs.integration.cg;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public interface ContractsAndGrantsCfda extends ExternalizableBusinessObject {
    /**
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber(); 
    
    /**
     * Gets the cfdaProgramTitleName attribute.
     * 
     * @return Returns the cfdaProgramTitleName
     */
    public String getCfdaProgramTitleName();
    
    /**
     * Gets the cfdaMaintenanceTypeId attribute.
     * 
     * @return Returns the cfdaMaintenanceTypeId
     */
    public String getCfdaMaintenanceTypeId();
}
