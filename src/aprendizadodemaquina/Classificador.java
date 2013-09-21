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
 * Classe abstrata que representa um classificador genérico. Define a interface
 * de um classificador. As implementações de classificadores devem herdar desta
 * classe.
 */
public abstract class Classificador implements java.io.Serializable {

    /**
     * Retorna o nome do classificador
     * 
     * @return Nome do classificador
     */
    public abstract String getNome();
    
    /**
     * Possibilita a adição de parâmetros extras ao classificador
     * 
     * @param parametro Nome do parâmetro a ser adicionado
     * @param valor Valor do parâmetro a ser adicionado
     */
    public abstract void adicionarParametro( String parametro, String valor );
    
    /**
     * Treina o classificador com o arquivo de treinamento e parâmetros
     * especificados.
     * 
     * @param arquivoTreinamento Arquivo que contém exemplos de treinamento no
     *                           formato esperado pelo classificador
     * @param parametros Parâmetros utilizados no treinamento
     * 
     * @return 0 se o treinamento foi bem sucedido, outra coisa caso contrário
     */
    public abstract int treinar( java.io.File arquivoTreinamento, String parametros );
    
    /**
     * Grava o classificador em um determinado arquivo. Deve ser chamado depois
     * do método treinar().
     * 
     * @param arquivoSaida Arquivo onde o classificador será gravado
     */
    public abstract void gravar( java.io.File arquivoSaida );
    
    /**
     * Classifica uma instância de relação passada como a sentença e os dois
     * termos de interesse.
     * 
     * @param s Sentença que contem os termos de interesse
     * @param t1 Primeiro termo da relação
     * @param t2 Segundo termo da relação
     * @return Classe predita pelo classificador para a instância fornecida
     */
    public abstract String classificar( Sentenca s, Termo t1, Termo t2 );
    
    /**
     * Método de limpeza chamado quando o classificador é trocado ou o programa
     * fecha. Usado para remover arquivos temporários ou liberar outros recursos
     * utilizados.
     */
    public abstract void finalizar();
    
}
