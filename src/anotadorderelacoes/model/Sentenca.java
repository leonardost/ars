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

import aprendizadodemaquina.Classificador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Representa uma sentença de um corpus
 * <p>
 * As sentenças vêm na forma JSON com os seguintes campos:
 * <ul>
 * <li>id*         : id da sentença, numérico
 * <li>texto*      : texto em forma superficial
 * <li>tokens*     : array contendo objetos Token
 * <li>termos      : array contendo objetos Termo
 * <li>relacoes    : array contendo objetos Relacao
 * <li>comentarios : comentários gerais sobre a sentença
 * <li>anotadores  : array contendo strings que indicam os anotadorse que
 *                   trabalharam nesta sentença
 * <li>anotada     : booleano que diz se a sentença já foi anotada por algum
 *                   anotador ou não
 * <li>ignorada    : booleano que diz se a sentença foi ignorada por algum erro
 *                   de marcação
 * </ul>
 * <p>
 * Campos marcados com * são obrigatorios
 * 
 * @see Token
 * @see Termo
 * @see Relacao
 */
public class Sentenca implements Comparable {

    private Integer id;
    private String texto;
    private List<Token> tokens;
    private List<Termo> termos;
    private List<Relacao> relacoes;
    private String comentarios;
    private List<String> anotadores;
    private Boolean anotada;
    private Boolean ignorada;
    
    // estruturas auxiliares
    private List<Integer> posicoesInicioTokens;  // posições em que cada token começa no texto da sentença
    private List<Integer> posicoesFimTokens;     // posições em que cada token termina no texto da sentença

    //<editor-fold defaultstate="collapsed" desc="Getters e setters">
    public Integer getId() {
        return id;
    }
    public void setId( Integer id ) {
        this.id = id;
    }
    public String getTexto() {
        return texto;
    }
    public void setTexto( String t ) {
        texto = t;
    }
    public List<Token> getTokens() {
        return tokens;
    }
    public List<Termo> getTermos() {
        return termos;
    }
    public List<Relacao> getRelacoes() {
        return relacoes;
    }
    public String getComentarios() {
        return comentarios;
    }
    public void setComentarios( String comentarios ) {
        this.comentarios = comentarios;
    }
    public List<String> getAnotadores() {
        return anotadores;
    }
    public Boolean isAnotada() {
        return anotada;
    }
    public void setAnotada( Boolean a ) {
        anotada = a;
    }
    public Boolean isIgnorada() {
        return ignorada;
    }
    public void setIgnorada( Boolean ignorada ) {
        this.ignorada = ignorada;
    }
    //</editor-fold>
    
    /**
     * Construtor default para a classe Sentenca. Não é usado na prática.
     */
    public Sentenca () {
        id = 0;
        texto = "";
        tokens = new ArrayList<Token>();
        termos = new ArrayList<Termo>();
        relacoes = new ArrayList<Relacao>();
        comentarios = "";
        anotadores = new ArrayList<String>();
        anotada = false;
        ignorada = false;
        
        posicoesInicioTokens = new ArrayList<Integer>();
        posicoesFimTokens = new ArrayList<Integer>();
    }
    
