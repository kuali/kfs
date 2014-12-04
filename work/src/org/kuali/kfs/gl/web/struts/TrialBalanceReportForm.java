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
package org.kuali.kfs.gl.web.struts;

import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * ASR-1212: This class is the action form for Trial Balance Reports.
 */
public class TrialBalanceReportForm extends LookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TrialBalanceReportForm.class);

    private Lookupable trialBalanceLookupable;
    private String message;


    /**
     * @param pendingEntryLookupable
     */
    public void setTrialBalanceLookupable(Lookupable pendingEntryLookupable) {
        this.trialBalanceLookupable = pendingEntryLookupable;
    }


    /**
     * @return Returns the pendingEntryLookupable.
     */
    public Lookupable getTrialBalanceLookupable() {
        return this.trialBalanceLookupable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
