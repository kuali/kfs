<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
