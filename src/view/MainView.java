package br.com.teahub.view;

import javax.swing.*;
import java.awt.*;

/*
 - Classe principal (MainView) que atua como contêiner central após a autenticação.
 
   Esta classe gerencia a navegação global do sistema utilizando uma estrutura 
   de layout composta:
   - BorderLayout: Para fixar a barra de navegação no topo.
   - CardLayout: Para alternar entre as visões de conteúdo (Pacientes, Atendimentos, Conta).
 
   Funcionalidades:
   - Centraliza o acesso aos módulos principais.
   - Implementa tratamento de exceções (fallback) caso um módulo falhe ao carregar.
   - Aplica identidade visual consistente em toda a navegação.
 
   Arquitetura:
   - Funciona como uma "janela-mãe" (Parent View) que instacia os sub-módulos.
*/
public class MainView extends JFrame {

    // ── Gerenciadores de conteúdo ──
    private JPanel painelConteudo; 
    private CardLayout cardLayout; 
    
    // ── Identidade visual ──
    private final Color COR_PRIMARIA  = new Color(0, 128, 128);  
    private final Color COR_DESTAQUE  = new Color(88, 44, 131);  
    private final Color COR_BRANCO    = Color.WHITE;

    /*
     - Construtor principal.
       Define as dimensões, layout e inicializa a árvore de componentes da interface.
     */
    public MainView() {
        setTitle("TEAHub - Painel Clínico");
        setSize(1280, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Adiciona a barra de navegação superior
        add(criarBarraSuperior(), BorderLayout.NORTH);

        // Inicializa o gerenciador de visões (CardLayout)
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);

        // Registro e isolamento das sub-telas
        try {
            painelConteudo.add(new CadastroPacienteView(), "PACIENTES");
        } catch (Exception e) {
            System.err.println("[Erro MainView] Falha ao carregar subview de Pacientes: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            painelConteudo.add(new AtendimentoView(), "ATENDIMENTOS");
        } catch (Exception e) {
            System.err.println("[Erro MainView] Falha ao carregar subview de Atendimentos: " + e.getMessage());
            // Painel de fallback (Segurança em tempo de execução)
            JPanel painelErro = new JPanel(new GridBagLayout());
            painelErro.add(new JLabel("Erro ao conectar com o banco de dados na aba Atendimentos."));
            painelConteudo.add(painelErro, "ATENDIMENTOS");
        }

        try {
            painelConteudo.add(new ContaView(), "CONTA");
        } catch (Exception e) {
            System.err.println("[Erro MainView] Falha ao carregar subview de Conta: " + e.getMessage());
        }

        add(painelConteudo, BorderLayout.CENTER);
        
        // Define o estado inicial da aplicação
        cardLayout.show(painelConteudo, "PACIENTES");
    }

    /*
     - Cria e organiza a barra de navegação superior (NavBar).
     
     @return JPanel com os botões de navegação.
     */
    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(COR_BRANCO);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 220))); 
        barra.setPreferredSize(new Dimension(1280, 65));

        // ── Painel Esquerdo: Branding e Navegação principal ──
        JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelEsquerdo.setBackground(COR_BRANCO);

        try {
            java.net.URL imgURL = getClass().getResource("/br/com/teahub/resources/logo.png");
            if (imgURL != null) {
                ImageIcon logoOriginal = new ImageIcon(imgURL);
                Image logoRedimensionada = logoOriginal.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                painelEsquerdo.add(new JLabel(new ImageIcon(logoRedimensionada)));
            }
        } catch (Exception e) {
            System.out.println("Logo não encontrada na barra superior: " + e.getMessage());
        }

        JButton btnPacientes = criarBotaoMenu("Pacientes", COR_PRIMARIA);
        JButton btnAtendimentos = criarBotaoMenu("Atendimentos", new Color(100, 100, 110));

        // Eventos de troca de contexto (CardLayout)
        btnPacientes.addActionListener(e -> {
            cardLayout.show(painelConteudo, "PACIENTES");
            btnPacientes.setForeground(COR_PRIMARIA);
            btnAtendimentos.setForeground(new Color(100, 100, 110));
        });

        btnAtendimentos.addActionListener(e -> {
            cardLayout.show(painelConteudo, "ATENDIMENTOS");
            btnAtendimentos.setForeground(COR_PRIMARIA);
            btnPacientes.setForeground(new Color(100, 100, 110));
        });

        painelEsquerdo.add(btnPacientes);
        painelEsquerdo.add(btnAtendimentos);
        barra.add(painelEsquerdo, BorderLayout.WEST);

        // ── Painel Direito: Ações de conta ──
        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        painelDireito.setBackground(COR_BRANCO);

        JButton btnConta = criarBotaoMenu("Minha Conta", COR_DESTAQUE);
        btnConta.addActionListener(e -> {
            cardLayout.show(painelConteudo, "CONTA");
            btnPacientes.setForeground(new Color(100, 100, 110));
            btnAtendimentos.setForeground(new Color(100, 100, 110));
        });

        painelDireito.add(btnConta);
        barra.add(painelDireito, BorderLayout.EAST);

        return barra;
    }

    /*
     - Método utilitário para padronização visual dos botões de menu.
     
     @param texto Rótulo do botão.
     @param corTexto Cor aplicada ao texto do botão.
     @return JButton configurado com o estilo do sistema.
     */
    private JButton criarBotaoMenu(String texto, Color corTexto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(corTexto);
        btn.setBackground(COR_BRANCO);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainView().setVisible(true));
    }
}