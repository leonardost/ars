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
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature que traz o número de termos que ocorrem entre os termos de interesse.
 */
public class FeatureNumeroTermosEntreTermos extends Feature {

    @Override
    public String nome() {
        return "numero_termos_entre_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        List<Object> valores = new ArrayList<Object>();
        int numeroTermos = 0;
        int inicio, fim;
        if ( t1.getDe() < t2.getDe() ) {
            inicio = t1.getAte() + 1;
            fim = t2.getDe();
        }
        else {
            inicio = t2.getAte() + 1;
            fim = t1.getDe(); 
        }
        for ( int i = inicio; i < fim; ++i ) {
            if ( s.tokenEmTermo(i) )
                // TODO TODO TODO:
                // Completar aqui
                ++numeroTermos;
        }
        valores.add( new Integer(numeroTermos) );
        return valores;
    }

    @Override
    public int quantosValores() {
        return 1;
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