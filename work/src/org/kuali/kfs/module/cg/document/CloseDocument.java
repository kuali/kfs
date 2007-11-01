/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.document;

import java.sql.Date;

import org.kuali.core.document.TransactionalDocumentBase;

/**
 * Instances of CloseDocument indicate dates on which the {@link CloseBatchStep} should be executed.
 */
public class CloseDocument extends TransactionalDocumentBase {

    private Date userInitiatedCloseDate;
    private Date closeOnOrBeforeDate;

    /**
     * The {@link CloseBatchStep} will close out {@link Proposal}s and {@link Award}s not yet closed and created before the date
     * returned from this method.
     * 
     * @return the date to use for comparison. See method description.
     */
    public Date getUserInitiatedCloseDate() {
        return userInitiatedCloseDate;
    }

    /**
     * The {@link CloseBatchStep} will close out {@link Proposal}s and {@link Award}s not yet closed and created before the date
     * passed into this method.
     * 
     * @param closeOnOrBeforeDate the date to use for comparison. See method description.
     */
    public void setUserInitiatedCloseDate(Date userInitiatedCloseDate) {
        this.userInitiatedCloseDate = userInitiatedCloseDate;
    }

    /**
     * Gets the date on which this instance should trigger the CloseBatchStep to close out {@link {Proposal}s and {@link Award}s.
     * 
     * @return the date on which this instance should trigger the CloseBatchStep to close out {@link {Proposal}s and {@link Award}s.
     */
    public Date getCloseOnOrBeforeDate() {
        return closeOnOrBeforeDate;
    }

    /**
     * Sets the date on which this instance should trigger the CloseBatchStep to close out {@link {Proposal}s and {@link Award}s.
     * 
     * @param userInitiatedCloseDate the date on which this instance should trigger the CloseBatchStep to close out
     *        {@link {Proposal}s and {@link Award}s.
     */
    public void setCloseOnOrBeforeDate(Date closeOnOrBeforeDate) {
        this.closeOnOrBeforeDate = closeOnOrBeforeDate;
    }

}
