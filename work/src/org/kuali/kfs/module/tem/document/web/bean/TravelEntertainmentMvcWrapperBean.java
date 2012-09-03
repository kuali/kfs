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
 * limitations under the License.
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
