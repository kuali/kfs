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
package org.kuali.kfs.module.endow.document;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface UnitsTotaling {
    
    /**
     * This method returns the total units/shares for the document.
     * 
     * @return The total units/shares as a KualiDecimal object instance.
     */
    // what object it should return?
    public KualiDecimal getTotalUnits();
    
}
