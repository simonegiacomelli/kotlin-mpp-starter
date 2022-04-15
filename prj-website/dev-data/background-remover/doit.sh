#!/bin/bash

mkdir -p no_background

for f in original/*.png ; do
  
  fullfile=$f
  filename=$(basename -- "$fullfile")
extension="${filename##*.}"
filename="${filename%.*}"
    
  cmd="backgroundremover -i $f  -m "u2net_human_seg" -a -ae 15 -o no_background/$filename.png"
  echo $cmd
  $cmd
done

#