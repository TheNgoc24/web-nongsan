package com.nongsan.controller;

import com.nongsan.entity.Post;
import com.nongsan.entity.Product;
import com.nongsan.entity.User;
import com.nongsan.model.Order;
import com.nongsan.repository.OrderRepository;
import com.nongsan.repository.PostRepository;
import com.nongsan.repository.ProductRepository;
import com.nongsan.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository repository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public AdminController(ProductRepository repository,
                           OrderRepository orderRepository,
                           UserRepository userRepository,
                           PostRepository postRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    private String checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(user.getRole())) {
            return "redirect:/";
        }

        return null;
    }

    @GetMapping
    public String adminPage(Model model,
                            HttpSession session,
                            @RequestParam(required = false) String orderName,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        List<User> users = userRepository.findAll()
                .stream()
                .filter(u -> !"ADMIN".equals(u.getRole()))
                .toList();

        List<Order> allOrders = orderRepository.findAll();

        List<Order> filteredOrders = allOrders.stream()
                .filter(o -> orderName == null
                        || orderName.trim().isEmpty()
                        || (o.getName() != null
                        && o.getName().toLowerCase().contains(orderName.toLowerCase())))
                .filter(o -> orderDate == null
                        || (o.getCreatedAt() != null
                        && o.getCreatedAt().toLocalDate().equals(orderDate)))
                .toList();

        double totalRevenue = allOrders.stream()
                .mapToDouble(Order::getTotal)
                .sum();

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        allOrders.stream()
                .filter(o -> o.getCreatedAt() != null)
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .forEach(o -> {
                    String date = o.getCreatedAt().toLocalDate().format(formatter);
                    revenueMap.put(date, revenueMap.getOrDefault(date, 0.0) + o.getTotal());
                });

        model.addAttribute("products", repository.findAll());
        model.addAttribute("users", users);
        model.addAttribute("posts", postRepository.findAllByOrderByCreatedAtDesc());

        model.addAttribute("totalProducts", repository.count());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalPosts", postRepository.count());
        model.addAttribute("totalRevenue", totalRevenue);

        model.addAttribute("latestOrders", filteredOrders);

        model.addAttribute("orderName", orderName);
        model.addAttribute("orderDate", orderDate);

        model.addAttribute("revenueLabels", new ArrayList<>(revenueMap.keySet()));
        model.addAttribute("revenueData", new ArrayList<>(revenueMap.values()));

        return "admin";
    }

    @PostMapping("/save-product")
    public String saveProduct(@RequestParam String name,
                              @RequestParam double price,
                              @RequestParam String description,
                              @RequestParam String image,
                              HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setImage(image);
        product.setSlug(toSlug(name));

        repository.save(product);

        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        repository.deleteById(id);

        return "redirect:/admin";
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        model.addAttribute("orders", orderRepository.findAll());

        return "orders";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Product product = repository.findById(id).orElse(null);

        if (product == null) {
            return "redirect:/admin";
        }

        model.addAttribute("product", product);

        return "edit-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam double price,
                                @RequestParam String description,
                                @RequestParam String image,
                                HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Product product = repository.findById(id).orElse(null);

        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setImage(image);
            product.setSlug(toSlug(name));

            repository.save(product);
        }

        return "redirect:/admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        long totalProducts = repository.count();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();

        double totalRevenue = orderRepository.findAll()
                .stream()
                .mapToDouble(Order::getTotal)
                .sum();

        model.addAttribute("products", totalProducts);
        model.addAttribute("orders", totalOrders);
        model.addAttribute("users", totalUsers);
        model.addAttribute("revenue", totalRevenue);

        model.addAttribute("labels", List.of("Sản phẩm", "Đơn hàng", "User"));
        model.addAttribute("data", List.of(totalProducts, totalOrders, totalUsers));

        return "dashboard";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        User userDelete = userRepository.findById(id).orElse(null);

        if (userDelete != null && !"ADMIN".equals(userDelete.getRole())) {
            userRepository.deleteById(id);
        }

        return "redirect:/admin";
    }

    @PostMapping("/save-post")
    public String savePost(@RequestParam String title,
                           @RequestParam String image,
                           @RequestParam String category,
                           @RequestParam String content,
                           HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Post post = new Post();

        post.setTitle(title);
        post.setImage(image);
        post.setCategory(category);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        return "redirect:/admin";
    }

    @GetMapping("/edit-post/{id}")
    public String editPost(@PathVariable Long id, Model model, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            return "redirect:/admin";
        }

        model.addAttribute("post", post);

        return "edit-post";
    }

    @PostMapping("/update-post")
    public String updatePost(@RequestParam Long id,
                             @RequestParam String title,
                             @RequestParam String image,
                             @RequestParam String category,
                             @RequestParam String content,
                             HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        Post post = postRepository.findById(id).orElse(null);

        if (post != null) {
            post.setTitle(title);
            post.setImage(image);
            post.setCategory(category);
            post.setContent(content);

            postRepository.save(post);
        }

        return "redirect:/admin";
    }

    @GetMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id, HttpSession session) {

        String check = checkAdmin(session);
        if (check != null) {
            return check;
        }

        postRepository.deleteById(id);

        return "redirect:/admin";
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