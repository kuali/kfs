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
package org.kuali.rice.kns.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.util.ActionFormUtilMap;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.pojo.PojoFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class common properites for all action forms.
 */
public class KualiForm extends PojoFormBase {
    /**
     * Tab state UI literals
     */
    public static enum TabState {
        OPEN,
        CLOSE
    }

    private static final long serialVersionUID = 1L;

    private static final String literalPrefixAndDelimiter =
    	KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX+ KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER;

    private String backLocation;
    private String methodToCall;
    private String refreshCaller;
    private String anchor;
    private Map<String, String> tabStates;
    private Map actionFormUtilMap;
    private Map displayedErrors = new HashMap();
    private Map<String, Object> displayedWarnings = new HashMap<String, Object>();
    private Map<String, Object> displayedInfo = new HashMap<String, Object>();
    private int currentTabIndex = 0;
    private int arbitrarilyHighIndex = 1000000;

    private String navigationCss;
    private HeaderNavigation[] headerNavigationTabs;
    protected List<ExtraButton> extraButtons = new AutoPopulatingList( ExtraButton.class ) ;

    private boolean fieldLevelHelpEnabled;
    
    private List<HeaderField> docInfo;
    private int numColumns = 2;
    
    private String fieldNameToFocusOnAfterSubmit;
    
    /**
     * @see org.kuali.rice.krad.web.struts.pojo.PojoFormBase#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties(){
    	super.addRequiredNonEditableProperties();
    	registerRequiredNonEditableProperty(KRADConstants.REFRESH_CALLER);
    }
    
    public int getNumColumns() {
        return this.numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public List<HeaderField> getDocInfo() {
        return this.docInfo;
    }

    public void setDocInfo(List<HeaderField> docInfo) {
        this.docInfo = docInfo;
    }

    /**
     * no args constructor which must init our tab states list
     */
    public KualiForm() {
        this.tabStates = new HashMap<String, String>();
        this.actionFormUtilMap = new ActionFormUtilMap();
        this.docInfo = new ArrayList<HeaderField>();
    }

    /**
     * Checks for methodToCall parameter, and if not populated in form calls utility method to parse the string from the request.
     */
    public void populate(HttpServletRequest request) {
        setMethodToCall(WebUtils.parseMethodToCall(this, request));
        
        super.populate(request);

        populateBackLocation(request);
        populateFieldLevelHelpEnabled(request);
        
        if (actionFormUtilMap instanceof ActionFormUtilMap) {
            ((ActionFormUtilMap) actionFormUtilMap).setCacheValueFinderResults(true);
        }
    }
        
    private static Boolean ENABLE_FIELD_LEVEL_HELP_IND;

    protected void populateBackLocation(HttpServletRequest request){
        if (getParameter(request, "returnLocation") != null) {
            setBackLocation(getParameter(request, "returnLocation"));
        }
    }
    
