package org.kuali.kfs.sys.document.workflow;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDecimal;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@ConfigureContext
public class FinancialSystemSearchableAttributeTest extends KualiTestBase {

    @Ignore
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

    @Ignore
    @Test
    public void testGetSearchingRowsContainsAppDocStatus() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        List<Row> rows = financialSystemSearchableAttribute.getSearchingRows("IB");

        Field appDocStatusField = FieldUtils.getPropertyField(FinancialSystemDocumentHeader.class, KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, true);
        appDocStatusField.setFieldDataType(KewApiConstants.SearchableAttributeConstants.DATA_TYPE_STRING);
        appDocStatusField.setColumnVisible(false);
        assertTrue("searching rows did not contain appDocStatus as expected", containsField(rows, appDocStatusField));
    }

    @Test
      public void testExtractFinancialSystemDocumentAttributes_nullDocument() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(null);
        assertTrue("Search attributes should be empty", searchAttributes.isEmpty());
    }

    class FakeTotalingDocument extends FinancialSystemTransactionalDocumentBase implements AmountTotaling {
        private final KualiDecimal amount;

        public FakeTotalingDocument(double amount) {
            this.amount = new KualiDecimal(amount);
        }

        @Override
        public KualiDecimal getTotalDollarAmount() {
            return amount;
        }
    }

    class FakeAppDocStatusDocument extends FinancialSystemTransactionalDocumentBase {

        FakeAppDocStatusDocument(String appDocStatus) {
            this.documentHeader = new FinancialSystemDocumentHeader();
            ((FinancialSystemDocumentHeader)this.documentHeader).setApplicationDocumentStatus(appDocStatus);
       }

    }

    @Test
    public void testExtractFinancialSystemDocumentAttributes_amountTotalling() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        final FakeTotalingDocument doc = new FakeTotalingDocument(27.5);
        List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(doc);
        assertEquals("Search attributes should have one attribute", 1, searchAttributes.size());
        assertDocumentAttributeDecimal(searchAttributes.get(0), 27.5);
    }

    protected void assertDocumentAttributeDecimal(DocumentAttribute documentAttribute, double decimalValue) {
        assertEquals("Search attribute should be of type DocumentAttributeDecimal", DocumentAttributeDecimal.class, documentAttribute.getClass());
        assertEquals("Search attribute should have expected decimal value", new BigDecimal(decimalValue).setScale(2, RoundingMode.HALF_UP), (((DocumentAttributeDecimal) documentAttribute).getValue()));
    }

    @Test
    public void testExtractFinancialSystemDocumentAttributes_accountingAttributes() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        final DistributionOfIncomeAndExpenseDocument diDoc = new DistributionOfIncomeAndExpenseDocument();
        try {
            diDoc.addSourceAccountingLine(AccountingLineFixture.LINE2.createSourceAccountingLine());
            diDoc.addTargetAccountingLine(AccountingLineFixture.LINE3.createTargetAccountingLine());
            List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(diDoc);
            assertEquals("Search attributes should have one attribute", 7, searchAttributes.size());

            assertDocumentAttributeDecimal(searchAttributes.get(0), 1.1);
            // TODO CONTINUE ASSERTIONS
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testExtractFinancialSystemDocumentAttributes_appDocStatus() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        final FakeAppDocStatusDocument doc = new FakeAppDocStatusDocument("In Process");
        List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(doc);
        assertEquals("Search attributes should have one attribute", 1, searchAttributes.size());
        assertEquals("Search attributes should have appDocStatus In Process", "In Process", searchAttributes.get(0).getValue().toString());

    }


}