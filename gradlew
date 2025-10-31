#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=""
# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$APP_HOME" ] &&
        APP_HOME=`cygpath --unix "$APP_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$CLASSPATH" ] &&
        CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# Attempt to locate JAVA_HOME if not already set.
if [ -z "$JAVA_HOME" ] ; then
    if $darwin ; then
        [ -x '/usr/libexec/java_home' ] && JAVA_HOME=`/usr/libexec/java_home`
    elif $cygwin ; then
        [ -n "`/usr/bin/which java`" ] && JAVA_HOME=`/usr/bin/which java | xargs readlink -f | xargs dirname | xargs dirname`
    else
        jsvc="`which java`"
        if [ -n "$jsvc" ] ; then
            if [ -f "$jsvc" -a ! -L "$jsvc" ] ; then
                 JAVA_HOME=`dirname "$jsvc"`/..
            fi
        fi
    fi
fi
if [ -z "$JAVA_HOME" ] ; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Set JAVA_EXE
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVA_EXE="$JAVA_HOME/jre/sh/java"
    else
        JAVA_EXE="$JAVA_HOME/bin/java"
    fi
fi

if [ -z "$JAVA_EXE" -o ! -x "$JAVA_EXE" ] ; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! $cygwin && ! $msys && [ "$MAX_FD" != "-1" ] ; then
    # Try increasing the file descriptors.
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ "$?" -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            # Use the system limit.
            MAX_FD="$MAX_FD_LIMIT"
        fi
        if [ "$MAX_FD" -gt "$MAX_FD_LIMIT" ] ; then
           warn "Value of MAX_FD is too large ($MAX_FD), using system limit ($MAX_FD_LIMIT) instead."
           MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ "$?" -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query system maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Add the gradle-wrapper.jar to the classpath.
if [ -n "$APP_HOME" ] ; then
    CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
fi

# Split up the JVM options only if the FULL_JVM_OPTS env var is not set.
if [ -z "$FULL_JVM_OPTS" ] ; then
    # Add the JVM options passed to this script to the list.
    JVM_OPTS=
    while [ "$1" != "" ]
    do
      case "$1" in
        -D*)
          JVM_OPTS="$JVM_OPTS $1"
          shift
          ;;
        -X*)
          JVM_OPTS="$JVM_OPTS $1"
          shift
          ;;
        -javaagent:*)
          JVM_OPTS="$JVM_OPTS $1"
          shift
          ;;
        *)
          break
          ;;
      esac
    done
fi

if [ -z "$FULL_JVM_OPTS" ] ; then
    FULL_JVM_OPTS="$DEFAULT_JVM_OPTS $JVM_OPTS $GRADLE_OPTS $JAVA_OPTS"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    APP_HOME=`cygpath --path --windows "$APP_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
    CYGHOME=`cygpath --path --windows "$HOME"`
    FULL_JVM_OPTS=`cygpath --path --windows "$FULL_JVM_OPTS"`
fi

# For Cygwin, we need to prefix the classpath with the drive letter of the current directory.
if $cygwin ; then
   CURRENT_DIR=`cygpath --windows .`
   if [ "$CLASSPATH" != "" ] ; then
      CLASSPATH="$CURRENT_DIR;$CLASSPATH"
   else
      CLASSPATH=$CURRENT_DIR
   fi
fi

# Execute Gradle
"$JAVA_EXE" $FULL_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
