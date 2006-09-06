/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */
package org.kuali.module.kra.budget.bo;

// import com.sun.rsasign.s;
import org.kuali.core.bo.KualiCodeBase;

/**
 * 
 * This class...
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class NonpersonnelSubCategory extends KualiCodeBase implements Comparable {

    private static final long serialVersionUID = 992811943219411565L;

    public NonpersonnelSubCategory() {
        super();
    }

    public NonpersonnelSubCategory(String nonpersonnelSubCategoryCode) {
        super();
        super.setCode(nonpersonnelSubCategoryCode);
    }

    private boolean nonpersonnelMtdcExcludedIndicator;
    private boolean nonpersonnelModularExcludedIndicator;

    /**
     * @return Returns the excluded.
     */
    public boolean isNonpersonnelMtdcExcludedIndicator() {
        return nonpersonnelMtdcExcludedIndicator;
    }

    /**
     * @param excluded The excluded to set.
     */
    public void setNonpersonnelMtdcExcludedIndicator(boolean excluded) {
        this.nonpersonnelMtdcExcludedIndicator = excluded;
    }

    /**
     * Gets the nonpersonnelModularExcludedIndicator attribute.
     * 
     * @return Returns the nonpersonnelModularExcludedIndicator.
     */
    public boolean isNonpersonnelModularExcludedIndicator() {
        return nonpersonnelModularExcludedIndicator;
    }

    /**
     * Sets the nonpersonnelModularExcludedIndicator attribute value.
     * 
     * @param nonpersonnelModularExcludedIndicator The nonpersonnelModularExcludedIndicator to set.
     */
    public void setNonpersonnelModularExcludedIndicator(boolean nonpersonnelModularExcludedIndicator) {
        this.nonpersonnelModularExcludedIndicator = nonpersonnelModularExcludedIndicator;
    }

    public int compareTo(Object o) {
        return super.getName().compareTo(((NonpersonnelSubCategory) o).getName());
    }
}
