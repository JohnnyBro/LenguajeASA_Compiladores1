/*----------------------------------------------------------------------------
--------------------- 1ra. Area: Codigo de Usuario
----------------------------------------------------------------------------*/

//-------> Paquete, importaciones
package Analizadores;

import java_cup.runtime.*;
import java.util.ArrayList;
import Acciones.ErrorT;

%%
/*----------------------------------------------------------------------------
--------------------- 2da. Area: Opciones y Declaraciones
----------------------------------------------------------------------------*/

%{
    public ArrayList<ErrorT> lista_errores;
%}

//-------> Directivas
%public
%class Lexico
%cupsym Simbolos
%cup
%char
%column
%full
%ignorecase
%line
%unicode

//-------> Expresiones Regulares
digito = [0-9]
numero = {digito}+("." {digito}+)?
tstring = "\"" ~"\""
tchar = "'" ~"'"
letra = [a-zA-ZñÑ]
id = {letra}+({letra}|{digito}|"_")*

//-------> Estados
%state COMENT_SIMPLE
%state COMENT_MULTI

%%
/*-------------------------------------------------------------------
--------------------- 3ra. y ultima area: Reglas Lexicas
-------------------------------------------------------------------*/

//-------> Comentarios
<YYINITIAL> "</"                {yybegin(COMENT_MULTI);}
<COMENT_MULTI> "/>"             {yybegin(YYINITIAL);}
<COMENT_MULTI>  .               {}
<COMENT_MULTI> [ \t\r\n\f]      {}

<YYINITIAL> "-->"           {yybegin(COMENT_SIMPLE);}
<COMENT_SIMPLE> [^"\n"]     {}
<COMENT_SIMPLE> "\n"        {yybegin(YYINITIAL);}
<COMENT_SIMPLE>  .          {}

//Tokens Tabla de simbolos..

