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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.external.kc.service.ExternalizableLookupableBusinessObjectService;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class ExternalizableLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

	private static final long serialVersionUID = 5276003184255639709L;

	private ExternalizableLookupableBusinessObjectService eboLookupableService;

	@Override
	public List<? extends BusinessObject> getSearchResults(
			Map<String, String> fieldValues) {
		return getEboLookupableService().getSearchResults(fieldValues);
	}

    public ExternalizableLookupableBusinessObjectService getEboLookupableService() {
        return eboLookupableService;
    }

    public void setEboLookupableService(ExternalizableLookupableBusinessObjectService eboLookupableService) {
        this.eboLookupableService = eboLookupableService;
    }


}
