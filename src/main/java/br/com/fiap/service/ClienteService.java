package br.com.fiap.service;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ClienteDTO;
import br.com.fiap.model.entity.Cliente;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    public Cliente cadastrar(ClienteDTO clienteDTO) throws NegocioException {
        validarCnpj(clienteDTO.cnpj());
        if (clienteRepository.findByCnpj(clienteDTO.cnpj()) != null) {
            throw new NegocioException("Já existe um cliente cadastrado com o CNPJ " + clienteDTO.cnpj() + ".");
        }
        Cliente cliente = new Cliente(clienteDTO);
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long codigo, ClienteDTO clienteDTO) throws NegocioException {
        validarCnpj(clienteDTO.cnpj());
        Cliente clienteComMesmoCnpj = clienteRepository.findByCnpj(clienteDTO.cnpj());
        if (clienteComMesmoCnpj != null && !clienteComMesmoCnpj.getCodigo().equals(codigo)) {
            throw new NegocioException("O CNPJ " + clienteDTO.cnpj() + " já pertence a outro cliente.");
        }
        Cliente cliente = new Cliente(clienteDTO);
        cliente.setCodigo(codigo);
        return clienteRepository.save(cliente);
    }

    public void excluir(Long codigo) throws NegocioException {
        if (!reuniaoRepository.findByCodigoCliente(codigo).isEmpty()) {
            throw new NegocioException("O cliente possui reuniões cadastradas e não pode ser excluído.");
        }
        clienteRepository.deleteById(codigo);
    }

    // Regra de negocio: confere os dois digitos verificadores do CNPJ
    public void validarCnpj(String cnpj) throws NegocioException {
        if (cnpj == null || !cnpj.matches("\\d{14}") || cnpj.matches("(\\d)\\1{13}")) {
            throw new NegocioException("CNPJ inválido: informe 14 números.");
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int digito1 = calcularDigito(cnpj, pesos1);
        int digito2 = calcularDigito(cnpj, pesos2);
        if (digito1 != cnpj.charAt(12) - '0' || digito2 != cnpj.charAt(13) - '0') {
            throw new NegocioException("CNPJ inválido: dígitos verificadores não conferem.");
        }
    }

    private int calcularDigito(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
