/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.identity;

import org.kuali.kfs.module.cg.businessobject.QuestionType;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class CgKimAttributes extends KfsKimAttributes {
    public static final String RESEARCH_RISK_TYPE_CODE = "researchRiskTypeCode";
    public static final String QUESTION_TYPE_CODE = "questionTypeCode";

    protected String researchRiskTypeCode;
    protected String questionTypeCode;

    protected ResearchRiskType researchRiskType;
    protected QuestionType questionType;
    
    
    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }
    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
    }
    public String getQuestionTypeCode() {
        return questionTypeCode;
    }
    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }
    public ResearchRiskType getResearchRiskType() {
        return researchRiskType;
    }
    public void setResearchRiskType(ResearchRiskType researchRiskType) {
        this.researchRiskType = researchRiskType;
    }
    public QuestionType getQuestionType() {
        return questionType;
    }
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
    
    
}
