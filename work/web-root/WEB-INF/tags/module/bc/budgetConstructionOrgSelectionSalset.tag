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

<kul:tab tabTitle="Select Operation" defaultOpen="true" tabErrorKey="reportSel">
	<div class="tab-container" align="center" id="G02" style="display: block;">
    		<h3>Select Operation</h3>
      	
      	<table width="100%" cellpadding="0" cellspacing="0">
      		 <tr>
                <td width="200">
                  <div align="center">
                     <html:image property="methodToCall.performPositionPick" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Position Pick" alt="Position Pick" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Show Position Pick List </td>
             </tr>
       		 <tr>
                <td width="200">
                  <div align="center">
                     <html:image property="methodToCall.performIncumbentPick" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Incumbent Pick" alt="Incumbent Pick" styleClass="tinybutton" />
                  </div>
                </td>
                <td>Show Incumbent Pick List </td>
             </tr>            
      </table>
  </div>
</kul:tab> 
