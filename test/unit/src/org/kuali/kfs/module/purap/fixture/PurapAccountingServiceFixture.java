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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.kns.util.KualiDecimal;

public enum PurapAccountingServiceFixture {
    
    PRORATION_ONE_ACCOUNT(PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class, 
            PurApAccountingLineFixture.BASIC_ACCOUNT_1),
    PRORATION_TWO_ACCOUNTS(
            PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class, 
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT,
            PurApAccountingLineFixture.ACCOUNT_50_PERCENT),
    PRORATION_THIRDS(PurapTestConstants.AmountsLimits.SMALL_POSITIVE_AMOUNT,PurapConstants.PRORATION_SCALE,PaymentRequestAccount.class,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD,
            PurApAccountingLineFixture.ACCOUNT_ONE_THIRD),;
    
    KualiDecimal totalAmount;
    Integer percentScale;
    Class accountClass;
    List<SourceAccountingLine> accountingLineList = new ArrayList<SourceAccountingLine>();
        
    private PurapAccountingServiceFixture(
            KualiDecimal totalAmount, 
            Integer percentScale, 
            Class accountClass, 
            PurApAccountingLineFixture ... purApAcctLineFixtures) {
        
        this.totalAmount = totalAmount;
        this.percentScale = percentScale;
        this.accountClass = accountClass;
        
        for( int i = 0; i < purApAcctLineFixtures.length; i++ ) {
            PurApAccountingLineFixture purApAcctLineFixture = purApAcctLineFixtures[i];
            
            PurApAccountingLine purApAcctLine = purApAcctLineFixture.createPurApAccountingLine(
                    accountClass, 
                    AccountingLineFixture.PURAP_LINE1);
            BigDecimal pct = purApAcctLine.getAccountLinePercent();
            pct = pct.divide(new BigDecimal(100));
            purApAcctLine.setAmount(totalAmount.multiply(new KualiDecimal(pct)));
            accountingLineList.add(purApAcctLine.generateSourceAccountingLine());
        }
    }

    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPercentScale() {
        return percentScale;
    }

    public void setPercentScale(Integer percentScale) {
        this.percentScale = percentScale;
    }

    public Class getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(Class accountClass) {
        this.accountClass = accountClass;
    }

    public List<SourceAccountingLine> getAccountingLineList() {
        return accountingLineList;
    }

    public void setAccountingLineList(List<SourceAccountingLine> accountingLineList) {
        this.accountingLineList = accountingLineList;
    }
}
