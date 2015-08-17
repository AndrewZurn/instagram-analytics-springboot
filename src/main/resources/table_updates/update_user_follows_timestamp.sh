#!/bin/bash
update='use instagram_prototype_raw;'

while read -r line
do
  echo $line
  update="${update} update source_user_entity set last_follows_traversal_time = '2015-08-17 09:00:00-0500' where user_id = ${line};"
done < user_ids.txt

update="${update} exit;"
cqlsh <<< $update
