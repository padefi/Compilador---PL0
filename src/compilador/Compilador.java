package compilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.swing.JFileChooser;

public class Compilador {

   
    public static void main(String[] args){
        
        try {
           
            //Reader r = new FileReader (new File ("C:\\Users\\Pablo\\Desktop\\Compilador\\PL0\\BIEN-00.PL0"));
            String nomArch = "";
            
            System.out.println("Seleccione el archivo que desea ejecutar");
            JFileChooser fc = new JFileChooser("");
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
               nomArch = fc.getSelectedFile().getPath();
            }
            
            if(nomArch.isEmpty()){
                System.out.println("No se ha cargado ningún archivo");
            }else{

                Reader r = new FileReader (new File (nomArch));

                AnalizadorLexico aLex = new AnalizadorLexico(r);
                GeneradorDeCodigo genCod = new GeneradorDeCodigo(nomArch + ".exe");
                IndicadorDeErrores indErrores = new IndicadorDeErrores();
                AnalizadorSemantico aSem = new AnalizadorSemantico(indErrores);
                AnalizadorSintactico aSint = new AnalizadorSintactico(aLex, aSem, genCod, indErrores);

                aSint.analizar();

                r.close();
            }
            
        }catch(Exception e){
            System.out.println("Ha ocurrido un error al abrir el archivo");
            e.printStackTrace();
        }
    }
    
}
