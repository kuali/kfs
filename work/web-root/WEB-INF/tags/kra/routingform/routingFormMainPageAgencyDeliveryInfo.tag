<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormMainPageAgencyDeliveryInfo.tag,v $
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>


<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />
<c:set var="routingFormAgencyAttributes" value="${DataDictionary.RoutingFormAgency.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tab tabTitle="Agency/Delivery Info" defaultOpen="false" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

		<div class="tab-container" align="center">
            <div class="tab-container-error"> </div>
            <div class="h2-container">
              <h2>Agency/Delivery Info</h2>
            </div>
            
            <table cellpadding="0" cellspacing="0" summary="view/edit document overview information">
              <tr>
                <th width="20%" align=right valign=middle>Agency:</th>
                <td width="30%"> (select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                <th align=right valign=middle>Due Date: </th>
                <td align=left valign=middle ><kul:htmlControlAttribute property="document.routingFormAgency.proposalDueDate" attributeEntry="${routingFormAgencyAttributes.proposalDueDate}" datePicker="true" /></td>
              </tr>
              <tr>
                <th width="20%" align=right valign=middle>Federal Pass Through: </th>

                <td width="30%" align=left valign=middle >(select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
                <th align=right valign=middle>Due Date Type: </th>

                <td align=left valign=middle ><select name="select">
                    <option selected>select:</option>
                    <option>receipt</option>
                    <option>postmark</option>
                    <option>target</option>
                  </select>
                </td>

              </tr>
              <tr>
                <th align=right valign=middle>Delivery Address: </th>
                <td align=left valign=middle ><textarea name="textfield" cols="20" rows="5"></textarea></td>
                <th align=right valign=middle>Delivery Instructions: </th>
                <td align=left valign=middle ><textarea name="textfield" cols="20" rows="3"></textarea>
                  <br>

                  <input name="checkbox" type="checkbox" class="radio" value="checkbox">
                  Disk to Accompany Proposal<br>
                  <input name="checkbox" type="checkbox" class="radio" value="checkbox">
                  Electronic Submision Required</td>
              </tr>
              <tr>
                <th align=right valign=middle>Program Announcement: </th>

                <td align=left valign=middle ><input name="textfield" type="text" size="20"></td>
                <th align=right valign=middle>Copies to Agency: </th>
                <td align=left valign=middle ><input name="textfield" type="text" size="5">
                  Submit 2 additional copies plus the number of required by your department and school.</td>
              </tr>
              <tr>
                <th align=right valign=middle>CFDA:</th>

                <td colspan="3" align=left valign=middle >(select) <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a> </td>
              </tr>
            </table>
          </div>

</kul:tab>
