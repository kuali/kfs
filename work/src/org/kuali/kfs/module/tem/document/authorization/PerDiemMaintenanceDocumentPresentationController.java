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
