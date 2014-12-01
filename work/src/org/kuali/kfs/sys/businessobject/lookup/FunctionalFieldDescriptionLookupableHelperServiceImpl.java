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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class FunctionalFieldDescriptionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final List<String> SORT_PROPERTIES = new ArrayList<String>();
    static {
        SORT_PROPERTIES.add(KFSPropertyConstants.NAMESPACE_CODE);
        SORT_PROPERTIES.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        SORT_PROPERTIES.add(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL);
    }
    protected KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<FunctionalFieldDescription> searchResults = kfsBusinessObjectMetaDataService.findFunctionalFieldDescriptions(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL), fieldValues.get(KFSPropertyConstants.DESCRIPTION), fieldValues.get(KFSPropertyConstants.ACTIVE));
        Collections.sort(searchResults, new BeanPropertyComparator(getSortProperties(), true));
        return searchResults;
    }

    protected List<String> getSortProperties() {
        return SORT_PROPERTIES;
    }

    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
