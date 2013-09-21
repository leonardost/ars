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
 * Anotador de Relaçoes Semanticas v3.2
 * 
 * Copyright (C) 2012-2013 LaLiC
 * 
 */

package anotadorderelacoes.ui;

import anotadorderelacoes.model.*;
import aprendizadodemaquina.Classificador;
import aprendizadodemaquina.Transformador;
import aprendizadodemaquina.classificadores.ClassificadorDT;
import aprendizadodemaquina.classificadores.ClassificadorSVM;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main extends javax.swing.JFrame {

    public Properties properties;
    public String ultimoDiretorio;
    public String caminhoSvmLightLearn;
    public String caminhoSvmLightClassify;
    
    private File arquivoAtual;                    // arquivo de pacote aberto atualmente
    private Sentenca sentencaAtual;               // sentença selecionada atualmente
    private List<Sentenca> listaSentencas;        // lista que guarda todas as sentenças do pacote atual
    private String anotadorAtual;                 // anotador que esta trabalhando no momento
    private String cabecalho;                     // cabeçalho do pacote aberto atualmente
    private Localizar instanciaLocalizar;
    private boolean modificado;                   // se alguma modificaçao foi feita no arquivo atual
    private List<File> arquivosRecentes;          // lista de arquivos recentemente modificados
    
    private Timer timer;                          // usado na verificaçao de clique duplo
    private boolean cliqueDuplo;                  // checa se houve um clique duplo em algum componente
    private List<Integer> termosSelecionados;     // guarda os índices da lista de termos que estão selecionados
    private int primeiroTokenSelecionado;
    private Termo primeiroTermoSelecionado;
    private int ultimoTermoSelecionado;
    private Classificador classificador;
    
    public Main() {

        initComponents();

        properties = new Properties();
        
        ultimoDiretorio = "";
        caminhoSvmLightLearn = "";
        caminhoSvmLightClassify = "";
        arquivosRecentes = new ArrayList<File>();
        
        File f = new File( "ars.properties" );
        if ( !f.exists() )
            salvarPropriedades();
        else {
            try {
                properties.load( new FileInputStream( "ars.properties" ) );
                ultimoDiretorio = properties.getProperty( "ultimo.diretorio" );
                caminhoSvmLightLearn = properties.getProperty( "caminho.svmlight.learn" );
                caminhoSvmLightClassify = properties.getProperty( "caminho.svmlight.classify" );
                for ( int i = 0; i < 5; ++i ) {
                    String arquivo = properties.getProperty( "arquivo.recente." + (i + 1) );
                    if ( !arquivo.equals( "" ) && new File( arquivo ).exists() )
                        arquivosRecentes.add( new File( arquivo ) );
                }
            }
            catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
        
        atualizarMenuArquivosRecentes();
        
        // Adicionando listeners para o Document do JTextField comentário,
        // para possibilitar a detecção de mudanças nos textos
        txtComentarios.getDocument().addDocumentListener( new DocumentListener() {
            @Override
            public void insertUpdate( DocumentEvent e ) {
                if ( sentencaAtual != null ) {
                    sentencaAtual.setComentarios( txtComentarios.getText() );
                    modificado = true;
                }
            }
            @Override
            public void removeUpdate( DocumentEvent e ) {
                if ( sentencaAtual != null ) {
                    sentencaAtual.setComentarios( txtComentarios.getText() );
                    modificado = true;
                }
            }
            @Override
            public void changedUpdate( DocumentEvent e ) {
                if ( sentencaAtual != null ) {
                    sentencaAtual.setComentarios( txtComentarios.getText() );
                    modificado = true;
                }
            }
        });

        // Listener da lista de sentenças para atualizar a sentença que esta em ediçao no momento
        lstListaSentencas.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged( ListSelectionEvent e ) {
                if ( lstListaSentencas.isSelectionEmpty() )
                    return;
                sentencaAtual = (Sentenca)lstListaSentencas.getSelectedValue();
                txtSentenca.setText( sentencaAtual.getTexto() );
                atualizarTexto();
                atualizarComentarios();
                atualizarListaTermos();
                atualizarListaRelacoes();
                atualizarAnotada();
                atualizarIgnorada();
            }
        });
        
        // Listener da lista de relações
        lstRelacoes.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged( ListSelectionEvent e ) {
                if ( lstRelacoes.isSelectionEmpty() )
                    return;
                Relacao r = (Relacao)lstRelacoes.getSelectedValue();
                int []selecoes = new int[2];
                selecoes[0] = sentencaAtual.getTermos().indexOf( r.getTermo1() );
                selecoes[1] = sentencaAtual.getTermos().indexOf( r.getTermo2() );
                lstTermos.setSelectedIndices( selecoes );
            }
        }); 

        lstListaSentencas.setCellRenderer( new MeuListCellRenderer() );
        
        acionarElementos( false );
        
        // TODO: refatorar, gambiarra
        termosSelecionados = new ArrayList<Integer>();  
        classificador = null;
        
        // Centraliza o formulário
        setLocationRelativeTo( null ); 

        // Pega o nome do anotador atual
        new Anotador( this, true ).setVisible( true );

        // Logging
        Handler fh = null;
        try {
            fh = new FileHandler( "ars.log", false );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( SecurityException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
        fh.setFormatter( new SimpleFormatter() );
        fh.setLevel( Level.ALL );
        Handler ch = new ConsoleHandler();
        ch.setLevel( Level.ALL );
        Logger.getLogger( "ARS logger" ).addHandler( fh );
//        Logger.getLogger( "ARS logger" ).addHandler( ch );
        Logger.getLogger( "ARS logger" ).setLevel( Level.FINE );
        
        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Iniciando ARS com o anotador " + anotadorAtual );
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupTxtSentenca = new javax.swing.JPopupMenu();
        mnuMarcarTermo = new javax.swing.JMenuItem();
        mnuDesmarcarTermo = new javax.swing.JMenuItem();
        popupLstRelacoes = new javax.swing.JPopupMenu();
        mnuInverterRelacao = new javax.swing.JMenuItem();
        mnuDesmarcarRelacao = new javax.swing.JMenuItem();
        popupLstTermos = new javax.swing.JPopupMenu();
        mnuDesmarcarTermo2 = new javax.swing.JMenuItem();
        panTopo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtComentarios = new javax.swing.JTextField();
        panEsquerda = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstListaSentencas = new javax.swing.JList();
        btnSentencaAnterior = new javax.swing.JButton();
        btnSentencaProxima = new javax.swing.JButton();
        panSentenca = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSentenca = new javax.swing.JTextPane();
        btnMarcarSelecaoComoTermo = new javax.swing.JButton();
        btnIgnorarSentenca = new javax.swing.JButton();
        chkAnotada = new javax.swing.JCheckBox();
        chkIgnorada = new javax.swing.JCheckBox();
        btnMarcarSentencaAnotada = new javax.swing.JButton();
        panTermosRelacoes = new javax.swing.JPanel();
        panTermos = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstTermos = new javax.swing.JList();
        btnDesmarcarTermos = new javax.swing.JButton();
        btnDetectarTermos = new javax.swing.JButton();
        panRelacao = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cmbRelacoes = new javax.swing.JComboBox();
        btnMarcarRelacao = new javax.swing.JButton();
        btnMarcarRelacaoInvertida = new javax.swing.JButton();
        btnDetectarRelacoes = new javax.swing.JButton();
        panRelacoes = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstRelacoes = new javax.swing.JList();
        btnDesmarcarRelacoes = new javax.swing.JButton();
        btnInverterRelacoes = new javax.swing.JButton();
        panBarraStatus = new javax.swing.JPanel();
        lblBarraStatus = new javax.swing.JLabel();
        panBarraInformacoes = new javax.swing.JPanel();
        lblInformacoes = new javax.swing.JLabel();
        panBarraInformacoesClassificador = new javax.swing.JPanel();
        lblClassificadorSelecionado = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        mnuArquivo = new javax.swing.JMenu();
        mnuAbrir = new javax.swing.JMenuItem();
        mnuAbrirArquivoRecente = new javax.swing.JMenu();
        mnuSalvar = new javax.swing.JMenuItem();
        mnuSalvarComo = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuImportar = new javax.swing.JMenu();
        mnuConverterPalavras = new javax.swing.JMenuItem();
        mnuConverterTexto = new javax.swing.JMenuItem();
        mnuExportar = new javax.swing.JMenu();
        mnuExportarRelacoes = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnuSair = new javax.swing.JMenuItem();
        mnuEditar = new javax.swing.JMenu();
        mnuAlterarAnotador = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuMarcarSentencaAnotada = new javax.swing.JMenuItem();
        mnuMarcarSentencaIgnorada = new javax.swing.JMenuItem();
        mnuSentencaAnterior = new javax.swing.JMenuItem();
        mnuSentencaProxima = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuLocalizar = new javax.swing.JMenuItem();
        mnuComparar = new javax.swing.JMenu();
        mnuCompararPacotes = new javax.swing.JMenuItem();
        mnuAprendizadoDeMaquina = new javax.swing.JMenu();
        mnuConfiguracoes = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuTransformar = new javax.swing.JMenuItem();
        mnuTreinarClassificador = new javax.swing.JMenuItem();
        mnuSelecionarClassificador = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuDetectarTermosPacote = new javax.swing.JMenuItem();
        mnuDetectarRelacoesPacote = new javax.swing.JMenuItem();
        mnuDetectarTermosRelacoesPacote = new javax.swing.JMenuItem();
        mnuRemoverTermosNaoUtilizados = new javax.swing.JMenuItem();
        mnuEstatisticas = new javax.swing.JMenu();
        mnuQuantidadeRelacoesNestePacote = new javax.swing.JMenuItem();
        mnuQuantidadeRelacoesConjuntoPacotes = new javax.swing.JMenuItem();
        mnuAjuda = new javax.swing.JMenu();
        mnuSobre = new javax.swing.JMenuItem();

        mnuMarcarTermo.setText("Marcar seleção como termo");
        mnuMarcarTermo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMarcarTermoActionPerformed(evt);
            }
        });
        popupTxtSentenca.add(mnuMarcarTermo);

        mnuDesmarcarTermo.setText("Desmarcar termo");
        mnuDesmarcarTermo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesmarcarTermoActionPerformed(evt);
            }
        });
        popupTxtSentenca.add(mnuDesmarcarTermo);

        mnuInverterRelacao.setText("Inverter relação");
        mnuInverterRelacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInverterRelacaoActionPerformed(evt);
            }
        });
        popupLstRelacoes.add(mnuInverterRelacao);

        mnuDesmarcarRelacao.setText("Desmarcar relação");
        mnuDesmarcarRelacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesmarcarRelacaoActionPerformed(evt);
            }
        });
        popupLstRelacoes.add(mnuDesmarcarRelacao);

        mnuDesmarcarTermo2.setText("Desmarcar termo");
        mnuDesmarcarTermo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesmarcarTermo2ActionPerformed(evt);
            }
        });
        popupLstTermos.add(mnuDesmarcarTermo2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ARS - Anotador de Relações Semânticas");

        jLabel2.setText("Comentários");

        javax.swing.GroupLayout panTopoLayout = new javax.swing.GroupLayout(panTopo);
        panTopo.setLayout(panTopoLayout);
        panTopoLayout.setHorizontalGroup(
            panTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTopoLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtComentarios, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE))
        );
        panTopoLayout.setVerticalGroup(
            panTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTopoLayout.createSequentialGroup()
                .addGroup(panTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtComentarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Lista de sentenças");

        lstListaSentencas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lstListaSentencas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(lstListaSentencas);

        btnSentencaAnterior.setText("< Anterior");
        btnSentencaAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSentencaAnteriorActionPerformed(evt);
            }
        });

        btnSentencaProxima.setText("Próxima >");
        btnSentencaProxima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSentencaProximaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panEsquerdaLayout = new javax.swing.GroupLayout(panEsquerda);
        panEsquerda.setLayout(panEsquerdaLayout);
        panEsquerdaLayout.setHorizontalGroup(
            panEsquerdaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panEsquerdaLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(112, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
            .addGroup(panEsquerdaLayout.createSequentialGroup()
                .addComponent(btnSentencaAnterior)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(btnSentencaProxima))
        );
        panEsquerdaLayout.setVerticalGroup(
            panEsquerdaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panEsquerdaLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panEsquerdaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSentencaAnterior)
                    .addComponent(btnSentencaProxima)))
        );

        jLabel4.setText("Sentença selecionada");

        txtSentenca.setEditable(false);
        txtSentenca.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        txtSentenca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtSentencaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtSentencaMouseReleased(evt);
            }
        });
        txtSentenca.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                txtSentencaMouseDragged(evt);
            }
        });
        txtSentenca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSentencaKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txtSentenca);

        btnMarcarSelecaoComoTermo.setText("Marcar seleção como termo");
        btnMarcarSelecaoComoTermo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarSelecaoComoTermoActionPerformed(evt);
            }
        });

        btnIgnorarSentenca.setText("Ignorar sentença");
        btnIgnorarSentenca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIgnorarSentencaActionPerformed(evt);
            }
        });

        chkAnotada.setText("Já foi anotada?");
        chkAnotada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAnotadaActionPerformed(evt);
            }
        });

        chkIgnorada.setText("Ignorada?");
        chkIgnorada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIgnoradaActionPerformed(evt);
            }
        });

        btnMarcarSentencaAnotada.setText("Marcar sentença como anotada e ir para próxima sentença");
        btnMarcarSentencaAnotada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarSentencaAnotadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panSentencaLayout = new javax.swing.GroupLayout(panSentenca);
        panSentenca.setLayout(panSentencaLayout);
        panSentencaLayout.setHorizontalGroup(
            panSentencaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSentencaLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(chkAnotada)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkIgnorada)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panSentencaLayout.createSequentialGroup()
                .addComponent(btnMarcarSentencaAnotada)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIgnorarSentenca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(btnMarcarSelecaoComoTermo))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
        );
        panSentencaLayout.setVerticalGroup(
            panSentencaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSentencaLayout.createSequentialGroup()
                .addGroup(panSentencaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(chkAnotada)
                    .addComponent(chkIgnorada))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panSentencaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMarcarSelecaoComoTermo)
                    .addComponent(btnIgnorarSentenca)
                    .addComponent(btnMarcarSentencaAnotada)))
        );

        jLabel5.setText("Lista de termos");

        lstTermos.setToolTipText("Selecione mais de um termo usando a tecla ctrl");
        lstTermos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lstTermosMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstTermosMouseReleased(evt);
            }
        });
        lstTermos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstTermosKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(lstTermos);

        btnDesmarcarTermos.setText("Desmarcar termos");
        btnDesmarcarTermos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesmarcarTermosActionPerformed(evt);
            }
        });

        btnDetectarTermos.setText("Detectar termos");
        btnDetectarTermos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetectarTermosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panTermosLayout = new javax.swing.GroupLayout(panTermos);
        panTermos.setLayout(panTermosLayout);
        panTermosLayout.setHorizontalGroup(
            panTermosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTermosLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap(55, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(btnDesmarcarTermos, javax.swing.GroupLayout.PREFERRED_SIZE, 155, Short.MAX_VALUE)
            .addComponent(btnDetectarTermos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panTermosLayout.setVerticalGroup(
            panTermosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTermosLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetectarTermos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDesmarcarTermos))
        );

        jLabel6.setText("Relação");

        cmbRelacoes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "is-a", "part-of", "made-of", "property-of", "effect-of", "used-for", "location-of" }));

        btnMarcarRelacao.setText("Marcar relação");
        btnMarcarRelacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarRelacaoActionPerformed(evt);
            }
        });

        btnMarcarRelacaoInvertida.setText("Marcar relação invertida");
        btnMarcarRelacaoInvertida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarRelacaoInvertidaActionPerformed(evt);
            }
        });

        btnDetectarRelacoes.setText("Detectar relações");
        btnDetectarRelacoes.setToolTipText("Usa o classificador selecionado para predizer novas relações");
        btnDetectarRelacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetectarRelacoesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panRelacaoLayout = new javax.swing.GroupLayout(panRelacao);
        panRelacao.setLayout(panRelacaoLayout);
        panRelacaoLayout.setHorizontalGroup(
            panRelacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnMarcarRelacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cmbRelacoes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMarcarRelacaoInvertida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panRelacaoLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(btnDetectarRelacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panRelacaoLayout.setVerticalGroup(
            panRelacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRelacaoLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbRelacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMarcarRelacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMarcarRelacaoInvertida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetectarRelacoes)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel7.setText("Relações semânticas");

        lstRelacoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lstRelacoesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstRelacoesMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(lstRelacoes);

        btnDesmarcarRelacoes.setText("Desmarcar relações");
        btnDesmarcarRelacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesmarcarRelacoesActionPerformed(evt);
            }
        });

        btnInverterRelacoes.setText("Inverter relações");
        btnInverterRelacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInverterRelacoesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panRelacoesLayout = new javax.swing.GroupLayout(panRelacoes);
        panRelacoes.setLayout(panRelacoesLayout);
        panRelacoesLayout.setHorizontalGroup(
            panRelacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRelacoesLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addContainerGap(325, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panRelacoesLayout.createSequentialGroup()
                .addComponent(btnInverterRelacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDesmarcarRelacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panRelacoesLayout.setVerticalGroup(
            panRelacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRelacoesLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panRelacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDesmarcarRelacoes)
                    .addComponent(btnInverterRelacoes)))
        );

        javax.swing.GroupLayout panTermosRelacoesLayout = new javax.swing.GroupLayout(panTermosRelacoes);
        panTermosRelacoes.setLayout(panTermosRelacoesLayout);
        panTermosRelacoesLayout.setHorizontalGroup(
            panTermosRelacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTermosRelacoesLayout.createSequentialGroup()
                .addComponent(panTermos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panRelacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panRelacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panTermosRelacoesLayout.setVerticalGroup(
            panTermosRelacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panTermos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panRelacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panRelacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panBarraStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblBarraStatus.setText("Status");

        javax.swing.GroupLayout panBarraStatusLayout = new javax.swing.GroupLayout(panBarraStatus);
        panBarraStatus.setLayout(panBarraStatusLayout);
        panBarraStatusLayout.setHorizontalGroup(
            panBarraStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBarraStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panBarraStatusLayout.setVerticalGroup(
            panBarraStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBarraStatus)
        );

        panBarraInformacoes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblInformacoes.setText("Informações");

        javax.swing.GroupLayout panBarraInformacoesLayout = new javax.swing.GroupLayout(panBarraInformacoes);
        panBarraInformacoes.setLayout(panBarraInformacoesLayout);
        panBarraInformacoesLayout.setHorizontalGroup(
            panBarraInformacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBarraInformacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformacoes)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panBarraInformacoesLayout.setVerticalGroup(
            panBarraInformacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBarraInformacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformacoes)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panBarraInformacoesClassificador.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblClassificadorSelecionado.setText("Classificador: Nenhum classificador selecionado atualmente");

        javax.swing.GroupLayout panBarraInformacoesClassificadorLayout = new javax.swing.GroupLayout(panBarraInformacoesClassificador);
        panBarraInformacoesClassificador.setLayout(panBarraInformacoesClassificadorLayout);
        panBarraInformacoesClassificadorLayout.setHorizontalGroup(
            panBarraInformacoesClassificadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBarraInformacoesClassificadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblClassificadorSelecionado)
                .addContainerGap(719, Short.MAX_VALUE))
        );
        panBarraInformacoesClassificadorLayout.setVerticalGroup(
            panBarraInformacoesClassificadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBarraInformacoesClassificadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblClassificadorSelecionado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mnuArquivo.setText("Arquivo");

        mnuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuAbrir.setText("Abrir");
        mnuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuAbrir);

        mnuAbrirArquivoRecente.setText("Abrir arquivo recente");
        mnuArquivo.add(mnuAbrirArquivoRecente);

        mnuSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalvar.setText("Salvar");
        mnuSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalvarActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuSalvar);

        mnuSalvarComo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSalvarComo.setText("Salvar como...");
        mnuSalvarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalvarComoActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuSalvarComo);
        mnuArquivo.add(jSeparator1);

        mnuImportar.setText("Importar");

        mnuConverterPalavras.setText("Converter arquivo processado pelo PALAVRAS");
        mnuConverterPalavras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConverterPalavrasActionPerformed(evt);
            }
        });
        mnuImportar.add(mnuConverterPalavras);

        mnuConverterTexto.setText("Converter arquivo de texto puro");
        mnuConverterTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConverterTextoActionPerformed(evt);
            }
        });
        mnuImportar.add(mnuConverterTexto);

        mnuArquivo.add(mnuImportar);

        mnuExportar.setText("Exportar");

        mnuExportarRelacoes.setText("Exportar relações");
        mnuExportarRelacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportarRelacoesActionPerformed(evt);
            }
        });
        mnuExportar.add(mnuExportarRelacoes);

        mnuArquivo.add(mnuExportar);
        mnuArquivo.add(jSeparator6);

        mnuSair.setText("Sair");
        mnuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSairActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuSair);

        menu.add(mnuArquivo);

        mnuEditar.setText("Editar");

        mnuAlterarAnotador.setText("Alterar anotador");
        mnuAlterarAnotador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAlterarAnotadorActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuAlterarAnotador);
        mnuEditar.add(jSeparator2);

        mnuMarcarSentencaAnotada.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        mnuMarcarSentencaAnotada.setText("Marcar sentença como anotada e ir para próxima sentença");
        mnuMarcarSentencaAnotada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMarcarSentencaAnotadaActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuMarcarSentencaAnotada);

        mnuMarcarSentencaIgnorada.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuMarcarSentencaIgnorada.setText("Marcar sentença como ignorada");
        mnuMarcarSentencaIgnorada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMarcarSentencaIgnoradaActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuMarcarSentencaIgnorada);

        mnuSentencaAnterior.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.CTRL_MASK));
        mnuSentencaAnterior.setText("Selecionar sentença anterior");
        mnuSentencaAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSentencaAnteriorActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuSentencaAnterior);

        mnuSentencaProxima.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.InputEvent.CTRL_MASK));
        mnuSentencaProxima.setText("Selecionar próxima sentença");
        mnuSentencaProxima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSentencaProximaActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuSentencaProxima);
        mnuEditar.add(jSeparator3);

        mnuLocalizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        mnuLocalizar.setText("Localizar...");
        mnuLocalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLocalizarActionPerformed(evt);
            }
        });
        mnuEditar.add(mnuLocalizar);

        menu.add(mnuEditar);

        mnuComparar.setText("Comparar pacotes");

        mnuCompararPacotes.setText("Comparar dois pacotes");
        mnuCompararPacotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCompararPacotesActionPerformed(evt);
            }
        });
        mnuComparar.add(mnuCompararPacotes);

        menu.add(mnuComparar);

        mnuAprendizadoDeMaquina.setText("Aprendizado de Máquina");

        mnuConfiguracoes.setText("Configurações");
        mnuConfiguracoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConfiguracoesActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuConfiguracoes);
        mnuAprendizadoDeMaquina.add(jSeparator5);

        mnuTransformar.setText("Transformar pacotes para outros formatos");
        mnuTransformar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTransformarActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuTransformar);

        mnuTreinarClassificador.setText("Treinar classificador");
        mnuTreinarClassificador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTreinarClassificadorActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuTreinarClassificador);

        mnuSelecionarClassificador.setText("Selecionar classificador");
        mnuSelecionarClassificador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSelecionarClassificadorActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuSelecionarClassificador);
        mnuAprendizadoDeMaquina.add(jSeparator4);

        menuDetectarTermosPacote.setText("Detectar termos em todas as sentenças deste pacote");
        menuDetectarTermosPacote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDetectarTermosPacoteActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(menuDetectarTermosPacote);

        mnuDetectarRelacoesPacote.setText("Detectar relações em todas as sentenças deste pacote");
        mnuDetectarRelacoesPacote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDetectarRelacoesPacoteActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuDetectarRelacoesPacote);

        mnuDetectarTermosRelacoesPacote.setText("Detectar termos e relações em todas as sentenças deste pacote");
        mnuDetectarTermosRelacoesPacote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDetectarTermosRelacoesPacoteActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuDetectarTermosRelacoesPacote);

        mnuRemoverTermosNaoUtilizados.setText("Remover termos não utilizados em relações de todas as sentenças do pacote");
        mnuRemoverTermosNaoUtilizados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRemoverTermosNaoUtilizadosActionPerformed(evt);
            }
        });
        mnuAprendizadoDeMaquina.add(mnuRemoverTermosNaoUtilizados);

        menu.add(mnuAprendizadoDeMaquina);

        mnuEstatisticas.setText("Estatísticas");

        mnuQuantidadeRelacoesNestePacote.setText("Quantidade de relações neste pacote");
        mnuQuantidadeRelacoesNestePacote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuQuantidadeRelacoesNestePacoteActionPerformed(evt);
            }
        });
        mnuEstatisticas.add(mnuQuantidadeRelacoesNestePacote);

        mnuQuantidadeRelacoesConjuntoPacotes.setText("Quantidade de relações em um conjunto de pacotes");
        mnuQuantidadeRelacoesConjuntoPacotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuQuantidadeRelacoesConjuntoPacotesActionPerformed(evt);
            }
        });
        mnuEstatisticas.add(mnuQuantidadeRelacoesConjuntoPacotes);

        menu.add(mnuEstatisticas);

        mnuAjuda.setText("Ajuda");

        mnuSobre.setText("Sobre");
        mnuSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSobreActionPerformed(evt);
            }
        });
        mnuAjuda.add(mnuSobre);

        menu.add(mnuAjuda);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panBarraInformacoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panEsquerda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panTermosRelacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panSentenca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(panTopo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panBarraStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panBarraInformacoesClassificador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panTopo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panSentenca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panTermosRelacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panEsquerda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panBarraInformacoesClassificador, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(panBarraInformacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panBarraStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed

        File arquivo = mostrarDialogoAbrirArquivo( null );
        if ( arquivo == null )
            return;
        arquivoAtual = arquivo;

        ultimoDiretorio = arquivoAtual.getParent();
        salvarPropriedades();

        abrirArquivo( arquivoAtual );

    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void mnuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSairActionPerformed
        sair();
    }//GEN-LAST:event_mnuSairActionPerformed

    private void mnuSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSobreActionPerformed
        new Sobre( this, true ).setVisible( true );
    }//GEN-LAST:event_mnuSobreActionPerformed

    private void btnMarcarSelecaoComoTermoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarSelecaoComoTermoActionPerformed
        marcarSelecaoComoTermo();
    }//GEN-LAST:event_btnMarcarSelecaoComoTermoActionPerformed

    private void txtSentencaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSentencaMousePressed

        // se não há nenhum arquivo aberto ou se nenhuma sentença foi selecionada ainda
        if ( sentencaAtual == null )
            return;
        
        if ( txtSentenca.getSelectedText() == null ) {
            mnuMarcarTermo.setEnabled( false );
            mnuDesmarcarTermo.setEnabled( false );
        }
        else {
            mnuMarcarTermo.setEnabled( true );
            mnuDesmarcarTermo.setEnabled( true );
        }

        // clique com o botão direito. Será que ele sempre é o BUTTON3?
        // isto é feito porque o isPopupTrigger é acionado no mouseReleased,
        // ao menos no Windows
        if ( evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3 ) {
            popupTxtSentenca.show( evt.getComponent(), evt.getX(), evt.getY() );
            return;
        }
        
        int posCaret = txtSentenca.getCaretPosition();
        
        int tokenSelecionado = sentencaAtual.qualTokenNestaPosicao( posCaret );

        // Verifica se esse token faz parte de algum termo
        int count = 0;
        boolean selected = false;
        primeiroTermoSelecionado = null;
        for ( Termo t : sentencaAtual.getTermos() ) {  
            if ( tokenSelecionado >= t.getDe() && tokenSelecionado <= t.getAte() ) {
                txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( t.getDe() ) );
                txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( t.getAte() ) );
                lstTermos.setSelectedIndex( count );
                primeiroTermoSelecionado = t;
                selected = true;
            }
            ++count;
        }
        
        if ( !selected ) {
            txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( tokenSelecionado ) );
            txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( tokenSelecionado ) );
            lstTermos.clearSelection();
            // se foi um clique duplo, marca o token selecionado como termo
            if ( evt.getClickCount() == 2 ) 
                marcarSelecaoComoTermo();
        }
        // clique duplo em um termo já marcado indica desmarcação
        else if ( selected && evt.getClickCount() == 2 )    
            desmarcarTermosSelecionados();

        primeiroTokenSelecionado = tokenSelecionado;

    }//GEN-LAST:event_txtSentencaMousePressed

    private void txtSentencaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSentencaMouseReleased

        // se não há nenhum arquivo aberto ou se nenhuma sentença foi selecionada ainda ou nenhum termo está selecionado
        if ( sentencaAtual == null )
            return;
        
        if ( txtSentenca.getSelectedText() == null ) {
            mnuMarcarTermo.setEnabled(false);
            mnuDesmarcarTermo.setEnabled(false);
        }
        else {
            mnuMarcarTermo.setEnabled(true);
            mnuDesmarcarTermo.setEnabled(true);
        }
        
        if ( evt.isPopupTrigger() ) {
            popupTxtSentenca.show(evt.getComponent(), evt.getX(), evt.getY());
            return;
        }
        
        primeiroTermoSelecionado = null;  // acho que nem precisa disto
        
    }//GEN-LAST:event_txtSentencaMouseReleased

    private void mnuMarcarTermoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMarcarTermoActionPerformed
        marcarSelecaoComoTermo();
    }//GEN-LAST:event_mnuMarcarTermoActionPerformed

    private void btnMarcarRelacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarRelacaoActionPerformed
        marcarRelacao( true );
    }//GEN-LAST:event_btnMarcarRelacaoActionPerformed

    private void mnuSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalvarActionPerformed
        salvarArquivo( arquivoAtual );
    }//GEN-LAST:event_mnuSalvarActionPerformed
    
    private void lstTermosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstTermosMousePressed

        if ( lstTermos.isSelectionEmpty() )
            mnuDesmarcarTermo2.setEnabled( false );
        else
            mnuDesmarcarTermo2.setEnabled( true );
        
        if ( evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3 )
            popupLstTermos.show( evt.getComponent(), evt.getX(), evt.getY() );

        if ( lstTermos.isSelectionEmpty() )
            return;

        // Se o botão direito foi pressionado sobre a lista de termos
        if ( evt.getButton() == evt.BUTTON2 ) {

            termosSelecionados = new ArrayList<Integer>();
            for ( int i : lstTermos.getSelectedIndices() ) {
                //System.out.println( i );
                termosSelecionados.add( i );
            }

            int x = evt.getX();
            int y = evt.getY();
            Point p = new Point( x, y );
            Rectangle r;
            System.out.println( "Oi! Clique com o botão do meio do mouse em " + x + ", " + y );
            boolean achou = false;
            int celula = -1;
            for ( int i = 0; i < lstTermos.getModel().getSize(); ++i ) {
                r = lstTermos.getCellBounds( i, i );
                if ( r.contains( p ) ) {
                    achou = true;
                    celula = i;
                    break;
                }
            }

            if ( achou ) {
                if ( termosSelecionados.contains( celula ) )
                    termosSelecionados.remove( new Integer( celula ) );
                else
                    termosSelecionados.add( celula );
                
                System.out.println( "Celula clicada: " + celula );
                int[] indicesSelecionados = new int[ termosSelecionados.size() ];
                for ( int i = 0; i < termosSelecionados.size(); ++i )
                    indicesSelecionados[i] = termosSelecionados.get( i );
                lstTermos.setSelectedIndices( indicesSelecionados );
                return;
            }

        }
        
        ultimoTermoSelecionado = lstTermos.getSelectedIndex();
        Termo selecionado = (Termo) lstTermos.getSelectedValue();
        txtSentenca.requestFocusInWindow();
        txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( selecionado.getDe() ) );
        txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( selecionado.getAte() ) );
        
