package edu.arizona.kfs.fp.document.authorization;

import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

public class DistributionOfIncomeAndExpenseDocumentPresentationController extends org.kuali.kfs.fp.document.authorization.DistributionOfIncomeAndExpenseDocumentPresentationController {
		    
    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);

        DistributionOfIncomeAndExpenseDocument distributionOfIncomeAndExpenseDocument = (DistributionOfIncomeAndExpenseDocument) document;
        String docInError = distributionOfIncomeAndExpenseDocument.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();
        
        if (StringUtils.isNotBlank(docInError)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }
        return documentActions;
    }
    			
}
