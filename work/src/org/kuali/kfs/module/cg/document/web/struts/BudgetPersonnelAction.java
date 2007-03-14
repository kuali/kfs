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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.rules.event.CalculatePersonnelEvent;
import org.kuali.module.kra.budget.rules.event.InsertPersonnelEventBase;
import org.kuali.module.kra.budget.rules.event.SavePersonnelEventBase;
import org.kuali.module.kra.budget.rules.event.UpdatePersonnelEventBase;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;


/**
 * This class handles Actions for Research Administration.
 * 
 * 
 */

public class BudgetPersonnelAction extends BudgetAction {

    private static final String PERSON = "person";
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPersonnelAction.class);

    /**
     * This method overrides the BudgetAction execute method. It does so for the purpose of recalculating Personnel expenses any
     * time the Personnel page is accessed
     * 
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        boolean rulePassed = runRule(budgetForm, new CalculatePersonnelEvent(budgetForm.getDocument()));
        
        if (rulePassed) {
            SpringServiceLocator.getBudgetPersonnelService().calculateAllPersonnelCompensation(budgetForm.getBudgetDocument());
            return super.execute(mapping, form, request, response);
        } else if (StringUtils.equals(Constants.RELOAD_METHOD_TO_CALL, budgetForm.getMethodToCall())) {
            GlobalVariables.getErrorMap().clear();
            return this.reload(mapping, form, request, response);
        } else {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
    }

    public ActionForward updateView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        boolean rulePassed = runRule(budgetForm, new UpdatePersonnelEventBase(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getPersonnel()));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward insertPersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        boolean rulePassed = runRule(budgetForm, new InsertPersonnelEventBase(budgetForm.getDocument(), budgetForm.getNewPersonnel(), !StringUtils.equals(PERSON, budgetForm.getNewPersonnelType())));
        
        if (rulePassed) {
            if (!StringUtils.equals(PERSON, budgetForm.getNewPersonnelType())) {
                BudgetUser newPersonnel = new BudgetUser();
                newPersonnel.setRole(budgetForm.getNewPersonnel().getRole());
                budgetForm.setNewPersonnel(newPersonnel);
            }

            budgetForm.getBudgetDocument().addPersonnel(budgetForm.getNewPersonnel());
            budgetForm.setNewPersonnel(new BudgetUser());

            budgetForm.setNewPersonnelType(PERSON);

        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward savePersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Cast
        BudgetForm budgetForm = (BudgetForm) form;

        // Cache
        List personnelList = new ArrayList(budgetForm.getBudgetDocument().getBudget().getPersonnel());
        Integer personnelNextSequenceNumber = budgetForm.getBudgetDocument().getPersonnelNextSequenceNumber();

        // Load
        super.load(mapping, form, request, response);


        // Set from Cache
        budgetForm.getBudgetDocument().setPersonnelNextSequenceNumber(personnelNextSequenceNumber);
        budgetForm.getBudgetDocument().getBudget().setPersonnel(personnelList);

        // check any business rules that are specific to saving from this page only.
        SpringServiceLocator.getKualiRuleService().applyRules(new SavePersonnelEventBase(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getPersonnel()));


        // Super save
        return super.save(mapping, form, request, response);
    }

    public ActionForward calculateCompensation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return this.updateView(mapping, form, request, response);
    }

    public ActionForward deletePersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        List personnel = budgetForm.getBudgetDocument().getBudget().getPersonnel();

        for (Iterator i = personnel.iterator(); i.hasNext(); ) {
            BudgetUser budgetUser = (BudgetUser)i.next();
            if (budgetUser.isDelete()) {
                i.remove();
            }
        }
        
//        String[] deleteIndexes = budgetForm.getDeleteValues();
//        if (deleteIndexes != null && deleteIndexes.length > 0) {
//            for (int i = deleteIndexes.length - 1; i > -1; i--) {
//                if (deleteIndexes[i] != null) {
//                    personnel.remove(Integer.parseInt(deleteIndexes[i]));
//                }
//            }
//        }
//        budgetForm.setDeleteValues(new String[personnel.size()]);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward clearNewPersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.setNewPersonnel(new BudgetUser());
        budgetForm.setNewPersonnelType(PERSON);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        super.reload(mapping, form, request, response);

        Collections.sort(budgetForm.getBudgetDocument().getBudget().getPersonnel());
        
        SpringServiceLocator.getBudgetPersonnelService().calculateAllPersonnelCompensation(budgetForm.getBudgetDocument());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private boolean runRule(BudgetForm budgetForm, KualiDocumentEventBase event) {
        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(event);

        if (!rulePassed) {
            // have to reset all of the tasks and appointment types of everyone on the page.
            for (Iterator i = budgetForm.getBudgetDocument().getBudget().getPersonnel().iterator(); i.hasNext();) {
                BudgetUser budgetUser = (BudgetUser) i.next();
                budgetUser.setCurrentTaskNumber(budgetUser.getPreviousTaskNumber());

                for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                    if (userAppointmentTask.getInstitutionAppointmentTypeCode().equals(budgetUser.getAppointmentTypeCode())) {
                        userAppointmentTask.setInstitutionAppointmentTypeCode(budgetUser.getPreviousAppointmentTypeCode());
                    } else if (userAppointmentTask.getInstitutionAppointmentTypeCode().equals(budgetUser.getSecondaryAppointmentTypeCode())) {
                        userAppointmentTask.setInstitutionAppointmentTypeCode(budgetUser.getPreviousSecondaryAppointmentTypeCode());
                    }
                    for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                        userAppointmentTaskPeriod.setInstitutionAppointmentTypeCode(userAppointmentTask.getInstitutionAppointmentTypeCode());
                    }
                }

                budgetUser.setAppointmentTypeCode(budgetUser.getPreviousAppointmentTypeCode());
                budgetUser.setSecondaryAppointmentTypeCode(budgetUser.getPreviousSecondaryAppointmentTypeCode());
                
            }
        }
        
        return rulePassed;
    }
}
