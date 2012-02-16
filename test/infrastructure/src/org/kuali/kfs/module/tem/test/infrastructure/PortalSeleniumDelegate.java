/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.test.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.util.NumberUtils;
import org.kuali.rice.test.web.HtmlUnitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;

public class PortalSeleniumDelegate {
    
    protected static String DEFAULT_USER = "quickstart";
    
    private static String BROWSER_PROTOCOL = "http";
    private static String BROWSER_ADDRESS = "127.0.0.1";
    private static String PORTAL_ADDRESS = "kc-dev/portal.jsp";
    
    private static String RESEARCHER_TAB_TITLE = "Researcher";
    private static String UNIT_TAB_TITLE = "Unit";
    private static String CENTRAL_ADMIN_TAB_TITLE = "Central Admin";
    private static String MAINTENANCE_TAB_TITLE = "Maintenance";
    private static String SYSTEM_ADMIN_TAB_TITLE = "System Admin";
    
    private static String HELP_PAGE_TITLE = "Kuali Research Administration Online Help";
    
    private static String CREATE_MAINTENANCE_DOCUMENT_LINK = "maintenance.do?businessObjectClassName=%s&methodToCall=start";
    
    private static String METHOD_TO_CALL_PREFIX = "methodToCall.";
    private static String SHOW_ALL_TABS_BUTTON = METHOD_TO_CALL_PREFIX + "showAllTabs";
    private static String HIDE_ALL_TABS_BUTTON = METHOD_TO_CALL_PREFIX + "hideAllTabs";
    private static String TOGGLE_TAB_BUTTON = METHOD_TO_CALL_PREFIX + "toggleTab";
    private static String SAVE_BUTTON = METHOD_TO_CALL_PREFIX + "save";
    private static String RELOAD_BUTTON = METHOD_TO_CALL_PREFIX + "reload";
    private static String CLOSE_BUTTON = METHOD_TO_CALL_PREFIX + "close";
    private static String ROUTE_BUTTON = METHOD_TO_CALL_PREFIX + "route";
    private static String APPROVE_BUTTON = METHOD_TO_CALL_PREFIX + "approve";
    private static String BLANKET_APPROVE_BUTTON = METHOD_TO_CALL_PREFIX + "blanketApprove";
    
    private static String ERRORS_FOUND_ON_PAGE = "error(s) found on page";
    private static String SAVE_SUCCESS_MESSAGE = "Document was successfully saved";
    private static String ROUTE_SUCCESS_MESSAGE = "Document was successfully submitted";
    private static String SUBMIT_SUCCESS_MESSAGE = "Document was successfully approved";
    
    private WebDriver driver;
    private GenericSeleniumDelegate genericUiUtil;
    
    protected PortalSeleniumDelegate(WebDriver driver) {
        this.driver = driver;
        genericUiUtil = new GenericSeleniumDelegate(driver);
    }
    
    /**
     * Checks for the Login web page and if it exists, logs in as the default user.
     */
    public void login() {
        if (StringUtils.equals(driver.getTitle(), "Login")) {
            genericUiUtil.set("__login_user", DEFAULT_USER);
            genericUiUtil.click("//input[@value='Login']");
        }
    }
    
    /**
     * Logs in as the backdoor user {@code loginUser}.
     */
    public void login(String loginUser) {
        // clickResearcherTab();

        genericUiUtil.set("backdoorId", loginUser);
        genericUiUtil.click("imageField");
    }
    
    
    /**
     * Do a document search looking for the a specific document based upon its document number.  The following occurs on a Document Search:
     * <ol>
     * <li>The Portal Page is opened</li>
     * <li>The Doc Search button is clicked on</li>
     * <li>In the Doc Search web page, the document number is filled in with the given value</li>
     * <li>The first item in the results is returned</li>
     * <li>The document number link is clicked on</li>
     * </ol>
     *
     * @param documentNumber the document number to search for
     */
    public void docSearch(String documentNumber) {
        openPortalPage();
        
        genericUiUtil.click("Document Search");
        
        genericUiUtil.set("routeHeaderId", documentNumber);
        
        genericUiUtil.click("methodToCall.search");
        
        genericUiUtil.click(documentNumber, true);
    }
    
    /**
     * Opens the Portal Web Page. The portal page is the starting point for many web tests in order to simulate a user.
     */
    public void openPortalPage() {
        driver.get(BROWSER_PROTOCOL + "://" + BROWSER_ADDRESS + ":" + HtmlUnitUtil.getPort().toString() + "/" + PORTAL_ADDRESS);
    }
    
    /**
     * Attempts to switch to KC's inner frame, named 'iframeportlet'.
     *
     * @return true if the driver successfully switched to the inner frame, false otherwise
     */
    public boolean switchToIFramePortlet() {
        boolean switchToIFramePortletSuccessful = true;
        
        try {
            driver.switchTo().frame("iframeportlet");
        } catch (Exception e) {
            switchToIFramePortletSuccessful = false;
        }
        
        return switchToIFramePortletSuccessful;
    }
}