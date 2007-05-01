/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.web.struts.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.rules.event.UpdateNonpersonnelEventBase;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelFormHelper;

public class BudgetNonpersonnelAction extends BudgetAction {

    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.execute(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.setBudgetNonpersonnelFormHelper(new BudgetNonpersonnelFormHelper(budgetForm));

        return forward;
    }

    public ActionForward insertNonpersonnelLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        int listIndex = super.getSelectedLine(request);

        // convert the list to an array to make it easier to interact with
        BudgetNonpersonnel[] newNonpersonnelArray = (BudgetNonpersonnel[]) budgetForm.getNewNonpersonnelList().toArray(new BudgetNonpersonnel[budgetForm.getNewNonpersonnelList().size()]);

        // get the item out of the array that represents the new nonpersonnel item we want to add and populate it
        BudgetNonpersonnel newNonpersonnel = (BudgetNonpersonnel) budgetForm.getNewNonpersonnelList().get(listIndex);
        // add the newNonpersonnel item to the nonpersonnel list
        budgetForm.getBudgetDocument().addNonpersonnel(newNonpersonnel);

        // replace the one added to the list with a new one
        newNonpersonnelArray[listIndex] = new BudgetNonpersonnel();

        // convert the array back to a list and set into the form
        budgetForm.setNewNonpersonnelList(Arrays.asList(newNonpersonnelArray));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteNonpersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        List nonpersonnelItems = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems();

        int lineToDelete = getLineToDelete(request);
        BudgetNonpersonnel budgetNonpersonnelRemoved = (BudgetNonpersonnel) nonpersonnelItems.get(lineToDelete);

        // Remove the item that the user selected for removal
        nonpersonnelItems.remove(getLineToDelete(request));

        if (budgetNonpersonnelRemoved.isCopiedOverItem()) {
            // Iterate over the whole list of NPRS items and remove the copy over items that have the removed item as origin.
            
            // necessary to make copy because period fill up below removes from the list, want to avoid concurrent modification exception
            List nonpersonnelItemsTmp = new ArrayList(budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems());

            for (Iterator nonpersonnelItemsIter = nonpersonnelItemsTmp.iterator(); nonpersonnelItemsIter.hasNext();) {
                BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItemsIter.next();

                // check for any item that uses this sequence number as the origin (they are all copied over) or for an item that
                // has the origin sequence number (that is the origin). Coincidentally the second check should not be necessary
                // since origin items also have origin item sequence number set. But better safe then sorry.
                if (budgetNonpersonnelRemoved.getBudgetOriginSequenceNumber().equals(budgetNonpersonnel.getBudgetOriginSequenceNumber()) || budgetNonpersonnelRemoved.getBudgetOriginSequenceNumber().equals(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber())) {
                    nonpersonnelItems.remove(budgetNonpersonnel);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward saveNonpersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // check any business rules. This has to be done before saving too because copy over could occur.
        // copy over can only occur when validation has taken place.
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new UpdateNonpersonnelEventBase(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems()));

        if (!rulePassed) {
            budgetForm.setCurrentTaskNumber(budgetForm.getPreviousTaskNumber());
            budgetForm.setCurrentPeriodNumber(budgetForm.getPreviousPeriodNumber());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // check if any "Check All" checkbox is checked.
        for (Iterator nonpersonnelListIter = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems().iterator(); nonpersonnelListIter.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelListIter.next();
            if (budgetNonpersonnel.getCopyToFuturePeriods()) {
                // found a checked one. Go ahead and construct to BudgetNonpersonnelCopyOverFormHelper
                // based on this, and immediatly deconstruct this. By default this will copy over all
                // checked items (= checked all) that haven't been copied over yet.
                List nonpersonnelItems = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems();
                SpringServiceLocator.getBudgetNonpersonnelService().refreshNonpersonnelObjectCode(nonpersonnelItems);
                new BudgetNonpersonnelCopyOverFormHelper(budgetForm).deconstruct(budgetForm);
                break;
            }
        }

        // Needs to be done after the copy over check because copy over may be changing nonpersonnel & sequence number data.
        List nonpersonnelList = new ArrayList(budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems());
        Integer nonpersonnelNextSequenceNumber = budgetForm.getBudgetDocument().getNonpersonnelNextSequenceNumber();
        
        // we are only saving nonpersonnel items, so load the doc and set the nonpersonnel items to the proper ones.
        super.load(mapping, form, request, response);
        budgetForm.getBudgetDocument().getBudget().setNonpersonnelItems(nonpersonnelList);
        budgetForm.getBudgetDocument().setNonpersonnelNextSequenceNumber(nonpersonnelNextSequenceNumber);

        return super.save(mapping, form, request, response);
    }

    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        return super.update(mapping, form, request, response);
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new UpdateNonpersonnelEventBase(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems()));

        if (rulePassed) {
            super.setupNonpersonnelCategories(budgetForm);
        }
        else {
            budgetForm.setCurrentTaskNumber(budgetForm.getPreviousTaskNumber());
            budgetForm.setCurrentPeriodNumber(budgetForm.getPreviousPeriodNumber());
        }

        return super.update(mapping, form, request, response);
    }

    public ActionForward nonpersonnelCopyOver(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        List nonpersonnelItems = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems();
        budgetForm.getBudgetNonpersonnelFormHelper().refresh(nonpersonnelItems);

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new UpdateNonpersonnelEventBase(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems()));

        if (!rulePassed) {
            budgetForm.setCurrentTaskNumber(budgetForm.getPreviousTaskNumber());
            budgetForm.setCurrentPeriodNumber(budgetForm.getPreviousPeriodNumber());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // Refresh reference objects so that NonpersonnelObjectCode is populated. This
        // is necessary because it maybe a new object that hasn't been saved to the
        // database yet.
        SpringServiceLocator.getBudgetNonpersonnelService().refreshNonpersonnelObjectCode(nonpersonnelItems);

        // prepare data for NPRS copy over page
        budgetForm.setBudgetNonpersonnelCopyOverFormHelper(new BudgetNonpersonnelCopyOverFormHelper(budgetForm));

        // preparee what category was selected
        String parameterName = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String lineCode = StringUtils.substringBetween(parameterName, ".code", ".");
        budgetForm.setCurrentNonpersonnelCategoryCode(lineCode);

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(false, false);

        return mapping.findForward("nonpersonnelCopyOver");
    }
}