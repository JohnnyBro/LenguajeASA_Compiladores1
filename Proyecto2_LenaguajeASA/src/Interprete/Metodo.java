/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interprete;

import Analizadores.Nodo;
import java.util.LinkedList;
import javafx.scene.Node;

/**
 *
 * @author johnnybravo
 */
public class Metodo {
    public String nombre;
    public Nodo Parametros;
    public String nombre_aux;
    public Nodo sentencias;
    public String tipo;

    public Metodo() {
    }

    public Metodo(String nombre, Nodo Parametros, String nombre_aux) {
        this.nombre = nombre;
        this.Parametros = Parametros;
        this.nombre_aux = nombre_aux;
    }
    
    
    
}
