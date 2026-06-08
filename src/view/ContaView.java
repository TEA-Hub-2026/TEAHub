package br.com.teahub.view;
import br.com.teahub.dao.ProfissionalDAO;
import br.com.teahub.model.Profissional;
import br.com.teahub.util.Sessao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/*
 - Tela de perfil do profissional autenticado no sistema TEAHub.
 
  Permite visualizar, editar e excluir a própria conta.
  Segue o padrão MVC na camada View: exibe dados recebidos do DAO
  e delega operações de banco ao ProfissionalDAO.
 
 Funcionalidades:
  - Exibição dos dados do profissional logado (carregados da sessão)
  - Edição de nome, e-mail, especialidade, registro e senha
  - Badge visual indicando se o profissional está Ativo ou Inativo
  - Mensagem de feedback que desaparece automaticamente após 3 segundos
  - Exclusão de conta com confirmação
 
 Pilares de POO aplicados:
  - Encapsulamento: atributos privados, lógica em métodos separados
  - Sobrescrita (@Override): paintComponent customizado para o badge e o círculo
  - Composição: usa ProfissionalDAO e Sessao como dependências internas
 */
public class ContaView extends JPanel {

    // ── Campos editáveis do formulário ──     
    private JTextField txtNome, txtEmail, txtEspecialidade, txtRegistro;
    private JPasswordField txtSenha; // Campo de senha com caracteres mascarados
    
    // ── Componentes de feedback visual ──   
    private JLabel lblStatus; // Mensagem de sucesso ou erro (some após 3s)
    private JLabel lblBadgeStatus; // Badge colorido: verde = Ativo, vermelho = Inativo 
    private Timer timerStatus;  // Temporizador que apaga a mensagem após 3 segundos

    // ── Dependências (camada de dados e sessão) ──
    private final ProfissionalDAO dao = new ProfissionalDAO();

   // ── Paleta de cores do sistema TEAHub ──
    private final Color COR_PRIMARIA  = new Color(0, 128, 128); // Teal (cor principal)
    private final Color COR_VERMELHO  = new Color(180, 50, 50); // Vermelho (erro/inativo)
    private final Color COR_VERDE     = new Color(34, 150, 80); // Verde (sucesso/ativo)
    private final Color COR_FUNDO     = new Color(245, 245, 250); // Cinza Claro (fundo da tela)
    private final Color COR_BRANCO    = Color.WHITE;
    private final Color COR_BORDA     = new Color(200, 200, 220); // Cinza suave para bordas

    /*
     - Construtor da tela.
      Inicializa o layout, monta o cartão de perfil e carrega os dados do profissional logado.
     */
    public ContaView() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // Painel principal centralizado com scroll para suportar telas menores
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0; g.weightx = 1;

        painelPrincipal.add(criarCartaoPerfil(), g);

