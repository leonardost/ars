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

import org.json.simple.JSONObject;

/**
 * Representa um termo de interesse na sentença.
 * <p>
 * Um termo é uma sequência de tokens, então só precisa guardar as posições do
 * token de início e do token final.
 * <p>
 * O termo em si é guardado na classe em formato de texto por questões de
 * conveniência
 * <p>
 * Em formato JSON:
 * <ul>
 * <li>de  : posição do token de início do termo
 * <li>ate : posição do token de fim do termo
 * </ul>
 * 
 * @see Relacao
 * @see Token
 */
public class Termo implements Comparable<Termo> {
    
    private Integer de;
    private Integer ate;
    private String termo;

    //<editor-fold defaultstate="collapsed" desc="Getters e setters">
    public void setDe( int d ) {
        de = d;
    }
    public int getDe() {
        return de;
    }
    public void setAte( int a ) {
        ate = a;
    }
    public int getAte() {
        return ate;
    }
    //</editor-fold>
    
    public Termo( int de, int ate, String termo ) {
        this.de = de;
        this.ate = ate;
        this.termo = termo;
    }
    
    public Termo( Termo t ) {
        this( t.de, t.ate, t.termo );
    }

    @Override
    public String toString() {
        return termo;
    }
    
    @Override
    public int compareTo( Termo t ) {
        return this.de - t.de;
    }

    // Pegadinha! O objeto Integer até 127 eh imutavel, por isso esta
    // verificação de igualdade dava errada para termos com índice alto.
    // Sempre use o método .equals para comparar objetos, mesmo os que fazem
    // autoboxing (Integer, Byte, etc).
    @Override
    public boolean equals( Object o ) {
        if ( ! ( o instanceof Termo ) )
            return false;
        Termo t = (Termo) o;
        if ( this.de.equals( t.de ) && this.ate.equals( t.ate ) )
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.de;
        hash = 23 * hash + this.ate;
        return hash;
    }
    
    /**
     * Retorna uma representação em formato JSON deste termo
     * 
     * @return Objeto JSONObject que representa este termo
     */
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put( "de", de );
        o.put( "ate", ate );
        return o;
    }
    
}
