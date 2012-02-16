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

public class KualiDocumentSeleniumDelegate {
    
    private static String CREATE_MAINTENANCE_DOCUMENT_LINK = "maintenance.do?businessObjectClassName=%s&methodToCall=start";

    private static String HELP_PAGE_TITLE = "Kuali Research Administration Online Help";

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
    private PortalSeleniumDelegate portal;

    protected KualiDocumentSeleniumDelegate(WebDriver driver) {
        this.driver = driver;
        genericUiUtil = new GenericSeleniumDelegate(driver);
        portal = new PortalSeleniumDelegate(driver);
    }
    
    
    /**
     * Click the Expand All button.
     */
    public void clickExpandAll() {
        genericUiUtil.click(SHOW_ALL_TABS_BUTTON);
    }
    
    /**
     * Click the Collapse All button.
     */
    public void clickCollapseAll() {
       genericUiUtil.click(HIDE_ALL_TABS_BUTTON);
    }

    /**
     * Opens the tab with id containing {@code tabTitle} on the web page.  The {@code tabTitle} is similar to the display text of the tab but has all non-word
     * characters removed.  It is also used in the id of the element, where it is the text between "tab-" and "-imageToggle".  For formatting purposes, 
     * {@code tabTitle} can be separated with spaces which will be removed on search.
     *
     * @param tabTitle the title of the tab on the web page
     */
    public void openTab(final String tabTitle) {
        WebElement tab = new ElementExistsWaiter(driver, "Tab with title " + tabTitle + " not found on page").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementById(getTabId(tabTitle));
                }
            }
        );

        clickTab(tab, "open");
    }
    
    /**
     * Opens the tab with index {@code index} on the web page.  The {@code index} should be a number between {@code 0} and the number of active 
     * tabs on the page.  It does not count inactive hidden tabs on the page.
     *
     * @param index the index of the tab on the web page
     */
    public void openTab(final int index) {
        WebElement tab = new ElementExistsWaiter(driver, "Tab with index " + index + " not found on page").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement tab = null;

                    List<WebElement> tabs = genericUiUtil.getElementsByName(TOGGLE_TAB_BUTTON, false);
                    if (0 <= index && index < tabs.size()) {
                        tab = tabs.get(index);
                    }

                    return tab;
                }
            }
        );
        
        clickTab(tab, "open");
    }

    /**
     * Closes the tab with id containing {@code tabTitle} on the web page.  The {@code tabTitle} is similar to the display text of the tab but has all non-word
     * characters removed.  It is also used in the id of the element, where it is the text between "tab-" and "-imageToggle".  For formatting purposes, 
     * {@code tabTitle} can be separated with spaces which will be removed on search.
     *
     * @param tabTitle the title of the tab on the web page
     */
    public void closeTab(final String tabTitle) {
        WebElement tab = new ElementExistsWaiter(driver, "Tab with title " + tabTitle + " not found on page").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementById(getTabId(tabTitle));
                }
            }
        );

        clickTab(tab, "close");
    }

    /**
     * Closes the tab with index {@code index} on the web page.  The {@code index} should be a number between {@code 0} and the number of active 
     * tabs on the page.  It does not count inactive hidden tabs on the page.
     *
     * @param index the index of the tab on the web page
     */
    public void closeTab(final int index) {
        WebElement tab = new ElementExistsWaiter(driver, "Tab with index " + index + " not found on page").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    WebElement tab = null;
                    
                    List<WebElement> tabs = genericUiUtil.getElementsByName(TOGGLE_TAB_BUTTON, false);
                    if (0 <= index && index < tabs.size()) {
                        tab = tabs.get(index);
                    }
                    
                    return tab;
                }
            }
        );

        clickTab(tab, "close");
    }
    
    /**
     * Returns the generated id of a tab based on the {@code tabTitle}, which appears between "tab-" and "-imageToggle" and without whitespace.
     * 
     * @param tabTitle the title of the tab on the web page
     */
    public String getTabId(final String tabTitle) {
        return "tab-" + StringUtils.deleteWhitespace(tabTitle) + "-imageToggle";
    }
    
    /**
     * Clicks the {@code tab} that contains the text {@code command} (typically 'open' or 'close').
     * 
     * @param tab the tab to click
     * @param command the instruction to either open or close the tab
     */
    public void clickTab(final WebElement tab, final String command) {
        new ElementExistsWaiter(driver, "Cannot " + command + " given tab").until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    boolean isClicked = false;
                    
                    String tabTitle = tab.getAttribute("title");
                    if (StringUtils.contains(tabTitle, command)) {
                        tab.click();
                    } else {
                        isClicked = true;
                    }
                    
                    return isClicked;
                }
            }
        );
    }
            
    /**
     * Gets the document number from a document's web page.  It is expected to be in an HTML table in a table labeled "headerarea".
     *
     * @return the document's number
     */
    public String getDocumentNumber() {
        final String locator = "//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]";
        
        WebElement documentNumber = new ElementExistsWaiter(driver, locator + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementByXPath(locator);
                }
            }
        );
        
        return documentNumber.getText();
    }
        
    /**
     * Performs a single value Lookup.  The following occurs on a Lookup:
     * <ol>
     * <li>The Lookup button is clicked on</li>
     * <li>In the Lookup web page, the search button is clicked on</li>
     * <li>The first item in the results is returned</li>
     * <li>The web page resulting from clicking on "Return Value" is returned</li>
     * </ol>
     * To find the Lookup button, the name attribute of all Lookup buttons are examined to see if it contains the given {@code tag}.  Make sure to pick 
     * a {@code tag} that is unique for that Lookup.
     * <p>
     * Using any of the {@code lookup()} methods is the preferred way to perform a lookup due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the lookup will be performed.
     *
     * @param tag identifies the Lookup button to click on
     */
    public void lookup(final String tag) {
       lookup(tag, null, null);
    }

    /**
     * Performs a single value Lookup.  The following occurs on a Lookup:
     * <ol>
     * <li>The Lookup button is clicked on</li>
     * <li>In the Lookup web page, the given field is filled in with the given value</li>
     * <li>In the Lookup web page, the search button is clicked on</li>
     * <li>The first item in the results is returned</li>
     * </ol>
     * To find the Lookup button, the name attribute of all Lookup buttons are examined to see if it contains the given {@code tag}.  Make sure to pick 
     * a {@code tag} that is unique for that Lookup.
     * <p>
     * Using any of the {@code lookup()} methods is the preferred way to perform a lookup due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the lookup will be performed.
     *
     * @param tag identifies the Lookup button to click on
     * @param searchFieldId the id of the search field (may be null)
     * @param searchFieldValue the value to insert into the search field (may be null if id is null)
     */
    public void lookup(final String tag, final String searchFieldId, final String searchFieldValue) {    
        clickLookup(tag);

        if (searchFieldId != null) {
            assertNotNull("searchValue is null", searchFieldValue);
            genericUiUtil.set(searchFieldId, searchFieldValue);
        }

        genericUiUtil.click("methodToCall.search");

        genericUiUtil.assertTableCellValue("row", 0, 0, "return value");
        
        genericUiUtil.click("return value");
    }

    /**
     * Performs a multiple value Lookup.  The following occurs on a Lookup:
     * <ol>
     * <li>The Lookup icon is clicked on</li>
     * <li>In the Lookup web page, the search button is clicked on</li>
     * <li>The "Select All from All Pages" button is clicked on</li>
     * <li>The web page resulting from clicking on "Return Selected" is returned</li>
     * </ol>
     * To find the Lookup button, the name attribute of all Lookup buttons are examined to see if it contains the given {@code tag}.  Make sure to pick 
     * a {@code tag} that is unique for that Lookup.
     * <p>
     * Using any of the {@code multiLookup()} methods is the preferred way to perform a lookup due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the lookup will be performed.
     * 
     * @param tag identifies the Lookup button to click on
     */
    public void multiLookup(final String tag) {
        multiLookup(tag, null, null);
    }

    /**
     * Performs a multiple value Lookup.  The following occurs on a Lookup:
     * <ol>
     * <li>The Lookup icon is clicked on</li>
     * <li>The search field is filled in with the given search value.</li>
     * <li>In the Lookup web page, the search button is clicked on</li>
     * <li>The "Select All from All Pages" button is clicked on</li>
     * <li>The web page resulting from clicking on "Return Selected" is returned</li>
     * </ol>
     * To find the Lookup button, the name attribute of all Lookup buttons are examined to see if it contains the given {@code tag}.  Make sure to pick 
     * a {@code tag} that is unique for that Lookup.
     * <p>
     * Using any of the {@code multiLookup()} methods is the preferred way to perform a lookup due to the login process.  If the login web page is 
     * encountered, the user will be automatically logged in and the lookup will be performed.
     *
     * @param tag identifies the Lookup button to click on
     * @param searchFieldId the id of the search field (may be null)
     * @param searchFieldValue the value to insert into the search field (may be null if id is null)
     */
    public void multiLookup(final String tag, final String searchFieldId, final String searchFieldValue) {
        clickLookup(tag);

        if (searchFieldId != null) {
            assertNotNull("searchValue is null", searchFieldValue);
            genericUiUtil.set(searchFieldId, searchFieldValue);
        }

        genericUiUtil.click("methodToCall.search");
        
        genericUiUtil.assertTableCellValue("row", 0, 1, searchFieldValue);
        
        genericUiUtil.click("methodToCall.selectAll");

        genericUiUtil.click("methodToCall.prepareToReturnSelectedResults");
    }
    
    /**
     * Clicks a Lookup element that has a name attribute containing {@code tag}.
     *
     * @param tag identifies the Lookup button to click on
     */
    public void clickLookup(final String tag) {
        final String locator = "//input[starts-with(@name,'methodToCall.performLookup') and contains(@name,'" + tag + "')]";

        WebElement lookup = new ElementExistsWaiter(driver, locator + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementByXPath(locator);
                }
            }
        );
        
        lookup.click();

        portal.login();
    }
    
    /**
     * Creates a new maintenance document based on {@code className} and verifies that the next page has the title {@code nextPageTitle}.
     * 
     * @param className the BO class name of this maintenance document
     * @param nextPageTitle the title of the maintenance document on the next page
     */
    public void createNewMaintenanceDocument(final String className, final String nextPageTitle) {
        final String locator = "//a[@href = '" + String.format(CREATE_MAINTENANCE_DOCUMENT_LINK, className) + "']";
        
        WebElement createNewButton = new ElementExistsWaiter(driver, locator + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementByXPath(locator);
                }
            }
        );
        
        createNewButton.click();
        
        portal.login();
        
        if (nextPageTitle != null) {
            assertTitleContains(nextPageTitle);
        }
    }
    
    /**
     * Reload a document by clicking on the Reload button.
     */
    public void reloadDocument() {
        waitForFormLoad();
        genericUiUtil.click(RELOAD_BUTTON);
    }

    /**
     * Save a document by clicking on the Save button.
     */
    public void saveDocument() {
        waitForFormLoad();
        genericUiUtil.click(SAVE_BUTTON);
    }
    
    /**
     * Closes a document without saving.
     */
    public void closeDocument() {
        waitForFormLoad();
        closeDocument(false);
    }

    /**
     * Closes a document, optionally saving if {@code save} is set to true.
     *
     * @param save whether or not the document should be saved before closing
     */
    public void closeDocument(final boolean save) {
        if (save) {
            saveDocument();
        }
        
        genericUiUtil.click(CLOSE_BUTTON);
        // TODO: this may or may not show up depending on the document, but getElement will never return null...
        if (genericUiUtil.getElement("methodToCall.processAnswer.button1", true) != null) {
            genericUiUtil.click("methodToCall.processAnswer.button1");
        }
    }

    /**
     * Saves and closes document and then performs a document search to retrieve the document.
     */
    public void closeAndSearchDocument() {
        String documentNumber = getDocumentNumber();
        
        closeDocument(true);

        portal.docSearch(documentNumber);
    }
    
    /**
     * Routes the document.
     */
    public void routeDocument() {
        waitForFormLoad();
        genericUiUtil.click(ROUTE_BUTTON);
    }
    
    /**
     * Approves the document.
     */
    public void approveDocument() {
        waitForFormLoad();
        genericUiUtil.click(APPROVE_BUTTON);
    }
    
    /**
     * Blanket approves the document.
     */
    public void blanketApproveDocument() {
        waitForFormLoad();
        genericUiUtil.click(BLANKET_APPROVE_BUTTON);
    }
    
    /**
     * Asserts that the document has been saved with no errors.
     */
    public void assertSave() {
        genericUiUtil.assertPageDoesNotContain(ERRORS_FOUND_ON_PAGE);
        genericUiUtil.assertPageContains(SAVE_SUCCESS_MESSAGE);
    }
    
    /**
     * Asserts that the document has been routed with no errors.
     */
    public void assertRoute() {
        genericUiUtil.assertPageContains(ROUTE_SUCCESS_MESSAGE);
    }
    
    /**
     * Asserts that the document has been approved with no errors.
     */
    public void assertApprove() {
        genericUiUtil.assertPageContains(SUBMIT_SUCCESS_MESSAGE);
    }
        
    /**
     * Asserts that the web page title contains {@code title}.
     * 
     * @param title the title to look for in the web page.
     */
    public void assertTitleContains(final String title) {
        String pageSource = driver.getPageSource();
        
        if (!StringUtils.contains(pageSource, title)) {
            if (portal.switchToIFramePortlet()) {
                pageSource = driver.getPageSource();
            }
        }

        assertTrue("Page does not contain " + title, StringUtils.contains(pageSource, title));
    }
    
    /**
     * Asserts that the web page title does <b>not</b> contain {@code title}.
     * 
     * @param title the title to look for in the web page.
     */
    public void assertTitleDoesNotContain(final String title) {
        String pageSource = driver.getPageSource();
        
        if (StringUtils.contains(pageSource, title)) {
            if (portal.switchToIFramePortlet()) {
                pageSource = driver.getPageSource();
            }
        }

        assertTrue("Page contains" + title, !StringUtils.contains(pageSource, title));
    }
    
    /**
     * Asserts that the Expanded Text Area is providing a popup window in which to change its value.  Verifies that the that this is working properly by 
     * performing the following:
     * <ol>
     * <li>The text area is set to the {@code originalText} value</li>
     * <li>The pencil button is clicked on, opening in a popup window</li>
     * <li>The text in the popup window is examined to verify that it is equal to {@code originalText}</li>
     * <li>The popup window text area is changed to {@code expandedAreaText}</li>
     * <li>The "Continue" button is clicked on, closing the popup window</li>
     * <li>The resulting web page is examined to verify that the text area has changed to the value of {@code expandedAreaText}</li>
     * </ol>
     *
     * @param textAreaId identifies the text area
     * @param originalText the string to set the original text area to
     * @param expandedAreaText the string to set in the popup window text area
     */
    public void assertExpandedTextArea(final String textAreaId, final String originalText, final String expandedAreaText) {
        genericUiUtil.set(textAreaId, originalText);
        
        final String locator = "//input[starts-with(@name,'methodToCall.updateTextArea') and contains(@name, '" + textAreaId + "')]";
        
        WebElement textAreaButton = new ElementExistsWaiter(driver, "Expand button for " + textAreaId + " not found").until(
            new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    return genericUiUtil.getElementByXPath(locator);
                }
            }
        );

        textAreaButton.click();
        
        driver.switchTo().window("null");
        
        assertEquals(originalText, genericUiUtil.get(textAreaId));

        genericUiUtil.set(textAreaId, expandedAreaText);
        
        genericUiUtil.click("methodToCall.postTextAreaToParent");

        assertEquals(expandedAreaText, genericUiUtil.get(textAreaId));
    }
    
    /**
     * Asserts that all of the Help links on a web page (identified by the {@code helpWindow} target) are bringing up a page with the appropriate
     * Help Page title.
     */
    public void assertHelpLinks() {
        List<WebElement> helpLinks = genericUiUtil.getElementsByXPath("//node()[@target='helpWindow']");
        for (WebElement helpLink : helpLinks) {
            helpLink.click();
            assertTitleContains(HELP_PAGE_TITLE);
        }
    }
    
    /**
     * Asserts that the page contains one or more errors.
     */
    public void assertPageErrors() {
        clickExpandAll();
        
        new ElementExistsWaiter(driver, "Page does not contain errors").until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return genericUiUtil.getElementByText(ERRORS_FOUND_ON_PAGE) != null 
                        || genericUiUtil.getElementByText("Errors Found in Document") != null 
                        || genericUiUtil.getElementByText("Kuali :: Incident Report") != null;
                }
            }
        );
    }
    
    /**
     * Asserts that the page contains no errors.
     */
    public void assertNoPageErrors() {
        clickExpandAll();
        
        new ElementDoesNotExistWaiter(driver, "Page contains errors").until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return genericUiUtil.getElementByText(ERRORS_FOUND_ON_PAGE) != null 
                        || genericUiUtil.getElementByText("Errors Found in Document") != null 
                        || genericUiUtil.getElementByText("Kuali :: Incident Report") != null;
                }
            }
        );
    }
    
    /**
     * Asserts that one or more of the errors contained in {@code panelId} contains {@code expectedText}.
     *
     * @param panelId the id attribute of the panel
     * @param text the string to look for in the errors
     */
    public void assertError(final String panelId, final String expectedText) {
        clickExpandAll();
        
        List<String> errorValues = new ArrayList<String>();
        for (WebElement error : genericUiUtil.getErrors(panelId)) {
            errorValues.add(error.getValue());
        }
        
        assertTrue("Errors in " + panelId + " do not contain " + expectedText, errorValues.contains(expectedText));
    }

    /**
     * Asserts that there are {@code expectedErrorCount} errors contained in {@code panelId}.
     *
     * @param panelId the id attribute of the panel
     * @param expectedErrorCount the number of errors expected on the page
     */
    public void assertErrorCount(final String panelId, final int expectedErrorCount) {
        clickExpandAll();
        
        List<WebElement> errors = genericUiUtil.getErrors(panelId);
        assertEquals("Error count of " + errors.size() + " did not match the expected error count of " + expectedErrorCount, expectedErrorCount, errors.size());
    }
            
    /**
     * Waits for the form to load by checking for the existence of "formComplete."
     */
    public void waitForFormLoad() {
        new ElementExistsWaiter(driver, "Page did not load").until(
            new Function<WebDriver, Boolean>() {
                public Boolean apply(WebDriver driver) {
                    boolean isFormComplete = false;
                    
                    WebElement element = driver.findElement(By.id("formComplete"));
                    if (element != null) {
                        isFormComplete = true;
                    }
                    
                    return isFormComplete;
                }
            }
        );
    }    
}