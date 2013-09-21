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

package aprendizadodemaquina.classificadores;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import aprendizadodemaquina.Classificador;
import aprendizadodemaquina.Featurizador;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * Classe que representa um classificador de árvore de decisão.
 */
public class ClassificadorDT extends Classificador implements Serializable {

    private String nome;                            // Identificação do classificador usado
    private Featurizador featurizador;              // Featurizador que gera os vetores de features
    private Instances dadosTreinamento;             // Os dados utilizados no treinamento deste classificador (necessário na classificação)
    private FilteredClassifier classificador;       // O classificador em si

    /**
     * Carrega um modelo de classificador já treinado
     * 
     * @param arquivoModelo Arquivo que contém o classificador treinado
     */
    public ClassificadorDT( File arquivoModelo ) throws IllegalArgumentException {
        nome = "Árvore de decisão J48 (WEKA)";
        try {
            ZipInputStream zis = new ZipInputStream( new FileInputStream( arquivoModelo ) );
            ZipEntry ze;
            byte[] buffer = new byte[4096];
            
            while ( ( ze = zis.getNextEntry() ) != null ) {
                
                FileOutputStream fos = new FileOutputStream( ze.getName() );
                int numBytes;
                while ( ( numBytes = zis.read( buffer, 0, buffer.length ) ) != -1 ) {
                    fos.write( buffer, 0, numBytes );
                }
                fos.close();
                zis.closeEntry();

                if ( ze.getName().equals( "modelo.obj" ) ) {
                    ObjectInputStream ois;
                    ois = new ObjectInputStream( new FileInputStream( ze.getName() ) );
                    classificador = (FilteredClassifier)ois.readObject();
                    featurizador = (Featurizador)ois.readObject();
                    dadosTreinamento = (Instances)ois.readObject();
                    ois.close();
                }

                new File( ze.getName() ).delete();
                
            }
            
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        // Este provavelmente é causado por um arquivo inválido
        catch (NullPointerException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException();
        }
        
    }

    public ClassificadorDT( String nome ) {
        this.nome = nome;
        this.featurizador = new Featurizador( false, true, true, true );
    }
    
    @Override
    public String getNome() {
        return nome;
    }
    
    /**
     * Classifica uma instância de relação passada como a sentença e os seus
     * dois termos de interesse. Internamente, o objeto featurizador cria um
     * objeto Instance a partir da sentença e dos termos, e entao é chamado o
     * metodo classificar(Instance i).
     * 
     * @param s Sentença que contem os termos de interesse
     * @param t1 Primeiro termo da relação
     * @param t2 Segundo termo da relação
     * @return Classe predita pelo classificador para a instância fornecida
     */
    @Override
    public String classificar( Sentenca s, Termo t1, Termo t2 ) {
        return classificar( featurizador.paraInstancia( s, null, t1, t2, "predicao" ) );
    }
    
    /**
     * Retorna a classificação de uma instância de relação.
     * 
     * @param instance Instância de relação criada pelo objeto featurizador
     * @return Classe predita pelo classificador para a instância fornecida
     */
    public String classificar( Instance instance ) {
        //System.out.println( "DEBUG: Classificando instância " + instance );
        instance.setDataset( dadosTreinamento );
        try {
            double pred = classificador.classifyInstance( instance );
            return dadosTreinamento.classAttribute().value( (int)pred );
        }
        catch ( Exception ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        return "none";
    }

    @Override
    public int treinar( File arquivoTreinamento, String parametros ) {

        Logger.getLogger( "ARS logger" ).info( "Treinamento de classificador J48" );
        // TODO: Verificar por que os parâmetros dos classificadores não estão funcionando
        
        try {

            ConverterUtils.DataSource fonte = new ConverterUtils.DataSource( arquivoTreinamento.getAbsolutePath() );
            dadosTreinamento = fonte.getDataSet();

            // Identifica o atributo que será a classe de interesse (último atributo = qual relação é)
            dadosTreinamento.setClassIndex( dadosTreinamento.numAttributes() - 1 );

            // Cria um classificador que aceita filtros
            classificador = new FilteredClassifier();
            classificador.setClassifier( new J48() );
            
            // Seleciona os atributos que não são parte da classificacao
            // TODO: SE OS DADOS DE TREINAMENTO TIVEREM A FEATURE DE IDENTIFICAÇÃO...
/*
                Remove remove = new Remove();
                int[] atributosRemovidos = new int[4];
                atributosRemovidos[0] = 0;   // ID da sentença
                atributosRemovidos[1] = 1;   // termo1
                atributosRemovidos[2] = 2;   // termo2
                remove.setAttributeIndicesArray( atributosRemovidos );
                // Acopla o filtro de remoção ao classificador
                fc.setFilter( remove );
*/
            // FIM-SE

//                String []param = weka.core.Utils.splitOptions(parametros[cmbTipoClassificador1.getSelectedIndex()]);
//                fc.setOptions(param);

            // Faz o treinamento do classificador
            classificador.buildClassifier( dadosTreinamento );

            return 0;
            
        }
        catch ( Exception ex ) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
            if ( ex instanceof IllegalArgumentException ) {
                javax.swing.JOptionPane.showMessageDialog( null,
                "O arquivo de treinamento fornecido não é um arquivo ARFF válido",
                "Erro",
                javax.swing.JOptionPane.ERROR_MESSAGE );
            }
            return 1;
        }
        
    }

    /**
     * Compacta o classificador e o armazena no disco
     *
     * @param arquivoSaida 
     */
    @Override
    public void gravar( File arquivoSaida ) {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream( new FileOutputStream( "modelo.obj" ) );
            oos.writeObject( classificador );
            oos.writeObject( featurizador );
            oos.writeObject( dadosTreinamento );
            oos.flush();
            oos.close();
            
            BufferedWriter bw = new BufferedWriter( new FileWriter( new File( "meta.ars") ) );
            bw.write( "classificador:j48" );
            bw.close();
            
            byte[] buffer = new byte[4096];
            ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( arquivoSaida.getAbsolutePath() ) ) ); 
            zos.putNextEntry( new ZipEntry( "modelo.obj" ) );
            FileInputStream fis = new FileInputStream( "modelo.obj" );
            int len;
            while ( ( len = fis.read(buffer) ) > 0 )
                zos.write( buffer, 0, len );
            fis.close();
            zos.closeEntry();
            
            zos.putNextEntry( new ZipEntry( "meta.ars" ) );
            fis = new FileInputStream(  "meta.ars" );
            while ( ( len = fis.read(buffer) ) > 0 )
                zos.write(  buffer, 0, len );
            fis.close();
            zos.closeEntry();
            zos.close();
            
            new File( "meta.ars" ).delete();
            new File( "modelo.obj" ).delete();
            
            Logger.getLogger( "ARS logger" ).log( Level.INFO, "Classificador J48 salvo no arquivo \"{0}\"", arquivoSaida.getAbsolutePath() );
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void adicionarParametro( String parametro, String valor ) {
        // Nothing to do here
    }

    @Override
    public void finalizar() {
        // Nothing to do here either
    }
    
    public String toString() {
        return "" + classificador;
    }
    
}
