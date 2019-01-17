/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interprete;

import Analizadores.Nodo;
import Acciones.*;
import java.util.ArrayList;
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
    public ArrayList<ErrorT> LSemanticos;
   
    
    
    
    public Interprete(Nodo raiz, ArrayList<ErrorT> lista) {
        this.LSemanticos=lista;
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
    
//    private void guardar_funcion(Nodo raiz) {
//        String nombre = raiz.hijos.get(1).valor;
//        String tipo = raiz.hijos.get(0).valor;
//        String nombre_aux=nombre;
//        
//        Metodo nuevo = new Metodo();
//        
//        if (raiz.hijos.size() == 3) {//no trae parametros
//            nuevo.sentencias = raiz.hijos.get(2);
//            
//        } else {
//            for (Nodo a : raiz.hijos.get(2).hijos) {
//                nombre_aux += "_" + a.hijos.get(0).valor;
//
//            }
//            nuevo.Parametros=raiz.hijos.get(2);
//            nuevo.sentencias = raiz.hijos.get(3);
//        }
//        
//        nuevo.nombre_aux=nombre_aux;
//        nuevo.nombre=nombre_aux;
//        nuevo.tipo = tipo;
//        
//        //comparamos
//        boolean flag = true;
//        for(Metodo m:lista_metodos){
//            if(m.nombre.toUpperCase().equals(nombre_aux.toUpperCase())){
//                //activar bandera erro ya existe Metodo
//                ErrorT error=new ErrorT(nombre_aux, "Semantico", "Ya existe un metodo con este nombre");
//                comp.lista_errores.add(error);
//                flag=false;
//                break;
//            }
//        }
//        
//        if(flag){
//            lista_metodos.add(nuevo);
//        }
//    }

    
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
         switch(res.getClass().getTypeName())
        {
            case "java.lang.Integer":
                return 3;
            case "java.lang.Double":
                return 2;
            case "java.lang.String":
                return 1;
            case "java.lang.Boolean":
                return 4;
            default:
                return -1;
        }
    }

    private String retornar_tipo_nombre(Object res) {
        String nombre = res.getClass().getTypeName();
        
        switch(res.getClass().getTypeName())
        {
            case "java.lang.Integer":
                return "ENTERO";
            case "java.lang.Double":
                return "DECIMAL";
            case "java.lang.String":
                return "TEXTO";
            case "java.lang.Boolean":
                return "BOOLEANO";
            default:
                return "-1";
        }
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
                    case "+": return evaluar_Mas(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "-": return evaluar_Menos(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "*": return evaluar_Por(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "/": return evaluar_Dividir(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "%": return evaluar_Modulo(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "^": return evaluar_Potencia(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "==": return evaluar_Igual(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "&&":
                    break;
                }
                break;
            case 2:
                if(raiz.hijos.get(0).valor.compareToIgnoreCase("-")==0)
                return (int)evaluar_expresion(raiz.hijos.get(1))*-1;
            case 1:
                switch (raiz.valor)
                {
                    case "EXP":
                        return evaluar_expresion(raiz.hijos.get(0));
                    case "TOKNUMERO":
                        return evaluar_Numero(raiz.hijos.get(0));
                    case "TOKVERDADERO":
                        return true;
                    case "TOKFALSO":
                        return false;    
                    default:
                        return evaluar_expresion(raiz.hijos.get(0));
                }
                
                
            case 0:
                return raiz.valor;
                   
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

    private Object evaluar_Mas(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        int tipo1 = retornar_tipo(val1);
        int tipo2 = retornar_tipo(val2);
        
        if(tipo1==-1 && tipo2==-1){
            //error evaluacion dio null
            
            return -3092;
        }
        
        if(tipo1==1|| tipo2==1){
            return val1.toString()+ val2.toString();
        }else{
            return Double.parseDouble(val1.toString())+Double.parseDouble(val2.toString());
        }
        
        /*else if( tipo1==2|| tipo2==2){
            return Double.parseDouble(val1.toString())+Double.parseDouble(val2.toString())
        }else if( tipo1==3|| tipo2==3){
            return Double.parseDouble(val1.toString())+Double.parseDouble(val2.toString())
        }*/
        
        
      /*  switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        return i1+i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1+d2;
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1+1;
                        }else
                        {
                            return b1;
                        }
                    case "java.lang.String":
                        String s1=val1.toString();
                        String s2=val2.toString();
                        return s1+s2;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        return i1+i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1+d2;
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1+1;
                        }else
                        {
                            return b1;
                        }
                    case "java.lang.String":
                        String s1=val1.toString();
                        String s2=val2.toString();
                        return s1+s2;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return i2+1;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return d2+1;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 2;
                            }else
                            {
                                return 1;
                            }
                        case "java.lang.String":
                            
                            String s2=val2.toString();
                            return "1"+s2;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 1;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            
                            String s2=val2.toString();
                            return "0"+s2;
                    }
                }
                
                break;
            case "java.lang.String":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                    case "java.lang.Double":
                    case "java.lang.String":
                        String s1=val1.toString();
                        String s2=val2.toString();
                        return s1+s2;
                    case "java.lang.Boolean":
                        String aux1=val1.toString();
                        boolean b=Boolean.parseBoolean(val2.toString());
                        if(b==true)
                        {
                            return aux1+"1";
                        }else
                        {
                            return aux1+"0";
                        }
                    
                }   
                break;
        }
    return -1;*/
    }

    private Object evaluar_Menos(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        
        int tipo1 = retornar_tipo(val1);
        int tipo2 = retornar_tipo(val2);
        
        if(tipo1==-1 || tipo2==-1){
            //error evaluacion dio null
            return -3092;
        }
        
        if(tipo1==1|| tipo2==1){
            //agregas a error no se pueden restar stirngs
            return -3092;
        }else{
            return Double.parseDouble(val1.toString())- Double.parseDouble(val2.toString());
        }
    }
        /*
        switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        return i1-i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1-d2;
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1-1;
                        }else
                        {
                            return b1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede restar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        return i1-i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1-d2;
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1-1;
                        }else
                        {
                            return b1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede restar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 1-i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 1-d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0;
                            }else
                            {
                                return 1;
                            }
                        case "java.lang.String":
                            
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede restar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return i2*-1;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return d2*-1;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return -1;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede restar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
                }
                
                break;
            case "java.lang.String":
                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede restar variables de tipo Texto");
                LSemanticos.add(error);
                        
                break;
        }
    return -1;
        
    }*/

    private Object evaluar_Por(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        return i1*i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1*d2;
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1*1;
                        }else
                        {
                            return b1*0;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna,"Semantico", "No se puede multiplicar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        return i1*i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1*d2;
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1*1;
                        }else
                        {
                            return b1*0;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(),der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 1*i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 1*d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 1;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 0*i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 0*d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
                }
                
                break;
            case "java.lang.String":
                ErrorT error=new ErrorT(val2.toString(), izq.linea, izq.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                LSemanticos.add(error);
                        
                break;
        }
    return -1;
    } 

    private Object evaluar_Dividir(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        if(i2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return i1/i2;
                        }
                        
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        if(d2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return d1/d2;
                        }
                        
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1/1;
                        }else
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna,"Semantico", "No se puede devidir variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        if(i2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return i1/i2;
                        }
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        if(d2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return d1/d2;
                        }
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1/1;
                        }else
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(),der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            if(i2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 1/i2;
                            }
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            if(d2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 1/d2;
                            }
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 1;
                            }else
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }
                        case "java.lang.String":
                            
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            if(i2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 0/i2;
                            }
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            if(d2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 0/d2;
                            }
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0;
                            }else
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
                }
                
                break;
            case "java.lang.String":
                ErrorT error=new ErrorT(val2.toString(), izq.linea, izq.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                LSemanticos.add(error);
                        
                break;
        }
    return -1;
    }

    private Object evaluar_Modulo(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        if(i2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return i1%i2;
                        }
                        
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        if(d2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return d1%d2;
                        }
                        
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1%1;
                        }else
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna,"Semantico", "No se puede devidir variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        if(i2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return i1%i2;
                        }
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        if(d2==0)
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }else
                        {
                            return d1%d2;
                        }
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1%1;
                        }else
                        {
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                            LSemanticos.add(error);
                            return -1;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(),der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            if(i2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 1%i2;
                            }
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            if(d2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 1%d2;
                            }
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 1;
                            }else
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }
                        case "java.lang.String":
                            
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            if(i2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 0%i2;
                            }
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            if(d2==0)
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }else
                            {
                                return 0%d2;
                            }
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0%1;
                            }else
                            {
                                ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir sobre 0");
                                LSemanticos.add(error);
                                return -1;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
                }
                
                break;
            case "java.lang.String":
                ErrorT error=new ErrorT(val2.toString(), izq.linea, izq.columna, "Semantico", "No se puede dividir variables de tipo Texto");
                LSemanticos.add(error);
                        
                break;
        }
    return -1;
    }

    private Object evaluar_Potencia(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        switch (val1.getClass().getTypeName())
        {
            case "java.lang.Integer":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        int i1=Integer.parseInt(val1.toString());
                        int i2=Integer.parseInt(val2.toString());
                        int pot_int=(int)Math.pow(i1, i2);
                        return pot_int;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        double pot_double=(double)Math.pow(d1, d2);
                        return pot_double;
                    case "java.lang.Boolean":
                        int b1=Integer.parseInt(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            int pot_bool=(int)Math.pow(b1, 1);
                            return pot_bool;// me quede aqui
                        }else
                        {
                            return b1*0;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna,"Semantico", "No se puede multiplicar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Double":
                switch(val2.getClass().getTypeName())
                {
                    case "java.lang.Integer":
                        double i1=Double.parseDouble(val1.toString());
                        double i2=Double.parseDouble(val2.toString());
                        return i1*i2;
                    case "java.lang.Double":
                        double d1=Double.parseDouble(val1.toString());
                        double d2=Double.parseDouble(val2.toString());
                        return d1*d2;
                    case "java.lang.Boolean":
                        double b1=Double.parseDouble(val1.toString());
                        boolean b2=Boolean.parseBoolean(val2.toString());
                        if(b2==true)
                        {
                            return b1*1;
                        }else
                        {
                            return b1*0;
                        }
                    case "java.lang.String":
                        ErrorT error=new ErrorT(val2.toString(),der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                        LSemanticos.add(error);
                        break;
                }
                break;
                
            case "java.lang.Boolean":
                boolean b1=Boolean.parseBoolean(val1.toString());
                if(b1==true)
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 1*i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 1*d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 1;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                    }
                }else
                {
                    switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 0*i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 0*d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
                }
                
                break;
            case "java.lang.String":
                ErrorT error=new ErrorT(val2.toString(), izq.linea, izq.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                LSemanticos.add(error);
                        
                break;
        }
    return -1;
    }

    private Object evaluar_Igual(Nodo izq, Nodo der) {
        String igual1=(String)evaluar_expresion(izq);
        String igual2=(String)evaluar_expresion(der);

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
        return -1;
    }

    private Object evaluar_Numero(Nodo nod) {
       if(nod.valor.contains("."))
       {
           double num=Double.parseDouble(nod.valor);
           return num;
       }else
       {
           int num=Integer.parseInt(nod.valor);
           return num;
       }
    }

    /*
    private int retornar_tipo(Object val){
        switch(val2.getClass().getTypeName())
                    {
                        case "java.lang.Integer":
                            int i2=Integer.parseInt(val2.toString());
                            return 0*i2;
                        case "java.lang.Double":
                            double d2=Double.parseDouble(val2.toString());
                            return 0*d2;
                        case "java.lang.Boolean":
                            boolean b2=Boolean.parseBoolean(val2.toString());
                            if(b2==true)
                            {
                                return 0;
                            }else
                            {
                                return 0;
                            }
                        case "java.lang.String":
                            ErrorT error=new ErrorT(val2.toString(), der.linea, der.columna, "Semantico", "No se puede multiplicar variables de tipo Texto");
                            LSemanticos.add(error);
                            break;
                            
                    }
    }*/
    
}

