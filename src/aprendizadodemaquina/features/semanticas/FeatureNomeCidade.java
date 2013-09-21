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

package aprendizadodemaquina.features.semanticas;

import anotadorderelacoes.model.Sentenca;
import anotadorderelacoes.model.Termo;
import anotadorderelacoes.model.Token;
import anotadorderelacoes.model.Utilidades;
import aprendizadodemaquina.Feature;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Feature que diz se um dos termos contêm o nome de uma cidade brasileira.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>O número de [cidades] localizadas em [São Paulo] é...</blockquote>
 * o vetor retornado seria [0, 1] (o primeiro termo não contém um nome de
 * estado, enquanto o segundo termo contém).
 * Dados retirados de http://www.fabioricotta.com/mysql/lista-de-cidades-brasileiras-banco-em-sql.html .
 * <p>
 */
public class FeatureNomeCidade extends Feature {

    String regexCidades;
    
    public FeatureNomeCidade() {

        // Lê os nomes das cidades do arquivo lista_cidades
        Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/recursos/lista_cidades"));

        regexCidades = ".*\\b(";
        boolean flag = false;
        while ( scanner.hasNextLine() ) {
            if (flag)
                regexCidades += "|";
            flag = true;
            regexCidades += scanner.nextLine();
        }
        regexCidades += ")\\b.*";
        
        System.out.println(regexCidades);
        
    }
    
    @Override
    public String nome() {
        return "termo_contem_nome_de_cidade";
    }

    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {

        List<Object> valores = new ArrayList<Object>();
        
        String texto = "";
        for ( Token t : Utilidades.tokensTermo(s, t1) )
            texto += t.getToken() + " ";
        texto = texto.replace("=", " ");
        
        if ( Pattern.matches(regexCidades, texto) )
            valores.add(new Integer(1));
        else
            valores.add(new Integer(0));

        texto = "";
        for ( Token t : Utilidades.tokensTermo(s, t2) )
            texto += t.getToken() + " ";
        texto = texto.replace("=", " ");

        if ( Pattern.matches(regexCidades, texto) )
            valores.add(new Integer(1));
        else
            valores.add(new Integer(0));

        return valores;
        
    }

    @Override
    public int quantosValores() {
        return 2;
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
