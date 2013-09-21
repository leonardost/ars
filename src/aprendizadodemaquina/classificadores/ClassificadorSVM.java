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
import anotadorderelacoes.model.UtilidadesPacotes;
import aprendizadodemaquina.Classificador;
import aprendizadodemaquina.Featurizador;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Representa um classificador SVM. Cuida da integração com o SVM Light e da
 * classificação one-vs-all com os 8 classificadores.
 */
public class ClassificadorSVM extends Classificador {
    
    private String nome;
    private Featurizador featurizador;              // Featurizador que gera os vetores de features
    private File arquivoModelosCompactados;         // Arquivo que guarda os 8 modelos compactados
    private File[] classificadores;                 // Guarda o caminho dos 8 classificadores
    
    // Parâmetros de normalização dos atributos
    private List<Double> maximos;                   
    private List<Double> minimos;
    
    // Parâmetros que vêm da aplicação
    private String caminhoSvmLearn;
    private String caminhoSvmClassify;
    
    // Para gerar números aleatórios na classificação
    private static Random rng = new Random();

    public static String[] classes = { "", "is-a", "property-of", "part-of", "made-of", "effect-of", "used-for", "locaiton-of", "none" };
    
    /**
     * Carrega um conjunto de classificadores já treinados, contidos no arquivo
     * passado como parâmetro
     * 
     * @param arquivoModelo Arquivo que contém os 8 classificadores treinados
     */
    public ClassificadorSVM( File arquivoModelo ) throws IllegalArgumentException {
        
        this.nome = "SVM (SVM Light)";
        this.featurizador = null;
        this.arquivoModelosCompactados = arquivoModelo;
        this.classificadores = new File[9];
        
        // Descompacta os 8 classificadores, o objeto featurizador e o arquivo meta.ars
        try {
            ZipInputStream zis = new ZipInputStream( new FileInputStream( arquivoModelosCompactados ) );
            ZipEntry ze;
            byte[] buffer = new byte[4096];

            // Para cada arquivo compactado
            while ( ( ze = zis.getNextEntry() ) != null )  {
                
                // Escrevemos o arquivo descompactado no disco
                FileOutputStream fos = new FileOutputStream( ze.getName() );
                int numBytes;
                while ( ( numBytes = zis.read( buffer, 0, buffer.length ) ) != -1 ) {
                    fos.write( buffer, 0, numBytes );
                }
                fos.close();
                zis.closeEntry();
                
                // Se for o featurizador_minimos_maximos.obj lemos os objetos e
                // o deletamos
                if ( ze.getName().equals( "featurizador_minimos_maximos.obj" ) ) {
                    ObjectInputStream ois;
                    ois = new ObjectInputStream( new FileInputStream( ze.getName() ) );
                    featurizador = (Featurizador)ois.readObject();
                    minimos = (List<Double>)ois.readObject();
                    maximos = (List<Double>)ois.readObject();
                    ois.close();
                    new File( ze.getName() ).delete();
                }
                
                // Se for o meta.ars apenas deletamos
                else if ( ze.getName().equals( "meta.ars" ) )
                    new File( "meta.ars" ).delete();
                
                // Agora são os arquivos de modelos
                else {
                    int numero = Integer.parseInt( "" + ze.getName().charAt( ze.getName().length() - 1 ) );
                    classificadores[numero] = new File( ze.getName() );
                }
            }
            zis.close();
        }
        catch ( ClassNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        // Provavelmente um arquivo inválido
        catch ( NullPointerException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
            throw new IllegalArgumentException();
        }
    }
    
    public ClassificadorSVM( String nome ) {
        this.nome = nome;
        featurizador = new Featurizador( false, true, true, true );
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String classificar( Sentenca s, Termo t1, Termo t2 ) {
        
        Logger.getLogger( "ARS logger" ).fine( "Classificação usando classificador SVM" );
        
        /**
         * Algoritmo:
         * ----------
         * 1. Descompactar os 8 classificadores
         * 2. Transforma os objetos Sentenca e Termos em uma instância
         * 3. Salvar instância em um arquivo
         * 4. Rodar os 8 classificadores sobre esse arquivo
         * 5. Verificar qual tem maior confiança
         */

        // 1. Descompactar os 8 classificadores
        // ====================================
        // Feito no construtor

        // 2. Transforma os objetos Sentenca e Termos em uma instância
        // ===========================================================
        List<Double> instancia = featurizador.paraSvm( s, null, t1, t2, "predicao" );

        // 3. Normaliza instância e Salva em um arquivo
        // ============================================
        
        // Número adicionado ao fim do nome do arquivo para minimizar as chances
        // de dar um erro de acesso de arquivo negado. Não deveria precisar
        // diso porque a classificação é sequencial e não paralela, mas acho que
        // às vezes o SO não solta o arquivo a tempo da próxima chamada ao
        // método. Poderia usar um contador sequencial também, basta que sejam
        // números diferentes.
        int salt = rng.nextInt( 1000000 );
        
        try {

            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "instancia.temp" + salt ) ) );
            bw.write( "0" );
            for ( int i = 1; i < instancia.size(); ++i )
                if ( instancia.get(i) != 0.0 ) {
                    if ( maximos.get(i) != 0.0 )
                        instancia.set(i, instancia.get(i) / maximos.get(i) );
                    bw.write( " " + i + ":" + instancia.get(i).toString() );
                }
            bw.write( "\n" );
            bw.close();
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex );
        }

        // 4. Rodar os 8 classificadores sobre esse arquivo
        // ================================================
        Double[] resultados = new Double[9];
        try {
            for ( int i = 1; i <= 8; ++i ) {
                
                List<String> comando = new ArrayList<String>();
                
                comando.add( caminhoSvmClassify );
                comando.add( "instancia.temp" + salt );
                comando.add( classificadores[i].getAbsolutePath() );
                comando.add( "saida.temp." + salt + "." + i );
                
                Logger.getLogger( "ARS logger" ).finer( "Linha sendo executada ");
                String linhaDebug = "";
                for ( String c : comando )
                    linhaDebug += c + " ";
                Logger.getLogger( "ARS logger" ).finer( linhaDebug );
                
                ProcessBuilder pb = new ProcessBuilder( comando );
                Process pr = pb.start();

                String saida;
                BufferedReader br = new BufferedReader( new InputStreamReader( pr.getInputStream() ) );
                while ( ( saida = br.readLine() ) != null )
                    //System.out.println( "STDOUT: " + saida );
                    Logger.getLogger( "ARS logger" ).log( Level.FINER, "STDOUT: {0}", saida );
                br.close();
                
                br = new BufferedReader( new InputStreamReader( pr.getErrorStream() ) );
                while ( ( saida = br.readLine() ) != null )
                    //System.out.println( "STDERR: " + saida );
                    Logger.getLogger( "ARS logger" ).log( Level.FINER, "STDERR: {0}", saida );
                br.close();

                int retorno = pr.waitFor();
                Logger.getLogger( "ARS logger" ).log( Level.FINER, "Valor retornado = {0}", retorno );

                br = new BufferedReader( new InputStreamReader( new FileInputStream( "saida.temp." + salt + "." + i ) ) );
                resultados[i] = Double.parseDouble( br.readLine() );
                br.close();
                
                new File( "saida.temp." + salt + "." + i ).delete();
                
            }
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        
        // 5. Verificar qual tem maior confiança
        // =====================================
        Double maior = -100.0;
        int maiorClasse = -1;
        for ( int i = 1; i <= 8; ++i ) {
            if ( maior < resultados[i] ) {
                maior = resultados[i];
                maiorClasse = i;
            }
        }

        // Deleta arquivos temporários
        new File( "instancia.temp" + salt ).delete();

        return classes[maiorClasse];
        
    }

    @Override
    public int treinar( File arquivoTreinamento, String parametros ) {

        Logger.getLogger( "ARS logger" ).info( "Treinamento de classificador SVM" );

        if ( !verificarSintaxe( arquivoTreinamento ) ) {
            javax.swing.JOptionPane.showMessageDialog( null,
            "O arquivo de treinamento fornecido não é um arquivo com formato válido",
            "Erro",
            javax.swing.JOptionPane.ERROR_MESSAGE );
            return 1;
        }
        
        /**
         * Algoritmo
         * =========
         * 1. Normalizar os atributos do arquivo de treinamento e guardar os
         *    parâmetros de normalização
         * 2. Dividir arquivo de treinamento em 8 (1 para cada classe)
         * 3. Treinar 8 classificadores
         * 4. Combinar os 8 classificadores em 1 arquivo => arquivoSaida
         */

        // 1. Normalizar os atributos do arquivo de treinamento e guardar os parâmetros de normalização
        // ============================================================================================
        BufferedReader br;
        minimos = null;
        maximos = null;
        String linha;
        String linhaSemClasse;

        try {

            // Primeiro temos que achar o número total de atributos que o arquivo contém
            
            br = new BufferedReader( new FileReader( arquivoTreinamento ) );
            int maiorAtributo = 0;
            while ( ( linha = br.readLine() ) != null ) {
                for ( String atributo : linha.substring(2).split( " " ) ) {
                    Integer indice = Integer.parseInt( atributo.split( ":" )[0] );
                    if ( indice > maiorAtributo )
                        maiorAtributo = indice;
                }
            }
            
            minimos = new ArrayList<Double>( Collections.nCopies( maiorAtributo + 1, 0.0 ) );

            // Encontramos os valores mínimos e máximos de cada atributo
            
            br = new BufferedReader( new FileReader( arquivoTreinamento ) );
            linha = br.readLine();
            linhaSemClasse = linha.substring(2);
            for ( String atributo : linhaSemClasse.split( " " ) ) {
                int indice = Integer.parseInt( atributo.split( ":" )[0] );
                Double valor = Double.parseDouble( atributo.split( ":" )[1] );
                minimos.set( indice, valor );
            }
            
            maximos = new ArrayList<Double>( minimos );
            
            while ( ( linha = br.readLine() ) != null ) {
                linhaSemClasse = linha.substring(2);
                for ( String atributo : linhaSemClasse.split( " " ) ) {
                    int indice = Integer.parseInt( atributo.split( ":" )[0] );
                    Double valor = Double.parseDouble( atributo.split( ":" )[1] );
                    if ( minimos.get( indice ) > valor )
                        minimos.set( indice, valor );
                    if ( maximos.get( indice ) < valor )
                        maximos.set( indice, valor );
                }
            }
            
            br.close();

            // Criamos um arquivo de treinamento com os atributos normalizados
            File arquivoTreinamentoNormalizado = new File( "treinamento_normalizado.train" );
            BufferedWriter bw = new BufferedWriter( new FileWriter( arquivoTreinamentoNormalizado ) );
            
            br = new BufferedReader( new FileReader( arquivoTreinamento ) );
            while ( ( linha = br.readLine() ) != null ) {

                bw.write( linha.substring( 0, 1 ) );
                
                linhaSemClasse = linha.substring( 2 );
                
                for ( String atributo : linhaSemClasse.split( " " ) ) {
                    int indice = Integer.parseInt( atributo.split( ":" )[0] );
                    bw.write( " " + indice + ":" );
                    Double valor = Double.parseDouble( atributo.split( ":" )[1] );

                    if ( maximos.get( indice ) != 0.0 )
                        valor = valor / maximos.get( indice );
                    
                    bw.write( UtilidadesPacotes.doubleToString( valor ) );

                }
                
                bw.write( "\n" );
                
            }

            br.close();
            bw.close();

        }
        catch (FileNotFoundException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        
        // 2. Dividir arquivo de treinamento em 8 (1 para cada classe)
        // ===========================================================
        File[] arquivosTreinamentoTemp = new File[9];
        BufferedWriter[] bws = new BufferedWriter[9];
        
        try {
            
            for ( int classe = 1; classe <= 8; ++classe ) {
                arquivosTreinamentoTemp[classe] = new File( "treinamento_classe" + classe );
                bws[classe] = new BufferedWriter( new FileWriter( arquivosTreinamentoTemp[classe] ) );
            }

            // Lê o arquivo de treinamento normalizado
            br = new BufferedReader( new FileReader( "treinamento_normalizado.train" ) );
            while ( ( linha = br.readLine() ) != null ) {
                linhaSemClasse = linha.substring(2);
                Character c = linha.charAt(0);
                for ( int classe = 1; classe <= 8; ++classe ) {
                    if ( Integer.toString( classe ).equals( c.toString() ) )
                        bws[classe].write( "1 " + linhaSemClasse );
                    else
                        bws[classe].write( "-1 " + linhaSemClasse );
                    bws[classe].write("\n");
                }
            }
            
            br.close();
            for ( int i = 1; i <= 8; ++i )
                bws[i].close();

            new File( "treinamento_normalizado.train" ).delete();
            
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        
        // 3. Treinar 8 classificadores
        // ============================
        classificadores = new File[9];
        
        try {
            for ( int i = 1; i <= 8; ++i ) {
                
                classificadores[i] = new File( "modelo" + i );
                classificadores[i].createNewFile();
                
                /**
                 * http://stackoverflow.com/questions/5479461/java-runtime-exec-woes-on-linux
                 */

                List<String> comando = new ArrayList<String>();
                comando.add( caminhoSvmLearn );
                // Estava havendo algum problema com o parsing dos argumentos.
                // Separá-los em partes parece ter ajudado
                for ( String parametro : parametros.split( " " ) )
                    comando.add( parametro );
                comando.add( arquivosTreinamentoTemp[i].getAbsolutePath() );
                comando.add( classificadores[i].getAbsolutePath() );

                Logger.getLogger( "ARS logger" ).fine( "Linha sendo executada ");
                String linhaDebug = "";
                for ( String s : comando )
                    linhaDebug += s + " ";
                Logger.getLogger( "ARS logger" ).fine( linhaDebug );

                ProcessBuilder pb = new ProcessBuilder( comando );
                Process pr = pb.start();
                
                /**
                 * Problema de travar aqui às vezes e porque com a leitura dos
                 * buffers funciona:
                 * http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=2
                 */
    
                String saida;
                br = new BufferedReader( new InputStreamReader( pr.getInputStream() ) );
                while ( ( saida = br.readLine() ) != null )
                    //System.out.println( "STDOUT: " + saida );
                    Logger.getLogger( "ARS logger" ).log( Level.FINER, "STDOUT: {0}", saida );
                br.close();

                br = new BufferedReader( new InputStreamReader( pr.getErrorStream() ) );
                while ( ( saida = br.readLine() ) != null )
                    //System.out.println( "STDERR: " + saida );
                    Logger.getLogger( "ARS logger" ).log( Level.FINER, "STDERR: {0}", saida );
                br.close();
                
                int retorno = pr.waitFor();
                Logger.getLogger( "ARS logger" ).log( Level.FINER, "Valor retornado = {0}", retorno );

                arquivosTreinamentoTemp[i].delete();
                
            }
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }

        return 0;
        
    }

    /**
     * Compacta os 8 classificadores e o objeto featurizador em 1 arquivo.
     * Deve ser chamado somente após o método treinar() ser chamado.
     * 
     * @param arquivoSaida Arquivo onde os classificadores serão armazenados
     */
    @Override
    public void gravar( File arquivoSaida ) {

        try {
            
            // Serializa objeto featurizador
            ObjectOutputStream oos;
            oos = new ObjectOutputStream( new FileOutputStream( new File( "featurizador_minimos_maximos.obj" ) ) );
            oos.writeObject( featurizador );
            oos.writeObject( minimos );
            oos.writeObject( maximos );
            oos.flush();
            oos.close();
        
            BufferedWriter bw = new BufferedWriter( new FileWriter( new File( "meta.ars") ) );
            bw.write( "classificador:svm" );
            bw.close();
            
            byte[] buffer = new byte[4096];
            
            // Adiciona objeto featurizador ao arquivo compactado
            ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( arquivoSaida ) ) ); 
            FileInputStream fis = new FileInputStream( "featurizador_minimos_maximos.obj" );
            zos.putNextEntry( new ZipEntry( "featurizador_minimos_maximos.obj" ) );
            int len;
            while ( ( len = fis.read(buffer) ) > 0 )
                    zos.write( buffer, 0, len );
            fis.close();
            zos.closeEntry();
            
            zos.putNextEntry( new ZipEntry( "meta.ars" ) );
            fis = new FileInputStream( "meta.ars" );
            while ( ( len = fis.read(buffer) ) > 0 )
                zos.write(  buffer, 0, len );
            fis.close();
            zos.closeEntry();
            
            new File( "meta.ars" ).delete();
            new File( "featurizador_minimos_maximos.obj" ).delete();
            
            // Adiciona classificadores treinados ao arquivo compactado
            for ( int i = 1; i <= 8; ++i ) {
                fis = new FileInputStream( classificadores[i].getAbsolutePath() );
                zos.putNextEntry( new ZipEntry( classificadores[i].getName() ) );
                while ( ( len = fis.read(buffer) ) > 0 )
                    zos.write( buffer, 0, len );
                zos.closeEntry();
                fis.close();
                classificadores[i].delete();
            }
            zos.close();

        Logger.getLogger( "ARS logger" ).log( Level.INFO, "Classificador SVM salvo no arquivo \"{0}\"", arquivoSaida.getAbsolutePath() );
            
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Pega o caminho do svm_learn e svm_classify
     * 
     * @param parametros 
     */
    @Override
    public void adicionarParametro( String parametro, String valor ) {
        if ( parametro.equals( "caminho.svmlight.learn" ) )
            caminhoSvmLearn = valor;            
        else if ( parametro.equals( "caminho.svmlight.classify" ) )
            caminhoSvmClassify = valor;
        else
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, "Parametro nao reconhecido: \"{0}:{1}\"", new Object[]{ parametro, valor });
    }

    @Override
    public void finalizar() {
        for ( int i = 1; i <= 8; ++i )
            classificadores[i].delete();
    }

    /**
     * Verifica se um arquivo de entrada tem o formato correto
     * 
     * @param arquivo Arquivo cujo formato será verificado
     * 
     * @return Verdadeiro se o arquivo é válido
     */
    public static boolean verificarSintaxe( File arquivo ) {
        BufferedReader br;
        try {
            br = new BufferedReader( new FileReader( arquivo ) );
            String linha;
            while ( ( linha = br.readLine() ) != null ) {
                String linhaSemClasse = linha.substring( 2, linha.length() );
                if ( !linha.substring( 0, 2 ).matches( "[1-8] " ) )
                    return false;
                String[] valores = linhaSemClasse.split( " " );
                for ( String valor : valores ) {
                    String[] partes = valor.split( ":" );
                    if ( partes.length != 2 )
                        return false;
                    if ( !partes[0].matches( "^\\d+$" ) )
                        return false;
                    if ( !partes[1].matches( "^\\d+\\.\\d+$" ) )
                        return false;
                }
            }
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( ClassificadorSVM.class.getName() ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex ) {
            Logger.getLogger( ClassificadorSVM.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return true;   
    }
    
}
