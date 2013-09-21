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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Classe utilitária que engloba algumas funcionalidades de manipulação de
 * sentenças, termos, tokens e relações.
 */
public class Utilidades {

    // http://beta.visl.sdu.dk/visl/pt/info/
    public static String[] classesGramaticais = new String[10];
    public static String[] padroesClassesGramaticais = new String[10];
    public static String[] classesSintaticas = new String[37];
    public static String[] padroesClassesSintaticas = new String[37];
    
    public static String[] classesGramaticais2 = new String[13];
    public static String[] padroesClassesGramaticais2 = new String[13];
    
    public final static List<String> listaTermosIdentificacao;
    
    static {
        
        classesGramaticais[0] = "N";
        classesGramaticais[1] = "ADJ";
        classesGramaticais[2] = "ART";
        classesGramaticais[3] = "PRON";
        classesGramaticais[4] = "NUM";
        classesGramaticais[5] = "V";
        classesGramaticais[6] = "ADV";
        classesGramaticais[7] = "PREP";
        classesGramaticais[8] = "CONJ";
        classesGramaticais[9] = "INTERJ";

        // TODO: Verificar essas expressões regulares. A ordem em que elas são aplicadas
        // não deveria fazer diferença, mas está fazendo na feature de tags POS ao redor
        // dos temros
        
        padroesClassesGramaticais[0] = ".*\\b(N|PROP)\\b.*";
        padroesClassesGramaticais[1] = ".*\\bADJ\\b.*";
        padroesClassesGramaticais[2] = ".*(<artd>|<arti>).*";
        padroesClassesGramaticais[3] = ".*(\\bPERS\\b|<poss|<dem>|<rel>|<interr>|SPEC).*";
//       "PRON"    : r"(\bPERS\b|<poss|<dem>|<rel> (DET|SPEC)|<interr>|<quant> (DET|SPEC)|<diff> <KOMP> DET)",
        padroesClassesGramaticais[4] = ".*(\\bNUM\\b|<NUM-ord>).*";
        padroesClassesGramaticais[5] = ".*\\bV\\b.*";
        padroesClassesGramaticais[6] = ".*\\bADV\\b.*";
        padroesClassesGramaticais[7] = ".*\\bPRP\\b.*";
        padroesClassesGramaticais[8] = ".*\\b(KC|KS)\\b.*";
        padroesClassesGramaticais[9] = ".*\\bIN\\b.*";
        
        // Classes sintaticas
        
        /*
        @SUBJ> @<SUBJ subject
        @ACC> @<ACC accusative (direct) object
        @DAT> @<DAT dative object (only pronominal)
        @PIV> @<PIV prepositional (indirect) object
        @ADVS> / @SA> @<ADVS / @<SA adverbial object (place, time, duration, quantity), subject-related
        @ADVO> / @OA> @<ADVO / @<OA adverbial object, object-related
        @SC> @<SC subject predicative
        @OC> @<OC object predicative
        @ADVL> @<ADVL adverbial
        @PASS> @<PASS agent of passive
        (All above clause arguments [@SUBJ, @ACC, @DAT, @PIV, @ADVS, @ADVO, @SC, @OC, @PASS] and the adverbial complements [@ADVL] attach to the nearest main verb to the left [<] or right [>].)
        */
        
        classesSintaticas[0] = "SUJEITO";
        padroesClassesSintaticas[0] = ".*@(SUBJ>|<SUBJ).*";
        classesSintaticas[1] = "ACUSATIVO";
        padroesClassesSintaticas[1] = ".*@(ACC>|<ACC).*";
        classesSintaticas[2] = "DATIVO";
        padroesClassesSintaticas[2] = ".*@(DAT>|<DAT).*";
        classesSintaticas[3] = "PREPOSICIONAL";
        padroesClassesSintaticas[3] = ".*@(PIV>|<PIV).*";
        classesSintaticas[4] = "OBJETO ADVERBIAL";
        padroesClassesSintaticas[4] = ".*@(ADVS>|<ADVS|SA>|<SA).*";
        classesSintaticas[5] = "OBJETO ADVERBIAL 2";
        padroesClassesSintaticas[5] = ".*@(ADVO>|<ADVO|OA>|<OA).*";
        classesSintaticas[6] = "SUJEITO PREDICATIVO";
        padroesClassesSintaticas[6] = ".*@(SC>|<SC).*";
        classesSintaticas[7] = "OBJETO PREDICATIVO";
        padroesClassesSintaticas[7] = ".*@(OC>|<OC).*";
        classesSintaticas[8] = "ADVERBIAL";
        padroesClassesSintaticas[8] = ".*@(ADVL>|<ADVL).*";
        classesSintaticas[9] = "AGENTE DA PASSIVA";
        padroesClassesSintaticas[9] = ".*@(PASS>|<PASS).*";
        
        /*
        @ADVL 'free' adverbial phrase (in non-sentence expression)
        @NPHR 'free' noun phrase (in non-sentence expression without verbs)
        @VOK 'vocative' (e.g. 'free' addressing proper noun in direct speech)
        @>N prenominal adject (attaches to the nearest NP-head to the right, that is not an adnominal itself)
        @N< postnominal adject (attaches to the nearest NP-head to the left, that is not an adnominal itself)
        @N<PRED postnominal (in-group predicative) or predicate in small clause introduced by 'com/sem' (rare, e.g. com a mão na bolsa, sem o pai ajudando, não conseguiu)
        @APP identifying apposition (always after NP + komma)
        @>A prepositioned adverbial adject
        (attaches to the nearest ADJ/PCP/ADV or attributive used N to the right)
        @A< postpositioned adverbial adject (rare, e.g. caro demais) or dependent/argument of attributive participle (with function tag attached, e.g. @A<ADVL or @A<SC)
        @PRED> 'forward' free predicative (refers to the following @SUBJ, even when this is incorporated in the VP)
         */

        classesSintaticas[10] = "SINTAGMA ADVERBIAL LIVRE";
        padroesClassesSintaticas[10] = ".*@ADVL[^<>].*";
        classesSintaticas[11] = "SINTAGMA NOMINAL LIVRE";
        padroesClassesSintaticas[11] = ".*@NPHR.*";
        classesSintaticas[12] = "VOCATIVO";
        padroesClassesSintaticas[12] = ".*@VOK.*";
        classesSintaticas[13] = "ADJUNTO PRENOMINAL";
        padroesClassesSintaticas[13] = ".*@>N.*";
        classesSintaticas[14] = "ADJUNTO POSNOMINAL";
        padroesClassesSintaticas[14] = ".*@N<[^P].*";
        classesSintaticas[15] = "POSNOMINAL";
        padroesClassesSintaticas[15] = ".*@N<PRED.*";
        classesSintaticas[16] = "APOSTO";
        padroesClassesSintaticas[16] = ".*@APP.*";
        classesSintaticas[17] = "prepositioned adverbial adject";
        padroesClassesSintaticas[17] = ".*@>A.*";
        classesSintaticas[18] = "postpositioned adverbial adject";
        padroesClassesSintaticas[18] = ".*@A<.*";
        classesSintaticas[19] = "'forward' free predicative";
        padroesClassesSintaticas[19] = ".*@PRED>.*";

        /*
        @<PRED `backward' free predicative (refers to the nearest NP-head to the left, or to the nearest @SUBJ to the left)
        @P< argument of preposition
        @S< sentence anaphor (`não venceu o que muito o contrariou')
        @FAUX finite auxiliary (cp. @#ICL-AUX<)
        @FMV finite main verb
        @IAUX infinite auxiliary (cp. @#ICL-AUX<)
        @IMV infinite main verb
        @PRT-AUX< verb chain particle (preposition or "que" after auxiliary)
        @CO coordinating conjunction
        @SUB subordinating conjunction
        */
        
        classesSintaticas[20] = "`backward' free predicative";
        padroesClassesSintaticas[20] = ".*@<PRED.*";
        classesSintaticas[21] = "argument of preposition";
        padroesClassesSintaticas[21] = ".*@P<.*";
        classesSintaticas[22] = "sentence anaphor";
        padroesClassesSintaticas[22] = ".*@S<.*";
        classesSintaticas[23] = "finite auxiliary";
        padroesClassesSintaticas[23] = ".*@FAUX.*";
        classesSintaticas[24] = "finite main verb";
        padroesClassesSintaticas[24] = ".*@FMV.*";
        classesSintaticas[25] = "infinite auxiliary";
        padroesClassesSintaticas[25] = ".*@IAUX.*";
        classesSintaticas[26] = "infinite main verb";
        padroesClassesSintaticas[26] = ".*@IMV.*";
        classesSintaticas[27] = "verb chain particle";
        padroesClassesSintaticas[27] = ".*@PRT-AUX<.*";
        classesSintaticas[28] = "coordinating conjunction";
        padroesClassesSintaticas[28] = ".*@CO.*";
        classesSintaticas[29] = "subordinating conjunction";
        padroesClassesSintaticas[29] = ".*@SUB.*";
        
        /*
        @KOMP< argument of comparative (e.g. "do que" referring to melhor)
        @COM direct comparator without preceding comparative
        @PRD role predicator (e.g. "work as", "function as")
        @FOC> @<FOC focus marker ("gosta é de peixe.")
        @TOP topic constituent ("Esse negócio, não gosto dele.")
        @#FS- finite subclause (combines with clausal role and intraclausal word tag, e.g.@#FS-<ACC @SUB for "não acredito que seja verdade")
        @#ICL- infinite subclause (combines with clausal role and intraclausal word tag, e.g. @#ICL-SUBJ> @IMV in "consertar um relógio não é fácil")
        @#ICL-AUX< argument verb in verb chain, refers to preceding auxiliary (the verb chain sequence @FAUX - @#ICL-AUX< is used, where both verbs have the same subject, @FMV - @#ICL-<ACC is used where the subjects are different)
        @#AS- averbal (i.e. verb-less) subclause (combines with clausal role and intraclausal word tag, e.g. @#AS-<ADVL @ADVL> in "ajudou onde possível")
        @AS< argument of complementiser in averbal subclause 
        */

        classesSintaticas[30] = "argument of comparative";
        padroesClassesSintaticas[30] = ".*@KOMP<.*";
        classesSintaticas[31] = "direct comparator";
        padroesClassesSintaticas[31] = ".*@COM.*";
        //classesSintaticas[32] = "role predicator";
        //padroesClassesSintaticas[32] = ".*@PRD.*";
        //classesSintaticas[33] = "focus marker";
        //padroesClassesSintaticas[33] = ".*@(FOC>|<FOC).*";
        //classesSintaticas[34] = "topic constituent";
        //padroesClassesSintaticas[34] = ".*@TOP.*";
        classesSintaticas[32] = "finite subclause";
        padroesClassesSintaticas[32] = ".*@#FS-.*";
        classesSintaticas[33] = "infinite subclause";
        padroesClassesSintaticas[33] = ".*@#ICL-.*";
        classesSintaticas[34] = "argument verb in verb chain";
        padroesClassesSintaticas[34] = ".*@#ICL-AUX<.*";
        classesSintaticas[35] = "averbal";
        padroesClassesSintaticas[35] = ".*@#AS-.*";
        classesSintaticas[36] = "argument of complementiser";
        padroesClassesSintaticas[36] = ".*@AS<.*";
        
        /*
        N Nouns
        PROP Proper nouns (names)
        SPEC Specifiers (defined as non-inflecting pronouns, that can't be used as prenominals):
        e.g. certain indefinite pronouns, nominal quantifiers, nominal relatives
        DET Determiners (defined as inflecting pronouns, that can be used as prenominals): --
        e.g. articles, attributive quantifiers
        PERS Personal pronouns (defined as person-inflecting pronouns)
        ADJ Adjectives (including ordinals, excluding participles which are tagged V PCP)
        ADV Adverbs (both 'primary' adverbs and derived adverbs ending in -mente)
        V Verbs (full verbs, auxiliaries)
        NUM Numerals (cardinals)
        PRP Preposition
        KS Subordinating conjunctions
        KC Coordinationg conjunctions
        IN Interjections 
        */
        
        
        
        
        /*
         * Experimentando com as classes gramaticais diretamente do PALAVRAS
        classesGramaticais2[0] = "N";
        classesGramaticais2[1] = "PROP";
        classesGramaticais2[2] = "SPEC";
        classesGramaticais2[3] = "DET";
        classesGramaticais2[4] = "PERS";
        classesGramaticais2[5] = "ADJ";
        classesGramaticais2[6] = "ADV";
        classesGramaticais2[7] = "V";
        classesGramaticais2[8] = "NUM";
        classesGramaticais2[9] = "PRP";
        classesGramaticais2[10] = "KS";
        classesGramaticais2[11] = "KC";
        classesGramaticais2[12] = "IN";
        
        padroesClassesGramaticais2[0] = ".*\\bN\\b.*";
        padroesClassesGramaticais2[1] = ".*\\bPROP\\b.*";
        padroesClassesGramaticais2[2] = ".*\\bSPEC\\b.*";
        padroesClassesGramaticais2[3] = ".*\\bDET\\b.*";
        padroesClassesGramaticais2[4] = ".*\\bPERS\\b.*";
        padroesClassesGramaticais2[5] = ".*\\bADJ\\b.*";
        padroesClassesGramaticais2[6] = ".*\\bADV\\b.*";
        padroesClassesGramaticais2[7] = ".*\\bV\\b.*";
        padroesClassesGramaticais2[8] = ".*\\bNUM\\b.*";
        padroesClassesGramaticais2[9] = ".*\\bPRP\\b.*";
        padroesClassesGramaticais2[10] = ".*\\bKS\\b.*";
        padroesClassesGramaticais2[11] = ".*\\bKC\\b.*";
        padroesClassesGramaticais2[12] = ".*\\bIN\\b.*";
        */

        // Lê os termos fixos da lista
        Scanner scanner = new Scanner( Utilidades.class.getResourceAsStream( "/recursos/lista_termos" ) );
        listaTermosIdentificacao = new ArrayList<String>();
        while ( scanner.hasNextLine() )
            listaTermosIdentificacao.add( scanner.nextLine() );
        
    }

