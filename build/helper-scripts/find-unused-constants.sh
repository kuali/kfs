pushd ../..
CONSTANT_NAMES=`grep -o "public static final String [^ =]*" work/src/org/kuali/kfs/sys/KFSConstants.java | awk '{ print $5 ; }'`
#echo $CONSTANT_NAMES
echo "Unused Constant Names"
for NM in $CONSTANT_NAMES; do
	grep -r -q --include=*.java --include=*.jsp --include=*.tag --exclude=KFSConstants.java $NM work
	if [[ $? != 0 ]]; then
		echo "$NM"
	fi
done
popd