package com.nongsan.controller;

import com.nongsan.entity.User;
import com.nongsan.model.CartItem;
import com.nongsan.model.Order;
import com.nongsan.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CheckoutController {

    private final OrderRepository orderRepository;

    public CheckoutController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model){

        User user = (User) session.getAttribute("user");

        if(user == null){
            return "redirect:/login";
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart == null || cart.isEmpty()){
            return "redirect:/cart";
        }

        double total = 0;

        for(CartItem item : cart){
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);

        return "checkout";
    }
    @PostMapping("/checkout")
    public String processCheckout(HttpSession session,
                                  @RequestParam String name,
                                  @RequestParam String phone,
                                  @RequestParam String address){

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart == null || cart.isEmpty()){
            return "redirect:/cart";
        }

        double total = 0;

        for(CartItem item : cart){
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        Order order = new Order();
        order.setTotal(total);
        order.setName(name);
        order.setPhone(phone);
        order.setAddress(address);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        session.removeAttribute("cart");

        return "redirect:/success";
    }
    @GetMapping("/success")
    public String success(){
        return "success";
    }

}