    /**
     * Retorna a classe gramatical de um dado token.
     * <p>
     * Usa expressões regulares sobre o atributo "pos" do token para tentar
     * encontrar a classe gramatical à qual ele pertence. Retorna null se a
     * classe não for reconhecida.
     * 
     * @param token Token cuja classe gramatical deseja-se obter
     * @return Classe gramatical do token passado como parâmetro ou null caso a
     *         classe gramatical não seja reconhecida
     */
    public static String classeGramatical( Token token ) {

        if ( token == null || token.getPos() == null )
            return null;

        for ( int i = 0; i < padroesClassesGramaticais.length; ++i )
            if ( Pattern.matches( padroesClassesGramaticais[i], token.getPos() ) )
                return classesGramaticais[i];

        // Se a classe não foi reconhecida e tem um "DET", provavelmente é um pronome
        if ( Pattern.matches( "DET", token.getPos() ) )
            return "PRON";

        // hard-coded rules
//    if token["l"] == "mesmo" or token["l"] == "algum" or token["l"] == "todo" \
//       or token["l"] == "tudo" or token["l"] == "ambos" or token["l"] == "outro" \
//       or token["l"] == "todo=o":
//        return "PRON"

        // TODO: Verificar o lema dos tokens "vários" e "várias"
        if ( token.getLema().equals( "algo" ) ||
                token.getToken().equals( "vários" ) ||
                token.getToken().equals( "várias" ) ||
                token.getToken().equals( "uns" ) ||
                token.getToken().equals( "umas" ) ||
                token.getLema().equals( "algum" ) )
            return "PRON";

//    if token["l"] == "vários" or token["l"] == "diversos" or token["l"] == "próprio":
//        return "ADJ"

        return null;

    }

