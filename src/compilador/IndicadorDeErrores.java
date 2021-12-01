package compilador;

public class IndicadorDeErrores {
    
    void mostrar(int i){
        switch(i)
        {
            case 0:
                System.out.println("COMPILACIÓN EXITOSA!");
        }
        System.exit(0);
    }
    
    void mostrar(int cod, String cadError, int linea, int columna){
        
        switch(cod)
        {
            case 1:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". se esperaba un PUNTO y se recibió: " + cadError);
                break;
            case 2:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un IDENTIFICADOR y se recibió: " + cadError);
                break;
            case 3:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un PUNTO Y COMA y se recibió: " + cadError);
                break;
            case 4:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba una ASIGNACION y se recibió: " + cadError);
                break;
            case 5:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un END y se recibió: " + cadError);
                break;
            case 6:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un THEN y se recibió: " + cadError);
                break;
            case 7:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un DO y se recibió: " + cadError);
                break;
            case 8:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un ABRE PARENTESIS y se recibió: " + cadError);
                break;
            case 9:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un CIERRA PARENTESIS y se recibió: " + cadError);
                break;
            case 10:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un OPERADOR RACIONAL y se recibió: " + cadError);
                break;
            case 11:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un IGUAL y se recibió: " + cadError);
                break;
            case 12:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un NUMERO y se recibió: " + cadError);
                break;
            case 13:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". La VARIABLE '" + cadError + "' no fue declarado");
                break;
            case 14:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". El PROCEDURE '" + cadError + "' no fue declarado");
                break;
            case 15:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un PROCEDURE y se recibió: " + cadError);
                break;
            case 16:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba una VARIABLE y se recibió: " + cadError);
                break;
            case 17:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba una CONSTANTE o una VARIABLE y se recibió: " + cadError);
                break;
            case 18:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". Se esperaba un IDENTIFICADOR o un NUMERO y se recibió: " + cadError);
                break;
            case 19:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". IDENTIFICADOR repetido: " + cadError);
                break;
            case 20:
                System.out.println("Error en la linea: " + linea + ", columna: " + columna + ". El IDENTIFICADOR '" + cadError + "' no fue declarado");
                break;
            case 21:
                System.out.println("Ha excedido la cantidad de variables a declarar");
                break;
        }
        System.exit(0);
    }
}
