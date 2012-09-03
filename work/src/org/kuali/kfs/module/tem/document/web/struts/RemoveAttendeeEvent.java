/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;

public class RemoveAttendeeEvent implements Observer {
    private static final int WRAPPER_ARG_IDX = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;

    @Override
    public void update(final Observable observable, Object arg) {
        if (!(arg instanceof Object[])) {
            return;
        }
        
        final Object[] args = (Object[]) arg;
        debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelEntertainmentMvcWrapperBean)) {
            return;
        }
        
        final TravelEntertainmentMvcWrapperBean wrapper = (TravelEntertainmentMvcWrapperBean) args[WRAPPER_ARG_IDX];
        final TravelEntertainmentDocument document = (TravelEntertainmentDocument) wrapper.getTravelDocument();
        final Integer deleteIndex = (Integer) args[SELECTED_LINE_ARG_IDX];

        Attendee line = document.getAttendee().get(deleteIndex);
        document.removeAttendee(deleteIndex);

        List<Attendee> attendeeLines = wrapper.getNewAttendeeLines();
        wrapper.setNewAttendeeLines(attendeeLines);
    }
}