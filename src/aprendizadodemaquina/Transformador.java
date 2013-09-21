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

package aprendizadodemaquina;

import anotadorderelacoes.model.Relacao;
import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.UtilidadesPacotes;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Classe responsável por transformar os pacotes de anotação para outros
 * formatos.
 * 
 * Algoritmo:
 * 
 * - Para cada arquivo na lista de arquivos de treinamento
 *      - Ler cada sentença
 * - Para cada sentença s lida
 *      - Para cada relaçao marcada em s
 *          - Gerar vetor de features para essa relaçao
 */
public class Transformador {
    
    private List<Sentenca> listaSentencas;
    private Featurizador featurizador;
    
    public Transformador( List<File> arquivosEntrada ) {
        // Le as sentenças do conjunto de arquivos selecionados
        this.listaSentencas = UtilidadesPacotes.lerSentencasPacote( arquivosEntrada );
        // Não usamos a feature de identificação e usamos todas as demais
        // (superficiais, morfológicas e sintáticas)
        this.featurizador = new Featurizador( false, true, true, true );
    }
    
    public void paraDt( File arquivoSaida ) {

        // Gera um objeto Instances ("cabeçalho" para instancias do Weka)
        Instances dadosTreinamento = featurizador.geraInstances();
        
        Set<Par> paresTermosUsados = new HashSet<Par>();

        // Gera as instancias de treinamento para cada sentença
        for ( Sentenca s : listaSentencas ) {

            s.removerTermosNaoUtilizados();
            
            // Instancias positivas (relaçoes marcadas)
            for ( Relacao r : s.getRelacoes() ) {
                Par p = new Par(s, r.getTermo1(), r.getTermo2());
                if ( paresTermosUsados.contains(p) )
                    continue;
                dadosTreinamento.add( featurizador.paraInstancia(s, r, r.getTermo1(), r.getTermo2(), "treinamento") );
                paresTermosUsados.add( p );
            }
            // Instancias negativas (todas os pares de termos nao marcados como relaçoes)
            for ( Termo t1 : s.getTermos() ) {
                for ( Termo t2 : s.getTermos() ) {
                    if ( t1.equals( t2 ) || paresTermosUsados.contains( new Par( s, t1, t2 ) ) )
                        continue;
                    dadosTreinamento.add( featurizador.paraInstancia( s, null, t1, t2, "negativa") );
                }
            }
        }

        // Salva o conjunto de dados no arquivo de saida
        try {
            ArffSaver arffSaver = new ArffSaver();
            arffSaver.setInstances( dadosTreinamento );
            arffSaver.setFile( arquivoSaida );
            arffSaver.writeBatch();
            Logger.getLogger( "ARS logger" ).log( Level.INFO,
                    "Conjunto de dados de treinamento salvo no arquivo {0}", arquivoSaida.getAbsolutePath() );
        }
        catch (IOException ex) {
            Logger.getLogger( "ARS logger" ).log(Level.SEVERE, null, ex);
        }

    }
    
    public void paraSvm( File arquivoSaida ) {

        List<List<Double>> dadosTreinamento = new ArrayList<List<Double>>();

        Set<Par> paresTermosUsados = new HashSet<Par>();
        
        // Gera as instancias de treinamento para cada sentença
        for ( Sentenca s : listaSentencas ) {
            
            s.removerTermosNaoUtilizados();
            
            // Instancias positivas (relaçoes marcadas)
            for ( Relacao r : s.getRelacoes() ) {
                Par par = new Par( s, r.getTermo1(), r.getTermo2() );
                if ( paresTermosUsados.contains( par ) )
                    continue;
                paresTermosUsados.add( par );
                List<Double> instancia = featurizador.paraSvm( s, r, r.getTermo1(), r.getTermo2(), "treinamento" );
                dadosTreinamento.add( instancia );
            }
            
            // Instancias negativas (todas os pares de termo nao marcados como relaçoes)
            for ( Termo t1 : s.getTermos() ) {
                for ( Termo t2 : s.getTermos() ) {
                    if ( t1.equals( t2 ) || paresTermosUsados.contains( new Par( s, t1, t2 ) ) )
                        continue;
                    List<Double> instancia = featurizador.paraSvm( s, null, t1, t2, "negativa" );
                    dadosTreinamento.add( instancia );
                }
            }
            
        }

        Logger.getLogger( "ARS logger" ).log( Level.FINE, "Numero de instancias de treinamento: {0}", dadosTreinamento.size() );
        
        // Salva o conjunto de dados no arquivo de saida
        try {
            BufferedWriter bw = new BufferedWriter( new FileWriter( arquivoSaida ) );
            for ( List<Double> instancia : dadosTreinamento ) {
                String linha;
                linha = "" + instancia.get(0).intValue();
                //bw.write( instancia.get(0).intValue() );
                for ( int i = 1; i < instancia.size(); ++i )
                    if ( instancia.get(i).doubleValue() != 0.0 )
                        linha += " " + i + ":" + UtilidadesPacotes.doubleToString( instancia.get(i) );
                        //bw.write( " " + i + ":" + UtilidadesPacotes.doubleToString( instancia.get(i) ) );
                //bw.write( "\n" );
                bw.write( linha + "\n" );
                //System.out.println( linha );
            }
            bw.close();
            Logger.getLogger( "ARS logger" ).log( Level.INFO,
                    "Conjunto de dados de treinamento salvo no arquivo {0}", arquivoSaida );
        }
        catch ( IOException ex ) {
            Logger.getLogger( "ARS logger" ).log( Level.SEVERE, null, ex );
        }    
    }
    
}

/**
 * Classe utilitaria só pra guardar os pares de termos que já foram utilizados
 * Note que o par não é simétrico (t1, t2) != (t2, t1)
 */
class Par {
    
    Sentenca s;
    Termo t1;
    Termo t2;
    Par( Sentenca s, Termo t1, Termo t2 ) {
        this.s = s;
        this.t1 = t1;
        this.t2 = t2;
    }
    
    @Override
    public boolean equals(Object o) {
        if ( !( o instanceof Par ) )
            return false;
        Par p = (Par)o;
        if ( s.equals( p.s ) && t1.equals( p.t1 ) && t2.equals( p.t2 ) )
            return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + ( this.s != null ? this.s.hashCode() : 0 );
        hash = 37 * hash + ( this.t1 != null ? this.t1.hashCode() : 0 );
        hash = 37 * hash + ( this.t2 != null ? this.t2.hashCode() : 0 );
        return hash;
    }
    
}