    /**
     * Cria um objeto Sentenca a partir de uma sentença codificada no formato
     * JSON.
     * <p>
     * Lança uma exceção NullPointerException quando recebe um arquivo com
     * formatação inválida.
     * 
     * @param sentencaJson String que codifica um objeto Sentenca em formato
     *                     JSON
     */
    public Sentenca( String sentencaJson ) throws NullPointerException {

        JSONObject json = (JSONObject)JSONValue.parse( sentencaJson );
        
        id = ( (Long)json.get( "id" ) ).intValue();
        texto = (String)json.get( "texto" );

        //System.out.println( "DEBUG: Parsing sentence " + id );
        //System.out.println( sentencaJson );

        // Os tokens são delimitados de acordo com suas posições na sentença
        posicoesInicioTokens = new ArrayList<Integer>();
        posicoesFimTokens = new ArrayList<Integer>();
        int caracteresAcumulados = 0;
        
        tokens = new ArrayList<Token>();
        for ( Object tokenJson : (JSONArray)json.get( "tokens" ) ) {
            if ( caracteresAcumulados > 0 )
                ++caracteresAcumulados;
            Token t = new Token( tokenJson.toString() );
            tokens.add( t );
            posicoesInicioTokens.add( caracteresAcumulados );
            caracteresAcumulados += t.getToken().length();
            posicoesFimTokens.add( caracteresAcumulados );
        }
        
        termos = new ArrayList<Termo>();
        if ( json.get( "termos" ) != null )
            for ( Object termoJson : (JSONArray)json.get( "termos" ) ) {
                Integer de = ( (Long)( (JSONObject)termoJson ).get( "de" ) ).intValue();
                Integer ate = ( (Long)( (JSONObject)termoJson ).get( "ate" ) ).intValue();
                String t = tokens.get( de ).getToken();
                for ( int i = de + 1; i <= ate; ++i )
                    t += " " + tokens.get( i ).getToken();
                termos.add( new Termo( de, ate, t ) );
            }
        
        relacoes = new ArrayList<Relacao>();
        if ( json.get( "relacoes" ) != null ) {
            for ( Object relacaoJson : (JSONArray)json.get( "relacoes" ) ) {
                String relacao = (String)( (JSONObject)relacaoJson ).get( "r" );
                Integer termo1 = ( (Long)( (JSONObject)relacaoJson ).get( "t1" ) ).intValue();
                Integer termo2 = ( (Long)( (JSONObject)relacaoJson ).get( "t2" ) ).intValue();
                relacoes.add( new Relacao( relacao,
                        new Termo( termos.get( termo1 ) ),
                        new Termo( termos.get( termo2 ) ) ) );
            }
        }

        comentarios = "";
        if ( json.get( "comentarios" ) != null )
            comentarios = (String)json.get( "comentarios" );

        anotadores = new ArrayList<String>();
        if ( json.get( "anotadores" ) != null )
            for ( Object anotador : (JSONArray)json.get( "anotadores" ) )
                anotadores.add( (String)anotador );

        anotada = false;
        if ( json.get( "anotada" ) != null )
            anotada = (Boolean)json.get( "anotada" );
        ignorada = false;
        if ( json.get( "ignorada" ) != null )
            ignorada = (Boolean)json.get( "ignorada" );

    }

    /**
     * Marca o texto delimitado pelos tokens tokenInicio e tokenFim como um
     * termo. Se algum dos tokens da seleção já faz parte de outro termo, não
     * faz nada.
     * 
     * @param tokenInicio Índice do token que inicia o termo
     * @param tokenFim Índice do token que termina o termo
     * @param termo Representa o termo em forma textual
     */
    public void marcarTermo( int tokenInicio, int tokenFim, String termo ) {
        // Verifica se a seleção já contém algum termo
        for ( Termo t : getTermos() )
            if ( t.getDe() <= tokenInicio && tokenInicio <= t.getAte() ||
                    t.getDe() <= tokenFim && tokenFim <= t.getAte() )
                return;

        termos.add( new Termo( tokenInicio, tokenFim, termo ) );
        Collections.sort( termos );
        Logger.getLogger( "ARS logger" ).log( Level.FINE, "Marcando termo ''{0}'', de {1} a {2}", new Object[]{ termo, tokenInicio, tokenFim } );
    }

    /**
     * Desmarca o termo cujo índice é idTermo. Relações que tinham esse termo em
     * sua composição também são desmarcadas.
     * 
     * @param idTermo Índice do termo a ser desmarcado
     * @return Verdadeiro se o termo foi desmarcado com sucesso, falso se o
     *         termo não foi desmarcado (id inválido)
     * 
     * @see Termo
     * @see Relacao
     */
    public boolean desmarcarTermo( int idTermo ) {
        Termo removido;
        try {
            removido = termos.remove( idTermo );
        }
        catch ( IndexOutOfBoundsException e ) {
            return false;
        }
        // Remove relações em que o termo removido aparecia
        desmarcarRelacao( removido );
        return true;
    }
    
    /**
     * Desmarca o termo 'termo' da sentença. Relações que tinham esse termo em
     * sua composiçao tambem sao desmarcadas.
     * 
     * @param termo Termo a ser desmarcado
     * @return Verdadeiro se o termo foi desmarcado com sucesso, falso caso
     *         contrario
     * @see Termo
     * @see Relacao
     */
    public boolean desmarcarTermo( Termo termo ) {
        termos.remove( termo );
        // Remove relações em que o termo removido aparecia
        desmarcarRelacao( termo );
        return true;
    }

