package org.kuali.module.budget.service;

import java.util.List;

import org.kuali.module.chart.bo.Org;


/**
 * This interface defines the methods that a PermissionService must provide.
 * 
 * PermissionService implements methods used to support the BudgetConstruction Security Model.
 * The User access mode to a Budgeted Account (BC Document) is calculated based on the current level
 * of the document in it's organization hierarchy and the set of organizations where the user is defined
 * as a BC Document approver in the organization review hierarchy or the set of accounts where the user
 * is defined as the Fiscal Officer.
 * 
 * Edit access requires the document to be at the same level as one of the user's organization approval nodes
 * or the user is a fiscal officer or delegate of an account for a document at level zero. the User gets 
 * View access to a document set at a level below a user's organization approval node. No access is allowed
 * to a document set at a level above the user's organization approval node.
 * 
 * Organization review hierarchy approval nodes are defined in Workflow as rules using the
 * KualiOrgReviewTemplate where the Document Type is KualiBudgetConstructionDocument and the
 * Chart and Organization codes define the node in the hierarchy and responsibilty type is 
 * Person or Workgroup and Action Request Code is Approve.
 * 
 * TODO verify the description of the rule definition after implementation.
 */
public interface PermissionService {

    /**
     * This method returns a list of organizations where the user is a BC document approver
     * 
     * @param universalUserId
     * @return
     * @throws Exception
     */
    public List<Org> getOrgReview(String personUserIdentifier) throws Exception;

    /**
     * This method returns whether or not a user is a BC approver for the passed in organization primary key values
     *
     * @param personUserIdentifier
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean isOrgReviewApprover(String personUserIdentifier, String chartOfAccountsCode, String organizationCode) throws Exception;
}