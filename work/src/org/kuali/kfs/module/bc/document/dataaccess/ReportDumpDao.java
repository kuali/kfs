/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.dataaccess;

/**
 * Populates dump tables.
 */
public interface ReportDumpDao {
    
    /**
     * Rebuilds the account dump maintenance table.
     * 
     * @param principalId - current user who is running the dump
     */
    public void updateAccountDump(String principalName);
    
    /**
     * Cleans the account dump maintenance table for given user
     * 
     * @param principalId - current user who is running the dump
     */
    public void cleanAccountDump(String principalName);
    
}

