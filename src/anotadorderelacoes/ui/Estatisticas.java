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

import anotadorderelacoes.model.Relacao;
import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.UtilidadesPacotes;
import java.io.File;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Janela que pérmite visualizar o número de relações semânticas existentes em
 * um grupo de pacotes de sentenças
 */
public class Estatisticas extends javax.swing.JFrame {

    private Main main;
    private List<File> arquivosSelecionadosA;
    private List<File> arquivosSelecionadosB;
    
    public Estatisticas( Main main ) {
        initComponents();
        setLocationRelativeTo( null );
        arquivosSelecionadosA = new ArrayList<File>();
        arquivosSelecionadosB = new ArrayList<File>();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstArquivosEscolhidosA = new javax.swing.JList();
        btnSelecionarArquivoA = new javax.swing.JButton();
        btnRemoverArquivoA = new javax.swing.JButton();
        btnTransformar = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSaida = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstArquivosEscolhidosB = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnSelecionarArquivoB = new javax.swing.JButton();
        btnRemoverArquivoB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Número de relações em pacotes");

        jLabel1.setText("Verificar número de relações nos seguintes pacotes");

        jScrollPane1.setViewportView(lstArquivosEscolhidosA);

        btnSelecionarArquivoA.setText("Adicionar arquivos");
        btnSelecionarArquivoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarArquivoAActionPerformed(evt);
            }
        });

        btnRemoverArquivoA.setText("Remover arquivos");
        btnRemoverArquivoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverArquivoAActionPerformed(evt);
            }
        });

        btnTransformar.setText("Verificar");
        btnTransformar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransformarActionPerformed(evt);
            }
        });

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        txtSaida.setColumns(20);
        txtSaida.setEditable(false);
        txtSaida.setRows(5);
        jScrollPane2.setViewportView(txtSaida);

        jScrollPane3.setViewportView(lstArquivosEscolhidosB);

        jLabel2.setText("Pacotes A");

        jLabel3.setText("Pacotes B");

        btnSelecionarArquivoB.setText("Adicionar arquivos");
        btnSelecionarArquivoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarArquivoBActionPerformed(evt);
            }
        });

        btnRemoverArquivoB.setText("Remover arquivos");
        btnRemoverArquivoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverArquivoBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSelecionarArquivoA, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSelecionarArquivoB, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnTransformar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSair))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnRemoverArquivoA, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnRemoverArquivoB, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelecionarArquivoB)
                    .addComponent(btnSelecionarArquivoA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoverArquivoA)
                    .addComponent(btnRemoverArquivoB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSair)
                    .addComponent(btnTransformar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnSelecionarArquivoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivoAActionPerformed
        selecionarArquivo( arquivosSelecionadosA );
    }//GEN-LAST:event_btnSelecionarArquivoAActionPerformed
    
    private void btnRemoverArquivoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverArquivoAActionPerformed
        removerArquivo( arquivosSelecionadosA, lstArquivosEscolhidosA );
    }//GEN-LAST:event_btnRemoverArquivoAActionPerformed

    private void btnTransformarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransformarActionPerformed
        verificar();
    }//GEN-LAST:event_btnTransformarActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnSelecionarArquivoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarArquivoBActionPerformed
        selecionarArquivo( arquivosSelecionadosB );
    }//GEN-LAST:event_btnSelecionarArquivoBActionPerformed

    private void btnRemoverArquivoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverArquivoBActionPerformed
        removerArquivo( arquivosSelecionadosB, lstArquivosEscolhidosB );
    }//GEN-LAST:event_btnRemoverArquivoBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoverArquivoA;
    private javax.swing.JButton btnRemoverArquivoB;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSelecionarArquivoA;
    private javax.swing.JButton btnSelecionarArquivoB;
    private javax.swing.JButton btnTransformar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstArquivosEscolhidosA;
    private javax.swing.JList lstArquivosEscolhidosB;
    private javax.swing.JTextArea txtSaida;
    // End of variables declaration//GEN-END:variables

    private void selecionarArquivo( List<File> arquivosSelecionados ) {
        
        File[] arquivos;
        JFileChooser fc = new JFileChooser( main.ultimoDiretorio );
        fc.setMultiSelectionEnabled( true );
        fc.setDialogTitle( "Selecione os pacotes de sentenças que deseja analisar" );
        
        int value = fc.showOpenDialog( this );
        if ( value == JFileChooser.APPROVE_OPTION )
            arquivos = fc.getSelectedFiles();
        else
            return;
        
        main.ultimoDiretorio = arquivos[0].getParent();
        
        for ( File arquivo : arquivos )
            if ( !arquivosSelecionados.contains( arquivo ) )
                arquivosSelecionados.add( arquivo );
        
        atualizarListaArquivos();
        
    }

    /**
     * Remove os arquivos atualmente selecionados da lista de arquivos
     * 
     * @param arquivosSelecionados
     * @param lstArquivosEscolhidos 
     */
    private void removerArquivo( List<File> arquivosSelecionados, javax.swing.JList lstArquivosEscolhidos ) {
        if ( lstArquivosEscolhidos.isSelectionEmpty() ) {
            return;
        }
        arquivosSelecionados.remove( (File)lstArquivosEscolhidos.getSelectedValue() );
        atualizarListaArquivos();
    }
    
    void atualizarListaArquivos() {
        DefaultListModel listModel = new DefaultListModel();
        for ( File f : arquivosSelecionadosA )
            listModel.addElement( f );
        lstArquivosEscolhidosA.setModel( listModel );
        listModel = new DefaultListModel();
        for ( File f : arquivosSelecionadosB )
            listModel.addElement( f );
        lstArquivosEscolhidosB.setModel( listModel );
    }

    /**
     * Verifica o número de relações semânticas existentes nos arquivos
     * selecionados. Sentenças repetidas são contadas apenas uma vez.
     */
    private void verificar() {

        if ( arquivosSelecionadosA.isEmpty() && arquivosSelecionadosB.isEmpty() ) {
            JOptionPane.showMessageDialog( null,
                    "Nenhum arquivo está selecionado",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        String[] relacoes = { "is-a", "part-of", "property-of", "location-of",
                              "made-of", "used-for", "effect-of" };
        Map<String, Integer> relacoesParaInteiros = new HashMap<String, Integer>();
        relacoesParaInteiros.put( "is-a", 0 );
        relacoesParaInteiros.put( "part-of", 1 );
        relacoesParaInteiros.put( "property-of", 2 );
        relacoesParaInteiros.put( "location-of", 3 );
        relacoesParaInteiros.put( "made-of", 4 );
        relacoesParaInteiros.put( "used-for", 5 );
        relacoesParaInteiros.put( "effect-of", 6 );
        
        /**
         * Indice 0 = Total de relacoes anotadas pelo anotador A
         *        1 = Total de relacoes anotadas pelo anotador B
         *        2 = Intersecçao das relacoes anotadas pelos anotadores (A interseccao B)
         *        3 = Total de relacoes distintas anotadas pelos anotadores (A uniao B - A interseçao B)
         */ 
        int[][] quantidade = new int[4][ relacoes.length ];
        
        List<Sentenca> listaSentencasA = UtilidadesPacotes.lerSentencasPacote( arquivosSelecionadosA );
        List<Sentenca> listaSentencasB = UtilidadesPacotes.lerSentencasPacote( arquivosSelecionadosB );

        // Sentenças marcadas pelo anotador A
        for ( Sentenca sentenca : listaSentencasA )
            for ( Relacao relacao : sentenca.getRelacoes() )
                quantidade[0][ relacoesParaInteiros.get( relacao.getRelacao() ) ]++;

        // Sentenças marcadas pelo anotador B
        for ( Sentenca sentenca : listaSentencasB )
            for ( Relacao relacao : sentenca.getRelacoes() )
                quantidade[1][ relacoesParaInteiros.get( relacao.getRelacao() ) ]++;

        // Intersecçao das sentenças marcadas por A e B
        listaSentencasA.retainAll( listaSentencasB );
        listaSentencasB.retainAll( listaSentencasA );
        
        Collections.sort( listaSentencasA );
        Collections.sort( listaSentencasB );
        
        // Verifica relaçoes que ambos os anotadores marcaram
        for ( int i = 0; i < listaSentencasA.size(); ++i ) {
            Sentenca sentencaA = listaSentencasA.get( i );
            Sentenca sentencaB = listaSentencasB.get( i );
            List<Relacao> relacoesA = sentencaA.getRelacoes();
            List<Relacao> relacoesB = sentencaB.getRelacoes();
            relacoesA.retainAll( relacoesB );
            for ( Relacao r : relacoesA )
                quantidade[2][ relacoesParaInteiros.get( r.getRelacao() ) ]++;
        }
        
        // (A uniao B) - (A intersecçao B)
        for ( int i = 0; i < relacoes.length; ++i )
            quantidade[3][i] = quantidade[0][i] + quantidade[1][i] - quantidade[2][i];

        // Output
        String saida = "";
        int total[] = new int[4];
        for ( int i = 0; i < 4; ++i ) {
            switch (i) {
                case 0:
                    saida += "Relaçoes anotadas pelo anotador A\n";
                    break;
                case 1:
                    saida += "Relaçoes anotadas pelo anotador B\n";
                    break;
                case 2:
                    saida += "Intersecçao das relaçoes anotadas pelos dois anotadores\n";
                    break;
                case 3:
                    saida += "(A uniao B) - (A intersecçao B)\n";
                    break;
            }
            for ( int j = 0; j < relacoes.length; ++j ) {
                total[i] += quantidade[i][j];
                saida += "Relação " + relacoes[j] + ": " + quantidade[i][j] + "\n";
            }
            saida += "Total: " + total[i] + "\n\n";
        }
        
        txtSaida.setText( saida );

    }
    
}
