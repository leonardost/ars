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

package anotadorderelacoes.ui;

/**
 * Interface de procura de expressões no arquivo aberto atualmente.
 */
public class Localizar extends javax.swing.JDialog {

    private Main main;
    private static Localizar instancia;

    private Localizar( java.awt.Frame parent, boolean modal ) {
        super( parent, modal );
        main = (Main)parent;
        initComponents();
        setLocationRelativeTo( null );
    }
    
    /**
     * "Fábrica" de instâncias.
     * Impede que mais que uma instancia da janela de busca seja criada.
     */
    public static Localizar novaInstancia ( java.awt.Frame parent ) {
        if ( instancia == null ) {
            instancia = new Localizar( parent, false );
            return instancia;
        }
        else
            return instancia;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtLocalizar = new javax.swing.JTextField();
        btnProximo = new javax.swing.JButton();
        Anterior = new javax.swing.JButton();
        chkSensivel = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Localizar...");

        jLabel1.setText("Expressão a ser localizada:");

        txtLocalizar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLocalizarKeyPressed(evt);
            }
        });

        btnProximo.setText("Proximo");
        btnProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProximoActionPerformed(evt);
            }
        });

        Anterior.setText("Anterior");
        Anterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnteriorActionPerformed(evt);
            }
        });

        chkSensivel.setText("Busca sensível à caixa");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocalizar)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(chkSensivel))
                        .addGap(0, 106, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Anterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnProximo)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLocalizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSensivel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Anterior)
                    .addComponent(btnProximo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProximoActionPerformed
        localizarExpressao( 1 );
    }//GEN-LAST:event_btnProximoActionPerformed
    
    private void txtLocalizarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocalizarKeyPressed
        if ( evt.getKeyCode() == evt.VK_ENTER )
            localizarExpressao( 1 );
        else if ( evt.getKeyCode() == evt.VK_ESCAPE ) {
            setVisible( false );
            dispose();
        }
    }//GEN-LAST:event_txtLocalizarKeyPressed

    private void AnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnteriorActionPerformed
        localizarExpressao( -1 );
    }//GEN-LAST:event_AnteriorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Anterior;
    private javax.swing.JButton btnProximo;
    private javax.swing.JCheckBox chkSensivel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField txtLocalizar;
    // End of variables declaration//GEN-END:variables

    public void focarCaixa() {
        txtLocalizar.requestFocusInWindow();
    }

    /**
     * Realiza a busca de uma expressão nas sentenças do arquivo atualmente
     * aberto.
     * 
     * @param direcao 1 se a busca é feita em frente ou -1 caso contrário
     */
    private void localizarExpressao ( int direcao ) {
        // Se a caixa só conter espaços, a busca não é realizada
        if ( txtLocalizar.getText().matches( "^\\s*$" ) ) {
            txtLocalizar.requestFocusInWindow();
            return;
        }
        main.localizar( txtLocalizar.getText(), direcao, chkSensivel.isSelected() );
        txtLocalizar.selectAll();
    }
    
}
