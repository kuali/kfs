########################################
# The Kuali Financial System, a comprehensive financial management system for higher education.
# 
# Copyright 2005-2014 The Kuali Foundation
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
########################################
# DO NOT add comments before the blank line below, or they will disappear.

svn revert -R bootstrap demo
# iterate over records/ilnes in child table
# grep for line starting with that value
# if found, replace on that line with the prefixed value
# save lines to new file
# at end, replace old file

# Role-Permission Table

mv bootstrap/krim_role_perm_t.csv bootstrap/krim_role_perm_t.backup
# copy the header line
touch bootstrap/krim_role_perm_t.csv
for LINE in $(cat bootstrap/krim_role_perm_t.backup); do
	#echo $LINE
	ROLE_ID=`echo "$LINE" | cut -d ',' -f 3`
	PERM_ID=`echo "$LINE" | cut -d ',' -f 4`
	#echo $PERM_ID

	if [[ "$ROLE_ID" != "ROLE_ID" ]]; then

		grep "^$PERM_ID," bootstrap/krim_perm_t.csv > /dev/null
		RESULT=$?
		#echo Perm: $PERM_ID : $RESULT
		if [[ $RESULT == 0 ]]; then
			PERM_ID=KFS$PERM_ID
		fi
		grep "^$ROLE_ID," bootstrap/krim_role_t.csv > /dev/null
		RESULT=$?
		#echo Role: $ROLE_ID : $RESULT
		if [[ $RESULT == 0 ]]; then
			ROLE_ID=KFS$ROLE_ID
		fi
	fi
	echo $LINE | sed "s/^\([^,]*\),\([^,]*\),\([^,]*\),\([^,]*\),/\1,\2,$ROLE_ID,$PERM_ID,/" >> bootstrap/krim_role_perm_t.csv
done

# Role-Responsibility
  # change role only - resp still integer

# Role Member (role member IDs)

mv bootstrap/krim_role_mbr_t.csv bootstrap/krim_role_mbr_t.backup
touch bootstrap/krim_role_mbr_t.csv
for LINE in $(cat bootstrap/krim_role_mbr_t.backup); do
	#echo $LINE
	ROLE_ID=`echo "$LINE" | cut -d ',' -f 3`
	ROLE_ID=${ROLE_ID//\"/}
	MBR_ID=`echo "$LINE" | cut -d ',' -f 4`
	MBR_ID=${MBR_ID//\"/}
	MBR_TYP_CD=`echo "$LINE" | cut -d ',' -f 5`
	MBR_TYP_CD=${MBR_TYP_CD//\"/}
	#echo $ROLE_ID 

	if [[ "$ROLE_ID" != "ROLE_ID" ]]; then
		grep "^$ROLE_ID," bootstrap/krim_role_t.csv > /dev/null
		RESULT=$?
		#echo Role: $ROLE_ID : $RESULT
		if [[ $RESULT == 0 ]]; then
			ROLE_ID=KFS$ROLE_ID
		fi
		if [[ "$MBR_TYP_CD" == "R" ]]; then
			grep "^$MBR_ID," bootstrap/krim_role_t.csv > /dev/null
			RESULT=$?
			#echo Role: $ROLE_ID : $RESULT
			if [[ $RESULT == 0 ]]; then
				MBR_ID=KFS$MBR_ID
			fi
		fi
	fi
	echo $LINE | sed "s/^\([^,]*\),\([^,]*\),\([^,]*\),\([^,]*\),/\1,\2,\"$ROLE_ID\",\"$MBR_ID\",/" >> bootstrap/krim_role_mbr_t.csv

done

mv demo/krim_role_mbr_t.csv demo/krim_role_mbr_t.backup
touch demo/krim_role_mbr_t.csv
for LINE in $(cat demo/krim_role_mbr_t.backup); do
	#echo $LINE
	ROLE_ID=`echo "$LINE" | cut -d ',' -f 3`
	ROLE_ID=${ROLE_ID//\"/}
	MBR_ID=`echo "$LINE" | cut -d ',' -f 4`
	MBR_ID=${MBR_ID//\"/}
	MBR_TYP_CD=`echo "$LINE" | cut -d ',' -f 5`
	MBR_TYP_CD=${MBR_TYP_CD//\"/}
	#echo $ROLE_ID 

	if [[ "$ROLE_ID" != "ROLE_ID" ]]; then
		grep "^$ROLE_ID," bootstrap/krim_role_t.csv > /dev/null
		RESULT=$?
		#echo Role: $ROLE_ID : $RESULT
		if [[ $RESULT == 0 ]]; then
			ROLE_ID=KFS$ROLE_ID
		fi
		if [[ "$MBR_TYP_CD" == "R" ]]; then
			grep "^$MBR_ID," bootstrap/krim_role_t.csv > /dev/null
			RESULT=$?
			#echo Role: $ROLE_ID : $RESULT
			if [[ $RESULT == 0 ]]; then
				MBR_ID=KFS$MBR_ID
			fi
		fi
	fi
	echo $LINE | sed "s/^\([^,]*\),\([^,]*\),\([^,]*\),\([^,]*\),/\1,\2,$ROLE_ID,$MBR_ID,/" >> demo/krim_role_mbr_t.csv

done


# Resp. Attribute

# TODO: Update the KIM Type IDs and attribute definition IDs

# Role Member Attribute

# TODO: Update the KIM Type IDs and attribute definition IDs

# Role

sed -e '1n' -e 's/^\([0-9]*\),/KFS\1,/' -i "backup" bootstrap/krim_role_t.csv

# Permission Templates?

# Permission Attribute

# Replace the permission IDs
sed -e '1n' -e 's/^\([^,]*,[^,]*,[^,]*,\)/\1KFS/' -i "backup" bootstrap/krim_perm_attr_data_t.csv

# TODO: Update the KIM Type IDs and attribute definition IDs

# Permission

sed -e '1n' -e 's/^\([0-9]*\),/KFS\1,KFS/' -i "backup" bootstrap/krim_perm_t.csv

# Update permission template IDs?

# KIM Type/Attribute Table

#TODO - lookup on attribute definition ID
#TODO - blanket replacement on type ID

# KIM Attribute Definitions

sed -e '1n' -e 's/^\([0-9]*\),/KFS\1,KFS/' -i "backup" bootstrap/krim_attr_defn_t.csv

# KIM Type

sed -e '1n' -e 's/^\([0-9]*\),/KFS\1,KFS/' -i "backup" bootstrap/krim_typ_t.csv

