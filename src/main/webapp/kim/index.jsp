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
<html>
<title>Kuali Identity Management</title>
</html>
<body>
<b>Kuali Identity Management</b>
<br/>
<br/>
<table border="1" cellspacing="0">
    <tr>
        <th>Lookups</th>
        <th>Documents</th>
    </tr>
    <tr>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=${ConfigProperties.kim.url}/index.jsp&hideReturnLink=true&showMaintenanceLinks=true">Person</a>
        </td>
        <td>
            <a href="${ConfigProperties.kim.url}/identityManagementPersonDocument.do?methodToCall=docHandler&command=initiate&docTypeName=IdentityManagementPersonDocument">Create New Person</a>
        </td>
    </tr>
    <tr>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.role.RoleBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&showMaintenanceLinks=true">Role</a>
        </td>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.rice.kim.impl.type.KimTypeBo&returnLocation=portal.do&docFormKey=IMRD">Role Document (started via type lookup)</a>
        </td>
    </tr>
    <tr>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.permission.UberPermissionBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&showMaintenanceLinks=true">Permission</a>
        </td>
        <td>
            (No Doc Yet)
        </td>
    </tr>
    <tr>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.responsibility.UberResponsibilityBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&showMaintenanceLinks=true">Responsibility</a>
        </td>
        <td>
            (No Doc Yet)
        </td>
    </tr>
    <tr>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.impl.group.GroupBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&showMaintenanceLinks=true">Group</a>
        </td>
        <td>
            <a href="${ConfigProperties.kr.url}/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.rice.kim.impl.type.KimTypeBo&returnLocation=portal.do&docFormKey=IMGD">Group Document (started via type lookup)</a>
        </td>
    </tr>
</table>            
<div align="center" id="footer-copyright"> Copyright 2005-2012 The Kuali Foundation. All rights reserved.<BR/>Portions of Kuali Rice are copyrighted by other parties as described in the <a href="../acknowledgments.jsp">Acknowledgments</a> screen.</div>
</body>
</html>

