package edu.arizona.kfs.fp.document.authorization;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.authorization.CapitalAccountingLinesAuthorizerBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralErrorCorrectionReversingAccountingLinesAuthorizer extends CapitalAccountingLinesAuthorizerBase {

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
    public boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, Person currentUser, boolean pageIsEditable) {
        return false;
    }

    @Override
    public boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editableLine, boolean editablePage, Person currentUser) {
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
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> retval = new HashMap<String, AccountingLineViewAction>();
        if (accountingLineIndex != null) {
            AccountingLineViewAction balanceInquiryAction = this.getBalanceInquiryAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            AccountingLineViewAction deleteAction = getDeleteAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            AccountingLineViewAction copyAction = getCopyAction(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, groupTitle);
            retval.put(KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD, balanceInquiryAction);
            retval.put(KFSConstants.DELETE_LINE_METHOD, deleteAction);
            retval.put(KFSConstants.COPY_METHOD, copyAction);
        }
        return retval;
    }

    /**
     * construct the copy action for the given accounting line, typically, a new accounting line
     *
     * @param accountingLine
     *            the given accounting line
     * @param accountingLinePropertyName
     *            the property name of the given account line, typically, the form name
     * @param accountingLineIndex
     *            the index of the given accounting line in its accounting line group
     * @param groupTitle
     *            the title of the accounting line group
     * @return the copy action for the given accounting line
     */
    protected AccountingLineViewAction getCopyAction(AccountingLine accountingLine, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        String actionMethod = this.getCopyLineMethod(accountingLine, accountingLinePropertyName, accountingLineIndex);
        String actionLabel = this.getActionLabel(KFSKeyConstants.AccountingLineViewRendering.ACCOUNTING_LINE_COPY_ACTION_LABEL, groupTitle, accountingLineIndex + 1);
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        String imagesPath = kualiConfigurationService.getPropertyValueAsString(KRADConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY);
        String actionImageName = imagesPath + "tinybutton-copy2.gif";
        AccountingLineViewAction retval = new AccountingLineViewAction(actionMethod, actionLabel, actionImageName);
        return retval;
    }

    /**
     * Builds the action method name of the method that deletes accounting lines for this group
     *
     * @param accountingLine
     *            the accounting line an action is being checked for
     * @param accountingLinePropertyName
     *            the property name of the accounting line
     * @param accountingLineIndex
     *            the index of the given accounting line within the the group being rendered
     * @return the action method name of the method that deletes accounting lines for this group
     */
    protected String getCopyLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String retval = "copyAccountingLine.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
        return retval;
    }

}
