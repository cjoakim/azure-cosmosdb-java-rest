#!/bin/bash

# Bash script to run maven to bootstrap this project.
# Chris Joakim, Microsoft, 2018/12/21

mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=com.chrisjoakim.azure.cosmosdb \
  -DartifactId=rest
