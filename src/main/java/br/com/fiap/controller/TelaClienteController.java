package br.com.fiap.controller;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ClienteDTO;
import br.com.fiap.model.entity.Cliente;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

// Telas de clientes feitas com Thymeleaf
@RestController
@RequestMapping("/telas/clientes")
public class TelaClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    @GetMapping()
    public ModelAndView consultar() {
        List<Cliente> clientes = clienteRepository.findAll();
        ModelAndView mv = new ModelAndView("clientes");
        mv.addObject("clientes", clientes);
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo() {
        ModelAndView mv = new ModelAndView("formulario-cliente");
        return mv;
    }

    @PostMapping
    public ModelAndView cadastrar(@Valid ClienteDTO clienteDTO, BindingResult result) {
        if (result.hasErrors()) {
            return voltarAoFormulario(clienteDTO, "Campo " + result.getFieldError().getField() + ": "
                    + result.getFieldError().getDefaultMessage());
        }
        try {
            clienteService.cadastrar(clienteDTO);
        } catch (NegocioException e) {
            return voltarAoFormulario(clienteDTO, e.getMessage());
        }
        ModelAndView mv = new ModelAndView("redirect:/telas/clientes");
        return mv;
    }

    private ModelAndView voltarAoFormulario(ClienteDTO clienteDTO, String erro) {
        ModelAndView mv = new ModelAndView("formulario-cliente");
        mv.addObject("cliente", clienteDTO);
        mv.addObject("erro", erro);
        return mv;
    }
}
