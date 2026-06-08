package br.com.teahub.view;

import br.com.teahub.dao.AtendimentoDAO;
import br.com.teahub.dao.PacienteDAO;
import br.com.teahub.model.Atendimento;
import br.com.teahub.model.Paciente;
import br.com.teahub.model.Profissional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe de visão responsável pela interface de gerenciamento de atendimentos.
 * Utiliza CardLayout para alternar entre Listagem, Formulário e Detalhes.
 */
public class AtendimentoView extends JPanel {

    // Gerenciador de layout para alternância de telas
    private final CardLayout cardLayout;
    private final JPanel painelCards;
    private JPanel painelContainerCards;

    // Componentes de busca e armazenamento temporário
    private JTextField txtPesquisa;
    private List<Atendimento> listaAtendimentosBanco = new ArrayList<>();

    // Componentes do formulário
    private JComboBox<Paciente> cbPaciente;
    private JComboBox<Profissional> cbProfissional;
    private JTextArea txtObservacoes;
    private JLabel lblDataHoraReal;
    private Timer timerRelogio;

    // Componentes de detalhes
    private JLabel lblDetTitulo, lblDetSub;
    private JLabel lblDet_Paciente, lblDet_Profissional, lblDet_Data, lblDet_Hora;
    private JTextArea txtDet_Observacoes;
    private Atendimento atendimentoEmExibicao = null;

    // Botões de ação do CRUD
    private JButton btnEditar = new JButton("Editar");
    private JButton btnDeletar = new JButton("Excluir");

    // Instância do DAO e formatadores de data
    private final AtendimentoDAO dao = new AtendimentoDAO();
    private final int idClinica = 1;
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter fmtCompleto = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Paleta de cores da interface
    private final Color COR_PRIMARIA   = new Color(0, 128, 128);
    private final Color COR_FUNDO      = new Color(245, 245, 250);
    private final Color COR_BRANCO     = Color.WHITE;
    private final Color COR_BORDA      = new Color(200, 200, 220);
    private final Color COR_VERDE      = new Color(34, 150, 80);
    private final Color COR_CINZA      = new Color(120, 120, 140);
    private final Color COR_VOLTAR     = new Color(110, 120, 135);
    private final Color COR_DELETAR    = new Color(180, 50, 50);

    public AtendimentoView() {
        // Configuração inicial do painel principal
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        cardLayout = new CardLayout();
        painelCards = new JPanel(cardLayout);
        painelCards.setBackground(COR_FUNDO);

        // Adiciona as "cartas" (telas) ao gerenciador de layout
        painelCards.add(criarPainelListagem(), "LISTA");
        painelCards.add(criarPainelFormulario(), "FORMULARIO");
        painelCards.add(criarPainelDetalhes(), "DETALHES");

        add(painelCards, BorderLayout.CENTER);
        atualizarListaAtendimentos();
    }

