package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.entity.User;
import com.nongsan.repository.OrderRepository;
import com.nongsan.repository.ProductRepository;
import com.nongsan.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository repository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public AdminController(ProductRepository repository, OrderRepository orderRepository, UserRepository userRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String adminPage(Model model, HttpSession session){

        model.addAttribute("products", repository.findAll());

        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/login";
        }

        if(!"ADMIN".equals(user.getRole())){
            return "redirect:/";
        }

        model.addAttribute("products", repository.findAll());
        model.addAttribute("totalProducts", repository.count());
        model.addAttribute("totalUsers", userRepository.count());

        return "admin";
    }

    @PostMapping("/save-product")
    public String saveProduct(
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam String description,
            @RequestParam String image
    ){

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);

        product.setImage(image);

        repository.save(product);

        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        repository.deleteById(id);

        return "redirect:/admin";
    }

    @GetMapping("/orders")
    public String orders(Model model){

        model.addAttribute("orders", orderRepository.findAll());

        return "orders";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        model.addAttribute("product", repository.findById(id).get());
        return "edit-product";
    }

    @PostMapping("/update-product")
    public String update(Product product){

        repository.save(product);

        return "redirect:/admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model){

        long totalProducts = repository.count();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();

        double totalRevenue = orderRepository.findAll()
                .stream()
                .mapToDouble(o -> o.getTotal())
                .sum();

        model.addAttribute("products", totalProducts);
        model.addAttribute("orders", totalOrders);
        model.addAttribute("users", totalUsers);
        model.addAttribute("revenue", totalRevenue);

        // fake chart (vì chưa có OrderItem)
        model.addAttribute("labels", List.of("Sản phẩm", "Đơn hàng", "User"));
        model.addAttribute("data", List.of(totalProducts, totalOrders, totalUsers));

        return "dashboard";
    }

}