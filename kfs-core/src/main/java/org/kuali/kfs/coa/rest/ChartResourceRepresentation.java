package org.kuali.kfs.coa.rest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ChartResourceRepresentation {
    protected DataDictionaryService dataDictionaryService;
    protected ConfigurationService configurationService;

    public ChartResourceRepresentation(DataDictionaryService dataDictionaryService, ConfigurationService configurationService) {
        this.dataDictionaryService = dataDictionaryService;
        this.configurationService = configurationService;
    }

    protected Map<String, Object> collectValue(Map<String, Object> matsya, Chart chart, String resultFieldName, boolean includeTitleAttributeAsLink) {
        final Object resultFieldValue = ObjectPropertyUtils.getPropertyValue(chart, resultFieldName);
        if (!ObjectUtils.isNull(resultFieldValue)) {
            // see KualiInquirableImpl#getInquiryUrl for more info on determining field references....
            final String fieldReference = determineFieldReference(chart, resultFieldName, includeTitleAttributeAsLink);
            if (!StringUtils.isBlank(fieldReference)) {
                collectLinkedValue(matsya, chart, resultFieldName, resultFieldValue, fieldReference);
            } else {
                matsya.put(resultFieldName, resultFieldValue);
            }
        }
        return matsya;
    }

    protected String determineFieldReference(Chart value, String resultFieldName, boolean includeTitleAttributeAsLink) {
        final BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry)dataDictionaryService.getDataDictionary().getBusinessObjectEntry(value.getClass().getName());
        if (!ObjectUtils.isNull(businessObjectEntry) && StringUtils.equals(businessObjectEntry.getTitleAttribute(), resultFieldName)) {
            if (includeTitleAttributeAsLink) {
                return resultFieldName;
            } else {
                return ""; // we're the title attribute, we don't want it to be a link, so...don't return anything
            }
        } else if (ObjectUtils.isNestedAttribute(resultFieldName)) {
            final String nestedReferenceName = ObjectUtils.getNestedAttributePrefix(resultFieldName);
            final Object nestedReferenceObject = ObjectUtils.getNestedValue(value, nestedReferenceName);

            if (ObjectUtils.isNotNull(nestedReferenceObject) && nestedReferenceObject instanceof BusinessObject) {
                return nestedReferenceName;
            }
        } else {
            Map primitiveReference = LookupUtils.getPrimitiveReference(value, resultFieldName);
            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                final String attributeRefName = (String) primitiveReference.keySet().iterator().next();
                return attributeRefName;
            }
        }
        return "";
    }

    protected void collectLinkedValue(Map<String, Object> matsya, Chart value, String resultFieldName, Object resultFieldValue, String referenceAttributeName) {
        Map<String, Object> linkMap = new ConcurrentHashMap<>();
        linkMap.put("value", resultFieldValue);

        final Object attributeReference = (!StringUtils.equals(resultFieldName, referenceAttributeName))
            ? ObjectPropertyUtils.getPropertyValue(value, referenceAttributeName)
            : value;
        if (!ObjectUtils.isNull(attributeReference)) {
            collectLink(linkMap, attributeReference);
        }

        matsya.put(resultFieldName, linkMap);
    }

    protected void collectLink(Map<String, Object> linkMap, Object attributeReference) {
        if (attributeReference instanceof Chart || attributeReference instanceof ObjectCode || attributeReference instanceof Person) {
            String link = "";
            if (attributeReference instanceof Chart) {
                link = ((Chart)attributeReference).generateInquiryUrl();
            } if (attributeReference instanceof ObjectCode) {
                link = ((ObjectCode)attributeReference).generateInquiryUrl();
            } else if (attributeReference instanceof Person) {
                link = buildLink((Person)attributeReference);
            }
            linkMap.put("link", configurationService.getPropertyValueAsString(KFSKeyConstants.KFS_URL) + link);
        }
    }

    protected String buildLink(Person person) {
        // TODO: we should point at inquiry
        return "/inquiry/kim/principal?principalId="+ person.getPrincipalId();
    }
}