    /*
     * Cria o painel de listagem contendo a barra de busca e o scroll dos cards.
     */
    private JPanel criarPainelListagem() {
        JPanel painel = new JPanel(new BorderLayout(0, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel barraSuperior = new JPanel(new GridBagLayout());
        barraSuperior.setBackground(COR_FUNDO);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;

        // Configuração da barra de busca com efeito de placeholder
        txtPesquisa = new JTextField("Pesquisar por paciente...");
        txtPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPesquisa.setForeground(COR_CINZA);
        txtPesquisa.setPreferredSize(new Dimension(0, 40));
        txtPesquisa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        
        txtPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtPesquisa.getText().equals("Pesquisar por paciente...")) {
                    txtPesquisa.setText("");
                    txtPesquisa.setForeground(new Color(50, 50, 60));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtPesquisa.getText().trim().isEmpty()) {
                    txtPesquisa.setText("Pesquisar por paciente...");
                    txtPesquisa.setForeground(COR_CINZA);
                }
            }
        });
        txtPesquisa.addCaretListener(e -> filtrarAtendimentosLocalmente());

        JButton btnNovo = new JButton("+ Novo Atendimento");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNovo.setBackground(COR_PRIMARIA);
        btnNovo.setForeground(COR_BRANCO);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorderPainted(false);
        btnNovo.setPreferredSize(new Dimension(180, 40));
        btnNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> prepararNovoAtendimento());

        g.gridx = 0; g.gridy = 0; g.weightx = 0.85; g.insets = new Insets(0, 0, 0, 15);
        barraSuperior.add(txtPesquisa, g);
        g.gridx = 1; g.weightx = 0.15; g.insets = new Insets(0, 0, 0, 0);
        barraSuperior.add(btnNovo, g);

        painel.add(barraSuperior, BorderLayout.NORTH);

        // Painel para exibir os cartões de cada atendimento individualmente
        painelContainerCards = new JPanel();
        painelContainerCards.setLayout(new BoxLayout(painelContainerCards, BoxLayout.Y_AXIS));
        painelContainerCards.setBackground(COR_FUNDO);

        JScrollPane scrollCards = new JScrollPane(painelContainerCards);
        scrollCards.setBorder(null);
        scrollCards.getViewport().setBackground(COR_FUNDO);
        scrollCards.getVerticalScrollBar().setUnitIncrement(16);

        painel.add(scrollCards, BorderLayout.CENTER);
        return painel;
    }

    /*
     * Busca os dados no banco e atualiza a listagem visual.
     */
    private void atualizarListaAtendimentos() {
        try {
            listaAtendimentosBanco = dao.listar(idClinica, "");
        } catch (Exception e) {
            listaAtendimentosBanco = new ArrayList<>();
        }
        renderizarCardsAtendimentos(listaAtendimentosBanco);
    }

    /*
     * Remove todos os cards atuais e recria com base na lista fornecida.
     */
    private void renderizarCardsAtendimentos(List<Atendimento> lista) {
        painelContainerCards.removeAll();

        if (lista == null || lista.isEmpty()) {
            JPanel painelVazio = new JPanel();
            painelVazio.setBackground(COR_FUNDO);
            painelVazio.add(new JLabel("Nenhum atendimento registrado."));
            painelContainerCards.add(painelVazio);
        } else {
            for (Atendimento a : lista) {
                painelContainerCards.add(criarCardItemAtendimento(a));
                painelContainerCards.add(Box.createVerticalStrut(10));
            }
        }
        painelContainerCards.revalidate();
        painelContainerCards.repaint();
    }

    /*
     * Cria o componente visual (card) de um único atendimento para listagem.
     */
    private JPanel criarCardItemAtendimento(Atendimento a) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(COR_BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        String nomePaciente = (a.getPaciente() != null) ? a.getPaciente().getNomePaciente() : "Desconhecido";
        String nomeProfissional = (a.getProfissional() != null) ? a.getProfissional().getNome() : "Não informado";
        String dataFormatada = (a.getDataAtendimento() != null) ? a.getDataAtendimento().format(fmtData) : "--/--/----";

        JPanel painelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        painelInfo.setBackground(COR_BRANCO);

        JLabel lblPaciente = new JLabel(nomePaciente);
        lblPaciente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPaciente.setForeground(new Color(50, 50, 60));

        JLabel lblProf = new JLabel("<html><font color='#78788C'>Profissional:</font> " + nomeProfissional + "</html>");
        lblProf.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblData = new JLabel("<html><font color='#78788C'>Data:</font> " + dataFormatada + "</html>");
        lblData.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        painelInfo.add(lblPaciente);
        painelInfo.add(lblProf);
        painelInfo.add(lblData);

        JButton btnVerMais = new JButton("Ver Atendimento");
        btnVerMais.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerMais.setForeground(COR_PRIMARIA);
        btnVerMais.setBackground(COR_BRANCO);
        btnVerMais.setBorder(BorderFactory.createLineBorder(COR_PRIMARIA, 1));
        btnVerMais.setFocusPainted(false);
        btnVerMais.setPreferredSize(new Dimension(140, 35));
        btnVerMais.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerMais.addActionListener(e -> exibirDetalhesAtendimento(a));

        card.add(painelInfo, BorderLayout.CENTER);
        card.add(btnVerMais, BorderLayout.EAST);

        return card;
    }

    /*
     * Filtra a lista já carregada em memória (sem nova consulta ao banco) pelo nome do paciente.
     */
    private void filtrarAtendimentosLocalmente() {
        String termo = txtPesquisa.getText().toLowerCase().trim();
        if (termo.isEmpty() || termo.equals("pesquisar por paciente...")) {
            renderizarCardsAtendimentos(listaAtendimentosBanco);
            return;
        }
        List<Atendimento> filtrados = new ArrayList<>();
        for (Atendimento a : listaAtendimentosBanco) {
            if (a.getPaciente() != null && a.getPaciente().getNomePaciente().toLowerCase().contains(termo)) {
                filtrados.add(a);
            }
        }
        renderizarCardsAtendimentos(filtrados);
    }

    /*
     * Cria o layout do formulário para inclusão/edição de atendimentos.
     */
    private JScrollPane criarPainelFormulario() {
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(COR_BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(35, 50, 35, 50)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        cabecalho.setBackground(COR_BRANCO);

        // Tenta carregar logo da aplicação
        try {
            java.net.URL url = getClass().getResource("/br/com/teahub/resources/logo.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                cabecalho.add(new JLabel(new ImageIcon(img)));
            }
        } catch (Exception e) { System.out.println("Logo não encontrada."); }

        JPanel txtCabecalho = new JPanel(new GridLayout(2, 1, 0, 3));
        txtCabecalho.setBackground(COR_BRANCO);
        JLabel lblTituloForm = new JLabel("Registrar Evolução de Atendimento");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloForm.setForeground(COR_PRIMARIA);
        
        JLabel lblSub = new JLabel("Insira as informações clínicas colhidas durante a sessão");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COR_CINZA);
        
        txtCabecalho.add(lblTituloForm);
        txtCabecalho.add(lblSub);
        cabecalho.add(txtCabecalho);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; g.insets = new Insets(0, 0, 15, 0);
        cartao.add(cabecalho, g);

        // Painel para exibir data/hora em tempo real
        JPanel painelRelogioDestaque = new JPanel(new BorderLayout());
        painelRelogioDestaque.setBackground(new Color(240, 248, 248));
        painelRelogioDestaque.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 215, 215), 1),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        
        lblDataHoraReal = new JLabel("Data e Hora em tempo real: --/--/---- 00:00:00");
        lblDataHoraReal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDataHoraReal.setForeground(COR_PRIMARIA);
        lblDataHoraReal.setHorizontalAlignment(SwingConstants.CENTER);
        painelRelogioDestaque.add(lblDataHoraReal, BorderLayout.CENTER);

        g.gridy = 2; g.insets = new Insets(0, 0, 20, 0);
        cartao.add(painelRelogioDestaque, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        g.gridy = 3; g.insets = new Insets(0, 0, 20, 0);
        cartao.add(sep, g);

        g.gridy = 4; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(criarTituloSecao("Vínculo do Atendimento"), g);

        g.gridy = 5; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Paciente Vinculado *"), g);
        
        cbPaciente = new JComboBox<>();
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbPaciente.setPreferredSize(new Dimension(0, 40));
        
        // Renderizador customizado para exibir o nome do paciente no ComboBox
        cbPaciente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Paciente) {
                    setText(((Paciente) value).getNomePaciente());
                }
                return this;
            }
        });
        
        g.gridy = 6; g.insets = new Insets(0, 0, 25, 0);
        cartao.add(cbPaciente, g);

        g.gridy = 7; g.insets = new Insets(5, 0, 10, 0);
        cartao.add(criarTituloSecao("Evolução Clínica"), g);

        g.gridy = 8; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Observações e Condutas Clínicas *"), g);
        
        txtObservacoes = criarAreaTexto("Relate detalhadamente a evolução do paciente durante o atendimento...");
        txtObservacoes.setRows(12); 
        
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        scrollObs.getVerticalScrollBar().setUnitIncrement(12);
        
        g.gridy = 9; g.insets = new Insets(0, 0, 30, 0);
        cartao.add(scrollObs, g);

        // Botões de ação do formulário
        JPanel painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBackground(COR_BRANCO);

        JButton btnCancelar = new JButton("Voltar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setBackground(COR_VOLTAR);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(120, 42));
        btnCancelar.addActionListener(e -> cancelarFormulario());

        JButton btnSalvar = new JButton("Salvar Atendimento");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setBackground(COR_VERDE);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setPreferredSize(new Dimension(210, 48));
        btnSalvar.addActionListener(e -> revisarESalvarAtendimento());

        painelBotoes.add(btnCancelar, BorderLayout.WEST);
        painelBotoes.add(btnSalvar, BorderLayout.EAST);

        g.gridy = 10; g.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoes, g);

        GridBagConstraints gP = new GridBagConstraints();
        gP.fill = GridBagConstraints.HORIZONTAL; gP.gridx = 0; gP.gridy = 0; gP.weightx = 1;
        painelPrincipal.add(cartao, gP);

        JScrollPane scrollPrincipal = new JScrollPane(painelPrincipal);
        scrollPrincipal.setBorder(null);
        return scrollPrincipal;
    }

    /*
     * Cria o layout de visualização detalhada de um atendimento selecionado.
     */
    private JScrollPane criarPainelDetalhes() {
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(COR_BRANCO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(35, 50, 35, 50)));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        cabecalho.setBackground(COR_BRANCO);

        try {
            java.net.URL url = getClass().getResource("/br/com/teahub/resources/logo.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                cabecalho.add(new JLabel(new ImageIcon(img)));
            }
        } catch (Exception e) { System.out.println("Logo não encontrada."); }

        JPanel txtCabecalho = new JPanel(new GridLayout(2, 1, 0, 3));
        txtCabecalho.setBackground(COR_BRANCO);
        lblDetTitulo = new JLabel("Histórico de Atendimento");
        lblDetTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDetTitulo.setForeground(COR_PRIMARIA);
        lblDetSub = new JLabel("Dados consolidados do registro em prontuário");
        lblDetSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetSub.setForeground(COR_CINZA);
        txtCabecalho.add(lblDetTitulo);
        txtCabecalho.add(lblDetSub);
        cabecalho.add(txtCabecalho);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(cabecalho, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        g.gridy = 1; g.insets = new Insets(0, 0, 20, 0);
        cartao.add(sep, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(criarTituloSecao("Informações do Atendimento"), g);

        JPanel painelGridExibicao = new JPanel(new GridBagLayout());
        painelGridExibicao.setBackground(COR_BRANCO);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.5;
        gc.insets = new Insets(6, 0, 6, 12);

        lblDet_Paciente = new JLabel("Paciente: ");
        lblDet_Data = new JLabel("Data do Registro: ");
        lblDet_Hora = new JLabel("Horário de Geração: ");

        JLabel[] labelsDet = {lblDet_Paciente, lblDet_Data, lblDet_Hora};
        for (JLabel label : labelsDet) { label.setFont(new Font("Segoe UI", Font.PLAIN, 14)); }

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; painelGridExibicao.add(lblDet_Paciente, gc);
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1; gc.insets = new Insets(6, 0, 12, 12); painelGridExibicao.add(lblDet_Data, gc);
        gc.gridx = 1; gc.gridy = 1; gc.insets = new Insets(6, 12, 12, 0); painelGridExibicao.add(lblDet_Hora, gc);

        g.gridy = 3; g.gridwidth = 4; g.insets = new Insets(0, 0, 12, 0);
        cartao.add(painelGridExibicao, g);

        g.gridy = 4; g.insets = new Insets(10, 0, 10, 0);
        cartao.add(criarTituloSecao("Observações Clínicas Armazenadas"), g);

        g.gridy = 5; g.insets = new Insets(6, 0, 3, 0);
        txtDet_Observacoes = new JTextArea(10, 0);
        txtDet_Observacoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDet_Observacoes.setEditable(false);
        txtDet_Observacoes.setLineWrap(true);
        txtDet_Observacoes.setWrapStyleWord(true);
        txtDet_Observacoes.setBackground(new Color(248, 248, 252));
        txtDet_Observacoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        g.gridy = 6; g.insets = new Insets(0, 0, 25, 0);
        cartao.add(new JScrollPane(txtDet_Observacoes), g);

        // Painel para botões de ação (editar/excluir/voltar)
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoesAcao.setBackground(COR_BRANCO);

        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setBackground(COR_PRIMARIA);
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setPreferredSize(new Dimension(140, 45));
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.addActionListener(e -> prepararEdicaoAtendimento());

        btnDeletar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setBackground(COR_DELETAR);
        btnDeletar.setFocusPainted(false);
        btnDeletar.setBorderPainted(false);
        btnDeletar.setPreferredSize(new Dimension(140, 45));
        btnDeletar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeletar.addActionListener(e -> executarExclusaoAtendimento());

        JButton btnVoltarLista = new JButton("Voltar");
        btnVoltarLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltarLista.setForeground(Color.WHITE);
        btnVoltarLista.setBackground(COR_VOLTAR);
        btnVoltarLista.setFocusPainted(false);
        btnVoltarLista.setBorderPainted(false);
        btnVoltarLista.setPreferredSize(new Dimension(120, 45));
        btnVoltarLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltarLista.addActionListener(e -> voltarParaLista());

        painelBotoesAcao.add(btnVoltarLista);
        painelBotoesAcao.add(btnEditar);
        painelBotoesAcao.add(btnDeletar);

        g.gridy = 7; g.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoesAcao, g);

        GridBagConstraints gP = new GridBagConstraints();
        gP.fill = GridBagConstraints.HORIZONTAL; gP.gridx = 0; gP.gridy = 0; gP.weightx = 1;
        painelPrincipal.add(cartao, gP);

        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.setBorder(null);
        return scroll;
    }

    /*
     * Inicia o timer que atualiza o relógio no topo do formulário.
     */
    private void iniciarRelogio() {
        if (timerRelogio != null && timerRelogio.isRunning()) return;
        timerRelogio = new Timer(1000, e -> {
            lblDataHoraReal.setText("Data e Hora em tempo real: " + LocalDateTime.now().format(fmtCompleto));
        });
        timerRelogio.start();
    }

    private void pararRelogio() {
        if (timerRelogio != null) {
            timerRelogio.stop();
        }
    }

    private void prepararNovoAtendimento() {
        carregarCombos();
        txtObservacoes.setText("");
        iniciarRelogio();
        cardLayout.show(painelCards, "FORMULARIO");
    }

    /*
     * Carrega a lista de pacientes no ComboBox via DAO.
     */
    private void carregarCombos() {
        cbPaciente.removeAllItems();
        try {
            List<Paciente> lista = new br.com.teahub.dao.PacienteDAO().listarTodosParaSeletor();
            if (lista == null || lista.isEmpty()) {
                System.out.println("[TEAHUB] Nenhum paciente retornado.");
            } else {
                for (Paciente p : lista) {
                    cbPaciente.addItem(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Preenche os campos do painel de detalhes com o objeto selecionado.
     */
    private void exibirDetalhesAtendimento(Atendimento a) {
        atendimentoEmExibicao = a;
        lblDetTitulo.setText("Atendimento - " + a.getPaciente().getNomePaciente());
        lblDet_Paciente.setText("<html><font color='#78788C'>Paciente:</font> " + a.getPaciente().getNomePaciente() + "</html>");
        lblDet_Data.setText("<html><font color='#78788C'>Data do Registro:</font> " + a.getDataAtendimento().format(fmtData) + "</html>");
        lblDet_Hora.setText("<html><font color='#78788C'>Horário de Geração:</font> " + a.getDataAtendimento().format(fmtHora) + "</html>");
        txtDet_Observacoes.setText(a.getObservacoes());
        cardLayout.show(painelCards, "DETALHES");
    }

    /*
     * Executa a lógica de exclusão no banco após confirmação do usuário.
     */
    private void executarExclusaoAtendimento() {
        if (atendimentoEmExibicao == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este registro de atendimento?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.excluir(atendimentoEmExibicao.getIdAtendimento())) {
                JOptionPane.showMessageDialog(this, "Registro excluído com sucesso.");
                voltarParaLista();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir atendimento.");
            }
        }
    }

    /*
     * Prepara o formulário para editar um registro existente.
     */
    private void prepararEdicaoAtendimento() {
        if (atendimentoEmExibicao == null) return;
        carregarCombos();
        cbPaciente.setSelectedItem(atendimentoEmExibicao.getPaciente());
        txtObservacoes.setText(atendimentoEmExibicao.getObservacoes());
        cardLayout.show(painelCards, "FORMULARIO");
    }

    /*
     * Valida e salva o atendimento (seja novo ou edição).
     */
    private void revisarESalvarAtendimento() {
        if (cbPaciente.getSelectedItem() == null || txtObservacoes.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Por favor, corrija os problemas de preenchimento.", "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Paciente p = (Paciente) cbPaciente.getSelectedItem();
        LocalDateTime momentoGrava = LocalDateTime.now();
        Atendimento a = (atendimentoEmExibicao != null) ? atendimentoEmExibicao : new Atendimento();
        a.setPaciente(p);
        a.setObservacoes(txtObservacoes.getText().trim());
        a.setDataAtendimento(momentoGrava);
        
        if (dao.cadastrar(a)) {
            JOptionPane.showMessageDialog(this, "Atendimento gravado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltarParaLista();
        } else {
            JOptionPane.showMessageDialog(this, "Erro operacional ao armazenar atendimento.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Cancela a operação atual e limpa campos, solicitando confirmação se houver dados digitados.
     */
    private void cancelarFormulario() {
        if (!txtObservacoes.getText().isBlank()) {
            int escolha = JOptionPane.showConfirmDialog(this, "Os dados não serão atualizados. Deseja voltar?", "Confirmar Retorno", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (escolha != JOptionPane.YES_OPTION) return;
        }
        voltarParaLista();
    }

    private void voltarParaLista() {
        pararRelogio();
        atualizarListaAtendimentos();
        cardLayout.show(painelCards, "LISTA");
    }

    // Métodos auxiliares para criação de componentes visuais padronizados
    private JLabel criarTituloSecao(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(60, 60, 80));
        return lbl;
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COR_CINZA);
        return lbl;
    }

    /*
     * Cria JTextArea com funcionalidade de placeholder customizado.
     */
    private JTextArea criarAreaTexto(String placeholder) {
        JTextArea ta = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(180, 180, 200));
                    g.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    g.drawString(placeholder, 10, 20);
                }
            }
        };
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return ta;
    }
}