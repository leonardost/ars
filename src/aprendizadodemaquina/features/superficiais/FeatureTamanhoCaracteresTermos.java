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
 * Feature que diz o tamanho dos termos em número de caracteres que os compõem.
 * (conta apenas os caracteres que compõem os tokens, os espaços não)
 */
public class FeatureTamanhoCaracteresTermos extends Feature {

    @Override
    public String nome() {
        return "tamanho_caracteres_dos_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();
        int tamanhoTermo1 = 0;
        int tamanhoTermo2 = 0;
        for ( Token token : Utilidades.tokensTermo(s, t1) )
            tamanhoTermo1 += token.getToken().length();
        for ( Token token : Utilidades.tokensTermo(s, t2) )
            tamanhoTermo2 += token.getToken().length();
        valores.add(new Integer(tamanhoTermo1));
        valores.add(new Integer(tamanhoTermo2));
        return valores;
    }

    @Override
    public int quantosValores() {
        return 2;
    }

    @Override
    public String tipo() {
        return "NUMERICO";
    }

    @Override
    public String[] valoresPossivis() {
        return null;
    }
    
}
