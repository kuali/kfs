/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.kra.routingform.bo.QuestionType;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormProjectDetailsService;

public class RoutingFormProjectDetailsServiceImpl implements RoutingFormProjectDetailsService {
    
    private BusinessObjectService businessObjectService;
    
    public void setupOtherProjectDetailsQuestions(RoutingFormDocument routingFormDocument) {
        List<QuestionType> questionTypes = getAllQuestionTypes();
        List<RoutingFormQuestion> questions = new ArrayList<RoutingFormQuestion>();
        for (QuestionType questionType: questionTypes) {
            questions.add(new RoutingFormQuestion(routingFormDocument.getDocumentNumber(), questionType));
        }
        routingFormDocument.setRoutingFormQuestions(questions);
    }
    
    public void reconcileOtherProjectDetailsQuestions(RoutingFormDocument routingFormDocument) {
        List<RoutingFormQuestion> questions = routingFormDocument.getRoutingFormQuestions();
        List<RoutingFormQuestion> newQuestions = new ArrayList<RoutingFormQuestion>();
        List<QuestionType> questionTypes = getAllQuestionTypes();
        List indexList = new ArrayList();
        for (RoutingFormQuestion question: questions) {
            QuestionType currentType = question.getQuestion();
            if (questionTypes.contains(currentType)) {
                newQuestions.add(question);
                indexList.add(questionTypes.indexOf(currentType));
            }
        }
        for (int i = 0; i < questionTypes.size(); i++) {
            if (!indexList.contains(i)) {
                newQuestions.add(new RoutingFormQuestion(routingFormDocument.getDocumentNumber(), questionTypes.get(i)));
            }
        }
        routingFormDocument.setRoutingFormQuestions(newQuestions);
    }
    
    private List<QuestionType> getAllQuestionTypes() {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, true);
        List<QuestionType> questionTypes = (List<QuestionType>) businessObjectService.findMatchingOrderBy(
                QuestionType.class, criteria, "questionTypeSortNumber", true);
        return questionTypes;
    }
    
    public List<String> getNotificationWorkgroups(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", documentNumber);
        List<RoutingFormQuestion> questions = new ArrayList<RoutingFormQuestion>(businessObjectService.findMatching(RoutingFormQuestion.class, fieldValues));
        List<String> workgroups = new ArrayList<String>();
        for (RoutingFormQuestion question : questions) {
            if (question.getQuestion().getQuestionTypeWorkgroupName() != null
                    && (question.getQuestion().getQuestionTypeNotificationValue() != null 
                            && question.getYesNoIndicator().equals(question.getQuestion().getQuestionTypeNotificationValue())
                            || question.getQuestion().getQuestionTypeNotificationValue().equals("A"))) {
                workgroups.add(question.getQuestion().getQuestionTypeWorkgroupName());
            }
        }
        return workgroups;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
