package edu.sru.cpsc.webshopping.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.sru.cpsc.webshopping.domain.products.Product;
import edu.sru.cpsc.webshopping.repository.products.ProductsRepository;

@Controller
@RequestMapping("/products")
public class ProductsController {
	@Autowired
	private ProductsRepository repo;

	@GetMapping({"","/"})
	public String showProducts(Model model) {
		List<Product> products = repo.findAll();
		model.addAttribute("products", products);
		return "products/recommendedItems";
	}
}
