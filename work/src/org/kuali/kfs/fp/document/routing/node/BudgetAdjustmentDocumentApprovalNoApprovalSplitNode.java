/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.workflow.node;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;
import edu.iu.uis.eden.routetemplate.RouteContext;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.FUND_GROUP_CODE.CONTRACT_GRANTS;

/**
 * Checks for conditions on a Budget Adjustment document that allow auto-approval by the initiator. If these conditions are not met,
 * standard financial routing is performed.
 * 
 * The conditions for auto-approval are: 1) Single account used on document 2) Initiator is fiscal officer or primary delegate for
 * the account 3) Only current adjustments are being made 4) The fund group for the account is not contract and grants
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentApprovalNoApprovalSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        boolean autoApprovalAllowed = true;

        // retrieve ba document
        String documentID = routeContext.getDocument().getRouteHeaderId().toString();
        BudgetAdjustmentDocument budgetDocument = (BudgetAdjustmentDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(documentID);

        List accountingLines = budgetDocument.getSourceAccountingLines();
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());
      
        /* only one account can be present on document and only current adjustments allowed */
        String chart = "";
        String accountNumber = "";
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (StringUtils.isNotBlank(accountNumber)) {
                if (!accountNumber.equals(line.getAccountNumber()) && !chart.equals(line.getChartOfAccountsCode())) {
                    autoApprovalAllowed = false;
                    break;
                }
            }
            
            if (line.getBaseBudgetAdjustmentAmount().isNonZero()) {
                autoApprovalAllowed = false;
                break;
            }
            chart = line.getChartOfAccountsCode();
            accountNumber = line.getAccountNumber();
        }
        
        // check remaining conditions
        if (autoApprovalAllowed) {
            // user should be fiscal officer or primary delegate for account
            List userAccounts = SpringServiceLocator.getAccountService().getAccountsThatUserIsResponsibleFor(GlobalVariables.getUserSession().getKualiUser());
            Account userAccount = null;
            for (Iterator iter = userAccounts.iterator(); iter.hasNext();) {
                Account account = (Account) iter.next();
                if (accountNumber.equals(account.getAccountNumber()) && chart.equals(account.getChartOfAccountsCode())) {
                    userAccount = account;
                    break;
                }
            }
            
            if (userAccount == null) {
                autoApprovalAllowed = false;
            }
            else {
                // fund group should not be CG
                if (CONTRACT_GRANTS.equals(userAccount.getSubFundGroup().getFundGroupCode())) {
                    autoApprovalAllowed = false;
                }
            }
        }

        List branchNames = new ArrayList();
        if (autoApprovalAllowed) {
            branchNames.add("NoApprovalBranch");
        }
        else {
            branchNames.add("ApprovalBranch");
        }
        
        return new SplitResult(branchNames);
    }
}
