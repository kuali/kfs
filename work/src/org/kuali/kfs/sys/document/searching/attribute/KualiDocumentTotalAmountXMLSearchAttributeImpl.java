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
package org.kuali.workflow.attribute;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.kuali.module.purap.PurapConstants;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.docsearch.DocSearchUtils;
import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.docsearch.SearchableAttributeFloatValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeStringValue;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;

/**
 * This class...
 */
public class KualiDocumentTotalAmountXMLSearchAttributeImpl extends KualiXmlSearchableAttributeImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentTotalAmountXMLSearchAttributeImpl.class);
    
    private static final String FIELD_DEF_NAME = "";

    /**
     * This method will take the search storage values from the {@link org.kuali.workflow.attribute.KualiXmlSearchableAttributeImpl} method
     * and either add or translate 
     */
    @Override
    public List getSearchStorageValues(String docContent) {
        List<SearchableAttributeValue> newSearchAttributeValues = new ArrayList();
        List<SearchableAttributeValue> superList = super.getSearchStorageValues(docContent);
        if (superList.isEmpty()) {
            SearchableAttributeFloatValue attValue = generateZeroDollarSearchableAttributeValue();
            newSearchAttributeValues.add(attValue);
        } else {
            for (Iterator iter = superList.iterator(); iter.hasNext();) {
                SearchableAttributeValue searchableValue = (SearchableAttributeValue) iter.next();
                if ( (FIELD_DEF_NAME.equals(searchableValue.getSearchableAttributeKey())) && (searchableValue.getSearchableAttributeValue() == null) ) {
                    searchableValue.setupAttributeValue("0.00");
                }
                newSearchAttributeValues.add(searchableValue);
            }
        }
        return newSearchAttributeValues;
    }
    
    private SearchableAttributeFloatValue generateZeroDollarSearchableAttributeValue() {
        SearchableAttributeFloatValue sav = new SearchableAttributeFloatValue();
        sav.setSearchableAttributeKey(FIELD_DEF_NAME);
        sav.setSearchableAttributeValue(new Float(0.00));
        return sav;
    }

}
