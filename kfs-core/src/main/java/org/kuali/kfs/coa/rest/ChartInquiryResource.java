package org.kuali.kfs.coa.rest;

import io.swagger.annotations.Api;
import org.kuali.kfs.coa.businessobject.inquiry.ChartInquiry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Api(value = "coa/chart")
@Path("coa/chart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChartInquiryResource {
    @GET
    public Map<String, Object> getChartInquiryByPrimaryKey(@QueryParam("code") String chartOfAccountsCode) {
        final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        final DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
        final ChartInquiry chartInquiry = new ChartInquiry(businessObjectService, dataDictionaryService, configurationService);

        final Map<String, Object> representation = chartInquiry.retrieveByChartCode(chartOfAccountsCode);
        return representation;
    }
}
