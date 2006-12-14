/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
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
package org.kuali.module.cg.lookup.valueFinder;

import org.kuali.core.bo.OriginationCode;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Returns the next Proposal number available.
 * 
 * 
 */
public class NextProposalNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        final String homeCode = SpringServiceLocator.getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode();
        // todo: NextSequenceNumberService or the following addition to the Kuali Nervous System, for proper transaction
        // return SpringServiceLocator.getOriginationCodeService().getNextCgProposalNumberAndIncrement(homeCode).toString();
        // Until the Kuali project gets around to resolving this, live dangerously without the transaction...
        OriginationCode o = SpringServiceLocator.getOriginationCodeService().getByPrimaryKey(homeCode);
        Long next = o.getNextCgProposalNumber();
        o.setNextCgProposalNumber(next + 1);
        SpringServiceLocator.getOriginationCodeService().save(o);
        return next.toString();
    }
}
