@echo off

if not exist %GLIFE_HOME% (
    echo No Game of Life home is not installed in %GLIFE_HOME%.
    echo Please check installation folder or correct configured GLIFE_HOME value.
	cmd /k
)

echo Shutting down server...

java -Duser.dir=%GLIFE_HOME% -cp %GLIFE_HOME%\lib\*;%GLIFE_HOME%\* io.vanderbeke.glife.GlifeShutdown %*

echo OK