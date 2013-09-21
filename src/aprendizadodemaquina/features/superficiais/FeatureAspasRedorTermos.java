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
 * Feature que diz se há aspas ao redor dos termos.
 */
public class FeatureAspasRedorTermos extends Feature {

    @Override
    public String nome() {
        return "existem_aspas_ao_redor_dos_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        
        List<Object> valores = new ArrayList<Object>();
        
        int valor1a = 0;
        int valor1d = 0;
        int valor2a = 0;
        int valor2d = 0;
        
        Token token;
        token = Utilidades.tokensJanelaTermo( s, t1, -1 ).get(0);
        if ( token != null && (token.getToken().contains("'") || token.getToken().contains("\"")) )
            valor1a = 1;
        token = Utilidades.tokensJanelaTermo( s, t1, 1 ).get(0);
        if ( token != null && (token.getToken().contains("'") || token.getToken().contains("\"")) )
            valor1d = 1;
        token = Utilidades.tokensJanelaTermo( s, t2, -1 ).get(0);
        if ( token != null && (token.getToken().contains("'") || token.getToken().contains("\"")) )
            valor2a = 1;
        token = Utilidades.tokensJanelaTermo( s, t2, 1 ).get(0);
        if ( token != null && (token.getToken().contains("'") || token.getToken().contains("\"")) )
            valor2d = 1;
        
        valores.add( new Integer(valor1a) );
        valores.add( new Integer(valor1d) );
        valores.add( new Integer(valor2a) );
        valores.add( new Integer(valor2d) );
        
        return valores;

    }

    @Override
    public int quantosValores() {
        return 4;
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
