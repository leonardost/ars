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
import org.json.simple.JSONValue;

/**
 * Representa um token na sentença.
 * <p>
 * Em formato JSON um objeto Token é formado pelos seguintes campos:
 * <ul>
 * <li>t   : a forma superficial do token, o token em si
 * <li>l   : forma lematizada do token
 * <li>pos : marcações de part-of-speech
 * <li>sin : marcações sintáticas
 * </ul>
 * <p>
 * A posição do token não precisa ser armazenada porque os tokens são guardados
 * como um array, que é uma estrutura ordenada.
 * 
 * @see Relacao
 * @see Sentenca
 */
public class Token {
    
    private String token;
    private String lema;
    private String pos;
    private String sintaxe;

    //<editor-fold defaultstate="collapsed" desc="Getters e setters">
    public String getLema() {
        return lema;
    }
    public void setLema( String lema ) {
        this.lema = lema;
    }
    public String getPos() {
        return pos;
    }
    public void setPos( String pos ) {
        this.pos = pos;
    }
    public String getSintaxe() {
        return sintaxe;
    }
    public void setSintaxe( String sintaxe ) {
        this.sintaxe = sintaxe;
    }
    public String getToken() {
        return token;
    }
    public void setToken( String token ) {
        this.token = token;
    }
    //</editor-fold>
    
    public Token() {
        token = "";
        lema = "";
        pos = "";
        sintaxe = "";
    }
    
    /**
     * Cria um novo token a partir de uma representação no formato JSON
     * 
     * @param tokenJson Representação de um token no formato JSON
     */
    public Token( String tokenJson ) {
        JSONObject json = (JSONObject)JSONValue.parse( tokenJson );
        token = (String)json.get( "t" );
        lema = (String)json.get( "l" );
        pos = (String)json.get( "pos" );
        sintaxe = (String)json.get( "sin" );
    }
    
    /**
     * Retorna a representação do token no formato JSON
     * 
     * @return Objeto JSONObject que representa este token
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put( "t", token );
        json.put( "l", lema );
        json.put( "pos", pos );
        json.put( "sin", sintaxe );
        return json;
    }

    @Override
    public String toString() {
        return "TOKEN(" + token + ")";
    }
    
}
