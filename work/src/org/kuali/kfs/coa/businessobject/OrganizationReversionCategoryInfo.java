/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

/**
 * Methods required by the organization reversion process to determine how to build origin entries
 * for the parent organization reversion
 */
public interface OrganizationReversionCategoryInfo {
    /**
     * Gets the organizationReversionObjectCode attribute.
     * 
     * @return Returns the organizationReversionObjectCode
     */
    public abstract String getOrganizationReversionObjectCode();
    
    /**
     * Gets the organizationReversionCode attribute.
     * 
     * @return Returns the organizationReversionCode
     */
    public abstract String getOrganizationReversionCode();
}
