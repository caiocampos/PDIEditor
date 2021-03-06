/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pdieditor;

import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;

/**
 *
 * @author Caio
 */
public class FiltroGaussiano extends javax.swing.JFrame {

    /**
     * Creates new form FiltroGaussiano
     */
    public FiltroGaussiano() {
        initComponents();
    }

    FiltroGaussiano(JanelaPrincipal aThis) {
        initComponents();
        ref = aThis;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titulo = new java.awt.Label();
        deslizanteTamanho = new javax.swing.JSlider();
        textoTamanho = new javax.swing.JLabel();
        valorTamanho = new javax.swing.JLabel();
        deslizanteDistribuicao = new javax.swing.JSlider();
        textoDistribuicao = new javax.swing.JLabel();
        valorDistribuicao = new javax.swing.JLabel();
        cancelar = new javax.swing.JButton();
        ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        titulo.setAlignment(java.awt.Label.CENTER);
        titulo.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        titulo.setText("Filtro Gausiano");

        deslizanteTamanho.setMaximum(64);
        deslizanteTamanho.setMinimum(1);
        deslizanteTamanho.setToolTipText("");
        deslizanteTamanho.setValue(1);
        deslizanteTamanho.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                deslizanteTamanhoStateChanged(evt);
            }
        });

        textoTamanho.setText("Tamanho:");

        valorTamanho.setText("1px");

        deslizanteDistribuicao.setMaximum(2000);
        deslizanteDistribuicao.setMinimum(1);
        deslizanteDistribuicao.setToolTipText("");
        deslizanteDistribuicao.setValue(200);
        deslizanteDistribuicao.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                deslizanteDistribuicaoStateChanged(evt);
            }
        });

        textoDistribuicao.setText("Distribuição:");

        valorDistribuicao.setText("1,00");

        cancelar.setText("Cancelar");
        cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarActionPerformed(evt);
            }
        });

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textoTamanho)
                            .addComponent(textoDistribuicao))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deslizanteDistribuicao, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                            .addComponent(deslizanteTamanho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(valorTamanho, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(valorDistribuicao, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ok)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(valorDistribuicao)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deslizanteTamanho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textoTamanho)
                            .addComponent(valorTamanho))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deslizanteDistribuicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textoDistribuicao))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelar)
                    .addComponent(ok))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarActionPerformed
        dispose();
    }//GEN-LAST:event_cancelarActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        PlanarImage pi = ref.imagemAtual();
        if(pi != null) {
            PlanarImage fi = Util.gauss(pi, tamanho, distribuicao);
            if(fi != null) {
                ref.atualizarImagem(fi);
                dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Não foi possível filtrar a imagem!", "Erro na imagem", JOptionPane.ERROR_MESSAGE);
        dispose();
    }//GEN-LAST:event_okActionPerformed

    private void deslizanteTamanhoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_deslizanteTamanhoStateChanged
        tamanho = deslizanteTamanho.getValue();
        valorTamanho.setText(tamanho + "px");
    }//GEN-LAST:event_deslizanteTamanhoStateChanged

    private void deslizanteDistribuicaoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_deslizanteDistribuicaoStateChanged
        distribuicao = deslizanteDistribuicao.getValue() / 200.0D;
        String s = String.format("%.2f", distribuicao);
        valorDistribuicao.setText(s);
    }//GEN-LAST:event_deslizanteDistribuicaoStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FiltroGaussiano.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
			new FiltroGaussiano().setVisible(true);
		});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelar;
    private javax.swing.JSlider deslizanteDistribuicao;
    private javax.swing.JSlider deslizanteTamanho;
    private javax.swing.JButton ok;
    private javax.swing.JLabel textoDistribuicao;
    private javax.swing.JLabel textoTamanho;
    private java.awt.Label titulo;
    private javax.swing.JLabel valorDistribuicao;
    private javax.swing.JLabel valorTamanho;
    // End of variables declaration//GEN-END:variables
    private double distribuicao = 1d;                   
    private int tamanho = 1;
    private JanelaPrincipal ref = null;
}
