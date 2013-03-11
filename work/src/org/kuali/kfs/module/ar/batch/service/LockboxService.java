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
package org.kuali.kfs.module.ar.batch.service;

import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.rice.kew.api.exception.WorkflowException;

public interface LockboxService {

    public boolean processLockboxes() throws WorkflowException;

    public void processLockbox(Lockbox lockbox, com.lowagie.text.Document pdfdoc);


    /**
     *
     * Returns the highest (numerically) value for the Lockbox
     * invoiceSequenceNumber.
     *
     * @return The max Lockbox.invoiceSequenceNumber
     */
    public Long getMaxLockboxSequenceNumber();

}
