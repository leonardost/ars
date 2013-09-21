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
import anotadorderelacoes.model.Utilidades;
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature que diz se há um artigo precedendo diretamente um dos termos da
 * relação
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>a [canção] do [pássaro]</blockquote>
 * o vetor retornado seria [1, 0] (há um artigo antes do primeiro termo e não há
 * antes do segundo termo).
 */
public class FeatureArtigoAntesTermo extends Feature {

    @Override
    public String nome() {
        return "artigo_antes_de_termo";
    }

    @Override
    public List<Object> gerar( Sentenca s, Termo t1, Termo t2 ) {
        List<Object> valores = new ArrayList<Object>();
        int valor1 = 0;
        Token token = Utilidades.tokensJanelaTermo(s, t1, -1).get(0);
        String classe = Utilidades.classeGramatical(token);
        if ( classe != null && classe.equals("ART") )
            valor1 = 1;
        int valor2 = 0;
        token = Utilidades.tokensJanelaTermo(s, t2, -1).get(0);
        classe = Utilidades.classeGramatical(token);
        if ( classe != null && classe.equals("ART") )
            valor2 = 1;
        valores.add( new Integer(valor1) );
        valores.add( new Integer(valor2) );
        return valores;
    }

    @Override
    public int quantosValores() {
        return 2;
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