/*        if ( evt.getClickCount() == 2 && timer.isRunning() ) {
            termosSelecionados.add( ultimoTermoSelecionado );
            int[] indicesSelecionados = new int[ termosSelecionados.size() ];
            for ( int i = 0; i < termosSelecionados.size(); ++i )
                indicesSelecionados[i] = termosSelecionados.get( i );
            lstTermos.setSelectedIndices( indicesSelecionados );
            cliqueDuplo = true;
//            System.out.println( "Clique duplo!" );
        }
        else */if ( evt.getClickCount() == 2 ) {
            termosSelecionados.add( ultimoTermoSelecionado );
            int[] indicesSelecionados = new int[ termosSelecionados.size() ];
            for ( int i = 0; i < termosSelecionados.size(); ++i )
                indicesSelecionados[i] = termosSelecionados.get( i );
            lstTermos.setSelectedIndices( indicesSelecionados );
            cliqueDuplo = true;
//            System.out.println( "Clique duplo!" );
        }
        else {
            Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty( "awt.multiClickInterval" );
            // O timer verifica se foi um clique duplo ou não
            timer = new Timer( timerinterval.intValue(), new ActionListener() {
                public void actionPerformed( ActionEvent evt ) {
                    if ( cliqueDuplo )
                        cliqueDuplo = false;
                    else { // clique simples
                        termosSelecionados = new ArrayList<Integer>( ultimoTermoSelecionado );
//                        System.out.println( "Clique simples!" );
                    }
                }
            });
            timer.setRepeats( false );
            timer.start();
        }

    }//GEN-LAST:event_lstTermosMousePressed

    private void mnuDesmarcarTermoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesmarcarTermoActionPerformed
        desmarcarTermosSelecionados();
    }//GEN-LAST:event_mnuDesmarcarTermoActionPerformed

    private void btnDesmarcarTermosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesmarcarTermosActionPerformed
        desmarcarTermosSelecionados();
    }//GEN-LAST:event_btnDesmarcarTermosActionPerformed

    private void btnDesmarcarRelacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesmarcarRelacoesActionPerformed
        desmarcarRelacoesSelecionadas();
    }//GEN-LAST:event_btnDesmarcarRelacoesActionPerformed

    private void mnuDesmarcarRelacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesmarcarRelacaoActionPerformed
        desmarcarRelacoesSelecionadas();
    }//GEN-LAST:event_mnuDesmarcarRelacaoActionPerformed

    private void lstRelacoesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstRelacoesMousePressed
        // se não há nenhuma relação selecionada
        if ( lstRelacoes.isSelectionEmpty() ) {
            mnuInverterRelacao.setEnabled( false );
            mnuDesmarcarRelacao.setEnabled( false );
        }
        // se ha alguma relação selecionada
        else {
            mnuInverterRelacao.setEnabled( true );
            mnuDesmarcarRelacao.setEnabled( true );
        }
        if ( evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3 )
            popupLstRelacoes.show( evt.getComponent(), evt.getX(), evt.getY() );
        if ( evt.getClickCount() == 2 )
            inverterRelacoesSelecionadas();
    }//GEN-LAST:event_lstRelacoesMousePressed

        private void lstRelacoesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstRelacoesMouseReleased
        // se não há nenhuma relação selecionada
        if ( lstRelacoes.isSelectionEmpty() ) {
            mnuInverterRelacao.setEnabled(false);
            mnuDesmarcarRelacao.setEnabled(false);
        }
        // se ha alguma relação selecionada
        else {
            mnuInverterRelacao.setEnabled(true);
            mnuDesmarcarRelacao.setEnabled(true);
        }
        if ( evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3 )
            popupLstRelacoes.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_lstRelacoesMouseReleased

    private void mnuSalvarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalvarComoActionPerformed
        // Seleciona arquivo a ser salvo
        File arquivoNovo = mostrarDialogoSalvarArquivo( null, arquivoAtual );
        if ( arquivoNovo == null )
            return;
        adicionarArquivoListaAbertosRecentemente( arquivoNovo );
        salvarArquivo( arquivoNovo );
        arquivoAtual = arquivoNovo;
        atualizarBarraStatus();
    }//GEN-LAST:event_mnuSalvarComoActionPerformed

    private void btnInverterRelacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInverterRelacoesActionPerformed
        inverterRelacoesSelecionadas();
    }//GEN-LAST:event_btnInverterRelacoesActionPerformed

    private void mnuInverterRelacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInverterRelacaoActionPerformed
        inverterRelacoesSelecionadas();
    }//GEN-LAST:event_mnuInverterRelacaoActionPerformed

    private void lstTermosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstTermosMouseReleased
        if ( lstTermos.isSelectionEmpty() )
            mnuDesmarcarTermo2.setEnabled( false );
        else
            mnuDesmarcarTermo2.setEnabled( true );
        if ( evt.isPopupTrigger() || evt.getButton() == evt.BUTTON3 )
            popupLstTermos.show( evt.getComponent(), evt.getX(), evt.getY() );
    }//GEN-LAST:event_lstTermosMouseReleased

    private void mnuDesmarcarTermo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesmarcarTermo2ActionPerformed
        desmarcarTermosSelecionados();
    }//GEN-LAST:event_mnuDesmarcarTermo2ActionPerformed

    private void txtSentencaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSentencaKeyPressed
        if ( txtSentenca.getSelectedText() == null )
            return;
        if ( evt.getKeyCode() == evt.VK_DELETE )
            desmarcarTermosSelecionados();
    }//GEN-LAST:event_txtSentencaKeyPressed

    private void btnSentencaAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSentencaAnteriorActionPerformed
        selecionarSentencaAnterior();
    }//GEN-LAST:event_btnSentencaAnteriorActionPerformed

    public void selecionarSentencaAnterior() {
        int indice = 0;
        if ( lstListaSentencas.isSelectionEmpty() )
            indice = 0;
        else if ( lstListaSentencas.getSelectedIndex() > 0 )
            indice = lstListaSentencas.getSelectedIndex() - 1;
        lstListaSentencas.setSelectedIndex( indice );
        lstListaSentencas.ensureIndexIsVisible( indice );                    
    }
    
    public void selecionarSentencaProxima () {
        int indice = 0;
        if ( lstListaSentencas.isSelectionEmpty() )
            indice = 0;
        else if ( lstListaSentencas.getSelectedIndex() < lstListaSentencas.getModel().getSize() - 1 )
            indice = lstListaSentencas.getSelectedIndex() + 1;
        lstListaSentencas.setSelectedIndex( indice );
        lstListaSentencas.ensureIndexIsVisible( indice );
    }
    
    private void btnSentencaProximaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSentencaProximaActionPerformed
        selecionarSentencaProxima();
    }//GEN-LAST:event_btnSentencaProximaActionPerformed

    private void mnuSentencaAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSentencaAnteriorActionPerformed
        selecionarSentencaAnterior();
    }//GEN-LAST:event_mnuSentencaAnteriorActionPerformed

    private void mnuSentencaProximaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSentencaProximaActionPerformed
        selecionarSentencaProxima();
    }//GEN-LAST:event_mnuSentencaProximaActionPerformed

    private void mnuAlterarAnotadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAlterarAnotadorActionPerformed
        new Anotador( this, true ).setVisible( true );
    }//GEN-LAST:event_mnuAlterarAnotadorActionPerformed

    private void txtSentencaMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSentencaMouseDragged
        
        int tokenSelecionado = sentencaAtual.qualTokenNestaPosicao( txtSentenca.getCaretPosition() );

        // Verifica se esse token faz parte de algum termo
        Termo termoSelecionado = null;
        for ( Termo t : sentencaAtual.getTermos() )
            if ( tokenSelecionado >= t.getDe() && tokenSelecionado <= t.getAte() )
                termoSelecionado = t;
        
        // tenho certeza que tem algum jeito melhor de fazer isso
        
        if ( primeiroTermoSelecionado != null ) {
            if ( termoSelecionado != null ) {
                if ( primeiroTermoSelecionado.getDe() < termoSelecionado.getDe() ) {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( primeiroTermoSelecionado.getDe() ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( termoSelecionado.getAte() ) );
                }
                else {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( termoSelecionado.getDe() ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( primeiroTermoSelecionado.getAte() ) );
                }
            }
            else {
                if ( primeiroTermoSelecionado.getDe() < tokenSelecionado ) {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( primeiroTermoSelecionado.getDe() ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( tokenSelecionado ) );
                }
                else {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( tokenSelecionado ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( primeiroTermoSelecionado.getAte() ) );
                }
            }
        }
        else {
            if ( termoSelecionado != null ) {
                if ( primeiroTokenSelecionado < termoSelecionado.getDe() ) {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( primeiroTokenSelecionado ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( termoSelecionado.getAte() ) );
                }
                else {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( termoSelecionado.getDe() ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( primeiroTokenSelecionado ) );
                }
            }
            else {
                if ( primeiroTokenSelecionado < tokenSelecionado ) {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( primeiroTokenSelecionado ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( tokenSelecionado ) );
                }
                else {
                    txtSentenca.setSelectionStart( sentencaAtual.ondeTokenComeca( tokenSelecionado ) );
                    txtSentenca.setSelectionEnd( sentencaAtual.ondeTokenTermina( primeiroTokenSelecionado ) );
                }
            }
        }

    }//GEN-LAST:event_txtSentencaMouseDragged

    private void lstTermosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstTermosKeyPressed
        if ( lstTermos.getSelectedIndex() == -1 )
            return;
        if ( evt.getKeyCode() == evt.VK_DELETE )
            desmarcarTermosSelecionados();
    }//GEN-LAST:event_lstTermosKeyPressed

    private void chkAnotadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAnotadaActionPerformed
        sentencaAtual.setAnotada( chkAnotada.isSelected() );
        if ( chkAnotada.isSelected() )
            sentencaAtual.setIgnorada( false );
        atualizarAnotada();
        atualizarIgnorada();
        modificado = true;
    }//GEN-LAST:event_chkAnotadaActionPerformed

    private void mnuMarcarSentencaAnotadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMarcarSentencaAnotadaActionPerformed
        marcarSentencaAnotada();
    }//GEN-LAST:event_mnuMarcarSentencaAnotadaActionPerformed

    private void btnIgnorarSentencaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIgnorarSentencaActionPerformed
        marcarSentencaIgnorada();
    }//GEN-LAST:event_btnIgnorarSentencaActionPerformed

    private void btnMarcarSentencaAnotadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarSentencaAnotadaActionPerformed
        marcarSentencaAnotada();
    }//GEN-LAST:event_btnMarcarSentencaAnotadaActionPerformed

    private void chkIgnoradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIgnoradaActionPerformed
        sentencaAtual.setIgnorada( chkIgnorada.isSelected() );
        if ( chkIgnorada.isSelected() )
            sentencaAtual.setAnotada( false );
        atualizarAnotada();
        atualizarIgnorada();
        modificado = true;
    }//GEN-LAST:event_chkIgnoradaActionPerformed

    private void mnuMarcarSentencaIgnoradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMarcarSentencaIgnoradaActionPerformed
        marcarSentencaIgnorada();
    }//GEN-LAST:event_mnuMarcarSentencaIgnoradaActionPerformed

    private void mnuCompararPacotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCompararPacotesActionPerformed
        new CompararPacotes( this ).setVisible( true );
    }//GEN-LAST:event_mnuCompararPacotesActionPerformed

    private void mnuLocalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLocalizarActionPerformed
        instanciaLocalizar = Localizar.novaInstancia( this );
        instanciaLocalizar.setVisible( true );
        instanciaLocalizar.requestFocus();
        instanciaLocalizar.focarCaixa();
    }//GEN-LAST:event_mnuLocalizarActionPerformed
    
    private void btnMarcarRelacaoInvertidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarRelacaoInvertidaActionPerformed
        marcarRelacao( false );
    }//GEN-LAST:event_btnMarcarRelacaoInvertidaActionPerformed

    private void btnDetectarRelacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetectarRelacoesActionPerformed
        detectarRelacoesSentenca( sentencaAtual );
    }//GEN-LAST:event_btnDetectarRelacoesActionPerformed

    private void mnuDetectarRelacoesPacoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDetectarRelacoesPacoteActionPerformed
        detectarRelacoesPacote();
    }//GEN-LAST:event_mnuDetectarRelacoesPacoteActionPerformed

    private void mnuTreinarClassificadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTreinarClassificadorActionPerformed
        new TreinarClassificador( this ).setVisible( true );
    }//GEN-LAST:event_mnuTreinarClassificadorActionPerformed

    private void mnuSelecionarClassificadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSelecionarClassificadorActionPerformed

        File arquivoModelo = mostrarDialogoAbrirArquivo( null );
        if ( arquivoModelo == null )
            return;
        ultimoDiretorio = arquivoModelo.getParent();

        Classificador novoClassificador = null;
        String qualClassificador;
        try {
            qualClassificador = UtilidadesPacotes.verificarClassificador( arquivoModelo );
            if ( !qualClassificador.equals( "j48" ) && !qualClassificador.equals( "svm" ) )
                throw new IllegalArgumentException();
        }
        catch ( IllegalArgumentException ex ) {
            javax.swing.JOptionPane.showMessageDialog( null,
                "O arquivo selecionado não é um classificador",
                "Erro",
                javax.swing.JOptionPane.ERROR_MESSAGE );
            return;
        }

        if ( classificador != null ) {
            classificador.finalizar();
            classificador = null;
        }
        
        if ( qualClassificador.equals( "j48" ) )
            novoClassificador = new ClassificadorDT( arquivoModelo );
        else if ( qualClassificador.equals( "svm" ) )
            novoClassificador = new ClassificadorSVM( arquivoModelo );
        
        novoClassificador.adicionarParametro( "caminho.svmlight.learn", caminhoSvmLightLearn );
        novoClassificador.adicionarParametro( "caminho.svmlight.classify", caminhoSvmLightClassify );

        classificador = novoClassificador;
        
        lblClassificadorSelecionado.setText( "Classificador: " + classificador.getNome() );
        
        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Classificador selecionado: {0}", arquivoModelo.getAbsolutePath() );
        
    }//GEN-LAST:event_mnuSelecionarClassificadorActionPerformed

    private void mnuTransformarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTransformarActionPerformed
        new TransformarDados( this ).setVisible( true );
    }//GEN-LAST:event_mnuTransformarActionPerformed

    private void mnuQuantidadeRelacoesNestePacoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuQuantidadeRelacoesNestePacoteActionPerformed
        
        EstatisticasPacoteAtual e = new EstatisticasPacoteAtual();
        e.setVisible( true );
        
        String[] relacoes = { "is-a", "part-of", "property-of", "location-of",
                              "made-of", "used-for", "effect-of" };
        int[] quantidade = new int[relacoes.length];
        
        for ( Sentenca s : listaSentencas )
            for ( int i = 0; i < relacoes.length; ++i )
                for ( Relacao r : s.getRelacoes() )
                    if ( r.getRelacao().equals(relacoes[i]) )
                        quantidade[i]++;

        String saida = "";
        int total = 0;
        for ( int i = 0; i < relacoes.length; ++i ) {
            saida += "Relação " + relacoes[i] + ": " + quantidade[i] + "\n";
            total += quantidade[i];
        }
        saida += "Total: " + total + "\n";
        e.setTxtSaida( saida );

    }//GEN-LAST:event_mnuQuantidadeRelacoesNestePacoteActionPerformed
    
    private void btnDetectarTermosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetectarTermosActionPerformed
        detectarTermosSentenca( sentencaAtual );
    }//GEN-LAST:event_btnDetectarTermosActionPerformed

    private void menuDetectarTermosPacoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDetectarTermosPacoteActionPerformed
        detectarTermosPacote();
    }//GEN-LAST:event_menuDetectarTermosPacoteActionPerformed

    /**
     * Detecta todos os termos das sentenças não anotadas e não ignoradas deste
     * pacote
     */
    private void detectarTermosPacote() {
        this.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        for ( Sentenca sentenca : listaSentencas )
            if ( !sentenca.isAnotada() && !sentenca.isIgnorada() )
                detectarTermosSentenca( sentenca );
        this.setCursor( null );
    }
    
    private void mnuDetectarTermosRelacoesPacoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDetectarTermosRelacoesPacoteActionPerformed
        if ( classificador == null ) {
            JOptionPane.showMessageDialog( null,
                    "Nenhum classificador de relações está carregado no momento",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        detectarTermosPacote();
        detectarRelacoesPacote();
    }//GEN-LAST:event_mnuDetectarTermosRelacoesPacoteActionPerformed

    private void mnuQuantidadeRelacoesConjuntoPacotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuQuantidadeRelacoesConjuntoPacotesActionPerformed
        new Estatisticas( this ).setVisible( true );
    }//GEN-LAST:event_mnuQuantidadeRelacoesConjuntoPacotesActionPerformed

    /**
     * Exporta as relações presentes no pacote atualmente aberto para um arquivo
     * externo.
     * 
     * @param evt 
     */
    private void mnuExportarRelacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportarRelacoesActionPerformed

        File arquivo = mostrarDialogoSalvarArquivo( null, null );
        if ( arquivo == null )
            return;

        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Gravando arquivo de exporta\u00e7\u00e3o de rela\u00e7\u00f5es {0}", arquivo.getAbsolutePath());

        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(arquivo));
            for ( Sentenca s : listaSentencas )
                for ( Relacao r : s.getRelacoes() ) {
                    bw.write(s.getId() + "\t" + r.toString());
                    bw.newLine();
                }
            bw.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex );
        }
                
    }//GEN-LAST:event_mnuExportarRelacoesActionPerformed

    private void mnuRemoverTermosNaoUtilizadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRemoverTermosNaoUtilizadosActionPerformed
        removerTermosNaoUtilizados();
    }//GEN-LAST:event_mnuRemoverTermosNaoUtilizadosActionPerformed

    private void mnuConfiguracoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConfiguracoesActionPerformed
        new ConfiguracoesAM( this ).setVisible( true );
    }//GEN-LAST:event_mnuConfiguracoesActionPerformed

    private void mnuConverterPalavrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConverterPalavrasActionPerformed

        File arquivoPalavras = mostrarDialogoAbrirArquivo( "Selecione o arquivo processado pelo PALAVRAS que deseja converter" );
        if ( arquivoPalavras == null )
            return;

        File arquivoSaida = mostrarDialogoSalvarArquivo( "Escolha o nome do arquivo que será salvo e seu local", new File( "pacote.json" ) );
        if ( arquivoSaida == null )
            return;

        UtilidadesPacotes.conveterPalavrasJson( arquivoPalavras, arquivoSaida );
        
        new Sucesso( this, true, "Arquivo convertido com sucesso" ).setVisible( true );
        
    }//GEN-LAST:event_mnuConverterPalavrasActionPerformed

    private void mnuConverterTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConverterTextoActionPerformed

        File arquivoTexto = mostrarDialogoAbrirArquivo( "Selecione o arquivo de texto que deseja converter" );
        if ( arquivoTexto == null )
            return;

        File arquivoSaida = mostrarDialogoSalvarArquivo( "Escolha o nome do arquivo que será salvo e seu local", new File( "pacote.json" ) );
        if ( arquivoSaida == null )
            return;

        UtilidadesPacotes.converterTextoJson( arquivoTexto, arquivoSaida );
        
        new Sucesso( this, true, "Arquivo convertido com sucesso" ).setVisible( true );
        
    }//GEN-LAST:event_mnuConverterTextoActionPerformed

    /**
     * Marca a sequencia de tokens atualmente selecionados como um termo.
     * 
     * @see Token
     */
    public void marcarSelecaoComoTermo() {

        // Se nada estiver selecionado, não há nada a fazer
        if ( txtSentenca.getSelectedText() == null )
            return;
        
        int tokenInicio = sentencaAtual.qualTokenNestaPosicao( txtSentenca.getSelectionStart() );
        int tokenFim = sentencaAtual.qualTokenNestaPosicao( txtSentenca.getSelectionEnd() );

        sentencaAtual.marcarTermo( tokenInicio, tokenFim, txtSentenca.getSelectedText() );
        
        atualizarTexto();
        atualizarListaTermos();
        atualizarAnotadores();
        modificado = true;
    }
    
    /**
     * Desmarca os termos que estao selecionados.
     */
    public void desmarcarTermosSelecionados() {
        if ( lstTermos.isSelectionEmpty() && txtSentenca.getSelectedText() == null )
            return;
        
        for ( Object o : lstTermos.getSelectedValues() )
            sentencaAtual.desmarcarTermo( (Termo)o );

        txtSentenca.setCaretPosition( 0 );

        atualizarTexto();
        atualizarListaTermos();
        atualizarListaRelacoes();
        atualizarAnotadores();
        modificado = true;
    }
    
    /**
     * Atualiza a lista de termos da sentença atual na interface.
     */
    public void atualizarListaTermos() {
        DefaultListModel listModel = new DefaultListModel();
        for ( Termo t : sentencaAtual.getTermos() )
            listModel.addElement( t );
        lstTermos.setModel( listModel );
    }

    /**
     * Marca os dois termos selecionados atualmente na lista de termos como uma
     * relação semântica binaria. Se mais ou menos de dois termos estao
     * selecionados, mostra uma mensagem de erro.
     * 
     * @param ordemDireta Se verdadeiro, a relação é marcada em ordem normal;
     *                    se falso, a relação é marcada em ordem invertida.
     */
    public void marcarRelacao( boolean ordemDireta ) {
        
        Object[] selectedValues = lstTermos.getSelectedValues();
        // Este método só existe a partir do Java 1.6 pelo jeito
        //List<Termo> selectedValues = lstTermos.getSelectedValuesList();

        // Mais ou menos de 2 termos estão selecionados
        if ( selectedValues.length != 2 ) {
            JOptionPane.showMessageDialog(null,
                    "Exatamente 2 termos devem estar selecionados",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String relacao = (String)cmbRelacoes.getSelectedItem();
        Termo termo1 = (Termo)selectedValues[0];
        Termo termo2 = (Termo)selectedValues[1];

        if ( ordemDireta )
            sentencaAtual.marcarRelacao( relacao, termo1, termo2 );
        else
            sentencaAtual.marcarRelacao( relacao, termo2, termo1 );
        
        atualizarListaRelacoes();
        atualizarAnotadores();
        modificado = true;
        
    }

    /**
     * Desmarca as relações que estão selecionadas na lista de relações.
     */
    public void desmarcarRelacoesSelecionadas() {
        if ( lstRelacoes.isSelectionEmpty() )
            return;
        Object[] relacoesSelecionadas = lstRelacoes.getSelectedValues();
        for ( Object o : relacoesSelecionadas )
            sentencaAtual.desmarcarRelacao((Relacao)o);
        atualizarListaRelacoes();
        atualizarAnotadores();
        modificado = true;
    }

    /**
     * Salva o pacote de sentenças aberto atualmente no arquivo 'arquivo'
     * 
     * @param arquivo Arquivo onde o pacote atualmente aberto será salvo
     */
    public void salvarArquivo( File arquivo ) {
        
        BufferedWriter bw;
        
        try {
            bw = new BufferedWriter( new FileWriter( arquivo ) );
            bw.write( cabecalho );
            for ( Sentenca s : listaSentencas ) {
                bw.write( s.toJson() );
                bw.newLine();
            }
            bw.close();
            modificado = false;
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }

        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo {0} salvo com sucesso", arquivo.getAbsolutePath() );
        
    }

    /**
     * Atualiza a lista de sentenças na interface.
     */
    public void atualizarListaSentencas () {
        int indiceSelecionado = lstListaSentencas.getSelectedIndex();
        DefaultListModel listModel = new DefaultListModel();
        for ( Sentenca sentenca : listaSentencas )
            listModel.addElement(sentenca);
        lstListaSentencas.setModel(listModel);
        lstListaSentencas.setSelectedIndex(indiceSelecionado);
    }

    /**
     * Atualiza a sentença de índice 'indice' na lista de sentenças na
     * interface. Este método foi criado porque a lista de sentenças pode ser
     * grande e às vezes é necessario atualizar apenas uma das sentenças.
     * 
     * @param indice Índice da sentença a ser atualizada
     */
    public void atualizarListaSentencas ( int indice ) {
        int indiceSelecionado = lstListaSentencas.getSelectedIndex();
        DefaultListModel listModel = (DefaultListModel)lstListaSentencas.getModel();
        listModel.set( indice, sentencaAtual );
        lstListaSentencas.setModel(listModel);
        lstListaSentencas.setSelectedIndex(indiceSelecionado);
    }

    /**
     * Atualiza a lista de relações da sentença atual na interface.
     */
    public void atualizarListaRelacoes() {
        DefaultListModel listModel = new DefaultListModel();
        for ( Relacao relacao : sentencaAtual.getRelacoes() )
            listModel.addElement( relacao );
        lstRelacoes.setModel( listModel );
    }
    
    /**
     * Atualiza a caixa de comentários com os comentários da sentença atual.
     * <p>
     * Este método só é chamado quando carregamos uma sentença, então a escrita
     * que é feita na caixa de comentários nao é uma modificação na verdade
     */
    public void atualizarComentarios() {
        txtComentarios.setText( sentencaAtual.getComentarios() );
    }
    
    /**
     * Inverte a ordem das relações selecionadas na lista de relações.
     */
    public void inverterRelacoesSelecionadas () {
        
        if ( lstRelacoes.isSelectionEmpty() )
            return;

        for ( int i : lstRelacoes.getSelectedIndices() )
            ((Relacao)lstRelacoes.getModel().getElementAt(i)).inverter();

        atualizarListaRelacoes();
        atualizarAnotadores();
        modificado = true;
        
    }

    /**
     * Atualiza a barra de status da interface
     */
    public void atualizarBarraStatus () {
        String status = "Anotador: " + anotadorAtual;
        if ( arquivoAtual != null )
            status += " - Arquivo aberto: " + arquivoAtual.getAbsolutePath();
        lblBarraStatus.setText(status);
    }
    
    /**
     * Atualiza os anotadores que trabalharam na sentença em edição atualmente.
     * <p>
     * Este método é chamado sempre que é feita alguma alteracao na sentença.
     */
    public void atualizarAnotadores () {
        sentencaAtual.adicionarAnotador( anotadorAtual );
    }
    
    /**
     * Método chamado quando a aplicação é terminada.
     */
    public void sair() {
        int result = JOptionPane.YES_OPTION;
        if ( modificado ) {
            result = JOptionPane.showConfirmDialog(
                    null,
                    "Existem modificações não salvas neste arquivo! Quer salvá-las antes de sair?",
                    "Modificações não salvas",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
        }
        if ( result == JOptionPane.YES_OPTION || result == JOptionPane.NO_OPTION ) {
            if ( classificador != null )
                classificador.finalizar();
            if ( result == JOptionPane.YES_OPTION && modificado )
                salvarArquivo( arquivoAtual );
            salvarPropriedades();
            System.exit( 0 );
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {

        System.out.println( "DEBUG: Argumentos de linha de comando:" );
        for ( String s : args ) {
            System.out.println( "DEBUG: " + s );
        }
    
        if ( args.length > 0 ) {

            String formato, arquivoSaida, arquivoEntrada, diretorioEntrada;
            String caminhoSvmLearn, caminhoSvmClassify, arquivoModelo;
            
            // Conversão de dados
            if ( args[0].equals( "-converter" ) ) {
                formato = args[1];
                diretorioEntrada = args[2];
                arquivoSaida = args[3];

                File diretorio = new File( diretorioEntrada );
                List<File> arquivosEntrada = new ArrayList<File>();
                for ( File f : diretorio.listFiles() )
                    if ( f.isFile() ) {
                        System.out.println( "DEBUG: Pacote de anotacao: " + f );
                        arquivosEntrada.add( f );
                    }
                Transformador transformador = new Transformador( arquivosEntrada );
                if ( formato.equals( "arff" ) )
                    transformador.paraDt( new File( arquivoSaida ) );
                else if ( formato.equals( "svmlight") )
                    transformador.paraSvm( new File( arquivoSaida ) );
                Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo {0} gerado com sucesso", arquivoSaida );
                return;
            }

            // Transformaçao de arquivo do PALAVRAS
            else if ( args[0].equals( "-transformar_palavras" ) ) {
                arquivoEntrada = args[1];
                arquivoSaida = args[2];
                UtilidadesPacotes.conveterPalavrasJson( new File( arquivoEntrada ), new File( arquivoSaida ) );
                return;
            }

            // Transformaçao de arquivo de texto puro
            else if ( args[0].equals( "-transformar_texto" ) ) {
                arquivoEntrada = args[1];
                arquivoSaida = args[2];
                UtilidadesPacotes.converterTextoJson( new File( arquivoEntrada ), new File( arquivoSaida ) );
                return;
            }

            // Treinamento de classificador de árvore de decisão
            else if ( args[0].equals( "-treinar_arvore" ) ) {
                arquivoEntrada = args[1];
                arquivoSaida = args[2];
                Classificador classificador = new ClassificadorDT( "Árvore de decisão J48" );
                classificador.treinar( new File( arquivoEntrada ), "-C 0.25 -M 2" );
                classificador.gravar( new File( arquivoSaida ) );
                return;
            }

            // Treinamento de classificador SVM
            else if ( args[0].equals( "-treinar_svm" ) ) {
                caminhoSvmLearn = args[1];
                caminhoSvmClassify = args[2];
                arquivoEntrada = args[3];
                arquivoSaida = args[4];

                Classificador classificador = new ClassificadorSVM( "SVM (SVM Light)" );
                classificador.adicionarParametro( "caminho.svmlight.learn", caminhoSvmLearn );
                classificador.adicionarParametro( "caminho.svmlight.classify", caminhoSvmClassify );
                classificador.treinar( new File( arquivoEntrada ), "-t 1 -d 3 -c 0.001 -m 1024" );
                classificador.gravar( new File( arquivoSaida ) );
                return;

            }

            // Idnetificação de termos
            else if ( args[0].equals( "-identificar_termos" ) ) {
                arquivoEntrada = args[1];
                arquivoSaida = args[2];

                String cabecalho = UtilidadesPacotes.lerCabecalhoPacote( new File( arquivoEntrada ) );
                List<Sentenca> listaSentencas = UtilidadesPacotes.lerSentencasPacote( new File( arquivoEntrada ) );
                for ( Sentenca sentenca : listaSentencas )
                    if ( !sentenca.isAnotada() && !sentenca.isIgnorada() )
                        sentenca.detectarTermos();

                BufferedWriter bw;
                try {
                    bw = new BufferedWriter( new OutputStreamWriter(  new FileOutputStream( arquivoSaida ), "UTF-8"  ) );
                    bw.write( cabecalho );
                    for ( Sentenca s : listaSentencas ) {
                        bw.write( s.toJson() );
                        bw.newLine();
                    }
                    bw.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
                }
                Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo {0} salvo com sucesso", arquivoSaida );
                return;

            }

            // Idnetificação de relações com árvore
            else if ( args[0].equals( "-identificar_relacoes_com_arvore" ) ) {
                arquivoModelo = args[1];
                arquivoEntrada = args[2];
                arquivoSaida = args[3];

                String cabecalho = UtilidadesPacotes.lerCabecalhoPacote( new File( arquivoEntrada ) );
                List<Sentenca> listaSentencas = UtilidadesPacotes.lerSentencasPacote( new File( arquivoEntrada ) );

                Classificador classificador = new ClassificadorDT( new File( arquivoModelo ) );

                for ( Sentenca sentenca : listaSentencas )
                    if ( !sentenca.isAnotada() && !sentenca.isIgnorada() )
                        sentenca.detectarRelacoes( classificador );

                BufferedWriter bw;
                try {
                    bw = new BufferedWriter( new OutputStreamWriter(  new FileOutputStream( arquivoSaida ), "UTF-8"  ) );
                    bw.write( cabecalho );
                    for ( Sentenca s : listaSentencas ) {
                        bw.write( s.toJson() );
                        bw.newLine();
                    }
                    bw.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
                }
                Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo {0} salvo com sucesso", arquivoSaida );                

                classificador.finalizar();
                return;

            }

            // Idnetificação de relações com SVM
            else if ( args[0].equals( "-identificar_relacoes_com_svm" ) ) {
                caminhoSvmLearn = args[1];
                caminhoSvmClassify = args[2];
                arquivoModelo = args[3];
                arquivoEntrada = args[4];
                arquivoSaida = args[5];

                String cabecalho = UtilidadesPacotes.lerCabecalhoPacote( new File( arquivoEntrada ) );
                List<Sentenca> listaSentencas = UtilidadesPacotes.lerSentencasPacote( new File( arquivoEntrada ) );

                Classificador classificador = new ClassificadorSVM( new File( arquivoModelo ) );
                classificador.adicionarParametro( "caminho.svmlight.learn", caminhoSvmLearn );
                classificador.adicionarParametro( "caminho.svmlight.classify", caminhoSvmClassify );

                for ( Sentenca sentenca : listaSentencas )
                    if ( !sentenca.isAnotada() && !sentenca.isIgnorada() )
                        sentenca.detectarRelacoes( classificador );

                BufferedWriter bw;
                try {
                    bw = new BufferedWriter( new OutputStreamWriter(  new FileOutputStream( arquivoSaida ), "UTF-8"  ) );
                    bw.write( cabecalho );
                    for ( Sentenca s : listaSentencas ) {
                        bw.write( s.toJson() );
                        bw.newLine();
                    }
                    bw.close();
                }
                catch ( IOException ex ) {
                    Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
                }
                Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo {0} salvo com sucesso", arquivoSaida );                

                classificador.finalizar();
                return;

            }

            else {
                System.out.println( "Argumento invalido!" );
                return;
            }

        }
            
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger( "ARS logger" ).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger( "ARS logger" ).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger( "ARS logger" ).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger( "ARS logger" ).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater( new Runnable() {
            @Override
            public void run() {
                final Main main = new Main();
                main.addWindowListener( new WindowAdapter() {
                    @Override
                    public void windowClosing( WindowEvent e ) {
                        main.sair();
                    }
                });
                main.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDesmarcarRelacoes;
    private javax.swing.JButton btnDesmarcarTermos;
    private javax.swing.JButton btnDetectarRelacoes;
    private javax.swing.JButton btnDetectarTermos;
    private javax.swing.JButton btnIgnorarSentenca;
    private javax.swing.JButton btnInverterRelacoes;
    private javax.swing.JButton btnMarcarRelacao;
    private javax.swing.JButton btnMarcarRelacaoInvertida;
    private javax.swing.JButton btnMarcarSelecaoComoTermo;
    private javax.swing.JButton btnMarcarSentencaAnotada;
    private javax.swing.JButton btnSentencaAnterior;
    private javax.swing.JButton btnSentencaProxima;
    private javax.swing.JCheckBox chkAnotada;
    private javax.swing.JCheckBox chkIgnorada;
    private javax.swing.JComboBox cmbRelacoes;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JLabel lblBarraStatus;
    private javax.swing.JLabel lblClassificadorSelecionado;
    private javax.swing.JLabel lblInformacoes;
    private javax.swing.JList lstListaSentencas;
    private javax.swing.JList lstRelacoes;
    private javax.swing.JList lstTermos;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem menuDetectarTermosPacote;
    private javax.swing.JMenuItem mnuAbrir;
    private javax.swing.JMenu mnuAbrirArquivoRecente;
    private javax.swing.JMenu mnuAjuda;
    private javax.swing.JMenuItem mnuAlterarAnotador;
    private javax.swing.JMenu mnuAprendizadoDeMaquina;
    private javax.swing.JMenu mnuArquivo;
    private javax.swing.JMenu mnuComparar;
    private javax.swing.JMenuItem mnuCompararPacotes;
    private javax.swing.JMenuItem mnuConfiguracoes;
    private javax.swing.JMenuItem mnuConverterPalavras;
    private javax.swing.JMenuItem mnuConverterTexto;
    private javax.swing.JMenuItem mnuDesmarcarRelacao;
    private javax.swing.JMenuItem mnuDesmarcarTermo;
    private javax.swing.JMenuItem mnuDesmarcarTermo2;
    private javax.swing.JMenuItem mnuDetectarRelacoesPacote;
    private javax.swing.JMenuItem mnuDetectarTermosRelacoesPacote;
    private javax.swing.JMenu mnuEditar;
    private javax.swing.JMenu mnuEstatisticas;
    private javax.swing.JMenu mnuExportar;
    private javax.swing.JMenuItem mnuExportarRelacoes;
    private javax.swing.JMenu mnuImportar;
    private javax.swing.JMenuItem mnuInverterRelacao;
    private javax.swing.JMenuItem mnuLocalizar;
    private javax.swing.JMenuItem mnuMarcarSentencaAnotada;
    private javax.swing.JMenuItem mnuMarcarSentencaIgnorada;
    private javax.swing.JMenuItem mnuMarcarTermo;
    private javax.swing.JMenuItem mnuQuantidadeRelacoesConjuntoPacotes;
    private javax.swing.JMenuItem mnuQuantidadeRelacoesNestePacote;
    private javax.swing.JMenuItem mnuRemoverTermosNaoUtilizados;
    private javax.swing.JMenuItem mnuSair;
    private javax.swing.JMenuItem mnuSalvar;
    private javax.swing.JMenuItem mnuSalvarComo;
    private javax.swing.JMenuItem mnuSelecionarClassificador;
    private javax.swing.JMenuItem mnuSentencaAnterior;
    private javax.swing.JMenuItem mnuSentencaProxima;
    private javax.swing.JMenuItem mnuSobre;
    private javax.swing.JMenuItem mnuTransformar;
    private javax.swing.JMenuItem mnuTreinarClassificador;
    private javax.swing.JPanel panBarraInformacoes;
    private javax.swing.JPanel panBarraInformacoesClassificador;
    private javax.swing.JPanel panBarraStatus;
    private javax.swing.JPanel panEsquerda;
    private javax.swing.JPanel panRelacao;
    private javax.swing.JPanel panRelacoes;
    private javax.swing.JPanel panSentenca;
    private javax.swing.JPanel panTermos;
    private javax.swing.JPanel panTermosRelacoes;
    private javax.swing.JPanel panTopo;
    private javax.swing.JPopupMenu popupLstRelacoes;
    private javax.swing.JPopupMenu popupLstTermos;
    private javax.swing.JPopupMenu popupTxtSentenca;
    private javax.swing.JTextField txtComentarios;
    private javax.swing.JTextPane txtSentenca;
    // End of variables declaration//GEN-END:variables

    /**
     * Seta o anotador padrao que será utilizado nesta sessão de anotação
     * 
     * @param anotador Nome do anotador que está trabalhando atualmente
     */
    public void setAnotadorPadrao ( String anotador ) {
        this.anotadorAtual = anotador;
    }

    /**
     * Procura por uma expressão nas sentenças do pacote de sentenças aberto
     * atualmente.
     * 
     * @param expressao A sequência de caracteres a ser procurada
     * @param direcao A direção de busca. Se for 1 busca nas sentenças
     *                seguintes, se for -1 nas sentenças anteriores
     * @param sensivelCaixa Se for verdadeiro, a busca é sensível à caixa
     */
    public void localizar( String expressao, int direcao, boolean sensivelCaixa ) {
        
        // Começamos a busca a partir da sentença selecionada atualmente
        int sentencaSelecionada = lstListaSentencas.getSelectedIndex();
        
        if ( !sensivelCaixa )
            expressao = expressao.toLowerCase();

        // Procura nas sentenças seguintes
        if ( direcao == 1 ) {
            for ( int i = sentencaSelecionada + 1; i < listaSentencas.size(); ++i ) {
                Sentenca s = listaSentencas.get( i );
                String texto = s.getTexto().replace( "=", " " );
                if ( !sensivelCaixa )
                    texto = texto.toLowerCase();
                if ( texto.contains( expressao ) ) {
                    lstListaSentencas.setSelectedIndex( i );
                    lstListaSentencas.ensureIndexIsVisible( i );
                    return;
                }
            }
            // Continua da primeira sentença se nada foi encontrado
            for ( int i = 0; i < sentencaSelecionada; ++i ) {
                Sentenca s = listaSentencas.get( i );
                String texto = s.getTexto().replace( "=", " " );
                if ( !sensivelCaixa )
                    texto = texto.toLowerCase();
                if ( texto.contains( expressao ) ) {
                    lstListaSentencas.setSelectedIndex( i );
                    lstListaSentencas.ensureIndexIsVisible( i );
                    return;
                }
            }
        }

        // Procura nas sentenças anteriores
        else {
            for ( int i = sentencaSelecionada - 1; i >= 0; --i ) {
                Sentenca s = listaSentencas.get( i );
                String texto = s.getTexto().replace( "=", " " );
                if ( !sensivelCaixa )
                    texto = texto.toLowerCase();
                if ( texto.contains( expressao ) ) {
                    lstListaSentencas.setSelectedIndex( i );
                    lstListaSentencas.ensureIndexIsVisible( i );
                    return;
                }
            }
            // Continua a busca da última sentença
            for ( int i = listaSentencas.size() - 1; i > sentencaSelecionada; --i ) {
                Sentenca s = listaSentencas.get( i );
                String texto = s.getTexto().replace( "=", " " );
                if ( !sensivelCaixa )
                    texto = texto.toLowerCase();
                if ( texto.contains( expressao ) ) {
                    lstListaSentencas.setSelectedIndex( i );
                    lstListaSentencas.ensureIndexIsVisible( i );
                    return;
                }
            }
        }
        
        // Se a expressão não foi encontrada com uma busca por todas as
        // sentenças do pacote, mostra um aviso
        JOptionPane.showMessageDialog( null,
                "A expressão de busca nao foi encontrada",
                "Expressão de busca nao encontrada",
                JOptionPane.INFORMATION_MESSAGE );

    }
    
    public void atualizarAnotada() {
        chkAnotada.setSelected( sentencaAtual.isAnotada() );
    }

    public void atualizarIgnorada() {
        chkIgnorada.setSelected( sentencaAtual.isIgnorada() );
    }
    
    public void marcarSentencaAnotada() {
        sentencaAtual.setAnotada( true );
        sentencaAtual.setIgnorada( false );
        atualizarAnotada();
        atualizarIgnorada();
        atualizarBarraInformacoes();
        selecionarSentencaProxima();
        modificado = true;
    }
    
    public void marcarSentencaIgnorada() {
        sentencaAtual.setIgnorada( true );
        sentencaAtual.setAnotada( false );
        atualizarAnotada();
        atualizarIgnorada();
        atualizarBarraInformacoes();
        selecionarSentencaProxima();
        modificado = true;
    }

    /**
     * Método responsável por atualizar o texto da sentença, colocando os termos
     * em destaque.
     */
    public void atualizarTexto() {
        StyledDocument doc = txtSentenca.getStyledDocument();
        
        Style destaque = doc.addStyle( "destaque", null );
        Style normal = doc.addStyle( "normal", null );
        
        StyleConstants.setBold( destaque, true );
        StyleConstants.setForeground( destaque, Color.BLUE );

        for ( int i = 0; i < sentencaAtual.getTokens().size(); ++i ) {
            if ( sentencaAtual.tokenEmTermo( i ) )
                doc.setCharacterAttributes( sentencaAtual.ondeTokenComeca(i),
                    sentencaAtual.getTokens().get(i).getToken().length(),
                    destaque,
                    true );
            else
                doc.setCharacterAttributes( sentencaAtual.ondeTokenComeca(i),
                    sentencaAtual.getTokens().get(i).getToken().length(),
                    normal,
                    true );
        }
        
    }

    public void atualizarBarraInformacoes() {
        int sentencasAnotadas = 0, sentencasIgnoradas = 0;
        int total = listaSentencas.size();
        String informacoes;
        for ( Sentenca s : listaSentencas ) {
            if ( s.isAnotada() )
                sentencasAnotadas++;
            else if ( s.isIgnorada() )
                sentencasIgnoradas++;
        }
        informacoes = "Sentenças anotadas: " + sentencasAnotadas +
                " (" + String.format( "%.2f", (double)sentencasAnotadas / (double)total * 100 )  +  "%) - ";
        informacoes += "Ignoradas: " + sentencasIgnoradas +
                " (" + String.format( "%.2f", (double)sentencasIgnoradas / (double)total * 100 ) + "%) - ";
        informacoes += "Restantes: " +
                ( total - sentencasAnotadas - sentencasIgnoradas ) + " - ";
        informacoes += "Total: " + total;
        lblInformacoes.setText( informacoes );
    }

    /**
     * Detecta todas as relações das sentenças não anotadas e não ignoradas
     * deste pacote.
     */
    private void detectarRelacoesPacote() {
        if ( classificador == null ) {
            JOptionPane.showMessageDialog( null,
                    "Nenhum classificador está carregado no momento",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }
        this.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        for ( Sentenca s : listaSentencas )
            if ( !s.isAnotada() && !s.isIgnorada() )
                detectarRelacoesSentenca( s );
        this.setCursor( null );
    }

    /**
     * Detecta automaticamente as relações existentes na sentença passada como
     * parametro, de acordo com o classificador selecionado atualmente.
     * 
     * @param sentenca Sentença cujas relações serão preditas
     */
    private void detectarRelacoesSentenca( final Sentenca sentenca ) {
        
        if ( classificador == null ) {
            JOptionPane.showMessageDialog( null,
                    "Nenhum classificador está carregado no momento",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        if ( classificador.getNome().contains( "SVM" ) ) {
            if ( !new File( caminhoSvmLightClassify ).exists() ) {
                JOptionPane.showMessageDialog( null,
                        "O caminho do programa svm_classify não está coreto! Verifique as configurações no menu \"Aprendizado de máquina\" -> \"Configurações\".",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        /**
         * Concorrência no Swing é meio chato
         * http://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html
         */
        final Processando processando = new Processando( this, true );
        SwingWorker sw = new SwingWorker<Integer, Integer>() {
            @Override
            public Integer doInBackground() {
                setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                sentenca.detectarRelacoes( classificador );
                return 0;
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

        atualizarListaRelacoes();
        modificado = true;
        
    }
    
    /**
     * Detecta automaticamente os termos existentes na sentença passada como
     * parâmetro.
     * 
     * @param sentenca Sentença que terá os termos identificados automaticamente
     */
    private void detectarTermosSentenca( Sentenca sentenca ) {
        sentenca.detectarTermos();
        atualizarTexto();
        atualizarListaTermos();
        atualizarListaRelacoes();
        modificado = true;
    }

    private void removerTermosNaoUtilizados() {
        this.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        for ( Sentenca sentenca : listaSentencas )
            sentenca.removerTermosNaoUtilizados();
        this.setCursor( null );
        atualizarTexto();
        atualizarListaTermos();
        modificado = true;
    }
    
    /**
     * Renderizador para as celulas da lista de sentenças
     */ 
    public class MeuListCellRenderer extends JLabel implements ListCellRenderer {
        public MeuListCellRenderer() {
            setOpaque( true );
        }
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Sentenca s = (Sentenca)value;
            int indice = listaSentencas.indexOf( value ) + 1;
            setText( indice + " - " + s.toString() );
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
        public final Color VERDE = new Color( 102, 198, 83 );
        public final Color AZUL = new Color( 57, 105, 138 );
        public final Color CINZA = new Color( 162, 162, 162 );
    }

    /**
     * Exporta uma pagina HTML contendo todas as sentenças do pacote atualmente
     * aberto. Este método atualmente não é chamado em nenhum lugar.
     */
    public void exportarHTML() {

        JFileChooser fc = new JFileChooser( ultimoDiretorio );
        int value = fc.showSaveDialog( this );
        File arquivo;
        if ( value == JFileChooser.APPROVE_OPTION )
            arquivo = fc.getSelectedFile();
        else
            return;
        
        BufferedWriter bw;
        
        try {
            
            bw = new BufferedWriter( new FileWriter( arquivo ) );
            
            String html;
//            bw.write( 
            html = "<!DOCTYPE HTML>\n";
            html += "<html>\n";
            html += "<head>\n";
            html += "<meta charset=\"utf-8\">\n";
            html += "<title>Teste</title>\n";

            html += "<style type=\"text/css\">\n";
            html += "html, body {\n";
            html += "font-size: 20pt;\n";
            html += "}\n";

            html += ".sentenca {\n";
            html += "border-top: 1px solid #333;\n";
            html += "}\n";

            html += ".tok-info {\n";
            html += "position:absolute;\n";
            html += "border: 1px solid #000;\n";
            html += "padding: 10px;\n";
            html += "background: #DDD;\n";
            html += "visibility: hidden;\n";
            html += "}\n";
            html += ".tok-info span {\n";
            html += "font-weight: bold;\n";
            html += "}\n";

            html += ".relacoes span {\n";
            html += "visibility: hidden;\n";
            html += "}\n";
            html += "</style>\n";

            html += "<script type=\"text/javascript\" src=\"jquery-1.7.2.min.js\"></script>\n";
            html += "<script type=\"text/javascript\">\n";
            html += "$(document).ready( function() {\n";

            html += "// Acopla os quadros de informação de tokens a cada token\n";
            html += "$(\"span[id^=t]\").each( function( i ) {\n";
            html += "var iddiv = \"#\" + $(this).attr( \"id\" ) + \"-info\";\n";
            html += "$(this).mousemove( function( e ) {\n";
            html += "$(this).css( \"background-color\", \"yellow\" );\n";
            html += "$(iddiv).css( {\n";
            html += "\"visibility\" : \"visible\",\n";
            html += "\"left\" : e.pageX + 20,\n";
            html += "\"top\": e.pageY + 20\n";
            html += "} );\n";
            html += "} );\n";
            html += "$(this).mouseout( function() {\n";
            html += "$(this).css( \"background\", \"none\" );\n";
            html += "$(iddiv).css( \"visibility\", \"hidden\" );\n";
            html += "} );\n";
            html += "} );\n";

            html += "var regexidsentence = /^r(\\d+)/;\n";

            html += "// Acopla as marcações de relações com os termos nas sentenças\n";
            html += "$(\"li[id^=r]\").each( function( i ) {\n";
            html += "var idrelation = $(this).attr( \"id\" );\n";
            html += "var idsentence = regexidsentence.exec( idrelation )[1];\n";
            html += "var termpositions = $( \"#\" + idrelation + \" span\" ).text().split( /,/ );\n";
            html += "var idtokenpartial = \"#tok\" + idsentence + \"-\";\n";

            html += "$(this).hover( function() {\n";
            html += "$(this).css( \"background-color\", \"yellow\" );\n";
            html += "for ( i = termpositions[0]; i <= termpositions[1]; ++i ) {\n";
            html += "var idtoken = idtokenpartial + i;\n";
            html += "$(idtoken).css( \"background-color\", \"#66F\" );\n";
            html += "}\n";
            html += "for ( i = termpositions[2]; i <= termpositions[3]; ++i ) {\n";
            html += "var idtoken = idtokenpartial + i;\n";
            html += "$(idtoken).css( \"background-color\", \"#66F\" );\n";
            html += "}\n";
            html += "},\n";
            html += "function() {\n";
            html += "$(this).css( \"background\", \"none\" );\n";
            html += "for ( i = termpositions[0]; i <= termpositions[1]; ++i ) {\n";
            html += "var idtoken = idtokenpartial + i;\n";
            html += "$(idtoken).css( \"background\", \"none\" );\n";
            html += "}\n";
            html += "for ( i = termpositions[2]; i <= termpositions[3]; ++i ) {\n";
            html += "var idtoken = idtokenpartial + i;\n";
            html += "$(idtoken).css( \"background\", \"none\" );\n";
            html += "}\n";
            html += "} );\n";
            html += "} );\n";

            html += "} );\n";
            html += "</script>\n";

            html += "<body>\n";
            
            bw.write( html );
            
            int[] numRelacoes = new int[7];
            
            for ( Sentenca s : listaSentencas ) {
                
                bw.write( s.toHtml() );
                bw.newLine();
                
                for ( Relacao r : s.getRelacoes() ) {
                    if ( r.getRelacao().equals( "is-a" ) )
                        ++numRelacoes[0];
                    else if ( r.getRelacao().equals( "part-of" ) )
                        ++numRelacoes[1];
                    else if ( r.getRelacao().equals( "made-of" ) )
                        ++numRelacoes[2];
                    else if ( r.getRelacao().equals( "effect-of" ) )
                        ++numRelacoes[3];
                    else if ( r.getRelacao().equals( "used-for" ) )
                        ++numRelacoes[4];
                    else if ( r.getRelacao().equals( "property-of" ) )
                        ++numRelacoes[5];
                    else if ( r.getRelacao().equals( "location-of" ) )
                        ++numRelacoes[6];
                }
 
            }

            for ( int i = 0; i < 7; ++i ) {
                System.out.println( numRelacoes[i] );
            }
            
            html = "</body>\n";
            html += "</html>\n";
            bw.write( html );
            bw.close();

        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }

    }

    public void setClassificador ( Classificador c ) {
        classificador = c;
    }
    
    /**
     * Aciona ou desaciona alguns elementos da GUI.
     * 
     * @param estado Se os elementos devem ser acionados ou desacionados
     */
    public void acionarElementos( boolean estado ) {
        mnuSalvar.setEnabled( estado );
        mnuSalvarComo.setEnabled( estado );
        mnuExportar.setEnabled( estado );
        
        mnuMarcarSentencaAnotada.setEnabled( estado );
        mnuMarcarSentencaIgnorada.setEnabled( estado );
        mnuSentencaAnterior.setEnabled( estado );
        mnuSentencaProxima.setEnabled( estado );
        mnuLocalizar.setEnabled( estado );
        
        menuDetectarTermosPacote.setEnabled( estado );
        mnuDetectarRelacoesPacote.setEnabled( estado );
        mnuDetectarTermosRelacoesPacote.setEnabled( estado );
        mnuRemoverTermosNaoUtilizados.setEnabled( estado );
        
        mnuQuantidadeRelacoesNestePacote.setEnabled( estado );
        
        btnSentencaAnterior.setEnabled( estado );
        btnSentencaProxima.setEnabled( estado );
        
        txtComentarios.setEditable( estado );
        chkAnotada.setEnabled( estado );
        chkIgnorada.setEnabled( estado );
        btnMarcarSentencaAnotada.setEnabled( estado );
        btnIgnorarSentenca.setEnabled( estado );
        btnMarcarSelecaoComoTermo.setEnabled( estado );
        
        btnDetectarTermos.setEnabled( estado );
        btnDesmarcarTermos.setEnabled( estado );

        cmbRelacoes.setEnabled( estado );
        btnMarcarRelacao.setEnabled( estado );
        btnMarcarRelacaoInvertida.setEnabled( estado );
        btnDetectarRelacoes.setEnabled( estado );
        btnInverterRelacoes.setEnabled( estado );
        btnDesmarcarRelacoes.setEnabled( estado );
    }
    
    /**
     * Envia os parâmetros de caminhos ao clasificador carregado atualmente.
     */
    public void atualizarClassificador() {
        if ( classificador != null ) {
            classificador.adicionarParametro( "caminho.svmlight.learn", caminhoSvmLightLearn );
            classificador.adicionarParametro( "caminho.svmlight.classify", caminhoSvmLightClassify );
        }
    }

    /**
     * Salva as propriedades da aplicação no arquivo ars.properties.
     */
    public void salvarPropriedades() {
        try {
            properties.setProperty( "ultimo.diretorio", ultimoDiretorio );
            properties.setProperty( "caminho.svmlight.learn", caminhoSvmLightLearn );
            properties.setProperty( "caminho.svmlight.classify", caminhoSvmLightClassify );
            for ( int i = 0; i < 5; ++i )
                if ( i < arquivosRecentes.size() )
                    properties.setProperty( "arquivo.recente." + (i + 1), arquivosRecentes.get(i).getAbsolutePath() );
                else
                    properties.setProperty( "arquivo.recente." + (i + 1), "" );
            properties.store( new FileOutputStream( "ars.properties" ), null );
        }
        catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Atualiza o menu de arquivos abertos recentemente.
     */
    public void atualizarMenuArquivosRecentes() {
        
        if ( arquivosRecentes.isEmpty() ) {
            mnuAbrirArquivoRecente.setEnabled( false );
            return;
        }
        
        mnuAbrirArquivoRecente.setEnabled( true );
        mnuAbrirArquivoRecente.removeAll();
        
        for ( final File arquivo : arquivosRecentes ) {

            if ( !arquivo.exists() )
                continue;
            
            JMenuItem menuitem = new JMenuItem( arquivo.getAbsolutePath() );
            menuitem.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent e ) {
                    arquivoAtual = arquivo;
                    abrirArquivo( arquivo );
                }
            } );
            mnuAbrirArquivoRecente.add( menuitem );
            
        }

    }

    /**
     * Abre um arquivo para anotação. O método verifica se é um arquivo de
     * sentenças válido. Caso não seja, não faz nada.
     * 
     * @param arquivo Arquivo a ser aberto
     */
    private void abrirArquivo( File arquivo ) {

        if ( modificado ) {
            int opcao;
            opcao = JOptionPane.showConfirmDialog(
                    null,
                    "Existem modificações não salvas neste arquivo! Quer salvá-las antes de continuar?",
                    "Modificações não salvas",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if ( opcao == JOptionPane.CANCEL_OPTION )
                return;
            else if ( opcao == JOptionPane.YES_OPTION )
                salvarArquivo( arquivoAtual );
        }

        // Verifica se é um arquivo válido
        try {
            UtilidadesPacotes.lerSentencasPacote( arquivo );
        }
        catch ( NullPointerException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
            javax.swing.JOptionPane.showMessageDialog( null,
            "O arquivo selecionado não é um arquivo JSON válido",
            "Erro",
            javax.swing.JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        adicionarArquivoListaAbertosRecentemente( arquivo );
        
        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Abrindo arquivo {0}", arquivo.getAbsolutePath() );
        
        // Inicializa a lista de sentenças
        cabecalho = UtilidadesPacotes.lerCabecalhoPacote( arquivo );
        listaSentencas = UtilidadesPacotes.lerSentencasPacote( arquivo );

        acionarElementos( true );

        atualizarListaSentencas();
        atualizarBarraStatus();
        atualizarBarraInformacoes();

        // selecionamos a primeira sentença que não está anotada
        int sentencaSelecionada = 0;
        for ( Sentenca s : listaSentencas ) {
            if ( !s.isAnotada() && !s.isIgnorada() )
                break;
            ++sentencaSelecionada;
        }
        if ( sentencaSelecionada == listaSentencas.size() )
            sentencaSelecionada = listaSentencas.size() - 1;
        lstListaSentencas.setSelectedIndex( sentencaSelecionada );
        lstListaSentencas.ensureIndexIsVisible( sentencaSelecionada );
        txtSentenca.requestFocusInWindow();
        
        modificado = false;
        
    }
 
    /**
     * Adiciona um arquivo à lista de arquivos abertos recentemente.
     * 
     * @param arquivo Arquivo a ser adicionado à lista
     */
    public void adicionarArquivoListaAbertosRecentemente( File arquivo ) {
        if ( arquivosRecentes.contains( arquivo ) )
            arquivosRecentes.remove( arquivo );
        arquivosRecentes.add( 0, arquivo );
        while ( arquivosRecentes.size() > 5 )
            arquivosRecentes.remove( 5 );
        atualizarMenuArquivosRecentes();
        salvarPropriedades();
    }

    /**
     * Mostra o dialogo de salvar um arquivo. Faz a verificação se o arquivo
     * escolhido já existe.
     * 
     * @param titulo Título da caixa de diálogo. Se for null, aparecerá como o
     *               padrão do SO.
     * @param sugestao Arquivo que aparecerá como sugestão de nome no diálogo.
     * 
     * @return Arquivo que foi escolhido para salvar ou null caso o usuário
     *         tenha cancelado a operação.
     */
    public File mostrarDialogoSalvarArquivo( String titulo, File sugestao ) {
        JFileChooser fc = new JFileChooser( ultimoDiretorio );
        if ( titulo != null )
            fc.setDialogTitle( titulo );
        if ( sugestao != null )
            fc.setSelectedFile( sugestao );
        File arquivoNovo;
        while ( true ) {
            int result1 = fc.showSaveDialog( null );
            if ( result1 == JFileChooser.APPROVE_OPTION )
                arquivoNovo = fc.getSelectedFile();
            else
                return null;
            int result2;
            if ( arquivoNovo.exists() ) {
                result2 = JOptionPane.showConfirmDialog(
                        null,
                        arquivoNovo.getName() + " já existe.\n Deseja substituí-lo?",
                        "Arquivo já existe",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
            }
            else
                return arquivoNovo;
            if ( result2 == JOptionPane.YES_OPTION )
                return arquivoNovo;
            if ( result2 == JOptionPane.CANCEL_OPTION )
                return null;
            fc.setSelectedFile( arquivoNovo );
        }
    }

    /**
     * Mostra o diálogo de abrir um arquivo.
     * 
     * @param titulo Título da caixa de diálogo. Se for null, aparecerá como o
     *               padrão do SO.
     * 
     * @return Arquivo que foi escolhido para abrir ou null caso o usuário tenha
     *         cancelado a operação.
     */
    public File mostrarDialogoAbrirArquivo( String titulo ) {
        JFileChooser fc = new JFileChooser( ultimoDiretorio );
        int value = fc.showOpenDialog( this );
        if ( value == JFileChooser.APPROVE_OPTION )
            return fc.getSelectedFile();
        else
            return null;        
    }
    
}

