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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;

public class RemoveAttendeeEvent implements Observer {
    
    public static Logger LOG = Logger.getLogger(RemoveAttendeeEvent.class);
    
    private static final int WRAPPER_ARG_IDX = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;

    @Override
    public void update(final Observable observable, Object arg) {
        if (!(arg instanceof Object[])) {
            return;
        }
        
        final Object[] args = (Object[]) arg;
        LOG.debug(args[WRAPPER_ARG_IDX]);
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
