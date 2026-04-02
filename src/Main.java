import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String pasta = "dados/";
        String pastaUsuarios = pasta + "usuarios/";
        String arquivoID = pasta + "id.txt";

        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Login");
            System.out.println("3 - Listar Usuários (Ordem Alfabética)");
            System.out.println("4 - Buscar Usuário");
            System.out.println("5 - Excluir Usuário");
            System.out.println("6 - Iniciar / Resetar");
            System.out.println("7 - Sair");
            System.out.print("Opção: ");

            opcao = sc.nextInt();
            sc.nextLine(); // limpar buffer

            switch (opcao) {

                case 1:
                    // 1️ lê o ID atual
                    int id = obterID(arquivoID);

                    // 2️ cria o objeto Usuario
                    usuarios u = criaUsuario(id);

                    // 3️ salva o objeto
                    salvarUsuario(u, pastaUsuarios);

                    // 4️ atualiza o ID
                    atualizarid(arquivoID, id + 1);
                    break;

                case 2:
                    usuarios logado = fazerLogin(pastaUsuarios);

                    // menu após login
                    if (logado != null) {
                        menuUsuario(logado);
                    }
                    break;

                case 3:
                    listarUsuarios();
                    break;

                case 4:
                    buscarUsuario();
                    break;

                case 5:
                    excluirUsuario();
                    break;

                case 6:
                    iniciarResetar(pasta, pastaUsuarios, arquivoID);
                    break;

                case 7:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 4);

        }

    private static int obterID(String arquivoID) {

        try {
            File f = new File(arquivoID);

            // Se o arquivo não existir, cria com ID = 0
            if (!f.exists()) {
                f.getParentFile().mkdirs(); // garante pasta
                PrintWriter pw = new PrintWriter(f);
                pw.println(0);
                pw.close();
            }

            BufferedReader br = new BufferedReader(new FileReader(f));
            int id = Integer.parseInt(br.readLine());
            br.close();

            return id;

        } catch (Exception e) {
            System.out.println("Erro ao obter ID.");
            return -1;
        }
    }

    private static void atualizarid(String arquivoID, int novoID) {

        try {
            PrintWriter pw = new PrintWriter(arquivoID);
            pw.println(novoID);
            pw.close();
        } catch (Exception e) {
            System.out.println("Erro ao atualizar ID.");
        }
    }

    private static usuarios criaUsuario(int id) {

        Scanner sc = new Scanner(System.in);

        usuarios u = new usuarios();   // cria o objeto Usuario
        char opcao = 's';
        int i = 0;

        u.id = id;

        // cria as classes heterogêneas
        u.contato = new contato();
        u.documento = new documento();
        u.enderecos = new Endereco[2]; // limite de endereços

        System.out.print("\nNome: ");
        u.nome = sc.nextLine();

        System.out.print("Senha: ");
        u.senha = sc.nextLine();

        System.out.print("Email: ");
        u.contato.email = sc.nextLine();

        System.out.print("Telefone: ");
        u.contato.telefone = sc.nextLine();

        System.out.print("CPF: ");
        u.documento.cpf = sc.nextLine();

        // cadastro de múltiplos endereços
        do {
            Endereco e = new Endereco(); // cria objeto Endereco

            System.out.print("Bairro: ");
            e.bairro = sc.nextLine();

            System.out.print("Rua: ");
            e.rua = sc.nextLine();

            System.out.print("Número: ");
            e.numero = sc.nextInt();
            sc.nextLine(); // limpar buffer

            u.enderecos[i] = e; // insere no vetor
            i++;

            if (i < u.enderecos.length) {
                System.out.print("Deseja cadastrar outro endereço? (s/n): ");
                opcao = sc.next().charAt(0);
                sc.nextLine();
            } else {
                opcao = 'n';
            }

        } while (opcao == 's');

        return u; // retorna o objeto COMPLETO
    }

    private static void salvarUsuario(usuarios u, String pastaUsuarios) {

        try {
            // garante que a pasta exista
            File dir = new File(pastaUsuarios);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String arquivo = pastaUsuarios + u.id + ".txt";
            PrintWriter pw = new PrintWriter(arquivo);

            // dados principais
            pw.println(u.id);
            pw.println(u.nome);
            pw.println(u.senha);

            // contato
            pw.println(u.contato.email);
            pw.println(u.contato.telefone);

            // documento
            pw.println(u.documento.cpf);

            // endereços
            for (int i = 0; i < u.enderecos.length; i++) {
                if (u.enderecos[i] != null) {
                    pw.println("ENDERECO");
                    pw.println(u.enderecos[i].bairro);
                    pw.println(u.enderecos[i].rua);
                    pw.println(u.enderecos[i].numero);
                }
            }

            pw.close();
            System.out.println("\nUsuário salvo com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao salvar usuário.");
        }
    }

    private static usuarios lerUsuario(String arquivo) {

        usuarios u = new usuarios();

        try {
            BufferedReader br = new BufferedReader(new FileReader(arquivo));

            // dados principais
            u.id = Integer.parseInt(br.readLine());
            u.nome = br.readLine();
            u.senha = br.readLine();

            // classes heterogêneas
            u.contato = new contato();
            u.contato.email = br.readLine();
            u.contato.telefone = br.readLine();

            u.documento = new documento();
            u.documento.cpf = br.readLine();

            // endereços
            u.enderecos = new Endereco[5];
            int i = 0;

            String linha;
            while ((linha = br.readLine()) != null) {

                if (linha.equals("ENDERECO")) {
                    Endereco e = new Endereco();

                    e.bairro = br.readLine();
                    e.rua = br.readLine();
                    e.numero = Integer.parseInt(br.readLine());

                    u.enderecos[i] = e;
                    i++;
                }
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Erro ao ler usuário.");
            return null;
        }

        return u;
    }

    private static usuarios fazerLogin(String pastaUsuarios) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        File pasta = new File(pastaUsuarios);
        File[] arquivos = pasta.listFiles();

        if (arquivos == null) {
            System.out.println("Nenhum usuário cadastrado.");
            return null;
        }

        for (File f : arquivos) {

            usuarios u = lerUsuario(f.getPath()); // 👈 AQUI

            if (u != null &&
                    u.contato.email.equals(email) &&
                    u.senha.equals(senha)) {

                System.out.println("\nLogin realizado com sucesso!");
                return u; // retorna o OBJETO
            }
        }

        System.out.println("Email ou senha inválidos.");
        return null;
    }

    private static void listarUsuarios() {

        File pasta = new File("dados/usuarios");

        if (!pasta.exists()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        File[] arquivos = pasta.listFiles();
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        usuarios[] Usuario = new usuarios[arquivos.length];
        int total = 0;

        usuarios[] usuarios = new usuarios[arquivos.length];
        int total1 = 0;

        for (File f : arquivos) {
            usuarios u = lerUsuario(f.getPath());
            if (u != null) {
                usuarios[total] = u;
                total++;
            }
        }

        // ORDENAÇÃO ALFABÉTICA (BUBBLE SORT)
        for (int i = 0; i < total - 1; i++) {
            for (int j = 0; j < total - i - 1; j++) {
                if (Usuario[j].nome.compareToIgnoreCase(Usuario[j + 1].nome) > 0) {
                    usuarios temp = Usuario[j];
                    Usuario[j] = Usuario[j + 1];
                    Usuario[j + 1] = temp;
                }
            }
        }

        System.out.println("\n=== LISTA DE USUÁRIOS ===");
        for (int i = 0; i < total; i++) {
            System.out.println("ID: " + Usuario[i].id + " | Nome: " + Usuario[i].nome);
        }
    }

    private static void buscarUsuario() {

        Scanner sc = new Scanner(System.in);
        System.out.print("Digite nome, email ou CPF para buscar: ");
        String busca = sc.nextLine().toLowerCase();

        File pasta = new File("dados/usuarios");

        if (!pasta.exists()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        File[] arquivos = pasta.listFiles();
        boolean encontrado = false;

        for (File f : arquivos) {

            usuarios u = lerUsuario(f.getPath());

            if (u != null &&
                    (
                            u.nome.toLowerCase().contains(busca) ||
                                    u.contato.email.toLowerCase().contains(busca) ||
                                    u.documento.cpf.contains(busca)
                    )
            ) {

                System.out.println("\n=== USUÁRIO ENCONTRADO ===");
                System.out.println("ID: " + u.id);
                System.out.println("Nome: " + u.nome);
                System.out.println("Email: " + u.contato.email);
                System.out.println("CPF: " + u.documento.cpf);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("Usuário não encontrado.");
        }
    }

    private static void excluirUsuario() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o ID do usuário para excluir: ");
        int id = sc.nextInt();
        sc.nextLine();

        File arquivoUsuario = new File("dados/usuarios/" + id + ".txt");

        if (!arquivoUsuario.exists()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        System.out.print("Tem certeza que deseja excluir? (s/n): ");
        char op = sc.nextLine().charAt(0);

        if (op == 's' || op == 'S') {

            arquivoUsuario.delete();

            apagarPasta("dados/receitas/" + id);
            apagarPasta("dados/despesas/" + id);

            System.out.println("Usuário excluído com sucesso!");
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    private static void apagarPasta(String caminho) {

        File dir = new File(caminho);
        if (!dir.exists()) return;

        File[] arquivos = dir.listFiles();
        if (arquivos != null) {
            for (File f : arquivos) {
                f.delete();
            }
        }
        dir.delete();
    }

        private static void menuUsuario(usuarios u) {

        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU DO USUÁRIO ===");
            System.out.println("Usuário logado: " + u.nome + " (ID: " + u.id + ")");
            System.out.println("1 - Receitas");
            System.out.println("2 - Despesas");
            System.out.println("3 - Buscar Extrato (Mês/Ano)");
            System.out.println("4 - Resumo do Mês Atual");
            System.out.println("5 - Sair");
            System.out.print("Opção: ");

            opcao = sc.nextInt();
            sc.nextLine(); // limpa buffer

            switch (opcao) {

                case 1:
                    menuReceitas(u);
                    break;

                case 2:
                    menuDespesas(u);
                    break;

                case 3:
                    buscarExtrato(u);
                    break;

                case 4:
                    resumoMesAtual(u);
                    break;

                case 5:
                    System.out.println("Saindo do usuário...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 5);
    }

    private static void buscarExtrato(usuarios u) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o mês (1-12): ");
        int mesEscolhido = sc.nextInt();

        System.out.print("Digite o ano: ");
        int anoEscolhido = sc.nextInt();

        System.out.println();
        System.out.println("==================== EXTRATO " +
                String.format("%02d/%d", mesEscolhido, anoEscolhido) +
                " ====================");

        double totalReceitas = 0;
        double totalDespesas = 0;

        // ===== RECEITAS =====
        System.out.println("\n---------------------- RECEITAS -------------------------");
        System.out.printf("%-12s %-30s %10s%n", "Data", "Nome", "Valor (R$)");
        System.out.println("---------------------------------------------------------");

        totalReceitas = listarExtratoReceitas(u.id, mesEscolhido, anoEscolhido);

        // ===== DESPESAS =====
        System.out.println("\n---------------------- DESPESAS -------------------------");
        System.out.printf("%-12s %-30s %10s%n", "Data", "Nome", "Valor (R$)");
        System.out.println("---------------------------------------------------------");

        totalDespesas = listarExtratoDespesas(u.id, mesEscolhido, anoEscolhido);

        // ===== RESUMO FINAL =====
        System.out.println("\n---------------------- RESUMO ---------------------------");
        System.out.printf("TOTAL DE RECEITAS:        R$ %.2f%n", totalReceitas);
        System.out.printf("TOTAL DE DESPESAS:        R$ %.2f%n", totalDespesas);
        System.out.printf("SALDO FINAL:              R$ %.2f%n",
                (totalReceitas - totalDespesas));

        System.out.println("========================================================");
    }

    private static double listarExtratoReceitas(int idUsuario, int mes, int ano) {

        String pasta = "dados/receitas/" + idUsuario + "/";
        File dir = new File(pasta);

        double total = 0;

        if (!dir.exists()) {
            System.out.println("Nenhuma receita encontrada.");
            return 0;
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null) return 0;

        for (File f : arquivos) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));

                Receita r = new Receita();
                r.nome = br.readLine();
                r.valor = Double.parseDouble(br.readLine().replace(",", "."));

                r.data = new Data();
                r.data.dia = Integer.parseInt(br.readLine());
                r.data.mes = Integer.parseInt(br.readLine());
                r.data.ano = Integer.parseInt(br.readLine());

                r.descricao = br.readLine();
                br.close();

                if (r.data.mes == mes && r.data.ano == ano) {
                    System.out.printf(
                            "%02d/%02d/%04d %-30s %10.2f%n",
                            r.data.dia, r.data.mes, r.data.ano,
                            r.nome, r.valor
                    );
                    total += r.valor;
                }

            } catch (Exception e) {
                System.out.println("Erro ao ler receita.");
            }
        }

        return total;
    }

    private static double listarExtratoDespesas(int idUsuario, int mes, int ano) {

        String[] tipos = {
                "fixas",
                "variaveis",
                "investimentos",
                "cartao_credito",
                "cartao_debito"
        };

        double total = 0;

        for (String tipo : tipos) {

            String pasta = "dados/despesas/" + idUsuario + "/" + tipo + "/";
            File dir = new File(pasta);

            if (!dir.exists()) continue;

            File[] arquivos = dir.listFiles();
            if (arquivos == null) continue;

            for (File f : arquivos) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));

                    Despesa d = new Despesa();
                    d.nome = br.readLine();
                    d.valor = Double.parseDouble(br.readLine().replace(",", "."));

                    d.data = new Data();
                    d.data.dia = Integer.parseInt(br.readLine());
                    d.data.mes = Integer.parseInt(br.readLine());
                    d.data.ano = Integer.parseInt(br.readLine());

                    d.descricao = br.readLine();
                    br.close();

                    if (d.data.mes == mes && d.data.ano == ano) {
                        System.out.printf(
                                "%02d/%02d/%04d %-30s %10.2f%n",
                                d.data.dia, d.data.mes, d.data.ano,
                                "[" + tipo.toUpperCase() + "] " + d.nome,
                                -d.valor
                        );
                        total += d.valor;
                    }

                } catch (Exception e) {
                    System.out.println("Erro ao ler despesa.");
                }
            }
        }

        return total;
    }

    private static Data dataAtual() {

        java.util.Calendar c = java.util.Calendar.getInstance();

        Data d = new Data();
        d.dia = c.get(java.util.Calendar.DAY_OF_MONTH);
        d.mes = c.get(java.util.Calendar.MONTH) + 1;
        d.ano = c.get(java.util.Calendar.YEAR);

        return d;
    }

    private static void resumoMesAtual(usuarios u) {

        Data hoje = dataAtual();

        int mes = hoje.mes;
        int ano = hoje.ano;

        System.out.println();
        System.out.println("================ RESUMO DO MÊS ATUAL =================");
        System.out.println("Período: " + String.format("%02d/%d", mes, ano));

        double totalReceitas = 0;
        double totalDespesas = 0;

        // ===== RECEITAS =====
        System.out.println("\n---------------------- RECEITAS -------------------------");
        System.out.printf("%-12s %-30s %10s%n", "Data", "Nome", "Valor (R$)");
        System.out.println("---------------------------------------------------------");

        totalReceitas = listarExtratoReceitas(u.id, mes, ano);

        // ===== DESPESAS =====
        System.out.println("\n---------------------- DESPESAS -------------------------");
        System.out.printf("%-12s %-30s %10s%n", "Data", "Nome", "Valor (R$)");
        System.out.println("---------------------------------------------------------");

        totalDespesas = listarExtratoDespesas(u.id, mes, ano);

        // ===== RESUMO =====
        System.out.println("\n---------------------- RESUMO ---------------------------");
        System.out.printf("TOTAL DE RECEITAS:        R$ %.2f%n", totalReceitas);
        System.out.printf("TOTAL DE DESPESAS:        R$ %.2f%n", totalDespesas);
        System.out.printf("SALDO FINAL:              R$ %.2f%n",
                (totalReceitas - totalDespesas));

        System.out.println("========================================================");
    }

    private static void menuReceitas(usuarios u) {

        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU RECEITAS ===");
            System.out.println("1 - Cadastrar Receita");
            System.out.println("2 - Listar Receitas do Mês Atual");
            System.out.println("3 - Excluir Receita");
            System.out.println("4 - Voltar");
            System.out.print("Opção: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {

                case 1:
                    Receita r = criarReceita();
                    salvarReceita(u.id, r);
                    break;

                case 2:
                    listarReceitasMesAtual(u.id);
                    break;

                case 3:
                    excluirReceita(u.id);
                    break;

                case 4:
                    System.out.println("Voltando ao menu do usuário...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 4);
    }

    private static Receita criarReceita() {

        Scanner sc = new Scanner(System.in);
        Receita r = new Receita();

        System.out.print("Nome da Receita: ");
        r.nome = sc.nextLine();

        System.out.print("Valor: ");
        r.valor = sc.nextDouble();

        r.data = new Data();

        System.out.print("Dia: ");
        r.data.dia = sc.nextInt();

        System.out.print("Mês: ");
        r.data.mes = sc.nextInt();

        System.out.print("Ano: ");
        r.data.ano = sc.nextInt();
        sc.nextLine();

        System.out.print("Descrição: ");
        r.descricao = sc.nextLine();

        return r;
    }

    private static void salvarReceita(int idUsuario, Receita r) {

        try {
            String pasta = "dados/receitas/" + idUsuario + "/";
            File dir = new File(pasta);
            if (!dir.exists()) dir.mkdirs();

            String nomeArquivo = pasta + System.currentTimeMillis() + ".txt";
            PrintWriter pw = new PrintWriter(nomeArquivo);

            pw.println(r.nome);
            pw.println(r.valor);
            pw.println(r.data.dia);
            pw.println(r.data.mes);
            pw.println(r.data.ano);
            pw.println(r.descricao);

            pw.close();
            System.out.println("Receita salva com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao salvar receita.");
        }
    }

    private static void listarReceitasMesAtual(int idUsuario) {

        Data hoje = dataAtual();
        int mes = hoje.mes;
        int ano = hoje.ano;

        String pasta = "dados/receitas/" + idUsuario + "/";
        File dir = new File(pasta);

        if (!dir.exists()) {
            System.out.println("Nenhuma receita cadastrada.");
            return;
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null) return;

        System.out.println("\n--- RECEITAS DO MÊS ATUAL ---");
        System.out.printf("%-12s %-30s %10s%n", "Data", "Nome", "Valor (R$)");
        System.out.println("--------------------------------------------------------");

        double total = 0;

        for (File f : arquivos) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));

                Receita r = new Receita();
                r.nome = br.readLine();
                r.valor = Double.parseDouble(br.readLine());

                r.data = new Data();
                r.data.dia = Integer.parseInt(br.readLine());
                r.data.mes = Integer.parseInt(br.readLine());
                r.data.ano = Integer.parseInt(br.readLine());

                r.descricao = br.readLine();
                br.close();

                if (r.data.mes == mes && r.data.ano == ano) {
                    System.out.printf(
                            "%02d/%02d/%04d %-30s %10.2f%n",
                            r.data.dia, r.data.mes, r.data.ano,
                            r.nome, r.valor
                    );
                    total += r.valor;
                }

            } catch (Exception e) {
                System.out.println("Erro ao ler receita.");
            }
        }

        System.out.printf("TOTAL DO MÊS: R$ %.2f%n", total);
    }

    private static void excluirReceita(int idUsuario) {

        Scanner sc = new Scanner(System.in);
        String pasta = "dados/receitas/" + idUsuario + "/";
        File dir = new File(pasta);

        if (!dir.exists()) {
            System.out.println("Nenhuma receita para excluir.");
            return;
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhuma receita cadastrada.");
            return;
        }

        System.out.println("\nSelecione a receita para excluir:");

        for (int i = 0; i < arquivos.length; i++) {
            System.out.println((i + 1) + " - " + arquivos[i].getName());
        }

        System.out.print("Opção: ");
        int op = sc.nextInt();

        if (op < 1 || op > arquivos.length) {
            System.out.println("Opção inválida.");
            return;
        }

        arquivos[op - 1].delete();
        System.out.println("Receita excluída com sucesso!");
    }

    private static void menuDespesas(usuarios u) {

        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU DESPESAS ===");
            System.out.println("1 - Cadastrar Despesa Fixa");
            System.out.println("2 - Cadastrar Despesa Variável");
            System.out.println("3 - Cadastrar Investimento / Guardado");
            System.out.println("4 - Cadastrar Cartão de Crédito");
            System.out.println("5 - Cadastrar Cartão de Débito");
            System.out.println("6 - Listar Despesas do Mês Atual");
            System.out.println("7 - Excluir Despesa");
            System.out.println("8 - Voltar");
            System.out.print("Opção: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {

                case 1:
                    salvarDespesa(u.id, criarDespesa("FIXA"));
                    break;

                case 2:
                    salvarDespesa(u.id, criarDespesa("VARIAVEL"));
                    break;

                case 3:
                    salvarDespesa(u.id, criarDespesa("INVESTIMENTO"));
                    break;

                case 4:
                    cadastrarCartaoCredito(u.id);
                    break;

                case 5:
                    salvarDespesa(u.id, criarDespesa("DEBITO"));
                    break;

                case 6:
                    listarDespesasMesAtual(u.id);
                    break;

                case 7:
                    excluirDespesa(u.id);
                    break;

                case 8:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 8);
    }

    private static Despesa criarDespesa(String tipo) {

        Scanner sc = new Scanner(System.in);
        Despesa d = new Despesa();

        d.tipo = tipo;

        System.out.print("Nome da Despesa: ");
        d.nome = sc.nextLine();

        System.out.print("Valor: ");
        d.valor = sc.nextDouble();

        d.data = new Data();

        System.out.print("Dia: ");
        d.data.dia = sc.nextInt();

        System.out.print("Mês: ");
        d.data.mes = sc.nextInt();

        System.out.print("Ano: ");
        d.data.ano = sc.nextInt();
        sc.nextLine();

        d.parcelas = 1;

        return d;
    }

    private static Data calcularDataParcela(Data base, int parcelaAtual) {

        Data nova = new Data();

        int mesTotal = base.mes + parcelaAtual;

        nova.ano = base.ano + (mesTotal - 1) / 12;
        nova.mes = ((mesTotal - 1) % 12) + 1;
        nova.dia = base.dia;

        return nova;
    }

    private static void cadastrarCartaoCredito(int idUsuario) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Nome da compra: ");
        String nome = sc.nextLine();

        System.out.print("Valor total: ");
        double valorTotal = sc.nextDouble();

        System.out.print("Número de parcelas: ");
        int parcelas = sc.nextInt();

        Data dataBase = new Data();

        System.out.print("Dia da compra: ");
        dataBase.dia = sc.nextInt();

        System.out.print("Mês inicial: ");
        dataBase.mes = sc.nextInt();

        System.out.print("Ano: ");
        dataBase.ano = sc.nextInt();

        double valorParcela = valorTotal / parcelas;

        for (int i = 0; i < parcelas; i++) {

            Despesa d = new Despesa();
            d.nome = nome + " (" + (i + 1) + "/" + parcelas + ")";
            d.valor = valorParcela;
            d.tipo = "CREDITO";
            d.parcelas = parcelas;

            d.data = calcularDataParcela(dataBase, i);

            salvarDespesa(idUsuario, d);
        }

        System.out.println("Parcelas lançadas corretamente!");
    }

    private static void salvarDespesa(int idUsuario, Despesa d) {

        try {
            String pasta = "dados/despesas/" + idUsuario + "/";
            File dir = new File(pasta);
            if (!dir.exists()) dir.mkdirs();

            String nomeArquivo = pasta + System.currentTimeMillis() + ".txt";
            PrintWriter pw = new PrintWriter(nomeArquivo);

            pw.println(d.nome);
            pw.println(d.valor);
            pw.println(d.data.dia);
            pw.println(d.data.mes);
            pw.println(d.data.ano);
            pw.println(d.tipo);
            pw.println(d.parcelas);

            pw.close();

        } catch (Exception e) {
            System.out.println("Erro ao salvar despesa.");
        }
    }

    private static void listarDespesasMesAtual(int idUsuario) {

        Data hoje = dataAtual();
        int mes = hoje.mes;
        int ano = hoje.ano;

        String pasta = "dados/despesas/" + idUsuario + "/";
        File dir = new File(pasta);

        if (!dir.exists()) {
            System.out.println("Nenhuma despesa cadastrada.");
            return;
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null) return;

        System.out.println("\n--- DESPESAS DO MÊS ATUAL ---");
        System.out.printf("%-12s %-35s %10s%n", "Data", "Nome", "Valor");
        System.out.println("----------------------------------------------------------");

        double total = 0;

        for (File f : arquivos) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));

                Despesa d = new Despesa();
                d.nome = br.readLine();
                d.valor = Double.parseDouble(br.readLine());

                d.data = new Data();
                d.data.dia = Integer.parseInt(br.readLine());
                d.data.mes = Integer.parseInt(br.readLine());
                d.data.ano = Integer.parseInt(br.readLine());

                d.tipo = br.readLine();
                d.parcelas = Integer.parseInt(br.readLine());
                br.close();

                if (d.data.mes == mes && d.data.ano == ano) {
                    System.out.printf(
                            "%02d/%02d/%04d %-35s %10.2f (%s)%n",
                            d.data.dia, d.data.mes, d.data.ano,
                            d.nome, -d.valor, d.tipo
                    );
                    total += d.valor;
                }

            } catch (Exception e) {
                System.out.println("Erro ao ler despesa.");
            }
        }

        System.out.printf("TOTAL DO MÊS: R$ %.2f%n", total);
    }

    private static void excluirDespesa(int idUsuario) {

        Scanner sc = new Scanner(System.in);
        String pasta = "dados/despesas/" + idUsuario + "/";
        File dir = new File(pasta);

        if (!dir.exists()) {
            System.out.println("Nenhuma despesa cadastrada.");
            return;
        }

        File[] arquivos = dir.listFiles();
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhuma despesa para excluir.");
            return;
        }

        System.out.println("\n=== EXCLUIR DESPESA ===");

        for (int i = 0; i < arquivos.length; i++) {
            System.out.println((i + 1) + " - " + arquivos[i].getName());
        }

        System.out.print("Escolha a despesa: ");
        int op = sc.nextInt();

        if (op < 1 || op > arquivos.length) {
            System.out.println("Opção inválida.");
            return;
        }

        if (arquivos[op - 1].delete()) {
            System.out.println("Despesa excluída com sucesso!");
        } else {
            System.out.println("Erro ao excluir despesa.");
        }
    }

    private static void iniciarResetar(String pasta, String pastaUsuarios, String arquivoID) {

        // Pasta principal
        File dados = new File("dados");
        if (!dados.exists()) {
            dados.mkdir();
        }

        // Subpastas
        File usuarios = new File("dados/usuarios");
        if (!usuarios.exists()) {
            usuarios.mkdir();
        }

        File receitas = new File("dados/receitas");
        if (!receitas.exists()) {
            receitas.mkdir();
        }

        File despesas = new File("dados/despesas");
        if (!despesas.exists()) {
            despesas.mkdir();
        }

        // Arquivo de controle de ID do usuário
        File idUsuario = new File("dados/idUsuario.txt");

        try {
            PrintWriter pw = new PrintWriter(idUsuario);
            pw.println(0); // começa do zero
            pw.close();
        } catch (Exception e) {
            System.out.println("Erro ao criar arquivo de ID.");
        }

        System.out.println("Sistema iniciado / resetado com sucesso!");
    }

}