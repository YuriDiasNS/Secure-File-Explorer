@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

REM Caminho raiz (com barra final)
set "ROOT=%CD%\"

for /f "delims=" %%D in ('dir /ad /b /s') do (
    set "FULL=%%D"
    set "REL=!FULL:%ROOT%=!"
    echo \!REL!
)

endlocal
pause
