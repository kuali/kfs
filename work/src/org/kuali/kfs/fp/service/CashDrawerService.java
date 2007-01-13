/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import org.kuali.module.financial.bo.CashDrawer;


/**
 * This interface defines methods that a CashDrawer service implementation must provide.
 * 
 * 
 */
public interface CashDrawerService {
    /**
     * Closes the CashDrawer instance associated with the given workgroupName, creating one if necessary.
     */
    public void closeCashDrawer(String workgroupName);

    /**
     * Opens the CashDrawer instance associated with the given workgroupName, creating one if necessary. Records the given
     * documentId as the document which opened the cashdrawer.
     */
    public void openCashDrawer(String workgroupName, String documentId);

    /**
     * Locks the currently-open CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException if
     * that cashDrawer is not open (i.e. is closed or locked). Records the given documentId as the document which locked the
     * cashDrawer.
     */
    public void lockCashDrawer(String workgroupName, String documentId);

    /**
     * Unlocks the currently-locked CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException
     * if that cashDrawer is not locked (i.e. is closed or open). Records the given documentId as the document which unlocked the
     * cashDrawer.
     */
    public void unlockCashDrawer(String workgroupName, String documentId);


    /**
     * Retrieves the CashDrawer instance associated with the given workgroupName, if any. If autocreate is true, and no CashDrawer
     * for the given workgroupName exists, getByWorkgroupName will return a newly-created (non-persisted) CashDrawer instance.
     * 
     * @param workgroupName
     * @param autocreate
     * @return CashDrawer instance or null
     */
    public CashDrawer getByWorkgroupName(String workgroupName, boolean autocreate);
}