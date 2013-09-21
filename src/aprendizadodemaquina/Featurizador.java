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
import aprendizadodemaquina.features.morfologicas.*;
import aprendizadodemaquina.features.sintaticas.*;
import aprendizadodemaquina.features.superficiais.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Classe que realiza a featurização das instâncias de relaçoes para uso no
 * treinamento e predição dos classificadores. Engloba as classes Feature dentro
 * da lista features.
 */
public class Featurizador implements Serializable {
    
    private List<Feature> features;      // quais features serão utilizadas na featurização
    private List<String> tiposRelacoes;  // as classes de relaçoes possiveis
    private boolean usarFeatureIdentificacao;
    private boolean usarFeaturesSuperficiais;
    private boolean usarFeaturesMorfologicas;
    private boolean usarFeaturesSintaticas;

    public Featurizador() {
        inicializacao();
    }
    
    public Featurizador( boolean featureId, boolean featuresSuperficiais, boolean featuresMorfologicas, boolean featuresSintaticas ) {
        this.usarFeatureIdentificacao = featureId;
        this.usarFeaturesSuperficiais = featuresSuperficiais;
        this.usarFeaturesMorfologicas = featuresMorfologicas;
        this.usarFeaturesSintaticas = featuresSintaticas;
        inicializacao();
    }
    
    private void inicializacao() {
        features = new ArrayList<Feature>();
        
        // "Feature" de identificação da sentença
        if ( usarFeatureIdentificacao )
            features.add( new FeatureIdentificacao() );
        
        // Superficiais
        if ( usarFeaturesSuperficiais ) {
            features.add( new FeatureDistanciaTermos() );
            features.add( new FeatureTamanhoTermos() );
            features.add( new FeatureOrdemTermos() );
            features.add( new FeatureVirgulasEntreTermos() );
            features.add( new FeaturePalavrasCapitalizadasEntre() );
            features.add( new FeaturePadroesTextuais() );              // aparentemente funcionando OK//
            features.add( new FeatureTokensTermosCapitalizados() );
            features.add( new FeatureParentesesRedorTermos() );
            features.add( new FeatureAspasRedorTermos() );//
            features.add( new FeatureVirgulasRedorTermos() );
            features.add( new FeatureTermosContemNumeros() );//
            features.add( new FeatureTermosComecamComDe() );
            features.add( new FeatureNumeroTermosEntreTermos() );
            features.add( new FeatureTamanhoCaracteresTermos() );
            features.add( new FeatureTermosCapitalizados() );
        }
            
        // Morfológicas
        if ( usarFeaturesMorfologicas ) {
            features.add( new FeaturePOSTermos() );                    // aparentemente funcionando OK
    //        features.add( new FeaturePOSTermos2() );
            features.add( new FeaturePOSRedor() );                     // aparentemente funcionando OK 
    //        features.add( new FeaturePOSRedor2() );
            features.add( new FeatureVerboSerEntre() );
            features.add( new FeaturePreposicaoEntre() );
            features.add( new FeatureArtigoAntesTermo() );
        }

        // Sintáticas
        if ( usarFeaturesSintaticas ) {
            features.add( new FeatureClasseSintaticaTermos() );
            features.add( new FeatureClasseSintaticaTokensRedor() );
        }
        
        // Semânticas
        //if ( usarFeaturesSemanticas ) {
            //features.add( new FeatureNomePais() );
            //features.add( new FeatureNomePais2() );
            //features.add( new FeatureNomeEstado() );
            //features.add( new FeatureSiglaEstado() );
            //features.add( new FeatureNomeCidade() );
        //}
        
        tiposRelacoes = new ArrayList<String>();
        tiposRelacoes.add( "is-a" );
        tiposRelacoes.add( "property-of" );
        tiposRelacoes.add( "part-of" );
        tiposRelacoes.add( "made-of" );
        tiposRelacoes.add( "effect-of" );
        tiposRelacoes.add( "used-for" );
        tiposRelacoes.add( "location-of" );
        tiposRelacoes.add( "none" );
    }
    
    /**
     * Converte uma instância de relação semântica em uma lista de objetos
     * Double, utilizada nos classificadores libSVM e SVMLight
     * 
     * @param s Sentença que contem a instância de relação
     * @param r A instância de relação em si (null se não for treinamento)
     * @param t1 Primeiro termo da relação
     * @param t2 Segundo termo da relação
     * @param tipo Pode ser "predicao", "treinamento" ou "negativa"
     * 
     * @return Lista de objetos Double que representa a instância de relação
     *         semântica passada como parametro.
     */
    public List<Double> paraSvm( Sentenca s, Relacao r, Termo t1, Termo t2, String tipo ) {
        
        List<Double> valores = new ArrayList<Double>();

        // Nas instâncias SVMLight a classe alvo é o primeiro atributo
        if ( tipo.equals( "predicao" ) )
            valores.add( 0.0 );
        else if ( tipo.equals( "treinamento" ) )
            valores.add( (double)tiposRelacoes.indexOf( r.getRelacao() ) + 1.0 );
        else if ( tipo.equals( "negativa" ) )
            valores.add( (double)tiposRelacoes.indexOf( "none" ) + 1.0 );
        
        for ( Feature f : features ) {

            List<Object> gerados = f.gerar( s, t1, t2 );
            
            if ( f.tipo().equals( "NUMERICO" ) )
                for ( Object o : gerados )
                    valores.add( Double.parseDouble( o.toString() ) );
            else if ( f.tipo().equals( "NOMINAL" ) ) {
                // Quando é um atributo nominal, recomenda-se que sejam criadas
                // N features binárias, uma para cada valor possível
                
                // Se já é uma feature binaria, mantemos os valores
                if ( f.valoresPossivis().length == 2 ) {
                    for ( Object o : gerados )
                        if ( (o.toString()).equals( "1" ) )
                            valores.add( 1.0 );
                        else if ( ( o.toString() ).equals( "0" ) )
                            valores.add( 0.0 );
                }
                // Se não é, fazemos a separação da feature em N features binárias
                else {
                    for ( Object o : gerados )
                        for ( String valorPossivel : f.valoresPossivis() )
                            if ( ( o.toString() ).equals( valorPossivel ) )
                                valores.add( 1.0 );
                            else
                                valores.add( 0.0 );
                }
            }

        }
        
        return valores;
        
    }
    
