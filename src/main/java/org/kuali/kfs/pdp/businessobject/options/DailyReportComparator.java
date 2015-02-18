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
package org.kuali.kfs.pdp.businessobject.options;

import java.util.Comparator;

import org.kuali.kfs.pdp.businessobject.DailyReport;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.context.SpringContext;

public class DailyReportComparator implements Comparator<DailyReport> {
    
    public int compare(DailyReport o1, DailyReport o2) {
        PaymentGroupService paymentGroupService = SpringContext.getBean(PaymentGroupService.class);
        String key1 = paymentGroupService.getSortGroupId(o1.getPaymentGroup()) + o1.getCustomer();
        String key2 = paymentGroupService.getSortGroupId(o2.getPaymentGroup()) + o2.getCustomer();
        
        return key1.compareTo(key2);
    }

}
