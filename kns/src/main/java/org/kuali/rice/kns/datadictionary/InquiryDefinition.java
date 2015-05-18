/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.inquiry.Inquirable;

import java.util.ArrayList;
import java.util.List;

/**
    The inquiry element is used to specify the fields that will be displayed on the
    inquiry screen for this business object and the order in which they will appear.

    JSTL: The inquiry element is a Map which is accessed using
    a key of "inquiry".  This map contains the following keys:
        * title (String)
        * inquiryFields (Map)
 */
@Deprecated
public class InquiryDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -2506403061297774668L;
    
	protected String title;
    protected List<InquirySectionDefinition> inquirySections = new ArrayList<InquirySectionDefinition>();
    protected Class<? extends Inquirable> inquirableClass;
    protected Class<? extends InquiryPresentationController> presentationControllerClass;
    protected Class<? extends InquiryAuthorizer> authorizerClass;
    
    protected boolean translateCodes = true;

    public InquiryDefinition() {
    }


    public String getTitle() {
        return title;
    }

    /**
               The title element is used specify the title that will appear in the header
                of an Inquiry or Lookup screen.
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }

        this.title = title;
    }

    /**
     * @return Collection of all inquiryField FieldDefinitions associated with this InquiryDefinition, in the order in which they
     *         were added
     */
    public List<InquirySectionDefinition> getInquirySections() {
        return inquirySections;
    }
   
    /**
     * Returns the FieldDefinition associated with the field attribute name
     * @param fieldName
     * @return
     */
    public FieldDefinition getFieldDefinition(String fieldName) {
        for (InquirySectionDefinition section : inquirySections ) {
            for (FieldDefinition field : section.getInquiryFields() ) {
                if (field.getAttributeName().equals(fieldName)) {
                    return field;
                }
            }
        }
        
        return null;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        for ( InquirySectionDefinition inquirySection : inquirySections ) {
            inquirySection.completeValidation(rootBusinessObjectClass, null);
        }
    }

    public InquirySectionDefinition getInquirySection( String sectionTitle ) {
        for ( InquirySectionDefinition inquirySection : inquirySections ) {
            if ( inquirySection.getTitle().equals(sectionTitle) ) {
                return inquirySection;
            }
        }
        return null;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "InquiryDefinition '" + getTitle() + "'";
    }


    public Class<? extends Inquirable> getInquirableClass() {
        return inquirableClass;
    }

    /**

            inquirableClass is required if a custom inquirable is required which will show
            additional data other than the business object attributes.

            Example from Org.xml:
                <inquirableClass>org.kuali.module.chart.maintenance.OrgInquirable</inquirableClass>
            The custom inquirable is required in this case because the organization hierarchy
            is shown on the inquiry screen.
     */
    public void setInquirableClass(Class<? extends Inquirable> inquirableClass) {
        this.inquirableClass = inquirableClass;
    }

    /**
     *                 inquirySections allows inquiry to be presented in sections.
                Each section can have a different format.
     */
    public void setInquirySections(List<InquirySectionDefinition> inquirySections) {
        this.inquirySections = inquirySections;
    }


	public Class<? extends InquiryPresentationController> getPresentationControllerClass() {
		return this.presentationControllerClass;
	}


	public void setPresentationControllerClass(
			Class<? extends InquiryPresentationController> presentationControllerClass) {
		this.presentationControllerClass = presentationControllerClass;
	}


	public Class<? extends InquiryAuthorizer> getAuthorizerClass() {
		return this.authorizerClass;
	}


	public void setAuthorizerClass(
			Class<? extends InquiryAuthorizer> authorizerClass) {
		this.authorizerClass = authorizerClass;
	}


	public boolean isTranslateCodes() {
		return this.translateCodes;
	}


	public void setTranslateCodes(boolean translateCodes) {
		this.translateCodes = translateCodes;
	}
	
}