    /**
     * Converte uma instância de relação semantica em um objeto Instance que e
     * utilizado pelo Weka.
     * <p>
     * O parâmetro tipo pode ser "predicao", "treinamento" ou "negativa",
     * dependendo do tipo de instância que está sendo gerada. Se for do tipo
     * "treinamento", o parâmetro r deve ser diferente de null. 
     * 
     * @param s Sentença que contem a instância de relação
     * @param r A instância de relação em si (null se não for treinamento)
     * @param t1 Primeiro termo da relação
     * @param t2 Segundo termo da relação
     * @param tipo Pode ser "predicao", "treinamento" ou "negativa"
     * 
     * @return Objeto Instance que representa a instância de relação semantica
     *         passada como parametro.
     */
    public Instance paraInstancia( Sentenca s, Relacao r, Termo t1, Termo t2, String tipo ) {

        List<Double> valores = new ArrayList<Double>();

        for ( Feature f : features ) {

            List<Object> gerados = f.gerar( s, t1, t2 );
            
            if ( f.tipo().equals( "NUMERICO" ) )
                for ( Object o : gerados )
                    valores.add( Double.parseDouble(o.toString()) );
            else if ( f.tipo().equals( "NOMINAL" ) ) {
                FastVector valoresPossiveis = new FastVector();
                for ( String valor : f.valoresPossivis() )
                    valoresPossiveis.addElement( valor );
                for ( Object o : gerados )
                    valores.add( (double)valoresPossiveis.indexOf( o.toString() ) );
            }

        }

        double[] valoresDouble = new double[numeroAtributos()];
        for ( int i = 0; i < valores.size(); ++i )
            valoresDouble[ i ] = valores.get( i );

        // Apenas o último atributo muda de acordo com o tipo de instância

        // Se for do tipo "predicao", o último atributo é um missingValue
        if ( tipo.equals( "predicao" ) )
            valoresDouble[ valores.size() ] = Instance.missingValue();
        // Se for do tipo "treinamento", a relação é fornecida
        else if ( tipo.equals( "treinamento" ) )
            valoresDouble[ valores.size() ] = tiposRelacoes.indexOf( r.getRelacao() );
        // Se for "negativa", a relação é negativa
        else if ( tipo.equals( "negativa" ) )
            valoresDouble[ valores.size() ] = tiposRelacoes.indexOf( "none" );
        
        // 
        Instance instance = new Instance( 1.0, valoresDouble );

        //System.out.println("DEBUG: Instância " + tipo + " = " + instance);
        
        return instance;
        
    }
    
    /**
     * Gera o objeto Instances - cabeçalho dos dados a serem utilizados pelo
     * classificador, guarda os atributos das instâncias e seus tipos
     * 
     * @return Objeto Instances que é armazenado na classe Classificador
     */
    public Instances geraInstances() {
        
        FastVector atributos = new FastVector();
        
        for ( Feature f : features )
            if ( f.tipo().equals( "NUMERICO" ) ) {
                if ( f.quantosValores() == 1 )
                    atributos.addElement( new Attribute( f.nome() ) );
                else
                    for ( int i = 0; i < f.quantosValores(); ++i )
                        atributos.addElement( new Attribute( f.nome() + (i + 1) ) );
            }
            else if ( f.tipo().equals( "NOMINAL" ) ) {
                FastVector valoresPossiveis = new FastVector();
                for ( String valor : f.valoresPossivis() )
                    valoresPossiveis.addElement( valor );
                if ( f.quantosValores() == 1 )
                    atributos.addElement( new Attribute( f.nome(), valoresPossiveis ) );
                else
                    for ( int i = 0; i < f.quantosValores(); ++i )
                        atributos.addElement( new Attribute( f.nome() + (i + 1), valoresPossiveis ) );
            }

        // Qual relação é
        FastVector valoresPossiveis = new FastVector();
        for ( String s : tiposRelacoes )
            valoresPossiveis.addElement(s);
        atributos.addElement( new Attribute( "relacao", valoresPossiveis ) );
        
        return( new Instances( "dados_de_treinamento", atributos, 0 ) );

    }
    
    /**
     * Retorna o numero de atributos gerados pelo featurizador
     * 
     * @return Número de atributos gerados
     */
    public int numeroAtributos () {
        int numero = 0;
        for ( Feature f : features )
            numero += f.quantosValores();
        numero++; // Qual relação é
        return numero;
    }
    
}
