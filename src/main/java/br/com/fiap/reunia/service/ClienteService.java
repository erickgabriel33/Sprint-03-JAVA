package br.com.fiap.reunia.service;

import br.com.fiap.reunia.dao.ClienteDAO;
import br.com.fiap.reunia.dao.ReuniaoDAO;
import br.com.fiap.reunia.exception.ConflitoException;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.exception.RecursoNaoEncontradoException;
import br.com.fiap.reunia.model.Cliente;
import br.com.fiap.reunia.util.ValidadorUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static br.com.fiap.reunia.factory.GerenciadorTransacao.consultar;
import static br.com.fiap.reunia.factory.GerenciadorTransacao.executarEmTransacao;

/**
 * Regras e validacoes de clientes (CNPJ valido e unico, porte obrigatorio...).
 */
@ApplicationScoped
public class ClienteService {

    public List<Cliente> listar(String nome) {
        String filtro = nome == null || nome.isBlank() ? null : nome.trim();
        return consultar(con -> new ClienteDAO(con).listar(filtro));
    }

    public Cliente buscarPorId(long id) {
        return consultar(con -> new ClienteDAO(con).buscarPorId(id))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }

    public Cliente cadastrar(Cliente cliente) {
        validar(cliente);
        return executarEmTransacao(con -> {
            ClienteDAO dao = new ClienteDAO(con);
            if (dao.existeCnpj(cliente.getCnpj(), null)) {
                throw new ConflitoException("Já existe um cliente com o CNPJ " + cliente.getCnpjFormatado() + ".");
            }
            return dao.inserir(cliente);
        });
    }

    public Cliente atualizar(long id, Cliente cliente) {
        validar(cliente);
        cliente.setId(id);
        return executarEmTransacao(con -> {
            ClienteDAO dao = new ClienteDAO(con);
            Cliente atual = dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
            if (dao.existeCnpj(cliente.getCnpj(), id)) {
                throw new ConflitoException("O CNPJ " + cliente.getCnpjFormatado() + " já pertence a outro cliente.");
            }
            cliente.setDataCadastro(atual.getDataCadastro());
            dao.atualizar(cliente);
            return cliente;
        });
    }

    public void excluir(long id) {
        executarEmTransacao(con -> {
            if (new ReuniaoDAO(con).contarPorCliente(id) > 0) {
                throw new ConflitoException("Cliente possui reuniões registradas e não pode ser excluído.");
            }
            if (!new ClienteDAO(con).excluir(id)) {
                throw new RecursoNaoEncontradoException("Cliente", id);
            }
            return null;
        });
    }

    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new NegocioException("Corpo da requisição é obrigatório.");
        }
        cliente.setNome(ValidadorUtil.textoObrigatorio(cliente.getNome(), "nome", 2, 120));
        cliente.setCnpj(ValidadorUtil.cnpj(cliente.getCnpj()));
        cliente.setSegmento(ValidadorUtil.textoObrigatorio(cliente.getSegmento(), "segmento", 2, 60));
        ValidadorUtil.obrigatorio(cliente.getPorte(), "porte");
        cliente.setEmailContato(ValidadorUtil.email(cliente.getEmailContato(), "emailContato", false));
    }
}
