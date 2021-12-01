package compilador;

import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import static compilador.Terminal.*;
import static compilador.Constantes.*;
import java.io.*;

public class AnalizadorSintactico {
    
    //Atributos
    private AnalizadorLexico aLex;
    private AnalizadorSemantico aSem;
    private GeneradorDeCodigo genCod;
    private IndicadorDeErrores indErrores;
    
    private int contador;
    
    AnalizadorSintactico(AnalizadorLexico aLex, AnalizadorSemantico aSem, GeneradorDeCodigo genCod, IndicadorDeErrores indErrores){
        this.aLex = aLex;
        this.aSem = aSem;
        this.genCod = genCod;
        this.indErrores = indErrores;
        
        contador = 0;
    }
    
    public void analizar() throws  IOException{
        
        System.out.println("COMIENZA A LEER EL ARCHIVO");
        aLex.escanear();
        programa();
        indErrores.mostrar(0);
    }
    
    private void programa() throws IOException{
        
        genCod.cargarHeader();
        
        bloque(0);
        escaneoSimple(aLex.getS(),PUNTO,1);
        
        genCod.cargarFinalDelPrograma();
        genCod.reemplazarInicio();
        genCod.iniciarVars(contador);
        genCod.reemplazarTamanoVirtual();
        genCod.llenarCeros();
        genCod.reemplazarTamanoSeccCod();
        genCod.reemplazarDataPura();
        genCod.reemplazarTamanoImgyBaseDato();
        genCod.volcarMemoria();
    }
    
    private void bloque(int base) throws IOException{
        
        int desp = 0;
        
        genCod.cargarSalto(0); //se va a pisar despues
        int posSalto = genCod.traerTop();
        
        
///////////////CONSTANTES///////////////
        if(aLex.getS()==CONST){
            
            asigConst(base,desp);
            desp++;
            
            while(aLex.getS()==COMA){

                asigConst(base,desp);
                desp++;
            }           
            escaneoSimple(aLex.getS(),PUNTO_Y_COMA,3);
        }
        
///////////////VARIABLES///////////////
        if(aLex.getS()==VAR){
            
            aLex.escanear();
            String nombre = aLex.getCad();
            escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
            
            IdentificadorBean identificador = aSem.buscar(nombre, base + desp - 1, base);

            if(identificador != null){
                indErrores.mostrar(19, nombre, aLex.getLinea(), aLex.getColumna());
            }else{
                aSem.cargar(base + desp, nombre, VAR, contador * Constantes.TAMANO_BEAN);
                contador++;
                desp++;

                while(aLex.getS()==COMA){

                    aLex.escanear();
                    nombre = aLex.getCad();
                    escaneoSimple(aLex.getS(),IDENTIFICADOR,2);

                    identificador = aSem.buscar(nombre, base + desp - 1, base);
                    
                    if(identificador != null){
                        indErrores.mostrar(19, nombre, aLex.getLinea(), aLex.getColumna());
                    }else{
                        aSem.cargar(base + desp, nombre, VAR, contador * Constantes.TAMANO_BEAN);
                        contador++;
                        desp++;
                    }
                }         
                escaneoSimple(aLex.getS(),PUNTO_Y_COMA,3);
            }
        }
        
///////////////PROCEDURE///////////////
        while(aLex.getS()==PROCEDURE){
            
            aLex.escanear();
            String nombre = aLex.getCad();
            escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
            IdentificadorBean identificador = aSem.buscar(nombre, base + desp - 1, base );

            if(identificador != null){
                indErrores.mostrar(19, nombre, aLex.getLinea(), aLex.getColumna());
            }else{
                
                aSem.cargar(base + desp, nombre, PROCEDURE, genCod.traerTop());
                desp++;
                
                escaneoSimple(aLex.getS(),PUNTO_Y_COMA,3);
                bloque(base+desp);
                genCod.cargarBloqueFinalProcedure();
                escaneoSimple(aLex.getS(),PUNTO_Y_COMA,3);
            }
        }
        
        if(genCod.traerTop()-posSalto != 0){
            genCod.reemplazarInt(posSalto - 4, genCod.traerTop() - posSalto);
        }
        //genCod.reemplazarInt(posSalto - 4, genCod.traerTop() - posSalto);
        proposicion(base, desp);
    }
    
