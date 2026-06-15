package com.nongsan.controller;

import com.nongsan.entity.Product;
import com.nongsan.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    public String sitemap(HttpServletRequest request) {

        String baseUrl = "https://web-nongsan-production.up.railway.app";

        List<Product> products = productRepository.findAll();

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        xml.append("<url>");
        xml.append("<loc>").append(baseUrl).append("/</loc>");
        xml.append("</url>");

        xml.append("<url>");
        xml.append("<loc>").append(baseUrl).append("/shop</loc>");
        xml.append("</url>");

        for (Product p : products) {
            xml.append("<url>");
            xml.append("<loc>")
                    .append(baseUrl)
                    .append("/product/")
                    .append(p.getSlug() != null ? p.getSlug() : p.getId())
                    .append("</loc>");
            xml.append("</url>");
        }

        xml.append("</urlset>");

        return xml.toString();
    }
}