/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interprete;

import Analizadores.Nodo;
import java.util.HashMap;
import java.util.LinkedList;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author johnnybravo
 */
public class Interprete {

    LinkedList<Metodo>lista_metodos;
    LinkedList<HashMap<String,Variable>>ambitos;
    HashMap<String, Variable> actual;
    Nodo principal;
    int num=0;
    
    
    
    
    public Interprete(Nodo raiz) {
        this.lista_metodos = new LinkedList<>();
        this.ambitos = new LinkedList<>();
        analizar(raiz);
        ejecutar(principal);
    }
    
    void aumentar_ambito(){
        this.ambitos.add(new HashMap<String,Variable>());
        this.actual=this.ambitos.getFirst();
    }
    
    
      void disminuir_ambito(){
        this.ambitos.removeFirst();
        this.actual=this.ambitos.getFirst();
    }
    
    public  void analizar(Nodo raiz){
        switch(raiz.valor.toUpperCase()){
            case "INICIO":
                for(Nodo a: raiz.hijos){
                    analizar(a);
                }
                break;
            case "ENCABEZADOS":
                
                break;
            case "SENTS":
                 for(Nodo a: raiz.hijos){
                    analizar(a);
                }
                break;
            case "FUNCION":
                guardar_funcion(raiz);
                break;
            case "METODO":
                guardar_metodo(raiz);
                break;
            case "PRINCIPAL":
                principal=raiz;
                break;
            case "DECLARACION":
                ejecutar_declaracion(raiz);
                break;
            case "ASIGNACION":
                ejecutar_asignacion(raiz);
                break;
        }
    }
    
    private void guardar_funcion(Nodo raiz) {
        String nombre = raiz.hijos.get(1).valor;
        String tipo = raiz.hijos.get(0).valor;
        String nombre_aux=nombre;
        
        Metodo nuevo = new Metodo();
        
        if (raiz.hijos.size() == 3) {//no trae parametros
            nuevo.sentencias = raiz.hijos.get(2);
            
        } else {
            for (Nodo a : raiz.hijos.get(2).hijos) {
                nombre_aux += "_" + a.hijos.get(0).valor;

            }
            nuevo.Parametros=raiz.hijos.get(2);
            nuevo.sentencias = raiz.hijos.get(3);
        }
        
        nuevo.nombre_aux=nombre_aux;
        nuevo.nombre=nombre_aux;
        nuevo.tipo = tipo;
        
        //comparamos
        boolean flag = true;
        for(Metodo m:lista_metodos){
            if(m.nombre.toUpperCase().equals(nombre_aux.toUpperCase())){
                //activar bandera erro ya existe Metodo
                flag=false;
                break;
            }
        }
        
        if(flag){
            lista_metodos.add(nuevo);
        }
    }

    
    private void guardar_metodo(Nodo raiz) {
        String nombre = raiz.hijos.get(0).valor;
        String nombre_aux=nombre;
        Metodo nuevo = new Metodo();
        
        if (raiz.hijos.size() == 2) {//no trae parametros
            nuevo.sentencias = raiz.hijos.get(1);
            
        } else {
            for (Nodo a : raiz.hijos.get(1).hijos) {
                nombre_aux += "_" + a.hijos.get(0).valor;

            }
            nuevo.Parametros=raiz.hijos.get(1);
            nuevo.sentencias = raiz.hijos.get(2);
        }
        
        nuevo.nombre_aux=nombre_aux;
        nuevo.nombre=nombre_aux;
        nuevo.tipo = "VACIO";
        
        //comparamos
        boolean flag = true;
        for(Metodo m:lista_metodos){
            if(m.nombre.toUpperCase().equals(nombre_aux.toUpperCase())){
                //activar bandera erro ya existe metodo
                flag=false;
                break;
            }
        }
        
        if(flag){
            lista_metodos.add(nuevo);
        }
        
        
    }

    private void ejecutar(Nodo raiz) {
        switch(raiz.valor){
            case "PRINCIPAL":
                for(Nodo a: raiz.hijos){
                    ejecutar(a);
                }
                break;
            case "ENCABEZADO":
                break;
            case "DECLARACION":
                ejecutar_declaracion(raiz);
                break;
            case "SENTS":
                 for(Nodo a: raiz.hijos){
                    ejecutar(a);
                }
                break;
            case "ASIGNACION":
                ejecutar_asignacion(raiz);
                break;
                
        }
    }

