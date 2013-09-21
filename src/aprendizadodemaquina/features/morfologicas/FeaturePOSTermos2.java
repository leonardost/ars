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

package aprendizadodemaquina.features.morfologicas;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.Token;
import aprendizadodemaquina.Feature;
import anotadorderelacoes.model.Utilidades;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Feature que traz as etiquetas gramaticais dos tokens que compõem os termos.
 * Feature teste
 * <p>
 * Como os termos podem ser compostos por vários tokens, esta feature na verdade
 * é formada por um atributo para cada uma das 10 classes gramaticais possíveis
 * (N, ADJ, ART, PRON, NUM, V, ADV, PREP, CONJ, INTERJ) vezes 2 (para cada
 * termo).
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>As ruas da [região metropolitana] de [São Paulo] estavam congestionadas</blockquote>
 * o vetor retornado seria [1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
 * 0, 0, 0].
 */
public class FeaturePOSTermos2 extends Feature {
    
    @Override
    public String nome() {
        return "etiquetas_pos_dos_termos_2";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();

        for ( int i = 0; i < Utilidades.padroesClassesGramaticais2.length; ++i ) {
            int valor = 0;
            for ( Token t : Utilidades.tokensTermo(s, t1) ) {
                if ( t.getPos() != null && Pattern.matches(
                     Utilidades.padroesClassesGramaticais2[i],
                     t.getPos() ) )
                    valor = 1;
            }
            valores.add( new Integer(valor) );
        }

        for ( int i = 0; i < Utilidades.padroesClassesGramaticais2.length; ++i ) {
            int valor = 0;
            for ( Token t : Utilidades.tokensTermo(s, t2) ) {
                if ( t.getPos() != null && Pattern.matches(
                     Utilidades.padroesClassesGramaticais2[i],
                     t.getPos() ) )
                    valor = 1;
            }
            valores.add( new Integer(valor) );
        }
        
        return valores;

    }

    @Override
    public int quantosValores() {
        return 2 * Utilidades.padroesClassesGramaticais2.length;
    }

    @Override
    public String tipo() {
        return "NOMINAL";
    }

    @Override
    public String[] valoresPossivis() {
        String[] valores = new String[2];
        valores[0] = "0";
        valores[1] = "1";
        return valores;
    }

}