    /**
     * Populates whether the each field will have field-level help associated with it.  Depending on how the jsp/tags are implemented, the value
     * populated by this method may be overruled by other settings
     * 
     * @param request
     */
    protected void populateFieldLevelHelpEnabled(HttpServletRequest request) {
    	if ( ENABLE_FIELD_LEVEL_HELP_IND == null ) {
    		ENABLE_FIELD_LEVEL_HELP_IND = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                    KRADConstants.KNS_NAMESPACE,
                    KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.ENABLE_FIELD_LEVEL_HELP_IND, Boolean.FALSE);
    	}
    	setFieldLevelHelpEnabled( ENABLE_FIELD_LEVEL_HELP_IND);
    }

    public Map getDisplayedErrors() {
        return displayedErrors;
    }

    /**
	 * @return the displayedWarnings
	 */
	public Map<String, Object> getDisplayedWarnings() {
		return this.displayedWarnings;
	}

	/**
	 * @return the displayedInfo
	 */
	public Map<String, Object> getDisplayedInfo() {
		return this.displayedInfo;
	}

	/**
     * Used by the dispatch action to determine which action method to call into.
     *
     * @return Returns the methodToCall.
     */
    public String getMethodToCall() {
        return methodToCall;
    }


    /**
     * @param methodToCall The methodToCall to set.
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }


    /**
     * Can be used by actions refresh method to determine what called the the refresh method.
     *
     * @return Returns the refreshCaller.
     */
    public String getRefreshCaller() {
        return refreshCaller;
    }


    /**
     * @param refreshCaller The refreshCaller to set.
     */
    public void setRefreshCaller(String refreshCaller) {
        this.refreshCaller = refreshCaller;
    }

    /**
     * @return the tab state list
     */
    public Map<String, String> getTabStates() {
        return tabStates;
    }

    /**
     * simple setter for the tab state Map
     *
     * @param tabStates
     */
    public void setTabStates(Map<String, String> tabStates) {
        this.tabStates = tabStates;
    }

    /**
     * Special getter based on key to work with multi rows for tab state objects
     */
    public String getTabState(String key) {
        String state = KRADConstants.EMPTY_STRING;
        if (tabStates.containsKey(key)) {
            if (tabStates.get(key) instanceof String) {
            	state = tabStates.get(key);
            }
            else {
            	//This is the case where the value is an Array of String,
            	//so we'll have to get the first element
            	Object result = tabStates.get(key);
            	result.getClass();
            	state = ((String[])result)[0];
            }
        }

        return state;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }

    public void incrementTabIndex() {
        this.currentTabIndex++;
    }

    public int getNextArbitrarilyHighIndex() {
        return this.arbitrarilyHighIndex++;
    }
    
	public String getFieldNameToFocusOnAfterSubmit() {
		return this.fieldNameToFocusOnAfterSubmit;
	}

	public void setFieldNameToFocusOnAfterSubmit(String fieldNameToFocusOnAfterSubmit) {
		this.fieldNameToFocusOnAfterSubmit = fieldNameToFocusOnAfterSubmit;
	}

	/**
     * @return Returns the validOptionsMap.
     */
    public Map getActionFormUtilMap() {
        return actionFormUtilMap;
    }

    /**
     * @param validOptionsMap The validOptionsMap to set.
     */
    public void setActionFormUtilMap(Map validOptionsMap) {
        this.actionFormUtilMap = validOptionsMap;
    }

    /**
     * Gets the headerNavigationTabs attribute.
     *
     * @return Returns the headerNavigationTabs.
     */
    public HeaderNavigation[] getHeaderNavigationTabs() {
        return headerNavigationTabs;
    }

    /**
     * Sets the headerNavigationTabs attribute value.
     *
     * @param headerNavigationTabs The headerNavigationTabs to set.
     */
    public void setHeaderNavigationTabs(HeaderNavigation[] headerNavigationTabs) {
        this.headerNavigationTabs = headerNavigationTabs;
    }

    /**
     * Gets the navigationCss attribute.
     *
     * @return Returns the navigationCss.
     */
    public String getNavigationCss() {
        return navigationCss;
    }

    /**
     * Sets the navigationCss attribute value.
     *
     * @param navigationCss The navigationCss to set.
     */
    public void setNavigationCss(String navigationCss) {
        this.navigationCss = navigationCss;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public List<ExtraButton> getExtraButtons() {
        return extraButtons;
    }

    public void setExtraButtons(List<ExtraButton> extraButtons) {
        if ( extraButtons instanceof AutoPopulatingList ) {
            this.extraButtons = extraButtons;
        } else {
            this.extraButtons.clear();
            this.extraButtons.addAll( extraButtons );
        }
    }

    public ExtraButton getExtraButton( int index ) {
        return extraButtons.get( index );
    }

    public void setExtraButton( int index, ExtraButton extraButton ) {
        extraButtons.set( index, extraButton );
    }

    /**
     * Returns whether field level help is enabled for this form.
     * 
     * @return
     */
    public boolean isFieldLevelHelpEnabled() {
        return this.fieldLevelHelpEnabled;
    }

    public void setFieldLevelHelpEnabled(boolean fieldLevelHelpEnabled) {
        this.fieldLevelHelpEnabled = fieldLevelHelpEnabled;
    }
    
    
    /**
     * Retrieves a value from the form for the purposes of passing it as a parameter into the lookup or inquiry frameworks 
     * 
     * @param parameterName the name of the parameter, as expected by the lookup or inquiry frameworks
     * @param parameterValueLocation the name of the property containing the value of the parameter
     * @return the value of the parameter
     */
    public String retrieveFormValueForLookupInquiryParameters(String parameterName, String parameterValueLocation) {
    	// dereference literal values by simply trimming of the prefix
    	if (parameterValueLocation.startsWith(literalPrefixAndDelimiter)) {
    		return parameterValueLocation.substring(literalPrefixAndDelimiter.length());
    	}

    	Object value = ObjectUtils.getPropertyValue(this, parameterValueLocation);
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		Formatter formatter = Formatter.getFormatter(value.getClass());
		return (String) formatter.format(value);	
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.web.struts.pojo.PojoFormBase#shouldPropertyBePopulatedInForm(String, HttpServletRequest)
	 */
	@Override
	public boolean shouldPropertyBePopulatedInForm(
			String requestParameterName, HttpServletRequest request) {
		if (requestParameterName.startsWith(KRADConstants.TAB_STATES)) {
			return true;
		}
		
		if (requestParameterName.equals(KRADConstants.DISPATCH_REQUEST_PARAMETER)) {
			String methodToCallParameterName = request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER);
			if(StringUtils.equals(methodToCallParameterName, KRADConstants.RETURN_METHOD_TO_CALL)){
				return true;
			}
		}
		
		return super.shouldPropertyBePopulatedInForm(requestParameterName, request);
	}
    
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
    	if ("GET".equalsIgnoreCase(request.getMethod())) {
    		return true;
    	}
    	if (shouldPropertyBePopulatedInForm(methodToCallParameterName, request)) {
    		return true;
    	}
    	if (methodToCallParameterName != null && WebUtils.endsWithCoordinates(methodToCallParameterName)) {
    		methodToCallParameterName = methodToCallParameterName.substring(0, WebUtils.getIndexOfCoordinateExtension(methodToCallParameterName));
        	if (shouldPropertyBePopulatedInForm(methodToCallParameterName, request)) {
        		return true;
        	}
    	}
    	if (KRADConstants.METHOD_TO_CALL_PATH.equals(methodToCallParameterName)) {
    		if (shouldPropertyBePopulatedInForm(methodToCallParameterValue, request)) {
    			return true;
    		}
    		if (methodToCallParameterValue != null && WebUtils.endsWithCoordinates(methodToCallParameterName)) {
    			methodToCallParameterValue = methodToCallParameterValue.substring(0, WebUtils
                        .getIndexOfCoordinateExtension(methodToCallParameterName));
    			if (shouldPropertyBePopulatedInForm(methodToCallParameterValue, request)) {
        			return true;
        		}
    		}
    	}
    	return false;
    }

	/**
	 * @see org.kuali.rice.krad.web.struts.pojo.PojoFormBase#clearEditablePropertyInformation()
	 */
	@Override
	public void clearEditablePropertyInformation() {
		super.clearEditablePropertyInformation();
	}
	
	public void setDerivedValuesOnForm(HttpServletRequest request) {
	}

	/**
	 * @see org.apache.struts.action.ActionForm#reset(ActionMapping, HttpServletRequest)
	 */
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		if (extraButtons != null) {
			extraButtons.clear();
		}
		//fieldNameToFocusOnAfterSubmit = "";
		clearDisplayedMessages();
	}

	/**
	 * @see org.apache.struts.action.ActionForm#reset(ActionMapping, ServletRequest)
	 */
	@Override
	public void reset(ActionMapping mapping, ServletRequest request) {
		super.reset(mapping, request);
		if (extraButtons != null) {
			extraButtons.clear();
		}
		//fieldNameToFocusOnAfterSubmit = "";
		clearDisplayedMessages();
	}
	
	private void clearDisplayedMessages() {
		if (displayedErrors != null) {
			displayedErrors.clear();
		}
		if (displayedWarnings != null) {
			displayedWarnings.clear();
		}
		if (displayedInfo != null) {
			displayedInfo.clear();
		}
	}
	
    /**
	 * @return the backLocation
	 */
	public String getBackLocation() {
		return this.backLocation;
	}

	/**
	 * @param backLocation the backLocation to set
	 */
	public void setBackLocation(String backLocation) {
		this.backLocation = backLocation;
	}

}
