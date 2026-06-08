package br.com.teahub.view;

import br.com.teahub.config.ConexaoBanco; 
import br.com.teahub.dao.ProfissionalDAO;
import br.com.teahub.model.Clinica;
import br.com.teahub.model.Profissional;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException; 

/*
 - Classe de visão (View) responsável pela interface de cadastro de novos usuários.
 
   Esta tela gerencia tanto o registro de Clínicas quanto de Profissionais, 
   ajustando dinamicamente os rótulos e a lógica de persistência conforme a escolha do usuário.
 
   Funcionalidades:
   - Layout de duas colunas (Branding + Formulário).
   - Alternância dinâmica de campos via RadioButtons.
   - Validação de preenchimento obrigatório.
   - Integração com ConexaoBanco (para Clínicas) e ProfissionalDAO (para Profissionais).
 
   Pilares de POO aplicados:
   - Encapsulamento: campos do formulário e paleta de cores são privados.
   - Composição: uso de componentes Swing para formar uma interface complexa.
*/
public class CadastroView extends JFrame {

    // ── Componentes de escolha de tipo de usuário ──
    private JRadioButton radioClinica;
    private JRadioButton radioProfissional;
    private ButtonGroup grupoTipo;

    // ── Campos do formulário dinâmicos ──
    private JLabel lblCampo1, lblCampo2, lblCampo3, lblCampo4, lblSenha;
    private JTextField txtCampo1, txtCampo2, txtCampo3, txtCampo4;
    private JPasswordField txtSenha;
    private JCheckBox chkMostrarSenha; 
    private JButton btnCriarConta;

    // ── Paleta de cores do sistema TEAHub ──
    private final Color COR_FUNDO_ESQUERDO = new Color(247, 247, 247);
    private final Color COR_FUNDO_DIREITO  = new Color(218, 233, 247); 
    private final Color COR_BOTOES         = new Color(26, 43, 73);     
    private final Color COR_TEXTO_PADRAO   = new Color(40, 50, 70);
    private final Color COR_TEAL           = new Color(0, 128, 128);    
    private final Color COR_BRANCO         = Color.WHITE;

    /*
     - Construtor principal.
       Inicializa os componentes visuais, layout e configurações de eventos.
     */
    public CadastroView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setTitle("TEAHub - Crie sua Conta");
        setSize(1100, 650); 
        setLocationRelativeTo(null);
        setResizable(false);

