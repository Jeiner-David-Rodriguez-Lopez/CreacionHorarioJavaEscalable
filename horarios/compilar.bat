@echo off
echo Compilando proyecto...
mkdir out 2>nul
javac -encoding UTF-8 -d out -sourcepath src src/Main.java src/Login.java src/usuarios/*.java src/asignaturas/*.java src/aulas/*.java src/horario/*.java src/datos/*.java src/gui/*.java
if %errorlevel% == 0 (
    echo Compilacion exitosa!
    echo Ejecutando...
    java -cp out Main
) else (
    echo Error de compilacion.
)
