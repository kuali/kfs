/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Special Circumstances Business Object
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
@Entity
@Table(name="TEM_SPCL_CRCMSNCS_T")
public class SpecialCircumstances extends PersistableBusinessObjectBase {

    @Column(name="doc_nbr")
    private String documentNumber;

    @Column(name="QID",nullable=false)
    private Long questionId;
    
    @Column(name="TEXT",length=255,nullable=false)
    private String text;
    
    @Column(name="RESPONSE",nullable=false,length=1)
    private Boolean response = false;

    @OneToOne(mappedBy="qid")
    private SpecialCircumstancesQuestion question;
    
    @Id
    @GeneratedValue(generator="TEM_SPCL_CRCMSNCS_S")
    @SequenceGenerator(name="TEM_SPCL_CRCMSNCS_S",sequenceName="TEM_SPCL_CRCMSNCS_S", allocationSize=5)
    @Column(name="ID",nullable=false)
    private Long id;
    
    
    /**
     * 
     * This method returns the document primary key to fix issues with multiple questions
     * @return document number
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * This method returns the document number this TravelerDetail object is associated with
     * @return document number
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * 
     * This method sets the document number this TravelerDetail object will be associated with
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SpecialCircumstancesQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SpecialCircumstancesQuestion question) {
        this.question = question;
    }

    public Boolean getResponse() {
        return response != null ? response : false;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("questionId", questionId);
        map.put("text", text);
        map.put("response", response);
        
        return map;
    }
}
