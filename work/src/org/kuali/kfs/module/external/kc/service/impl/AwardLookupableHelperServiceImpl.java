package org.kuali.kfs.module.external.kc.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class AwardLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

	private static final long serialVersionUID = 5276003184255639709L;

	private AwardServiceImpl awardService;

	@Override
	public List<? extends BusinessObject> getSearchResults(
			Map<String, String> fieldValues) {
		return getAwardService().getSearchResults(fieldValues);
	}

    public AwardServiceImpl getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardServiceImpl awardService) {
        this.awardService = awardService;
    }

}