<YYINITIAL> "Importar"		{   return new Symbol(Simbolos.tokImportar, yycolumn, yyline, yytext());}
<YYINITIAL> "Definir"		{   return new Symbol(Simbolos.tokDefinir, yycolumn, yyline, yytext());}
<YYINITIAL> "Decimal"		{   return new Symbol(Simbolos.tokDecimal, yycolumn, yyline, yytext());}
<YYINITIAL> "Booleano"		{   return new Symbol(Simbolos.tokBooleano, yycolumn, yyline, yytext());}
<YYINITIAL> "Texto"			{   return new Symbol(Simbolos.tokTexto, yycolumn, yyline, yytext());}
<YYINITIAL> "Entero"		{   return new Symbol(Simbolos.tokEntero, yycolumn, yyline, yytext());}
<YYINITIAL> "Caracter"		{   return new Symbol(Simbolos.tokCaracter, yycolumn, yyline, yytext());}
<YYINITIAL> "Vacio"			{   return new Symbol(Simbolos.tokVacio, yycolumn, yyline, yytext());}
<YYINITIAL> "Retorno"		{   return new Symbol(Simbolos.tokRetorno, yycolumn, yyline, yytext());}
<YYINITIAL> "Es_verdadero"	{   return new Symbol(Simbolos.tokEsVerdadero, yycolumn, yyline, yytext());}
<YYINITIAL> "Es_falso"		{   return new Symbol(Simbolos.tokEsFalso, yycolumn, yyline, yytext());}
<YYINITIAL> "Cambiar_a"		{   return new Symbol(Simbolos.tokCambiarA, yycolumn, yyline, yytext());}
<YYINITIAL> "No_cumple"		{   return new Symbol(Simbolos.tokNoCumple, yycolumn, yyline, yytext());}
<YYINITIAL> "Valor"			{   return new Symbol(Simbolos.tokValor, yycolumn, yyline, yytext());}
<YYINITIAL> "Para"			{   return new Symbol(Simbolos.tokPara, yycolumn, yyline, yytext());}
<YYINITIAL> "Hasta_que"		{   return new Symbol(Simbolos.tokHastaQue, yycolumn, yyline, yytext());}
<YYINITIAL> "Mientras_que"	{   return new Symbol(Simbolos.tokMientrasQue, yycolumn, yyline, yytext());}
<YYINITIAL> "Romper"		{   return new Symbol(Simbolos.tokRomper, yycolumn, yyline, yytext());}
<YYINITIAL> "Continuar"		{   return new Symbol(Simbolos.tokContinuar, yycolumn, yyline, yytext());}
<YYINITIAL> "Mostrar"		{   return new Symbol(Simbolos.tokMostrar, yycolumn, yyline, yytext());}
<YYINITIAL> "DibujarAST"	{   return new Symbol(Simbolos.tokDibujarAST, yycolumn, yyline, yytext());}
<YYINITIAL> "DibujarEXP"	{   return new Symbol(Simbolos.tokDibujarExp, yycolumn, yyline, yytext());}
<YYINITIAL> "DibujarTS"		{   return new Symbol(Simbolos.tokDibujarTS, yycolumn, yyline, yytext());}
<YYINITIAL> "verdadero"		{   return new Symbol(Simbolos.tokVerdadero, yycolumn, yyline, yytext());}
<YYINITIAL> "falso"			{   return new Symbol(Simbolos.tokFalso, yycolumn, yyline, yytext());}
<YYINITIAL> "Principal"		{   return new Symbol(Simbolos.tokPrincipal, yycolumn, yyline, yytext());}
<YYINITIAL> "||"			{   return new Symbol(Simbolos.tokOr, yycolumn, yyline, yytext());}
<YYINITIAL> "&&"			{   return new Symbol(Simbolos.tokAnd, yycolumn, yyline, yytext());}
<YYINITIAL> "!"				{   return new Symbol(Simbolos.tokNot, yycolumn, yyline, yytext());}
<YYINITIAL> "+"				{   return new Symbol(Simbolos.tokMas, yycolumn, yyline, yytext());}
<YYINITIAL> "++"			{   return new Symbol(Simbolos.tokMasMas, yycolumn, yyline, yytext());}
<YYINITIAL> "-"				{   return new Symbol(Simbolos.tokMenos, yycolumn, yyline, yytext());}
<YYINITIAL> "--"			{   return new Symbol(Simbolos.tokMenosMenos, yycolumn, yyline, yytext());}
<YYINITIAL> "*"				{   return new Symbol(Simbolos.tokPor, yycolumn, yyline, yytext());}
<YYINITIAL> "/"				{   return new Symbol(Simbolos.tokDiv, yycolumn, yyline, yytext());}
<YYINITIAL> "%"				{   return new Symbol(Simbolos.tokModulo, yycolumn, yyline, yytext());}
<YYINITIAL> "^"				{   return new Symbol(Simbolos.tokPotencia, yycolumn, yyline, yytext());}
<YYINITIAL> "="				{   return new Symbol(Simbolos.tokIgual, yycolumn, yyline, yytext());}
<YYINITIAL> "=="			{   return new Symbol(Simbolos.tokIgualIgual, yycolumn, yyline, yytext());}
<YYINITIAL> "!="			{   return new Symbol(Simbolos.tokDiferente, yycolumn, yyline, yytext());}
<YYINITIAL> ">"				{   return new Symbol(Simbolos.tokMayor, yycolumn, yyline, yytext());}
<YYINITIAL> "<"				{   return new Symbol(Simbolos.tokMenor, yycolumn, yyline, yytext());}
<YYINITIAL> ">="			{   return new Symbol(Simbolos.tokMayorIgual, yycolumn, yyline, yytext());}
<YYINITIAL> "<="			{   return new Symbol(Simbolos.tokMenorIgual, yycolumn, yyline, yytext());}
<YYINITIAL> "("				{   return new Symbol(Simbolos.tokApar, yycolumn, yyline, yytext());}
<YYINITIAL> ")"				{   return new Symbol(Simbolos.tokCpar, yycolumn, yyline, yytext());}
<YYINITIAL> "{"				{   return new Symbol(Simbolos.tokAlla, yycolumn, yyline, yytext());}
<YYINITIAL> "}"				{   return new Symbol(Simbolos.tokClla, yycolumn, yyline, yytext());}
<YYINITIAL> ";"				{   return new Symbol(Simbolos.tokPcoma, yycolumn, yyline, yytext());}
<YYINITIAL> ":"				{   return new Symbol(Simbolos.tokDosp, yycolumn, yyline, yytext());}
<YYINITIAL> ","				{   return new Symbol(Simbolos.tokComa, yycolumn, yyline, yytext());}
<YYINITIAL> "."				{   return new Symbol(Simbolos.tokPunto, yycolumn, yyline, yytext());}
<YYINITIAL> "asa"			{   return new Symbol(Simbolos.tokExtension, yycolumn, yyline, yytext());}


//-------> Dato

<YYINITIAL> {numero}    {   return new Symbol(Simbolos.tokNumero, yycolumn, yyline, yytext());}
<YYINITIAL> {tstring}   {   return new Symbol(Simbolos.tokString, yycolumn, yyline, yytext());}
<YYINITIAL> {tchar}     {   return new Symbol(Simbolos.tokChar, yycolumn, yyline, yytext());}
<YYINITIAL> {id}        {   return new Symbol(Simbolos.tokId, yycolumn, yyline, yytext());}

//-------> Espacios
[ \t\r\n\f]                 {/* Espacios en blanco, se ignoran */ }
 
//-------> Errores Lexicos
.                           { System.out.println("Error Lexico: <<"+yytext()+">> ["+yyline+" , "+yycolumn+"]");
                                lista_errores.add(new ErrorT(yytext(),yyline,yycolumn,"Error Lexico","Lexema Invalido"));}

