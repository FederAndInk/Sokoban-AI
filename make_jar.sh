#!/usr/bin/env zsh

javac **/*.java
cd sources
cp -r ../resources/* .
res=$(echo Images Niveaux defaut.cfg)
jar -cfe Sokoban.jar Sokoban **/*.class $(echo $res)
rm -r **/*.class $(echo $res)
mv Sokoban.jar ../
