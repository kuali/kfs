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
package org.kuali.module.purap.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.purap.PurapConstants;

/**
 * This class returns list of ba fund restriction levels.
 * 
 * 
 */
public class PurchaseOrderVendorQuoteStatusCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.DELV, "Delivery"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.FUIP, "Follow up in Progress"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.IIQ, "Incorrect Items Quoted"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.LEXP, "Lack of Exprience"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.MULT, "Multiple Award"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.NORS, "No Response"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.PTFE, "Payment Terms - Freight"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.RECV, "Received"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.RIR, "Received - Incomplete Response"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.RECL, "Received - Low Bid"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.RNLB, "Received - Not Low Bid - Competitive"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.RNLN, "Received - Not Low Bid - Not Competitive"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.NOBD, "Responded - No Bid"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.SQNA, "Substitutions Quoted - Not Acceptable"));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteStatusCode.TINC, "Technically Incompatible"));

        return keyValues;
    }

}