package org.kuali.kfs.sys.document.workflow;

import org.junit.Test;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@ConfigureContext
public class FinancialSystemSearchableAttributeTest extends KualiTestBase {

    @Test
    public void testGetSearchingRowsDoesNotContainAppDocStatus() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        List<Row> rows = financialSystemSearchableAttribute.getSearchingRows("KFST");

        Field appDocStatusField = FieldUtils.getPropertyField(FinancialSystemDocumentHeader.class, KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, true);
        appDocStatusField.setFieldDataType(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_STRING);
        appDocStatusField.setColumnVisible(false);
        assertFalse("searching rows did contain appDocStatus when none was expected", containsField(rows, appDocStatusField));
    }

    private boolean containsField(List<Row> rows, Field appDocStatusField) {
        for (Row row: rows) {
            for (Field field: row.getFields()) {
                if (field.toString().equals(appDocStatusField.toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Test
    public void testGetSearchingRowsContainsAppDocStatus() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        List<Row> rows = financialSystemSearchableAttribute.getSearchingRows("IB");

        Field appDocStatusField = FieldUtils.getPropertyField(FinancialSystemDocumentHeader.class, KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, true);
        appDocStatusField.setFieldDataType(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_STRING);
        appDocStatusField.setColumnVisible(false);
        assertTrue("searching rows did not contain appDocStatus as expected", containsField(rows, appDocStatusField));
    }

}