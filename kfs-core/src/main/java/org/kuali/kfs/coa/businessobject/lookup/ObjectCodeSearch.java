package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.LookupService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCodeSearch {

    protected LookupService lookupService;

    public ObjectCodeSearch(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    public ObjectCode retrieveByPK(Integer fiscalYear, String chartCode, String objectCode) {
        ObjectCode objCode = findObjectCode(fiscalYear, chartCode, objectCode);
        return objCode;
    }

    protected ObjectCode findObjectCode(Integer fiscalYear, String chartCode, String objectCode) {
        Map<String, String> searchProperties = new ConcurrentHashMap<>();
        searchProperties.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear+"");
        searchProperties.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        searchProperties.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        Collection objectCodes = lookupService.findCollectionBySearch(ObjectCode.class, searchProperties);
        return (ObjectCode) objectCodes.iterator().next();
    }

}