    /**
     * Marca a relação 'relacao' com os termos termo1 e termo2 como argumentos.
     * Se essa relação já existe, não faz nada.
     * 
     * @param relacao Tipo da relação
     * @param termo1 Primeiro termo da relação
     * @param termo2 Segundo termo da relação
     */
    public void marcarRelacao( String relacao, Termo termo1, Termo termo2 ) {
        
        // Verifica se a relaç_o ja existe
        Relacao r1 = new Relacao( relacao, termo1, termo2 );
        Relacao r2 = new Relacao( relacao, termo2, termo1 );
        if ( relacoes.contains(r1) || relacoes.contains(r2) )
            return;
        
        relacoes.add( new Relacao( relacao, new Termo( termo1 ), new Termo( termo2 ) ) );

//        System.out.println( "TRACE: Marcando relação: " + relacao + "(" + termo1 + "(" + termo1.getDe() + "-" + termo1.getAte() + "), "
//                + termo2 + "(" + termo2.getDe() + "-" + termo2.getAte() + "))" );
        
        Collections.sort( relacoes );
        
    }
    
    /**
     * Desmarca todas as relações que tem como argumento o termo 'termo' passado
     * como parâmetro
     * 
     * @param termo Todas as relações que contêm este termo serão desmarcadas
     */
    public void desmarcarRelacao( Termo termo ) {
        Iterator<Relacao> i = relacoes.iterator();
        while ( i.hasNext() ) {
            Relacao r = i.next();
            if ( r.getTermo1().equals( termo ) || r.getTermo2().equals( termo ) )
                i.remove();
        }
    }

    /**
     * Desmarca uma relação específica passada como parâmetro
     * 
     * @param relacao Relação a ser desmarcada
     */ 
    public void desmarcarRelacao( Relacao relacao ) {
        relacoes.remove( relacao );
    }

    /**
     * Adiciona um novo anotador para esta sentença, se ele ainda não está
     * presente
     * 
     * @param anotador Nome do anotador a ser adicionado
     */ 
    public void adicionarAnotador( String anotador ) {
        if ( anotadores.contains( anotador ) )
            return;
        anotadores.add( anotador );
    }

    @Override
    public String toString() {
        return "(" + this.id + ") " + texto;
    }

    /**
    * Encontra qual token está localizado na posição (em caracteres) passada
    * como parâmetro
    * <p>
    * Ex: O rato roeu...
    * <p>
    *     Se pos == 0 ou pos == 1, token = 0
    *     Se pos == 2 até 6, token = 1
    *
    * @param posCaret Posição na sentença (começando em 0)
    * 
    * @return Índice do token que está localizado nessa posição, -1 se é uma 
    *         uma posição fora da sentença (sempre há um token em uma posição
    *         válida)
    */
    public int qualTokenNestaPosicao( int posCaret ) {
        for ( int i = 0; i < posicoesInicioTokens.size(); ++i )
            if ( posicoesInicioTokens.get( i ) <= posCaret &&
                    posCaret <= posicoesFimTokens.get( i ) )
                return i;
        return -1;
    }
    
    /**
     * Retorna verdadeiro se o token que está em uma dada posição (em
     * caracteres) faz parte de algum termo
     * 
     * @param posicao Posição que será verificada
     */
    public boolean tokenEmTermo( int posicao ) {
        for ( Termo t : termos )
            if ( t.getDe() <= posicao && posicao <= t.getAte() )
                return true;
        return false;
    }
    
    /**
     * Retorna a posição (em caracteres) onde um dado token de índice i começa.
     * Usado internamente para determinar quais tokens são selecionados na
     * seleção com o mouse.
     * 
     * @param indiceToken Índice do token a ser verificado
     * 
     * @return Posição em caracteres onde o token desejado começa
     */
    public int ondeTokenComeca( int indiceToken ) {
        return posicoesInicioTokens.get( indiceToken );
    }
    
