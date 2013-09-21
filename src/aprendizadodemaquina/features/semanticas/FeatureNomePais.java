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
 * Feature que diz se um dos termos contêm o nome de um país.
 * <p>
 * <b>Exemplo</b>: Para o par de termos
 * <blockquote>As [tropas] americanas chegaram ao [Afeganistão]</blockquote>
 * o vetor retornado seria [0, 1] (o primeiro termo não contém nome de país,
 * enquanto o segundo termo contém).
 * <p>
 * Lista de países retirada da Wikipedia:
 * http://pt.wikipedia.org/wiki/Anexo:Lista_de_Estados_soberanos
 */
public class FeatureNomePais extends Feature {
    
    private String regexPaises = "";

    public FeatureNomePais() {
        // Lê os nomes das cidades do arquivo lista_cidades
        Scanner scanner = new Scanner(this.getClass().getResourceAsStream("/recursos/lista_cidades"));

        regexPaises = ".*\\b(";
        boolean flag = false;
        while ( scanner.hasNextLine() ) {
            if (flag)
                regexPaises += "|";
            flag = true;
            regexPaises += scanner.nextLine();
        }
        regexPaises += ")\\b.*";
        
        System.out.println(regexPaises);
    }
    
    @Override
    public String nome() {
        return "termo_contem_nome_de_pais";
    }
    
    @Override
    public List<Object> gerar(Sentenca s, Termo t1, Termo t2) {

        List<Object> valores = new ArrayList<Object>();
        
        String texto = "";
        for ( Token t : Utilidades.tokensTermo(s, t1) )
            texto += t.getToken() + " ";
        texto = texto.toLowerCase().replace("=", " ");
        
        if ( Pattern.matches(regexPaises, texto) ) {
            valores.add(new Integer(1));
            System.out.println("Pais1Termo1 = " + texto + "$");
        }
        else
            valores.add(new Integer(0));

        texto = "";
        for ( Token t : Utilidades.tokensTermo(s, t2) )
            texto += t.getToken() + " ";
        texto = texto.toLowerCase().replace("=", " ");

        if ( Pattern.matches(regexPaises, texto) ) {
            valores.add(new Integer(1));
            System.out.println("Pais1Termo2 = " + texto + "$");
        }
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
