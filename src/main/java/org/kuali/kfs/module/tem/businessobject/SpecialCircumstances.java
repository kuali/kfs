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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Special Circumstances Business Object
 *
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

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("questionId", questionId);
        map.put("text", text);
        map.put("response", response);

        return map;
    }
}
