/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interprete;

import Analizadores.Nodo;
import Acciones.*;
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
    Compilador comp=new Compilador();
    
    
    
    public Interprete(Nodo raiz) {
        this.lista_metodos = new LinkedList<>();
        this.ambitos = new LinkedList<>();
        aumentar_ambito();
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
                ErrorT error=new ErrorT(nombre_aux, "Semantico", "Ya existe un metodo con este nombre");
                comp.lista_errores.add(error);
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
            for(Nodo a:raiz.hijos.get(1).hijos){
                guardar_variable(tipo,a.valor,res);
            }
        }else
        {
            //ERROR NO COINCIDEN LOS TIPOS;
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
        
       if(this.isNumeric((String)res))
       {
           if(this.esEntero((String)res))
           {
               return "ENTERO";
           }else
           {
               return "DECIMAL";
           }
               
       }else
       {
           switch((String)res)
           {
               case "FALSO":
                   return "BOOLEANO";
               case "VERDADERO":
                   return "BOOLEANO";
               default:
                   return "TEXTO";
           }
       }
       //return "-1";
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
                        String val1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String val2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
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
                    case "-":
                        String res1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String res2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(res1)==true && isNumeric(res2)==true)
                        {
                            double r1=Double.parseDouble(res1);
                            double r2=Double.parseDouble(res2);
                            return r1-r2;
                        }else if(isNumeric(res1)==true && isNumeric(res2)==false)
                        {
                            if(res2.compareToIgnoreCase("falso")==0)
                            {
                                double r1=Double.parseDouble(res1);
                                return r1;
                            }else if(res2.compareToIgnoreCase("verdadero")==0)
                            {
                                double r1=Double.parseDouble(res1);
                                return r1-1;
                                
                            }else
                            {
                                //Error No se puede restar un valor numerico con un texto
                            }
                        }else if(isNumeric(res1)==false && isNumeric(res2)==true)
                        {
                            if(res1.compareToIgnoreCase("verdadero")==0)
                            {
                                double r2=Double.parseDouble(res2);
                                return 1-r2;
                            }else if(res1.compareToIgnoreCase("falso")==0)
                            {
                                double r2=Double.parseDouble(res2);
                                return 0-r2;
                            }else
                            {
                                //Error no se puede restar un valor Texto con un numerico
                            }
                        }else if(isNumeric(res1)==false && isNumeric(res2)==false)
                        {
                            if(res1.compareToIgnoreCase("verdadero")==0)
                            {
                                if(res2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(res2.compareToIgnoreCase("falso")==0)
                                {
                                    return 1;
                                }else
                                {
                                    //ERROR no se puede restar un valor booleano con un texto
                                }
                            }else if(res1.compareToIgnoreCase("falso")==0)
                            {
                                if(res2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return -1;
                                }else if(res2.compareToIgnoreCase("falso")==0)
                                {
                                    return 0;
                                }else
                                {
                                    //ERROR no se puede restar un valor booleano con un texto
                                }
                            }else
                            {
                                //Error no se pueden restar valores de tipo texto
                            }
                        }
                        break;
                    case "*":
                        String por1 = (String)evaluar_expresion(raiz.hijos.get(0));
                        String por2 = (String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(por1)==true && isNumeric(por2)==true)
                        {
                            double p1=Double.parseDouble(por1);
                            double p2=Double.parseDouble(por2);
                            return p1*p2;
                        }else if(isNumeric(por1)==true && isNumeric(por2)==false)
                        {
                            if(por2.compareToIgnoreCase("verdadero")==0)
                            {
                                double p2=Double.parseDouble(por2);
                                return p2;
                            }else if(por2.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else
                            {
                                //ERROR no se puede multiplicar un valor Numerico por un Texto
                            }
                            
                        }else if(isNumeric(por1)==false && isNumeric(por2)==true)
                        {
                            if(por1.compareToIgnoreCase("verdadero")==0)
                            {
                                double p1=Double.parseDouble(por1);
                                return p1;
                            }else if(por1.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else
                            {
                                //ERROR no se puede multiplicar un valor Numerico por un Texto
                            }
                        }else if(isNumeric(por1)==false && isNumeric(por2)==false)
                        {
                            if(por1.compareToIgnoreCase("verdadero")==0)
                            {
                                if(por2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 1;
                                }else if(por2.compareToIgnoreCase("falso")==0)
                                {
                                    return 0;
                                }else
                                {
                                    //ERROR NO SE PUEDE MULTIPLICAR UN VALOR NUMERICO POR UN TEXTO
                                }   
                            }else if(por1.compareToIgnoreCase("falso")==0)
                            {
                                if(por2.compareToIgnoreCase("verdadero")==0 || por2.compareToIgnoreCase("falso")==0)
                                {
                                    return 0;
                                }else
                                {
                                    //ERROR NO SE PUEDE MULTIPLICAR UN VALOR NUMERICO POR UN TEXTO
                                }
                            }else
                            {
                                //ERROR NO SE PUEDEN MULTIPLICAR VALORES DE TIPO TEXTO
                            }
                        }
                        break;
                    case "/":
                        String div1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String div2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(div1)==true && isNumeric(div2)==true)
                        {
                            double d1=Double.parseDouble(div1);
                            double d2=Double.parseDouble(div2);
                            
                            if(d2==0)
                            {
                                //ERROR, NO SE PUEDE DIVIDIR SOBRE 0
                            }else
                            {
                                return d1/d2;
                            }
                        }else if(isNumeric(div1)==true && isNumeric(div2)==false)
                        {
                            if(div2.compareToIgnoreCase("verdadero")==0)
                            {
                                double d1=Double.parseDouble(div1);
                                return d1;
                            }else if(div2.compareToIgnoreCase("falso")==0)
                            {
                                //ERROR no se puede dividir sobre 0
                            }else
                            {
                                //ERROR impompatibilidad de tipos
                            }
                        }else if(isNumeric(div1)==false && isNumeric(div2)==true)
                        {
                            if(div1.compareToIgnoreCase("verdadero")==0)
                            {
                                double d2=Double.parseDouble(div2);
                                return 1/d2;
                            }else if(div1.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else
                            {
                                //Error imcompatiblidad de tipos
                            }
                        }else if(isNumeric(div1)==false && isNumeric(div2)==false)
                        {
                            if(div1.compareToIgnoreCase("verdadero")==0)
                            {
                             if(div2.compareToIgnoreCase("verdadero")==0)
                             {
                                return 1;
                             }else if(div2.compareToIgnoreCase("falso")==0)
                             {
                                //ERROR no se puede dividir sobre 0
                             }else
                             {
                                 // ERROR INCOMPATIBILIDAD DE TIPOS
                             }
                            }else if( div1.compareToIgnoreCase("falso")==0)
                            {
                                if(div2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(div2.compareToIgnoreCase("falso")==0)
                                {
                                    //ERROR NO SE PUEDE DIVIDIR SOBRE 0
                                }else
                                {
                                    //ERROR INCOMPATIBLIDAD DE TIPOS
                                }
                            }else
                            {
                                //ERROR NO SE PUEDE OPERAR CON VALORES DE TIPO TEXTO
                            }
                        }
                        break;
                    case "%":
                        
                        String mod1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String mod2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(mod1)==true && isNumeric(mod2)==true)
                        {
                            double m1=Double.parseDouble(mod1);
                            double m2=Double.parseDouble(mod2);
                            
                            return m1%m2;
                            
                        }else if(isNumeric(mod1)==true && isNumeric(mod2)==false)
                        {
                            if(mod2.compareToIgnoreCase("verdadero")==0)
                            {
                                double m1=Double.parseDouble(mod1);
                                return m1%1;
                            }else if(mod2.compareToIgnoreCase("falso")==0)
                            {
                                //Error no se puede dividir sobre 0
                            }else
                            {
                                //INCOMPATIBILIDAD DE TIPOS
                            }
                            
                        }else if(isNumeric(mod1)==false && isNumeric(mod2)==true)
                        {
                            if(mod1.compareToIgnoreCase("verdadero")==0)
                            {
                                double m2=Double.parseDouble(mod2);
                                return 1%m2;
                            }else if(mod1.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else
                            {
                                //ERROR INCOMPATIBILIDAD DE TIPOS
                            }
                            
                        }else if(isNumeric(mod1)==false && isNumeric(mod2)==false)
                        {
                            if(mod1.compareToIgnoreCase("verdadero")==0)
                            {
                                if(mod2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(mod2.compareToIgnoreCase("falso")==0)
                                {
                                    //ERROR NO SE PUEDE DIIDIR SOBRE 0
                                }else
                                {
                                    //ERROR INCOMPATIBILIDAD DE TIPOS
                                }
                            }else if(mod1.compareToIgnoreCase("falso")==0)
                            {
                                if(mod2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(mod2.compareToIgnoreCase("falso")==0)
                                {
                                    //Error no se puede dividir sobre 0
                                }else
                                {
                                    //Error incompatibilidad de tipos
                                }
                            }else
                            {
                                //ERROR NO SE PUEDE OPERAR CON VALORES DE TIPO TEXTO
                            }
                        }
                        break;
                    case "^":
                        String pot1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String pot2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(pot1)==true && isNumeric(pot2)==true)
                        {
                            double pote1=Double.parseDouble(pot1);
                            double pote2=Double.parseDouble(pot2);
                            double pot=Math.pow(pote1, pote2);
                            return pot;
                        }else if(isNumeric(pot1)==true && isNumeric(pot2)==false)
                        {
                            if(pot2.compareToIgnoreCase("verdadero")==0)
                            {
                                double pote1=Double.parseDouble(pot1);
                                return pote1;
                            }else if(pot2.compareToIgnoreCase("falso")==0)
                            {
                                return 1;
                            }else
                            {
                                //Error incompatibilidad de tipos;
                            }
                            
                        }else if(isNumeric(pot1)==false && isNumeric(pot2)==true)
                        {
                            if(pot1.compareToIgnoreCase("verdadero")==0)
                            {
                                return 1;
                            }else if(pot1.compareToIgnoreCase("falso")==0)
                            {
                                return 0;
                            }else 
                            {
                                //incompatibilidad de tipos
                            }
                            
                        }else if(isNumeric(pot1)==false && isNumeric(pot2)==false)
                        {
                            if(pot1.compareToIgnoreCase("verdadero")==0)
                            {
                                if(pot2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 1;
                                }else if(pot2.compareToIgnoreCase("falso")==0)
                                {
                                    return 1;
                                }else
                                {
                                    //Error incompatiblidad de tipos
                                }
                                
                            }else if(pot1.compareToIgnoreCase("falso")==0)
                            {
                                if(pot1.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(pot1.compareToIgnoreCase("falso")==0)
                                {
                                    //Error instruccion no soportada 0^0;
                                }else 
                                {
                                    //Error incompatibilidad de tipos
                                }
                            }else 
                            {
                                //Error no se puede operar con valores de tipo texto
                            }
                        }
                        break;
                    case "==":
                        String igual1=(String)evaluar_expresion(raiz.hijos.get(0));
                        String igual2=(String)evaluar_expresion(raiz.hijos.get(2));
                        
                        if(isNumeric(igual1)==true && isNumeric(igual2)==true)
                        {
                            if(igual1.compareTo(igual2)==0)
                            {
                                return 1;
                            }else
                            {
                                return 0;
                            }
                            
                        }else if(isNumeric(igual1)==false && isNumeric(igual2)==false)
                        {
                            if(igual1.compareToIgnoreCase("verdadero")==0)
                            {
                                if(igual2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 1;
                                }else if(igual2.compareToIgnoreCase("falso")==0)
                                {
                                    return 0;
                                }else 
                                {
                                    //Error incompatibilidad de tipos
                                }
                            }else if(igual1.compareToIgnoreCase("falso")==0)
                            {
                                if(igual2.compareToIgnoreCase("verdadero")==0)
                                {
                                    return 0;
                                }else if(igual2.compareToIgnoreCase("falso")==0)
                                {
                                    return 1;
                                }else
                                {
                                    //incompatibilidad de tipos
                                }
                            }else
                            {
                                if(igual1.compareTo(igual2)==0)
                                {
                                    return 1;
                                }else
                                {
                                    return 0;
                                }
                            }
                            
                        }else
                        {
                            //error incompatibilidad de tipos
                        }
                            
                        break;
                    case "&&":
                    break;
                }
                break;
            case 2:
                if(raiz.hijos.get(0).valor.compareToIgnoreCase("-")==0)
                return (int)evaluar_expresion(raiz.hijos.get(1))*-1;
            case 1:
                return evaluar_expresion(raiz.hijos.get(0));
            case 0:
                switch (raiz.valor)
                    {
                        case "EXP":
                            return evaluar_expresion(raiz.hijos.get(0));
                        default:
                            return raiz.valor;
                    }
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
    
    public boolean esEntero(String num)
    {
         
        double numero=Double.parseDouble(num);
        if (numero % 1 == 0) 
        {
            return true;
        }
        
        return false;
    }

    
    
   
    
}
