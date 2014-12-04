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

<%@ attribute name="capitalAssetNumber" type="java.lang.Long" required="true" description="Capital Asset Number" %>
<%@ attribute name="isTransactionalDocument" required="false" %>

<c:if test="${empty isTransactionalDocument}">
    <c:set var="isTransactionalDocument" value="true" />
</c:if>

<kul:tab tabTitle="Payments Lookup" defaultOpen="false" useCurrentTabIndexAsKey="true"> 
    <div class="tab-container" align="center">
      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
      	<tr>
            <td class="tab-subhead">Payments Lookup</td>
		</tr>	
		<tr>
            <td>
			  <c:choose>
				<c:when test="${isTransactionalDocument}">
				  <html:link target="_blank" styleClass="portal_link" title="Payment Information" href="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&capitalAssetNumber=${capitalAssetNumber}">Click here</html:link>
				</c:when>
				<c:otherwise>
				  <html:link target="_blank" styleClass="portal_link" title="Payment Information" href="lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&capitalAssetNumber=${capitalAssetNumber}">Click here</html:link>				
				</c:otherwise>
			  </c:choose>
              to view the payment lookup for this asset.
            </td>
		</tr>
	</table>
	</div>
</kul:tab>
