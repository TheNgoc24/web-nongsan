package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.repository.PostRepository;
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
    private final PostRepository postRepository;

    public HomeController(ProductRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @GetMapping("/")
    public String newsHome(Model model) {

        model.addAttribute("posts", postRepository.findAllByOrderByCreatedAtDesc());

        return "posts";
    }

    @GetMapping("/shop")
    public String shop(@RequestParam(defaultValue = "0") int page, Model model) {

        Page<Product> productPage = repository.findAll(PageRequest.of(page, 8));

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "index";
    }

    @GetMapping("/product/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {

        Product product = repository.findBySlug(slug);

        if (product == null) {
            return "redirect:/";
        }

        model.addAttribute("product", product);

        return "product";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         Model model) {

        List<Product> products;

        if (keyword == null || keyword.trim().isEmpty()) {
            products = repository.findAll();
        } else {
            products = repository.findByNameContainingIgnoreCase(keyword);
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);

        return "index";
    }

    @GetMapping("/posts")
    public String postsRedirect() {
        return "redirect:/";
    }

    public String toSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

}



