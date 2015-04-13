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
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;

/**
 * define the service methods that are related to budget construction appointment funding reason code
 * 
 * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode
 */
public interface BudgetConstructionAppointmentFundingReasonCodeService {

    /**
     * retrieve an Appointment Funding Reason Code object by its primary key - the reason code.
     * 
     * @param reasonCode the given reason code
     * @return an Appointment Funding Reason Code object retrived by its primary key
     */
    public BudgetConstructionAppointmentFundingReasonCode getByPrimaryId(String reasonCode);
}
