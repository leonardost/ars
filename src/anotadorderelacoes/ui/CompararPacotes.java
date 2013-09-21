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
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.UtilidadesPacotes;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Janela que realiza a comparação entre as sentenças comuns presentes em dois
 * pacotes distintos.
 * 
 * Termos e relações marcados da mesma forma por ambos os anotadores aparecem
 * em azul claro.
 */
public class CompararPacotes extends javax.swing.JFrame {

    private Main main;
    
    private File arquivoPacoteA = null;
    private File arquivoPacoteB = null;
    private List<Sentenca> listaSentencasA;
    private List<Sentenca> listaSentencasB;
    private List<Termo> listaTermosInterseccao;
    private List<Relacao> listaRelacoesInterseccao;
    
    public CompararPacotes( java.awt.Frame pai ) {
        this.main = (Main) pai;
        initComponents();

        lstSentencasA.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged( ListSelectionEvent e ) {
                if ( lstSentencasA.isSelectionEmpty() )
                    return;
                lstSentencasB.setSelectedIndex( lstSentencasA.getSelectedIndex() );
                atualizarListasTermos();
                atualizarListasRelacoes();
            }
        });

        lstSentencasB.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged( ListSelectionEvent e ) {
                if ( lstSentencasB.isSelectionEmpty() )
                    return;
                lstSentencasA.setSelectedIndex( lstSentencasB.getSelectedIndex() );
                atualizarListasTermos();
                atualizarListasRelacoes();
            }
        });
        
        lstSentencasA.setCellRenderer( new MeuListCellRenderer() );
        lstSentencasB.setCellRenderer( new MeuListCellRenderer() );

        lstTermosA.setCellRenderer( new MeuListCellRenderer2() );
        lstTermosB.setCellRenderer( new MeuListCellRenderer2() );
        lstRelacoesA.setCellRenderer( new MeuListCellRenderer2() );
        lstRelacoesB.setCellRenderer( new MeuListCellRenderer2() );
        
        // Sincroniza as duas barras de rolagem
        scrollSentencasB.getVerticalScrollBar().setModel( scrollSentencasA.getVerticalScrollBar().getModel() );
        
        setLocationRelativeTo( null );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        scrollSentencasB = new javax.swing.JScrollPane();
        lstSentencasB = new javax.swing.JList();
        scrollSentencasA = new javax.swing.JScrollPane();
        lstSentencasA = new javax.swing.JList();
        btnSelecionarPacoteA = new javax.swing.JButton();
        btnSelecionarPacoteB = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstRelacoesA = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstTermosB = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        lstTermosA = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstRelacoesB = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        lblInformações = new javax.swing.JLabel();
        lblArquivoA = new javax.swing.JLabel();
        lblArquivoB = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Comparação de pacotes");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pacote B");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Pacote A");

        scrollSentencasB.setViewportView(lstSentencasB);

        scrollSentencasA.setViewportView(lstSentencasA);

        btnSelecionarPacoteA.setText("Selecionar pacote A");
        btnSelecionarPacoteA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarPacoteAActionPerformed(evt);
            }
        });

        btnSelecionarPacoteB.setText("Selecionar pacote B");
        btnSelecionarPacoteB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarPacoteBActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(lstRelacoesA);

        jScrollPane4.setViewportView(lstTermosB);

        jScrollPane5.setViewportView(lstTermosA);

        jScrollPane6.setViewportView(lstRelacoesB);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformações)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformações)
                .addContainerGap(116, Short.MAX_VALUE))
        );

        lblArquivoA.setText("Arquivo selecionado: nenhum");

        lblArquivoB.setText("Arquivo selecionado: nenhum");

        jLabel2.setText("Sentenças");

        jLabel4.setText("Sentenças");

        jLabel5.setText("Termos");

        jLabel6.setText("Termos");

        jLabel7.setText("Relações");

        jLabel8.setText("Relações");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblArquivoA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scrollSentencasA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                        .addComponent(btnSelecionarPacoteA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scrollSentencasB, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addGap(330, 330, 330))
                                        .addComponent(lblArquivoB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnSelecionarPacoteB, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel8))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSelecionarPacoteB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblArquivoB)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollSentencasB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSelecionarPacoteA)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblArquivoA)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollSentencasA, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jSeparator1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelecionarPacoteAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarPacoteAActionPerformed
        selecionarPacote( "A" );
        if ( arquivoPacoteA != null )
            lblArquivoA.setText( arquivoPacoteA.getName() );
        atualizarListasSentencas();
    }//GEN-LAST:event_btnSelecionarPacoteAActionPerformed

    private void btnSelecionarPacoteBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarPacoteBActionPerformed
        selecionarPacote( "B" );
        if ( arquivoPacoteB != null )
            lblArquivoB.setText( arquivoPacoteB.getName() );
        atualizarListasSentencas();
    }//GEN-LAST:event_btnSelecionarPacoteBActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelecionarPacoteA;
    private javax.swing.JButton btnSelecionarPacoteB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblArquivoA;
    private javax.swing.JLabel lblArquivoB;
    private javax.swing.JLabel lblInformações;
    private javax.swing.JList lstRelacoesA;
    private javax.swing.JList lstRelacoesB;
    private javax.swing.JList lstSentencasA;
    private javax.swing.JList lstSentencasB;
    private javax.swing.JList lstTermosA;
    private javax.swing.JList lstTermosB;
    private javax.swing.JScrollPane scrollSentencasA;
    private javax.swing.JScrollPane scrollSentencasB;
    // End of variables declaration//GEN-END:variables

    /**
     * Mostra o diálogo de seleção de pacote.
     * 
     * @param pacote Qual pacote será selecionado (A ou B)
     */
    public void selecionarPacote( String pacote ) {
        
        // Seleciona arquivo do pacote
        File arquivoPacote;
        List<Sentenca> listaSentencas;
        
        JFileChooser fc = new JFileChooser( main.ultimoDiretorio );
        fc.setDialogTitle( "Selecione um pacote de sentenças de anotação" );
        int value = fc.showOpenDialog( this );
        if ( value == JFileChooser.APPROVE_OPTION )
            arquivoPacote = fc.getSelectedFile();
        else
            return;
        main.ultimoDiretorio = arquivoPacote.getAbsolutePath();
        
        // Inicializa a lista de sentenças
        listaSentencas = UtilidadesPacotes.lerSentencasPacote( arquivoPacote );

        // Ordena as sentenças pelo ID
        Collections.sort( listaSentencas );

        if ( pacote.equals( "A" ) ) {
            arquivoPacoteA = arquivoPacote;
            listaSentencasA = listaSentencas;
        }
        else if ( pacote.equals( "B" ) )  {
            arquivoPacoteB = arquivoPacote;
            listaSentencasB = listaSentencas;
        }

    }

    public void atualizarListasSentencas() {
        
        if ( arquivoPacoteA == null || arquivoPacoteB == null )
            return;

        // Pegamos apenas as sentencas comuns aos dois pacotes
        listaSentencasA.retainAll( listaSentencasB );
        listaSentencasB.retainAll( listaSentencasA );
        
        DefaultListModel listModelA = new DefaultListModel();
        for ( Sentenca s : listaSentencasA )
            listModelA.addElement( s );
        lstSentencasA.setModel( listModelA );
        
        DefaultListModel listModelB = new DefaultListModel();
        for ( Sentenca s : listaSentencasB )
            listModelB.addElement( s );
        lstSentencasB.setModel( listModelB );

        lstSentencasA.setSelectedIndex( 0 );
        
        atualizarInformacoes();
        
    }

    public void atualizarListasTermos() {
        List<Termo> listaTermosA = new ArrayList<Termo>();
        List<Termo> listaTermosB = new ArrayList<Termo>();
        
        DefaultListModel listModelA = new DefaultListModel();
        for ( Termo t : ( (Sentenca)lstSentencasA.getSelectedValue() ).getTermos() ) {
            listModelA.addElement( t );
            listaTermosA.add( t );
        }
        lstTermosA.setModel( listModelA );
        
        DefaultListModel listModelB = new DefaultListModel();
        for ( Termo t : ( (Sentenca)lstSentencasB.getSelectedValue() ).getTermos() ) {
            listModelB.addElement( t );
            listaTermosB.add( t );
        }
        lstTermosB.setModel( listModelB );
        
        listaTermosA.retainAll( listaTermosB );
        listaTermosInterseccao = listaTermosA;
    }
    
    public void atualizarListasRelacoes() {
        List<Relacao> listaRelacoesA = new ArrayList<Relacao>();
        List<Relacao> listaRelacoesB = new ArrayList<Relacao>();
        
        DefaultListModel listModel;
        
        listModel = new DefaultListModel();
        for ( Relacao r : ( (Sentenca)lstSentencasA.getSelectedValue() ).getRelacoes() ) {
            listModel.addElement( r );
            listaRelacoesA.add( r );
        }
        lstRelacoesA.setModel( listModel );
        
        listModel = new DefaultListModel();
        for ( Relacao r : ( (Sentenca)lstSentencasB.getSelectedValue() ).getRelacoes() ) {
            listModel.addElement( r );
            listaRelacoesB.add( r );
        }
        lstRelacoesB.setModel( listModel );

        listaRelacoesA.retainAll( listaRelacoesB );
        listaRelacoesInterseccao = listaRelacoesA;
    }
    
    public class MeuListCellRenderer extends JLabel implements ListCellRenderer {
        public final Color VERDE = new Color( 102, 198, 83 );
        public final Color AZUL = new Color( 57, 105, 138 );
        public final Color CINZA = new Color( 162, 162, 162 );
        public MeuListCellRenderer() {
            setOpaque( true );
        }
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Sentenca s = (Sentenca)value;
            setText( s.toString() );
            setForeground( Color.BLACK );
            if ( isSelected ) {
                setBackground( AZUL );
                setForeground( Color.WHITE );
            }
            else if ( s.isAnotada() )
                setBackground( VERDE );
            else if ( s.isIgnorada() )
                setBackground( CINZA );
            else
                setBackground( Color.WHITE );
            return this;
        }
    }
    
    public class MeuListCellRenderer2 extends JLabel implements ListCellRenderer {
        public final Color AZUL = new Color( 57, 105, 138 );
        public final Color AZUL_CLARO = new Color( 145, 244, 255 );
        public MeuListCellRenderer2() {
            setOpaque( true );
        }
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            setText( value.toString() );
            setForeground( Color.BLACK );
            setBackground( Color.WHITE );
            if ( value instanceof Termo ) {
                if ( listaTermosInterseccao.contains( value ) )
                    setBackground( AZUL_CLARO );
            }
            else if ( value instanceof Relacao ) {
                Relacao r = (Relacao)value;
                if ( listaRelacoesInterseccao.contains( value ) )
                    setBackground( AZUL_CLARO );
            }
            if ( isSelected ) {
                setBackground( AZUL );
                setForeground( Color.WHITE );
            }
            return this;
        }
    }
    
    /**
     * Atualiza a caixa de informações principais
     */
    public void atualizarInformacoes() {
        
        int totalRelacoesDistintasAnotadas = 0;          // número total de relações distintas anotadas
        int totalRelacoesAnotadasIguais = 0;             // número total de relações anotadas iguais pelos dois anotadores

        int relacoesTotaisA = 0;     // numero de relaçoes anotadas pelo anotador A
        int relacoesTotaisB = 0;     // numero de relaçoes anotadas pelo anotador B
        
        for ( int i = 0; i < listaSentencasA.size(); ++i ) {
            
            Sentenca a = listaSentencasA.get( i );
            Sentenca b = listaSentencasB.get( i );

            if ( a.isIgnorada() || b.isIgnorada() )
                continue;

            List<Relacao> relacoesA = a.getRelacoes();
            List<Relacao> relacoesB = b.getRelacoes();
            List<Relacao> interseccao = new ArrayList<Relacao>( relacoesA );
            interseccao.retainAll( relacoesB );

            relacoesTotaisA += relacoesA.size();
            relacoesTotaisB += relacoesB.size();
            
            totalRelacoesDistintasAnotadas += relacoesA.size() + relacoesB.size() - interseccao.size();
            totalRelacoesAnotadasIguais += interseccao.size();
            
        }

        String informacoes = "<html>";
        informacoes += "Número de sentenças em comum: " + listaSentencasA.size() + "<br>";
        informacoes += "Anotador A: " + relacoesTotaisA + " relações anotadas<br>";
        informacoes += "Anotador B: " + relacoesTotaisB + " relações anotadas<br>";
        informacoes += "<br>";
        informacoes += "Total de relações distintas anotadas: " + totalRelacoesDistintasAnotadas + "<br>";
        informacoes += "Total de relaçoes anotadas da mesma forma: " + totalRelacoesAnotadasIguais + "<br>";
        informacoes += "Taxa de concordância: " +
                String.format( "%.2f", (double)totalRelacoesAnotadasIguais / (double)totalRelacoesDistintasAnotadas * 100 ) + "%<br>";
        informacoes += "</html>";
        
        lblInformações.setText( informacoes );
        
    }
    
}