    private void ejecutar_declaracion(Nodo raiz) {
        String tipo = raiz.hijos.get(0).valor;
        //Object res = null;//evaluar_expresion(raiz.hijos.get(2));
        Object res=evaluar_expresion(raiz.hijos.get(2));
        String tipo_var = retornar_tipo_nombre(res);
        
        if(tipo.equals(tipo_var)){
            for(Nodo a:raiz.hijos){
                guardar_variable(tipo,a.valor,res);
            }
        }
    }
    
      private void ejecutar_asignacion(Nodo raiz) {
        String id = raiz.hijos.get(0).valor;
       // Object res = null;//evaluar_expresion(raiz.hijos.get(1));
        Object res=evaluar_expresion(raiz.hijos.get(1));
        Variable aux = get_variable(id);
        
        if(aux!=null){
            String tipo_var = retornar_tipo_nombre(res);
            if(tipo_var.equals(aux.tipo)){
                aux.valor=res;
            }
        }
    }

    private int retornar_tipo(Object res) {
        //get_object_type
        return -1;
    }

    private String retornar_tipo_nombre(Object res) {
       switch(res.getClass().getTypeName()){
           case "System.Integer":
               return "Entero";
               
       }
       return "-1";
    }

    private void guardar_variable(String tipo,String valor, Object res) {
        if(!actual.containsKey(valor)){
            this.actual.put(valor, new Variable(tipo,valor,res));
        }else{
            //error ya existe variable en el ambito actual
        }
    }

    private Variable get_variable(String id) {
        for(HashMap<String,Variable> lista:ambitos){
            if(lista.containsKey(id)){
                return lista.get(id);
            }
        }
        //no se encontro variable..
        return null;
    }

    private Object evaluar_expresion(Nodo raiz) {
        
        switch(raiz.hijos.size())
        {
            case 3:
                switch(raiz.hijos.get(1).valor)
                {
                    case "+":
                        String val1=raiz.hijos.get(0).valor;
                        String val2=raiz.hijos.get(2).valor;
                        
                        if(isNumeric(val1)==true && isNumeric(val2)==true)
                        {
                            double v1=Double.parseDouble(val1);
                            double v2=Double.parseDouble(val2);
                            return v1+v2;
                        }else if(isNumeric(val1)==true && isNumeric(val2)==false)
                        {
                            if(val2.compareToIgnoreCase("falso")==0)
                            {
                                double v1=Double.parseDouble(val1);
                                return v1;
                            }else if(val2.compareToIgnoreCase("verdadero")==0)
                            {
                                double v1=Double.parseDouble(val1);
                                return v1+1;
                            }else
                            {
                                return val1+val2;
                            }
                        }else if(isNumeric(val1)==false && isNumeric(val2)==true)
                        {
                            if(val1.compareToIgnoreCase("falso")==0)
                            {
                                double v2=Double.parseDouble(val2);
                                return v2;
                            }else if(val1.compareToIgnoreCase("verdadero")==0)
                            {
                                double v2=Double.parseDouble(val2);
                                return v2+1;
                            }else
                            {
                                return val1+val2;
                            }
                        }else if(isNumeric(val1)==false && isNumeric(val2)==false)
                        {
                            if(val1.compareToIgnoreCase("verdadero")==0 && val2.compareToIgnoreCase("verdadero")==0)
                            {
                                return 2;
                            }else if(val1.compareToIgnoreCase("falso")==0 && val2.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else if(val1.compareToIgnoreCase("verdadero")==0 || val2.compareToIgnoreCase("falso")==0)
                            {
                                return 1;
                            }else if(val1.compareToIgnoreCase("falso")==0 || val2.compareToIgnoreCase("verdadero")==0)
                            {
                                return 1;
                            }else
                            {
                                if(val1.compareToIgnoreCase("verdadero")==0)
                                {
                                    val2="1 "+val2;
                                    return val2;
                                }else if(val1.compareToIgnoreCase("falso")==0)
                                {
                                    val2 = "0 "+ val2;
                                    return val2;
                                }else if(val2.compareToIgnoreCase("verdadero")==0)
                                {
                                    val1+=" 1";
                                    return val1;
                                }else if(val2.compareToIgnoreCase("falso")==0)
                                {
                                    val1+=" 0";
                                    return val1;
                                }else
                                {
                                    return val1+val2;
                                }
                            }
                        }
                        return val1+val2;
                }
                break;
            case 2:
                break;
            case 1:
                break;
            default:
                break;
                
        }
        return null;
    }
    
    public boolean isNumeric(String str)
    {
        try
        {
          double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
          return false;
        }
        return true;
    }
    
    public boolean esEntero(int numero)
    {
        if (numero % 1 == 0) 
        {
            return true;
        }
        
        return false;
    }

    
    
   
    
}
