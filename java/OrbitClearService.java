import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * OrbitClearService: camada de servico (regra de negocio) do marketplace.
 *
 * Mantem os dados em memoria com ArrayLists (SEM banco real, conforme as
 * restricoes do projeto) e oferece:
 *   - CRUD (cadastrar / listar / buscarPorId / atualizar) das 4 entidades;
 *   - agendarContrato(...) -> cria um contrato ligando objeto + operador;
 *   - concluirContrato(...) -> fecha o contrato e marca o objeto como REMOVIDO.
 *
 * O construtor PRE-CARREGA os mesmos dados de exemplo do script banco/orbitclear.sql
 * (agencias NASA/ESA/INPE; operadores ClearSpace/Astroscale/Orbital Cleaners;
 * objetos Ariane-5 R/B, Envisat, Cosmos-2251, CBERS-1), para a demonstracao
 * comecar ja populada.
 */
public class OrbitClearService {

    private final List<ClienteOperador> clientes = new ArrayList<>();
    private final List<OperadorMissao> operadores = new ArrayList<>();
    private final List<ObjetoOrbital> objetos = new ArrayList<>();
    private final List<ContratoRemocao> contratos = new ArrayList<>();

    // Geradores de id (proximo id livre de cada entidade).
    private int proximoIdCliente = 1;
    private int proximoIdOperador = 1;
    private int proximoIdObjeto = 1;
    private int proximoIdContrato = 1;

    public OrbitClearService() {
        carregarDadosDeExemplo();
    }

    // ============================================================
    // Carga inicial (espelha banco/orbitclear.sql)
    // ============================================================

    private void carregarDadosDeExemplo() {
        // --- Clientes (agencias donas dos objetos) ---
        ClienteOperador nasa = new ClienteOperador(proximoIdCliente++, "NASA", "EUA", "orbital@nasa.gov");
        ClienteOperador esa = new ClienteOperador(proximoIdCliente++, "ESA", "Europa", "debris@esa.int");
        ClienteOperador inpe = new ClienteOperador(proximoIdCliente++, "INPE", "Brasil", "contato@inpe.br");
        clientes.add(nasa);
        clientes.add(esa);
        clientes.add(inpe);

        // --- Operadores (empresas caca-detritos) ---
        OperadorMissao clearSpace = new OperadorMissao(proximoIdOperador++, "ClearSpace", "Suica",
                TecnologiaCaptura.BRACO_ROBOTICO, 500.00, 1200000.00);
        OperadorMissao astroscale = new OperadorMissao(proximoIdOperador++, "Astroscale", "Japao",
                TecnologiaCaptura.REDE, 300.00, 850000.00);
        OperadorMissao orbitalCleaners = new OperadorMissao(proximoIdOperador++, "Orbital Cleaners", "Brasil",
                TecnologiaCaptura.ARPAO, 200.00, 600000.00);
        operadores.add(clearSpace);
        operadores.add(astroscale);
        operadores.add(orbitalCleaners);

        // --- Objetos orbitais (o lixo) ---
        ObjetoOrbital ariane = new ObjetoOrbital(proximoIdObjeto++, "Estagio superior Ariane-5 R/B",
                TipoObjeto.ESTAGIO_FOGUETE, 780, 1200.00, NivelRisco.CRITICO, StatusRemocao.CATALOGADO, esa);
        ObjetoOrbital envisat = new ObjetoOrbital(proximoIdObjeto++, "Satelite Envisat (inativo)",
                TipoObjeto.SATELITE_MORTO, 770, 8211.00, NivelRisco.CRITICO, StatusRemocao.EM_NEGOCIACAO, esa);
        ObjetoOrbital cosmos = new ObjetoOrbital(proximoIdObjeto++, "Fragmento Cosmos-2251",
                TipoObjeto.FRAGMENTO, 790, 4.50, NivelRisco.ALTO, StatusRemocao.CATALOGADO, nasa);
        ObjetoOrbital cbers = new ObjetoOrbital(proximoIdObjeto++, "CBERS-1 (desativado)",
                TipoObjeto.SATELITE_MORTO, 740, 1450.00, NivelRisco.MEDIO, StatusRemocao.AGENDADO, inpe);
        objetos.add(ariane);
        objetos.add(envisat);
        objetos.add(cosmos);
        objetos.add(cbers);

        // --- Contratos de remocao (os "pedidos") ---
        contratos.add(new ContratoRemocao(proximoIdContrato++, cbers, orbitalCleaners,
                LocalDate.of(2026, 8, 15), 600000.00, StatusContrato.AGENDADO));
        contratos.add(new ContratoRemocao(proximoIdContrato++, envisat, clearSpace,
                LocalDate.of(2026, 9, 1), 1500000.00, StatusContrato.PROPOSTO));
    }

    // ============================================================
    // CRUD - ClienteOperador
    // ============================================================

    /** Cadastra uma agencia cliente; o id e atribuido automaticamente. */
    public ClienteOperador cadastrarCliente(String nomeAgencia, String paisOrigem, String contatoEmail) {
        ClienteOperador cliente = new ClienteOperador(proximoIdCliente++, nomeAgencia, paisOrigem, contatoEmail);
        clientes.add(cliente);
        return cliente;
    }

    public List<ClienteOperador> listarClientes() {
        return clientes;
    }

