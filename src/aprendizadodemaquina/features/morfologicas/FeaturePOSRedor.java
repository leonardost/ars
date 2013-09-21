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

package aprendizadodemaquina.features.morfologicas;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.Token;
import anotadorderelacoes.model.Utilidades;
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature que traz as etiquetas gramaticais dos tokens que rodeiam os termos de
 * interesse. A janela padrão utilizada é de 3 tokens antes e 3 após cada termo.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>Ouvíamos a [canção] do [pássaro] enquanto fazíamos um piquenique</blockquote>
 * o vetor retornado seria [NAO_EXISTE, V, ART, PREP, N, ADV, ART, N, PREP, ADV,
 * V, ART].
 */
public class FeaturePOSRedor extends Feature {

    private int tamanhoJanela = 3;  // 3 trouxe melhores resultados
    
    @Override
    public String nome() {
        return "etiquetas_pos_ao_redor_dos_termos";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {
        
        List<Object> valores = new ArrayList<Object>();

        Termo[] termos = { t1, t1, t2, t2 };
        int[] sinais = { -1, 1, -1, 1 };
        
        for ( int i = 0; i < 4; ++i ) {
        
            for ( Token token : Utilidades.tokensJanelaTermo( s, termos[i], tamanhoJanela * sinais[i] ) ) {

                String pos = "NAO_EXISTE";
                if ( token != null ) {
                    if ( token.getPos() == null )
                        pos = "PONTUACAO";
                    else
                        pos = Utilidades.classeGramatical( token );
                    // classe gramatical não reconhecida
                    if ( pos == null ) {
                        //TODO: Descomentar depois
                        //System.out.println( s.getTexto() );
                        //System.out.println( "Classe gramatical não reconhecida: " + token );
                        pos = "NAO_EXISTE";
                    }
                }

                valores.add( pos );  // TODO: Ver aqui se isso não pode dar problema, se não é preciso criar uma nova string aqui

            }
            
        }
        
        return valores;

    }

    @Override
    public int quantosValores() {
        return 4 * tamanhoJanela;
    }

    @Override
    public String tipo() {
        return "NOMINAL";
    }

    @Override
    public String[] valoresPossivis() {
        String[] valores = new String[12];
        valores[0] = "N";
        valores[1] = "ADJ";
        valores[2] = "ART";
        valores[3] = "PRON";
        valores[4] = "NUM";
        valores[5] = "V";
        valores[6] = "ADV";
        valores[7] = "PREP";
        valores[8] = "CONJ";
        valores[9] = "INTERJ";
        valores[10] = "PONTUACAO";
        valores[11] = "NAO_EXISTE";
        return valores;
    }
    
}
