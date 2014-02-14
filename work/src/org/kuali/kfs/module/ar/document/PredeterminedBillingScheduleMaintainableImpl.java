/*
 * Copyright 2011 The Kuali Foundation.
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
 * limitations under the License. awardLookupable
 */
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT;
import static org.kuali.kfs.sys.KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.validation.impl.PredeterminedBillingScheduleRuleUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Methods for the Predetermined Billing Schedule maintenance document UI.
 */
public class PredeterminedBillingScheduleMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Constructs an MilestoneScheduleMaintainableImpl.
     */
    public PredeterminedBillingScheduleMaintainableImpl() {
        super();
    }

    /**
     * Constructs a MilestoneScheduleMaintainableImpl.
     *
     * @param award
     */
    public PredeterminedBillingScheduleMaintainableImpl(PredeterminedBillingSchedule predeterminedBillingSchedule) {
        super(predeterminedBillingSchedule);
        this.setBoClass(predeterminedBillingSchedule.getClass());
    }

    /**
     * This method is called to check if the award already has bills set, and to validate on refresh
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */

    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        if (StringUtils.equals("awardLookupable", (String) fieldValues.get(KFSConstants.REFRESH_CALLER))) {

            boolean isMilestonesExist = PredeterminedBillingScheduleRuleUtil.checkIfBillsExist(getPredeterminedBillingSchedule());
            if (isMilestonesExist) {
                String pathToMaintainable = DOCUMENT + "." + NEW_MAINTAINABLE_OBJECT;
                GlobalVariables.getMessageMap().addToErrorPath(pathToMaintainable);
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PREDETERMINED_BILLING_SCHEDULE_EXISTS, new String[] { getPredeterminedBillingSchedule().getProposalNumber().toString() });
                GlobalVariables.getMessageMap().removeFromErrorPath(pathToMaintainable);

            }
        }
        else {
            super.refresh(refreshCaller, fieldValues, document);
        }
    }


    /**
     * This method is called for refreshing the Agency before display to show the full name in case the agency number was changed by
     * hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        PredeterminedBillingSchedule predeterminedBillingSchedule = getPredeterminedBillingSchedule();
        predeterminedBillingSchedule.refreshNonUpdateableReferences();
        super.processAfterRetrieve();
    }


    /**
     * Gets the underlying Milestone Schedule.
     *
     * @return
     */
    public PredeterminedBillingSchedule getPredeterminedBillingSchedule() {

        return (PredeterminedBillingSchedule) getBusinessObject();
    }

}
