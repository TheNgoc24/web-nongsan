package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SitemapController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {

        List<Product> products = productRepository.findAll();

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        // Trang chính
        xml.append("<url>");
        xml.append("<loc>http://localhost:8080/</loc>");
        xml.append("</url>");

        // Category
        xml.append("<url>");
        xml.append("<loc>http://localhost:8080/category</loc>");
        xml.append("</url>");

        // Product dynamic
        for (Product p : products) {
            xml.append("<url>");
            xml.append("<loc>http://localhost:8080/product/" + p.getId() + "</loc>");
            xml.append("</url>");
        }

        xml.append("</urlset>");

        return xml.toString();
    }
}