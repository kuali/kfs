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
package org.kuali.kfs.module.tem.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.tem.test.infrastructure.GenericSeleniumDelegate;
import org.kuali.kfs.module.tem.test.infrastructure.TemSeleniumTestBase;

public class TravelAuthorizationDocumentTest extends TemSeleniumTestBase {
    GenericSeleniumDelegate page;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        page = new GenericSeleniumDelegate(driver);
    }
    
    @After
    public void tearDown() throws Exception {
        
        super.tearDown();
    }

	@Test
	public void testSaveEmptyTA() {
		selenium.open("/kfs-dev/portal.do");
		selenium.click("link=Main Menu");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Travel Authorization");
		selenium.waitForPageToLoad("30000");
        selenium.selectFrame("iframeportlet");
		selenium.click("methodToCall.performLookup.(!!org.kuali.kfs.module.tem.businessobject.TravelerDetail!!).(((principalName:document.traveler.principalName,principalId:document.traveler.principalId,customerNumber:document.traveler.customerNumber,lastName:document.traveler.lastName,firstName:document.traveler.firstName,streetAddressLine1:document.traveler.streetAddressLine1,streetAddressLine2:document.traveler.streetAddressLine2,cityName:document.traveler.cityName,stateCode:document.traveler.stateCode,zipCode:document.traveler.zipCode,countryCode:document.traveler.countryCode,phoneNumber:document.traveler.phoneNumber,travelerTypeCode:document.traveler.travelerTypeCode,emailAddress:document.traveler.emailAddress))).((`document.traveler.travelerTypeCode:travelerTypeCode`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
		selenium.waitForPageToLoad("30000");
		selenium.type("principalName", "mylarge");
        page.getElementByXPath("//input[@name='methodToCall.search']").click();
        // selenium.click("methodToCall.search");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=return value");
		selenium.waitForPageToLoad("30000");
		selenium.type("//input[@id='document.traveler.streetAddressLine1']", "blah");
		selenium.type("//input[@id='document.traveler.cityName']", "blah");
		selenium.type("//input[@id='document.traveler.zipCode']", "55555");
		selenium.select("//select[@id='document.traveler.stateCode']", "label=FL");
		selenium.select("//select[@id='document.traveler.countryCode']", "label=Australia");
		selenium.select("//select[@id='document.tripTypeCode']", "label=Out of State");
		selenium.click("methodToCall.performLookup.(!!org.kuali.kfs.module.tem.businessobject.PerDiem!!).(((id:document.initialPerDiemId))).((`document.initialPrimaryDestination:primaryDestination,document.tripBegin:seasonBeginDate,document.tripTypeCode:tripTypeCode`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
		selenium.waitForPageToLoad("30000");
		selenium.type("seasonBeginDate", "");
		page.getElementByXPath("//input[@name='methodToCall.search']").click();
		selenium.waitForPageToLoad("30000");
		selenium.click("//table[@id='row']/tbody/tr[3]/td[1]/a");
		selenium.waitForPageToLoad("30000");
		selenium.click("methodToCall.save");
		selenium.waitForPageToLoad("30000");
	}

}
