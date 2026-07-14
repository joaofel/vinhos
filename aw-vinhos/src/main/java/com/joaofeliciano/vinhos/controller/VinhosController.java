package com.joaofeliciano.vinhos.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.joaofeliciano.vinhos.model.TipoVinho;
import com.joaofeliciano.vinhos.model.Vinho;
import com.joaofeliciano.vinhos.repository.Vinhos;
import com.joaofeliciano.vinhos.repository.filter.VinhoFilter;

@Controller
@RequestMapping("/vinhos")
public class VinhosController {
	
	@Autowired
	private Vinhos vinhos;
	
	@GetMapping("/novo")
	public ModelAndView novo(Vinho vinho) {
		ModelAndView mv = new ModelAndView("vinho/cadastro-vinho");
		mv.addObject(vinho);
		mv.addObject("tipos", TipoVinho.values());	
		return mv;
	}
	
	@PostMapping("/novo")
	public ModelAndView salvar(@Valid Vinho vinho, BindingResult result, 
			RedirectAttributes attributes) {	
		if(result.hasErrors()) {
			return novo(vinho);
		}
		
		vinhos.save(vinho);
		attributes.addFlashAttribute("mensagem", "Vinho salvo com sucesso!");
		return new ModelAndView("redirect:/vinhos/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(VinhoFilter vinhoFilter) {
		ModelAndView mv = new ModelAndView("vinho/pesquisa-vinhos");
		String nome = vinhoFilter.getNome();
		List<Vinho> resultado = StringUtils.hasText(nome)
				? vinhos.findByNomeContainingIgnoreCase(nome)
				: vinhos.findAll();
		mv.addObject("vinhos", resultado);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Vinho vinho	= vinhos.findById(codigo).orElseThrow();
		return novo(vinho);
	}

	@DeleteMapping("/{codigo}")
	public String deletar(@PathVariable Long codigo, RedirectAttributes attributes) {
		vinhos.deleteById(codigo);
		attributes.addFlashAttribute("mensagem", "Vinho removido com sucesso!");
		return "redirect:/vinhos";
	}
}
