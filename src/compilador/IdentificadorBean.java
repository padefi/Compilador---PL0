package compilador;

public class IdentificadorBean {
    
    private String nombre;
    private Terminal tipo;
    private int valor ;
    
    public IdentificadorBean(String nom, Terminal tipo, int val) {
        this.nombre = nom;
        this.tipo = tipo;
        this.valor = val;
    }

    IdentificadorBean() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Terminal getTipo() {
        return tipo;
    }

    public void setTipo(Terminal tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