    /**
     * Retorna a classe gramatical de um dado token.
     * <p>
     * Retorna null se a classe não for reconhecida.
     * 
     * @param token Token cuja classe gramatical deseja-se obter
     * @return Classe gramatical do token passado como parâmetro ou null caso a
     *         classe gramatical não seja reconhecida
     */
    public static String classeGramatical2( Token token ) {

        if ( token == null || token.getPos() == null )
            return null;

        for ( int i = 0; i < padroesClassesGramaticais2.length; ++i )
            if ( Pattern.matches(padroesClassesGramaticais2[i], token.getPos()) )
                return classesGramaticais2[i];

        // Se a classe não foi reconhecida e tem um "DET", provavelmente é um pronome
        if ( Pattern.matches("DET", token.getPos()) )
            return "PRON";

        // hard-coded rules
//    if token["l"] == "mesmo" or token["l"] == "algum" or token["l"] == "todo" \
//       or token["l"] == "tudo" or token["l"] == "ambos" or token["l"] == "outro" \
//       or token["l"] == "todo=o":
//        return "PRON"

        // TODO: Verificar o lema dos tokens "vários" e "várias"
        if ( token.getLema().equals("algo") || token.getToken().equals("vários") || token.getToken().equals("várias") ||
             token.getToken().equals("uns") || token.getToken().equals("umas") || token.getLema().equals("algum") )
            return "PRON";

//    if token["l"] == "vários" or token["l"] == "diversos" or token["l"] == "próprio":
//        return "ADJ"

        return null;

    }
    
