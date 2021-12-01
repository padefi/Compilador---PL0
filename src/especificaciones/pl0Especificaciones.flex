package compilador;




%%



/* Modificadores */
%public
%class AnalizadorLexico
%type Terminal
%function escanear
%line
%column
%ignorecase
%unicode



/* Expresiones regulares para la tercera seccion */
identificador = [A-Za-z] ( [A-Za-z] | [0-9] )*
numero = 0 | [1-9] [0-9]*
cadenaLiteral = \' [^']* \'
lineTerminator = \n|\r|\r\n
whiteSpace = {lineTerminator} | [ \t\f]



%{
	private Terminal s;



	public String getCad(){
		return yytext();
	}

	public Terminal getS(){
		return s;
	}

	public int getLinea(){
		return yyline + 1;
	}

	public int getColumna(){
    		return yycolumn + 1;
  	}
%}




%%



/* Caracteres que deben saltearse */
{whiteSpace}				{}



/* Simbolos terminales no alfabeticos */
"("		{ s = Terminal.ABRE_PARENTESIS; return s; }
")"		{ s = Terminal.CIERRA_PARENTESIS; return s; }
":="            { s = Terminal.ASIGNACION; return s; }
"+"		{ s = Terminal.MAS; return s; }
"-"		{ s = Terminal.MENOS; return s; }
"*"		{ s = Terminal.POR; return s; }
"/"		{ s = Terminal.DIVIDIDO; return s; }
"="		{ s = Terminal.IGUAL; return s; }
">"		{ s = Terminal.MAYOR; return s; }
"<"		{ s = Terminal.MENOR; return s; }
">="            { s = Terminal.MAYOR_IGUAL; return s; }
"<="            { s = Terminal.MENOR_IGUAL; return s; }
"<>"            { s = Terminal.DISTINTO; return s; }
"."		{ s = Terminal.PUNTO; return s; }
","		{ s = Terminal.COMA; return s; }
";"		{ s = Terminal.PUNTO_Y_COMA; return s; }




/* Palabras Reservadas (Simbolos terminales en mayusculas) */
if      	{ s = Terminal.IF; return s; }
begin 		{ s = Terminal.BEGIN; return s; }
end   		{ s = Terminal.END; return s; }
do    		{ s = Terminal.DO; return s; }
then  		{ s = Terminal.THEN; return s; }
while 		{ s = Terminal.WHILE; return s; }
write 		{ s = Terminal.WRITE; return s; }
readln 		{ s = Terminal.READLN; return s; }
writeln 	{ s = Terminal.WRITELN; return s; }
call 		{ s = Terminal.CALL; return s; }
const 		{ s = Terminal.CONST; return s; }
var 		{ s = Terminal.VAR; return s; }
procedure 	{ s = Terminal.PROCEDURE; return s; }
odd 		{ s = Terminal.ODD; return s; }
sqr 		{ s = Terminal.SQR; return s; }

/* Simbolos terminales en minusculas */
{numero}	{ s = Terminal.NUMERO; return s; }
{identificador} { s = Terminal.IDENTIFICADOR; return s; }
{cadenaLiteral}	{ s = Terminal.CADENA_LITERAL; return s; }



/* Si no es ninguno de los anteriores */
[^]		{ s = Terminal.NULO; return s; }


/* Si se ha llegado al final del archivo */
<<EOF>>		{ s = Terminal.EOF; return s; }