    /**
     * Retorna a posição (em caracteres) onde um dado token de índice i termina.
     * Usado internamente para determinar quais tokens são selecionados na
     * seleção com o mouse.
     * 
     * @param indiceToken Índice do token a ser verificado
     * @return Posição em caracteres onde o token desejado começa
     */
    public int ondeTokenTermina( int indiceToken ) {
        return posicoesFimTokens.get( indiceToken );
    }

    /**
     * Retorna a representação em formato JSON desta sentença
     * 
     * @return String JSON que codifica esta sentença
     */
    public String toJson() {
        
        // ID, texto e comentarios da sentença
        JSONObject json = new JSONObject();
        json.put( "id", id );
        json.put( "texto", texto );
        json.put( "comentarios", comentarios );

        // Tokens
        JSONArray listaTokens = new JSONArray();
        for ( Token t : tokens )
            listaTokens.add( t.toJson() );
        json.put( "tokens", listaTokens );
        
        // Termos
        JSONArray listaTermos = new JSONArray();
        for ( Termo t : termos )
            listaTermos.add( t.toJson() );
        json.put( "termos", listaTermos );

        // Relaçoes
        JSONArray listaRelacoes = new JSONArray();
        for ( Relacao r : relacoes ) {
            JSONObject o = new JSONObject();
            o.put( "r", r.getRelacao() );
            o.put( "t1", termos.indexOf( r.getTermo1() ) );
            o.put( "t2", termos.indexOf( r.getTermo2() ) );
            listaRelacoes.add( o );
        }
        json.put("relacoes", listaRelacoes);

        // Anotadores
        JSONArray listaAnotadores = new JSONArray();
        for ( String a : anotadores )
            listaAnotadores.add( a );
        json.put( "anotadores", listaAnotadores );

        json.put( "anotada", anotada );
        json.put( "ignorada", ignorada );

        return json.toString();
        
    }

    @Override
    public int compareTo( Object o ) {
        if ( ! ( o instanceof Sentenca ) )
            throw new ClassCastException( "Erro de comparação entre classes" );
        return this.id - ( (Sentenca)o ).id;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( ! ( o instanceof Sentenca ) )
            return false;
        return this.id.equals( ( (Sentenca)o ).id );
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + ( this.id != null ? this.id.hashCode() : 0 );
        return hash;
    }

    /**
     * Gera uma representação da sentença em formato HTML. Atualmente não está
     * sendo utilizado.
     * 
     * @return String com a representaçao da sentença em formato HTML
     */
    public String toHtml() {
        String html = "<div class=\"sentenca\">\n";
        html += "<h3>Sentença ID " + id + "</h3>\n";
        for ( int i = 0; i < tokens.size(); ++i )
            html += "<span id=\"tok" + id + "-" + (i + 1) + "\">" + tokens.get(i).getToken() + "</span>";
        html += "\n";
        for ( int i = 0; i < tokens.size(); ++i )
            html += "<div id=\"tok" + id + "-" + (i + 1) + "-info\"><span>Posiçao:</span> " + (i + 1) + "<br><span>Token:</span> " + tokens.get(i).getLema() + "<br><span>POS:</span> " + tokens.get(i).getPos() + "<br><span>Sintaxe:</span> " + tokens.get(i).getSintaxe() + "<br></div>\n";
        html += "<h4>Relaçoes</h4>\n<ul class=\"relacoes\">\n";
        for ( int i = 0; i < relacoes.size(); ++i )
            html += "<li id=\"r" + id + "-" + (i + 1) + "\">" + relacoes.get(i).getRelacao() + "( " + relacoes.get(i).getTermo1() + ", " + relacoes.get(i).getTermo2() + " )<span>" + relacoes.get(i).getTermo1().getDe() + "," + relacoes.get(i).getTermo2().getDe() + "," + relacoes.get(i).getTermo2().getAte() + "</span></li>\n";
        html += "</ul>\n";
        html += "</div>\n";
        return html;
    }

