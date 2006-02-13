/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;


public class ObjectCodePreRules extends PreRulesContinuationBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodePreRules.class);
    
    private final static String Q1="objectCodeQuestion";

    public boolean doRules(MaintenanceDocument maintenanceDocument) {

        boolean answer;
        boolean answer2;

        // TODO - externalize a reasonable value into Constants.java
        answer=askOrAnalyzeYesNoQuestion("ObjectCode1"+Q1,"Do you like object codes?");
        if (!answer) {
            answer=askOrAnalyzeYesNoQuestion("ObjectCode3"+"objectCodeQuestion3","Are you sure?");
        }
        answer2=askOrAnalyzeYesNoQuestion("ObjectCode2"+"objectCodeQuestion2","Do you really really like object codes?");

        LOG.debug("You answered: "+answer+","+answer2);
    
        if (!answer && !answer2) {
            abortRulesCheck();
        }

        return true;
        
    }
}
