/*
 * Copyright 2006-2008 The Kuali Foundation
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
