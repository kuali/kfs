
# iterate over records/ilnes in child table
# grep for line starting with that value
# if found, replace on that line with the prefixed value
# save lines to new file
# at end, replace old file

# Role-Permission Table

#cp bootstrap/krim_role_perm_t.csv bootstrap/krim_role_perm_t.backup
# copy the header line
#head -1 bootstrap/krim_role_perm_t.backup > bootstrap/krim_role_perm_t.csv
#for LINE in $(cat bootstrap/krim_role_perm_t.backup); do
#	#echo $LINE
#	ROLE_ID=`echo "$LINE" | cut -d ',' -f 3`
#	PERM_ID=`echo "$LINE" | cut -d ',' -f 4`
#	#echo $PERM_ID 
#
#	grep "^$PERM_ID," bootstrap/krim_perm_t.csv > /dev/null
#	RESULT=$?
#	#echo Perm: $PERM_ID : $RESULT
#	if [[ $RESULT == 0 ]]; then
#		PERM_ID=KFS$PERM_ID
#	fi
#	grep "^$ROLE_ID," bootstrap/krim_role_t.csv > /dev/null
#	RESULT=$?
#	#echo Role: $ROLE_ID : $RESULT
#	if [[ $RESULT == 0 ]]; then
#		ROLE_ID=KFS$ROLE_ID
#	fi
#	echo $LINE | sed "s/^\([^,]*\),\([^,]*\),\([^,]*\),\([^,]*\),/\1,\2,$ROLE_ID,$PERM_ID,/" >> bootstrap/krim_role_perm_t.csv
#done

# Role-Responsibility
  # change role only - resp still integer

# Role Member (role member IDs)

cp bootstrap/krim_role_mbr_t.csv bootstrap/krim_role_mbr_t.backup
head -1 bootstrap/krim_role_mbr_t.backup > bootstrap/krim_role_mbr_t.csv
for LINE in $(cat bootstrap/krim_role_mbr_t.backup); do
	#echo $LINE
	ROLE_ID=`echo "$LINE" | cut -d ',' -f 3`
	ROLE_ID=${ROLE_ID//\"/}
	MBR_ID=`echo "$LINE" | cut -d ',' -f 4`
	MBR_ID=${MBR_ID//\"/}
	MBR_TYP_CD=`echo "$LINE" | cut -d ',' -f 5`
	MBR_TYP_CD=${MBR_TYP_CD//\"/}
	#echo $ROLE_ID 

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
	echo $LINE | sed "s/^\([^,]*\),\([^,]*\),\([^,]*\),\([^,]*\),/\1,\2,\"$ROLE_ID\",\"$MBR_ID\",/" >> bootstrap/krim_role_mbr_t.csv

done


# Permission Attribute

# Resp. Attribute

# Role Member Attribute


# Role

# Permission

# KIM Type
