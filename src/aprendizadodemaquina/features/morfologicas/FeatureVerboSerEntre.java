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
 * Feature que diz se existe o verbo ser entre os termos de interesse.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>[São Paulo] é a maior [cidade] da América do Sul</blockquote>
 * o vetor retornado seria [1].
 */
public class FeatureVerboSerEntre extends Feature {
    
    @Override
    public String nome() {
        return "existe_verbo_ser_entre_os_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();
        int achou = 0;
        for ( Token token : Utilidades.tokensEntre(s, t1, t2) ) {
            String lema = token.getLema();
            String classe = Utilidades.classeGramatical(token);
            if ( lema != null && lema.equals("ser") && classe != null && classe.equals("V") )
                achou = 1;
        }
        valores.add( new Integer(achou) );
        return valores;

    }

    @Override
    public int quantosValores() {
        return 1;
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

