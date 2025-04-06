:: ---------------------------------------------------------------------
:: JAP COURSE - SCRIPT
:: SCRIPT CST8221 - Fall 2025
:: ---------------------------------------------------------------------
:: CST8116 - Assignment 3.1 - Build and Run Script
:: ---------------------------------------------------------------------

CLS

:: LOCAL VARIABLES ....................................................

:: Some of the below variables will need to be changed.
:: Remember to always use RELATIVE paths.

:: If your code needs no external libraries, remove all references to LIBDIR
:: and modulepath in this script.

:: You will also need to change MAINCLASSSRC (main class src) to point to
:: the file that contains main(), and MAINCLASSBIN (main class bin) needs to
:: point to the full class name of that class.  This means fullpackage.classname

:: Finally, recall that this file must end in .bat.  It has been renamed to .txt
:: to make it easier for you to edit.

:: As always, ask your lab prof if you have any questions or issues.

SET SRCDIR=src
SET BINDIR=bin
SET DOCDIR=doc
SET JARNAME=CrazyEights.jar
SET BINERR=compile.err
SET JARERR=jar.err
SET JAROUT=jar.out
SET DOCERR=javadoc.err
SET MAINCLASSBIN=view.StartPage


@echo off

ECHO "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
ECHO "@                                                                   @"
ECHO "@                   #       @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  @"
ECHO "@                  ##       @  A L G O N Q U I N  C O L L E G E  @  @"
ECHO "@                ##  #      @    JAVA APPLICATION PROGRAMMING    @  @"
ECHO "@             ###    ##     @         F A L L  -  2 0 2 3        @  @"
ECHO "@          ###    ##        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  @"
ECHO "@        ###    ##                                                  @"
ECHO "@        ##    ###                 ###                              @"
ECHO "@         ##    ###                ###                              @"
ECHO "@           ##    ##               ###   #####  ##     ##  #####    @"
ECHO "@         (     (      ((((()      ###       ## ###   ###      ##   @"
ECHO "@     ((((     ((((((((     ()     ###   ######  ###  ##   ######   @"
ECHO "@        ((                ()      ###  ##   ##   ## ##   ##   ##   @"
ECHO "@         ((((((((((( ((()         ###   ######    ###     ######   @"
ECHO "@         ((         ((           ###                               @"
ECHO "@          (((((((((((                                              @"
ECHO "@   (((                      ((                                     @"
ECHO "@    ((((((((((((((((((((() ))                                      @"
ECHO "@         ((((((((((((((((()                                        @"
ECHO "@                                                                   @"
ECHO "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

ECHO [1] Cleaning previous build ...
IF EXIST %BINDIR% rmdir /S /Q %BINDIR%
mkdir %BINDIR%

ECHO [2] Compiling source files ...
javac --release 17 -Xlint -d %BINDIR% -sourcepath %SRCDIR% ^
  %SRCDIR%\view\*.java ^
  %SRCDIR%\controller\*.java ^
  %SRCDIR%\model\*.java ^
  %SRCDIR%\network\*.java 2> %BINERR%

IF ERRORLEVEL 1 (
    ECHO ❌ Compilation failed. See %BINERR%
    EXIT /B 1
)

ECHO [3] Copying assets ...
xcopy /E /I /Y Assets %BINDIR%\Assets > nul

ECHO [3.1] Copying property files ...
xcopy /Y %SRCDIR%\Messages_*.properties %BINDIR% > nul

ECHO [4] Creating executable JAR ...
cd %BINDIR%
jar cvfe %JARNAME% %MAINCLASSBIN% . > ../%JAROUT% 2> ../%JARERR%

IF ERRORLEVEL 1 (
    ECHO ❌ Failed to create JAR. Check %JARERR%
    cd ..
    EXIT /B 1
)

cd ..

IF NOT EXIST %BINDIR%\%JARNAME% (
    ECHO ❌ JAR file not created. See errors.
    EXIT /B 1
)

ECHO [5] Running the game ...
java -jar %BINDIR%\%JARNAME%

ECHO [6] Generating Javadoc ...
IF EXIST %DOCDIR% rmdir /S /Q %DOCDIR%
javadoc -d %DOCDIR% -sourcepath %SRCDIR% -subpackages controller:network:model:view 2> %DOCERR%

IF ERRORLEVEL 1 (
    ECHO ⚠ Javadoc created with warnings. See %DOCERR%
) ELSE (
    ECHO ✅ Javadoc successfully created in %DOCDIR%
)

ECHO.
ECHO ✅ Build & Run Complete!
PAUSE