//OK!
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

package aprendizadodemaquina;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;

/**
 * Classe abstrata da qual todas as features devem herdar. Define a interface
 * padrão de uma feature.
 */
public abstract class Feature implements java.io.Serializable {

    /**
     * @return Nome que identifica esta feature
     */
    public abstract String nome();

    /**
     * @return Quantos atributos esta feature gera
     */
    public abstract int quantosValores();
    
    /**
     * @return Se é um atributo nominal ("NOMINAL") ou numérico ("NUMERICO")
     */
    public abstract String tipo();
   
    /** 
     * @return Se é um atributo nominal, retorna os possíveis valores que o
     *         atributo pode assumir
     */
    public abstract String[] valoresPossivis();
    
    /**
     * Transforma a instância passada como parâmetro em uma lista de valores
     * referentes a esta feature.
     * 
     * @param s Sentença que engloba os termos da instância de relação a ser
     *          gerada
     * @param t1 Primeiro termo da relação
     * @param t2 Segundo termo da relação
     * @return Lista de valores que representam a instância após passar por esta
     *         feature
     */
    public abstract java.util.List<Object> gerar( Sentenca s, Termo t1, Termo t2 );
    
}
