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
package org.kuali.module.integration.bo;

/**
 * An interface that declares methods to retrieve information about Restricted Materials.
 */
public interface PurchasingAccountsPayableRestrictedMaterial {

    /**
     * Gets the code for this restricted material
     * @return the code for this restricted material
     */
    public String getRestrictedMaterialCode();
    
    /**
     * Gets the description for this restricted material
     * @return the description for this restricted material
     */
    public String getRestrictedMaterialDescription();
    
    /**
     * Gets the default description for this restricted material
     * @return the default description for this restricted material
     */
    public String getRestrictedMaterialDefaultDescription();
}
