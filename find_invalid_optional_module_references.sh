# This script finds references to classes in other optional modules

MODULES="ar
bc
cg
ec
endow
ld
"

for MODULE in $MODULES
do
	echo "******************************************************"
	echo "Checking for external references to \"$MODULE\" module"
	echo "******************************************************"
	grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/"
done

MODULE=purap
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cab/

MODULE=cam
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cab/

MODULE=cab
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cam/ -e /purap/