        // Scroll permite rolar a tela caso o conteúdo ultrapasse a altura da janela
        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        carregarDados(); // Preenche os campos com dados do profissional logado
    }

    /*
     - Contrói o cartão visual do perfil com todas as seções:
       cabeçalho, informações pessoais, segurança e botões de ação
       
       @return JPanel com o cartão de perfil montado 
     */
    private JPanel criarCartaoPerfil() {
        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(COR_BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 10, 10, 10);
        g.weightx = 1;

        // ── Cabeçalho: circulo com inicial + título + badge de status ──
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        cabecalho.setBackground(COR_BRANCO);

        /*
         - Componente visual personalizado: círculo teal com a inicial do nome.
          Usa sobrescrita de paintComponent() para desenhar o círculo manualmente,
          demonstrando polimorfismo de método (override) da POO.
         */
        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g2) {
                super.paintComponent(g2);
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(COR_PRIMARIA);
                g.fillOval(0, 0, 70, 70); // Desenha o círculo de fundo
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g.getFontMetrics();
                // Extrai e centraliza a inicial do nome do profissional
                String inicial = (txtNome != null && !txtNome.getText().isBlank())
                        ? String.valueOf(txtNome.getText().charAt(0)).toUpperCase() : "P";
                g.drawString(inicial, (70 - fm.stringWidth(inicial)) / 2,
                        (70 - fm.getHeight()) / 2 + fm.getAscent());
            }
        };
        circulo.setPreferredSize(new Dimension(70, 70));
        circulo.setOpaque(false);

        // Painel vertical com titulo, subtitulo e badge de status
        JPanel infoHeader = new JPanel();
        infoHeader.setLayout(new BoxLayout(infoHeader, BoxLayout.Y_AXIS));
        infoHeader.setBackground(COR_BRANCO);

        JLabel lblTitulo = new JLabel("Perfil do Profissional");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(COR_PRIMARIA);

        JLabel lblSub = new JLabel("Gerencie suas informacoes pessoais e de acesso");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(120, 120, 140));

        /*
         - Badge de status com bordas arredondadas.
         Usa sobrescrita de paintComponent() para desenhar o fundo arredondado,
         pois o JLabel padrão não suporta esse efeito nativo.
         A cor do badge é definida dinamicamente em carregarDados().
         */
        lblBadgeStatus = new JLabel("  Profissional Ativo  ") {
            @Override
            protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getBackground()); // Usa a cor definida externamente (verde ou vermelho)
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bordas arredondadas
                super.paintComponent(g2);
            }
        };
        lblBadgeStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadgeStatus.setForeground(Color.WHITE);
        lblBadgeStatus.setBackground(COR_VERDE); // Começa como Ativo; atualizando ao carregar
        lblBadgeStatus.setOpaque(false);
        lblBadgeStatus.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        infoHeader.add(lblTitulo);
        infoHeader.add(Box.createVerticalStrut(4));
        infoHeader.add(lblSub);
        infoHeader.add(Box.createVerticalStrut(8));
        infoHeader.add(lblBadgeStatus);

        cabecalho.add(circulo);
        cabecalho.add(infoHeader);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        cartao.add(cabecalho, g);

        // Linha separadora visual entre cabeçalho e campos
        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        g.gridy = 1; g.insets = new Insets(10, 10, 20, 10);
        cartao.add(sep, g);

        // ── Seção: Informações Pessoais ──
        g.insets = new Insets(10, 10, 8, 10);
        JLabel lblSecao1 = new JLabel("Informacoes Pessoais");
        lblSecao1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecao1.setForeground(new Color(60, 60, 80));
        g.gridy = 2; g.gridwidth = 4;
        cartao.add(lblSecao1, g);

        // Campos em grade de 2 colunas para melhor aproveitamento do espaço
        g.gridwidth = 2; g.insets = new Insets(6, 10, 6, 10);

        g.gridx = 0; g.gridy = 3;
        cartao.add(criarLabel("Nome completo"), g);
        g.gridx = 2;
        cartao.add(criarLabel("E-mail"), g);

        txtNome  = criarCampo(true);
        txtEmail = criarCampo(true);

        g.gridx = 0; g.gridy = 4;
        cartao.add(txtNome, g);
        g.gridx = 2;
        cartao.add(txtEmail, g);

        g.gridx = 0; g.gridy = 5;
        cartao.add(criarLabel("Especialidade"), g);
        g.gridx = 2;
        cartao.add(criarLabel("Registro Profissional"), g);

        txtEspecialidade = criarCampo(true);
        txtRegistro      = criarCampo(true);

        g.gridx = 0; g.gridy = 6;
        cartao.add(txtEspecialidade, g);
        g.gridx = 2;
        cartao.add(txtRegistro, g);

        // ── Seção: Segurança ──
        g.gridx = 0; g.gridy = 7; g.gridwidth = 4;
        g.insets = new Insets(25, 10, 8, 10);
        JLabel lblSecao2 = new JLabel("Seguranca");
        lblSecao2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecao2.setForeground(new Color(60, 60, 80));
        cartao.add(lblSecao2, g);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(COR_BORDA);
        g.gridy = 8; g.insets = new Insets(0, 10, 15, 10);
        cartao.add(sep2, g);

        // Campo de senha: permite alterar sem exibir o valor atual (segrança)
        g.gridx = 0; g.gridy = 9; g.gridwidth = 2;
        g.insets = new Insets(6, 10, 6, 10);
        cartao.add(criarLabel("Nova senha de acesso"), g);

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setPreferredSize(new Dimension(0, 38));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        g.gridy = 10;
        cartao.add(txtSenha, g);

        JLabel lblDica = new JLabel("Ao digitar e atualizar sua senha mudará.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(new Color(150, 150, 170));
        g.gridy = 11;
        cartao.add(lblDica, g);

        // ── Label de feedback e botões de ação ──
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridx = 0; g.gridy = 12; g.gridwidth = 4;
        g.insets = new Insets(20, 10, 5, 10);
        cartao.add(lblStatus, g);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        painelBotoes.setBackground(COR_BRANCO);

        JButton btnAtualizar = criarBotao("Salvar alteracoes", COR_PRIMARIA);
        JButton btnDeletar   = criarBotao("Deletar conta",     COR_VERMELHO);

        btnAtualizar.addActionListener(e -> atualizar()); // Aciona o método de atualização
        btnDeletar.addActionListener(e -> deletar()); // Aciona o método de exclusão

        // Deletar fica à esquerda do Salvar para evitar cliques acidentais (princípio IHC)
        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnAtualizar);

        g.gridy = 13; g.insets = new Insets(10, 10, 0, 10);
        cartao.add(painelBotoes, g);

        return cartao;
    }

    /*
     - Carrega os dados do profissional logado (via Sessao) e preenche os campos da tela.
       Também atualiza o badge de status (Ativo/Inativo) com a cor correspondente.
     */
    private void carregarDados() {
        lblStatus.setText(" "); // Limpa mensagens anteriores ao recarregar
        
        int id = Sessao.getIdLogado();
        
        // Verifica se há um profissional autenticado na sessão
        if (id == -1) {
            mostrarStatus("Nenhum profissional logado.", COR_VERMELHO, false);
            return;
        }
        
        // Busca os dados completos no banco pelo ID da sessão
        Profissional p = dao.buscarPorId(id);
        if (p != null) {
            // Preenche cada campo com o dado correspondente do objeto recuperado
            txtNome.setText(p.getNome());
            txtEmail.setText(p.getEmail());
            txtEspecialidade.setText(p.getEspecialidade());
            txtRegistro.setText(p.getRegistroProfissional());
            txtSenha.setText(""); // Senha nunca é exibida por segurança

            // Atualiza o badge de status com a cor e texto corretos
            if (p.isStatusAtivo()) {
                lblBadgeStatus.setText("  Profissional Ativo  ");
                lblBadgeStatus.setBackground(COR_VERDE);
            } else {
                lblBadgeStatus.setText("  Profissional Inativo  ");
                lblBadgeStatus.setBackground(COR_VERMELHO);
            }
            lblBadgeStatus.repaint(); // Força o redesenho do badge com a nova cor
        } else {
            mostrarStatus("Nao foi possivel carregar os dados.", COR_VERMELHO, false);
        }
    }

    /*
     - Valida os campos obrigatórios e atualiza os dados do profissional no banco.
     
      Recupera o objeto Profissional da sessão para preservar campos
      que não aparecem na tela (como a Clínica), evitando sobrescrevê-los com null.
      Após salvar, atualiza o objeto na sessão para manter a consistência.
     */
    private void atualizar() {
    // Valida se os campos obrigatórios estão preenchidos
    if (txtNome.getText().isBlank() || txtEmail.getText().isBlank()
            || txtEspecialidade.getText().isBlank()) {
        mostrarStatus("Preencha todos os campos obrigatórios.", COR_VERMELHO, true);
        return;
    }

    // 1. Recupera o objeto da sessão para não perder dados como a Clínica vinculada
    Profissional p = Sessao.getLogado();
    
    if (p == null) {
        mostrarStatus("Erro: Sessão expirada.", COR_VERMELHO, true);
        return;
    }

    // 2. Aplica apenas os campos alteráveis ao objeto da sessão
    p.setNome(txtNome.getText().trim());
    p.setEmail(txtEmail.getText().trim());
    p.setEspecialidade(txtEspecialidade.getText().trim());
    p.setRegistroProfissional(txtRegistro.getText().trim());
    
    // Atualiza a senha somente se o campo não estiver vazio
    String novaSenha = new String(txtSenha.getPassword()).trim();
    if (!novaSenha.isBlank()) {
        p.setSenhaLogin(novaSenha);
    }

    // 3. Persiste as alterações no banco via DAO
    if (dao.atualizar(p)) {
        txtSenha.setText(""); // Limpa o campo de senha após salvar
        Sessao.setLogado(p); // Mantém a sessão sincronizada com os dados atualizados
        mostrarStatus("Dados atualizados com sucesso!", COR_VERDE, true);
    } else {
        mostrarStatus("Erro ao atualizar os dados.", COR_VERMELHO, true);
    }
}

    /*
     - Exibe uma mensagem de feedback na tela.
      Se autoSumir for true, a mensagem desaparece automaticamente após 3 segundos
      usando um javax.swing.Timer (executado na thread de eventos do Swing).
     
      @param msg       Texto da mensagem a exibir
      @param cor       Cor do texto (COR_VERDE para sucesso, COR_VERMELHO para erro)
      @param autoSumir true para apagar automaticamente após 3 segundos
     */
    private void mostrarStatus(String msg, Color cor, boolean autoSumir) {
        lblStatus.setText(msg);
        lblStatus.setForeground(cor);
        
        // Cancela um timer anterior se ainda estiver rodando, evitando conflitos
        if (timerStatus != null && timerStatus.isRunning()) timerStatus.stop();
        
        if (autoSumir) {
            // Cria um timer de disparo único (setRepeats = false) que limpa a mensagem
            timerStatus = new Timer(3000, (ActionEvent e) -> lblStatus.setText(" "));
            timerStatus.setRepeats(false);
            timerStatus.start();
        }
    }

    /*
     - Solicita confirmação e exclui a conta do profissional logado
      Após a exclusão bem-sucedida, encerra o sistema completamente
     */
    private void deletar() {
        int confirma = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja deletar sua conta?\nEssa acao nao pode ser desfeita.",
                "Confirmar exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirma == JOptionPane.YES_OPTION) {
            if (dao.excluir(Sessao.getIdLogado())) {
                JOptionPane.showMessageDialog(this, "Conta desativada. O sistema sera encerrado.");
                System.exit(0); // Encerra o sistema após deletar a conta
            } else {
                mostrarStatus("Erro ao deletar a conta.", COR_VERMELHO, true);
            }
        }
    }

    /*
     - Método público chamado pela MainView toda vez que o usuário navega para esta aba
     
      Garante que os dados exibidos estejam sempre atualizados e 
      que mensagens de uma visista anterior não permaneçam visíveis
     */
    public void recarregar() {
        carregarDados();
    }
    
   // ── Métodos auxiliares de construção de componentes ──

    /* Cria um JLabel de rótulo com estilo padrão da tela */
    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(80, 80, 100));
        return label;
    }

    /*
     - Cria um JTextField estilizado.
      Se editavel=false, o campo fica bloqueado e com fundo acinzentado,
      indicando visualmente que aquela informação não pode ser alterada.
     */
    private JTextField criarCampo(boolean editavel) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setEditable(editavel);
        tf.setBackground(editavel ? COR_BRANCO : new Color(235, 235, 240));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return tf;
    }

    /* Cria um JButton estilizado com a cor de fundo especificada */
    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return btn;
    }
}