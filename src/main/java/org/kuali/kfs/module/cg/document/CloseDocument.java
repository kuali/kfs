/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.document;

import java.sql.Date;

import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;

/**
 * Instances of CloseDocument indicate dates on which the {@link CloseBatchStep} should be executed.
 */
public class CloseDocument extends FinancialSystemTransactionalDocumentBase {

    protected Date userInitiatedCloseDate;
    protected Date closeOnOrBeforeDate;

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
