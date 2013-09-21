//OK!
/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *   Copyright (C) 2012-2013 LaLiC
 */

package anotadorderelacoes.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * Nesta tela o usuário pode acoplar o software SVM Light à ferramenta, bastando
 * colocar o caminho dos programas svm_learn e svm_classify.
 */
public class ConfiguracoesAM extends javax.swing.JFrame {

    private Main main;
    
    public ConfiguracoesAM( Main main ) {
        this.main = main;
        initComponents();
        setLocationRelativeTo( null );
        txtCaminhoLearn.setText( main.caminhoSvmLightLearn );
        txtCaminhoClassify.setText( main.caminhoSvmLightClassify );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        txtCaminhoLearn = new javax.swing.JTextField();
        btnProcurar1 = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        btnProcurar2 = new javax.swing.JButton();
        txtCaminhoClassify = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configurações");

        jLabel1.setText("Caminho do SVM Light (svm_learn)");

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnProcurar1.setText("Procurar");
        btnProcurar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcurar1ActionPerformed(evt);
            }
        });

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnProcurar2.setText("Procurar");
        btnProcurar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcurar2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Caminho do SVM Light (svm_classify)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCaminhoLearn, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnProcurar1))
                            .addComponent(jLabel1))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSalvar)
                                .addGap(269, 269, 269)
                                .addComponent(btnSair))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtCaminhoClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnProcurar2))
                                .addComponent(jLabel2)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCaminhoLearn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProcurar1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCaminhoClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProcurar2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnSair))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnProcurar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcurar1ActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle( "Selecione o programa svm_learn" );
        File svmlight;
        if ( ! txtCaminhoLearn.getText().equals( "" ) )
            fc.setSelectedFile( new File( txtCaminhoLearn.getText() ) );
        else
            fc.setSelectedFile( new File( main.ultimoDiretorio ) );
        if ( fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            svmlight = fc.getSelectedFile();
            txtCaminhoLearn.setText( svmlight.getAbsolutePath() );
        }
        else
            return;
        main.ultimoDiretorio = txtCaminhoLearn.getText();
    }//GEN-LAST:event_btnProcurar1ActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        main.caminhoSvmLightLearn = txtCaminhoLearn.getText();
        main.caminhoSvmLightClassify = txtCaminhoClassify.getText();
        try {
            main.properties.setProperty( "caminho.svmlight.learn", txtCaminhoLearn.getText() );
            main.properties.setProperty( "caminho.svmlight.classify", txtCaminhoClassify.getText() );
            main.properties.store( new FileOutputStream( "ars.properties" ), null );
        }
        catch ( IOException ex ) {
            ex.printStackTrace();
        }
        main.atualizarClassificador();
        setVisible( false );
        dispose();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnProcurar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcurar2ActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle( "Selecione o programa svm_classify" );
        File svmlight;
        if ( ! txtCaminhoClassify.getText().equals( "" ) )
            fc.setSelectedFile( new File( txtCaminhoClassify.getText() ) );
        else
            fc.setSelectedFile( new File( main.ultimoDiretorio ) );
        if ( fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
            svmlight = fc.getSelectedFile();
            txtCaminhoClassify.setText( svmlight.getAbsolutePath() );
        }
        else
            return;
        main.ultimoDiretorio = txtCaminhoClassify.getText();
    }//GEN-LAST:event_btnProcurar2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProcurar1;
    private javax.swing.JButton btnProcurar2;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtCaminhoClassify;
    private javax.swing.JTextField txtCaminhoLearn;
    // End of variables declaration//GEN-END:variables
    
}
