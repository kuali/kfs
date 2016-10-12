package edu.arizona.kfs.fp.document.authorization;

import edu.arizona.kfs.sys.KFSConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizerBase;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralErrorCorrectionCorrectingAccountingLinesAuthorizer extends CapitalAccountingLinesAuthorizerBase {

    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        return false;
    }

    @Override
    public boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser) {
        WorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) {
            return StringUtils.equalsIgnoreCase(workflowDocument.getInitiatorPrincipalId(), currentUser.getPrincipalId());
        }
        return false;
    }

    @Override
    public List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, Person currentUser, String groupTitle) {
        List<AccountingLineViewAction> actions = new ArrayList<AccountingLineViewAction>();
        Map<String, AccountingLineViewAction> actionMap = this.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);
        actions.addAll(actionMap.values());
        return actions;
    }

    @Override
    public boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, Person currentUser, boolean pageIsEditable) {
        if (accountingLine.getSequenceNumber() == null) {
            return false;
        }
        boolean retval = super.hasEditPermissionOnAccountingLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUser, pageIsEditable);
        return retval;
    }

    @Override
    public boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editableLine, boolean editablePage, Person currentUser) {
        if (accountingLine.getSequenceNumber() == null) {
            return false;
        }
        boolean retval = super.hasEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editableLine, editablePage, currentUser);
        return retval;
    }

    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> retval = new HashMap<String, AccountingLineViewAction>();
        if (accountingLineIndex != null) {
            AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            AccountingLineViewAction deleteAction = getDeleteAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            AccountingLineViewAction addAction = this.getAddLineAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            retval.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);
            retval.put(KFSConstants.DELETE_LINE_METHOD, deleteAction);
            retval.put(KFSConstants.ADD_LINE_METHOD, addAction);
        }
        return retval;
    }

    private AccountingLineViewAction getAddLineAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getAddLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_ADD_ACTION_LABEL, groupTitle);
        String actionImageName = getRiceImagePath() + "tinybutton-add1.gif";
        AccountingLineViewAction retval = new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
        return retval;
    }

    private String getAddLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        String retval = KFSConstants.INSERT_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
        return retval;
    }

}
