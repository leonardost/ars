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

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.UtilidadesPacotes;
import aprendizadodemaquina.Transformador;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Interface de transformação de pacotes de anotação para formatos compatíveis
 * com os métodos de aprendizado de máquina. Usa a classe Featurizador para
 * transformar os dados.
 */
public class TransformarDados extends javax.swing.JFrame {

    private Main main;
    private List<File> arquivosSelecionados;
    
    public TransformarDados( Main main ) {
        this.main = main;
        initComponents();
        setLocationRelativeTo( null );
        arquivosSelecionados = new ArrayList<File>();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstArquivosEscolhidos = new javax.swing.JList();
        btnSelecionarArquivo = new javax.swing.JButton();
        btnRemoverArquivo = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        btnTransformar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        chkSuperficiais = new javax.swing.JCheckBox();
        chkMorfologicas = new javax.swing.JCheckBox();
        chkSintaticas = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        cmbFormato = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transformar pacotes de anotação");

        jLabel1.setText("Transformar pacotes de anotação para outros formatos");

        jScrollPane1.setViewportView(lstArquivosEscolhidos);

        btnSelecionarArquivo.setText("Adicionar arquivos");
        btnSelecionarArquivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarArquivoActionPerformed(evt);
            }
        });

        btnRemoverArquivo.setText("Remover arquivos");
        btnRemoverArquivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverArquivoActionPerformed(evt);
            }
        });

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnTransformar.setText("Transformar");
        btnTransformar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransformarActionPerformed(evt);
            }
        });

        jLabel2.setText("Features");

        chkSuperficiais.setSelected(true);
        chkSuperficiais.setText("Superficiais");

        chkMorfologicas.setSelected(true);
        chkMorfologicas.setText("Morfológicas");

        chkSintaticas.setSelected(true);
        chkSintaticas.setText("Sintáticas");

        jLabel3.setText("Formato:");

        cmbFormato.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Selecione um formato de conversão", "ARFF (WEKA)", "SVM Light" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnTransformar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbFormato, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSelecionarArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(chkSuperficiais)
                            .addComponent(chkMorfologicas)
                            .addComponent(chkSintaticas))
                        .addContainerGap(30, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSair)
                        .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkSuperficiais)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMorfologicas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSintaticas)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelecionarArquivo)
                    .addComponent(btnRemoverArquivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSair)
                    .addComponent(btnTransformar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelecionarArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivoActionPerformed

        File[] arquivos;
        
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile( new File( main.ultimoDiretorio ) );
        fc.setMultiSelectionEnabled( true );
        fc.setDialogTitle( "Selecione os pacotes de anotação que deseja transformar" );
        int value = fc.showOpenDialog( this );
        
        if ( value == JFileChooser.APPROVE_OPTION )
            arquivos = fc.getSelectedFiles();
        else
            return;
        
        for ( File arquivo : arquivos )
            if ( !arquivosSelecionados.contains( arquivo ) ) 
                arquivosSelecionados.add( arquivo );
        
        main.ultimoDiretorio = arquivos[0].getAbsolutePath();
        
        atualizarListaArquivos();
        
    }//GEN-LAST:event_btnSelecionarArquivoActionPerformed

    private void btnRemoverArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverArquivoActionPerformed
        if ( lstArquivosEscolhidos.isSelectionEmpty() )
            return;
        arquivosSelecionados.remove( (File)lstArquivosEscolhidos.getSelectedValue() );
        atualizarListaArquivos();
    }//GEN-LAST:event_btnRemoverArquivoActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnTransformarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransformarActionPerformed

        if ( arquivosSelecionados.isEmpty() ) {
            JOptionPane.showMessageDialog( null,
                    "Nenhum arquivo está selecionado",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        if ( cmbFormato.getSelectedIndex() <= 0 ) {
            JOptionPane.showMessageDialog( null,
                    "Selecione um formato de conversão",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        // Verifica se ha alguma relaçao semantica nos arquivos selecionados
        List<Sentenca> listaSentencas = UtilidadesPacotes.lerSentencasPacote( arquivosSelecionados );        
        boolean haRelacao = false;
        for ( Sentenca s : listaSentencas ) {
            s.removerTermosNaoUtilizados();
            if ( !s.getRelacoes().isEmpty() ) {
                haRelacao = true;
                break;
            }
        }
        if ( !haRelacao ) {
            JOptionPane.showMessageDialog( null,
                    "os arquivos selecionados não contêm nenhuma relação semântica marcada.\nNenhum arquivo será gerado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        File sugestao = null;
        if ( cmbFormato.getSelectedIndex() == 1 )
            sugestao = new File( "treinamento.arff" );
        else if ( cmbFormato.getSelectedIndex() == 2 )
            sugestao = new File( "treinamento.train" );

        File arquivoSaida = main.mostrarDialogoSalvarArquivo( null, sugestao );
        if ( arquivoSaida == null )
            return;

        main.ultimoDiretorio = arquivoSaida.getParent();
        
        this.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        
        Transformador transformador = new Transformador( arquivosSelecionados );
        
        if ( cmbFormato.getSelectedIndex() == 1 )
            transformador.paraDt( arquivoSaida );
        else if ( cmbFormato.getSelectedIndex() == 2 )
            transformador.paraSvm( arquivoSaida );
        
        this.setCursor( null );

        new Sucesso( this, true, "Arquivos convertidos com sucesso!" ).setVisible( true );
        
    }//GEN-LAST:event_btnTransformarActionPerformed

    void atualizarListaArquivos() {
        DefaultListModel listModel = new DefaultListModel();
        for ( File f : arquivosSelecionados )
            listModel.addElement( f );
        lstArquivosEscolhidos.setModel( listModel );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoverArquivo;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSelecionarArquivo;
    private javax.swing.JButton btnTransformar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkMorfologicas;
    private javax.swing.JCheckBox chkSintaticas;
    private javax.swing.JCheckBox chkSuperficiais;
    private javax.swing.JComboBox cmbFormato;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList lstArquivosEscolhidos;
    // End of variables declaration//GEN-END:variables
        
}
