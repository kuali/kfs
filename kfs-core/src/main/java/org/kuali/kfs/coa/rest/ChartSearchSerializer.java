package org.kuali.kfs.coa.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import javafx.beans.property.ObjectProperty;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.IOException;

public class ChartSearchSerializer extends JsonSerializer<Chart> {
    private DataDictionaryService dataDictionaryService;

    public ChartSearchSerializer() {}

    public ChartSearchSerializer(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @Override
    public void serialize(Chart value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        final BusinessObjectEntry entry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        for (String resultFieldName : entry.getLookupDefinition().getResultFieldNames()) {
            final Object resultFieldValue = ObjectPropertyUtils.getPropertyValue(value, resultFieldName);
            if (!ObjectUtils.isNull(resultFieldValue)) {
                final Class<?> resultFieldType = ObjectPropertyUtils.getPropertyType(value, resultFieldName);
                if (resultFieldType.isAssignableFrom(Number.class)) {
                    gen.writeNumberField(resultFieldName, ((Number) resultFieldValue).doubleValue());
                } else {
                    gen.writeStringField(resultFieldName, resultFieldValue.toString());
                }
            }
        }
        gen.writeEndObject();
    }

    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }
}
