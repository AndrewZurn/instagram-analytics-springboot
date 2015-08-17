#!/bin/bash
update='use instagram_prototype_raw;'

while read -r line
do
  echo $line
  update="${update} update source_user_entity set has_been_follows_traversed = False where user_id = ${line};"
done < user_ids.txt

update="${update} exit;"
cqlsh <<< $update
