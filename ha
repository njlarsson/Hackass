#!/bin/bash

SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

java -cp "$SCRIPTPATH/hackass.jar:/usr/local/lib/antlr-complete.jar" hackass.DoOnePass $*
