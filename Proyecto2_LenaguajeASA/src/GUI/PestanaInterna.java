/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import jsyntaxpane.DefaultSyntaxKit;

/**
 *
 * @author johnnybravo
 */
public class PestanaInterna extends javax.swing.JInternalFrame {

    /**
     * Creates new form PestanaInterna
     */
    public PestanaInterna() {
        initComponents();
        iniciarEditor();
    }
    
     private void iniciarEditor()
    {   
        
        DefaultSyntaxKit.initKit();
        this.txtEntrada.setContentType("text/java");  
        txtEntrada.setFont(new Font("Comic Sans", txtEntrada.getFont().getStyle(), 14));
        //txtEntrada.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtEntrada = new javax.swing.JEditorPane();
        lcaret = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(600, 400));

        txtEntrada.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtEntradaCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(txtEntrada);

        lcaret.setText("Position");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lcaret)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lcaret)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtEntradaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtEntradaCaretUpdate
        // TODO add your handling code here:
        try 
        {
            int posicion = txtEntrada.getCaretPosition();
            int fila = (posicion == 0) ? 1 : 0;
            for (int offset = posicion; offset > 0;)
            {
                offset = Utilities.getRowStart(txtEntrada, offset) - 1;
                fila++;
            }
            int offset = Utilities.getRowStart(txtEntrada, posicion);
            int columna = posicion - offset + 1;
            lcaret.setText("Linea: "+fila+"       Columna: "+columna);
        } 
        catch (BadLocationException ex) 
        {Logger.getLogger(PestanaInterna.class.getName()).log(Level.SEVERE, null, ex);}
    }//GEN-LAST:event_txtEntradaCaretUpdate


    /**
     * Metodo para insertar texto dentro del txtEntrada del formulario
     * @param entrada 
     */
    public void setTxtEntrada(String entrada) {
        this.txtEntrada.setText(entrada);
    }

    public String getTxtEntrada() {
        return txtEntrada.getText();
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lcaret;
    private javax.swing.JEditorPane txtEntrada;
    // End of variables declaration//GEN-END:variables
}
