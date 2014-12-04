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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Value finder class for Dunning Letter Template.
 */
public class DunningLetterTemplateValuesFinder extends KeyValuesBase {
    protected static volatile DunningLetterService dunningLetterService;

    protected List<KeyValue> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        List<DunningLetterTemplate> boList = (List<DunningLetterTemplate>) SpringContext.getBean(BusinessObjectService.class).findAll(DunningLetterTemplate.class);
        for (DunningLetterTemplate element : boList) {
            if (!element.isRestrictUseByChartOrg() && element.isActive()) {
                keyValues.add(new ConcreteKeyValue(element.getDunningLetterTemplateCode(), element.getDunningLetterTemplateDescription()));
            }
            else {
                if (getDunningLetterService().isValidOrganizationForTemplate(element, currentUser) && element.isActive()) {
                    keyValues.add(new ConcreteKeyValue(element.getDunningLetterTemplateCode(), element.getDunningLetterTemplateDescription()));
                }
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }

    public DunningLetterService getDunningLetterService() {
        if (dunningLetterService == null) {
            dunningLetterService = SpringContext.getBean(DunningLetterService.class);
        }
        return dunningLetterService;
    }
}
