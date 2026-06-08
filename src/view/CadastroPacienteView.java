package br.com.teahub.view;

import br.com.teahub.dao.PacienteDAO;
import br.com.teahub.model.Clinica;
import br.com.teahub.model.Paciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/*
 - Tela de gerenciamento de pacientes do sistema TEAHub.
 
 Permite listar, cadastrar, editar e visualizar prontuários de pacientes.
 Segue o padrão de navegação via CardLayout para alternância fluida entre
 listagem, formulário e prontuário sem trocar de janela.
 
 Funcionalidades:
  - Listagem com filtro dinâmico local (busca em tempo real)
  - Formulário com validação de dados e máscara de formato
  - Prontuário para exibição detalhada de informações clínicas
  - Badge visual de Status (Ativo/Inativo) integrado ao card do paciente
 
 Pilares de POO aplicados:
  - Encapsulamento: campos privados com métodos construtores de componentes.
  - Composição: uso de JPanels aninhados (Cartão dentro de ScrollPane) para responsividade.
  - Reutilização: métodos auxiliares para padronização da UI (criarLabel, criarCampo).
 */
public class CadastroPacienteView extends JPanel {
    
    private final CardLayout cardLayout;
    private final JPanel painelCards;
    private JPanel painelContainerCards; 

    // ── Componentes da Listagem ──
    private JTextField txtPesquisa;
    private List<Paciente> listaPacientesBanco = new ArrayList<>();

    // ── Campos do formulário ──
    private JTextField txtNome, txtDataNascimento, txtResponsavel, txtTelefone;
    private JSpinner   spnNivelTea;
    private JTextArea  txtMedicacoes, txtRestricoes;
    private JLabel     lblTituloFormulario;
    private JButton    btnSalvarFormulario;
    private Paciente   pacienteEmEdicao = null;
    private JComboBox<String> cbStatusAtivo;

    // ── Componentes do Prontuário ──
    private JLabel     lblProntuarioTitulo, lblProntuarioSub;
    private JLabel     lblP_Nome, lblP_Nascimento, lblP_Idade, lblP_Responsavel, lblP_Telefone, lblP_Nivel, lblP_Status;
    private JTextArea  txtP_Medicacoes, txtP_Restricoes;
    private Paciente   pacienteEmExibicaoProntuario = null;

    // ── Controle e Configurações ──
    private final PacienteDAO dao = new PacienteDAO();
    private final int idClinica = 1; 
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Paleta de cores do sistema ──
    private final Color COR_PRIMARIA   = new Color(0, 128, 128);
    private final Color COR_FUNDO      = new Color(245, 245, 250);
    private final Color COR_BRANCO     = Color.WHITE;
    private final Color COR_BORDA      = new Color(200, 200, 220);
    private final Color COR_VERDE      = new Color(34, 150, 80);
    private final Color COR_CINZA      = new Color(120, 120, 140);
    private final Color COR_VOLTAR     = new Color(110, 120, 135); 
    private final Color COR_DELETAR    = new Color(180, 50, 50); 

    public CadastroPacienteView() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        // CardLayout central gerencia as três visões principais da tela
        cardLayout = new CardLayout();
        painelCards = new JPanel(cardLayout);
        painelCards.setBackground(COR_FUNDO);

        painelCards.add(criarPainelListagem(), "LISTA");
        painelCards.add(criarPainelFormulario(), "FORMULARIO");
        painelCards.add(criarPainelProntuario(), "PRONTUARIO");

