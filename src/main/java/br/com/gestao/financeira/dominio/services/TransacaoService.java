package br.com.gestao.financeira.dominio.services;

import br.com.gestao.financeira.dominio.enums.CategoriaTransacao;
import br.com.gestao.financeira.dominio.entity.Transacao;
import br.com.gestao.financeira.dominio.repository.TransacaoRepository;
import br.com.gestao.financeira.dominio.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Serviço de domínio responsável pela gestão de transações financeiras.
 */
@Service
@Transactional
@SuppressWarnings("null")
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra uma nova transação.
     * 
     * @param transacao dados da transação
     * @return a transação registrada
     * @throws UsuarioService.UsuarioNaoEncontradoException se usuário não existe
     */
    public Transacao registrarTransacao(Transacao transacao) {
        validarUsuarioExiste(transacao.getUsuarioId());

        if (transacao.getData() == null) {
            transacao.setData(LocalDateTime.now());
        }
        if (transacao.getMoedaOriginal() == null || transacao.getMoedaOriginal().isBlank()) {
            transacao.setMoedaOriginal("BRL");
        }

        return transacaoRepository.save(transacao);
    }

    /**
     * Atualiza uma transação existente.
     * 
     * @param id          identificador da transação
     * @param atualizacao dados para atualização
     * @return a transação atualizada
     * @throws TransacaoNaoEncontradaException se transação não existe
     */
    public Transacao atualizarTransacao(Long id, Transacao atualizacao) {
        Transacao existente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));

        atualizarCampo(atualizacao.getValorOriginal(), existente::setValorOriginal);
        atualizarCampo(atualizacao.getMoedaOriginal(), existente::setMoedaOriginal);
        atualizarCampo(atualizacao.getCategoria(), existente::setCategoria);
        atualizarCampo(atualizacao.getDescricao(), existente::setDescricao);
        atualizarCampo(atualizacao.getTipo(), existente::setTipo);

        return transacaoRepository.save(existente);
    }

    private <T> void atualizarCampo(T valor, Consumer<T> setter) {
        if (valor != null) {
            setter.accept(valor);
        }
    }

    /**
     * Exclui uma transação pelo ID.
     * 
     * @param id identificador da transação
     * @throws TransacaoNaoEncontradaException se transação não existe
     */
    public void excluirTransacao(Long id) {
        if (!transacaoRepository.existsById(id)) {
            throw new TransacaoNaoEncontradaException(id);
        }
        transacaoRepository.deleteById(id);
    }

    /**
     * Obtém os detalhes de uma transação.
     * 
     * @param id identificador da transação
     * @return a transação encontrada
     * @throws TransacaoNaoEncontradaException se transação não existe
     */
    public Transacao detalharTransacao(Long id) {
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));
    }

    /**
     * Lista transações de um usuário com filtros opcionais e paginação.
     * 
     * @param usuarioId  identificador do usuário
     * @param dataInicio data inicial do período (opcional)
     * @param dataFim    data final do período (opcional)
     * @param categoria  categoria para filtrar (opcional)
     * @param moeda      moeda para filtrar (opcional)
     * @param pageable   informações de paginação
     * @return página de transações filtradas
     */
    public Page<Transacao> listarTransacoes(Long usuarioId, LocalDateTime dataInicio,
            LocalDateTime dataFim, CategoriaTransacao categoria,
            String moeda, Pageable pageable) {

        if (usuarioId != null) {
            validarUsuarioExiste(usuarioId);
        }

        Specification<Transacao> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            adicionarFiltro(predicates, usuarioId, v -> cb.equal(root.get("usuarioId"), v));
            adicionarFiltro(predicates, dataInicio, v -> cb.greaterThanOrEqualTo(root.get("data"), v));
            adicionarFiltro(predicates, dataFim, v -> cb.lessThanOrEqualTo(root.get("data"), v));
            adicionarFiltro(predicates, categoria, v -> cb.equal(root.get("categoria"), v));
            adicionarFiltro(predicates, moeda, v -> cb.equal(root.get("moedaOriginal"), v));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return transacaoRepository.findAll(spec, pageable);
    }

    private <T> void adicionarFiltro(List<Predicate> predicates, T valor,
            Function<T, Predicate> regra) {
        if (valor != null) {
            predicates.add(regra.apply(valor));
        }
    }

    /**
     * Lista todas as transações de um usuário.
     * 
     * @param usuarioId identificador do usuário
     * @return lista de transações do usuário
     */
    public List<Transacao> listarPorUsuario(Long usuarioId) {
        validarUsuarioExiste(usuarioId);
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    private void validarUsuarioExiste(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        }
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioService.UsuarioNaoEncontradoException(usuarioId);
        }
    }

    public static class TransacaoNaoEncontradaException extends RuntimeException {
        public TransacaoNaoEncontradaException(Long id) {
            super("Transação não encontrada com ID: " + id);
        }
    }
}