        // Divisão da interface em dois painéis
        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2));
        painelPrincipal.add(criarPainelEsquerdo());
        painelPrincipal.add(criarPainelDireito());

        add(painelPrincipal);
        configurarEventos();
        alternarCampos(); // Define o estado inicial da interface
    }

    /*
     - Cria o painel lateral esquerdo com branding.
     
     @return Painel contendo a logo ou título do sistema.
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
            System.out.println("Erro ao carregar imagem no Cadastro: " + e.getMessage());
        }
        
        lblIlustracao.setBackground(COR_FUNDO_ESQUERDO);
        lblIlustracao.setOpaque(true);
        
        painel.add(lblIlustracao);
        return painel;
    }

    /*
     - Cria o painel direito contendo o formulário de cadastro.
     
     @return Painel do formulário com todos os inputs e botões.
     */
    private JPanel criarPainelDireito() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_FUNDO_DIREITO);
        painel.setLayout(null);

        // Títulos
        JLabel lblTitulo = new JLabel("Seja Bem-Vindo");
        lblTitulo.setBounds(50, 20, 450, 35);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 30)); 
        lblTitulo.setForeground(COR_TEAL);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Crie sua Conta");
        lblSubtitulo.setBounds(50, 55, 450, 25);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubtitulo.setForeground(COR_TEXTO_PADRAO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        painel.add(lblSubtitulo);

        // Seleção de perfil
        radioClinica = new JRadioButton("Clínica (Admin)", true);
        radioClinica.setBounds(110, 90, 160, 25);
        radioClinica.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radioClinica.setForeground(COR_TEXTO_PADRAO);
        radioClinica.setOpaque(false);

        radioProfissional = new JRadioButton("Profissional");
        radioProfissional.setBounds(290, 90, 160, 25);
        radioProfissional.setFont(new Font("Segoe UI", Font.BOLD, 14));
        radioProfissional.setForeground(COR_TEXTO_PADRAO);
        radioProfissional.setOpaque(false);

        grupoTipo = new ButtonGroup();
        grupoTipo.add(radioClinica);
        grupoTipo.add(radioProfissional);
        painel.add(radioClinica);
        painel.add(radioProfissional);

        // Configuração dos campos do formulário (Posicionamento absoluto para controle fino)
        int x = 50, largura = 450, altura = 35;

        lblCampo1 = new JLabel();
        lblCampo1.setBounds(x, 125, largura, 20);
        lblCampo1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCampo1.setForeground(COR_BOTOES);
        painel.add(lblCampo1);

        txtCampo1 = new JTextField();
        txtCampo1.setBounds(x, 145, largura, altura);
        txtCampo1.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtCampo1);

        lblCampo2 = new JLabel();
        lblCampo2.setBounds(x, 190, largura, 20);
        lblCampo2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCampo2.setForeground(COR_BOTOES);
        painel.add(lblCampo2);

        txtCampo2 = new JTextField();
        txtCampo2.setBounds(x, 210, largura, altura);
        txtCampo2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtCampo2);

        lblCampo3 = new JLabel();
        lblCampo3.setBounds(x, 255, largura, 20);
        lblCampo3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCampo3.setForeground(COR_BOTOES);
        painel.add(lblCampo3);

        txtCampo3 = new JTextField();
        txtCampo3.setBounds(x, 275, largura, altura);
        txtCampo3.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtCampo3);

        lblCampo4 = new JLabel();
        lblCampo4.setBounds(x, 320, largura, 20);
        lblCampo4.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCampo4.setForeground(COR_BOTOES);
        painel.add(lblCampo4);

        txtCampo4 = new JTextField();
        txtCampo4.setBounds(x, 340, largura, altura);
        txtCampo4.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtCampo4);

        lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(x, 385, largura, 20);
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSenha.setForeground(COR_BOTOES);
        painel.add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(x, 405, largura, altura);
        txtSenha.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        painel.add(txtSenha);

        chkMostrarSenha = new JCheckBox("Mostrar Senha");
        chkMostrarSenha.setBounds(x, 445, 150, 20);
        chkMostrarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkMostrarSenha.setForeground(COR_TEXTO_PADRAO);
        chkMostrarSenha.setOpaque(false);
        painel.add(chkMostrarSenha);

        btnCriarConta = new JButton("Criar Conta");
        btnCriarConta.setBounds(175, 490, 200, 42); 
        btnCriarConta.setBackground(COR_TEAL);
        btnCriarConta.setForeground(COR_BRANCO);
        btnCriarConta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCriarConta.setFocusPainted(false);
        btnCriarConta.setBorderPainted(false);
        btnCriarConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(btnCriarConta);

        JButton btnJaTenhoConta = new JButton("Já tenho conta? Faça login");
        btnJaTenhoConta.setBounds(50, 550, 450, 20);
        btnJaTenhoConta.setForeground(COR_TEXTO_PADRAO);
        btnJaTenhoConta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnJaTenhoConta.setContentAreaFilled(false);
        btnJaTenhoConta.setBorderPainted(false);
        btnJaTenhoConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(btnJaTenhoConta);

        btnJaTenhoConta.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose();
        });

        return painel;
    }

    /*
     - Vincula os ouvintes de eventos (ActionListeners) aos componentes da interface.
       Gerencia a lógica de exibição de senha e o envio do formulário.
     */
    private void configurarEventos() {
        ActionListener alternarListener = e -> alternarCampos();
        radioClinica.addActionListener(alternarListener);
        radioProfissional.addActionListener(alternarListener);

        chkMostrarSenha.addActionListener(e -> {
            if (chkMostrarSenha.isSelected()) {
                txtSenha.setEchoChar((char) 0); 
            } else {
                txtSenha.setEchoChar('•'); 
            }
        });

        btnCriarConta.addActionListener(e -> executarCadastroCompleto());
    }

    /*
     - Alterna os rótulos (labels) dos campos dinamicamente, permitindo reuso da 
       estrutura visual para entidades Clínica e Profissional.
     */
    private void alternarCampos() {
        limparCampos();
        if (radioClinica.isSelected()) {
            lblCampo1.setText("Nome da Clínica:");
            lblCampo2.setText("E-mail Institucional:");
            lblCampo3.setText("CNPJ:");
            lblCampo4.setText("Telefone Comercial:");
        } else {
            lblCampo1.setText("Nome Completo do Profissional:");
            lblCampo2.setText("E-mail Profissional:");
            lblCampo3.setText("Registro Profissional (Ex: CRM, CRP):");
            lblCampo4.setText("Especialidade Atendida:");
        }
    }

    /* Limpa o formulário após a troca de modo ou após uma submissão. */
    private void limparCampos() {
        txtCampo1.setText(""); txtCampo2.setText("");
        txtCampo3.setText(""); txtCampo4.setText("");
        txtSenha.setText("");
        chkMostrarSenha.setSelected(false);
        txtSenha.setEchoChar('•');
    }

    /*
     - Lógica principal de processamento do formulário.
       Valida dados, decide o tipo de persistência (Direta/DAO) e realiza o cadastro.
     */
    private void executarCadastroCompleto() {
        String c1 = txtCampo1.getText().trim();
        String c2 = txtCampo2.getText().trim();
        String c3 = txtCampo3.getText().trim();
        String c4 = txtCampo4.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (c1.isEmpty() || c2.isEmpty() || c3.isEmpty() || c4.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (radioClinica.isSelected()) {
            String usuarioAutomaticoClinica = c3.replaceAll("[^0-9]", ""); 
            
            String sql = "INSERT INTO clinica (nome_clinica, cnpj, usuario_admin, senha_admin, telefone) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = ConexaoBanco.conectar()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Erro de Conexão com o Banco.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, c1); 
                    stmt.setString(2, c3); 
                    stmt.setString(3, usuarioAutomaticoClinica); 
                    stmt.setString(4, senha); 
                    stmt.setString(5, c4); 
                    
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Clínica criada! Use o seu CNPJ no login.\nUsuário: " + usuarioAutomaticoClinica);
                    
                    new LoginView().setVisible(true);
                    this.dispose();
                }
            } catch (SQLException ex) { 
                JOptionPane.showMessageDialog(this, "Erro ao salvar Clínica: " + ex.getMessage(), "Erro no Banco", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Persistência via DAO para Profissional (boas práticas de isolamento)
            Profissional pro = new Profissional();
            Clinica clinicaProvisoria = new Clinica();
            clinicaProvisoria.setIdClinica(1); 
            
            pro.setClinica(clinicaProvisoria);
            pro.setNome(c1);
            pro.setEmail(c2);
            pro.setUsuarioLogin(c2); 
            pro.setSenhaLogin(senha);   
            pro.setRegistroProfissional(c3);
            pro.setEspecialidade(c4);
            pro.setStatusAtivo(true);

            ProfissionalDAO proDAO = new ProfissionalDAO();
            if (proDAO.cadastrar(pro)) {
                JOptionPane.showMessageDialog(this, "Profissional criado! Use seu E-mail no login.\nUsuário: " + c2);
                new LoginView().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar Profissional.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroView().setVisible(true));
    }
}