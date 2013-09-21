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

import aprendizadodemaquina.Classificador;
import aprendizadodemaquina.classificadores.ClassificadorDT;
import aprendizadodemaquina.classificadores.ClassificadorSVM;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class TreinarClassificador extends javax.swing.JFrame {

    private Main main;
    private File arquivoTreinamento;

    private static String[] parametros;
    static {
        parametros = new String[2];
        parametros[0] = "-C 0.25 -M 2";        
        parametros[1] = "-t 1 -d 3 -c 0.001 -m 1024";
    }
    
    public TreinarClassificador() {
        initComponents();
        setLocationRelativeTo( null );
        lblArquivoTreinamento.setText( null );
        
        cmbTipoClassificador.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                txtParametros.setText( parametros[cmbTipoClassificador.getSelectedIndex()] );
            }
        });

        txtParametros.setText( parametros[0] );
    }

    public TreinarClassificador( Main main ) {
        this.main = main;
        initComponents();
        setLocationRelativeTo( null );
        lblArquivoTreinamento.setText( null );
        
        cmbTipoClassificador.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                txtParametros.setText( parametros[cmbTipoClassificador.getSelectedIndex()] );
            }
        });

        txtParametros.setText( parametros[0] );
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        btnSelecionarArquivoTreinamento = new javax.swing.JButton();
        lblArquivoTreinamento = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cmbTipoClassificador = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtParametros = new javax.swing.JTextField();
        btnTreinarModelo = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Treinar classificadores");

        jLabel2.setText("Arquivo de treinamento:");

        btnSelecionarArquivoTreinamento.setText("Selecionar arquivo de treinamento");
        btnSelecionarArquivoTreinamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarArquivoTreinamentoActionPerformed(evt);
            }
        });

        lblArquivoTreinamento.setBackground(new java.awt.Color(255, 255, 255));
        lblArquivoTreinamento.setText("lblArquivoTreinamento");

        cmbTipoClassificador.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Árvore J48", "Support Vector Machine (SVM Light)" }));

        jLabel3.setText("Parâmetros");

        jLabel4.setText("Tipo do classificador");

        btnTreinarModelo.setText("Treinar modelo e salvar");
        btnTreinarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTreinarModeloActionPerformed(evt);
            }
        });

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblArquivoTreinamento, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                    .addComponent(cmbTipoClassificador, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtParametros)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSelecionarArquivoTreinamento)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTreinarModelo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblArquivoTreinamento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelecionarArquivoTreinamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipoClassificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtParametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTreinarModelo)
                    .addComponent(btnSair))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelecionarArquivoTreinamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivoTreinamentoActionPerformed
        JFileChooser fc = new JFileChooser( main.ultimoDiretorio );
        fc.setDialogTitle( "Selecione o arquivo de treinamento" );
        int value = fc.showOpenDialog( this );
        if ( value == JFileChooser.APPROVE_OPTION )
            arquivoTreinamento = fc.getSelectedFile();
        else
            return;
        main.ultimoDiretorio = arquivoTreinamento.getParent();
        lblArquivoTreinamento.setText( arquivoTreinamento.getAbsolutePath() );
    }//GEN-LAST:event_btnSelecionarArquivoTreinamentoActionPerformed

    private void btnTreinarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTreinarModeloActionPerformed

        if ( arquivoTreinamento == null ) {
            JOptionPane.showMessageDialog( null,
                    "Selecione um arquivo de treinamento",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        if ( cmbTipoClassificador.getSelectedIndex() == 1 &&
                ( main.caminhoSvmLightClassify.equals( "" ) || main.caminhoSvmLightLearn.equals( "" ) ) ) {
            JOptionPane.showMessageDialog( null,
                    "Antes de treinar um modelo SVM é necessário finalizar as configurações\n"
                    + "no menu \"Aprendizado de Máquina\" -> \"Configurações\"",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        if ( cmbTipoClassificador.getSelectedIndex() == 1 &&
                !new File( main.caminhoSvmLightLearn ).exists() ) {
            JOptionPane.showMessageDialog( null,
                    "O caminho do programa svm_learn não está coreto!\n"
                    + "Verifique as configurações no menu \"Aprendizado de máquina\" -> \"Configurações\".",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        File sugestao = null;
        if ( cmbTipoClassificador.getSelectedIndex() == 0 )
            sugestao = new File( "classificador.j48" );
        else if ( cmbTipoClassificador.getSelectedIndex() == 1 )
            sugestao = new File( "classificador.svm" );
        
        File arquivoSaida = main.mostrarDialogoSalvarArquivo( null, sugestao );
        if ( arquivoSaida == null )
            return;
        
        main.ultimoDiretorio = arquivoSaida.getParent();

        Logger.getLogger( "ARS logger" ).log( Level.FINE, "Treinando classificador {0}", cmbTipoClassificador.getSelectedItem() );
        
        Classificador classificador = null;
        if ( cmbTipoClassificador.getSelectedIndex() == 0 )
            classificador = new ClassificadorDT( "Árvore de decisão J48" );
        else if ( cmbTipoClassificador.getSelectedIndex() == 1 )
            classificador = new ClassificadorSVM( "SVM (SVM Light)" );

        final Classificador classificador2 = classificador;
        
        classificador2.adicionarParametro( "caminho.svmlight.learn", main.caminhoSvmLightLearn );
        classificador2.adicionarParametro( "caminho.svmlight.classify", main.caminhoSvmLightClassify );
        
        final Processando processando = new Processando( this, true );
        SwingWorker sw = new SwingWorker<Integer, Integer>() {
            @Override
            public Integer doInBackground() {
                setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                int resultado;
                resultado = classificador2.treinar( arquivoTreinamento, txtParametros.getText() );
                return resultado;
            }
            @Override
            public void done() {
                processando.setVisible( false );
                processando.dispose();
                setCursor( null );
            }
        };
        sw.execute();
        processando.setVisible( true );
        
        try {
            if ( (Integer)sw.get() != 0 )
                return;
        }
        catch ( InterruptedException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( ExecutionException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
        Logger.getLogger( "ARS logger" ).log( Level.FINE, "Classificador treinado" );
        
        classificador2.gravar( arquivoSaida );
        
        new Sucesso( this, true, "Classificador treinado com sucesso" ).setVisible( true );
        setVisible( false );
        dispose();

    }//GEN-LAST:event_btnTreinarModeloActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSelecionarArquivoTreinamento;
    private javax.swing.JButton btnTreinarModelo;
    private javax.swing.JComboBox cmbTipoClassificador;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblArquivoTreinamento;
    private javax.swing.JTextField txtParametros;
    // End of variables declaration//GEN-END:variables

}
