/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sec.document.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl;
import org.kuali.kfs.sys.document.web.RenderableAccountingLineContainer;
import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * Integrates with access security module to check security on accounting lines before rendering
 */
public class SecAccountingLineGroupImpl extends DefaultAccountingLineGroupImpl {
    protected boolean hasEditRestrictions;
    protected boolean hasViewRestrictions;

    /**
     * Constructs a SecAccountingLineGroupImpl
     */
    public SecAccountingLineGroupImpl() {
        hasEditRestrictions = false;
        hasViewRestrictions = false;
    }

    /**
     * Performs access security edit check and sets edit flag on container line to false if access is not allowed or removes
     * container if view is not allowed
     *
     * @see org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl#initialize(org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition,
     *      org.kuali.kfs.sys.document.AccountingDocument, java.util.List, java.lang.String, java.lang.String, java.util.Map,
     *      java.util.Map, java.util.Map, boolean)
     */
    @Override
    public void initialize(AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, List<RenderableAccountingLineContainer> containers, String collectionPropertyName, String collectionItemPropertyName, Map<String, Object> displayedErrors, Map<String, Object> displayedWarnings, Map<String, Object> displayedInfo, boolean canEdit) {
        AccessSecurityService accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        // check view and edit access
        List<RenderableAccountingLineContainer> unviewableContainers = new ArrayList<RenderableAccountingLineContainer>();
        for (RenderableAccountingLineContainer container : containers) {
            boolean lineHasError = false;
            for (Object errorKeyAsObject : GlobalVariables.getMessageMap().getErrorMessages().keySet() ) {
                if (((String) errorKeyAsObject).startsWith(collectionItemPropertyName)) {
                    lineHasError = true;
                }
            }

            if (lineHasError || container.isNewLine()) {
                container.setEditableLine(true);
                continue;
            }

            boolean viewAllowed = accessSecurityService.canViewDocumentAccountingLine(accountingDocument, container.getAccountingLine(), currentUser);
            if (!viewAllowed) {
                unviewableContainers.add(container);
                hasViewRestrictions = true;
            }
            else {
                boolean editAllowed = accessSecurityService.canEditDocumentAccountingLine(accountingDocument, container.getAccountingLine(), currentUser);

                if (container.isEditableLine() && !editAllowed) {
                    container.setEditableLine(false);
                    hasEditRestrictions = true;
                }
            }
        }

        // remove containers that are not viewable
        for (RenderableAccountingLineContainer container : unviewableContainers) {
            containers.remove(container);
        }

        super.initialize(groupDefinition, accountingDocument, containers, collectionPropertyName, collectionItemPropertyName, displayedErrors, displayedWarnings, displayedInfo, canEdit);
    }

    /**
     * Adds info message if we have restricted view of any accounting lines and adds an additional key to match on
     *
     * @see org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl#renderErrors(javax.servlet.jsp.PageContext,
     *      javax.servlet.jsp.tagext.Tag)
     */
    @Override
    protected void renderErrors(PageContext pageContext, Tag parentTag) throws JspException {
        renderSecurityMessage(pageContext, parentTag);

        renderMessages(pageContext, parentTag, groupDefinition.getErrorKey());
    }

    /**
     * Helper method for outputting messages
     *
     * @param pageContext
     * @param parentTag
     * @param messageKey - key for messages to display
     * @throws JspException
     */
    protected void renderMessages(PageContext pageContext, Tag parentTag, String messageKey) throws JspException {
        GroupErrorsRenderer errorRenderer = getErrorRenderer();
        errorRenderer.setErrorKeyMatch(messageKey);
        errorRenderer.setColSpan(getWidthInCells());
        errorRenderer.render(pageContext, parentTag);

        moveListToMap(errorRenderer.getErrorsRendered(), getDisplayedErrors());
        moveListToMap(errorRenderer.getWarningsRendered(), getDisplayedWarnings());
        moveListToMap(errorRenderer.getInfoRendered(), getDisplayedInfo());

        errorRenderer.clear();
    }

    /**
     * Adds info message for any security restrictions that have been applied
     *
     * @param pageContext
     * @param parentTag
     * @throws JspException
     */
    protected void renderSecurityMessage(PageContext pageContext, Tag parentTag) throws JspException {
        String secErrorKey = SecConstants.ACCOUNTING_GROUP_ERROR_KEY_PREFIX + collectionItemPropertyName + collectionPropertyName;

        // add info message if we are restricting any lines from view
        if (hasEditRestrictions || hasViewRestrictions) {
            List pageWarnings = (List) pageContext.getRequest().getAttribute("InfoPropertyList");
            if (pageWarnings == null) {
                pageWarnings = new ArrayList();
            }
            pageWarnings.add(secErrorKey);
            pageContext.getRequest().setAttribute("InfoPropertyList", pageWarnings);

            ActionMessages requestErrors = (ActionMessages) pageContext.getRequest().getAttribute("InfoActionMessages");
            if (requestErrors == null) {
                requestErrors = new ActionMessages();
            }

            if (hasViewRestrictions) {
                requestErrors.add(secErrorKey, new ActionMessage(SecKeyConstants.MESSAGE_ACCOUNTING_LINE_VIEW_RESTRICTED));
            }
            else {
                requestErrors.add(secErrorKey, new ActionMessage(SecKeyConstants.MESSAGE_ACCOUNTING_LINE_EDIT_RESTRICTED));
            }

            pageContext.getRequest().setAttribute(Globals.ERROR_KEY, requestErrors);
        }

        renderMessages(pageContext, parentTag, secErrorKey);
    }

}
