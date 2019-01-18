package Acciones;

import Analizadores.Lexico;
import Analizadores.Nodo;
import Analizadores.Sintactico;
import GUI.PanelPrincipal;
import Interprete.Interprete;
import Interprete.Metodo;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author johnnybravo
 */
public class Compilador
{
    //public static ArrayList<ErrorT> lista_errores;
    public StringBuilder e = new StringBuilder();
    
    public void analizar(String entrada, Data d)
    {
        Reader reader = new StringReader(entrada);
        Lexico analizador_lexico =  new Lexico(reader);
        Sintactico analizador_sintactico = new Sintactico(analizador_lexico);
        Interprete inter = new Interprete();
        Reportes.ReporteErrores err=new Reportes.ReporteErrores();
        
        
        
        
        
        /*Actualizando listas
        como todos los objetos en java son por referencia :D
        incluso tambien se pudo creado una variable del tipo Data en los 
        analizadores y pasarle un objeto de tipo data "d", pero por fines practicos
        solo se pasaron las listas :)
        */
        
        analizador_lexico.lista_errores = d.lista_errores;
        analizador_sintactico.lista_errores = d.lista_errores;
        inter.LSemanticos=d.lista_errores;
        err.lista_errores=d.lista_errores;
        //lista_errores=d.lista_errores;
        
      
        e.append("\n");
            try
        {
            analizador_sintactico.parse();
            d.raiz = analizador_sintactico.raiz;
            inter.Interprete(d.raiz);
            //
            
           
        }
        catch (Exception ex)
        {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("! ============================================================ Analisis Abortado");
            JOptionPane.showMessageDialog(null,"Analisis abortado","Proyecto 2 - JFLex/Cup y AST",JOptionPane.ERROR_MESSAGE);
            
           
            
            
            
        }
        
        if (d.lista_errores.size() != 0) {

            for (ErrorT n : d.lista_errores) {

                e.append("Error " + n.tipo + ": ");
                e.append("Lexema: " + n.lexema + " ");
                e.append("Linea: " + n.linea + " ");
                e.append("Columna: " + n.columna + " ");
                e.append("Descripcion: " + n.descripcion);
                e.append("\n");
            }
            err.generarReporteErrores();
            JOptionPane.showMessageDialog(null, "Existen Errores", "Proyecto 2 - JFLex/Cup y AST", JOptionPane.ERROR_MESSAGE);
        } else 
        {
            err.generarReporteErrores();
            System.out.println("! ============================================================ Analisis Completado");
            JOptionPane.showMessageDialog(null, "Analisis terminado con exito", "Ejemplo 3 - JFLex/Cup y AST", JOptionPane.INFORMATION_MESSAGE);
        }
        
        e.append(inter.consola.toString());

        PanelPrincipal.txtConsola.setText(e.toString());
        Graficador g = new Graficador();
        g.graficarAST(d.raiz);
        
    }      
    
}
