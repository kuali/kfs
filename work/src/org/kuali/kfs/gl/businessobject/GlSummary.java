/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.bo;

import org.kuali.core.util.KualiDecimal;

public class GlSummary {
    private String fundGroup;
    private KualiDecimal beginningBalance;
    private KualiDecimal cgBeginningBalance;
    private KualiDecimal annualBalance;
    private KualiDecimal month1;
    private KualiDecimal month2;
    private KualiDecimal month3;
    private KualiDecimal month4;
    private KualiDecimal month5;
    private KualiDecimal month6;
    private KualiDecimal month7;
    private KualiDecimal month8;
    private KualiDecimal month9;
    private KualiDecimal month10;
    private KualiDecimal month11;
    private KualiDecimal month12;
    private KualiDecimal month13;

    public GlSummary() {
        beginningBalance = KualiDecimal.ZERO;
        cgBeginningBalance = KualiDecimal.ZERO;
        annualBalance = KualiDecimal.ZERO;
        month1 = KualiDecimal.ZERO;
        month2 = KualiDecimal.ZERO;
        month3 = KualiDecimal.ZERO;
        month4 = KualiDecimal.ZERO;
        month5 = KualiDecimal.ZERO;
        month6 = KualiDecimal.ZERO;
        month7 = KualiDecimal.ZERO;
        month8 = KualiDecimal.ZERO;
        month9 = KualiDecimal.ZERO;
        month10 = KualiDecimal.ZERO;
        month11 = KualiDecimal.ZERO;
        month12 = KualiDecimal.ZERO;
        month13 = KualiDecimal.ZERO;
    }

    public GlSummary(Object[] data) {
        fundGroup = (String) data[0];

        setAnnualBalance((KualiDecimal) data[1]);
        setBeginningBalance((KualiDecimal) data[2]);
        setCgBeginningBalance((KualiDecimal) data[3]);
        setMonth1((KualiDecimal) data[4]);
        setMonth2((KualiDecimal) data[5]);
        setMonth3((KualiDecimal) data[6]);
        setMonth4((KualiDecimal) data[7]);
        setMonth5((KualiDecimal) data[8]);
        setMonth6((KualiDecimal) data[9]);
        setMonth7((KualiDecimal) data[10]);
        setMonth8((KualiDecimal) data[11]);
        setMonth9((KualiDecimal) data[12]);
        setMonth10((KualiDecimal) data[13]);
        setMonth11((KualiDecimal) data[14]);
        setMonth12((KualiDecimal) data[15]);
        setMonth13((KualiDecimal) data[16]);
    }

    public void add(GlSummary sum) {
        beginningBalance = beginningBalance.add(sum.beginningBalance);
        cgBeginningBalance = cgBeginningBalance.add(sum.cgBeginningBalance);
        annualBalance = annualBalance.add(sum.annualBalance);
        month1 = month1.add(sum.month1);
        month2 = month2.add(sum.month2);
        month3 = month3.add(sum.month3);
        month4 = month4.add(sum.month4);
        month5 = month5.add(sum.month5);
        month6 = month6.add(sum.month6);
        month7 = month7.add(sum.month7);
        month8 = month8.add(sum.month8);
        month9 = month9.add(sum.month9);
        month10 = month10.add(sum.month10);
        month11 = month11.add(sum.month11);
        month12 = month12.add(sum.month12);
        month13 = month13.add(sum.month13);
    }

    public KualiDecimal getYearBalance() {
        KualiDecimal sum = KualiDecimal.ZERO;
        sum = sum.add(month1);
        sum = sum.add(month2);
        sum = sum.add(month3);
        sum = sum.add(month4);
        sum = sum.add(month5);
        sum = sum.add(month6);
        sum = sum.add(month7);
        sum = sum.add(month8);
        sum = sum.add(month9);
        sum = sum.add(month10);
        sum = sum.add(month11);
        sum = sum.add(month12);
        sum = sum.add(month13);
        return sum;
    }


    public KualiDecimal getCgBeginningBalance() {
        return cgBeginningBalance;
    }

    public void setCgBeginningBalance(KualiDecimal cgBeginningBalance) {
        this.cgBeginningBalance = cgBeginningBalance;
    }

    public KualiDecimal getAnnualBalance() {
        return annualBalance;
    }

    public void setAnnualBalance(KualiDecimal annualBalance) {
        this.annualBalance = annualBalance;
    }

    public KualiDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(KualiDecimal beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public String getFundGroup() {
        return fundGroup;
    }

    public void setFundGroup(String fundGroup) {
        this.fundGroup = fundGroup;
    }

    public KualiDecimal getMonth1() {
        return month1;
    }

    public void setMonth1(KualiDecimal month1) {
        this.month1 = month1;
    }

    public KualiDecimal getMonth10() {
        return month10;
    }

    public void setMonth10(KualiDecimal month10) {
        this.month10 = month10;
    }

    public KualiDecimal getMonth11() {
        return month11;
    }

    public void setMonth11(KualiDecimal month11) {
        this.month11 = month11;
    }

    public KualiDecimal getMonth12() {
        return month12;
    }

    public void setMonth12(KualiDecimal month12) {
        this.month12 = month12;
    }

    public KualiDecimal getMonth13() {
        return month13;
    }

    public void setMonth13(KualiDecimal month13) {
        this.month13 = month13;
    }

    public KualiDecimal getMonth2() {
        return month2;
    }

    public void setMonth2(KualiDecimal month2) {
        this.month2 = month2;
    }

    public KualiDecimal getMonth3() {
        return month3;
    }

    public void setMonth3(KualiDecimal month3) {
        this.month3 = month3;
    }

    public KualiDecimal getMonth4() {
        return month4;
    }

    public void setMonth4(KualiDecimal month4) {
        this.month4 = month4;
    }

    public KualiDecimal getMonth5() {
        return month5;
    }

    public void setMonth5(KualiDecimal month5) {
        this.month5 = month5;
    }

    public KualiDecimal getMonth6() {
        return month6;
    }

    public void setMonth6(KualiDecimal month6) {
        this.month6 = month6;
    }

    public KualiDecimal getMonth7() {
        return month7;
    }

    public void setMonth7(KualiDecimal month7) {
        this.month7 = month7;
    }

    public KualiDecimal getMonth8() {
        return month8;
    }

    public void setMonth8(KualiDecimal month8) {
        this.month8 = month8;
    }

    public KualiDecimal getMonth9() {
        return month9;
    }

    public void setMonth9(KualiDecimal month9) {
        this.month9 = month9;
    }


}