    public ClienteOperador buscarClientePorId(int id) {
        for (ClienteOperador cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    /** Atualiza os dados de uma agencia existente. Retorna false se o id nao existe. */
    public boolean atualizarCliente(int id, String nomeAgencia, String paisOrigem, String contatoEmail) {
        ClienteOperador cliente = buscarClientePorId(id);
        if (cliente == null) {
            return false;
        }
        cliente.setNomeAgencia(nomeAgencia);
        cliente.setPaisOrigem(paisOrigem);
        cliente.setContatoEmail(contatoEmail);
        return true;
    }

    // ============================================================
    // CRUD - OperadorMissao
    // ============================================================

    public OperadorMissao cadastrarOperador(String nomeEmpresa, String pais,
                                            TecnologiaCaptura tecnologiaCaptura,
                                            double capacidadeKgPorMissao, double precoBaseUSD) {
        OperadorMissao operador = new OperadorMissao(proximoIdOperador++, nomeEmpresa, pais,
                tecnologiaCaptura, capacidadeKgPorMissao, precoBaseUSD);
        operadores.add(operador);
        return operador;
    }

    public List<OperadorMissao> listarOperadores() {
        return operadores;
    }

    public OperadorMissao buscarOperadorPorId(int id) {
        for (OperadorMissao operador : operadores) {
            if (operador.getId() == id) {
                return operador;
            }
        }
        return null;
    }

    public boolean atualizarOperador(int id, String nomeEmpresa, String pais,
                                     TecnologiaCaptura tecnologiaCaptura,
                                     double capacidadeKgPorMissao, double precoBaseUSD) {
        OperadorMissao operador = buscarOperadorPorId(id);
        if (operador == null) {
            return false;
        }
        operador.setNomeEmpresa(nomeEmpresa);
        operador.setPais(pais);
        operador.setTecnologiaCaptura(tecnologiaCaptura);
        operador.setCapacidadeKgPorMissao(capacidadeKgPorMissao);
        operador.setPrecoBaseUSD(precoBaseUSD);
        return true;
    }

    // ============================================================
    // CRUD - ObjetoOrbital
    // ============================================================

    public ObjetoOrbital cadastrarObjeto(String nome, TipoObjeto tipo, int altitudeKm, double massaKg,
                                         NivelRisco nivelRiscoColisao, StatusRemocao statusRemocao,
                                         ClienteOperador dono) {
        ObjetoOrbital objeto = new ObjetoOrbital(proximoIdObjeto++, nome, tipo, altitudeKm, massaKg,
                nivelRiscoColisao, statusRemocao, dono);
        objetos.add(objeto);
        return objeto;
    }

    public List<ObjetoOrbital> listarObjetos() {
        return objetos;
    }

    public ObjetoOrbital buscarObjetoPorId(int id) {
        for (ObjetoOrbital objeto : objetos) {
            if (objeto.getId() == id) {
                return objeto;
            }
        }
        return null;
    }

    /** Atualiza apenas o status de remocao de um objeto (transicao do fluxo). */
    public boolean atualizarStatusObjeto(int id, StatusRemocao novoStatus) {
        ObjetoOrbital objeto = buscarObjetoPorId(id);
        if (objeto == null) {
            return false;
        }
        objeto.setStatusRemocao(novoStatus);
        return true;
    }

    // ============================================================
    // CRUD - ContratoRemocao + regras do marketplace
    // ============================================================

    public List<ContratoRemocao> listarContratos() {
        return contratos;
    }

    public ContratoRemocao buscarContratoPorId(int id) {
        for (ContratoRemocao contrato : contratos) {
            if (contrato.getId() == id) {
                return contrato;
            }
        }
        return null;
    }

    /**
     * Agenda um contrato de remocao ligando um objeto a um operador.
     * Cria o contrato com status AGENDADO e move o objeto para AGENDADO.
     * Lanca IllegalArgumentException se o objeto ou o operador nao existirem,
     * ou se o objeto ja estiver removido.
     */
    public ContratoRemocao agendarContrato(int objetoId, int operadorId, LocalDate dataJanela, double valorUSD) {
        ObjetoOrbital objeto = buscarObjetoPorId(objetoId);
        if (objeto == null) {
            throw new IllegalArgumentException("Objeto orbital #" + objetoId + " nao encontrado.");
        }
        OperadorMissao operador = buscarOperadorPorId(operadorId);
        if (operador == null) {
            throw new IllegalArgumentException("Operador de missao #" + operadorId + " nao encontrado.");
        }
        if (objeto.getStatusRemocao() == StatusRemocao.REMOVIDO) {
            throw new IllegalArgumentException("O objeto \"" + objeto.getNome()
                    + "\" ja foi removido; nao da para agendar novo contrato.");
        }

        ContratoRemocao contrato = new ContratoRemocao(proximoIdContrato++, objeto, operador,
                dataJanela, valorUSD, StatusContrato.AGENDADO);
        contratos.add(contrato);

        // Refletir no objeto que ele agora tem janela agendada.
        objeto.setStatusRemocao(StatusRemocao.AGENDADO);
        return contrato;
    }

    /**
     * Conclui um contrato: marca o contrato como CONCLUIDO e o objeto associado
     * como REMOVIDO (a remocao aconteceu). Retorna false se o contrato nao existe.
     * Lanca IllegalStateException se o contrato estiver cancelado.
     */
    public boolean concluirContrato(int contratoId) {
        ContratoRemocao contrato = buscarContratoPorId(contratoId);
        if (contrato == null) {
            return false;
        }
        if (contrato.getStatus() == StatusContrato.CANCELADO) {
            throw new IllegalStateException("Contrato #" + contratoId
                    + " esta cancelado; nao pode ser concluido.");
        }
        contrato.setStatus(StatusContrato.CONCLUIDO);
        if (contrato.getObjeto() != null) {
            contrato.getObjeto().setStatusRemocao(StatusRemocao.REMOVIDO);
        }
        return true;
    }
}
