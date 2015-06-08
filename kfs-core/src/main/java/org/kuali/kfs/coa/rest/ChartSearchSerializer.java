package org.kuali.kfs.coa.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;

public class ChartSearchSerializer extends JsonSerializer<Chart> {
    private DataDictionaryService dataDictionaryService;
    private ConfigurationService configurationService;

    public ChartSearchSerializer() {}

    public ChartSearchSerializer(DataDictionaryService dataDictionaryService, ConfigurationService configurationService) {
        this.dataDictionaryService = dataDictionaryService;
        this.configurationService = configurationService;
    }

    @Override
    public void serialize(Chart value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        final BusinessObjectEntry entry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        entry.getLookupDefinition().getResultFieldNames().stream().forEach(resultFieldName -> {
            writeValuesForLookupResult(value, gen, resultFieldName);
        });
        gen.writeEndObject();
    }

    protected void writeValuesForLookupResult(Chart value, JsonGenerator gen, String resultFieldName) {
        final Object resultFieldValue = ObjectPropertyUtils.getPropertyValue(value, resultFieldName);
        if (!ObjectUtils.isNull(resultFieldValue)) {
            try {
                // see KualiInquirableImpl#getInquiryUrl for more info on determining field references....
                final String fieldReference = determineFieldReference(value, resultFieldName);
                if (!StringUtils.isBlank(fieldReference)) {
                    writeLinkedValue(gen, value, resultFieldName, resultFieldValue, fieldReference);
                } else {
                    writeValue(gen, resultFieldName, resultFieldValue);
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    protected String determineFieldReference(Chart value, String resultFieldName) {
        if (ObjectUtils.isNestedAttribute(resultFieldName)) {
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

    protected void writeLinkedValue(JsonGenerator gen, Chart value, String resultFieldName, Object resultFieldValue, String referenceAttributeName) throws IOException {
        gen.writeFieldName(resultFieldName);
        gen.writeStartObject();
        writeValue(gen, "value", resultFieldValue);

        final Object attributeReference = ObjectPropertyUtils.getPropertyValue(value, referenceAttributeName);
        if (!ObjectUtils.isNull(attributeReference)) {
            writeLink(gen, attributeReference);
        }

        gen.writeEndObject();
    }

    protected void writeLink(JsonGenerator gen, Object attributeReference) throws IOException {
        if (attributeReference instanceof ObjectCode || attributeReference instanceof Person) {
            String link = "";
            if (attributeReference instanceof ObjectCode) {
                link = buildLink((ObjectCode)attributeReference);
            } else if (attributeReference instanceof Person) {
                link = buildLink((Person)attributeReference);
            }
            gen.writeStringField("link", getConfigurationService().getPropertyValueAsString(KFSKeyConstants.KFS_URL) + link);
        }
    }

    protected String buildLink(ObjectCode attributeReference) {
        // TODO: we should point at inquiry
        return "/lookup/coa/object?chartCode=" + attributeReference.getChartOfAccountsCode() + "&objectCode=" + attributeReference.getFinancialObjectCode();
    }

    protected String buildLink(Person person) {
        // TODO: we should point at inquiry
        return "/lookup/kim/principal?principaId="+ person.getPrincipalId();
    }

    protected void writeValue(JsonGenerator gen, String propertyName, Object value) throws IOException {
        if (value.getClass().isAssignableFrom(Number.class)) {
            gen.writeNumberField(propertyName, ((Number) value).doubleValue());
        } else {
            gen.writeStringField(propertyName, value.toString());
        }
    }

    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
}
