package br.com.fiap.controller;

import br.com.fiap.service.DashboardService;
import br.com.fiap.service.ReuniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

// Tela inicial (dashboard) feita com Thymeleaf
@RestController
@RequestMapping("/telas")
public class TelaInicioController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReuniaoService reuniaoService;

    @GetMapping()
    public ModelAndView inicio() {
        ModelAndView mv = new ModelAndView("inicio");
        mv.addObject("resumo", dashboardService.gerarResumo());
        mv.addObject("prioritarias", reuniaoService.listarPrioritarias());
        mv.addObject("clientesRisco", dashboardService.listarClientesEmRisco());
        return mv;
    }
}
