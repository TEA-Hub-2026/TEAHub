package br.com.teahub.view;

import br.com.teahub.config.ConexaoBanco; 
import br.com.teahub.dao.ProfissionalDAO;
import br.com.teahub.model.Profissional;
import br.com.teahub.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 - Classe de interface para autenticação de usuários (Clínica ou Profissional) no sistema.
 
   Esta tela centraliza o controle de acesso, validando credenciais contra o 
   banco de dados e estabelecendo a sessão do usuário.
 
   Funcionalidades:
   - Interface de login com duas colunas (Branding / Formulário).
   - Validação diferenciada: via ProfissionalDAO para profissionais e 
     consulta direta via PreparedStatement para administradores de clínicas.
   - Gerenciamento de sessão global através da classe Sessao.
   - Feedback visual imediato em caso de erro de credenciais.
 
   Pilares de POO aplicados:
   - Encapsulamento: campos de entrada (JPasswordField, JTextField) são privados.
   - Composição: uso de componentes Swing para criar uma interface de acesso seguro.
*/
public class LoginView extends JFrame {

    // ── Componentes de interface do formulário ──
    private JLabel lblUsuario, lblSenha, lblTitulo;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JCheckBox chkMostrarSenha;
    private JButton btnEntrar; 

    // ── Paleta de cores do sistema TEAHub ──
    private final Color COR_FUNDO_ESQUERDO = new Color(247, 247, 247);
    private final Color COR_FUNDO_DIREITO  = new Color(218, 233, 247); 
    private final Color COR_BOTOES         = new Color(26, 43, 73);     
    private final Color COR_TEXTO_PADRAO   = new Color(40, 50, 70);
    private final Color COR_TEAL           = new Color(0, 128, 128);    
    private final Color COR_BRANCO         = Color.WHITE;

    /*
     - Construtor da tela de Login.
       Inicializa o layout, monta os painéis laterais e configura os ouvintes de eventos.
     */
    public LoginView() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setTitle("TEAHub - Conectando Cuidado & Jogo");
        setSize(1100, 650); 
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2));
        painelPrincipal.add(criarPainelEsquerdo());
        painelPrincipal.add(criarPainelDireito());

        add(painelPrincipal);
        configurarEventos();
    }

    /*
     - Cria o painel esquerdo para exibição de logo ou branding.
     
     @return JPanel decorativo.
     */
    private JPanel criarPainelEsquerdo() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_FUNDO_ESQUERDO);
        painel.setLayout(new GridBagLayout()); 

        JLabel lblIlustracao = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/br/com/teahub/resources/logo.png.jpeg");
            if (imgURL != null) {
                ImageIcon icone = new ImageIcon(imgURL);
                Image img = icone.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                lblIlustracao.setIcon(new ImageIcon(img));
            } else {
                lblIlustracao.setText("TEAHub");
                lblIlustracao.setFont(new Font("Segoe UI", Font.BOLD, 32));
                lblIlustracao.setForeground(COR_TEAL);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem no Login: " + e.getMessage());
        }
        
        lblIlustracao.setBackground(COR_FUNDO_ESQUERDO);
        lblIlustracao.setOpaque(true);
        
        painel.add(lblIlustracao);
        return painel;
    }

    /*
     - Cria o painel direito contendo os campos de entrada e botões de ação.
     
     @return JPanel com formulário de autenticação.
     */
    private JPanel criarPainelDireito() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_FUNDO_DIREITO);
        painel.setLayout(null);

        lblTitulo = new JLabel("Faça seu login");
        lblTitulo.setBounds(50, 80, 450, 45);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36)); 
        lblTitulo.setForeground(COR_BOTOES);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblTitulo);

        int x = 50, largura = 450, altura = 40;

        // Campo de identificação do usuário
        lblUsuario = new JLabel("E-mail ou CNPJ:");
        lblUsuario.setBounds(x, 180, largura, 20);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(COR_TEXTO_PADRAO);
        painel.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(x, 200, largura, altura);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtUsuario);

        // Campo de senha
        lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(x, 260, largura, 20);
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSenha.setForeground(COR_TEXTO_PADRAO);
        painel.add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(x, 280, largura, altura);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtSenha);

        chkMostrarSenha = new JCheckBox("Mostrar Senha");
        chkMostrarSenha.setBounds(x, 325, 150, 20);
        chkMostrarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrarSenha.setForeground(COR_TEXTO_PADRAO);
        chkMostrarSenha.setOpaque(false);
        painel.add(chkMostrarSenha);

        // Botão principal de login
        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(175, 385, 200, 45); 
        btnEntrar.setBackground(COR_BOTOES);
        btnEntrar.setForeground(COR_BRANCO);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(btnEntrar);

        // Link de navegação para cadastro
        JButton btnIrParaCadastro = new JButton("Não tem conta? Cadastre-se");
        btnIrParaCadastro.setBounds(50, 450, 450, 25); 
        btnIrParaCadastro.setForeground(COR_TEXTO_PADRAO);
        btnIrParaCadastro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnIrParaCadastro.setContentAreaFilled(false);
        btnIrParaCadastro.setBorderPainted(false);
        btnIrParaCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(btnIrParaCadastro);

        btnIrParaCadastro.addActionListener(e -> {
            new CadastroView().setVisible(true); 
            this.dispose(); 
        });

        return painel;
    }

    /*
     - Configura a escuta de eventos (Listeners) para interatividade da interface.
     */
    private void configurarEventos() {
        chkMostrarSenha.addActionListener(e -> {
            if (chkMostrarSenha.isSelected()) {
                txtSenha.setEchoChar((char) 0);
            } else {
                txtSenha.setEchoChar('•');
            }
        });

        btnEntrar.addActionListener(e -> executarLogin());
    }

    /*
     - Processa a tentativa de autenticação.
       Verifica o perfil do usuário (Profissional via DAO ou Clínica via SQL direto)
       e gerencia a transição de estado da sessão.
     */
    private void executarLogin() {
        String usuarioInput = txtUsuario.getText().trim();
        String senhaInput = new String(txtSenha.getPassword()).trim();

        if (usuarioInput.isEmpty() || senhaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o Usuário e a Senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Validação Profissional: Usa o padrão DAO para manter isolamento de dados
        ProfissionalDAO dao = new ProfissionalDAO();
        Profissional p = dao.buscarPorLogin(usuarioInput, senhaInput);

        if (p != null) {
            Sessao.setLogado(p); 
            JOptionPane.showMessageDialog(this, "Login Efetuado como Profissional!");
            abrirTelaPacientes();
            return;
        }

        // 2. Validação Clínica: Consulta direta para acesso administrativo
        String sqlClinica = "SELECT * FROM clinica WHERE usuario_admin = ? AND senha_admin = ?";
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlClinica)) {
                    stmt.setString(1, usuarioInput);
                    stmt.setString(2, senhaInput);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            new ClinicaView().setVisible(true);
                            this.dispose();
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar login: " + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
    }

    /* Abre a visão principal após autenticação bem-sucedida do Profissional. */
    private void abrirTelaPacientes() {
        try {
            new MainView().setVisible(true); 
            this.dispose(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o painel principal: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}