    /**
     * Detecta automaticamente os termos existentes na sentença passada como
     * parametro. Usa a expressão regular (TODO: Corrigir esta expressão e
     * expandí-la. Por ex. Fundação de Amparo à Pesquisa não é identificado por
     * essa expressão)
     * <blockquote>A+ | A+ (de A+ (a|os A+))+</blockquote>
     * para identificar termos compostos. Todos os substantivos e adjetivos
     * restantes são marcados como termos.
     * <p>
     * Seria interessante experimentar com aprendizado de máquina também pra
     * identificar termos
     */
    public void detectarTermos () {
    
        // Começamos desmarcando todos os termos desta sentença
        List<Termo> copia = new ArrayList<Termo>( getTermos() );
        for ( Termo termo : copia ) {
            desmarcarTermo( termo );
        }
        
        // Marca alguns termos já pré-identificados como tais, presentes no
        // arquivo /recursos/lista_termos
        /*
        for ( int i = 0; i < getTokens().size(); ++i ) {
            if ( tokenEmTermo( i ) )
                continue;
            boolean marcou = false;
            for ( String t : Utilidades.listaTermosIdentificacao ) {
                String temp = getTokens().get(i).getToken().replace("=", " ");
                int j = i;
                while ( t.startsWith(temp) && j < getTokens().size() ) {
                    int posFim = j;
                    System.out.println(">>>>" + temp);
                    if ( t.equals(temp) ) {
                        marcarTermo(i, posFim, temp);
                        marcou = true;
                        break;
                    }
                    ++j;
                    temp += " " + getTokens().get(j).getToken().replace("=", " ");
                }
                if ( marcou )
                    break;
            }
        }
        */
        
        // Sequências de palavras capitalizadas são marcadas como termos
        // Aqui começa a identificação usando o autômato finito
        int posicao = 0;
        int posInicio = -1;
        int posFim = -1;
        int estado = 0;

        for ( Token token : getTokens() ) {

            String classeGramatical = Utilidades.classeGramatical( token );
            
            // Se o token já faz parte de algum termo e estávamos identificando
            // algum termo, marcamos o termo e movemos para a proxima posição na
            // sentença
            if ( tokenEmTermo( posicao ) ) {
                if ( estado == 1 || estado == 2 || estado == 3 ) {
                    List<Token> tokens = Utilidades.tokensJanela( this, posInicio, posFim - ( estado - 1 ) );
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posFim - (estado - 1), termo);
                    estado = 0;
                }
                ++posicao;
                continue;
            }

            if ( estado == 0 ) {

                posInicio = posicao;
                
                if ( classeGramatical != null && classeGramatical.equals("ADV") ) {
                    estado = 4;
                }
                else if ( token.getPos() != null && Pattern.matches(".*<.*(poss|dem).*>.*", token.getPos()) && !Pattern.matches(".*<.*artd.*>.*", token.getPos()) ) {
                    // Alguns artigos são marcados como pronomes demonstrativos
                    estado = 5;                    
                }
                // Se começa com letra maiúscula, provavelmente é um termo
                else if ( Utilidades.capitalizado(token) && classeGramatical != null && (classeGramatical.equals("N") || classeGramatical.equals("ADJ")) ) {
                    posFim = posicao;
                    estado = 1;
                }
                
            }
            // Token iniciado com letra maiúscula
            else if ( estado == 1 ) {
                
                if ( Utilidades.capitalizado(token) ) {
                    posFim = posicao;
                }
                else if ( token.getToken().equals("de") || token.getToken().equals("a") ) {
                    posFim = posicao;
                    estado = 2;    
                }
                else {
                    List<Token> tokens = Utilidades.tokensJanela(this, posInicio, posFim);
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posFim, termo);
                    estado = 0;
                }
                
            }
            // A + de/a
            else if ( estado == 2 ) {

                if ( Utilidades.capitalizado(token) ) {
                    posFim = posicao;
                    estado = 1;
                }
                else if ( token.getToken().equals("a") ||
                          token.getToken().equals("o") ||
                          token.getToken().equals("as") ||
                          token.getToken().equals("os") ) {
                    posFim = posicao;
                    estado = 3;    
                }
                else {
                    List<Token> tokens = Utilidades.tokensJanela(this, posInicio, posFim - 1);
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posFim - 1, termo);
                    estado = 0;
                }
                
            }
            // A + de/a + a(s)/o(s)
            else if ( estado == 3 ) {
            
                if ( Utilidades.capitalizado(token) ) {
                    posFim = posicao;
                    estado = 1;
                }
                else {
                    List<Token> tokens = Utilidades.tokensJanela(this, posInicio, posFim - 2);
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posFim - 2, termo);
                    estado = 0;
                }
                
            }
            // Advérbio
            else if ( estado == 4 ) {
                
                if ( Utilidades.capitalizado(token) ) {
                    posInicio = posicao;
                    posFim = posicao;
                    estado = 1;
                }
                else if ( classeGramatical != null && classeGramatical.equals("ADJ") ) {
                    List<Token> tokens = Utilidades.tokensJanela(this, posInicio, posicao);
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posicao, termo);
                    estado = 0;
                }
                else
                    estado = 0;
                
            }
            // Pronome possessivo ou demonstrativo
            else if ( estado == 5 ) {

                if ( Utilidades.capitalizado(token) ) {
                    posInicio = posicao;
                    posFim = posicao;
                    estado = 1;
                }
                else if ( classeGramatical != null && classeGramatical.equals("N") ) {
                    List<Token> tokens = Utilidades.tokensJanela(this, posInicio, posicao);
                    String termo = tokens.get(0).getToken();
                    for ( int i = 1; i < tokens.size(); ++i )
                        termo += " " + tokens.get(i).getToken();
                    marcarTermo(posInicio, posicao, termo);
                    estado = 0;
                }
                else
                    estado = 0;
                
            }

            ++posicao;
            
        }

        if ( estado == 1 || estado == 2 || estado == 3 ) {
            List<Token> tokens = Utilidades.tokensJanela( this, posInicio, posFim - ( estado - 1 ) );
            String termo = tokens.get(0).getToken();
            for ( int i = 1; i < tokens.size(); ++i )
                termo += " " + tokens.get(i).getToken();
            marcarTermo( posInicio, posFim - (estado - 1), termo );
        }
        
        // Todos os substantivos e adjetivos restantes são marcados como termos
        posicao = 0;
        for ( Token token : getTokens() ) {
            String classeGramatical = Utilidades.classeGramatical( token );
            if ( !tokenEmTermo( posicao ) &&
                 token.getToken().length() > 1 &&
                 classeGramatical != null &&
                 ( classeGramatical.equals( "N" ) || classeGramatical.equals( "ADJ" ) )
            )
                marcarTermo( posicao, posicao, token.getToken() );
            ++posicao;
        }

    }
    
    /**
     * Detecta automaticamente as relações existentes na sentença passada como
     * parametro, de acordo com o classificador selecionado atualmente.
     * <p>
     * Algoritmo:
     * para cada par de termos t1 e t2 de uma sentença s
     *     transformar a instancia(s, t1, t2) em um vetor de features v
     *     classificar o vetor de features v
     *     se a classificação for diferente de "none"
     *         adicionar a relacao(t1, t2) com a classe predita à sentença s
     * 
     * @param classificador Classificador utilizado para realizar a
     *                      classificação das relações semanticas
     */
    public void detectarRelacoes ( Classificador classificador ) {
        for ( Termo termo1 : termos )
            for ( Termo termo2 : termos ) {
                if ( termo1.equals( termo2 ) )
                    continue;
                //System.out.println( "DEBUG: Sentenca " + sentenca.getId() + ", termo1 = " + termo1 + ", termo2 = " + termo2 );
                String relacaoDetectada = classificador.classificar( this, termo1, termo2 );
                //System.out.println( "DEBUG: Relação detectada = " + relacaoDetectada );
                if ( ! relacaoDetectada.equals( "none" ) )
                    marcarRelacao( relacaoDetectada, termo1, termo2 );
            }

    }

    /**
     * Remove os termos que não estão sendo utilizados em nenhuma relação
     */
    public void removerTermosNaoUtilizados() {
        List<Termo> remover = new ArrayList<Termo>();
        for ( Termo t : termos ) {
            boolean fazParteDeRelacao = false;
            for ( Relacao r : relacoes )
                if ( r.getTermo1().equals( t ) || r.getTermo2().equals( t ) ) {
                    fazParteDeRelacao = true;
                    break;
                }
            if ( !fazParteDeRelacao )
                remover.add( t );
        }
        for ( Termo t : remover )
            termos.remove( t );
    }
    
}
