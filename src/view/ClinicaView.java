package br.com.teahub.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/*
 - Tela do Menu Principal da Clínica (Administrador).
 
   Esta classe serve como o painel central de navegação para o gestor da clínica, 
   exibindo os módulos disponíveis no ecossistema TEAHub.
 
   Funcionalidades:
   - Dashboard de navegação com estrutura em Cards para módulos do sistema.
   - Layout responsivo usando ScrollPane e GridBagLayout para adaptação de tela.
   - Integração de marca (branding) com carregamento dinâmico de logo.
   - Acesso seguro para encerramento de sessão.
 
   Pilares de POO aplicados:
   - Encapsulamento: definição de constantes de estilo e encapsulamento de 
     componentes em métodos privados.
   - Composição: uso de sub-painéis para compor uma interface complexa e modular.
*/
public class ClinicaView extends JFrame {

    // ── Paleta de cores do sistema TEAHub ──
    private final Color COR_FUNDO       = new Color(245, 245, 250);
    private final Color COR_BRANCO      = Color.WHITE;
    private final Color COR_PRIMARIA    = new Color(0, 128, 128);
    private final Color COR_BOTOES      = new Color(26, 43, 73);
    private final Color COR_BORDA       = new Color(220, 220, 220);
    private final Color COR_SUBTITULO   = new Color(120, 120, 140);

    /*
     - Construtor da tela de Clínica.
       Inicializa a janela principal, organiza os painéis de navegação, 
       cabeçalho e os cards de módulos.
     */
    public ClinicaView() {
        setTitle("TEAHub - Menu Principal");
        setSize(1100, 650);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(COR_FUNDO);

        // ── Barra superior: Logo e branding ──
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(COR_BRANCO);
        topo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelLogo.setBackground(COR_BRANCO);

        JLabel lblIcone = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/br/com/teahub/resources/logo.png.jpeg");
            if (imgURL != null) {
                ImageIcon icone = new ImageIcon(imgURL);
                Image scaled = icone.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                lblIcone.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) {
            System.out.println("Erro ao carregar logo: " + ex.getMessage());
        }

        JLabel lblTitulo = new JLabel("TEAHub");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(COR_PRIMARIA);

        painelLogo.add(lblIcone);
        painelLogo.add(lblTitulo);
        topo.add(painelLogo, BorderLayout.WEST);

        // ── Painel central: Conteúdo e Scroll ──
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(COR_FUNDO);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(COR_FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        // Cabeçalho da área de conteúdo
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBackground(COR_FUNDO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel lblTituloMenu = new JLabel("Menu Clínica");
        lblTituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTituloMenu.setForeground(COR_BOTOES);
        lblTituloMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Selecione um módulo para continuar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(COR_SUBTITULO);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        cabecalho.add(lblTituloMenu);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(lblSub);

        // Cards de módulos
        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setBackground(COR_FUNDO);

        String[][] modulos = {
            {"Pacientes",      "Cadastre, visualize e gerencie informações dos pacientes."},
            {"Atendimentos",   "Agende e acompanhe os atendimentos realizados."},
            {"Jogos",          "Atividades lúdicas de apoio terapêutico."},
            {"Relatórios",     "Visualize e exporte relatórios clínicos."},
            {"Configurações",  "Gerencie as preferências do sistema."}
        };

        for (int i = 0; i < modulos.length; i++) {
            cards.add(criarCard(modulos[i][0], modulos[i][1]));
            if (i < modulos.length - 1) {
                cards.add(Box.createVerticalStrut(10));
            }
        }

        conteudo.add(cabecalho, BorderLayout.NORTH);
        conteudo.add(cards, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 80, 0, 80);
        wrapper.add(conteudo, gbc);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(COR_FUNDO);
        scroll.getViewport().setBackground(COR_FUNDO);

        // ── Rodapé: Ação de saída ──
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 12));
        rodape.setBackground(COR_BRANCO);
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA));

        JButton btnSair = new JButton("Sair do Sistema");
        btnSair.setBackground(COR_BOTOES);
        btnSair.setForeground(COR_BRANCO);
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSair.setFocusPainted(false);
        btnSair.setBorderPainted(false);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btnSair.addActionListener(e -> System.exit(0));

        rodape.add(btnSair);

        tela.add(topo, BorderLayout.NORTH);
        tela.add(scroll, BorderLayout.CENTER);
        tela.add(rodape, BorderLayout.SOUTH);

        add(tela);
    }

    /*
     - Cria um componente visual de "Card" para representar os módulos do sistema.
     
     @param titulo Título do módulo.
     @param descricao Pequena descrição da função do módulo.
     @return JPanel estilizado no padrão de cards do sistema.
     */
    private JPanel criarCard(String titulo, String descricao) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COR_BRANCO);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)));

        JPanel texto = new JPanel();
        texto.setBackground(COR_BRANCO);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COR_BOTOES);

        JLabel lblDescricao = new JLabel(descricao);
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescricao.setForeground(COR_SUBTITULO);

        texto.add(lblTitulo);
        texto.add(Box.createVerticalStrut(4));
        texto.add(lblDescricao);

        card.add(texto, BorderLayout.CENTER);

        JLabel lblBreve = new JLabel("Disponível em breve");
        lblBreve.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBreve.setForeground(COR_SUBTITULO);
        lblBreve.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        card.add(lblBreve, BorderLayout.EAST);

        return card;
    }
}