        add(painelCards, BorderLayout.CENTER);
        atualizarListaPacientes();
    }

    /* - Cria o painel de listagem contendo a barra de busca e o container de cards.
     Usa um JScrollPane para permitir rolagem caso o número de pacientes exceda a área visível.
    */
    
    private JPanel criarPainelListagem() {
        JPanel painel = new JPanel(new BorderLayout(0, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel barraSuperior = new JPanel(new GridBagLayout());
        barraSuperior.setBackground(COR_FUNDO);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;

        txtPesquisa = new JTextField("Pesquisar pacientes...");
        txtPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPesquisa.setForeground(COR_CINZA);
        txtPesquisa.setPreferredSize(new Dimension(0, 40));
        txtPesquisa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        
        txtPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtPesquisa.getText().equals("Pesquisar pacientes...")) {
                    txtPesquisa.setText("");
                    txtPesquisa.setForeground(new Color(50, 50, 60));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtPesquisa.getText().trim().isEmpty()) {
                    txtPesquisa.setText("Pesquisar pacientes...");
                    txtPesquisa.setForeground(COR_CINZA);
                }
            }
        });
        txtPesquisa.addCaretListener(e -> filtrarPacientesLocalmente());

        JButton btnNovo = new JButton("+ Adicionar Paciente");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNovo.setBackground(COR_PRIMARIA);
        btnNovo.setForeground(COR_BRANCO);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorderPainted(false);
        btnNovo.setPreferredSize(new Dimension(180, 40));
        btnNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> prepararNovoCadastro());

        g.gridx = 0; g.gridy = 0; g.weightx = 0.85; g.insets = new Insets(0, 0, 0, 15);
        barraSuperior.add(txtPesquisa, g);
        g.gridx = 1; g.weightx = 0.15; g.insets = new Insets(0, 0, 0, 0);
        barraSuperior.add(btnNovo, g);

        painel.add(barraSuperior, BorderLayout.NORTH);

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
     * Sincroniza a lista local de pacientes com a fonte de dados (banco).
     * Utiliza um bloco try-catch para garantir que, em caso de erro na consulta,
     * a lista seja inicializada como vazia, evitando NullPointerException na UI.
     */
    private void atualizarListaPacientes() {
        try {
            listaPacientesBanco = dao.listar(idClinica, "");
        } catch (Exception e) {
            listaPacientesBanco = new ArrayList<>();
        }
        renderizarCardsPacientes(listaPacientesBanco);
    }
    
    /*
     * Limpa o container de exibição e reconstrói os componentes visuais (Cards).
     * @param lista A lista de objetos Paciente a serem renderizados.
     */
    private void renderizarCardsPacientes(List<Paciente> lista) {
        painelContainerCards.removeAll();

        if (lista == null || lista.isEmpty()) {
            JPanel painelVazio = new JPanel();
            painelVazio.setBackground(COR_FUNDO);
            painelVazio.add(new JLabel("Nenhum paciente cadastrado."));
            painelContainerCards.add(painelVazio);
        } else {
            for (Paciente p : lista) {
                painelContainerCards.add(criarCardItemPaciente(p));
                painelContainerCards.add(Box.createVerticalStrut(10));
            }
        }
        painelContainerCards.revalidate();
        painelContainerCards.repaint();
    }
    
    /*
     * Constrói um componente visual (JPanel) individual para cada paciente.
     * @param p Objeto paciente contendo os dados.
     * @return Painel customizado com layout BorderLayout.
     */
    private JPanel criarCardItemPaciente(Paciente p) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(COR_BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        int idade = 0;
        if (p.getDataNascimento() != null) {
            idade = Period.between(p.getDataNascimento(), LocalDate.now()).getYears();
        }

        String responsavel = (p.getResponsavel() == null || p.getResponsavel().isBlank()) ? "Não informado" : p.getResponsavel();
        String status = p.isStatusAtivo() ? "Ativo" : "Inativo";

        JPanel painelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0)); 
        painelInfo.setBackground(COR_BRANCO);

        JLabel lblNomeIdade = new JLabel(p.getNomePaciente() + " - (" + idade + " anos)");
        lblNomeIdade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNomeIdade.setForeground(new Color(50, 50, 60));

        // Aplicação de formatação HTML em JLabel para suporte a cores condicionais
        JLabel lblResp = new JLabel("<html><font color='#78788C'>Responsável:</font> " + responsavel + "</html>");
        lblResp.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblNivel = new JLabel("<html><font color='#78788C'>Nível TEA:</font> " + p.getNivelTea() + "</html>");
        lblNivel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        String corStatus = status.equalsIgnoreCase("Ativo") ? "#229650" : "#B43232";
        JLabel lblStat = new JLabel("<html><font color='#78788C'>Status:</font> <font color='" + corStatus + "'><b>" + status + "</b></font></html>");
        lblStat.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        painelInfo.add(lblNomeIdade);
        painelInfo.add(lblResp);
        painelInfo.add(lblNivel);
        painelInfo.add(lblStat); 

        JButton btnVerProntuario = new JButton("Ver Prontuário");
        btnVerProntuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerProntuario.setForeground(COR_PRIMARIA);
        btnVerProntuario.setBackground(COR_BRANCO);
        btnVerProntuario.setBorder(BorderFactory.createLineBorder(COR_PRIMARIA, 1));
        btnVerProntuario.setFocusPainted(false);
        btnVerProntuario.setPreferredSize(new Dimension(130, 35));
        btnVerProntuario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerProntuario.addActionListener(e -> exibirProntuario(p));

        card.add(painelInfo, BorderLayout.CENTER);
        card.add(btnVerProntuario, BorderLayout.EAST);

        return card;
    }

    /*
     * Realiza o filtro da lista em memória (client-side) baseando-se no texto do campo de busca.
     */
    private void filtrarPacientesLocalmente() {
        String termo = txtPesquisa.getText().toLowerCase().trim();
        if (termo.isEmpty() || termo.equals("pesquisar pacientes...")) {
            renderizarCardsPacientes(listaPacientesBanco);
            return;
        }
        List<Paciente> filtrados = new ArrayList<>();
        for (Paciente p : listaPacientesBanco) {
            if (p.getNomePaciente().toLowerCase().contains(termo)) {
                filtrados.add(p);
            }
        }
        renderizarCardsPacientes(filtrados);
    }

    /*
     * Cria o layout principal do formulário dentro de um JScrollPane.
     * Utiliza GridBagLayout para alinhamento preciso de campos e labels.
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

        try {
            java.net.URL url = getClass().getResource("/br/com/teahub/resources/logo.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                cabecalho.add(new JLabel(new ImageIcon(img)));
            }
        } catch (Exception e) { System.out.println("Logo não encontrada."); }

        JPanel txtCabecalho = new JPanel(new GridLayout(2, 1, 0, 3));
        txtCabecalho.setBackground(COR_BRANCO);
        lblTituloFormulario = new JLabel("Cadastro de Paciente");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloFormulario.setForeground(COR_PRIMARIA);
        JLabel lblSub = new JLabel("Preencha todos os campos obrigatórios (*)");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COR_CINZA);
        txtCabecalho.add(lblTituloFormulario);
        txtCabecalho.add(lblSub);
        cabecalho.add(txtCabecalho);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(cabecalho, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        g.gridy = 1; g.insets = new Insets(0, 0, 20, 0);
        cartao.add(sep, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(criarTituloSecao("Dados do Paciente"), g);

        g.gridy = 3; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Nome completo *"), g);
        txtNome = criarCampo("Ex: Ana Clara Ferreira");
        g.gridy = 4; g.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtNome, g);

        g.gridwidth = 2; g.insets = new Insets(6, 0, 3, 8);
        g.gridx = 0; g.gridy = 5;
        cartao.add(criarLabel("Data de Nascimento * (dd/MM/yyyy)"), g);
        g.gridx = 2; g.insets = new Insets(6, 8, 3, 0);
        cartao.add(criarLabel("Telefone do Responsável *"), g);

        txtDataNascimento = criarCampo("Ex: 15/03/2015");
        txtTelefone       = criarCampo("Ex: (99) 99999-9999");

        g.gridx = 0; g.gridy = 6; g.insets = new Insets(0, 0, 12, 8);
        cartao.add(txtDataNascimento, g);
        g.gridx = 2; g.insets = new Insets(0, 8, 12, 0);
        cartao.add(txtTelefone, g);

        g.gridx = 0; g.gridy = 7; g.insets = new Insets(6, 0, 3, 8);
        cartao.add(criarLabel("Responsável *"), g);
        g.gridx = 2; g.insets = new Insets(6, 8, 3, 0);
        cartao.add(criarLabel("Nível TEA *"), g);

        txtResponsavel = criarCampo("Ex: Maria da Silva");
        
        spnNivelTea = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        spnNivelTea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnNivelTea.setPreferredSize(new Dimension(0, 38));
        spnNivelTea.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1)); 
        JComponent editor = spnNivelTea.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(COR_BRANCO);
        }

        g.gridx = 0; g.gridy = 8; g.insets = new Insets(0, 0, 12, 8);
        cartao.add(txtResponsavel, g);
        g.gridx = 2; g.gridy = 8; g.insets = new Insets(0, 8, 12, 0);
        cartao.add(spnNivelTea, g);

        
        g.gridx = 0; g.gridy = 9; g.insets = new Insets(6, 0, 3, 8);
        cartao.add(criarLabel("Status do Paciente *"), g);
        g.gridx = 2; g.gridy = 9; g.insets = new Insets(0, 8, 3, 0);
        JLabel lblDicaTea = new JLabel("1=Leve  |  2=Substancial  |  3=Muito severo");
        lblDicaTea.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDicaTea.setForeground(COR_CINZA);
        cartao.add(lblDicaTea, g);

        cbStatusAtivo = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        cbStatusAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbStatusAtivo.setPreferredSize(new Dimension(0, 38));
        cbStatusAtivo.setBackground(COR_BRANCO);
        cbStatusAtivo.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1)); 

        g.gridx = 0; g.gridy = 10; g.insets = new Insets(0, 0, 15, 8);
        cartao.add(cbStatusAtivo, g);

        g.gridx = 0; g.gridy = 11; g.gridwidth = 4; g.insets = new Insets(10, 0, 10, 0);
        cartao.add(criarTituloSecao("Informações Clínicas"), g);

        g.gridy = 12; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Medicações em uso (opcional)"), g);
        txtMedicacoes = criarAreaTexto("Descreva os medicamentos em uso, se houver...");
        g.gridy = 13; g.insets = new Insets(0, 0, 12, 0);
        cartao.add(new JScrollPane(txtMedicacoes), g);

        g.gridy = 14; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Restrições alimentares (opcional)"), g);
        txtRestricoes = criarAreaTexto("Descreva restrições alimentares, alergias, intolerâncias...");
        g.gridy = 15; g.insets = new Insets(0, 0, 25, 0);
        cartao.add(new JScrollPane(txtRestricoes), g);

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
        btnCancelar.addActionListener(e -> cancelar());

        btnSalvarFormulario = new JButton("Cadastrar");
        btnSalvarFormulario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvarFormulario.setBackground(COR_VERDE);
        btnSalvarFormulario.setForeground(Color.WHITE);
        btnSalvarFormulario.setFocusPainted(false);
        btnSalvarFormulario.setBorderPainted(false);
        btnSalvarFormulario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvarFormulario.setPreferredSize(new Dimension(210, 48));
        btnSalvarFormulario.addActionListener(e -> revisarECadastrar());

        painelBotoes.add(btnCancelar, BorderLayout.WEST);
        painelBotoes.add(btnSalvarFormulario, BorderLayout.EAST);

        g.gridy = 16; g.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoes, g);

        GridBagConstraints gP = new GridBagConstraints();
        gP.fill = GridBagConstraints.HORIZONTAL; gP.gridx = 0; gP.gridy = 0; gP.weightx = 1;
        painelPrincipal.add(cartao, gP);

        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.setBorder(null);
        return scroll;
    }

    /*
     * Cria a visualização estática do prontuário do paciente selecionado.
     * Utiliza JTextArea não editável e labels com formatação HTML.
     */
    private JScrollPane criarPainelProntuario() {
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
        lblProntuarioTitulo = new JLabel("Prontuário Clínico");
        lblProntuarioTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblProntuarioTitulo.setForeground(COR_PRIMARIA);
        lblProntuarioSub = new JLabel("Histórico médico e acompanhamento do paciente");
        lblProntuarioSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProntuarioSub.setForeground(COR_CINZA);
        txtCabecalho.add(lblProntuarioTitulo);
        txtCabecalho.add(lblProntuarioSub);
        cabecalho.add(txtCabecalho);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(cabecalho, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(COR_BORDA);
        g.gridy = 1; g.insets = new Insets(0, 0, 20, 0);
        cartao.add(sep, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 10, 0);
        cartao.add(criarTituloSecao("Dados do Paciente"), g);

        JPanel painelGridExibicao = new JPanel(new GridBagLayout());
        painelGridExibicao.setBackground(COR_BRANCO);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.5; 
        gc.insets = new Insets(6, 0, 6, 12);

        lblP_Nome = new JLabel("Nome completo: ");
        lblP_Responsavel = new JLabel("Responsável legal: ");
        lblP_Nascimento = new JLabel("Data de nascimento: ");
        lblP_Telefone = new JLabel("Telefone de contato: ");
        lblP_Idade = new JLabel("Idade cronológica: ");
        lblP_Nivel = new JLabel("Nível de suporte TEA: ");
        lblP_Status = new JLabel("Status do Paciente: "); 

        JLabel[] labelsProntuario = {lblP_Nome, lblP_Responsavel, lblP_Nascimento, lblP_Telefone, lblP_Idade, lblP_Nivel, lblP_Status};
        for (JLabel label : labelsProntuario) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        gc.gridx = 0; gc.gridy = 0; painelGridExibicao.add(lblP_Nome, gc);
        gc.gridx = 1; gc.gridy = 0; gc.insets = new Insets(6, 12, 6, 0); painelGridExibicao.add(lblP_Responsavel, gc);

        gc.gridx = 0; gc.gridy = 1; gc.insets = new Insets(6, 0, 6, 12); painelGridExibicao.add(lblP_Nascimento, gc);
        gc.gridx = 1; gc.gridy = 1; gc.insets = new Insets(6, 12, 6, 0); painelGridExibicao.add(lblP_Telefone, gc);

        gc.gridx = 0; gc.gridy = 2; gc.insets = new Insets(6, 0, 6, 12); painelGridExibicao.add(lblP_Idade, gc);
        gc.gridx = 1; gc.gridy = 2; gc.insets = new Insets(6, 12, 6, 0); painelGridExibicao.add(lblP_Nivel, gc);
        
        gc.gridx = 0; gc.gridy = 3; gc.insets = new Insets(6, 0, 12, 12); painelGridExibicao.add(lblP_Status, gc); // Inserido na linha de baixo

        g.gridy = 3; g.gridwidth = 4; g.insets = new Insets(0, 0, 12, 0);
        cartao.add(painelGridExibicao, g);

        g.gridy = 4; g.insets = new Insets(10, 0, 10, 0);
        cartao.add(criarTituloSecao("Informações Clínicas"), g);

        g.gridy = 5; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Medicações em uso"), g);
        txtP_Medicacoes = new JTextArea(3, 0);
        configurarAreaTextoProntuario(txtP_Medicacoes);
        g.gridy = 6; g.insets = new Insets(0, 0, 12, 0);
        cartao.add(new JScrollPane(txtP_Medicacoes), g);

        g.gridy = 7; g.insets = new Insets(6, 0, 3, 0);
        cartao.add(criarLabel("Restrições alimentares / Alergias"), g);
        txtP_Restricoes = new JTextArea(3, 0);
        configurarAreaTextoProntuario(txtP_Restricoes);
        g.gridy = 8; g.insets = new Insets(0, 0, 25, 0);
        cartao.add(new JScrollPane(txtP_Restricoes), g);

        JPanel painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBackground(COR_BRANCO);

        JButton btnVoltarLista = new JButton("Voltar");
        btnVoltarLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltarLista.setForeground(Color.WHITE);
        btnVoltarLista.setBackground(COR_VOLTAR); 
        btnVoltarLista.setFocusPainted(false);
        btnVoltarLista.setBorderPainted(false);
        btnVoltarLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltarLista.setPreferredSize(new Dimension(120, 42));
        btnVoltarLista.addActionListener(e -> voltarParaLista());

        JPanel acoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        acoesDireita.setBackground(COR_BRANCO);

        JButton btnDeletar = new JButton("Deletar Registro");
        btnDeletar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeletar.setForeground(COR_DELETAR);
        btnDeletar.setBackground(COR_BRANCO);
        btnDeletar.setFocusPainted(false);
        btnDeletar.setBorder(BorderFactory.createLineBorder(COR_DELETAR, 1));
        btnDeletar.setPreferredSize(new Dimension(140, 45));
        btnDeletar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeletar.addActionListener(e -> executarExclusaoProntuario());
        
        btnDeletar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDeletar.setBackground(new Color(255, 242, 242));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDeletar.setBackground(COR_BRANCO);
            }
        });

        JButton btnEditar = new JButton("Editar Dados");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditar.setBackground(COR_PRIMARIA);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setPreferredSize(new Dimension(140, 45));
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.addActionListener(e -> prepararEdicao());

        acoesDireita.add(btnDeletar);
        acoesDireita.add(btnEditar);

        painelBotoes.add(btnVoltarLista, BorderLayout.WEST);
        painelBotoes.add(acoesDireita, BorderLayout.EAST);

        g.gridy = 9; g.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoes, g);

        GridBagConstraints gP = new GridBagConstraints();
        gP.fill = GridBagConstraints.HORIZONTAL; gP.gridx = 0; gP.gridy = 0; gP.weightx = 1;
        painelPrincipal.add(cartao, gP);

        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.setBorder(null);
        return scroll;
    }

    /*
     * Padroniza a configuração de JTextArea para o modo apenas leitura dentro do prontuário.
     * @param A JTextArea que terá as propriedades configuradas.
     */
    private void configurarAreaTextoProntuario(JTextArea ta) {
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBackground(new Color(248, 248, 252));
        ta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    /*
     * Prepara a interface para o modo de inclusão de novo paciente.
     * Limpa o estado da variável de edição e reseta os componentes do formulário.
     */
    private void prepararNovoCadastro() {
        pacienteEmEdicao = null;
        lblTituloFormulario.setText("Cadastro de Paciente");
        btnSalvarFormulario.setText("Cadastrar");
        limparFormulario();
        cbStatusAtivo.setSelectedItem("Ativo");
        cardLayout.show(painelCards, "FORMULARIO");
    }
    
    /*
     * Carrega os dados de um paciente específico na tela de visualização (Prontuário).
     * @param p Paciente a ser exibido.
     */
    private void exibirProntuario(Paciente p) {
        pacienteEmExibicaoProntuario = p;
        int idade = p.getDataNascimento() != null ? Period.between(p.getDataNascimento(), LocalDate.now()).getYears() : 0;

        // Atualização dos labels com formatação HTML para estruturação visual
        lblProntuarioTitulo.setText("Prontuário - " + p.getNomePaciente());
        lblP_Nome.setText("<html><font color='#78788C'>Nome completo:</font> " + p.getNomePaciente() + "</html>");
        lblP_Nascimento.setText("<html><font color='#78788C'>Data de nascimento:</font> " + (p.getDataNascimento() != null ? p.getDataNascimento().format(formatter) : "") + "</html>");
        lblP_Idade.setText("<html><font color='#78788C'>Idade cronológica:</font> " + idade + " anos</html>");
        lblP_Responsavel.setText("<html><font color='#78788C'>Responsável legal:</font> " + (p.getResponsavel() == null ? "Não informado" : p.getResponsavel()) + "</html>");
        lblP_Telefone.setText("<html><font color='#78788C'>Telefone de contato:</font> " + p.getTelefoneResponsavel() + "</html>");
        
        // Tradução numérica do nível TEA para representação textual amigável
        String descTea = switch (p.getNivelTea()) {
            case 1 -> "1 - Leve";
            case 2 -> "2 - Suporte Substancial";
            case 3 -> "3 - Suporte Muito Severo";
            default -> String.valueOf(p.getNivelTea());
        };
        lblP_Nivel.setText("<html><font color='#78788C'>Nível de suporte TEA:</font> " + descTea + "</html>");
        
        String statusTexto = p.isStatusAtivo() ? "Ativo" : "Inativo";
        lblP_Status.setText("<html><font color='#78788C'>Status do cadastro:</font> " + statusTexto + "</html>");
        
        // Definição de valores padrão para campos de texto nulos
        txtP_Medicacoes.setText(p.getMedicacoesEmUso() == null ? "Nenhuma medicação registrada." : p.getMedicacoesEmUso());
        txtP_Restricoes.setText(p.getRestricoesAlimentares() == null ? "Nenhuma restrição registrada." : p.getRestricoesAlimentares());

        cardLayout.show(painelCards, "PRONTUARIO");
    }

    /*
     * Transfere os dados do paciente em visualização para o formulário de edição.
     */
    private void prepararEdicao() {
        if (pacienteEmExibicaoProntuario == null) return;
        pacienteEmEdicao = pacienteEmExibicaoProntuario;

        lblTituloFormulario.setText("Editar Cadastro do Paciente");
        btnSalvarFormulario.setText("Atualizar Registro");

        txtNome.setText(pacienteEmEdicao.getNomePaciente());
        txtDataNascimento.setText(pacienteEmEdicao.getDataNascimento() != null ? pacienteEmEdicao.getDataNascimento().format(formatter) : "");
        txtResponsavel.setText(pacienteEmEdicao.getResponsavel() != null ? pacienteEmEdicao.getResponsavel() : "");
        
        txtTelefone.setText(pacienteEmEdicao.getTelefoneResponsavel() != null ? pacienteEmEdicao.getTelefoneResponsavel() : "");
        
        spnNivelTea.setValue(pacienteEmEdicao.getNivelTea());
        txtMedicacoes.setText(pacienteEmEdicao.getMedicacoesEmUso());
        txtRestricoes.setText(pacienteEmEdicao.getRestricoesAlimentares());

        // Sincronização do estado do ComboBox com a propriedade booleana do objeto
        if (cbStatusAtivo != null) {
            cbStatusAtivo.setSelectedItem(pacienteEmEdicao.isStatusAtivo() ? "Ativo" : "Inativo");
        }

        cardLayout.show(painelCards, "FORMULARIO");
    }

    /*
     * Valida os campos, gera uma pré-visualização em HTML e abre um JDialog para confirmação.
     */
    private void revisarECadastrar() {
        if (!validarCampos()) return;

        String nivelDesc = switch ((int) spnNivelTea.getValue()) {
            case 1 -> "1 - Leve";
            case 2 -> "2 - Substancial";
            case 3 -> "3 - Muito Severo";
            default -> "";
        };

        String med = txtMedicacoes.getText().isBlank() ? "Não informado" : txtMedicacoes.getText().trim();
        String res = txtRestricoes.getText().isBlank()  ? "Não informado" : txtRestricoes.getText().trim();
        
        String statusTexto = cbStatusAtivo != null ? (String) cbStatusAtivo.getSelectedItem() : "Ativo";

        // Construção de string HTML para exibição formatada no diálogo de revisão
        String html = "<html><body style='font-family:Segoe UI; width:400px; margin:4px'>"
                + "<h2 style='color:#008080; margin:0 0 6px 0; font-size:15px'>"
                + "Revise os dados antes de confirmar</h2>"
                + "<hr style='border:none; border-top:1px solid #008080; margin-bottom:10px'>"
                + "<table style='font-size:13px; width:100%; border-spacing:0 8px'>"
                + linha("Nome",    txtNome.getText().trim())
                + linha("Nascimento",   txtDataNascimento.getText().trim())
                + linha("Responsável", txtResponsavel.getText().isBlank() ? "Não informado" : txtResponsavel.getText().trim())
                + linha("Telefone",    txtTelefone.getText().trim())
                + linha("Nível TEA",   nivelDesc)
                + linha("Status",     statusTexto)
                + linha("Medicações",  med)
                + linha("Restrições",  res)
                + "</table>"
                + "</body></html>";

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Confirmação de Cadastro", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel painelConteudo = new JPanel(new BorderLayout(0, 14));
        painelConteudo.setBackground(Color.WHITE);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));

        JLabel lblRevisao = new JLabel(html);
        lblRevisao.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane scroll = new JScrollPane(lblRevisao);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(460, 270));
        scroll.getViewport().setBackground(Color.WHITE);

        painelConteudo.add(scroll, BorderLayout.CENTER);

        JButton btnConfirmar = criarBotaoDialog("Confirmar", COR_PRIMARIA);
        JButton btnCancelar  = criarBotaoDialog("Cancelar",  COR_DELETAR);

        final boolean[] confirmado = {false};
        btnConfirmar.addActionListener(e -> { confirmado[0] = true;  dialog.dispose(); });
        btnCancelar .addActionListener(e -> { confirmado[0] = false; dialog.dispose(); });

        JPanel painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        painelBotoes.add(btnCancelar,  BorderLayout.WEST);
        painelBotoes.add(btnConfirmar, BorderLayout.EAST);

        painelConteudo.add(painelBotoes, BorderLayout.SOUTH);

        dialog.setContentPane(painelConteudo);
        dialog.pack();
        dialog.setSize(new Dimension(520, 440));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (confirmado[0]) {
            salvar();
        }
    }

    /*
     * Fábrica de botões padronizada para modais/diálogos.
     */
    private JButton criarBotaoDialog(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        return btn;
    }

    /*
     * Helper para gerar linhas de tabela HTML para o componente de revisão.
     */
    private String linha(String rotulo, String valor) {
        return "<tr>"
             + "<td style='color:#4a5568; width:115px; vertical-align:top; padding:3px 10px 3px 0'>"
             + rotulo + ":</td>"
             + "<td style='color:#1a202c; font-weight:normal; vertical-align:top; padding:3px 0'>"
             + valor + "</td>"
             + "</tr>";
    }

    /*
     * Persiste os dados do formulário no banco através do DAO.
     */
    private void salvar() {
        Paciente p = (pacienteEmEdicao != null) ? pacienteEmEdicao : new Paciente();
        Clinica c  = new Clinica();
        c.setIdClinica(idClinica);
        p.setClinica(c);
        p.setNomePaciente(txtNome.getText().trim());
        p.setDataNascimento(LocalDate.parse(txtDataNascimento.getText().trim(), formatter));
        p.setResponsavel(txtResponsavel.getText().isBlank() ? null : txtResponsavel.getText().trim());
        p.setTelefoneResponsavel(txtTelefone.getText().trim());
        p.setNivelTea((int) spnNivelTea.getValue());
        
        boolean estaAtivo = cbStatusAtivo != null && "Ativo".equals(cbStatusAtivo.getSelectedItem());
        p.setStatusAtivo(estaAtivo);

        p.setMedicacoesEmUso(txtMedicacoes.getText().isBlank() ? null : txtMedicacoes.getText().trim());
        p.setRestricoesAlimentares(txtRestricoes.getText().isBlank() ? null : txtRestricoes.getText().trim());

        if (pacienteEmEdicao != null) {
            p.setIdPaciente(pacienteEmEdicao.getIdPaciente());
        }

        boolean sucesso = (pacienteEmEdicao != null) ? dao.atualizar(p) : dao.cadastrar(p);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Registro processado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltarParaLista();
        } else {
            JOptionPane.showMessageDialog(this, "Erro operacional ao salvar dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Executa a remoção permanente do registro do banco de dados com confirmação do usuário.
     */
    private void executarExclusaoProntuario() {
        if (pacienteEmExibicaoProntuario == null) return;

        int escolha = JOptionPane.showConfirmDialog(this,
                "Deseja realmente deletar o prontuário de " + pacienteEmExibicaoProntuario.getNomePaciente() + "?\nEsta ação é permanente.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (escolha == JOptionPane.YES_OPTION) {
            if (dao.excluir(pacienteEmExibicaoProntuario.getIdPaciente())) {
                JOptionPane.showMessageDialog(this, "Paciente removido com sucesso.");
                voltarParaLista();
            } else {
                JOptionPane.showMessageDialog(this, "Erro operacional ao excluir registro.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gerencia a ação de cancelamento, verificando se há dados não salvos para evitar perda acidental.
     */
    private void cancelar() {
        if (formularioPreenchido()) {
            String mensagem = (pacienteEmEdicao != null) 
                ? "As alterações feitas não serão salvas.\nDeseja realmente voltar?"
                : "Os dados preenchidos serão perdidos.\nDeseja realmente voltar?";
                
            int confirma = JOptionPane.showConfirmDialog(this, mensagem, "Confirmar Retorno", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirma != JOptionPane.YES_OPTION) return;
        }
        voltarParaLista();
    }

    /*
     * Reseta estados, atualiza a lista e retorna à view principal.
     */
    private void voltarParaLista() {
        pacienteEmEdicao = null;
        pacienteEmExibicaoProntuario = null;
        limparFormulario();
        atualizarListaPacientes();
        cardLayout.show(painelCards, "LISTA");
    }

    /*
     * Verifica se os campos do formulário contêm dados.
     */
    private boolean formularioPreenchido() {
        return !txtNome.getText().isBlank()
            || !txtDataNascimento.getText().isBlank()
            || !txtTelefone.getText().isBlank()
            || !txtMedicacoes.getText().isBlank()
            || !txtRestricoes.getText().isBlank();
    }

    /*
     * Valida os campos obrigatórios e formato de dados.
     * @return true se o formulário for válido, false caso contrário.
     */
    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (txtNome.getText().trim().isEmpty()) {
            erros.append("- O campo 'Nome completo' é obrigatório.\n");
        }

        String dataTexto = txtDataNascimento.getText().trim();
        if (dataTexto.isEmpty()) {
            erros.append("- O campo 'Data de Nascimento' é obrigatório.\n");
        } else {
            try {
                LocalDate.parse(dataTexto, formatter);
            } catch (DateTimeParseException e) {
                erros.append("- A Data de Nascimento é inválida. Use o formato dd/MM/yyyy.\n");
            }
        }

        if (txtResponsavel.getText().trim().isEmpty()) {
            erros.append("- O campo 'Responsável' é obrigatório.\n");
        }

        if (txtTelefone.getText().trim().isEmpty()) {
            erros.append("- O campo 'Telefone do Responsável' é obrigatório.\n");
        }

        int nivel = (int) spnNivelTea.getValue();
        if (nivel < 1 || nivel > 3) {
            erros.append("- O campo 'Nível TEA' deve ser 1, 2 ou 3.\n");
        }

        if (erros.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, corrija os seguintes problemas:\n\n" + erros.toString(), 
                "Campos Obrigatórios", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /*
     * Limpa todos os componentes de entrada do formulário.
     */
    private void limparFormulario() {
        txtNome.setText("");
        txtDataNascimento.setText("");
        txtResponsavel.setText("");
        txtTelefone.setText("");
        spnNivelTea.setValue(1);
        txtMedicacoes.setText("");
        txtRestricoes.setText("");
        
        if (cbStatusAtivo != null) {
            cbStatusAtivo.setSelectedItem("Ativo");
        }
    }

    // ── Helpers de UI ───
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

    private JTextField criarCampo(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(180, 180, 200));
                    g.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    g.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return tf;
    }

    private JTextArea criarAreaTexto(String placeholder) {
        JTextArea ta = new JTextArea(3, 0) {
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