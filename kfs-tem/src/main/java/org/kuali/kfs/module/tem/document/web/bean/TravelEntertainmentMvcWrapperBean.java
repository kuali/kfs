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
package org.kuali.kfs.module.tem.document.web.bean;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;

/**
 * This class...
 */
public interface TravelEntertainmentMvcWrapperBean extends TravelMvcWrapperBean {

    Attendee getNewAttendeeLine();

    void setNewAttendeeLines(final List<Attendee> newAttendeeLines);

    List<Attendee> getNewAttendeeLines();

    void setNewAttendeeLine(final Attendee newAttendeeLine);

    @Override
    ActualExpense getNewActualExpenseLine();

    @Override
    void setNewActualExpenseLines(final List<ActualExpense> newActualExpenseLines);

    @Override
    List<ActualExpense> getNewActualExpenseLines();

    @Override
    void setNewActualExpenseLine(final ActualExpense newActualExpenseLine);


}
