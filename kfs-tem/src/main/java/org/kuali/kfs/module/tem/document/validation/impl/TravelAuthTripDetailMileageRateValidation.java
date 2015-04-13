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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.PER_DIEM_CATEGORIES;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthTripDetailMileageRateValidation extends GenericValidation {

    //@Override
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        boolean showMileage = false;

        TravelDocumentBase document = (TravelDocumentBase) event.getDocument();
        //check to see if mileage=Y in PER_DIEM_CATEGORIES param
        ParameterService paramService = SpringContext.getBean(ParameterService.class);
        Collection<String> perDiemCats = paramService.getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, PER_DIEM_CATEGORIES);
        for (String category : perDiemCats) {
            String[] pair = category.split("=");
            if (pair[0].equalsIgnoreCase(TemConstants.MILEAGE) && pair[1].equalsIgnoreCase(TemConstants.YES)) {
                showMileage = true;
            }
        }
        if (showMileage) {
            for (PerDiemExpense estimate : document.getPerDiemExpenses()) {
                if (StringUtils.isBlank(estimate.getMileageRateExpenseTypeCode())) {
                    GlobalVariables.getMessageMap().putError("document.perDiemExpenses", TemKeyConstants.ERROR_TA_NO_MILEAGE_RATE);
                    rulePassed = false;
                }
            }
        }

        return rulePassed;
    }

}
