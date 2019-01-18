/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reportes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import Acciones.ErrorT;
import java.util.ArrayList;

/**
 *
 * @author johnnybravo
 */
public class ReporteErrores {
    
    public ArrayList<ErrorT> lista_errores;

    protected String ruta_html,ruta_css;
    public ReporteErrores() 
    {
        
    }    
    
    public void generarReporteErrores()
    {
        try {
            File folder = new File(System.getProperty("user.home") + File.separator + "SalidasDot_Pro2");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            //Rutas para el .html
            ruta_html = System.getProperty("user.home") + File.separator + "SalidasDot_Pro2" + File.separator + "Errores.html";
            ruta_css = System.getProperty("user.home") + File.separator + "SalidasDot_Pro2" + File.separator + "Errores.css";
            
            File file = new File(ruta_html);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter sw = new BufferedWriter(fw);
            sw.write("contenido");
            sw.write("<!DOCTYPE html>");
            sw.write("<html>");
            sw.write("\t<head>");
            sw.write("\t\t<meta charset=\"utf-8\">");
            sw.write("\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"Errores.css\"> ");
            sw.write("\t\t<title>REPORTE DE ERRORES</title>");
            sw.write("\t</head>");
            sw.write("\t<body>");
            sw.write("\t\t<h1>REPORTE DE ERRORES</h1>");
            sw.write("\t\t<div id=\"main-container\">");
            sw.write("\t\t<table class=\"egt\">");
            sw.write("\t\t<thead>");
            sw.write("\t\t\t<tr>");
            sw.write("\t\t\t\t<th>\tTIPO\t</th>");
            sw.write("\t\t\t\t<th>\tLEXEMA\t</th>");
            sw.write("\t\t\t\t<th>\tLINEA\t</th>");
            sw.write("\t\t\t\t<th>\tCOLUMNA\t</th>");
            sw.write("\t\t\t\t<th>\tMENSAJE\t</th>");
            sw.write("\t\t\t</tr>");
            sw.write("\t\t</thead");
            for(ErrorT n: lista_errores)
            {
                sw.write("\t\t\t<tr>");
                sw.write("\t\t\t<tr>");
                sw.write("\t\t\t\t<td>\t"+n.tipo+"\t</td>");
                sw.write("\t\t\t\t<td>\t"+ n.lexema+"\t</td>");
                sw.write("\t\t\t\t<td>\t"+ n.linea+"\t</td>");
                sw.write("\t\t\t\t<td>\t"+ n.columna+"\t</td>");
                sw.write("\t\t\t\t<td>\t"+ n.descripcion+"\t</td>");
                sw.write("\t\t\t</tr>");
            }
            sw.write("\t\t</table");
            sw.write("\t\t</div>");
            sw.write("\t</body>");
            sw.write("</html>");
            sw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //CREANDO ARCHIVO .CSS
        try
        {
            //
            //#5858FA
            File file1 = new File(ruta_css);
            // Si el archivo no existe es creado
            if (!file1.exists()) {
                file1.createNewFile();
            }
            FileWriter fw = new FileWriter(file1);
            BufferedWriter sw = new BufferedWriter(fw);
            sw.write("body{background: #632432; font-family: Arial;}");
            sw.write("#main-container{margin: 150px auto; width: 600px;}");
            sw.write("table{background-color: white; text-align:left; border-collapse: collapse; width: 100%;}");
            sw.write("th,td{padding: 20px;}");
            sw.write("thead{background-color: #246355; border-bottom: solid 5px #0F362D; color: white; }");
            sw.write("tr:nth-child(even){background-color: #ddd;}");
            sw.write("tr:hover td{background-color: #369681; color: white;}");
            sw.write("h1{background-color:#246355; ");
            sw.write("\t color: white;");
            sw.write("\t text-align: center;}");
            sw.close();
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
    }
    
}
