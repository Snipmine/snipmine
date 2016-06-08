#!/bin/bash
echo '\hrule'
echo "select t.tag from (tags t join hastag h on t.id = h.tag) where h.snippet = $1;" | mysql -N -u root -p snipcloud_c2 | sed 's/sm\:input\:/Input\: /' | sed 's/sm\:output\:/Output\: /' | sed 's/sm\:src\:\/home\/alex\/bachelor\/eval2\/repos\//Source\: /' | sed 's/`//g' | grep ' ' | sort | sed 's/$/ \\\\/' | sed 's/</{\\textless}/g' | sed 's/>/{\\textgreater}/g' | sed 's/_/\\_/g'
echo
