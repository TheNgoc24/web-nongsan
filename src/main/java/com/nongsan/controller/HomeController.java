package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ProductRepository repository;

    public HomeController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page, Model model){

        Page<Product> productPage = repository.findAll(PageRequest.of(page, 8));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "index";
    }

    @GetMapping("/product/{slug}-{id}")
    public String productDetail(@PathVariable String slug,
                                @PathVariable Long id,
                                Model model) {

        Product product = repository.findById(id).orElse(null);

        model.addAttribute("product", product);

        return "product";
    }
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         Model model){

        List<Product> products;

        if(keyword == null || keyword.trim().isEmpty()){
            products = repository.findAll();
        }else{
            products = repository.findByNameContainingIgnoreCase(keyword);
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);

        return "index";
    }
    @GetMapping("/category")
    public String category(){
        return "category";
    }

    @GetMapping("/posts")
    public String posts(){
        return "posts";
    }

}



