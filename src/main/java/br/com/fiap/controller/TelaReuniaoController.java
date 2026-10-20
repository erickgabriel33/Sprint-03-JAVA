package br.com.fiap.controller;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ReuniaoDTO;
import br.com.fiap.model.dto.StatusDTO;
import br.com.fiap.model.dto.TranscricaoDTO;
import br.com.fiap.model.entity.Cliente;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.repository.AnaliseRepository;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.model.repository.TranscricaoRepository;
import br.com.fiap.model.repository.UsuarioRepository;
import br.com.fiap.service.ReuniaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Telas de reunioes feitas com Thymeleaf
@RestController
@RequestMapping("/telas/reunioes")
public class TelaReuniaoController {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private ReuniaoService reuniaoService;

    @GetMapping()
    public ModelAndView consultar() {
        List<Reuniao> reunioes = reuniaoRepository.findAll();
        Map<Long, String> nomesClientes = new HashMap<>();
        for (Cliente cliente : clienteRepository.findAll()) {
            nomesClientes.put(cliente.getCodigo(), cliente.getNome());
        }
        ModelAndView mv = new ModelAndView("reunioes");
        mv.addObject("reunioes", reunioes);
        mv.addObject("nomesClientes", nomesClientes);
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo() {
        return montarFormulario(null, null);
    }

    @PostMapping
    public ModelAndView cadastrar(@Valid ReuniaoDTO reuniaoDTO, BindingResult result) {
        if (result.hasErrors()) {
            return montarFormulario(reuniaoDTO, "Campo " + result.getFieldError().getField() + ": "
                    + result.getFieldError().getDefaultMessage());
        }
        try {
            reuniaoService.cadastrar(reuniaoDTO);
        } catch (NegocioException e) {
            return montarFormulario(reuniaoDTO, e.getMessage());
        }
        ModelAndView mv = new ModelAndView("redirect:/telas/reunioes");
        return mv;
    }

    @GetMapping("/{codigo}")
    public ModelAndView detalhar(@PathVariable Long codigo) {
        if (!reuniaoRepository.existsById(codigo)) {
            return new ModelAndView("redirect:/telas/reunioes");
        }
        return montarDetalhe(codigo, null);
    }

    @PostMapping("/{codigo}/transcricao")
    public ModelAndView enviarTranscricao(@PathVariable Long codigo, @Valid TranscricaoDTO transcricaoDTO, BindingResult result) {
        if (result.hasErrors()) {
            return montarDetalhe(codigo, "A transcrição deve ter entre 20 e 4000 caracteres.");
        }
        try {
            reuniaoService.enviarTranscricao(codigo, transcricaoDTO.texto());
        } catch (NegocioException e) {
            return montarDetalhe(codigo, e.getMessage());
        }
        ModelAndView mv = new ModelAndView("redirect:/telas/reunioes/" + codigo);
        return mv;
    }

    @PostMapping("/{codigo}/status")
    public ModelAndView alterarStatus(@PathVariable Long codigo, @Valid StatusDTO statusDTO, BindingResult result) {
        if (result.hasErrors()) {
            return montarDetalhe(codigo, "Status inválido.");
        }
        try {
            reuniaoService.alterarStatus(codigo, statusDTO.status());
        } catch (NegocioException e) {
            return montarDetalhe(codigo, e.getMessage());
        }
        ModelAndView mv = new ModelAndView("redirect:/telas/reunioes/" + codigo);
        return mv;
    }

    private ModelAndView montarFormulario(ReuniaoDTO reuniaoDTO, String erro) {
        ModelAndView mv = new ModelAndView("formulario-reuniao");
        mv.addObject("reuniao", reuniaoDTO);
        mv.addObject("clientes", clienteRepository.findAll());
        mv.addObject("usuarios", usuarioRepository.findAll());
        mv.addObject("erro", erro);
        return mv;
    }

    private ModelAndView montarDetalhe(Long codigo, String erro) {
        Reuniao reuniao = reuniaoRepository.findByCodigo(codigo);
        ModelAndView mv = new ModelAndView("reuniao");
        mv.addObject("reuniao", reuniao);
        mv.addObject("cliente", clienteRepository.findByCodigo(reuniao.getCodigoCliente()));
        mv.addObject("usuario", usuarioRepository.findByCodigo(reuniao.getCodigoUsuario()));
        mv.addObject("transcricao", transcricaoRepository.findByCodigoReuniao(codigo));
        mv.addObject("analise", analiseRepository.findByCodigoReuniao(codigo));
        mv.addObject("erro", erro);
        return mv;
    }
}
