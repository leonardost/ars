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
import java.util.regex.Pattern;

/**
 * Feature que diz se há algum padrão textual específico entre os termos de
 * interesse. Cada padrão gera um atributo.
 * <p>
 * Padrões utilizados:
 * <ul>
 * <li>tais como</li>
 * <li>tal como</li>
 * <li>como</li>
 * <li>entre outros</li>
 * <li>e outros</li>
 * <li>e outro</li>
 * <li>alguns de os</li>
 * </ul>
 */
public class FeaturePadroesTextuais extends Feature {

    private List<String> padroes;

    public FeaturePadroesTextuais () {
        padroes = new ArrayList<String>();
        padroes.add(".*\\btais como\\b.*");
        padroes.add(".*\\btal como\\b.*");
        padroes.add(".*\\bcomo\\b.*");
        padroes.add(".*\\bentre outros\\b.*");
        padroes.add(".*\\be outros\\b.*");
        padroes.add(".*\\be outro\\b.*");
        padroes.add(".*\\balguns de os\\b.*");
    }

    @Override
    public String nome() {
        return "existem_padores_textuais_entre_os_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {

        String texto = "";
        for ( Token t : Utilidades.tokensEntre(s, t1, t2) )
            texto += t.getToken() + " ";
        texto = texto.toLowerCase();

        List<Object> valores = new ArrayList<Object>();
        
        for ( String p : padroes ) {
            if ( Pattern.matches(p, texto) )
                valores.add(new Integer(1));
            else
                valores.add(new Integer(0));
        }

        return valores;
        
    }

    @Override
    public int quantosValores() {
        return padroes.size();
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
