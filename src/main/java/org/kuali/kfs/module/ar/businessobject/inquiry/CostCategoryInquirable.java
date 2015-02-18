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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;

/**
 * Override of base financial system inquirable to have cost category inquiries hide inactive items in child object collections
 */
public class CostCategoryInquirable extends KfsInquirableImpl {

    /**
     * overridden to add child object collection names to inactiveRecordDisplay
     */
    public CostCategoryInquirable() {
        super();
        initiateInactiveRecordDisplay();
    }

    /**
     * By default, the children record collections will hide inactive records
     */
    protected void initiateInactiveRecordDisplay() {
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CODES, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_LEVELS, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CONSOLIDATIONS, Boolean.FALSE);
    }
}
