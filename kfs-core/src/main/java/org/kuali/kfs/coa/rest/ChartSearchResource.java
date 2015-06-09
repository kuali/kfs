package org.kuali.kfs.coa.rest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.swing.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("coa/chart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChartSearchResource {

    @GET
    public List<Map<String, Object>> lookupChartByPrimaryKey(@QueryParam("code") String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        final ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService, dataDictionaryService, persistenceStructureService, configurationService);
        return Arrays.asList(chartSearch.retrieveByChartCode(chartOfAccountsCode));
    }


}
