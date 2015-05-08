package org.kuali.kfs.sys.document.workflow;

import org.junit.Test;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.*;
import org.kuali.kfs.sys.document.*;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.*;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//@ConfigureContext
public class FinancialSystemSearchableAttributeTest {//extends KualiTestBase {

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

    @Test
      public void testExtractFinancialSystemDocumentAttributes_nullDocument() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(null);
        assertTrue("Search attributes should be empty", searchAttributes.isEmpty());
    }

    @Test
    public void testExtractFinancialSystemDocumentAttributes_amountTotalling() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        final FakeTotalingDocument doc = new FakeTotalingDocument() {
            @Override
            public KualiDecimal getTotalDollarAmount() {
                return new KualiDecimal(27.5);
            }
        };
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
        final FakeDistributionOfIncomeAndExpenseDocument diDoc = new FakeDistributionOfIncomeAndExpenseDocument();
            diDoc.addSourceAccountingLine(createSourceLine());
            diDoc.addTargetAccountingLine(createTargetLine());
            List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(diDoc);
            assertEquals("Search attributes should have 4 attribute (chart and account for source and target)", 4, searchAttributes.size());

//            assertDocumentAttributeDecimal(searchAttributes.get(0), 1.1);
            // TODO CONTINUE ASSERTIONS

    }

    private SourceAccountingLine createSourceLine()  {
      SourceAccountingLine sal = new SourceAccountingLine() {
          @Override
          public void setAccountNumber(String accountNumber) {
              this.accountNumber = accountNumber;
          }
      };
        sal.setAccountNumber("1031400");
        sal.setAmount(new KualiDecimal(1.10));
        sal.setChartOfAccountsCode("BL");
        return sal;
    }

    private TargetAccountingLine createTargetLine()  {
        TargetAccountingLine tal = new TargetAccountingLine() {
            @Override
            public void setAccountNumber(String accountNumber) {
                this.accountNumber = accountNumber;
            }
        };
        tal.setAccountNumber("6044900");
        tal.setAmount(new KualiDecimal(1.10));
        tal.setChartOfAccountsCode("BA");
        return tal;
    }


    @Test
    public void testExtractFinancialSystemDocumentAttributes_appDocStatus() {
        FinancialSystemSearchableAttribute financialSystemSearchableAttribute = new FinancialSystemSearchableAttribute();
        final FakeFinancialSystemTransactionalDocument doc = new FakeFinancialSystemTransactionalDocument() {
            @Override
            public String getApplicationDocumentStatus() {
                return "In Process";
            }
        };
        List<DocumentAttribute> searchAttributes = financialSystemSearchableAttribute.extractFinancialSystemDocumentAttributes(doc);
        assertEquals("Search attributes should have one attribute", 1, searchAttributes.size());
        assertEquals("Search attributes should have appDocStatus In Process", "In Process", searchAttributes.get(0).getValue().toString());

    }


}