<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<c:set var="benefitInq"
	value="${DataDictionary.BenefitInquiry.attributes}" />

<kul:page showDocumentInfo="false" showTabButtons="false"
	headerTitle="Fringe Benefit Inquiry" docTitle="Fringe Benefit Inquiry"
	transactionalDocument="false" htmlFormAction="fringeBenefitInquiry">
	<kul:tabTop tabTitle="Fringe Benefit Detail" defaultOpen="true">
        <div id="workarea">
          <table cellpadding="0" cellspacing="0" class="datatable" summary="" width="50%">
            <tr>
              <kul:htmlAttributeHeaderCell literalLabel="Object Code" scope="col"/>
              <kul:htmlAttributeHeaderCell literalLabel="Amount" scope="col" />
            </tr>

            <logic:iterate name="KualiForm" id="benefitInquiry" property="benefitInquiry" indexId="ctr">
              <tr>
                 <td>
                 <bean:write name="benefitInquiry" property="fringeBenefitObjectCode" />
                </td>
                <td>
                  <bean:write name="benefitInquiry" property="benefitAmount" />
                </td>
              </tr>
            </logic:iterate>
          </table>
        </div>
        <div id="globalbuttons" class="globalbuttons">
	    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" 
	        onclick="window.close();return false;" title="close the window" alt="close the window"/>		
		</div>
      </kul:tabTop>
	    
</kul:page>
