package org.kuali.kfs.fp.document.web.struts;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAccountingLinesDocumentBase;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CapitalAssetInformationActionBaseTest extends KualiTestBase {
    protected Logger LOG = Logger.getLogger(CapitalAssetInformationActionBaseTest.class);
    protected CapitalAssetInformationActionBase capitalAssetInformationActionBase;

    @Override
    @ConfigureContext(session = khuntley)
    public void setUp() throws Exception {
        super.setUp();
        this.capitalAssetInformationActionBase = new DistributionOfIncomeAndExpenseAction();
    }

    public void testCheckCapitalAccountingLinesSelected() throws Exception {
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = getForm();

        assertTrue(((CapitalAccountingLinesDocumentBase)capitalAccountingLinesFormBase.getFinancialDocument()).getCapitalAssetInformation().size()> 0);
        capitalAssetInformationActionBase.checkCapitalAccountingLinesSelected(capitalAccountingLinesFormBase);
        /*Ensure the function doesn't take the capital asset informations off the document!*/
        assertTrue(((CapitalAccountingLinesDocumentBase)capitalAccountingLinesFormBase.getFinancialDocument()).getCapitalAssetInformation().size()> 0);

        assertTrue(capitalAccountingLinesFormBase.getSystemControlAmount().equals(new KualiDecimal("5")));
        assertTrue(capitalAccountingLinesFormBase.getCreatedAssetsControlAmount().equals(KualiDecimal.ZERO));
        List<CapitalAccountingLines> lines = ((CapitalAccountingLinesDocumentBase)capitalAccountingLinesFormBase.getDocument()).getCapitalAccountingLines();
        assertTrue(lines.get(0).isSelectLine());
        assertFalse(lines.get(1).isSelectLine());
    }

    protected CapitalAccountingLinesFormBase getForm() throws Exception {
        CapitalAccountingLinesDocumentBase capitalAccountingLinesDocumentBase = new DistributionOfIncomeAndExpenseDocument();
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = new DistributionOfIncomeAndExpenseForm();
        CapitalAssetInformation capitalAssetInformation= new CapitalAssetInformation();
        capitalAssetInformation.setCapitalAssetLineNumber(1);
        capitalAssetInformation.setDocumentNumber("1031400");
        capitalAssetInformation.setCapitalAssetLineAmount(new KualiDecimal("5"));
        CapitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails = getCapitalAssetAccountsGroupDetails("1031400", "BL", "7000","5",1, true);
        CapitalAccountingLines capitalAccountingLines = getCapitalAccountingLines(capitalAssetAccountsGroupDetails);
        capitalAccountingLines.setSelectLine(true);
        capitalAssetInformation.getCapitalAssetAccountsGroupDetails().add(capitalAssetAccountsGroupDetails);
        capitalAccountingLinesDocumentBase.getCapitalAccountingLines().add(capitalAccountingLines);
        capitalAssetAccountsGroupDetails = getCapitalAssetAccountsGroupDetails("1031400", "BL", "7300", "2", 2, false);
        capitalAccountingLines = getCapitalAccountingLines(capitalAssetAccountsGroupDetails);
        capitalAssetInformation.getCapitalAssetAccountsGroupDetails().add(capitalAssetAccountsGroupDetails);
        capitalAccountingLinesDocumentBase.getCapitalAccountingLines().add(capitalAccountingLines);

        capitalAccountingLinesDocumentBase.getCapitalAssetInformation().add(capitalAssetInformation);
        capitalAccountingLinesFormBase.setDocument(capitalAccountingLinesDocumentBase);
        return capitalAccountingLinesFormBase;
    }

    protected static CapitalAssetAccountsGroupDetails getCapitalAssetAccountsGroupDetails(String accountNumber, String chartOfAccountsCode,
            String financialObjectCode, String amount, int lineNumber, boolean isSourceAccountingLine){
        CapitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails = new CapitalAssetAccountsGroupDetails();
        capitalAssetAccountsGroupDetails.setAccountNumber(accountNumber);
        capitalAssetAccountsGroupDetails.setChartOfAccountsCode(chartOfAccountsCode);
        capitalAssetAccountsGroupDetails.setFinancialObjectCode(financialObjectCode);
        capitalAssetAccountsGroupDetails.setAmount(new KualiDecimal(amount));
        capitalAssetAccountsGroupDetails.setCapitalAssetLineNumber(lineNumber);
        capitalAssetAccountsGroupDetails.setSequenceNumber(lineNumber);
        capitalAssetAccountsGroupDetails.setFinancialDocumentLineTypeCode(isSourceAccountingLine ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE);
        return capitalAssetAccountsGroupDetails;
    }

    protected static CapitalAccountingLines getCapitalAccountingLines(CapitalAssetAccountsGroupDetails capitalAssetAccountsGroupDetails){
        CapitalAccountingLines capitalAccountingLines = new CapitalAccountingLines();
        capitalAccountingLines.setAccountNumber(capitalAssetAccountsGroupDetails.getAccountNumber());
        capitalAccountingLines.setChartOfAccountsCode(capitalAssetAccountsGroupDetails.getChartOfAccountsCode());
        capitalAccountingLines.setFinancialObjectCode(capitalAssetAccountsGroupDetails.getFinancialObjectCode());
        capitalAccountingLines.setAmount(capitalAssetAccountsGroupDetails.getAmount());
        capitalAccountingLines.setLineType(KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(capitalAssetAccountsGroupDetails.getFinancialDocumentLineTypeCode()) ? KFSConstants.SOURCE :KFSConstants.TARGET);
        capitalAccountingLines.setSequenceNumber(capitalAssetAccountsGroupDetails.getSequenceNumber());
        return capitalAccountingLines;
    }

}
