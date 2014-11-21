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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list, ready for populating a drop down select control, of "systems" that can
 * be used by the GLCP
 */
public class CorrectionChooseSystemValuesFinder extends KeyValuesBase {

    /**
     * Returns the list of data sources that can feed data to the GLCP
     * 
     * @return a List of data sources
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue("", "Select System"));
        activeLabels.add(new ConcreteKeyValue(CorrectionDocumentService.SYSTEM_DATABASE, "Database"));
        activeLabels.add(new ConcreteKeyValue(CorrectionDocumentService.SYSTEM_UPLOAD, "File Upload"));

        return activeLabels;
    }

}
