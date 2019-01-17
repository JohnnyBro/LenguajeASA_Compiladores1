package Acciones;

import Analizadores.Lexico;
import Analizadores.Nodo;
import Analizadores.Sintactico;
import GUI.PanelPrincipal;
import Interprete.Interprete;
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
    
    public void analizar(String entrada, Data d)
    {
        Reader reader = new StringReader(entrada);
        Lexico analizador_lexico =  new Lexico(reader);
        Sintactico analizador_sintactico = new Sintactico(analizador_lexico);
        
        
        /*Actualizando listas
        como todos los objetos en java son por referencia :D
        incluso tambien se pudo creado una variable del tipo Data en los 
        analizadores y pasarle un objeto de tipo data "d", pero por fines practicos
        solo se pasaron las listas :)
        */
        
        analizador_lexico.lista_errores = d.lista_errores;
        analizador_sintactico.lista_errores = d.lista_errores;
        //lista_errores=d.lista_errores;
        
      
        
            try
        {
            analizador_sintactico.parse();
            d.raiz = analizador_sintactico.raiz;
            Interprete inter = new Interprete(d.raiz,d.lista_errores);
            //Graficador g= new Graficador();
            //g.graficarAST(d.raiz);
           
        }
        catch (Exception ex)
        {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("! ============================================================ Analisis Abortado");
            JOptionPane.showMessageDialog(null,"Analisis abortado","Proyecto 2 - JFLex/Cup y AST",JOptionPane.ERROR_MESSAGE);
            
            GUI.PanelPrincipal panel=new PanelPrincipal();
            
            return;
        }
        if(analizador_lexico.lista_errores.size()==0 && analizador_sintactico.lista_errores.size()==0)
        {
            System.out.println("! ============================================================ Analisis Completado");
        JOptionPane.showMessageDialog(null,"Analisis terminado con exito","Ejemplo 3 - JFLex/Cup y AST",JOptionPane.INFORMATION_MESSAGE);
        }else
        {
            PanelPrincipal panel=new PanelPrincipal();
            JOptionPane.showMessageDialog(null,"Existen Errores","Proyecto 2 - JFLex/Cup y AST",JOptionPane.ERROR_MESSAGE);
            int l1=analizador_lexico.lista_errores.size();
            int l2=analizador_sintactico.lista_errores.size();
            
//            if(l1>0)
//            {
//                for(int i=0; i<l1;i++)
//                {
//                     panel.mostrarErrores(analizador_lexico.lista_errores.get(i).tipo+"**** "+analizador_lexico.lista_errores.get(i).lexema+" "+
//                     analizador_lexico.lista_errores.get(i).linea+" "+analizador_lexico.lista_errores.get(i).columna+" "+analizador_lexico.lista_errores.get(i).descripcion);
//                }
//            }
//            
//            if(l2>0)
//            {
//                for(int i=0; i<l2;i++)
//                {
//                    panel.mostrarErrores(analizador_sintactico.lista_errores.get(i).tipo+" "+analizador_sintactico.lista_errores.get(i).lexema+" "+
//                     analizador_sintactico.lista_errores.get(i).linea+" "+analizador_sintactico.lista_errores.get(i).columna+" "+analizador_sintactico.lista_errores.get(i).descripcion);
//                }
//            }
            
            return;
        }
        
    }      
    
}
