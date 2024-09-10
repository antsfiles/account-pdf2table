#!/bin/sh

BASEDIR=$(dirname $0)
cd "${BASEDIR}"

java -jar target/account-pdf2table-1.0-SNAPSHOT-jar-with-dependencies.jar
