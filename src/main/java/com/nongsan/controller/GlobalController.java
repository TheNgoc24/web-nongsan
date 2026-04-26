package com.nongsan.controller;

import com.nongsan.model.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("count")
    public int cartCount(HttpSession session) {

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if(cart == null){
            return 0;
        }

        int count = 0;

        for(CartItem item : cart){
            count += item.getQuantity();
        }

        return count;
    }
}