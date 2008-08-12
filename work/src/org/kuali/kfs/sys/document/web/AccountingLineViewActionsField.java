/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.web;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.renderers.ActionsRenderer;

/**
 * A field that can join tables and also be rendered, this represents a table cell
 * that displays the actions available on an accounting line
 */
public class AccountingLineViewActionsField extends FieldTableJoiningWithHeader {
    private String name = KFSConstants.AccountingLineViewStandardBlockNames.ACTION_BLOCK;
    private int tabIndex;

    /**
     * Returns the name of this actions field
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this actions field
     * @param name the name of this block
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * We are an action block.  For real, even
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#isActionBlock()
     */
    @Override
    public boolean isActionBlock() {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        ActionsRenderer renderer = new ActionsRenderer();
        renderer.setActions(renderingContext.getActionsForLine());
        //renderer.setTabIndex(tabIndex);
        renderer.setTabIndex(0);
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return new LiteralHeaderLabel(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.ACCOUNTING_LINE_ACTIONS_LABEL));
    }

    /**
     * This doesn't hold a field, so this imlementation does nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFieldNames(List<String> fieldNames) { }

    /**
     * Populated on the second pass
     * @see org.kuali.kfs.sys.document.web.TabIndexRequestor#getTabIndexPass()
     */
    public int getTabIndexPass() {
        return 1;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int[] passIndexes, int reallyHighIndex) {
        if (getTabIndexPass() > -1) {
            this.tabIndex = passIndexes[getTabIndexPass()];
        }
    }
    
}
