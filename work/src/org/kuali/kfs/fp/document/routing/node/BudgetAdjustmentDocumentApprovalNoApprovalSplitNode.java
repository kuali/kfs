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
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;
import edu.iu.uis.eden.routetemplate.RouteContext;

/* TODO this code is fake for POC - put the correct business logic in here
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocumentApprovalNoApprovalSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        XPath xpath = KualiWorkflowUtils.getXPath(KualiWorkflowUtils.getDocument(routeContext.getDocument().getDocContent()));
        NodeList accountNumberNodeList = (NodeList) xpath.evaluate("//org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine/accountNumber/text()", KualiWorkflowUtils.getDocument(routeContext.getDocument().getDocContent()), XPathConstants.NODESET);
        for (int accountNumberIndex = 0; accountNumberIndex < accountNumberNodeList.getLength(); accountNumberIndex++) {
            Node accountNumberNode = accountNumberNodeList.item(accountNumberIndex);
            if ("1031420".equals(accountNumberNode.getNodeValue())) {
                List branchNames = new ArrayList();
                branchNames.add("NoApprovalBranch");
                return new SplitResult(branchNames);
            }
        }
        List branchNames = new ArrayList();
        branchNames.add("ApprovalBranch");
        return new SplitResult(branchNames);
    }
}