    private void proposicion(int base, int desp) throws IOException{
        
        switch(aLex.getS()){
            case IDENTIFICADOR:
                IdentificadorBean identificador = aSem.buscar(aLex.getCad(), base + desp - 1, 0);

                if(identificador == null){
                    indErrores.mostrar(20, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
                }else if(identificador.getTipo() == PROCEDURE || identificador.getTipo() == CONST){
                    indErrores.mostrar(16,aLex.getCad(),aLex.getLinea(),aLex.getColumna());
                }else{
                    aLex.escanear();
                }
                escaneoSimple(aLex.getS(),ASIGNACION,4);
                expresion(base, desp);
                genCod.cargarAsignacion(identificador.getValor());
                break;
            
            case CALL:
                aLex.escanear();
                identificador = aSem.buscar(aLex.getCad(), base + desp - 1, 0);

                if(identificador == null){
                    indErrores.mostrar(14, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
                }else if(identificador.getTipo() == VAR || identificador.getTipo() == CONST){
                    indErrores.mostrar(15,aLex.getCad(),aLex.getLinea(),aLex.getColumna());
                }else{              
                    escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
                    genCod.cargarCall(identificador.getValor());
                }
                break;
            
            case BEGIN:
                aLex.escanear();
                proposicion(base, desp);
                
                while(aLex.getS()==PUNTO_Y_COMA){
                    aLex.escanear();
                    proposicion(base, desp);
                }
                
                escaneoSimple(aLex.getS(),END,5);
                break;
                
            case IF:
                aLex.escanear();
                condicion(base, desp);
                int punto = genCod.traerTop();
                escaneoSimple(aLex.getS(),THEN,6);
                proposicion(base, desp);
                
                int destino = genCod.traerTop();
                int distancia = destino - punto;
                
                genCod.reemplazarInt((punto-4),distancia);
                break;
                
            case WHILE:
                aLex.escanear();
                int preSalto = genCod.traerTop();
                condicion(base, desp);
                
                punto = genCod.traerTop();
                
                escaneoSimple(aLex.getS(),DO,7);
                proposicion(base, desp);
                
                int saltoDistancia = preSalto - (genCod.traerTop() + 5);
                genCod.cargarSalto(saltoDistancia);
                destino = genCod.traerTop();
                distancia = destino - punto;
                
                genCod.reemplazarInt((punto-4),distancia);
                break;
                
            case READLN:
                aLex.escanear();
                escaneoSimple(aLex.getS(),ABRE_PARENTESIS,8);
                
                identificador = aSem.buscar(aLex.getCad(), base + desp - 1, 0);

                if(identificador == null){
                    indErrores.mostrar(13, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
                }else if(identificador.getTipo() == PROCEDURE || identificador.getTipo() == CONST){
                    indErrores.mostrar(16,aLex.getCad(),aLex.getLinea(),aLex.getColumna());
                }else{               
                    escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
                    genCod.cargarReadln(identificador.getValor());
                }
                
                while(aLex.getS()==COMA){
                    aLex.escanear();
                    identificador = aSem.buscar(aLex.getCad(), base + desp - 1, 0);

                    if(identificador == null){
                        indErrores.mostrar(13, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
                    }else if(identificador.getTipo() == PROCEDURE || identificador.getTipo() == CONST){
                        indErrores.mostrar(16,aLex.getCad(),aLex.getLinea(),aLex.getColumna());
                    }else{               
                        escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
                        genCod.cargarReadln(identificador.getValor());
                    }
                }
                escaneoSimple(aLex.getS(),CIERRA_PARENTESIS,9);
                break;
            
            case WRITELN:
                aLex.escanear();
                if(aLex.getS()==ABRE_PARENTESIS){
                    aLex.escanear();
                    if(aLex.getS()==CADENA_LITERAL){
                        genCod.cargarWriteString(aLex.getCad());
                        aLex.escanear();
                    }else{
                        expresion(base, desp);
                        genCod.cargarSalidaExpresion();
                    }
                    
                    while(aLex.getS()==COMA){
                        aLex.escanear();
                        if(aLex.getS()==CADENA_LITERAL){
                            genCod.cargarWriteString(aLex.getCad());
                            aLex.escanear();
                        }else{
                            expresion(base, desp);
                            genCod.cargarSalidaExpresion();
                        }
                    }
                    escaneoSimple(aLex.getS(),CIERRA_PARENTESIS,9);
                }
                genCod.cargarCall(SALTO_DE_LINEA);
                break;
            
            case WRITE:
                aLex.escanear();
                escaneoSimple(aLex.getS(),ABRE_PARENTESIS,8);

                if(aLex.getS()==CADENA_LITERAL){
                    genCod.cargarWriteString(aLex.getCad());
                    aLex.escanear();
                }else{
                    expresion(base, desp);
                    genCod.cargarSalidaExpresion();
                }
                    
                while(aLex.getS()==COMA){
                    aLex.escanear();
                    if(aLex.getS()==CADENA_LITERAL){
                        genCod.cargarWriteString(aLex.getCad());
                        aLex.escanear();
                    }else{
                        expresion(base, desp);
                        genCod.cargarSalidaExpresion();
                    }
                }
                escaneoSimple(aLex.getS(),CIERRA_PARENTESIS,9);
                break;
        }
    }
    
    private void condicion(int base, int desp) throws IOException{
        if(aLex.getS()==ODD){
            aLex.escanear();
            expresion(base, desp);
            genCod.impar();
        }else{
            expresion(base, desp);
            Terminal simbolo = aLex.getS();
             switch(simbolo){
                 case IGUAL:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                     
                 case DISTINTO:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                     
                 case MENOR:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                     
                 case MENOR_IGUAL:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                     
                 case MAYOR:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                      
                 case MAYOR_IGUAL:
                      aLex.escanear();
                      expresion(base, desp);
                      genCod.cargarCondicion(simbolo);
                      break;
                 
                 default:
                     indErrores.mostrar(10, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
             }
        }
    }
    
    private void expresion(int base, int desp) throws IOException{
        
        Terminal simbolo = aLex.getS();
        if(simbolo == MAS || simbolo == MENOS){
            aLex.escanear();   
        }
        
        termino(base, desp);
        
        if(simbolo == MENOS){
            genCod.menosUnario();
        }
        
        while(aLex.getS() == MAS || aLex.getS() == MENOS){
            simbolo = aLex.getS();
            aLex.escanear();
            termino(base, desp);
            
            if(simbolo == MAS){
                genCod.suma();
            }else{
                genCod.resta();
            }
        }
    }
    
    private void termino(int base, int desp) throws IOException{

        factor(base, desp);       
        while(aLex.getS() == POR || aLex.getS() == DIVIDIDO){
            Terminal simbolo = aLex.getS();
            aLex.escanear();
            factor(base, desp);
            
            if(simbolo == POR){
                genCod.multiplicar();
            }else{
                genCod.dividir();
            }
        }
    }
    
    private void factor(int base, int desp) throws IOException{
        
        switch(aLex.getS()){
                case IDENTIFICADOR:
                    String nombre = aLex.getCad();
                    IdentificadorBean identificador = aSem.buscar(nombre, base + desp - 1, 0);
                    if(identificador == null){
                        indErrores.mostrar(20, nombre, aLex.getLinea(), aLex.getColumna());
                    }else if (identificador.getTipo() == PROCEDURE){
                        indErrores.mostrar(17, nombre, aLex.getLinea(), aLex.getColumna());
                    }else{
                        aLex.escanear();
                        if(identificador.getTipo() == VAR){
                            genCod.generarFactorVar(identificador.getValor());
                        }else{
                            genCod.generarFactorNum(identificador.getValor());
                        }
                    }
                    break;
                     
                case NUMERO:
                    String valor = aLex.getCad();
                    aLex.escanear();
                    genCod.generarFactorNum(Integer.parseInt(valor));
                    break;
                     
                case ABRE_PARENTESIS:
                    aLex.escanear();
                    expresion(base, desp);
                    escaneoSimple(aLex.getS(),CIERRA_PARENTESIS,9);
                    break;
                
                case SQR:
                    aLex.escanear();
                    escaneoSimple(aLex.getS(),ABRE_PARENTESIS,8);
                    nombre = aLex.getCad();
                    identificador = aSem.buscar(nombre, base + desp - 1, 0);
                    
                    if(identificador == null){
                        indErrores.mostrar(13, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
                    }else if(identificador.getTipo() == PROCEDURE || identificador.getTipo() == CONST){
                        indErrores.mostrar(16,aLex.getCad(),aLex.getLinea(),aLex.getColumna());
                    }else{
                        expresion(base, desp);  
                    }
                    escaneoSimple(aLex.getS(),CIERRA_PARENTESIS,9);
                    genCod.sqr();
                    break;
                 
                default:
                    indErrores.mostrar(18, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
             }
    }
    
    private void escaneoSimple(Terminal recibida,  Terminal esperada, int cod) throws IOException{
        
        if(recibida==esperada){
            aLex.escanear();
        }else{
            indErrores.mostrar(cod, aLex.getCad(), aLex.getLinea(), aLex.getColumna());
        }
    }

    private void asigConst(int base, int desp) throws IOException{
        
        aLex.escanear();
        String nombre = aLex.getCad();
        escaneoSimple(aLex.getS(),IDENTIFICADOR,2);
        IdentificadorBean identificador = aSem.buscar(nombre, base + desp - 1, base );

        if(identificador != null){
            indErrores.mostrar(19, nombre, aLex.getLinea(), aLex.getColumna());
        }else{
            
            escaneoSimple(aLex.getS(),IGUAL,11);
            String valor = aLex.getCad();
            if(valor.equals("-")){
                aLex.escanear();
                valor += aLex.getCad();
            }
            escaneoSimple(aLex.getS(),NUMERO,12);
            aSem.cargar(base + desp, nombre, CONST, Integer.parseInt(valor));
        }
    }
}