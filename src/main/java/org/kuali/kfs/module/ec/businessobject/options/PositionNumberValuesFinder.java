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
package org.kuali.kfs.module.ec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.web.struts.CertificationReportForm;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class...
 */
public class PositionNumberValuesFinder extends KeyValuesBase {
    private List keyValues;

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        CertificationReportForm form = (CertificationReportForm) KNSGlobalVariables.getKualiForm();
        EffortCertificationDocument document = (EffortCertificationDocument)form.getDocument();
        List keyValues = new ArrayList();
        List<String> positionNumberList = document.getPositionList();
        for (String positionNumber : positionNumberList) {
            keyValues.add(new ConcreteKeyValue(positionNumber, positionNumber));
        }
        
        return keyValues;
    }

}
