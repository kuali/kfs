<%--

    Copyright 2005-2014 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<c:set var="showSaveReminder" value="${requestScope.saveReminder}" />
<kul:page headerTitle="Workflow Preferences" lookup="false"
  headerMenuBar="" transactionalDocument="false" showDocumentInfo="false"
  htmlFormAction="Preferences" docTitle="Workflow Preferences" errorKey="*">

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">
  <tbody>
    <tr>
      <td><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
      <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
    </tr>
  </tbody>
</table>

<html-el:hidden property="returnMapping"/>
<div id="workarea" >
  <div class="tab-container" align="center">
    <table width="100%" class="datatable-80" align="center" cellspacing="0">
	    <tbody id="G" style="display: none;"></tbody>
        <tbody id="G448" style="display: none;"></tbody>
        <tbody id="G449" style="display: none;"></tbody>
        <tbody id="G2449" style="display: none;"></tbody>
        <tbody id="G55" style="display: none;"></tbody>
        <tbody id="G56" style="display: none;"></tbody>
        <tbody id="G57" style="display: none;"></tbody>
        <tbody id="G58" style="display: none;"></tbody>
        <tbody id="G538" style="display: none;"></tbody>
        <tr>
		  <td colspan="2" class="subhead" >General</td>
	    </tr>
	    <tr>
		  <th><div align="right">Automatic Refresh Rate:</div></th>
          <td class="datacell"><html-el:text property="preferences.refreshRate" size="3" />
            <kul:checkErrors keyMatch="preferences.refreshRate" />
            <c:if test="${hasErrors}">
              <kul:fieldShowErrorIcon />
            </c:if>
             in whole minutes - 0 is no automatic refresh.</td>
	    </tr>
        <tr>
          <th width="50%"><div align="right">Action List Page Size</div></th>
          <td class="datacell">
            <html-el:text property="preferences.pageSize" size="3" />
            <kul:checkErrors keyMatch="preferences.pageSize" />
            <c:if test="${hasErrors}">
              <kul:fieldShowErrorIcon />
            </c:if>
          </td>
        </tr>
        <tr>
          <th width="50%"><div align="right">Delegator Filter</div></th>
          <td class="datacell">
				<html-el:select property="preferences.delegatorFilter">
				  <html-el:options collection="delegatorFilter" labelProperty="value" property="key"/>
				</html-el:select>
          </td>
        </tr>
        <tr>
          <th width="50%"><div align="right">Primary Delegate Filter</div></th>
          <td class="datacell">
				<html-el:select property="preferences.primaryDelegateFilter">
				  <html-el:options collection="primaryDelegateFilter" labelProperty="value" property="key"/>
				</html-el:select>
          </td>
        </tr>
        <tr>
           <td colspan="2" class="subhead" >Fields Displayed In Action List</td>
        </tr>

        <tr>
          <th ><div align="right">Document Type</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showDocType" value="${Constants.PREFERENCES_YES_VAL }"/></td>
        </tr>

        <tr>
          <th ><div align="right">Title</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showDocTitle" value="${Constants.PREFERENCES_YES_VAL }"/></td>
        </tr>

        <tr>
          <th ><div align="right">ActionRequested</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showActionRequested" value="${Constants.PREFERENCES_YES_VAL }"/></td>
        </tr>

        <tr>
          <th ><div align="right">Initiator</div></th>
			<td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showInitiator" value="${Constants.PREFERENCES_YES_VAL }"/>
          </td>
        </tr>

        <tr>
          <th ><div align="right">Delegator</div></th>
			<td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showDelegator" value="${Constants.PREFERENCES_YES_VAL }"/>
          </td>
        </tr>

        <tr>
          <th ><div align="right">Date Created</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showDateCreated" value="${Constants.PREFERENCES_YES_VAL }"/>
          </td>
        </tr>
		<tr>
          <th ><div align="right">Date Approved</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showDateApproved" value="${Constants.PREFERENCES_YES_VAL }"/>
          </td>
        </tr>
        <tr>
          <th ><div align="right">Current Route Node(s)</div></th>
			<td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showCurrentNode" value="${Constants.PREFERENCES_YES_VAL }"/>
          		</td>
        </tr>
        <tr>
          <th ><div align="right">WorkGroup Request</div></th>
			<td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showWorkgroupRequest" value="${Constants.PREFERENCES_YES_VAL }"/>          </td>
        </tr>

        <tr>
          <th ><div align="right">Document Route Status</div></th>
          <td class="datacell"> <html-el:checkbox styleClass="nobord" property="preferences.showDocumentStatus" value="${Constants.PREFERENCES_YES_VAL }"/>          </td>
        </tr>


        <tr>
          <th ><div align="right">Clear FYI</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.showClearFyi" value="${Constants.PREFERENCES_YES_VAL }"/>
    		</td>
        </tr>

		<c:if test="${KualiForm.showOutbox }">
	        <tr>
	          <th ><div align="right">Use Outbox</div></th>
	          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.useOutbox" value="${Constants.PREFERENCES_YES_VAL }"/>	          </td>
	        </tr>
        </c:if>

        <tr>
          <td colspan="2" class="subhead" >Document Route Status Colors for Actionlist Entries</td>
        </tr>
		<tr>
			<th class="thnormal"><div align="right">Saved</div></th>
			<td>
			  <table style="border:none">
			    <tbody><tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorSaved" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
			  </tbody>
			 </table>
			</td>
		</tr>

		<tr>
		  <th class="thnormal"><div align="right">Initiated</div></th>
		  <td>
		    <table style="border:none">
              <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorInitiated" value="${colorType.key}" /></div></td>
                  </c:forEach>
			    </tr>
              </tbody>
			</table>
	  	  </td>
		</tr>

		<tr>
		  <th class="thnormal"><div align="right">Disapproved</div></th>
		  <td>
			<table style="border:none">
              <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio  styleClass="nobord" property="preferences.colorDisapproved" value="${colorType.key}" /></div></td>
                  </c:forEach>
			    </tr>
              </tbody>
		    </table>
	      </td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Enroute</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorEnroute" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Approved</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorApproved" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Final</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorFinal" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Processed</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorProcessed" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Exception</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorException" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<th class="thnormal"><div align="right">Canceled</div></th>
			<td>
			  <table style="border:none">
                <tbody>
			    <tr>
                  <c:forEach items="${Constants.ACTION_LIST_COLOR_PALETTE}" var="colorType">
		            <td bgcolor='<c:out value="${colorType.value}"/>' style=" border:none; background-color:${colorType.value}"><div align="center"><html-el:radio styleClass="nobord" property="preferences.colorCanceled" value="${colorType.key}" /></div></td>
                  </c:forEach>
				</tr>
                </tbody>
			  </table>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subhead">Email Notification Preferences</td>
		</tr>
		<tr>
          <th><div align="right">Receive Primary Delegate Emails</div></th>
          <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.notifyPrimaryDelegation" value="${Constants.PREFERENCES_YES_VAL}"/></td>
        </tr>
        <tr>
           <th><div align="right">Receive Secondary Delegate Emails</div></th>
           <td class="datacell"><html-el:checkbox styleClass="nobord" property="preferences.notifySecondaryDelegation" value="${Constants.PREFERENCES_YES_VAL}"/></td>
        </tr>
		<tr>
          <th><div align="right">Default Email Notification</div></th>
          <td class="datacell">
            <html-el:select property="preferences.emailNotification">
              <html-el:option value="${Constants.EMAIL_RMNDR_NO_VAL}">None</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_DAY_VAL}">Daily</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_WEEK_VAL}">Weekly</html-el:option>
              <html-el:option value="${Constants.EMAIL_RMNDR_IMMEDIATE}">Immediate</html-el:option>
            </html-el:select>
          </td>
        </tr>
        <tr>
          <th><div align="right">Document Type Notifications</div></th>
          <td class="datacell">
            <c:if test="${showSaveReminder}">
            <div style="color: red; font-weight: bold;"><bean:message key="docType.preference.save.reminder" /></div>
            </c:if>
            <table>
              <tr>
                <th>Document Type</th>
                <th>Notification Preference</th>
                <th>Actions</th>
              </tr>
              <logic:iterate name="KualiForm" property="preferences.documentTypeNotificationPreferences" id="entry" indexId="status">
              <tr>
                <td>
                  <c:set var="documentType"><bean:write name="entry" property="key" /></c:set>
                  ${documentType}
                  <html-el:hidden name="KualiForm" property="preferences.documentTypeNotificationPreference(${fn:replace(documentType, '.', Constants.DOCUMENT_TYPE_NOTIFICATION_DELIMITER)})" />
                </td>
                <td>
                  <c:set var="preferenceValue"><bean:write name="entry" property="value" /></c:set>
                  <c:choose>
                  	<c:when test="${preferenceValue == Constants.EMAIL_RMNDR_NO_VAL}">None</c:when>
                  	<c:when test="${preferenceValue == Constants.EMAIL_RMNDR_DAY_VAL}">Daily</c:when>
                  	<c:when test="${preferenceValue == Constants.EMAIL_RMNDR_WEEK_VAL}">Weekly</c:when>
                  	<c:when test="${preferenceValue == Constants.EMAIL_RMNDR_IMMEDIATE}">Immediate</c:when>
                  </c:choose>
                </td>
                <td>
                  <html:image property="methodToCall.deleteNotificationPreference.${documentType}"
                              src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
                              styleClass="tinybutton"/>
                </td>
              </tr>
              </logic:iterate>
              <tr>
                <td>
                  <html-el:text name="KualiForm" property="documentTypePreferenceName" />
                  <kul:checkErrors keyMatch="documentTypePreferenceName" />
            	  <c:if test="${hasErrors}">
                    <br/><kul:fieldShowErrorIcon />
            	  </c:if>
            	  <kul:lookup boClassName="org.kuali.rice.kew.doctype.bo.DocumentType" fieldConversions="name:documentTypePreferenceName"/>
                </td>
                <td>
                  <html-el:select name="KualiForm" property="documentTypePreferenceValue">
                    <html-el:option value="${Constants.EMAIL_RMNDR_NO_VAL}">None</html-el:option>
                    <html-el:option value="${Constants.EMAIL_RMNDR_DAY_VAL}">Daily</html-el:option>
                    <html-el:option value="${Constants.EMAIL_RMNDR_WEEK_VAL}">Weekly</html-el:option>
                    <html-el:option value="${Constants.EMAIL_RMNDR_IMMEDIATE}">Immediate</html-el:option>
                  </html-el:select>
                </td>
                <td>
                  <html:image property="methodToCall.addNotificationPreference"
                              src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
                              styleClass="tinybutton"/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <th><div align="right">Send Email Notifications For</div></th>
          <td class="datacell">
            <ul style="padding-left: 0;">
              <li style="list-style-type: none;"><html-el:checkbox styleClass="nobord" property="preferences.notifyComplete" value="${Constants.PREFERENCES_YES_VAL}"/> Complete</li>
              <li style="list-style-type: none;"><html-el:checkbox styleClass="nobord" property="preferences.notifyApprove" value="${Constants.PREFERENCES_YES_VAL}"/> Approve</li>
              <li style="list-style-type: none;"><html-el:checkbox styleClass="nobord" property="preferences.notifyAcknowledge" value="${Constants.PREFERENCES_YES_VAL}"/> Acknowledge</li>
              <li style="list-style-type: none;"><html-el:checkbox styleClass="nobord" property="preferences.notifyFYI" value="${Constants.PREFERENCES_YES_VAL}"/> FYI</li>
            </ul>
          </td>
        </tr>
	  </table>
    </div><!-- End tab-container -->
    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
      <tr>
        <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
        <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
      </tr>
    </table>
	<div class="globalbuttons">
	  <html-el:hidden property="backLocation" />
      <html-el:image style="border-width:0px" property="methodToCall.save" src="images/buttonsmall_save.gif"  />
	  <a href="javascript:document.forms[0].reset()"><img src="images/buttonsmall_reset.gif" alt="cancel" width="59" height="18" hspace="5" border="0"></a>
      <a href="${KualiForm.backLocation}"><img src="images/buttonsmall_cancel.gif" border="0" alt="cancel"></a>
    </div>
  </div> <!-- End workarea -->
</kul:page>
