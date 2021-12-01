package compilador;

public class AnalizadorSemantico {
    
    private IdentificadorBean[] tabla;
    private IndicadorDeErrores indErrores;

    public AnalizadorSemantico(IndicadorDeErrores indErrores) {
        tabla = new IdentificadorBean[Constantes.MAXIDENT];
        this.indErrores = indErrores;
    }
    
    public IdentificadorBean buscar(String ident, int desde, int hasta){
        IdentificadorBean id = null;
        int i = desde;
        while(i >= hasta && i >= 0  && i < tabla.length){
            if( tabla[i].getNombre().toLowerCase().equals(ident.toLowerCase())){
                id =  tabla[i];
                break;
            }
            i--;
        }
        return id;
    }
    
    public void cargar(int pos, String nom, Terminal tipo , int val){
        if(pos >= 256){
            indErrores.mostrar(21, nom, 0, 0);
        }else{
            tabla[pos] = new IdentificadorBean(nom, tipo, val);
        } 
    }
}