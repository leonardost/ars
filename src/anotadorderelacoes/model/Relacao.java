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

package anotadorderelacoes.model;

/**
 * Representa uma relação binária entre dois termos na sentença
 * <p>
 * Campos:
 * <ul>
 * <li>String relacao : qual relação está sendo definida
 * <li>Termo termo1   : primeiro termo da relação
 * <li>Termo termo2   : segundo termo da relação
 * </ul>
 * Em formato JSON:
 * <ul>
 * <li>r  : qual relação semântica está sendo definida
 * <li>t1 : índice do primeiro termo da relação
 * <li>t2 : indice do segundo termo da relação
 * </ul>
 * 
 * @see Termo
 * @see Sentenca
 */
public class Relacao implements Comparable<Relacao> {

    private String relacao;
    private Termo termo1;
    private Termo termo2;

    public Relacao() {
        this.relacao = "";
    }

    public Relacao( String relacao, Termo termo1, Termo termo2 ) {
        this.relacao = relacao;
        this.termo1 = termo1;    
        this.termo2 = termo2;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters e setters">
    public String getRelacao() {
        return relacao;
    }
    public Termo getTermo1() {
        return termo1;
    }
    public void setTermo1( Termo t ) {
        termo1 = t;
    }
    public Termo getTermo2() {
        return termo2;
    }
    public void setTermo2( Termo t ) {
        termo2 = t;
    }
    //</editor-fold>
    
    /**
     * Inverte a ordem desta relação (termo1 se torna termo2 e vice-versa)
     */
    public void inverter() {
        Termo t1 = termo1;
        Termo t2 = termo2;
        termo1 = t2;
        termo2 = t1;
    }
    
    @Override
    public String toString() {
        return relacao + "(" + termo1 + "," + termo2 + ")";
    }
    
    /**
     * Instâncias de relações são ordenadas pela posição de início do termo1
     * 
     * @param r Objeto com o qual se quer comparar
     * @return
     */
    @Override
    public int compareTo( Relacao r ) {
        return this.getTermo1().getDe() - r.getTermo1().getDe();
    }
    
    /**
     * Uma instância de relação é igual a outra se a relação é a mesma e os
     * termos que a compõem são os mesmos
     * 
     * @param o Objeto com o qual se quer comparar
     * @return Verdadeiro se são iguais, falso caso contrário
     */
    @Override
    public boolean equals( Object o ) {

        if ( !( o instanceof Relacao ) )
            return false;
        
        Relacao r = (Relacao)o;

        if ( this.relacao.equals( r.getRelacao() ) )
            if ( this.termo1.equals( r.getTermo1() ) && this.termo2.equals( r.getTermo2() ) ||
                    this.termo2.equals( r.getTermo2() ) && this.termo1.equals( r.getTermo1() ) )
                return true;

        return false;
        
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + ( this.relacao != null ? this.relacao.hashCode() : 0 );
        hash = 67 * hash + ( this.termo1 != null ? this.termo1.hashCode() : 0 );
        hash = 67 * hash + ( this.termo2 != null ? this.termo2.hashCode() : 0 );
        return hash;
    }
    
}
