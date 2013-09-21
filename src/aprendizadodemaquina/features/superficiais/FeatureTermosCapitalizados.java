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

package aprendizadodemaquina.features.superficiais;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.Token;
import anotadorderelacoes.model.Utilidades;
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature que diz se um termo est_ completamente capitalizado ou n_o.
 */
public class FeatureTermosCapitalizados extends Feature {

    @Override
    public String nome() {
        return "termos_capitalizados";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();
        String stringTermo1 = "", stringTermo2 = "";
        int termo1 = 0, termo2 = 0;
        for ( Token token : Utilidades.tokensTermo(s, t1) )
            stringTermo1 += token.getToken();
        for ( Token token : Utilidades.tokensTermo(s, t2) )
            stringTermo2 += token.getToken();
        if ( stringTermo1.toUpperCase().equals(stringTermo1) )
            valores.add(new Integer(1));
        else
            valores.add(new Integer(0));
        if ( stringTermo2.toUpperCase().equals(stringTermo2) )
            valores.add(new Integer(1));
        else
            valores.add(new Integer(0));
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