    /**
     * Retorna a classe sintática de um dado token.
     * <p>
     * Retorna null se a classe não for reconhecida.
     * 
     * @param token Token cuja classe sint_tica deseja-se obter
     * @return Classe sint_tica do token passado como parâmetro ou null caso a
     *         classe sint_tica não seja reconhecida
     */
    public static String classeSintatica ( Token token ) {

        if ( token == null || token.getSintaxe() == null )
            return null;
        
        for ( int i = 0; i < padroesClassesSintaticas.length; ++i )
            if ( Pattern.matches(padroesClassesSintaticas[i], token.getSintaxe()) )
                return classesSintaticas[i];

        return null;

    }
    
    /**
     * Retorna uma lista dos tokens entre os termos de interesse
     * 
     * @param sentenca Sentença que contém os termos de interesse
     * @param termo1 Primeiro termo de interesse
     * @param termo2 Segundo termo de interesse
     * @return Lista de tokens entre os termos passados como parâmetro
     */
    public static List<Token> tokensEntre( Sentenca sentenca, Termo termo1, Termo termo2 ) {

        List<Token> tokens = new ArrayList<Token>();
        
        int comeco, fim;
        
        if ( termo1.getDe() < termo2.getDe() ) {
            comeco = termo1.getAte() + 1;
            fim = termo2.getDe();
        }
        else {
            comeco = termo2.getAte() + 1;
            fim = termo1.getDe();
        }

        for ( int i = comeco; i < fim; ++i )
            tokens.add( sentenca.getTokens().get(i) );

        return tokens;
                
    }

