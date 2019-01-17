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
        this.ambitos.addFirst(new HashMap<String,Variable>());
        this.actual=this.ambitos.getFirst();
    }
    
    
      void disminuir_ambito(){
        this.ambitos.removeFirst();
        this.actual=this.ambitos.getFirst();
    }
    
    public  void analizar(Nodo raiz){
        if(raiz==null)
            return;
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
                
            case "MOSTRAR":
                ejecutar_mostrar(raiz.hijos.get(0));
                break;
                
            case "ES_VERDADERO":
                ejecutar_if(raiz);
                break;
            case "MIENTRAS_QUE":
                ejecutar_while(raiz);
                break;
                
        }
    }

    private void ejecutar_declaracion(Nodo raiz) {
        String tipo = raiz.hijos.get(0).valor;
        //Object res = null;//evaluar_expresion(raiz.hijos.get(2));
        Object res=evaluar_expresion(raiz.hijos.get(2));
        
        String tipo_var = retornar_tipo_nombre(res);
        
        if("-3092".equals(tipo_var)||(!"Texto".equals(tipo)&&"Texto".equals(tipo_var))){
            //error
            return;
        }
        
        for(Nodo a:raiz.hijos.get(1).hijos){
            guardar_variable(tipo,a.valor,res);
        }

        }
    
      private void ejecutar_asignacion(Nodo raiz) {
        String id = raiz.hijos.get(0).valor;
       // Object res = null;//evaluar_expresion(raiz.hijos.get(1));
        Object res=evaluar_expresion(raiz.hijos.get(1));
        Variable aux = get_variable(id);
        
        String tipo_var = retornar_tipo_nombre(res);
        if("-3092".equals(tipo_var)||(!"Texto".equals(aux.tipo)&&"Texto".equals(tipo_var))){
            //error
            return;
        }
        
        if(aux!=null){
            
            switch(aux.tipo.toLowerCase()){
                case "texto":
                    aux.valor=String.valueOf(res);
                    break;
                case "entero":
                    //castear a entero
                case "decimal":
                case "booleano":
                    if(tipo_var!="-1"){
                        aux.valor=res;
                    }
            }
            /*if(tipo_var.equals(aux.tipo)){
                aux.valor=res;
            }*/
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
                return "DECIMAL";
            case "java.lang.Double":
                return "DECIMAL";
            case "java.lang.String":
                return "TEXTO";
            case "java.lang.Boolean":
                return "DECIMAL";
            default:
                return "-3092";
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
                    case ">": return evaluar_mayor(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "<": return evaluar_menor(raiz.hijos.get(0), raiz.hijos.get(2));
                    case ">=": return evaluar_mayorIgual(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "<=": return evaluar_menorIgual(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "!=": return evaluar_disinto(raiz.hijos.get(0), raiz.hijos.get(2));
                    case "&&": return evaluar_and(raiz.hijos.get(0), raiz.hijos.get(2));
                    
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
                        return 1;
                    case "TOKFALSO":
                        return 0;  
                    case "ID":
                        return evaluar_id(raiz);
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
        

    private Object evaluar_Por(Nodo izq, Nodo der) {
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
            return Double.parseDouble(val1.toString())* Double.parseDouble(val2.toString());
        }
    } 

    private Object evaluar_Dividir(Nodo izq, Nodo der) {
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
            return Double.parseDouble(val1.toString())/Double.parseDouble(val2.toString());
        }
    }

    private Object evaluar_Modulo(Nodo izq, Nodo der) {
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
            return Double.parseDouble(val1.toString())% Double.parseDouble(val2.toString());
        }
    }

    private Object evaluar_Potencia(Nodo izq, Nodo der) {
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
            return Math.pow(Double.parseDouble(val1.toString()), Double.parseDouble(val2.toString()));
        }
    }

    private Object evaluar_Igual(Nodo izq, Nodo der) {
        String igual1=(String)evaluar_expresion(izq);
        String igual2=(String)evaluar_expresion(der);

        return igual1.equals(igual2);
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

    

    private Object evaluar_and(Nodo izq, Nodo der) {
       Object val1 = evaluar_expresion(izq);
       Object val2 = evaluar_expresion(der);
       
       try{
           return (boolean)val1&&(boolean)val2;
       }catch(Exception e){
           //agregar error
       }
       return -3092;
    }
    
    private Object evaluar_or(Nodo izq, Nodo der) {
       Object val1 = evaluar_expresion(izq);
       Object val2 = evaluar_expresion(der);
       
       try{
           return (boolean)val1||(boolean)val2;
       }catch(Exception e){
           //agregar error
       }
       return -3092;
    }

    private Object evaluar_mayor(Nodo izq, Nodo der) {
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
            if(val1.toString().compareTo(val2.toString())>0){
                return true;
            }else 
                return false;
        }else{
            return Double.parseDouble(val1.toString()) > Double.parseDouble(val2.toString());
        }
    }
    
    private Object evaluar_menor(Nodo izq, Nodo der) {
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
            if(val1.toString().compareTo(val2.toString())>=0){
                return false;
            }else 
                return true;
        }else{
            return Double.parseDouble(val1.toString()) < Double.parseDouble(val2.toString());
        }
        
    }
    
     private Object evaluar_menorIgual(Nodo izq, Nodo der) {
        Object val1=evaluar_expresion(izq);
        Object val2=evaluar_expresion(der);
        
        int tipo1 = retornar_tipo(val1);
        int tipo2 = retornar_tipo(val2);
        
        if(tipo1==-1 || tipo2==-1){
            //error evaluacion dio null
            return -3092;
        }
        if(tipo1==tipo2){
            
        }
        if(tipo1==1|| tipo2==1){
            //agregas a error no se pueden restar stirngs
            if(val1.toString().compareTo(val2.toString())>0){
                return false;
            }else 
                return true;
        }else{
            return Double.parseDouble(val1.toString()) <= Double.parseDouble(val2.toString());
        }
    }
     
     
     private Object evaluar_mayorIgual(Nodo izq, Nodo der) {
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
            if(val1.toString().compareTo(val2.toString())>=0){
                return true;
            }else 
                return false;
        }else{
            return Double.parseDouble(val1.toString()) >= Double.parseDouble(val2.toString());
        }
    }

    private Object evaluar_disinto(Nodo izq, Nodo der) {
        String igual1 = (String) evaluar_expresion(izq);
        String igual2 = (String) evaluar_expresion(der);

        return !igual1.equals(igual2);
    }

    private void ejecutar_mostrar(Nodo raiz) {
        Object res = evaluar_expresion(raiz);
        if(retornar_tipo(res)==-1){
            //error no se peude imprimir
            return;
        }
        
        System.out.println(res.toString());
        
    }

    private void ejecutar_if(Nodo raiz) {
        try{
            if((boolean)evaluar_expresion(raiz.hijos.get(0))){
                aumentar_ambito();
                ejecutar(raiz.hijos.get(1));
                disminuir_ambito();
            }else{
                if(raiz.hijos.size()==3){
                    aumentar_ambito();
                    ejecutar(raiz.hijos.get(2));
                    disminuir_ambito();
                }
            }
        }catch(Exception e){
            //error al evaluar if
        }
    }
    
    private void ejecutar_while(Nodo raiz){
        try{
            while((boolean)evaluar_expresion(raiz.hijos.get(0))){
                aumentar_ambito();
                ejecutar(raiz.hijos.get(1));
                disminuir_ambito();
            }
        }catch(Exception e){
            //error al evaluar if
        }
    }

    private Object evaluar_id(Nodo raiz) {
        String id = raiz.hijos.get(0).valor;
        Variable var = get_variable(id);
        if(var!=null)
            return var.valor;
        else{
            //garegar a error no existe variable
            return -3092;
        }
    }
}

