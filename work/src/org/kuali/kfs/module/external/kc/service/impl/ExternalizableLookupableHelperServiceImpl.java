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
