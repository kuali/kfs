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
        final ChartSearch chartSearch = new ChartSearch(lookupService);
        return Arrays.asList(representChartForLookup(chartSearch.retrieveByChartCode(chartOfAccountsCode), dataDictionaryService, persistenceStructureService, configurationService));
    }

    protected Map<String, Object> representChartForLookup(Chart chart, DataDictionaryService dataDictionaryService, PersistenceStructureService persistenceStructureService, ConfigurationService configurationService) {
        final BusinessObjectEntry entry = (BusinessObjectEntry)dataDictionaryService.getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        Map<String, Object> identity = new ConcurrentHashMap<>();
        return entry.getLookupDefinition().getResultFieldNames().stream().reduce(identity,
                (matsya, resultFieldName) -> {
                    return collectValueForLookupResult(matsya, chart, resultFieldName, persistenceStructureService, configurationService);
                },
                (matsya, resultFieldName) -> {
                    return matsya;
                });
    }

    protected Map<String, Object> collectValueForLookupResult(Map<String, Object> matsya, Chart chart, String resultFieldName, PersistenceStructureService persistenceStructureService, ConfigurationService configurationService) {
        final Object resultFieldValue = ObjectPropertyUtils.getPropertyValue(chart, resultFieldName);
        if (!ObjectUtils.isNull(resultFieldValue)) {
            // see KualiInquirableImpl#getInquiryUrl for more info on determining field references....
            final String fieldReference = determineFieldReference(chart, resultFieldName, persistenceStructureService);
            if (!StringUtils.isBlank(fieldReference)) {
                collectLinkedValue(matsya, chart, resultFieldName, resultFieldValue, fieldReference, configurationService);
            } else {
                matsya.put(resultFieldName, resultFieldValue);
            }
        }
        return matsya;
    }

    protected String determineFieldReference(Chart value, String resultFieldName, PersistenceStructureService persistenceStructureService) {
        if (ObjectUtils.isNestedAttribute(resultFieldName)) {
            final String nestedReferenceName = ObjectUtils.getNestedAttributePrefix(resultFieldName);
            final Object nestedReferenceObject = ObjectUtils.getNestedValue(value, nestedReferenceName);

            if (ObjectUtils.isNotNull(nestedReferenceObject) && nestedReferenceObject instanceof BusinessObject) {
                return nestedReferenceName;
            }
        } else if (!isPrimaryKey(value, resultFieldName, persistenceStructureService)) {
            Map primitiveReference = LookupUtils.getPrimitiveReference(value, resultFieldName);
            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                final String attributeRefName = (String) primitiveReference.keySet().iterator().next();
                return attributeRefName;
            }
        }
        return "";
    }

    protected boolean isPrimaryKey(Chart value, String resultFieldName, PersistenceStructureService persistenceStructureService) {
        final List<String> primaryKeyFields = persistenceStructureService.getPrimaryKeys(value.getClass());
        return primaryKeyFields.contains(resultFieldName);
    }

    protected void collectLinkedValue(Map<String, Object> matsya, Chart value, String resultFieldName, Object resultFieldValue, String referenceAttributeName, ConfigurationService configurationService) {
        Map<String, Object> linkMap = new ConcurrentHashMap<>();
        linkMap.put("value", resultFieldValue);

        final Object attributeReference = ObjectPropertyUtils.getPropertyValue(value, referenceAttributeName);
        if (!ObjectUtils.isNull(attributeReference)) {
            collectLink(linkMap, attributeReference, configurationService);
        }

        matsya.put(resultFieldName, linkMap);
    }

    protected void collectLink(Map<String, Object> linkMap, Object attributeReference, ConfigurationService configurationService) {
        if (attributeReference instanceof ObjectCode || attributeReference instanceof Person) {
            String link = "";
            if (attributeReference instanceof ObjectCode) {
                link = buildLink((ObjectCode)attributeReference);
            } else if (attributeReference instanceof Person) {
                link = buildLink((Person)attributeReference);
            }
            linkMap.put("link", configurationService.getPropertyValueAsString(KFSKeyConstants.KFS_URL) + link);
        }
    }

    protected String buildLink(ObjectCode attributeReference) {
        // TODO: we should point at inquiry
        return "/lookup/coa/object?chartCode=" + attributeReference.getChartOfAccountsCode() + "&objectCode=" + attributeReference.getFinancialObjectCode();
    }

    protected String buildLink(Person person) {
        // TODO: we should point at inquiry
        return "/lookup/kim/principal?principalId="+ person.getPrincipalId();
    }
}
