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
package org.kuali.kfs.module.tem.document.validation.event;

import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;


public class AssignDistributionAccountingLinesEvent extends AttributedDocumentEventBase {
    private TravelMvcWrapperBean travelForm;

    /**
     * Constructs an AssignDistributionAccountingLinesEvent with the given errorPathPrefix, document, and otherExpense.
     *
     * @param errorPathPrefix
     * @param document
     * @param groupTraveler
     */
    public AssignDistributionAccountingLinesEvent(String errorPathPrefix, Document document, TravelMvcWrapperBean travelForm) {
        super("distributing to source accounting lines " + getDocumentId(document), errorPathPrefix, document);
        this.travelForm = travelForm;
    }

    public TravelMvcWrapperBean getTravelForm(){
        return travelForm;
    }
}
