import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main: menu de console do OrbitClear (marketplace SIMULADO de remocao de
 * lixo espacial). Usa Scanner e um loop while para navegar entre as operacoes
 * do OrbitClearService.
 *
 * Ao listar objetos orbitais, mostra o selo "Impacto na Terra"
 * (ObjetoOrbital.impactoNaTerra()) — o diferencial do projeto, que amarra o lixo
 * espacial ao impacto concreto na Terra (GPS, meteorologia, comunicacoes, agro).
 *
 * Tudo em memoria, sem banco e sem integracao com as outras pecas.
 */
public class Main {

    // Servico com toda a regra de negocio e os dados de exemplo pre-carregados.
    private static final OrbitClearService service = new OrbitClearService();

    // Scanner unico para toda a aplicacao.
    private static final Scanner scanner = new Scanner(System.in);

    // Formato de data aceito na entrada do usuario (ex.: 2026-08-15).
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("   ORBITCLEAR - Marketplace de remocao de lixo espacial");
        System.out.println("   Limpar a orbita e manter a Terra funcionando.");
        System.out.println("============================================================");

        boolean executando = true;
        while (executando) {
            exibirMenuPrincipal();
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> menuObjetos();
                case 2 -> menuOperadores();
                case 3 -> menuClientes();
                case 4 -> menuContratos();
                case 0 -> {
                    executando = false;
                    System.out.println("\nObrigado por usar o OrbitClear. Orbita mais limpa, Terra mais segura!");
                }
                default -> System.out.println(">> Opcao invalida. Tente novamente.");
            }
        }

        scanner.close();
    }

    // ============================================================
    // Menu principal
    // ============================================================

    private static void exibirMenuPrincipal() {
        System.out.println("\n========== MENU PRINCIPAL ==========");
        System.out.println("1 - Objetos orbitais (o lixo)");
        System.out.println("2 - Operadores de missao (caca-detritos)");
        System.out.println("3 - Clientes (agencias donas)");
        System.out.println("4 - Contratos de remocao (pedidos)");
        System.out.println("0 - Sair");
        System.out.println("====================================");
    }

    // ============================================================
    // Menu: Objetos orbitais
    // ============================================================

    private static void menuObjetos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n----- OBJETOS ORBITAIS -----");
            System.out.println("1 - Listar objetos (com selo Impacto na Terra)");
            System.out.println("2 - Buscar objeto por id");
            System.out.println("3 - Cadastrar novo objeto");
            System.out.println("4 - Atualizar status de remocao");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> listarObjetos();
                case 2 -> buscarObjeto();
                case 3 -> cadastrarObjeto();
                case 4 -> atualizarStatusObjeto();
                case 0 -> voltar = true;
                default -> System.out.println(">> Opcao invalida.");
            }
        }
    }

    private static void listarObjetos() {
        List<ObjetoOrbital> objetos = service.listarObjetos();
        if (objetos.isEmpty()) {
            System.out.println("Nenhum objeto orbital catalogado.");
            return;
        }
        System.out.println("\n=== Objetos orbitais catalogados ===");
        for (ObjetoOrbital objeto : objetos) {
            System.out.println(objeto);
            // DIFERENCIAL: o selo "Impacto na Terra" aparece logo abaixo de cada objeto.
            System.out.println("   >> Impacto na Terra: " + objeto.impactoNaTerra());
            System.out.println();
        }
    }

    private static void buscarObjeto() {
        int id = lerInteiro("Id do objeto: ");
        ObjetoOrbital objeto = service.buscarObjetoPorId(id);
        if (objeto == null) {
            System.out.println(">> Objeto #" + id + " nao encontrado.");
            return;
        }
        System.out.println(objeto);
        System.out.println("   >> Impacto na Terra: " + objeto.impactoNaTerra());
    }

    private static void cadastrarObjeto() {
        System.out.println("\n-- Novo objeto orbital --");
        String nome = lerTexto("Nome: ");
        TipoObjeto tipo = lerTipoObjeto();
        int altitude = lerInteiro("Altitude (km): ");
        double massa = lerDouble("Massa (kg): ");
        NivelRisco risco = lerNivelRisco();
        StatusRemocao status = lerStatusRemocao();

        ClienteOperador dono = escolherCliente();
        if (dono == null) {
            System.out.println(">> Cadastro cancelado: e preciso ter um cliente dono.");
            return;
        }

        ObjetoOrbital novo = service.cadastrarObjeto(nome, tipo, altitude, massa, risco, status, dono);
        System.out.println(">> Objeto cadastrado com sucesso!");
        System.out.println(novo);
        System.out.println("   >> Impacto na Terra: " + novo.impactoNaTerra());
    }

    private static void atualizarStatusObjeto() {
        int id = lerInteiro("Id do objeto a atualizar: ");
        ObjetoOrbital objeto = service.buscarObjetoPorId(id);
        if (objeto == null) {
            System.out.println(">> Objeto #" + id + " nao encontrado.");
            return;
        }
        System.out.println("Status atual: " + objeto.getStatusRemocao().getRotulo());
        StatusRemocao novoStatus = lerStatusRemocao();
        service.atualizarStatusObjeto(id, novoStatus);
        System.out.println(">> Status atualizado para: " + novoStatus.getRotulo());
    }

    // ============================================================
    // Menu: Operadores de missao
    // ============================================================

    private static void menuOperadores() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n----- OPERADORES DE MISSAO -----");
            System.out.println("1 - Listar operadores");
            System.out.println("2 - Buscar operador por id");
            System.out.println("3 - Cadastrar novo operador");
            System.out.println("4 - Atualizar operador");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> listarOperadores();
                case 2 -> buscarOperador();
                case 3 -> cadastrarOperador();
                case 4 -> atualizarOperador();
                case 0 -> voltar = true;
                default -> System.out.println(">> Opcao invalida.");
            }
        }
    }

    private static void listarOperadores() {
        List<OperadorMissao> operadores = service.listarOperadores();
        if (operadores.isEmpty()) {
            System.out.println("Nenhum operador cadastrado.");
            return;
        }
        System.out.println("\n=== Operadores de missao ===");
        for (OperadorMissao operador : operadores) {
            System.out.println(operador);
        }
    }

    private static void buscarOperador() {
        int id = lerInteiro("Id do operador: ");
        OperadorMissao operador = service.buscarOperadorPorId(id);
        if (operador == null) {
            System.out.println(">> Operador #" + id + " nao encontrado.");
            return;
        }
        System.out.println(operador);
    }

    private static void cadastrarOperador() {
        System.out.println("\n-- Novo operador de missao --");
        String nome = lerTexto("Nome da empresa: ");
        String pais = lerTexto("Pais: ");
        TecnologiaCaptura tecnologia = lerTecnologiaCaptura();
        double capacidade = lerDouble("Capacidade (kg por missao): ");
        double preco = lerDouble("Preco base (USD): ");

        OperadorMissao novo = service.cadastrarOperador(nome, pais, tecnologia, capacidade, preco);
        System.out.println(">> Operador cadastrado com sucesso!");
        System.out.println(novo);
    }

    private static void atualizarOperador() {
        int id = lerInteiro("Id do operador a atualizar: ");
        OperadorMissao operador = service.buscarOperadorPorId(id);
        if (operador == null) {
            System.out.println(">> Operador #" + id + " nao encontrado.");
            return;
        }
        System.out.println("Editando: " + operador);
        String nome = lerTexto("Novo nome da empresa: ");
        String pais = lerTexto("Novo pais: ");
        TecnologiaCaptura tecnologia = lerTecnologiaCaptura();
        double capacidade = lerDouble("Nova capacidade (kg por missao): ");
        double preco = lerDouble("Novo preco base (USD): ");

        service.atualizarOperador(id, nome, pais, tecnologia, capacidade, preco);
        System.out.println(">> Operador atualizado com sucesso!");
    }

    // ============================================================
    // Menu: Clientes (agencias)
    // ============================================================

    private static void menuClientes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n----- CLIENTES (AGENCIAS) -----");
            System.out.println("1 - Listar clientes");
            System.out.println("2 - Buscar cliente por id");
            System.out.println("3 - Cadastrar novo cliente");
            System.out.println("4 - Atualizar cliente");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> listarClientes();
                case 2 -> buscarCliente();
                case 3 -> cadastrarCliente();
                case 4 -> atualizarCliente();
                case 0 -> voltar = true;
                default -> System.out.println(">> Opcao invalida.");
            }
        }
    }

    private static void listarClientes() {
        List<ClienteOperador> clientes = service.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("\n=== Clientes (agencias donas) ===");
        for (ClienteOperador cliente : clientes) {
            System.out.println(cliente);
        }
    }

    private static void buscarCliente() {
        int id = lerInteiro("Id do cliente: ");
        ClienteOperador cliente = service.buscarClientePorId(id);
        if (cliente == null) {
            System.out.println(">> Cliente #" + id + " nao encontrado.");
            return;
        }
        System.out.println(cliente);
    }

    private static void cadastrarCliente() {
        System.out.println("\n-- Novo cliente (agencia) --");
        String nome = lerTexto("Nome da agencia: ");
        String pais = lerTexto("Pais de origem: ");
        String email = lerTexto("Email de contato: ");

        ClienteOperador novo = service.cadastrarCliente(nome, pais, email);
        System.out.println(">> Cliente cadastrado com sucesso!");
        System.out.println(novo);
    }

    private static void atualizarCliente() {
        int id = lerInteiro("Id do cliente a atualizar: ");
        ClienteOperador cliente = service.buscarClientePorId(id);
        if (cliente == null) {
            System.out.println(">> Cliente #" + id + " nao encontrado.");
            return;
        }
        System.out.println("Editando: " + cliente);
        String nome = lerTexto("Novo nome da agencia: ");
        String pais = lerTexto("Novo pais de origem: ");
        String email = lerTexto("Novo email de contato: ");

        service.atualizarCliente(id, nome, pais, email);
        System.out.println(">> Cliente atualizado com sucesso!");
    }

    // ============================================================
    // Menu: Contratos de remocao
    // ============================================================

    private static void menuContratos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n----- CONTRATOS DE REMOCAO -----");
            System.out.println("1 - Listar contratos");
            System.out.println("2 - Buscar contrato por id");
            System.out.println("3 - Agendar contrato (objeto + operador)");
            System.out.println("4 - Concluir contrato (remove o objeto)");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> listarContratos();
                case 2 -> buscarContrato();
                case 3 -> agendarContrato();
                case 4 -> concluirContrato();
                case 0 -> voltar = true;
                default -> System.out.println(">> Opcao invalida.");
            }
        }
    }

    private static void listarContratos() {
        List<ContratoRemocao> contratos = service.listarContratos();
        if (contratos.isEmpty()) {
            System.out.println("Nenhum contrato registrado.");
            return;
        }
        System.out.println("\n=== Contratos de remocao ===");
        for (ContratoRemocao contrato : contratos) {
            System.out.println(contrato);
        }
    }

    private static void buscarContrato() {
        int id = lerInteiro("Id do contrato: ");
        ContratoRemocao contrato = service.buscarContratoPorId(id);
        if (contrato == null) {
            System.out.println(">> Contrato #" + id + " nao encontrado.");
            return;
        }
        System.out.println(contrato);
    }

    private static void agendarContrato() {
        System.out.println("\n-- Agendar contrato de remocao --");

        if (service.listarObjetos().isEmpty() || service.listarOperadores().isEmpty()) {
            System.out.println(">> E preciso ter ao menos um objeto e um operador cadastrados.");
            return;
        }

        System.out.println("Objetos disponiveis:");
        for (ObjetoOrbital objeto : service.listarObjetos()) {
            System.out.println("  " + objeto);
        }
        int objetoId = lerInteiro("Id do objeto a remover: ");

        System.out.println("Operadores disponiveis:");
        for (OperadorMissao operador : service.listarOperadores()) {
            System.out.println("  " + operador);
        }
        int operadorId = lerInteiro("Id do operador que executa: ");

        LocalDate data = lerData("Data da janela de desorbita (yyyy-MM-dd): ");
        double valor = lerDouble("Valor do contrato (USD): ");

        try {
            ContratoRemocao contrato = service.agendarContrato(objetoId, operadorId, data, valor);
            System.out.println(">> Contrato agendado com sucesso!");
            System.out.println(contrato);
        } catch (IllegalArgumentException e) {
            System.out.println(">> Nao foi possivel agendar: " + e.getMessage());
        }
    }

    private static void concluirContrato() {
        int id = lerInteiro("Id do contrato a concluir: ");
        try {
            boolean ok = service.concluirContrato(id);
            if (!ok) {
                System.out.println(">> Contrato #" + id + " nao encontrado.");
                return;
            }
            ContratoRemocao contrato = service.buscarContratoPorId(id);
            System.out.println(">> Contrato concluido! O objeto foi marcado como REMOVIDO.");
            System.out.println(contrato);
        } catch (IllegalStateException e) {
            System.out.println(">> Nao foi possivel concluir: " + e.getMessage());
        }
    }

    // ============================================================
    // Selecao de entidades relacionadas
    // ============================================================

    /** Lista os clientes e deixa o usuario escolher um pelo id (para ser dono de um objeto). */
    private static ClienteOperador escolherCliente() {
        List<ClienteOperador> clientes = service.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println(">> Nenhum cliente cadastrado. Cadastre um cliente primeiro.");
            return null;
        }
        System.out.println("Clientes disponiveis (dono do objeto):");
        for (ClienteOperador cliente : clientes) {
            System.out.println("  " + cliente);
        }
        int id = lerInteiro("Id do cliente dono: ");
        ClienteOperador dono = service.buscarClientePorId(id);
        if (dono == null) {
            System.out.println(">> Cliente #" + id + " nao encontrado.");
        }
        return dono;
    }

    // ============================================================
    // Leitura de enums (menus auxiliares)
    // ============================================================

    private static TipoObjeto lerTipoObjeto() {
        System.out.println("Tipo do objeto:");
        TipoObjeto[] valores = TipoObjeto.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i].getRotulo());
        }
        int escolha = lerIntervalo("Escolha o tipo: ", 1, valores.length);
        return valores[escolha - 1];
    }

    private static NivelRisco lerNivelRisco() {
        System.out.println("Nivel de risco de colisao:");
        NivelRisco[] valores = NivelRisco.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i].getRotulo()
                    + "  (Impacto: " + valores[i].getImpactoNaTerra() + ")");
        }
        int escolha = lerIntervalo("Escolha o nivel: ", 1, valores.length);
        return valores[escolha - 1];
    }

    private static StatusRemocao lerStatusRemocao() {
        System.out.println("Status de remocao:");
        StatusRemocao[] valores = StatusRemocao.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i].getRotulo());
        }
        int escolha = lerIntervalo("Escolha o status: ", 1, valores.length);
        return valores[escolha - 1];
    }

    private static TecnologiaCaptura lerTecnologiaCaptura() {
        System.out.println("Tecnologia de captura:");
        TecnologiaCaptura[] valores = TecnologiaCaptura.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i].getRotulo());
        }
        int escolha = lerIntervalo("Escolha a tecnologia: ", 1, valores.length);
        return valores[escolha - 1];
    }

    // ============================================================
    // Utilitarios de leitura (entrada robusta via Scanner)
    // ============================================================

    /** Le uma linha de texto nao vazia. */
    private static String lerTexto(String rotulo) {
        while (true) {
            System.out.print(rotulo);
            String linha = scanner.nextLine().trim();
            if (!linha.isEmpty()) {
                return linha;
            }
            System.out.println(">> Valor nao pode ser vazio.");
        }
    }

    /** Le um inteiro qualquer; repete enquanto a entrada nao for um numero. */
    private static int lerInteiro(String rotulo) {
        while (true) {
            System.out.print(rotulo);
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println(">> Digite um numero inteiro valido.");
            }
        }
    }

    /** Le um inteiro dentro de [min, max]; repete enquanto estiver fora. */
    private static int lerIntervalo(String rotulo, int min, int max) {
        while (true) {
            int valor = lerInteiro(rotulo);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println(">> Escolha um numero entre " + min + " e " + max + ".");
        }
    }

    /** Le um numero decimal; aceita virgula ou ponto como separador. */
    private static double lerDouble(String rotulo) {
        while (true) {
            System.out.print(rotulo);
            String linha = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println(">> Digite um numero valido (ex.: 600000 ou 4.50).");
            }
        }
    }

    /** Le uma data no formato yyyy-MM-dd; repete enquanto invalida. */
    private static LocalDate lerData(String rotulo) {
        while (true) {
            System.out.print(rotulo);
            String linha = scanner.nextLine().trim();
            try {
                return LocalDate.parse(linha, FMT_DATA);
            } catch (DateTimeParseException e) {
                System.out.println(">> Data invalida. Use o formato yyyy-MM-dd (ex.: 2026-08-15).");
            }
        }
    }
}
