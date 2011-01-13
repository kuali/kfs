rm -f fy-maker-2011.sql
# touch fy-maker-2011.sql
for f in *.sql
do
	echo "-- $f" >> fy-maker-2011.sql
	cat -s $f >> fy-maker-2011.sql
done