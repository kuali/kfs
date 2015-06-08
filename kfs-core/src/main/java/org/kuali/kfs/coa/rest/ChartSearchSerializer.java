package org.kuali.kfs.coa.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;

public class ChartSearchSerializer extends JsonSerializer<Chart> {
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;

    public ChartSearchSerializer() {}

    public ChartSearchSerializer(DataDictionaryService dataDictionaryService, PersistenceStructureService persistenceStructureService) {
        this.dataDictionaryService = dataDictionaryService;
        this.persistenceStructureService = persistenceStructureService;
    }

    @Override
    public void serialize(Chart value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        final BusinessObjectEntry entry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        entry.getLookupDefinition().getResultFieldNames().stream().forEach(resultFieldName -> {
            final Object resultFieldValue = ObjectPropertyUtils.getPropertyValue(value, resultFieldName);
            if (!ObjectUtils.isNull(resultFieldValue)) {
                try {
                    if (getPersistenceStructureService().hasReference(value.getClass(), resultFieldName)) {
                        writeLinkedValue(gen, value, resultFieldName, resultFieldValue);
                    } else {
                        writeValue(gen, resultFieldName, resultFieldValue);
                    }
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        });
        gen.writeEndObject();
    }

    protected void writeLinkedValue(JsonGenerator gen, Chart value, String resultFieldName, Object resultFieldValue) throws IOException {
        gen.writeFieldName(resultFieldName);
        gen.writeStartObject();
        writeValue(gen, "value", resultFieldValue);
        gen.writeEndObject();
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

    public PersistenceStructureService getPersistenceStructureService() {
        if (persistenceStructureService == null) {
            persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        }
        return persistenceStructureService;
    }
}