    /**
     * Retorna uma lista dos tokens que compõem o termo passado como parâmetro
     * 
     * @param sentenca Sentença que contém o termo de interesse
     * @param termo Termo cujos tokens deseja-se obter
     * @return Lista de objetos Token que compõem esse termo
     */
    public static List<Token> tokensTermo( Sentenca sentenca, Termo termo ) {
        List<Token> tokens = new ArrayList<Token>();
        for ( int i = termo.getDe(); i < termo.getAte() + 1; ++i )
            tokens.add( sentenca.getTokens().get(i) );
        return tokens;
    }
  
    /**
     * Retorna uma lista dos tokens que estão dentro de uma certa distância do
     * termo passado como parâmetro
     * <p>
     * <b>Exemplo</b>: sentenca = [A, rosa, vermelha, brigou, com, o, cravo]<br>
     * termo = "rosa vermelha"<br>
     * Com distância = 2, retorna [brigou, com]<br>
     * Com distância = -2, retorna [null, A]<br>
     *
     * @param sentença Sentença de onde os tokens desejados serão obtidos
     * @param termo Termo que é o centro da janela de busca
     * @param distancia Número de tokens que serão retornados. Se for positivo
     *        retorna os tokens seguintes ao termo passado como parâmetro, se
     *        for negativo retorna os tokens anteriores ao termo.
     */ 
    public static List<Token> tokensJanelaTermo( Sentenca sentença, Termo termo, int distancia ) {

        int inicio, fim;
        
        if ( distancia < 0 ) {
            inicio = termo.getDe() + distancia;
            fim = termo.getDe();
        }
        else {
            inicio = termo.getAte() + 1;
            fim = termo.getAte() + 1 + distancia;
        }
            
        List<Token> tokens = new ArrayList<Token>();

        for ( int i = inicio; i < fim; ++i )
            if ( i < 0 || i >= sentença.getTokens().size() )
                tokens.add( null );
            else
                tokens.add( sentença.getTokens().get(i) );

        return tokens;

    }

    /**
     * Retorna verdadeiro se o token é capitalizado, falso caso contŕario
     * 
     * @param token O token de interesse
     * @return Verdadeiro se o token inicia com letra maiúscula, falso caso
     *         contrário
     */
    public static boolean capitalizado( Token token ) {
        String primeiraLetra = token.getToken().substring( 0, 1 );
        if ( primeiraLetra.equals( primeiraLetra.toLowerCase() ) )
            return false;
        return true;
    }

    /**
     * Retorna uma lista dos tokens que estão entre dois tokens
     *
     * @param sentença Sentença de onde os tokens desejados serão obtidos
     * @param indiceTokenInicio Índice do token que é o início do intervalo
     * @param indiceTokenFim Índice do token que é o fim do intervalo
     *        (inclusive)
     * @return Lista dos tokens que estão entre os tokens passados como
     *         parâmetro
     */ 
    public static List<Token> tokensJanela( Sentenca sentença, int indiceTokenInicio, int indiceTokenFim ) {
        List<Token> tokens = new ArrayList<Token>();
        for ( int i = indiceTokenInicio; i <= indiceTokenFim; ++i )
            tokens.add( sentença.getTokens().get(i) );
        return tokens;
    }
    
}
