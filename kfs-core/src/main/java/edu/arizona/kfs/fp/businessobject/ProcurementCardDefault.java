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
package edu.arizona.kfs.fp.businessobject;

import org.kuali.kfs.coa.businessobject.*;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.group.GroupBo;

import java.sql.Date;

/**
 * Holds the default data about Procurement Card and Procurement Card Holders loaded from batch job
 * @author nataliac
 */

public class ProcurementCardDefault extends org.kuali.kfs.fp.businessobject.ProcurementCardDefault {
    //Procurement Card Holder netid
    private String cardHolderSystemId;
    private Person cardholderUser;

    private String organizationCode;
    private Organization organization;

    private String reconcilerGroupId;
    private GroupBo reconcilerGroup;


    //encoded PCard Number - except last 4
    private String creditCardNumber;
    //last four digits of the PCard (corresponding to cardApprovalOfficial in KFS3.0)
    private String creditCardLastFour;

    private Integer cardMonthlyNumber;
    private Integer cardCycleVolLimit;

    private String cardCancelCode;
    private Date cardOpenDate;
    private Date cardCancelDate;
    private Date cardExpireDate;

    public String getCardHolderSystemId() {
        return cardHolderSystemId;
    }

    public void setCardHolderSystemId(String cardHolderSystemId) {
        this.cardHolderSystemId = cardHolderSystemId;
    }

    public Person getCardholderUser() {
        cardholderUser = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(cardHolderSystemId, cardholderUser);
        return cardholderUser;
    }

    public void setCardholderUser(Person cardholderUser) {
        this.cardholderUser = cardholderUser;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getReconcilerGroupId() {
        return reconcilerGroupId;
    }

    public void setReconcilerGroupId(String reconcilerGroupId) {
        this.reconcilerGroupId = reconcilerGroupId;
    }

    public GroupBo getReconcilerGroup() {
        return reconcilerGroup;
    }

    public void setReconcilerGroup(GroupBo reconcilerGroup) {
        this.reconcilerGroup = reconcilerGroup;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCreditCardLastFour() {
        return creditCardLastFour;
    }

    public void setCreditCardLastFour(String creditCardLastFour) {
        this.creditCardLastFour = creditCardLastFour;
    }

    public Integer getCardMonthlyNumber() {
        return cardMonthlyNumber;
    }

    public void setCardMonthlyNumber(Integer cardMonthlyNumber) {
        this.cardMonthlyNumber = cardMonthlyNumber;
    }

    public Integer getCardCycleVolLimit() {
        return cardCycleVolLimit;
    }

    public void setCardCycleVolLimit(Integer cardCycleVolLimit) {
        this.cardCycleVolLimit = cardCycleVolLimit;
    }

    public String getCardCancelCode() {
        return cardCancelCode;
    }

    public void setCardCancelCode(String cardCancelCode) {
        this.cardCancelCode = cardCancelCode;
    }

    public Date getCardOpenDate() {
        return cardOpenDate;
    }

    public void setCardOpenDate(Date cardOpenDate) {
        this.cardOpenDate = cardOpenDate;
    }

    public Date getCardCancelDate() {
        return cardCancelDate;
    }

    public void setCardCancelDate(Date cardCancelDate) {
        this.cardCancelDate = cardCancelDate;
    }

    public Date getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(Date cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }
}
