package br.com.fiap.controller;

import br.com.fiap.service.DashboardService;
import br.com.fiap.service.ReuniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

// Tela inicial (dashboard) feita com Thymeleaf
@RestController
public class TelaInicioController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReuniaoService reuniaoService;

    // Quem acessar localhost:8080 e levado para a tela inicial
    @GetMapping("/")
    public ModelAndView raiz() {
        ModelAndView mv = new ModelAndView("redirect:/telas");
        return mv;
    }

    @GetMapping("/telas")
    public ModelAndView inicio() {
        ModelAndView mv = new ModelAndView("inicio");
        mv.addObject("resumo", dashboardService.gerarResumo());
        mv.addObject("prioritarias", reuniaoService.listarPrioritarias());
        mv.addObject("clientesRisco", dashboardService.listarClientesEmRisco());
        return mv;
    }
}
