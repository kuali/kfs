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
package org.kuali.kfs.lookup;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.util.ParameterDetailTypeUtils;

public class ParameterDetailTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger( ParameterDetailTypeLookupableHelperServiceImpl.class );
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {

        List<BusinessObject> baseLookup = (List<BusinessObject>)super.getSearchResults(fieldValues);
        
        // all step beans
        // all BO beans
        // all trans doc beans 

        List<ParameterDetailType> components = ParameterDetailTypeUtils.getDDComponents();
        
        String activeCheck = fieldValues.get("active");
        if ( activeCheck == null ) {
            activeCheck = "";
        }
        int maxResultsCount = LookupUtils.getApplicationSearchResultsLimit();
        // only bother with the component lookup if returning active components
        if ( baseLookup instanceof CollectionIncomplete && !activeCheck.equals( "N" ) ) {
            long originalCount = Math.max(baseLookup.size(), ((CollectionIncomplete)baseLookup).getActualSizeIfTruncated() );
            long totalCount = originalCount;
            Pattern detailTypeRegex = null;
            Pattern namespaceRegex = null;
            Pattern nameRegex = null;
            
            if ( StringUtils.isNotBlank( fieldValues.get("parameterDetailTypeCode") ) ) {
                String patternStr = fieldValues.get("parameterDetailTypeCode").replace("*", ".*").toUpperCase();
                try {
                    detailTypeRegex = Pattern.compile(patternStr);
                } catch ( PatternSyntaxException ex ) {
                    LOG.error( "Unable to parse parameterDetailTypeCode pattern, ignoring.", ex );
                }                
            }
            if ( StringUtils.isNotBlank( fieldValues.get("parameterNamespaceCode") ) ) {
                String patternStr = fieldValues.get("parameterNamespaceCode").replace("*", ".*").toUpperCase();
                try {
                    namespaceRegex = Pattern.compile(patternStr);
                } catch ( PatternSyntaxException ex ) {
                    LOG.error( "Unable to parse parameterNamespaceCode pattern, ignoring.", ex );
                }                
            }
            if ( StringUtils.isNotBlank( fieldValues.get("parameterDetailTypeName") ) ) {
                String patternStr = fieldValues.get("parameterDetailTypeName").replace("*", ".*").toUpperCase();
                try {
                    nameRegex = Pattern.compile(patternStr);
                } catch ( PatternSyntaxException ex ) {
                    LOG.error( "Unable to parse parameterDetailTypeName pattern, ignoring.", ex );
                }                
            }
            for ( ParameterDetailType pdt : components ) {
                boolean includeType = true;
                if ( detailTypeRegex != null ) {
                    includeType = detailTypeRegex.matcher( pdt.getParameterDetailTypeCode().toUpperCase() ).matches();
                }
                if ( includeType && namespaceRegex != null ) {
                    includeType = namespaceRegex.matcher( pdt.getParameterNamespaceCode().toUpperCase() ).matches();
                }
                if ( includeType && nameRegex != null ) {
                    includeType = nameRegex.matcher( pdt.getParameterDetailTypeName().toUpperCase() ).matches();
                }
                if ( includeType ) {
                    if ( totalCount < maxResultsCount ) {
                        baseLookup.add( pdt );
                    }
                    totalCount++;
                }
            }
            if ( totalCount > maxResultsCount ) {
                ((CollectionIncomplete)baseLookup).setActualSizeIfTruncated(totalCount);
            } else {
                ((CollectionIncomplete)baseLookup).setActualSizeIfTruncated(0L);
            }
        }
        
        return baseLookup;
    }
}
