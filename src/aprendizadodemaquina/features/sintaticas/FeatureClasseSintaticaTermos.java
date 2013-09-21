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
 * Feature que traz os papéis sintaticos dos tokens que compõem os termos.
 * <p>
 * Como os termos podem ser compostos por vários tokens, esta feature na verdade
 * é formada por um atributo para cada uma das classes sintaticas possiveis.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>As ruas da [região metropolitana] de [São Paulo] estavam congestionadas</blockquote>
 */
public class FeatureClasseSintaticaTermos extends Feature {
    
    @Override
    public String nome() {
        return "classes_sintaticas_dos_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();

        for ( int i = 0; i < Utilidades.classesSintaticas.length; ++i ) {
            int valor = 0;
            for ( Token t : Utilidades.tokensTermo(s, t1) ) {
                if ( t.getSintaxe() != null && Pattern.matches(
                     Utilidades.padroesClassesSintaticas[i],
                     t.getSintaxe() ) )
                    valor = 1;
            }
            valores.add( new Integer(valor) );
        }

        for ( int i = 0; i < Utilidades.classesSintaticas.length; ++i ) {
            int valor = 0;
            for ( Token t : Utilidades.tokensTermo(s, t2) ) {
                if ( t.getSintaxe() != null && Pattern.matches(
                     Utilidades.padroesClassesSintaticas[i],
                     t.getSintaxe() ) )
                    valor = 1;
            }
            valores.add( new Integer(valor) );
        }
        
        return valores;

    }

    @Override
    public int quantosValores() {
        return 2 * Utilidades.classesSintaticas.length;
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