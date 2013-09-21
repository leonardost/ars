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

package anotadorderelacoes.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Classe utilitaria que engloba algumas funcionalidades que nao se encaixam bem
 * nas demais classes. Engloba funcionalidades de leitura dos pacotes de
 * anotação.
 */
public class UtilidadesPacotes {

    /**
     * Retorna o cabeçalho de um pacote
     * 
     * @param arquivo Pacote de sentenças
     * 
     * @return Cabeçalho do pacote
     * 
     * @see Sentenca
     */
    public static String lerCabecalhoPacote( File arquivo ) {

/*        FileReader fr = null;
        try {
            fr = new FileReader( arquivo );
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        Scanner scanner = new Scanner( fr );*/
        
        BufferedReader br = null;
        try {
             br = new BufferedReader( new InputStreamReader( new FileInputStream( arquivo ), "UTF-8" ) );
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        String linha;
        
        // Leitura do cabeçalho
        linha = "";
        String cabecalho = "";
        try {
            while ( !linha.equals( "===" ) && linha != null ) {
                //if ( scanner.hasNextLine() )
                linha = br.readLine();
            }
            //if ( scanner.hasNextLine )
                //linha = scanner.nextLine();
            //else
                //return "";
            cabecalho += linha + "\n";
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
        if ( !linha.equals( "===" ) )
            return "";
        
        try {
            //fr.close();
            br.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
        return cabecalho;
        
    }
    
    /**
     * Faz a leitura de um pacote de sentenças, retornando uma lista de
     * objetos Sentenca.
     * 
     * @param arquivo Pacote de sentenças
     * @return Lista de sentenças do pacote
     * @see Sentenca
     */
    public static List<Sentenca> lerSentencasPacote( File arquivo ) {
        
        String cabecalho = lerCabecalhoPacote( arquivo );
        
        BufferedReader br = null;
        try {
             br = new BufferedReader( new InputStreamReader( new FileInputStream( arquivo ), "UTF-8" ) );
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( UnsupportedEncodingException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
/*        FileReader fr = null;
        try {
            fr = new FileReader( arquivo );
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }

*/
        
//        Scanner scanner = new Scanner( fr );
        
        String linha = "";
        if ( !cabecalho.equals( "" ) ) {
            try {
                while ( !linha.equals( "===" ) )
                    //linha = scanner.nextLine();
                    linha = br.readLine();
            }
            catch ( IOException ex ) {
                Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
            }
        }

        List<Sentenca> lista = new ArrayList<Sentenca>();
        // Lemos as sentenças (cada uma está em uma linha)
        /*while ( scanner.hasNextLine() ) {
            linha = scanner.nextLine();
            lista.add( new Sentenca( linha ) );
        }*/
        try {
            while ( ( linha = br.readLine() ) != null )
                lista.add( new Sentenca( linha ) );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }

        //scanner.close();

        try {
            //fr.close();
            br.close();
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
        return lista;
        
    }

    /**
     * Faz a leitura de um conjunto de pacotes de sentenças, retornando uma
     * lista de objetos Sentenca. Sentenças repetidas nao sao tratadas, todas as
     * sentenças sao lidas.
     * 
     * @param arquivos Lista de pacotes de sentenças
     * @return Lista de sentenças do pacote
     * @see Sentenca
     */
    public static List<Sentenca> lerSentencasPacote( List<File> arquivos ) {
        // Inicializa a lista de sentenças
        List<Sentenca> listaSentencas = new ArrayList<Sentenca>();
        for ( File arquivo : arquivos )
            listaSentencas.addAll( lerSentencasPacote( arquivo ) );
        return listaSentencas;
    }

    /**
     * Converte um arquivo de saída do parser PALAVRAS para o formato JSON
     * aceito pela ARS.
     *
     * @param arquivoPalavras Arquivo resultante do parser PALAVRAS
     * @param arquivoSaida Arquivo de texto convertido para o formato JSON
     */
    public static void conveterPalavrasJson( File arquivoPalavras, File arquivoSaida ) {

        BufferedReader br;
        BufferedWriter bw;
        String linha;
        int contadorSentencas = 1;

        JSONObject sentenca = new JSONObject();
        JSONArray tokens = new JSONArray();
        
        sentenca.put( "id", contadorSentencas++ );
        sentenca.put( "texto", "" );
        sentenca.put( "comentarios", "" );
        
        sentenca.put( "termos", new JSONArray() );
        sentenca.put( "relacoes", new JSONArray() );
        sentenca.put( "anotadores", new JSONArray() );
        
        sentenca.put( "anotada", false );
        sentenca.put( "ignorada", false );
        
        try {
            
            //br = new BufferedReader( new FileReader( arquivoPalavras ) );
            br = new BufferedReader( new InputStreamReader( new FileInputStream( arquivoPalavras ), "UTF-8" )  );
            //bw = new BufferedWriter( new FileWriter( arquivoSaida ) );
            bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( arquivoSaida ), "UTF-8" ) );
            
            bw.write( "# Arquivo \"" + arquivoPalavras.getName() + "\" convertido para o formato JSON\n" );
            bw.write( "===\n");
            
            while ( ( linha = br.readLine() ) != null ) {
                
                // Fim de uma sentença
                if ( linha.equals( "</s>" ) ) {

                    String texto = "";
                    for ( Object token : tokens )
                        texto += ( (JSONObject)token ).get( "t" ) + " ";
                    texto = texto.substring( 0, texto.length() - 1 );
                    sentenca.put( "texto", texto );
                    
                    sentenca.put( "tokens", tokens );
                    bw.write( sentenca.toString() + "\n" );
                    
                    sentenca = new JSONObject();
                    tokens = new JSONArray();
                    sentenca.put( "id", contadorSentencas++ );
                    sentenca.put( "texto", "" );
                    sentenca.put( "comentarios", "" );
                    sentenca.put( "termos", new JSONArray() );
                    sentenca.put( "relacoes", new JSONArray() );
                    sentenca.put( "anotadores", new JSONArray() );
                    sentenca.put( "anotada", false );
                    sentenca.put( "ignorada", false );
                    
                }
                
                // Lendo uma sentença
                // Transformações adaptadas do script "criar-pacotes.pl"
                else {

                    linha = linha.replace( "«", "<<" );
                    linha = linha.replace( "»", ">>" );
                    linha = linha.replace( "º", "o" );
                    
                    Matcher matcher;
                    
                    
                    // Apenas para tratar as sentenças que têm "secretaria" (e.g. 96524)
                    if ( linha.matches( "(.*\\t.*)\\t.*" ) ) {
                        matcher = Pattern.compile( "(.*\\t.*)\\t.*" ).matcher( linha );
                        linha = matcher.group( 1 );
                    }
                    
                    //if ( linha.matches( "<s id=\"(\\d+)\">" ) ) {
                    if ( patterns[0].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "<s id=\"(\\d+)\">" ).matcher( linha );
                        matcher.find();
                        sentenca.put( "id", matcher.group( 1 ) );
                    }
                    else if ( linha.startsWith( "$," ) ) {
                        tokens.add( novoToken( ",", ",", null, null ) );
                    }
                    //else if ( linha.matches( "^\\$(.)$" ) ) {
                    else if ( patterns[1].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^\\$(.)$" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 1 ), null, null ) );
                    }    
//                    else if ( linha.matches( "^\\$(..)" ) ) {
                    else if ( patterns[2].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^\\$(..)" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 1 ), null, null ) );
                    }
//                    else if ( linha.matches( "^\\$(%)" ) ) {
                    else if ( patterns[3].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^\\$(%)" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 1 ), null, null ) );
                    }
                    // Linha normal
//                    else if ( linha.matches( "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+) (@.*)$" ) ) {
                    else if ( patterns[4].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+) (@.*)$" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 2 ), matcher.group( 3 ), matcher.group( 4 ) ) );
                    }
                    // Alguns tokens não têm a tag sintática
//                    else if ( linha.matches( "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+)$" ) ) {
                    else if ( patterns[5].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+)$" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 2 ), matcher.group( 3 ), null ) );
                    }
                    // E alguns não têm o lema
//                    else if ( linha.matches( "^(.*)\\t ?([^@]+) (@.*)$" ) ) {
                    else if ( patterns[6].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^(.*)\\t ?([^@]+) (@.*)$" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), null, matcher.group( 2 ), matcher.group( 3 ) ) );
                    }
                    // Às vezes tem alguns tokens que vêm sem nada, são tratados como pontuação
//                    else if ( linha.matches( "^(.*)$" ) ) {
                    else if ( patterns[7].matcher( linha ).find() ) {
                        matcher = Pattern.compile( "^(.*)$" ).matcher( linha );
                        matcher.find();
                        tokens.add( novoToken( matcher.group( 1 ), matcher.group( 2 ), null, null ) );
                    }
                    else {
                        System.out.println( "PROBLEMA!" );
                        System.out.println( linha );
                    }

                }
            }

            String texto = "";
            for ( Object token : tokens )
                texto += ( (JSONObject)token ).get( "t" ) + " ";
            texto = texto.substring( 0, texto.length() - 1 );
            sentenca.put( "texto", texto );
            
            sentenca.put( "tokens", tokens );
            bw.write( sentenca.toString() + "\n" );
            
            bw.close();
            br.close();
            
            Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo JSON \"{0}\" gravado.", arquivoSaida.getAbsolutePath() );
            
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        
    }
    
    private static String[] regexes;
    private static Pattern[] patterns;
    static {
        regexes = new String[] { "<s id=\"(\\d+)\">", "^\\$(.)$", "^\\$(..)", "^\\$(%)",
            "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+) (@.*)$",
            "^(.*)\\t ?\\[([^\\]]+)\\] ([^@]+)$",
            "^(.*)\\t ?([^@]+) (@.*)$",
            "^(.*)$" };
        patterns = new Pattern[ regexes.length ];
        for ( int i = 0; i < regexes.length; ++i )
            patterns[i] = Pattern.compile( regexes[i] );
    }
    
    public static JSONObject novoToken( Object t, Object l, Object pos, Object sin ) {
        JSONObject token = new JSONObject();
        token.put( "t", t );
        token.put( "l", l );
        token.put( "pos", pos );
        token.put( "sin", sin );
        return token;
    }
    
    /**
     * Converte um arquivo de texto puro para o formato JSON aceito pela ARS.
     * O arquivo de texto deve conter uma sentença por linha.
     *
     * @param arquivoTexto Arquivo de texto puro
     * @param arquivoSaida Arquivo de texto convertido para o formato JSON
     */
    public static void converterTextoJson( File arquivoTexto, File arquivoSaida ) {

        BufferedReader br;
        BufferedWriter bw;
        String linha;
        int contadorSentencas = 1;
        
        try {
            
            //br = new BufferedReader( new FileReader( arquivoTexto ) );
            br = new BufferedReader( new InputStreamReader( new FileInputStream( arquivoTexto ), "UTF-8" )  );
            //bw = new BufferedWriter( new FileWriter( arquivoSaida ) );
            bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( arquivoSaida ), "UTF-8" ) );
            
            bw.write( "# Arquivo \"" + arquivoTexto.getName() + "\" convertido para o formato JSON\n" );
            bw.write( "===\n");
            
            // Uma sentença por linha
            while ( ( linha = br.readLine() ) != null ) {

                JSONObject sentenca = new JSONObject();
                JSONArray tokens = new JSONArray();

                sentenca.put( "id", contadorSentencas++ );
                sentenca.put( "texto", linha);
                sentenca.put( "comentarios", "" );

                sentenca.put( "termos", new JSONArray() );
                sentenca.put( "relacoes", new JSONArray() );
                sentenca.put( "anotadores", new JSONArray() );

                sentenca.put( "anotada", false );
                sentenca.put( "ignorada", false );
                
                // Separação simples de tokens
                for ( String token : linha.split( " " ) )
                    tokens.add( novoToken( token, token, null, null ) );
                sentenca.put( "tokens", tokens );

                bw.write( sentenca.toString() + "\n" );

            }
            
            bw.close();
            br.close();
            
            Logger.getLogger( "ARS logger" ).log( Level.INFO, "Arquivo JSON \"{0}\" gravado.", arquivoSaida.getAbsolutePath() );
            
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Método utilitário para imprimir Doubles sem zeros excedentes
     * 
     * @param d Double que se deseja imprimir
     * @return Representação em texto do Double com 17 casas decimais e sem
     *         zeros excedentes
     */
    public static String doubleToString ( Double d ) {
        String valorString = String.format( Locale.US, "%.17f", d.doubleValue() );
        while ( valorString.endsWith( "0" ) && !valorString.endsWith( ".0" ) )
            valorString = valorString.substring( 0, valorString.length() - 1 );
        return valorString;
    }
    
    /**
     * Verifica se um dado arquivo é um classificador e, se sim, qual é. Faz
     * isso descompactando o arquivo e procurando pelo arquivo "meta.ars".
     * 
     * @param arquivo
     * 
     * @return "invalido" se não é um classificador, "svm" ou "j48" caso
     *         contrário.
     */
    public static String verificarClassificador ( File arquivo ) {
        
        String classificador = "invalido";
        
        try {

            ZipInputStream zis = new ZipInputStream( new FileInputStream( arquivo ) );
            ZipEntry ze;
            byte[] buffer = new byte[4096];
            
            while ( ( ze = zis.getNextEntry() ) != null ) {

                FileOutputStream fos = new FileOutputStream( ze.getName() );
                int numBytes;
                while ( ( numBytes = zis.read( buffer, 0, buffer.length ) ) != -1 )
                    fos.write( buffer, 0, numBytes );
                fos.close();
                zis.closeEntry();

                if ( ze.getName().equals( "meta.ars" ) ) {
                    BufferedReader br = new BufferedReader( new FileReader( "meta.ars" ) );
                    String linha;
                    while ( ( linha = br.readLine() ) != null ) {
                        String[] valores = linha.split( ":" );
                        if ( valores[0].equals( "classificador" ) )
                            classificador = valores[1];
                    }
                    br.close();
                }

                new File( ze.getName() ).delete();
                
            }
            
        }
        catch ( FileNotFoundException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }
        catch ( NullPointerException ex ) {
            //Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
            return classificador;
        }
        
        return classificador;
        
    }
    
}
