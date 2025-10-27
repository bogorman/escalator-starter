#!/bin/sh

# this script is cribbed from Shapeless
# https://github.com/milessabin/shapeless/blob/master/scripts/try-shapeless.sh

COURSIER_URL=https://git.io/coursier-cli
# check if coursier exists
# TODO: check version? only new-ish works with latest.version
test -e ~/.coursier/coursier || \
  # if not, download latest coursier-cli
  (mkdir -p ~/.coursier && curl -L -s --output ~/.coursier/coursier $COURSIER_URL && chmod +x ~/.coursier/coursier)

MSG="Welcome the EscalatorStarter REPL, powered by Ammonite"
IMPORT_STATEMENT="" 
# launch Ammonite with bitcoin-s on the classpath
# import com.escalatorstarter._

~/.coursier/coursier launch -q -P \
  com.lihaoyi:ammonite_2.13.6:2.4.1 \
  -M ammonite.Main -- --banner "$MSG" \
  --predef-code "$IMPORT_STATEMENT" < /dev/tty