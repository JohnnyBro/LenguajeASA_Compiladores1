/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interprete;

/**
 *
 * @author johnnybravo
 */
public class Variable {
    public String nombre;
    public Object valor;
    public String tipo;

    public Variable() {
    }

    public Variable(String tipo,String nombre, Object valor) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.valor = valor;
    }
    
    
}
