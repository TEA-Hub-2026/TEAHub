package teahub;

import br.com.teahub.view.CadastroView; 
import javax.swing.UIManager;

/*
 - Classe de ponto de entrada (Main) do sistema TEAHub.
 Responsável por configurar o Look and Feel (L&F) da aplicação e inicializar 
 o ciclo de vida da interface gráfica na Event Dispatch Thread (EDT).
 */
public class TEAHub {

    /*
     - Método principal de inicialização da aplicação.
     Configura o tema visual (Look and Feel) e garante a thread-safety 
     na inicialização dos componentes visuais.
     */
    public static void main(String[] args) {
        
        // Tenta aplicar o Look and Feel para uma melhor experiência visual (FlatLaf)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("FlatLaf Light".equals(info.getName())) { 
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Caso o tema customizado falhe, recorre ao padrão do SO para manter a usabilidade
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[Erro TEAHub] Falha ao definir Look and Feel: " + e.getMessage());
            }
        }

        /* - A criação e exibição de janelas Swing deve ocorrer na Event Dispatch Thread (EDT).
         O uso do invokeLater garante que a interface seja construída de forma 
         sincronizada, evitando erros de concorrência ou bloqueios no sistema.
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Instancia a view inicial e centraliza a janela no monitor do usuário
                CadastroView telaCadastro = new CadastroView();
                telaCadastro.setLocationRelativeTo(null);
                telaCadastro.setVisible(true);
            }
        });
    }
}