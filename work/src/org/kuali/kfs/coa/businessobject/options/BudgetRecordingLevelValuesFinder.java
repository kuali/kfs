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
package org.kuali.kfs.coa.businessobject.options;

import org.kuali.kfs.coa.businessobject.BudgetRecordingLevel;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link BudgetRecordingLevel}s)
 */
public class BudgetRecordingLevelValuesFinder extends KualiSystemCodeValuesFinder {

    /**
     * This method is called by the {@link KualiSystemCodeValuesFinder} that creates a list out of the code and the code and
     * description from the class
     * 
     * @see org.kuali.kfs.coa.businessobject.options.KualiSystemCodeValuesFinder#getValuesClass()
     */
    protected Class getValuesClass() {
        return BudgetRecordingLevel.class;
    }

}
