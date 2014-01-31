/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Calendar;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Prevents records created in the past from being edited
 */
public class PerDiemMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    protected volatile static DateTimeService dateTimeService;

    /**
     *
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#canMaintain(java.lang.Object)
     */
    @Override
    public boolean canMaintain(Object dataObject) {
        if (dataObject instanceof PerDiem) {
            final PerDiem perDiem = (PerDiem)dataObject;
            if (perDiem.getEffectiveToDate() != null) {
                Calendar now = Calendar.getInstance();
                now.setTimeInMillis(KfsDateUtils.clearTimeFields(getDateTimeService().getCurrentDate()).getTime());
                Calendar perDiemEndDate = Calendar.getInstance();
                perDiemEndDate.setTimeInMillis(KfsDateUtils.clearTimeFields(perDiem.getEffectiveToDate()).getTime());
                if (perDiemEndDate.before(now)) {
                    return false;
                }
            }
        }
        return super.canMaintain(dataObject);
    }

    protected DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
}
