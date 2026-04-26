package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.model.CartItem;
import com.nongsan.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository repository;

    public CartController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart == null){
            cart = new ArrayList<>();
        }

        double total = 0;
        int count = 0;

        for(CartItem item : cart){
            total += item.getProduct().getPrice() * item.getQuantity();
            count += item.getQuantity();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("count", count);

        return "cart";
    }

    @ResponseBody
    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        boolean found = false;

        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(id)) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(new CartItem(product, 1));
        }

        session.setAttribute("cart", cart);

        return "ok";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart != null){
            cart.removeIf(item -> item.getProduct().getId().equals(id));
        }

        session.setAttribute("cart", cart);

        return "redirect:/cart";
    }

    @GetMapping("/increase/{id}")
    public String increase(@PathVariable Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart != null){
            for(CartItem item : cart){
                if(item.getProduct().getId().equals(id)){
                    item.setQuantity(item.getQuantity() + 1);
                }
            }
        }

        return "redirect:/cart";
    }

    @GetMapping("/decrease/{id}")
    public String decrease(@PathVariable Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart != null){
            for(CartItem item : cart){
                if(item.getProduct().getId().equals(id)){
                    if(item.getQuantity() > 1){
                        item.setQuantity(item.getQuantity() - 1);
                    }
                }
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/increase")
    @ResponseBody
    public double increaseAjax(@RequestParam Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        double total = 0;

        if(cart != null){
            for(CartItem item : cart){
                if(item.getProduct().getId().equals(id)){
                    item.setQuantity(item.getQuantity() + 1);
                }
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        return total;
    }
    @PostMapping("/decrease")
    @ResponseBody
    public double decreaseAjax(@RequestParam Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        double total = 0;

        if(cart != null){
            for(CartItem item : cart){
                if(item.getProduct().getId().equals(id)){
                    if(item.getQuantity() > 1)
                        item.setQuantity(item.getQuantity() - 1);
                }
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        return total;
    }
    @PostMapping("/remove")
    @ResponseBody
    public double removeAjax(@RequestParam Long id, HttpSession session){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart != null){
            cart.removeIf(item -> item.getProduct().getId().equals(id));
        }

        double total = 0;
        if(cart != null){
            for(CartItem item : cart){
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        return total;
    }
}
