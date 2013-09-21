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

package aprendizadodemaquina.features.sintaticas;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.Token;
import anotadorderelacoes.model.Utilidades;
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Feature que traz os papéis sintáticos dos tokens ao redor dos termos de
 * interesse.
 * <p>
 * Como os termos podem ser compostos por vários tokens, esta feature na verdade
 * é formada por um atributo para cada uma das classes sintaticas possiveis.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>As ruas da [região metropolitana] de [São Paulo] estavam congestionadas</blockquote>
 */
public class FeatureClasseSintaticaTokensRedor extends Feature {
    
    private int tamanhoJanela = 1;
    
    @Override
    public String nome() {
        return "classes_sintaticas_redor";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {

        List<Object> valores = new ArrayList<Object>();

        Termo[] termos = { t1, t1, t2, t2 };
        int[] sinais = { -1, 1, -1, 1 };
        
        for ( int i = 0; i < 4; ++i ) {
            for ( Token token : Utilidades.tokensJanelaTermo( s, termos[i], tamanhoJanela * sinais[i] ) ) {
                for ( int j = 0; j < Utilidades.classesSintaticas.length; ++j ) {
                    int valor = 0;
                    if ( Utilidades.padroesClassesSintaticas[j].equals("") )
                        continue;
                    if ( token == null ) {
                        valores.add(new Integer(0));
                        continue;
                    }
                    if ( token.getSintaxe() != null && Pattern.matches(
                        Utilidades.padroesClassesSintaticas[j],
                        token.getSintaxe() ) )
                        valor = 1;
                    valores.add(new Integer(valor));
                }
            }
        }
        
        return valores;

    }

    @Override
    public int quantosValores() {
        return 4 * tamanhoJanela * Utilidades.classesSintaticas.length;
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